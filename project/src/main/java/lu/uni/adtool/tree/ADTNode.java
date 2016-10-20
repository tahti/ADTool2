/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>)
 * Date:   10/12/2015
 * Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty of Science,
 *     Technology and Communication FSTC
 * All rights reserved.
 * Licensed under GNU Affero General Public License 3.0;
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.tree;

import lu.uni.adtool.adtree.ADTreeNode;
import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.RankExporter;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import bibliothek.util.xml.XAttribute;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;

public class ADTNode extends GuiNode {

  public enum Type {
    AND_OPP, OR_OPP, AND_PRO, OR_PRO
  }

  public enum Role {
    PROPONENT, OPPONENT
  }

  public ADTNode() {
    super();
    type = Type.AND_PRO;
  }

  public ADTNode(ADTreeNode node) {
    super(node.getLabel());
    if (node.getType() == ADTreeNode.Type.OPPONENT) {
      if (node.getRefinmentType() == ADTreeNode.RefinementType.DISJUNCTIVE) {
        type = Type.OR_OPP;
      }
      else {
        type = Type.AND_OPP;
      }
    }
    else {
      if (node.getRefinmentType() == ADTreeNode.RefinementType.DISJUNCTIVE) {
        type = Type.OR_PRO;
      }
      else {
        type = Type.AND_PRO;
      }
    }
  }

  public ADTNode(Type type) {
    super();
    this.type = type;
  }

  public void addCounter(ADTNode counter) {
    getNotNullChildren().add(counter);
    counter.setParent(this);
  }

  @Override
  public void addChild(Node child) {
    if (!this.isCountered()) {
      getNotNullChildren().add(child);
    }
    else {
      getNotNullChildren().add(getNotNullChildren().size() - 1, child);
    }
    child.setParent(this);
  }

  public static ADTNode readStream(DataInputStream in) throws IOException {
    String name = in.readUTF();
    Type type = Type.values()[in.readInt()];
    ADTNode result = new ADTNode(type);
    result.setParent(null);
    result.setName(name);
    int noChildren = in.readInt();
    for (int i = 0; i < noChildren; i++) {
      ADTNode child = ADTNode.readStream(in);
      if (i < (noChildren - 1) && child.getRole() != result.getRole()) {
        throw new IOException(Options.getMsg("exception.counter"));
      }
      result.addChild(child);
    }
    return result;
  }

  public void writeStream(DataOutputStream out) throws IOException {
    out.writeUTF(this.getName());
    out.writeInt(this.type.ordinal());
    if (getChildren() == null || getChildren().size() == 0) {
      out.writeInt(0);
    }
    else {
      out.writeInt(getChildren().size());
    }
    for (Node child : getChildren()) {
      ((ADTNode) child).writeStream(out);
    }
  }

  /**
   * Import from XML using format that stores every tree together with layout
   * used by Docking Frames
   *
   */
  public void fromXml(XElement e) throws IOException {
    setName(e.getElement("label").getString());
    XElement commentXml = e.getElement("comment");
    if (commentXml != null) {
      setComment(commentXml.getString());
    }
    this.type = stringToType(e.getString("refinement"));
    int i = 0;
    XElement elements[] = e.getElements("node");
    for (XElement child : elements) {
      ADTNode ch = new ADTNode();
      ch.fromXml(child);
      if (i < (elements.length - 1) && ch.getRole() != getRole()) {
        throw new IOException(Options.getMsg("exception.counter"));
      }
      this.addChild(ch);
      i++;
    }
  }

  public String toLatex(int depth) {
    StringBuilder texStr = new StringBuilder();
    StringBuilder indent = new StringBuilder();
    for (int i = 0; i<depth; i++) {indent.append("  ");}
    texStr.append(indent.toString()+"[");
    texStr.append(XmlConverter.escape2Latex(this.getName()));
    if (this.getParent()== null) { //root
      texStr.append(", proTree");
    }
    else {
      if (((ADTNode)this.getParent()).isCountered()){
        if (getRole() == ADTNode.Role.PROPONENT) {
          texStr.append(", proTree");
        }
        else {
          texStr.append(", oppTree");
        }
      }
    }
    if (this.getChildren() != null && this.getChildren().size() > 0) {
      texStr.append(System.getProperty("line.separator"));
      for (Node node : this.getNotNullChildren()) {
        texStr.append(((ADTNode)node).toLatex(depth + 1));
      }
      texStr.append(indent.toString());
    }
    texStr.append("]");
    if (this.getChildren() != null && this.getChildren().size() > 1 &&
       (getType() == ADTNode.Type.AND_OPP ||getType() == ADTNode.Type.AND_PRO) ) {
      ADTNode lastChild = (ADTNode)this.getChildren().get((this.getChildren().size() -1));
      if (lastChild.getRole() == getRole()) {
        texStr.append("\\andN");
      }
      else if (this.getChildren().size()>2) {
        texStr.append("\\andC");
      }
    }
    texStr.append(System.getProperty("line.separator"));
    return texStr.toString();
  }

