package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Parametrized;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A domain to calculate minimal skill level needed for the proponent.
 *
 * @author Piot Kordy
 */
public class ReachPar extends RankingDomain<BoundedInteger> implements Parametrized {
  static final long serialVersionUID = 154666564465361444L;
  private int       bound;

  /**
   * Constructs a new instance.
   */
  public ReachPar() {
    bound = Integer.MAX_VALUE;
  }

  public boolean isOrType(ADTNode.Type operation) {
    switch (operation) {
    case AND_OPP:
    case OR_PRO:
      return true;
    case OR_OPP:
    case AND_PRO:
    default:
      return false;
    }
  }

  public BoundedInteger calc(BoundedInteger a, BoundedInteger b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return BoundedInteger.max(a, b);
    case AND_OPP:
      return BoundedInteger.min(a, b);
    case OR_PRO:
      return BoundedInteger.min(a, b);
    case AND_PRO:
      return BoundedInteger.max(a, b);
    default:
      return BoundedInteger.max(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final BoundedInteger getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new BoundedInteger(bound, bound);
    }
    else {
      return new BoundedInteger(bound, bound);
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
  public String getName() {
    return Options.getMsg("adtdomain.reachpar.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.reachpar.description");

    final String vd = "{0,\u2026,k}";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)",
            "min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }


  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger cp(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger co(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.min(a, b);
  }

  /**
   * Gets the bound for this instance.
   *
   * @return The bound.
   */
  public int getBound() {
    return this.bound;
  }

  /**
   * Sets the bound for this instance.
   *
   * @param bound
   *          The bound.
   */
  public void setParameter(Object parameter) {
    this.bound = ((Integer) parameter).intValue();
  }

  /**
   * Gets the bound for this instance.
   *
   * @return The bound.
   */
  public Integer getParameter() {
    return new Integer(getBound());
  }
}
