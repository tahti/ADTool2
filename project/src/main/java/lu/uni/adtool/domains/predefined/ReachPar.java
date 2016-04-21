package lu.uni.adtool.domains.predefined;

import lu.uni.adtool.domains.predefined.Domain;
import lu.uni.adtool.domains.rings.BoundedInteger;

/**
 * A domain to calculate minimal skill level needed for the proponent. -
 * Obsolete
 *
 * @author Piot Kordy
 */
public class ReachPar implements Domain<BoundedInteger>, Parametrized {

  /**
   * Constructs a new instance.
   */
  public ReachPar() {
    bound = Integer.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getDefaultValue()
   */
  public final BoundedInteger getDefaultValue(final boolean proponent) {
    if (proponent) {
      return new BoundedInteger(bound, bound);
    }
    else {
      return new BoundedInteger(bound, bound);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#isValueModifiable(boolean)
   */
  public final boolean isValueModifiable(final boolean proponent) {
    return proponent;
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getName()
   */
  public String getName() {
    return "Reachability of the proponent's goal in less than k units" + " (in parallel)";
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getDescription()
   */
  public final String getDescription() {
    return "";
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#op(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger op(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.min(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#ap(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger ap(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#oo(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger oo(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#ao(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger ao(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.min(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#cp(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger cp(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#co(BoundedInteger,BoundedInteger)
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
  static final long serialVersionUID = 154666564465361444L;
  private int       bound;
}
