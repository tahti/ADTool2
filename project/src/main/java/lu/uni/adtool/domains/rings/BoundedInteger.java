/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>)
 * Date:   10/12/2015
 * Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty of Science,
 *     Technology and Communication FSTC
 * All rights reserved.
 * Licensed under GNU Affero General Public License 3.0;
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.domains.rings;

/**
 * Class representing bounded integers.
 *
 * @author Piotr Kordy
 */
public class BoundedInteger implements Ring {

  /**
   * Constructs a new instance.
   *
   * @param value
   *          new value
   * @param bound
   *          upper bound on integers.
   */
  public BoundedInteger(final int value, final int bound) {
    this.value = value;
    this.bound = bound;
    checkValue();
  }

  public Object clone() {
    return new BoundedInteger(value, bound);
  }

  /**
   * Gets the value for this instance.
   *
   * @return The value.
   */
  public final int getValue() {
    return this.value;
  }

  /**
   * Gets the upper bound for this instance.
   *
   * @return The bound.
   */
  public final int getBound() {
    return this.bound;
  }

  /**
   * Sum with upper bound of two integers.
   *
   * @param a
   *          first bounded integer.
   * @param b
   *          second bounded integer.
   * @return sum or upper bound if sum is larger than the bound.
   */
  public static BoundedInteger sum(final BoundedInteger a, final BoundedInteger b) {
    if (a.getValue() == INF || b.getValue() == INF) {
      return new BoundedInteger(INF, Math.min(a.getBound(), b.getBound()));
    }
    return new BoundedInteger(a.getValue() + b.getValue(), Math.min(a.getBound(), b.getBound()));
  }

  /**
   * Max of two integers.
   *
   * @param a
   *          first bounded integer.
   * @param b
   *          second bounded integer.
   * @return bigger of the two integers.
   */
  public static BoundedInteger max(final BoundedInteger a, final BoundedInteger b) {
    if (a.getValue() == INF || b.getValue() == INF) {
      return new BoundedInteger(INF, Math.min(a.getBound(), b.getBound()));
    }
    return new BoundedInteger(Math.max(a.getValue(), b.getValue()),
        Math.min(a.getBound(), b.getBound()));
  }

  /**
   * Min of two integers.
   *
   * @param a
   *          first bounded integer.
   * @param b
   *          second bounded integer.
   * @return smaller of the two integers.
   */
  public static BoundedInteger min(final BoundedInteger a, final BoundedInteger b) {
    if (a.getValue() == INF) {
      return new BoundedInteger(b.getValue(), Math.min(a.getBound(), b.getBound()));
    }
    if (b.getValue() == INF) {
      return new BoundedInteger(a.getValue(), Math.min(a.getBound(), b.getBound()));
    }
    return new BoundedInteger(Math.min(a.getValue(), b.getValue()),
        Math.min(a.getBound(), b.getBound()));
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  public final String toString() {
    if (getValue() == -1) {
      return "Infinity";
    }
    else {
      return new Integer(getValue()).toString();
    }
  }

  /**
   * Unicode representation of a number.
   *
   * @return string with unicode representation.
   */
  public final String toUnicode() {
    if (getValue() == -1) {
      return "\u221E";
    }
    else {
      return new Integer(getValue()).toString();
    }
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
    if (!checkValue()) {
      this.value = origValue;
      return false;
    }
    return true;
  }

  /**
   * Checks if the value is bigger than zero and lower than bound
   */
  private boolean checkValue() {
    if (value < -1) {
      value = 0;
      return false;
    }
    if (value > bound) {
      value = bound;
      return false;
    }
    return true;

  }

  public int compareTo(Object o) {
    if (o instanceof BoundedInteger) {
      int val2 = ((BoundedInteger) o).getValue();
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
    throw new ClassCastException("Unable to compare BoundedInteger class with " + o);
  }

  static final long serialVersionUID = 94244625469424462L;
  public static int INF              = -1;
  private int       value;
  private int       bound;
}
