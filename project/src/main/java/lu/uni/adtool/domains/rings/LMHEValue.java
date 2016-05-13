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

import java.io.Serializable;

/**
 * Class representing values in the domain {L,M,H, Infinity} L=Low M=Medium
 * H=High.
 *
 * @author Piotr Kordy
 */
public class LMHEValue implements Serializable, Ring {

  /**
   * Constructs a new instance.
   *
   * @param k
   *          new value
   */
  public LMHEValue(final int k) {
    value = k;
    if (value < 1) {
      value = 1;
    }
    if (value > 5) {
      value = 5;
    }
  }

  public Object clone() {
    return new LMHEValue(value);
  }

  /**
   * Constructs a new instance.
   *
   * @param k
   *          new value as a string
   */
  public LMHEValue(final String s) {
    value = translate(s);
  }

  /**
   * Set the new value reading from the string
   *
   */
  public final boolean updateFromString(String s) {
    int newValue = translate(s);
    if (newValue == -1) {
      return false;
    }
    this.value = newValue;
    return true;
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
   * Sum with upper bound of two integers.
   *
   * @param a
   *          first bounded integer.
   * @param b
   *          second bounded integer.
   * @return sum or upper bound if sum is larger than the bound.
   */
  public static LMHEValue sum(final LMHEValue a, final LMHEValue b) {
    if (a.getValue() == INF || b.getValue() == INF) {
      return new LMHEValue(INF);
    }
    return new LMHEValue(Math.min(a.getValue() + b.getValue(), 4));
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
  public static LMHEValue max(final LMHEValue a, final LMHEValue b) {
    return new LMHEValue(Math.max(a.getValue(), b.getValue()));
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
  public static LMHEValue min(final LMHEValue a, final LMHEValue b) {
    return new LMHEValue(Math.min(a.getValue(), b.getValue()));
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  public final String toString() {
    if (getValue() == INF) {
      return "Infinity";
    }
    else {
      if (getValue() == 1) {
        return "L";
      }
      else {
        if (getValue() == 2) {
          return "M";
        }
        else {
          if (getValue() == 3) {
            return "H";
          }
          else {
            if (getValue() == 4) {
              return "E";
            }
          }
        }
      }
    }
    return "Unknown";
  }

  /**
   * Unicode representation of a number.
   *
   * @return string with unicode representation.
   */
  public final String toUnicode() {
    if (getValue() == INF) {
      return "\u221E";
    }
    else {
      if (getValue() == 1) {
        return "L";
      }
      else {
        if (getValue() == 2) {
          return "M";
        }
        else {
          if (getValue() == 3) {
            return "H";
          }
          else {
            if (getValue() == 4) {
              return "E";
            }
          }
        }
      }
    }
    return "Unknown";
  }

  public int compareTo(Object o) {
    if (o instanceof LMHEValue) {
      int val2 = ((LMHEValue) o).getValue();
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
    throw new ClassCastException("Unable to compare LMHEValue class with " + o);
  }

  private int translate(String s) {
    if (s.equals("L")) {
      return 1;
    }
    else {
      if (s.equals("M")) {
        return 2;
      }
      else {
        if (s.equals("H")) {
          return 3;
        }
        else {
          if (s.equals("E")) {
            return 4;
          }
          else {
            if (s.equals("\u221E") || s.equals("Infinity")) {
              return INF;
            }
          }
        }
      }
    }
    return -1;
  }

  static final long serialVersionUID = 9446634255734462L;
  public static int INF              = 5;
  private int       value;
}
