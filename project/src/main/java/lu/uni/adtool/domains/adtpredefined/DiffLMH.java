package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.rings.LMHValue;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A domain to calculate minimal skill level needed for the proponent.
 *
 * @author Piotr Kordy
 */
public class DiffLMH  extends RankingDomain<LMHValue> {
  /**
   * Constructs a new instance.
   */
  public DiffLMH() {
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

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final LMHValue getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new LMHValue(3);
    }
    else {
      return new LMHValue(LMHValue.INF);
    }
  }

  public LMHValue calc(LMHValue a, LMHValue b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return LMHValue.max(a, b);
    case AND_OPP:
      return LMHValue.min(a, b);
    case OR_PRO:
      return LMHValue.min(a, b);
    case AND_PRO:
      return LMHValue.max(a, b);
    default:
      return LMHValue.max(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(LMHValue,LMHValue)
   */
  public final LMHValue cp(final LMHValue a, final LMHValue b) {
    return LMHValue.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(LMHValue,LMHValue)
   */
  public final LMHValue co(final LMHValue a, final LMHValue b) {
    return LMHValue.min(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#isValueModifiable(boolean)
   */
  public final boolean isValueModifiable(ADTNode node) {
    return ((ADTNode) node).getRole() == ADTNode.Role.PROPONENT;
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getName()
   */
  public String getName() {
    return Options.getMsg("adtdomain.difflmh.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.difflmh.description");

    final String vd = "<nobr>{L,M,H,\u221E}</nobr>";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)",
            "min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }


  static final long serialVersionUID = 14645614725266844L;
}
