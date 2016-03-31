package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A AtdDomain defined on booleans.
 *
 * @author Piot Kordy
 */
public class ProbSucc implements AdtDomain<RealZeroOne> {

  /**
   * Constructs a new instance.
   */
  public ProbSucc() {
  }
  public RealZeroOne calc(RealZeroOne a, RealZeroOne b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return RealZeroOne.plusProb(a, b);
    case AND_OPP:
      return RealZeroOne.times(a, b);
    case OR_PRO:
      return RealZeroOne.plusProb(a, b);
    case AND_PRO:
      return RealZeroOne.times(a, b);
    default:
      return RealZeroOne.times(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final RealZeroOne getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new RealZeroOne(0);
    }
    else {
      return new RealZeroOne(0);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#isValueModifiable(boolean)
   */
  public final boolean isValueModifiable(ADTNode node) {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getName()
   */
  public final String getName() {
    return Options.getMsg("adtdomain.probsucc.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.probsucc.description");
    final String vd = "[0,1]";
    final String[] operators = {"<i>x</i>&nbsp;+&nbsp;<i>y</i>&nbsp;-&nbsp;<i>x</i><i>y</i>",
        "<i>x</i><i>y</i>", "<i>x</i>&nbsp;+&nbsp;<i>y</i>&nbsp;-&nbsp;<i>x</i><i>y</i>",
        "<i>x</i><i>y</i>", "<i>x</i>(1&nbsp;-&nbsp;<i>y</i>)", "<i>x</i>(1&nbsp;-&nbsp;<i>y</i>)"};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }


  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(RealZeroOne,RealZeroOne)
   */
  public final RealZeroOne cp(final RealZeroOne a, final RealZeroOne b) {
    return RealZeroOne.minusProb(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(RealZeroOne,RealZeroOne)
   */
  public final RealZeroOne co(final RealZeroOne a, final RealZeroOne b) {
    return RealZeroOne.minusProb(a, b);
  }
  static final long serialVersionUID = 865945232556446848L;
}
