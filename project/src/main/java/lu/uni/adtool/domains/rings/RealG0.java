package lu.uni.adtool.domains.rings;

import java.io.Serializable;

/**
 * Set of real numbers with maximum value.
 *
 * @author Piotr Kordy
 */
public class RealG0 implements Serializable, Ring {

  /**
   * Constructs a new instance.
   */
  public RealG0() {
    this(0);
  }

  /**
   * Constructs a newly allocated RealG0 object that represents the primitive
   * double argument.
   *
   * @param d
   *          the value to be represented by the RealG0.
   */
  public RealG0(final double d) {
    value = d;
    checkValue();
  }

  public Object clone() {
    return new RealG0(value);
  }

  /**
   * Set the new value reading from the string
   *
   */
  public final boolean updateFromString(String s) {
    double origValue = this.value;
    try {
      this.value = Double.parseDouble(s);
    }
    catch (NumberFormatException e) {
      this.value = origValue;
      return false;
    }
    if (!checkValue()) {
      this.value = origValue;
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  public final String toString() {
    return new Double(getValue()).toString();
  }

  /**
   * Unicode representation of a number.
   *
   * @return string with unicode representation.
   */
  public final String toUnicode() {
    if (getValue() == Double.POSITIVE_INFINITY) {
      return "\u221E";
    }
    else {
      return new Double(getValue()).toString();
    }
  }

  /**
   * Return a minimal number of the two.
   *
   * @param a
   *          the first number to be compared.
   * @param b
   *          the second nubmer to be compared.
   * @return the smaller of two numbers.
   */
  public static final RealG0 min(final RealG0 a, final RealG0 b) {
    return new RealG0(Math.min(a.getValue(), b.getValue()));
  }

  /**
   * Return a maximal number of the two.
   *
   * @param a
   *          the first number to be compared.
   * @param b
   *          the second nubmer to be compared.
   * @return the bigger of two numbers.
   */
  public static final RealG0 max(final RealG0 a, final RealG0 b) {
    return new RealG0(Math.max(a.getValue(), b.getValue()));
  }

  /**
   * Sums two numbers.
   *
   * @param a
   *          the first number to be compared.
   * @param b
   *          the second nubmer to be compared.
   * @return the sum of two numbers.
   */
  public static final RealG0 sum(final RealG0 a, final RealG0 b) {
    return new RealG0(a.getValue() + b.getValue());
  }

  /**
   * Gets the value for this instance.
   *
   * @return The value.
   */
  public double getValue() {
    return this.value;
  }

  public int compareTo(Object o) {
    if (o instanceof RealG0) {
      double val2 = ((RealG0) o).getValue();
      if (value == val2) {
        return 0;
      }
      if (value < val2) {
        return -1;
      }
      if (value > val2) {
        return 1;
      }
    }
    if (o instanceof RealZeroOne) {
      double val2 = ((RealZeroOne) o).getValue();
      if (value == val2) {
        return 0;
      }
      if (value < val2) {
        return -1;
      }
      if (value > val2) {
        return 1;
      }
    }
    throw new ClassCastException("Unable to compare RealG0 class with " + o);
  }

  private boolean checkValue() {
    if (value < 0) {
      value = 0;
      return false;
    }
    return true;
  }

  static final long serialVersionUID = 122132278985141212L;
  private double    value;
}
