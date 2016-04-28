package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.rings.Bool;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A AtdDomain defined on booleans.
 *
 * @author Piotr Kordy
 */
public class SatProp implements AdtDomain<Bool> {
  /**
   * A default constructor.
   *
   */
  public SatProp() {
  }

  public Bool calc(Bool a, Bool b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return Bool.and(a, b);
    case AND_OPP:
      return Bool.or(a, b);
    case OR_PRO:
      return Bool.or(a, b);
    case AND_PRO:
      return Bool.and(a, b);
    default:
      return Bool.and(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final Bool getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new Bool(false);
    }
    else {
      return new Bool(false);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#isValueModifiable(boolean)
   */
  public final boolean isValueModifiable(ADTNode node) {
    return node.getRole() == ADTNode.Role.PROPONENT;
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getName()
   */
  public final String getName() {
    return Options.getMsg("adtdomain.satprop.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.satprop.description");
    final String vd = "{true,&nbsp;false}";
    final String[] operators =
        {"<i>x</i>&nbsp;&or;&nbsp;<i>y</i>", "<i>x</i>&nbsp;&and;&nbsp;<i>y</i>",
            "<i>x</i>&nbsp;&and;&nbsp;<i>y</i>", "<i>x</i>&nbsp;&or;&nbsp;<i>y</i> ",
            "<i>x</i>&nbsp;&and;&nbsp;<i>y</i>", "<i>x</i>&nbsp;&or;&nbsp;<i>y</i>",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(Bool,Bool)
   */
  public final Bool cp(final Bool a, final Bool b) {
    return Bool.and(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(Bool,Bool)
   */
  public final Bool co(final Bool a, final Bool b) {
    return Bool.or(a, b);
  }

  static final long serialVersionUID = 268474778914366456L;
}
