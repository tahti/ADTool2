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
 * Set of real numbers in between 0 and 1 (including 0 and 1).
 *
 * @author Piotr Kordy
 */
public class RealZeroOne implements Ring {

  /**
   * Constructs a new instance.
   */
  public RealZeroOne() {
    this(0);
  }

  /**
   * Constructs a newly allocated object that represents the primitive double
   * argument.
   *
   * @param d
   *          the value to be represented by the RealZeroOne.
   */
  public RealZeroOne(final double d) {
    value = d;
    checkValue();
  }

  public Object clone() {
    return new RealZeroOne(value);
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
    return new Double(value).toString();
  }

  /**
   * {@inheritDoc}
   *
   * @see Ring#toUnicode()
   */
  public final String toUnicode() {
    return this.toString();
  }

  /**
   * Return a sum of probabilities.
   *
   * @param a
   *          the first probability
   * @param b
   *          the second probability
   * @return the sum of probabilities
   */
  public static final RealZeroOne plusProb(final RealZeroOne a, final RealZeroOne b) {
    final double x = a.getValue();
    final double y = b.getValue();
    return new RealZeroOne(x + y - x * y);
  }

  /**
   * Return a difference of probabilities.
   *
   * @param a
   *          the first probability
   * @param b
   *          the second probability
   * @return the difference of probabilities
   */
  public static final RealZeroOne minusProb(final RealZeroOne a, final RealZeroOne b) {
    final double x = a.getValue();
    final double y = b.getValue();
    return new RealZeroOne(x * (1 - y));
  }

  /**
   * Return a multiplitation of probabilities.
   *
   * @param a
   *          the first probability
   * @param b
   *          the second probability
   * @return the result.
   */
  public static final RealZeroOne times(final RealZeroOne a, final RealZeroOne b) {
    final double x = a.getValue();
    final double y = b.getValue();
    return new RealZeroOne(x * y);
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
    throw new ClassCastException("Unable to compare RealZeroOne class with " + o);
  }

  private boolean checkValue() {
    if (value < 0) {
      value = 0;
      return false;
    }
    if (value > 1) {
      value = 1;
      return false;
    }
    return true;
  }

  static final long serialVersionUID = 145654787766564466L;
  private double    value;

}
