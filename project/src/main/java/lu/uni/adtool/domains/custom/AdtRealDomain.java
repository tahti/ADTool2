package lu.uni.adtool.domains.custom;

import java.text.DecimalFormat;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.adtpredefined.DescriptionGenerator;
import lu.uni.adtool.domains.rings.Real;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

public class AdtRealDomain  implements AdtDomain<Real>, AdtCustomDomain {

  public AdtRealDomain() {
    name = Options.getMsg("adtdomain.custom.real.name");
    description = Options.getMsg("adtdomain.custom.real.description");
    this.parser = new RealParser();
    this.proDefault = new Real(0);
    this.oppDefault = new Real(0);
    this.opponnentModifiable = true;
    this.proponentModifiable = true;
    this.precision = new DecimalFormat("#0.###");
  }

  public boolean isValueModifiable(boolean isProponent) {
    if(isProponent) {
      return this.proponentModifiable;
    }
    else {
      return this.opponnentModifiable;
    }
  }

  public Real calc(Real a, Real b, ADTNode.Type type) {
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

  public boolean setName(String name) {
    this.name = name;
    if (name != null && (!name.equals(""))) {
      Debug.log("ok");
      return true;
    }
    Debug.log("not ok");
    return false;
  }

  public String format(Real a) {
    return this.precision.format(a.getValue());
  }

  public boolean setDescription(String description) {
    if (description == null || (description.equals(""))) {
      return false;
    }
    this.description = description;
    return true;
  }

  public final Real cp(final Real a, final Real b) {
    return this.cp.evaluate(a, b);
  }

  public final Real co(final Real a, final Real b) {
    return this.co.evaluate(a, b);
  }

  public final Real getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return (Real) proDefault.clone();
    }
    else {
      return (Real) oppDefault.clone();
    }
  }

  public final String getPrecision() {
    if (this.precision == null) {
      return "";
    }
    return this.precision.toPattern();
  }

  public boolean setPrecision(String text) {
    if (text.equals("")) {
      this.precision = null;
      return true;
    }
    else {
      try {
        this.precision.applyPattern(text);
        return true;
      }
      catch (IllegalArgumentException e) {
        return false;
      }
      catch (NullPointerException e) {
        return false;
      }
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
    }
    else {
      return "";
    }
  }

  public String getCo() {
    if (co != null) {
      return this.co.toString();
    }
    else {
      return "";
    }
  }

  public String getAp() {
    if (ap != null) {
      return this.ap.toString();
    }
    else {
      return "";
    }
  }

  public String getAo() {
    if (ao != null) {
      return this.ao.toString();
    }
    else {
      return "";
    }
  }

  public String getOp() {
    if (op != null) {
      return this.op.toString();
    }
    else {
      return "";
    }
  }

  public String getOo() {
    if (oo != null) {
      return this.oo.toString();
    }
    else {
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
      return DescriptionGenerator.generateDescription(this, description, Options.getMsg("domain.real"), operators);
    } catch (NullPointerException e) {
      return this.description;
    }
  }

  public boolean setOppDefault(String value) {
    return this.oppDefault.updateFromString(value);
  }

  public void setOppDefault(double value) {
    this.oppDefault = new Real(value);
  }

  public void setProDefault(double value) {
    this.proDefault = new Real(value);
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
    }
    else {
      return "";
    }
  }

  public String getProDefault() {
    if (this.proDefault != null) {
      return proDefault.toString();
    }
    else {
      return "";
    }
  }

  public boolean isOppModifiable() {
    return this.opponnentModifiable;
  }

  public boolean isProModifiable() {
    return this.proponentModifiable;
  }

  private Real                 oppDefault;
  private Real                 proDefault;
  private boolean              opponnentModifiable;
  private boolean              proponentModifiable;
  private String               name;
  private String               description;
  private RealExpression       cp;
  private RealExpression       co;
  private RealExpression       ao;
  private RealExpression       ap;
  private RealExpression       oo;
  private RealExpression       op;
  private DecimalFormat        precision;
  private transient RealParser parser;

  private static final long    serialVersionUID = -7059248027203727886L;
}
