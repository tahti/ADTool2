package lu.uni.adtool.domains.predefined;

import lu.uni.adtool.domains.Parametrized;
import lu.uni.adtool.domains.rings.BoundedInteger;

/**
 * A domain to calculate minimal skill level needed for the proponent. - Obsolete - used to import adt only.
 *
 * @author Piotr Kordy
 */
public class MinSkill implements Domain<BoundedInteger>, Parametrized {

  /**
   * {@inheritDoc}
   *
   * @see Domain#getDefaultValue()
   */
  public final BoundedInteger getDefaultValue(final boolean proponent) {
    if (proponent) {
      return new BoundedInteger(bound, bound);
      // return new BoundedInteger(BoundedInteger.INF,bound);
    }
    else {
      return new BoundedInteger(BoundedInteger.INF, bound);
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
    return "Minimal skill level needed for the proponent";
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

  static final long serialVersionUID = 146456146456646844L;
  private int       bound;
}
