package lu.uni.adtool.domains.rings;

public class Int implements Ring {

  public Int(final int value) {
    this.value = value;
  }

  public Object clone() {
    return new Int(value);
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  public final String toString() {
    return new Integer(getValue()).toString();
  }

  public final String toUnicode() {
    return new Integer(getValue()).toString();
  }

  public final int getValue() {
    return this.value;
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
}