  /**
   * Export to XML using format used by the first version of ADTool.
   *
   * @param domains - all domains available
   * @param rankers - list of rankers
   * @param idToExport - set of domain id which we should export.
   * @return
   */
  public XElement exportXml(Collection<ValuationDomain> domains, ArrayList<RankExporter> rankers, Set<Integer> idToExport) {
    XElement result = new XElement("node");
    result.addString("refinement", typeToXml(type));
    if (this.getParent() != null && (((ADTNode) getParent()).getRole() != getRole())) {
      result.addString("switchRole", "yes");
    }
    result.addElement("label").setString(getName());
    if (getComment() != null && (!getComment().equals(""))) {
      result.addElement("comment").setString(getComment());
    }
    if (domains != null && idToExport != null) {
      int i = 0;
      for (ValuationDomain vd : domains) {
        if (idToExport.contains(vd.getDomainId())) {
          String domainId = vd.getExportXmlId();
          if (this.isEditable((AdtDomain<Ring>) vd.getDomain())) {
            if (vd.getValue(this) != null) {
              XElement param = result.addElement("parameter");
              param.addString("domainId", domainId);
              param.addString("category", "basic");
              param.setString(vd.getValue(this).toString());
            }
          }
          else {
            if (Options.main_saveDerivedValues) {
              if (hasDefault()) {
                if (vd.getValue(this) != null) {
                  XElement param = result.addElement("parameter");
                  param.addString("domainId", domainId);
                  param.addString("category", "default");
                  param.setString(vd.getValue(this).toString());
                }
              }
              XElement param = result.addElement("parameter");
              param.addString("domainId", domainId);
              param.addString("category", "derived");
              param.setString(vd.getTermValue(this).toString());
            }
          }
          if (rankers != null && rankers.size() > i) {
            for (int j = 0; j < Options.rank_noRanked; j++) {
              Ring value = rankers.get(i).getValue(this, j);
//               Debug.log("Exporting ranking value:"+value.toString());
              if (value != null) {
                XElement rank = result.addElement("ranking");
                rank.addInt("rank", j + 1);
                rank.setString(value.toString());
              }
            }
          }
          ++i;
        }
      }
    }
    if (this.getChildren() != null) {
      for (Node node : this.getNotNullChildren()) {
        result.addElement(((ADTNode) node).exportXml(domains, rankers, idToExport));
      }
    }
    return result;
  }

  public boolean isEditable(AdtDomain<Ring> domain) {
    return hasDefault() && domain.isValueModifiable(this.getRole()==ADTNode.Role.PROPONENT);
  }

  /**
   * Export to XML using format to store every tree together with layout used by
   * Docking Frames
   *
   */
  public XElement toXml() {
    XElement result = new XElement("node");
    XAttribute typeAttribute = new XAttribute("refinement");
    typeAttribute.setString(typeToString(type));
    result.addAttribute(typeAttribute);
    result.addElement("label").setString(getName());
    if (getComment() != null && (!getComment().equals(""))) {
      result.addElement("comment").setString(getComment());
    }
    if (this.getChildren() != null) {
      for (Node node : this.getNotNullChildren()) {
        result.addElement(((ADTNode) node).toXml());
      }
    }
    return result;
  }

  public void toggleOp() {
    switch (type) {
    case AND_OPP:
      setType(Type.OR_OPP);
      break;
    case AND_PRO:
      setType(Type.OR_PRO);
      break;
    case OR_OPP:
      setType(Type.AND_OPP);
      break;
    case OR_PRO:
      setType(Type.AND_PRO);
      break;
    }
  }

  public void toggleRole() {
    switch (type) {
    case AND_OPP:
      setType(Type.AND_PRO);
      break;
    case AND_PRO:
      setType(Type.AND_OPP);
      break;
    case OR_OPP:
      setType(Type.OR_PRO);
      break;
    case OR_PRO:
      setType(Type.OR_OPP);
      break;
    }
  }

  public void toggleRoleRecursive() {
    this.toggleRole();
    if (getChildren() != null) {
      for (Node node : this.getNotNullChildren()) {
        ((ADTNode) node).toggleRoleRecursive();
      }
    }
  }

