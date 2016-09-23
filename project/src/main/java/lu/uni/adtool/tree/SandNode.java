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

import lu.uni.adtool.domains.RankExporter;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
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

public class SandNode extends GuiNode {

  public SandNode() {
    super("Root");
    this.type = Type.AND;
  }

  public SandNode(Type type) {
    super();
    this.type = type;
  }

  /**
   * Type of node: and, or, sequential and, leaf node.
   */
  public enum Type {
    AND, OR, SAND
  }

  public static SandNode readStream(DataInputStream in) throws IOException {
    String name = in.readUTF();
    String comment = in.readUTF();
    Type type = Type.values()[in.readInt()];
    SandNode result = new SandNode(type);
    result.setParent(null);
    result.setName(name);
    result.setComment(comment);
    int noChildren = in.readInt();
    for (int i = 0; i < noChildren; i++) {
      SandNode child = readStream(in);
      result.addChild(child);
    }
    return result;
  }

  public void writeStream(DataOutputStream out) throws IOException {
    out.writeUTF(this.getName());
    out.writeUTF(this.getComment());
    out.writeInt(this.type.ordinal());
    if (getChildren() == null || getChildren().size() == 0) {
      out.writeInt(0);
    }
    else {
      out.writeInt(getChildren().size());
    }
    for (Node child : getChildren()) {
      ((SandNode) child).writeStream(out);
    }
  }
  /**
   * Import from XML using format that stores every tree together with layout used by Docking Frames
   *
   */

  public void fromXml(XElement e) {
    setName(e.getElement("label").getString());
    XElement commentXml = e.getElement("comment");
    if (commentXml != null) {
      setComment(commentXml.getString());
    }
    this.type = stringToType(e.getString("refinement"));
    for (XElement child : e.getElements("node")) {
      SandNode ch = new SandNode();
      ch.fromXml(child);
      this.addChild(ch);
    }
  }

