package lu.uni.adtool.domains.custom;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.adtpredefined.DescriptionGenerator;
import lu.uni.adtool.domains.rings.Int;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

public class AdtIntDomain implements AdtDomain<Int>, AdtCustomDomain {

  public AdtIntDomain() {
    name = Options.getMsg("adtdomain.custom.int.name");
    description = Options.getMsg("adtdomain.custom.int.description");
    this.parser = new IntParser();
    this.proDefault = new Int(0);
    this.oppDefault = new Int(0);
    this.opponnentModifiable = true;
    this.proponentModifiable = true;
  }

  public boolean isValueModifiable(boolean isProponent) {
    if(isProponent) {
      return this.proponentModifiable;
    }
    else {
      return this.opponnentModifiable;
    }
  }

  public Int calc(Int a, Int b, ADTNode.Type type) {
    try {
      switch (type) {
      case OR_OPP:
        return oo.evaluate(a, b);
      case AND_OPP:
        return ao.evaluate(a, b);
      case OR_PRO:
        return op.evaluate(a, b);
      case AND_PRO:
        return ap.evaluate(a, b);
      default:
        Debug.log("Wrong type:" + type);
        return oo.evaluate(a, b);
      }
    }
    catch (ArithmeticException e) {
      return new Int(0, true);
    }
  }

  public boolean setName(String name) {
    this.name = name;
    if (name != null && (!name.equals(""))) {
      Debug.log("ok");
      return true;
    }
    Debug.log("not ok");
    return false;
  }

  public boolean setDescription(String description) {
    if (description == null || (description.equals(""))) {
      return false;
    }
    this.description = description;
    return true;
  }

  public final Int cp(final Int a, final Int b) {
    try {
      return this.cp.evaluate(a, b);
    }
    catch (ArithmeticException e) {
      return new Int(0, true);
    }

  }

  public final Int co(final Int a, final Int b) {
    try {
      return this.co.evaluate(a, b);
    }
    catch (ArithmeticException e) {
      return new Int(0, true);
    }
  }

  public final Int getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return (Int) proDefault.clone();
    } else {
      return (Int) oppDefault.clone();
    }
  }

  public boolean setCp(String expr) {
    this.cp = this.parser.parseString(expr);
    if (this.cp == null) {
      Debug.log("not ok:\"" + expr + "\"");
      return false;
    }
    Debug.log("ok");
    this.cp.toString();
    return true;
  }

  public boolean setCo(String expr) {
    this.co = this.parser.parseString(expr);
    if (this.co == null) {
      Debug.log("not ok:" + expr);
      return false;
    }
    Debug.log("ok");
    return true;
  }

  public boolean setAp(String expr) {
    this.ap = this.parser.parseString(expr);
    if (this.ap == null) {
      Debug.log("not ok");
      return false;
    }
    Debug.log("ok");
    return true;
  }

  public boolean setAo(String expr) {
    this.ao = this.parser.parseString(expr);
    if (this.ao == null) {
      Debug.log("not ok");
      return false;
    }
    Debug.log("ok");
    return true;
  }

  public boolean setOo(String expr) {
    this.oo = this.parser.parseString(expr);
    if (this.oo == null) {
      Debug.log("not ok");
      return false;
    }
    Debug.log("ok");
    return true;
  }

  public boolean setOp(String expr) {
    this.op = this.parser.parseString(expr);
    if (this.op == null) {
      Debug.log("not ok");
      return false;
    }
    Debug.log("ok");
    return true;
  }

  public String getCp() {
    if (cp != null) {
      return this.cp.toString();
    } else {
      return "";
    }
  }

  public String getCo() {
    if (co != null) {
      return this.co.toString();
    } else {
      return "";
    }
  }

  public String getAp() {
    if (ap != null) {
      return this.ap.toString();
    } else {
      return "";
    }
  }

  public String getAo() {
    if (ao != null) {
      return this.ao.toString();
    } else {
      return "";
    }
  }

  public String getOp() {
    if (op != null) {
      return this.op.toString();
    } else {
      return "";
    }
  }

  public String getOo() {
    if (oo != null) {
      return this.oo.toString();
    } else {
      return "";
    }
  }

  public String getName() {
    return this.name;
  }

  public String getShortDescription() {
    return this.description;
  }

  public String getDescription() {
    try {
      String[] operators = { this.op.toString() // 0
          , this.ap.toString() // 1
          , this.oo.toString() // 2
          , this.ao.toString() // 3
          , this.cp.toString() // 4
          , this.co.toString() // 5
      };
      return DescriptionGenerator.generateDescription(this, description, Options.getMsg("domain.integer"), operators);
    } catch (NullPointerException e) {
      return this.description;
    }
  }

  public boolean setOppDefault(String value) {
    return this.oppDefault.updateFromString(value);
  }

  public void setOppDefault(int value) {
    this.oppDefault = new Int(value);
  }

  public void setProDefault(int value) {
    this.proDefault = new Int(value);
  }

  public boolean setProDefault(String value) {
    return this.proDefault.updateFromString(value);
  }

  public void setOppModifiable(boolean value) {
    this.opponnentModifiable = value;
  }

  public void setProModifiable(boolean value) {
    this.proponentModifiable = value;
  }

  public String getOppDefault() {
    if (this.oppDefault != null) {
      return oppDefault.toString();
    } else {
      return "";
    }
  }

  public String getProDefault() {
    if (this.proDefault != null) {
      return proDefault.toString();
    } else {
      return "";
    }
  }

  public boolean isOppModifiable() {
    return this.opponnentModifiable;
  }

  public boolean isProModifiable() {
    return this.proponentModifiable;
  }

  private Int                 oppDefault;
  private Int                 proDefault;
  private boolean             opponnentModifiable;
  private boolean             proponentModifiable;
  private String              name;
  private String              description;
  private IntExpression       cp;
  private IntExpression       co;
  private IntExpression       ao;
  private IntExpression       ap;
  private IntExpression       oo;
  private IntExpression       op;
  private transient IntParser parser;

  private static final long   serialVersionUID = -7059248027203727886L;
}
