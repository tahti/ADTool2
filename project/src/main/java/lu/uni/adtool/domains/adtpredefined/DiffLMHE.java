package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A domain to calculate minimal skill level needed for the proponent.
 *
 * @author Piot Kordy
 */
public class DiffLMHE extends RankingDomain<LMHEValue> {

  /**
   * Constructs a new instance.
   */
  public DiffLMHE() {
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

  public LMHEValue calc(LMHEValue a, LMHEValue b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return LMHEValue.max(a, b);
    case AND_OPP:
      return LMHEValue.min(a, b);
    case OR_PRO:
      return LMHEValue.min(a, b);
    case AND_PRO:
      return LMHEValue.max(a, b);
    default:
      return LMHEValue.max(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(LMHEValue,LMHEValue)
   */
  public final LMHEValue cp(final LMHEValue a, final LMHEValue b) {
    return LMHEValue.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(LMHEValue,LMHEValue)
   */
  public final LMHEValue co(final LMHEValue a, final LMHEValue b) {
    return LMHEValue.min(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final LMHEValue getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new LMHEValue(4);
    }
    else {
      return new LMHEValue(LMHEValue.INF);
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
    return Options.getMsg("adtdomain.difflmhe.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.difflmhe.description");
    final String vd = "<nobr>{L,M,H,E,\u221E}</nobr>";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)",
            "min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }

  static final long serialVersionUID = 14682694465266848L;
}
