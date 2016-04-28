package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.tools.Options;

/**
 * A AtdDomain defined on booleans.
 *
 * @author Piotr Kordy
 */
public class MinCost extends MinTimeSeq {
  // number 6

  /**
   * Constructs a new instance.
   */
  public MinCost() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getName()
   */
  public final String getName() {
    return Options.getMsg("adtdomain.mincost.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.mincost.description");
    final String vd = "&#x211D;\u208A\u222A{\u221E}";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "<i>x</i>&nbsp;+&nbsp;<i>y</i>", "<i>x</i>&nbsp;+&nbsp;<i>y</i>",
            "min(<i>x</i>,<i>y</i>)", "<i>x</i>&nbsp;+&nbsp;<i>y</i>", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }
  static final long serialVersionUID = 665945232556446846L;
}
