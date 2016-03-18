package lu.uni.adtool.tree;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Parametrized;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

public class TreeLayout implements MultipleCDockableLayout {
  public static int SAND_ID = 1;
  public static int ADT_ID  = 2;

  public TreeLayout(int id) {
    this.id = id;
    this.treeRoot = null;
    this.domains = new ArrayList<ValuationDomain>();
    this.switchRole = false;
  }

  public TreeLayout(int id, Node treeRoot) {
    this.id = id;
    this.treeRoot = treeRoot;
    this.domains = new ArrayList<ValuationDomain>();
    this.switchRole = false;
  }

  // public SandTreeLayout() {
  // this.id = -1;
  // this.treeRoot = new SandNode(SandNode.Type.AND);
  // this.treeRoot.setName("Root");
  // this.domains = new ArrayList<ValuationDomain>();
  // }
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
    this.id = in.readInt();
    if (type == SAND_ID) {
      this.treeRoot = SandNode.readStream(in);
    }
    else {
      this.treeRoot = ADTNode.readStream(in);
      int sR = in.readInt();
      if (sR == 0) {
        this.switchRole = false;
      }
      else {
        this.switchRole = true;
      }
    }
  }

  public void writeStream(DataOutputStream out) throws IOException {
    if (this.treeRoot instanceof SandNode) {
      out.writeInt(SAND_ID);
      out.writeInt(this.id);
      ((SandNode) this.treeRoot).writeStream(out);
    }
    else {
      out.writeInt(ADT_ID);
      out.writeInt(this.id);
      ((ADTNode) this.treeRoot).writeStream(out);
      if (this.switchRole) {
        out.writeInt(1);
      }
      else {
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
    if (e == null) return;
    this.id = e.getInt();
    e = element.getElement("type");
    if (e == null) return;
    int type = e.getInt();
    if (type == SAND_ID) {
      this.treeRoot = new SandNode();
      ((SandNode) this.treeRoot).fromXml(element.getElement("node"));
    }
    else {
      this.treeRoot = new ADTNode();
      XElement rootNode = element.getElement("node");
      try {
        ((ADTNode) this.treeRoot).fromXml(rootNode);
      }
      catch (IOException ex) {
        this.treeRoot = null;
        ex.printStackTrace();
      }
      try {
        this.switchRole = (rootNode.getString("switchRole").toLowerCase().equals("yes")
            || rootNode.getString("switchRole").toLowerCase().equals("true"));
      }
      catch (XException exception) {
        this.switchRole = false;
      }
    }
  }

  public void writeXML(XElement element) {
    element.addElement("id").setInt(this.id);
    if (this.treeRoot instanceof SandNode) {
      element.addElement("type").setInt(SAND_ID);
      element.addElement(((SandNode) this.treeRoot).toXml());
    }
    else {
      element.addElement("type").setInt(ADT_ID);
      XElement root = ((ADTNode) this.treeRoot).toXml();
      if (this.getSwitchRole()) {
        root.addString("switchRole", "yes");
      }
      element.addElement(root);
    }
  }

  public void importXml(XElement e, int treeId) throws IllegalArgumentException {
    XElement root;
    root = e.getElement("node");
    HashMap<String, ValuationDomain> domainsHash = new HashMap<String, ValuationDomain>();
    int i = 0;
    if (e.getName().equals("sandtree")) {
      for (XElement domain : e.getElements("domain")) {
        i++;
        String domainName = domain.getElement("class").getString();
        SandDomain<Ring> d = (SandDomain<Ring>) DomainFactory.createFromString(domainName);
        if (d == null) {
          throw new IllegalArgumentException(Options.getMsg("exception.nodomain") + domainName);
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
      this.domains = new ArrayList<ValuationDomain>(domainsHash.values());
    }
    else if (e.getName().equals("adtree")) {
      for (XElement domain : e.getElements("domain")) {
        i++;
        String domainName = domain.getElement("class").getString();
        AdtDomain<Ring> d = (AdtDomain<Ring>) DomainFactory.createFromString(domainName);
        if (d == null) {
          throw new IllegalArgumentException(Options.getMsg("exception.nodomain") + domainName);
        }
        if (d instanceof Parametrized) {
          String range = domain.getElement("range").getString();
          if (((Parametrized) d).getParameter() instanceof Integer) {
            if (range.equals(Options.getMsg("inputdialog.infinity"))) {
              ((Parametrized) d).setParameter(Integer.MAX_VALUE);
            }
            else {
              ((Parametrized) d).setParameter(Integer.parseInt(range));
            }
          }
        }
        String domainId = domain.getString("id");
        if (domainId == null || domainsHash.containsKey(domainId)) {
          throw new IllegalArgumentException(Options.getMsg("exception.wrongxml"));
        }
        domainsHash.put(domain.getString("id"), new ValuationDomain(treeId, i, d));
      }
      this.treeRoot = new ADTNode();
      this.treeRoot.setName(root.getElement("label").getString());
      try {
        this.switchRole = (root.getString("switchRole").toLowerCase().equals("yes")
            || root.getString("switchRole").toLowerCase().equals("true"));
      }
      catch (XException exception) {
        this.switchRole = false;
      }
      for (XElement parameter : root.getElements("parameter")) {
        String category = parameter.getString("category");
        if (category == null) {
          throw new IllegalArgumentException(Options.getMsg("exception.wrongxml"));
        }
        if (category.equals("basic")) {
          ValuationDomain d = domainsHash.get(parameter.getString("domainId"));
          if (d != null) {
            Ring r = d.getDomain().getDefaultValue(this.treeRoot);
            r.updateFromString(parameter.getString());
            d.setValue(true, this.treeRoot.getName(), r);
          }
        }
      }
      for (XElement child : root.getElements("node")) {
        ADTNode ch = new ADTNode();
        this.treeRoot.addChild(ch);
        ch.importXml(child, domainsHash);
      }
      this.domains = new ArrayList<ValuationDomain>(domainsHash.values());
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
    this.domains.add(values);
  }

  public void setDefaultValuation(SandNode node) {
    for (ValuationDomain values : domains) {
      values.setDefaultValue(node);
    }
  }

  public void setDefaultValuation(ADTNode node) {
    for (ValuationDomain values : domains) {
      values.setDefaultValue(node);
    }
  }

  public void recalculateValues() {
    for (ValuationDomain values : domains) {
      if (treeRoot instanceof SandNode) {
        values.valuesUpdated((SandNode) treeRoot);
      }
      else {
        values.valuesUpdated((ADTNode) treeRoot);
      }
    }
  }

  public void refreshValues() {
    for (ValuationDomain values : domains) {
      if (treeRoot instanceof SandNode) {
        values.treeChanged((SandNode) treeRoot);
      }
      else {
        values.treeChanged((ADTNode) treeRoot);
      }
    }
  }

  public void rename(Node node, String newName) {
    for (ValuationDomain values : domains) {
      values.rename(node, newName);
    }
  }

  public void removeValuation(ValuationDomain values) {
    domains.remove(values);
  }

  public int getId() {
    return this.id;
  }

  public ArrayList<ValuationDomain> getDomains() {
    return domains;
  }

  public void setDomains(ArrayList<ValuationDomain> domains) {
    this.domains = domains;
  }

  protected int getLabelCounter(Node node, String prefix) {
    int count = 0;
    if (node.getName().startsWith(prefix)) {
      try {
        count = Integer.parseInt(node.getName().substring(prefix.length()));
      }
      catch (NumberFormatException e) {
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

  private Node                       treeRoot;
  private int                        id;
  private ArrayList<ValuationDomain> domains;
  private boolean                    switchRole;
}
