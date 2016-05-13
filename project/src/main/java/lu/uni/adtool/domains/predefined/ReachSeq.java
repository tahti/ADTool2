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
package lu.uni.adtool.domains.predefined;

import lu.uni.adtool.domains.rings.BoundedInteger;

/**
 * A domain to calculate minimal skill level needed for the proponent. -
 * Obsolete
 *
 * @author Piotr Kordy
 */
public class ReachSeq implements Domain<BoundedInteger>, Parametrized {
  /**
   * Constructs a new instance.
   */
  public ReachSeq() {
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
    return "Reachability of the proponent's goal in less than k units" + " (sequential)";
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
    return BoundedInteger.sum(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#oo(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger oo(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.sum(a, b);
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
    return BoundedInteger.sum(a, b);
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
