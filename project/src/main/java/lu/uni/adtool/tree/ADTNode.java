package lu.uni.adtool.tree;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

  /**
   * Export to XML using format used by the first version of ADTool
   *
   */
  public XElement exportXml(ArrayList<ValuationDomain> domains) {
    XElement result = new XElement("node");
    result.addString("refinement", typeToXml(type));
    if (getParent() != null && (((ADTNode) getParent()).getRole() != getRole())) {
      result.addString("switchRole", "yes");
    }
    result.addElement("label").setString(getName());
    if (domains != null && Options.main_saveDomains) {
      for (int i = 0; i < domains.size(); i++) {
        ValuationDomain vd = domains.get(i);
        String domainId = vd.getExportXmlId();
        if (this.isEditable((AdtDomain) vd.getDomain())) {
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
      }
    }
    if (this.getChildren() != null) {
      for (Node node : this.getNotNullChildren()) {
        result.addElement(((ADTNode) node).exportXml(domains));
      }
    }
    return result;
  }

  public boolean isEditable(AdtDomain domain) {
    return hasDefault() && domain.isValueModifiable(this);
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
    if (type == Type.OR_PRO ||type == Type.OR_OPP) {
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
    eol = "\n";
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

  public void importXml(XElement e, HashMap<String, ValuationDomain> domains)
      throws IllegalArgumentException {
    setName(e.getElement("label").getString());
    boolean switchRole;
    try {
      switchRole = (e.getString("switchRole").toLowerCase().equals("yes")
          || e.getString("switchRole").toLowerCase().equals("true"));
    }
    catch (XException exception) {
      switchRole = false;
    }
    Debug.log("Switch role:" + switchRole + " node:" + getName());
    if (getParent() == null) {
      this.type = xmlToType(e.getString("refinement"), Role.PROPONENT);
    }
    else {
      this.type = xmlToType(e.getString("refinement"), ((ADTNode) getParent()).getRole());
    }
    if (switchRole) {
      this.toggleRole();
    }
    Debug.log("type:" + this.type + " node:" + getName());
    for (XElement parameter : e.getElements("parameter")) {
      String category = parameter.getString("category");
      if (category == null) {
        throw new IllegalArgumentException(Options.getMsg("exception.wrongxml"));
      }
      if (category.equals("basic")) {
        ValuationDomain d = domains.get(parameter.getString("domainId"));
        if (d != null) {
          Ring r = d.getDomain().getDefaultValue(this);
          r.updateFromString(parameter.getString());
          d.setValue(this.getRole() == ADTNode.Role.PROPONENT, getName(), r);
        }
      }
    }
    for (XElement child : e.getElements("node")) {
      ADTNode ch = new ADTNode();
      this.addChild(ch);
      ch.importXml(child, domains);
    }
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

  private static final String C_PRO = "cp";
  private static final String C_OPP = "co";

  private Type                type;

}
