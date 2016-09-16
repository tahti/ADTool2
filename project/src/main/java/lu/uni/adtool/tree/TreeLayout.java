/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>) Date:
 * 10/12/2015 Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty
 * of Science, Technology and Communication FSTC All rights reserved. Licensed
 * under GNU Affero General Public License 3.0; This program is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.tree;

import lu.uni.adtool.adtree.ADTreeNode;
import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Parametrized;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.custom.AdtCustomDomain;
import lu.uni.adtool.domains.custom.SandCustomDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

public class TreeLayout implements MultipleCDockableLayout {
  public static int SAND_ID = 1;
  public static int ADT_ID  = 2;

  public TreeLayout(int treeId) {
    this.treeId = treeId;
    this.treeRoot = null;
    this.domains = new HashMap<Integer, ValuationDomain>();
    this.switchRole = false;
  }

  public TreeLayout(int id, Node treeRoot) {
    this.treeId = id;
    this.treeRoot = treeRoot;
    this.domains = new HashMap<Integer, ValuationDomain>();
    this.switchRole = false;
  }

  public boolean isSand() {
    if (this.treeRoot instanceof SandNode) {
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    // TreeLayout other = (TreeLayout) obj;
    // return equals( background, other.background ) &&
    // equals( fileName, other.fileName ) &&
    // equals( fileContent, other.fileContent );
    return false;// TODO finish
  }

  public void readStream(DataInputStream in) throws IOException {
    int type = in.readInt();
    this.treeId = in.readInt();
    if (type == SAND_ID) {
      this.treeRoot = SandNode.readStream(in);
    } else {
      this.treeRoot = ADTNode.readStream(in);
      int sR = in.readInt();
      if (sR == 0) {
        this.switchRole = false;
      } else {
        this.switchRole = true;
      }
    }
  }

  public void writeStream(DataOutputStream out) throws IOException {
    if (this.treeRoot instanceof SandNode) {
      out.writeInt(SAND_ID);
      out.writeInt(this.treeId);
      ((SandNode) this.treeRoot).writeStream(out);
    } else {
      out.writeInt(ADT_ID);
      out.writeInt(this.treeId);
      ((ADTNode) this.treeRoot).writeStream(out);
      if (this.switchRole) {
        out.writeInt(1);
      } else {
        out.writeInt(0);
      }
    }
  }

  public boolean getSwitchRole() {
    return switchRole;
  }

  public void toggleRole() {
    if (!isSand()) {
      this.switchRole = !this.switchRole;
    }
  }

  public void readXML(XElement element) {
    this.treeRoot = null;
    XElement e = element.getElement("id");
    if (e == null)
      return;
    this.treeId = e.getInt();
    e = element.getElement("type");
    if (e == null)
      return;
    int type = e.getInt();
    if (type == SAND_ID) {
      this.treeRoot = new SandNode();
      ((SandNode) this.treeRoot).fromXml(element.getElement("node"));
    } else {
      this.treeRoot = new ADTNode();
      XElement rootNode = element.getElement("node");
      try {
        ((ADTNode) this.treeRoot).fromXml(rootNode);
      } catch (IOException ex) {
        this.treeRoot = null;
        ex.printStackTrace();
      }
      try {
        this.switchRole = (rootNode.getString("switchRole").toLowerCase().equals("yes")
            || rootNode.getString("switchRole").toLowerCase().equals("true"));
      } catch (XException exception) {
        this.switchRole = false;
      }
    }
  }

  public void writeXML(XElement element) {
    element.addElement("id").setInt(this.treeId);
    if (this.treeRoot instanceof SandNode) {
      element.addElement("type").setInt(SAND_ID);
      element.addElement(((SandNode) this.treeRoot).toXml());
    } else {
      element.addElement("type").setInt(ADT_ID);
      XElement root = ((ADTNode) this.treeRoot).toXml();
      if (this.getSwitchRole()) {
        root.addString("switchRole", "yes");
      }
      element.addElement(root);
    }
  }

  public void importAdt(ADTreeNode root, Map<ADTreeNode, ArrayList<ADTreeNode>> childrenMap,
      Map<ADTreeNode, ADTreeNode> parents) {
    ADTNode current = new ADTNode(root);
    // if ( current.getRole() = ADTNode.Role.OPPONENT) {
    // this.switchRole = true;
    // }
    // else {
    // this.switchRole = false;
    // }
    ADTreeNode current2 = root;
    this.treeRoot = current;
    Deque<ADTNode> stack = new ArrayDeque<ADTNode>();
    stack.addFirst(current);
    Deque<ADTreeNode> stack2 = new ArrayDeque<ADTreeNode>();
    stack2.addFirst(current2);
    while (stack.peek() != null) {
      current = stack.removeLast();
      current2 = stack2.removeLast();
      ArrayList<ADTreeNode> children = childrenMap.get(current2);
      for (ADTreeNode n : children) {
        ADTNode node = new ADTNode(n);
        stack.addFirst(node);
        stack2.addFirst(n);
        current.addChild(node);
      }
    }
  }

  public void addAdtDomain(AdtDomain<Ring> domain, lu.uni.adtool.adtree.ValueAssignement<Ring> v1,
      lu.uni.adtool.adtree.ValueAssignement<Ring> v2, int treeId, int domainId) {
    ValuationDomain values = new ValuationDomain(treeId, domainId, domain);
    for (String s : v1.keySet()) {
      values.setValue(true, s, v1.get(s));
    }
    for (String s : v2.keySet()) {
      values.setValue(false, s, v2.get(s));
    }
    domains.put(new Integer(values.getDomainId()), values);
  }

  public void importXml(XElement e, int treeId) throws IllegalArgumentException, XException {
    XElement root;
    root = e.getElement("node");
    // map from domainid to domain
    HashMap<String, ValuationDomain> domainsHash = new HashMap<String, ValuationDomain>();
    int i = 0;
    if (e.getName().equals("sandtree")) {
      for (XElement domain : e.getElements("domain")) {
        ++i;
        String domainName = domain.getElement("class").getString();
        SandDomain<Ring> d = (SandDomain<Ring>) DomainFactory.createFromString(domainName);
        if (d == null) {
          throw new IllegalArgumentException(Options.getMsg("exception.nodomain") + domainName);
        }
        if (d instanceof SandCustomDomain) {
          ((SandCustomDomain)d).setName(domain.getElement("name").getString());
          ((SandCustomDomain)d).setDescription(domain.getElement("description").getString());
          ((SandCustomDomain)d).setOr(domain.getElement("or").getString());
          ((SandCustomDomain)d).setAnd(domain.getElement("and").getString());
          ((SandCustomDomain)d).setSand(domain.getElement("sand").getString());
          ((SandCustomDomain)d).setDefault(domain.getElement("defaultValue").getString());
        }
        String domainId = domain.getString("id");
        if (domainId == null || domainsHash.containsKey(domainId)) {
          throw new IllegalArgumentException(Options.getMsg("exception.wrongxml"));
        }
        domainsHash.put(domain.getString("id"), new ValuationDomain(treeId, i, d));
      }
      this.treeRoot = new SandNode();
      ((SandNode) this.treeRoot).importXml(root, domainsHash);
      this.switchRole = false;
    }
    else if (e.getName().equals("adtree")) {
      for (XElement domain : e.getElements("domain")) {
        ++i;
        String domainName = domain.getElement("class").getString();
        AdtDomain<Ring> d = (AdtDomain<Ring>) DomainFactory.createFromString(domainName);
        if (d == null) {
          throw new IllegalArgumentException(Options.getMsg("exception.nodomain") + domainName);
        }
        if (d instanceof AdtCustomDomain) {
          ((AdtCustomDomain)d).setName(domain.getElement("name").getString());
          ((AdtCustomDomain)d).setDescription(domain.getElement("description").getString());
          ((AdtCustomDomain)d).setOp(domain.getElement("op").getString());
          ((AdtCustomDomain)d).setOo(domain.getElement("oo").getString());
          ((AdtCustomDomain)d).setAp(domain.getElement("ap").getString());
          ((AdtCustomDomain)d).setAo(domain.getElement("ao").getString());
          ((AdtCustomDomain)d).setCp(domain.getElement("cp").getString());
          ((AdtCustomDomain)d).setCo(domain.getElement("co").getString());
          ((AdtCustomDomain)d).setOppDefault(domain.getElement("opponentDefault").getString());
          ((AdtCustomDomain)d).setProDefault(domain.getElement("proponentDefault").getString());
          ((AdtCustomDomain)d).setProModifiable(!domain.getElement("proModifiable").getString().toUpperCase().equals("FALSE"));
          ((AdtCustomDomain)d).setOppModifiable(!domain.getElement("oppModifiable").getString().toUpperCase().equals("FALSE"));
        } else if (d instanceof Parametrized) {
          String range = domain.getElement("range").getString();
          if (((Parametrized) d).getParameter() instanceof Integer) {
            if (range.equals(Options.getMsg("inputdialog.infinity"))) {
              ((Parametrized) d).setParameter(Integer.MAX_VALUE);
            } else {
              ((Parametrized) d).setParameter(Integer.parseInt(range));
            }
          }
        }
        String domainId = domain.getString("id");
        if (domainId == null || domainsHash.containsKey(domainId)) {
          throw new IllegalArgumentException(Options.getMsg("exception.wrongxml"));
        }
        Debug.log("adding domain with id:"+ domain.getString("id"));
        domainsHash.put(domain.getString("id"), new ValuationDomain(treeId, i, d));
      }
      this.treeRoot = new ADTNode();
      this.switchRole = ((ADTNode) this.treeRoot).importXml(root, domainsHash, treeId);
    }

    this.domains = new HashMap<Integer, ValuationDomain>();
    for (ValuationDomain v: domainsHash.values()) {
      this.domains.put(new Integer(v.getDomainId()), v);
    }
  }

  public int getLabelCounter(String prefix) {
    return getLabelCounter(treeRoot, prefix);
  }

  public Node getRoot() {
    return treeRoot;
  }

  public void setRoot(Node root) {
    this.treeRoot = root;
  }

  public void addDomain(ValuationDomain values) {
    this.domains.put(new Integer(values.getDomainId()), values);
  }

  public void setDefaultValuation(SandNode node) {
    for (ValuationDomain values : domains.values()) {
      values.setDefaultValue(node);
    }
  }

  public void setDefaultValuation(ADTNode node) {
    for (ValuationDomain values : domains.values()) {
      values.setDefaultValue(node);
    }
  }

  /**
   * Recalculates values - does not remove old uneccesary values
   */
  public void recalculateValues() {
    for (ValuationDomain values : domains.values()) {
      if (treeRoot instanceof SandNode) {
        values.valuesUpdated((SandNode) treeRoot);
      } else {
        values.valuesUpdated((ADTNode) treeRoot);
      }
    }
  }

  /**
   * Recalculates values and removes unnecesary values
   */
  public void refreshValues() {
    for (ValuationDomain values : domains.values()) {
      if (treeRoot instanceof SandNode) {
        values.treeChanged((SandNode) treeRoot);
      } else {
        values.treeChanged((ADTNode) treeRoot);
      }
    }
  }

  public void rename(Node node, String newName) {
    for (ValuationDomain values : domains.values()) {
      values.rename(node, newName);
    }
  }

  public boolean removeValuation(ValuationDomain values) {
    return domains.remove(new Integer(values.getDomainId())) != null;
  }

  public int getTreeId() {
    return this.treeId;
  }

  public ValuationDomain getDomain(int domainId) {
    return this.domains.get(new Integer(domainId));
  }
  public Collection<ValuationDomain> getDomains() {
    return this.domains.values();
  }
//   public void setDomains(ArrayList<ValuationDomain> domains) {
//     this.domains = domains;
//   }
  public boolean hasDomain() {
    return this.domains.size() > 0;
  }

  public int getNewDomainId() {
    int size = domains.values().size() + 1;
    for (int i = 1; i < size; i++) {
      if (this.domains.get(new Integer(i)) == null) {
        return i;
      }
    }
    return size;
  }

  protected int getLabelCounter(Node node, String prefix) {
    int count = 0;
    if (node.getName().startsWith(prefix)) {
      try {
        count = Integer.parseInt(node.getName().substring(prefix.length()));
      } catch (NumberFormatException e) {
        // do nothing
      }
    }
    if (node.getChildren() == null) {
      return count;
    }
    for (Node child : node.getChildren()) {
      count = Math.max(count, getLabelCounter(child, prefix));
    }
    return count;
  }

  private Node                              treeRoot;
  private int                               treeId;
  private HashMap<Integer, ValuationDomain> domains;
  private boolean                           switchRole;
}
