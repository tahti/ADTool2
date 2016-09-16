package lu.uni.adtool.domains.custom;

import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.adtpredefined.DescriptionGenerator;
import lu.uni.adtool.domains.rings.Real;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

import java.text.DecimalFormat;

public class SandRealDomain  implements SandDomain<Real>, SandCustomDomain {


  public SandRealDomain() {
    name = Options.getMsg("adtdomain.custom.real.name");
    description = Options.getMsg("adtdomain.custom.real.description");
    this.parser = new RealParser();
    this.defaultValue = new Real(0);
    this.precision = new DecimalFormat("#0.###");
  }

  public Real or(Real a, Real b) {
    return calc(a, b, SandNode.Type.OR);
  }

  public Real and(Real a, Real b) {
    return calc(a, b, SandNode.Type.AND);
  }

  public Real sand(Real a, Real b) {
    return calc(a, b, SandNode.Type.SAND);
  }

  public Real calc(Real a, Real b, SandNode.Type type) {
    switch (type) {
    case OR:
      return this.or.evaluate(a, b);
    case AND:
      return this.and.evaluate(a, b);
    case SAND:
      return this.sand.evaluate(a, b);
    default:
      Debug.log("Wrong type:" + type);
      return this.or.evaluate(a, b);
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


  public final Real getDefaultValue(Node node) {
    return (Real) this.defaultValue.clone();
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


  public boolean setOr(String expr) {
    this.or = this.parser.parseString(expr);
    if (this.or == null) {
      return false;
    }
    return true;
  }

  public boolean setAnd(String expr) {
    this.and = this.parser.parseString(expr);
    if (this.and == null) {
      return false;
    }
    return true;
  }

  public boolean setSand(String expr) {
    this.sand = this.parser.parseString(expr);
    if (this.sand == null) {
      return false;
    }
    return true;
  }


  public String getOr() {
    if (this.or != null) {
      return this.or.toString();
    }
    else {
      return "";
    }
  }

  public String getAnd() {
    if (this.and != null) {
      return this.and.toString();
    }
    else {
      return "";
    }
  }

  public String getSand() {
    if (this.sand != null) {
      return this.sand.toString();
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
      String[] operators = { this.or.toString() // 0
          , this.and.toString() // 2
          , this.sand.toString() // 3
      };
      return DescriptionGenerator.generateDescription(this, description, Options.getMsg("domain.real"), operators);
    } catch (NullPointerException e) {
      return this.description;
    }
  }

  public boolean setDefault(String value) {
    return this.defaultValue.updateFromString(value);
  }

  public void setDefault(double value) {
    this.defaultValue = new Real(value);
  }



  public String getDefault() {
    if (this.defaultValue != null) {
      return defaultValue.toString();
    }
    else {
      return "";
    }
  }



  private Real                 defaultValue;
  private String               name;
  private String               description;
  private RealExpression       or;
  private RealExpression       and;
  private RealExpression       sand;
  private DecimalFormat        precision;
  private transient RealParser parser;
  private static final long serialVersionUID = -7507500315046509127L;
}
