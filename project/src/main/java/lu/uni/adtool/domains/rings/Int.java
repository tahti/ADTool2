package lu.uni.adtool.domains.rings;

import lu.uni.adtool.tools.Options;

public class Int implements Ring {

  public Int(final int value) {
    this.value = value;
    this.nan = false;
  }

  public Int(final int value, final boolean nan) {
    this.value = value;
    this.nan = nan;
  }

  public Object clone() {
    return new Int(this.value, this.nan);
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  public final String toString() {
    if (!this.nan) {
      return new Integer(getValue()).toString();
    }
    else {
      return Options.getMsg("domain.nan");
    }
  }

  public final String toUnicode() {
    if (!this.nan) {
      return new Integer(getValue()).toString();
    }
    else {
      return Options.getMsg("domain.nan");
    }
  }

  public final int getValue() {
    if (this.nan) {
      throw new ArithmeticException("Not a number");
    }
    return this.value;
  }

  public boolean isValid() {
    return !this.nan;
  }

  public int compareTo(Object o) {
    if (o instanceof Int) {
      int val2 = ((Int) o).getValue();
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
    throw new ClassCastException("Unable to compare Int class with " + o);
  }

  /**
   * Set the new value reading from the string Returns false if the value is not
   * valid.
   *
   */
  public final boolean updateFromString(String s) {
    int origValue = this.value;
    if (s.equals(Options.getMsg("domain.nan"))) {
      this.nan = true;
      return true;
    }
    try {
      this.value = Integer.parseInt(s);
    }
    catch (NumberFormatException e) {
      this.value = origValue;
      return false;
    }
    return true;
  }

  private int value;
  private boolean nan;
}