  public Role getRole() {
    switch (type) {
    case AND_OPP:
    case OR_OPP:
      return Role.OPPONENT;
    default:
      return Role.PROPONENT;
    }
  }

  public SandNode sandCopy() {
    SandNode result = new SandNode();
    result.setName(getName());
    result.setComment(getComment());
    if (type == Type.OR_PRO || type == Type.OR_OPP) {
      result.setType(SandNode.Type.OR);
    }
    else {
      result.setType(SandNode.Type.AND);
    }
    result.setLeftSibling(null);
    result.setRightSibling(null);
    if (getChildren() != null) {
      for (Node child : getChildren()) {
        result.addChild(((ADTNode) child).sandCopy());
      }
    }
    return result;
  }

  public ADTNode deepCopy() {
    ADTNode result = new ADTNode();
    result.setName(getName());
    result.setComment(getComment());
    result.setType(getType());
    result.setLeftSibling(null);
    result.setRightSibling(null);
    if (getChildren() != null) {
      for (Node child : getChildren()) {
        result.addCounter(((ADTNode) child).deepCopy());
      }
    }
    return result;
  }

  public String toString() {
    return "not implemented";
  }

  public Type getType() {
    return this.type;
  }

  public void setType(Type newType) {
    this.type = newType;
  }

  /**
   * Returns true if node has default value even if it is not modifiable. This
   * is the case if it is leaf or it is node with one child that is countered.
   */
  public boolean hasDefault() {
    if (this.isLeaf()) {
      return true;
    }
    return (super.getNotNullChildren().size() == 1) && (isCountered());
  }

  public boolean isCountered() {
    if (super.getChildren() == null || super.getChildren().size() < 1) {
      return false;
    }
    else if (((ADTNode) super.getChildren().get(getChildren().size() - 1)).getRole() != getRole()) {
      return true;
    }
    return false;
  }

  public String toTerms() {
    return toTerms(0);
  }

  /**
   * Pretty print a string representation of a tree.
   *
   * @param level
   * @return
   */
  public String toTerms(final int level) {
    String result = "";
    String indent = "";
    String currIndent = "";
    String eol = "";
    for (int i = 0; i < Options.indentLevel; i++) {
      indent += " ";
    }
    for (int i = 0; i < level; i++) {
      currIndent += indent;
    }
    eol = System.getProperty("line.separator");
    if (children != null && children.size() > 0) {
      int c = 0;
      int nextLevel = level + 1;
      if (isCountered()) {
        c = 1;
        nextLevel++;
      }
      for (int i = 0; i < (children.size() - c); ++i) {
        final ADTNode n = (ADTNode) children.get(i);
        if (n != null) {
          result += n.toTerms(nextLevel);
          if ((i + 1) < (children.size() - c)) {
            result += ",";
          }
          result += eol;
        }
        else {
          System.err.println("Null child at index:" + i);
        }
      }
      if (isCountered() && children.size() > 1) {
        String counter = ((getRole() == Role.PROPONENT) ? C_PRO : C_OPP);
        return currIndent + counter + "(" + eol + currIndent + indent + typeToString(type) + "("
            + eol + result + currIndent + indent + ")," + eol
            + ((ADTNode) children.get(children.size() - 1)).toTerms(level + 1) + eol + currIndent
            + ")";
      }
      else if (isCountered()) {
        String counter = ((getRole() == Role.PROPONENT) ? C_PRO : C_OPP);
        return currIndent + counter + "(" + eol + currIndent + indent + getName() + "," + eol
            + ((ADTNode) children.get(children.size() - 1)).toTerms(level + 1) + eol + currIndent
            + ")";
      }
      else {
        return currIndent + typeToString(type) + "(" + eol + result + currIndent + ")";
      }
    }
    else {
      return currIndent + this.getName();
    }
  }