  /**
   * Export to XML using format used by the first version of ADTool
   *
   */
  public XElement exportXml(Collection<ValuationDomain> domains, ArrayList<RankExporter> rankers, Set<Integer> idToExport) {
    XElement result = new XElement("node");
    result.addString("refinement", typeToXml(type));
    result.addElement("label").setString(getName());
    if (getComment()!= null && (!getComment().equals(""))) {
      result.addElement("comment").setString(getComment());
    }
    if (domains != null && idToExport != null) {
      int i = 0;
      for (ValuationDomain vd: domains) {
        if (idToExport.contains(vd.getDomainId())) {
          String domainId = vd.getExportXmlId();
          if (this.isEditable()) {
            if (vd.getValue(this) != null) {
              XElement param = result.addElement("parameter");
              param.addString("domainId", domainId);
              param.addString("category", "basic");
              param.setString(vd.getValue(this).toString());
            }
          }
          else {
            if (Options.main_saveDerivedValues) {
              XElement param = result.addElement("parameter");
              param.addString("domainId", domainId);
              param.addString("category", "derived");
              param.setString(vd.getTermValue(this).toString());
            }
          }
          if (rankers != null && rankers.size() > i) {
            for (int j=0; j < Options.rank_noRanked; j++) {
              Ring value = rankers.get(i).getValue(this, j);
              if(value != null) {
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
        result.addElement(((SandNode) node).exportXml(domains, rankers, idToExport));
      }
    }
    return result;
  }
  /**
   * Export to XML using format to store every tree together with layout used by Docking Frames
   *
   */
  public XElement toXml() {
    XElement result = new XElement("node");
    XAttribute typeAttribute = new XAttribute("refinement");
    typeAttribute.setString(typeToString(type));
    result.addAttribute(typeAttribute);
    result.addElement("label").setString(getName());
    if (getComment()!= null && (!getComment().equals(""))) {
      result.addElement("comment").setString(getComment());
    }
    if (this.getChildren() != null) {
      for (Node node : this.getNotNullChildren()) {
        result.addElement(((SandNode) node).toXml());
      }
    }
    return result;
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
    if (this.getChildren() != null && this.getChildren().size() > 0) {
      texStr.append(System.getProperty("line.separator"));
      for (Node node : this.getNotNullChildren()) {
        texStr.append(((SandNode)node).toLatex(depth + 1));
      }
      texStr.append(indent.toString());
    }
    texStr.append("]");
    if (this.getChildren() != null && this.getChildren().size() > 1) {
      if (getType() == SandNode.Type.AND) {
        texStr.append("\\andN");
      }
      else if (getType() == SandNode.Type.SAND) {
        texStr.append("\\sandN");
      }
    }
    texStr.append(System.getProperty("line.separator"));
    return texStr.toString();
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

  public String toTerms() {
    return toTerms(0);
  }

  public boolean isEditable() {
    return isLeaf();
  }

  public void toggleOp() {
    switch (type) {
    case AND:
      setType(Type.SAND);
      break;
    case SAND:
      setType(Type.OR);
      break;
    case OR:
      setType(Type.AND);
      break;
    }
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
    eol = "\n";
    if (children != null && children.size() > 0) {
      for (int i = 0; i < children.size(); ++i) {
        final SandNode n = (SandNode) children.get(i);
        if (n != null) {
          result += n.toTerms(level + 1);
          if ((i + 1) < children.size()) {
            result += ",";
          }
          result += eol;
        }
        else {
          System.err.println("Null child at index:" + i);
        }
      }
      return currIndent + typeToString(type) +"(" + eol + result + currIndent + ")";
    }
    else {
      return currIndent + this.getName();
    }
  }

  public void importXml(XElement e, HashMap<String, ValuationDomain> domains)
      throws IllegalArgumentException {
    setName(e.getElement("label").getString());
    XElement commentXml = e.getElement("comment");
    if (commentXml != null) {
      setComment(commentXml.getString());
    }
    this.type = xmlToType(e.getString("refinement"));
    for (XElement parameter: e.getElements("parameter")) {
      String category = parameter.getString("category");
      if (category == null) {
        throw new IllegalArgumentException(Options.getMsg("exception.wrongxml"));
      }
      if (category.equals("basic")){
        ValuationDomain d = domains.get(parameter.getString("domainId"));
        if (d != null) {
          Ring r = d.getDomain().getDefaultValue(this);
          r.updateFromString(parameter.getString());
          d.setValue(true, getName(), r);
        }
      }
    }
    for (XElement child : e.getElements("node")) {
      SandNode ch = new SandNode();
      ch.importXml(child, domains);
      this.addChild(ch);
    }
  }

  public ADTNode adtCopy() {
    ADTNode result = new ADTNode();
    result.setName(getName());
    result.setComment(getComment());
    if (type == Type.OR) {
      result.setType(ADTNode.Type.OR_PRO);
    }
    else {
      result.setType(ADTNode.Type.AND_PRO);
    }
    result.setLeftSibling(null);
    result.setRightSibling(null);
    if (getChildren() != null) {
      for (Node child : getChildren()) {
        result.addChild(((SandNode) child).adtCopy());
      }
    }
    return result;
  }

  public SandNode deepCopy() {
    SandNode result = new SandNode();
    result.setName(getName());
    result.setComment(getComment());
    result.setType(getType());
    result.setLeftSibling(null);
    result.setRightSibling(null);
    if (getChildren() != null) {
      for (Node child : getChildren()) {
        result.addChild(((SandNode) child).deepCopy());
      }
    }
    return result;
  }

  private Type stringToType(String typeStr) {
    if (typeStr.equals("AND")) {
      return Type.AND;
    }
    else if (typeStr.equals("SAND")) {
      return Type.SAND;
    }
    else {
      return Type.OR;
    }
  }

  private String typeToString(Type type) {
    switch (this.type) {
    case AND:
      return "AND";
    case OR:
      return "OR";
    case SAND:
      return "SAND";
    }
    return "";
  }

  private Type xmlToType(String typeStr) {
    if (typeStr.equals("conjunctive")) {
      return Type.AND;
    }
    else if (typeStr.equals("disjunctive")) {
      return Type.OR;
    }
    else {
      return Type.SAND;
    }
  }

  private String typeToXml(Type type) {
    switch (this.type) {
    case AND:
      return "conjunctive";
    case OR:
      return "disjunctive";
    case SAND:
      return "sequential";
    }
    return "";
  }

  private Type type;
  private static final long serialVersionUID = -8503744808785401801L;

}
