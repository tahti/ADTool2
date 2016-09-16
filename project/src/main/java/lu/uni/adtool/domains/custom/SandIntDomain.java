package lu.uni.adtool.domains.custom;

import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.adtpredefined.DescriptionGenerator;
import lu.uni.adtool.domains.rings.Int;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

public class SandIntDomain implements SandDomain<Int>, SandCustomDomain {

  public SandIntDomain() {
    name = Options.getMsg("adtdomain.custom.int.name");
    description = Options.getMsg("adtdomain.custom.int.description");
    this.parser = new IntParser();
    this.defaultValue = new Int(0);
  }

  public Int or(Int a, Int b) {
    return calc(a, b, SandNode.Type.OR);
  }

  public Int and(Int a, Int b) {
    return calc(a, b, SandNode.Type.AND);
  }

  public Int sand(Int a, Int b) {
    return calc(a, b, SandNode.Type.SAND);
  }

  public Int calc(Int a, Int b, SandNode.Type type) {
    try {
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
    catch (ArithmeticException e) {
      return new Int(0, true);
    }
  }

  public boolean setName(String name) {
    this.name = name;
    if (name != null && (!name.equals(""))) {
      return true;
    }
    return false;
  }

  public boolean setDescription(String description) {
    if (description == null || (description.equals(""))) {
      return false;
    }
    this.description = description;
    return true;
  }

  public final Int getDefaultValue(Node node) {
    return (Int) defaultValue.clone();
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
    } else {
      return "";
    }
  }

  public String getAnd() {
    if (this.and != null) {
      return this.and.toString();
    } else {
      return "";
    }
  }

  public String getSand() {
    if (this.sand != null) {
      return this.sand.toString();
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
      String[] operators = { this.or.toString() // 0
          , this.and.toString() // 1
          , this.sand.toString() // 2
      };
      return DescriptionGenerator.generateDescription(this, description, Options.getMsg("domain.integer"), operators);
    } catch (NullPointerException e) {
      return this.description;
    }
  }

  public boolean setDefault(String value) {
    return this.defaultValue.updateFromString(value);
  }

  public void setDefault(int value) {
    this.defaultValue = new Int(value);
  }

  public String getDefault() {
    if (this.defaultValue != null) {
      return defaultValue.toString();
    } else {
      return "";
    }
  }

  private Int                 defaultValue;
  private String              name;
  private String              description;
  private IntExpression       or;
  private IntExpression       and;
  private IntExpression       sand;
  private transient IntParser parser;
  private static final long serialVersionUID = -2237029895126663951L;

}