  /**
   *  Function imports the tree contained in XML element
   *
   * @param e - xml element from which we import
   * @param domains - initialized set of domains for which we set the valuations
   * @param treeId - tree id for which we import nodes
   * @return true if switchRole is set for a node (important for root node only)
   *
   * @throws IllegalArgumentException
   * @throws XException
   */
  public boolean importXml(XElement e, HashMap<String, ValuationDomain> domains, int treeId)
      throws IllegalArgumentException, XException {
    setName(e.getElement("label").getString());
    XElement commentXml = e.getElement("comment");
    if (commentXml != null) {
      setComment(commentXml.getString());
    }
    boolean switchRole;
    try {
      switchRole = (e.getString("switchRole").toLowerCase().equals("yes")
          || e.getString("switchRole").toLowerCase().equals("true"));
    }
    catch (XException exception) {
      switchRole = false;
    }
    if (getParent() == null) {
      this.type = xmlToType(e.getString("refinement"), Role.PROPONENT);
    }
    else {
      this.type = xmlToType(e.getString("refinement"), ((ADTNode) getParent()).getRole());
    }
    if (switchRole && getParent() != null) {
      this.toggleRole();
    }
    XElement[] parameters = e.getElements("parameter");
    boolean fromTreemaker = false;
    try {
      if (parameters.length > 0 && parameters[0].getString("class") != null) {
        fromTreemaker = true;
      }
    }
    catch (XException e1) {
    }
    if (fromTreemaker) {
      int i = 0;
      for (XElement parameter : parameters) {
        ++i;
        try {
          String name = null;
          name = parameter.getString("name");
          ValuationDomain vd = null;
          if (name.equals("cost")) {
            // check if we have domain
            name = "MinCost";
          }
          else if (name.equals("likelihood")) {
            name = "ProbSucc";
          }
          else if (name.equals("difficulty")) {
            name = "DiffLMHE";
          }
          else {// if (name.equals("time")) {
            name = null;
          }
          if (name != null ) {
            if (domains.size() >= i) {
              vd = domains.get(Integer.toString(i));
            }
            else {
              AdtDomain<Ring> domain = (AdtDomain<Ring>) DomainFactory.createFromString(name);
              if (domain == null) {
                throw new IllegalArgumentException(
                    Options.getMsg("exception.nodomain") + name);
              }
              vd = new ValuationDomain(treeId, i, domain);
              domains.put(Integer.toString(i), vd);
            }
            if (vd != null) {
              Ring r = vd.getDomain().getDefaultValue(this);
              r.updateFromString(parameter.getString());
              vd.setValue(this.getRole() == ADTNode.Role.PROPONENT, getName(), r);
            }
          }
        }
        catch (XException e1) {
          throw new IllegalArgumentException(Options.getMsg("exception.wrongxml"));
        }
      }
    }
    else {
      for (XElement parameter : parameters) {
        String category = null;
        try {
          category = parameter.getString("category");
        }
        catch (XException e1) {
          // "basic" was default category in older versions of ADTool
          category = "basic";
        }
        Debug.log("adding parameter in category:"+ category);
        if (category.equals("basic")) {
          ValuationDomain d = domains.get(parameter.getString("domainId"));
          if (d != null) {
            Ring r = d.getDomain().getDefaultValue(this);
            r.updateFromString(parameter.getString());
            d.setValue(this.getRole() == ADTNode.Role.PROPONENT, this.getName(), r);
            Debug.log("set value "+ this.getName() + " to "+  r.toUnicode());
          }
          else {
            Debug.log("No domain with id " + parameter.getString("domainId"));
          }
        }
      }
    }
    for (XElement child : e.getElements("node")) {
      ADTNode ch = new ADTNode();
      this.addChild(ch);
      ch.importXml(child, domains, treeId);
    }
    return switchRole;
  }

  private String typeToString(Type type) {
    switch (this.type) {
    case AND_OPP:
      return "ao";
    case OR_OPP:
      return "oo";
    case AND_PRO:
      return "ap";
    case OR_PRO:
      return "op";
    }
    return "";
  }

  private String typeToXml(Type type) {
    switch (this.type) {
    case AND_OPP:
    case AND_PRO:
      return "conjunctive";
    case OR_OPP:
    case OR_PRO:
      return "disjunctive";
    }
    return "";
  }

  private Type xmlToType(String typeStr, Role role) {
    typeStr = typeStr.toLowerCase();
    if (role == Role.PROPONENT) {
      if (typeStr.equals("disjunctive") || typeStr.equals("or")) {
        return Type.OR_PRO;
      }
      else {
        return Type.AND_PRO;
      }
    }
    else {
      if (typeStr.equals("disjunctive") || typeStr.equals("or")) {
        return Type.OR_OPP;
      }
      else {
        return Type.AND_OPP;
      }
    }
  }

  private Type stringToType(String typeStr) {
    if (typeStr.equals("ao")) {
      return Type.AND_OPP;
    }
    else if (typeStr.equals("oo")) {
      return Type.OR_OPP;
    }
    else if (typeStr.equals("ap")) {
      return Type.AND_PRO;
    }
    else {
      return Type.OR_PRO;
    }
  }

  private static final String C_PRO            = "cp";
  private static final String C_OPP            = "co";

  private Type                type;
  private static final long   serialVersionUID = -8433351516296617004L;

}
