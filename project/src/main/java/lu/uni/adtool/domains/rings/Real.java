package lu.uni.adtool.domains.rings;

import lu.uni.adtool.tools.Options;

public class Real implements Ring {

  public Real(final double value) {
    this.value = value;
  }

  public Object clone() {
    return new Real(value);
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  public final String toString() {
    if (getValue()== Double.NaN) {
      return Options.getMsg("domain.nan");
    }
    return new Double (getValue()).toString();
  }

  public final String toUnicode() {
    if (getValue()== Double.NaN) {
      return Options.getMsg("domain.nan");
    }
    return new Double(getValue()).toString();
  }

  public final double getValue() {
    return this.value;
  }

  public int compareTo(Object o) {
    if (o instanceof Real) {
      double val2 = ((Real) o).getValue();
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
    throw new ClassCastException("Unable to compare Real class with " + o);
  }

  /**
   * Set the new value reading from the string Returns false if the value is not
   * valid.
   *
   */
  public final boolean updateFromString(String s) {
    double origValue = this.value;
    //Accept NaN?
//     if (s.equals(Options.getMsg("domain.nan"))) {
//       return false;
//     }
    if (s.toLowerCase().equals("pi")) {
      this.value = Math.PI;
    }
    else if (s.toLowerCase().equals("e")) {
      this.value = Math.E;
    }
    else if (s.equals(Double.toString((Double.NEGATIVE_INFINITY)))) {
      this.value = Double.NEGATIVE_INFINITY;
    }
    else if (s.equals(Double.toString((Double.POSITIVE_INFINITY)))) {
      this.value = Double.POSITIVE_INFINITY;
    }
    else {
      try {
        this.value = Double .parseDouble(s);
      }
      catch (NumberFormatException e) {
        this.value = origValue;
        return false;
      }
    }
    return true;
  }

  private double value;
}
