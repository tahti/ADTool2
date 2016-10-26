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
package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Parametrized;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A domain to calculate minimal skill level needed for the proponent.
 *
 * @author Piotr Kordy
 */
public class MinSkill extends AdtRankingDomain<BoundedInteger> implements Parametrized {

  /**
   * Constructs a new instance.
   */
  public MinSkill() {
    bound = Integer.MAX_VALUE;
  }

  public boolean isOrType(ADTNode.Type operation) {
    switch (operation) {
    case AND_OPP:
    case OR_PRO:
      return true;
    case OR_OPP:
    case AND_PRO:
    default:
      return false;
    }
  }

  public BoundedInteger calc(BoundedInteger a, BoundedInteger b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return BoundedInteger.max(a, b);
    case AND_OPP:
      return BoundedInteger.min(a, b);
    case OR_PRO:
      return BoundedInteger.min(a, b);
    case AND_PRO:
      return BoundedInteger.max(a, b);
    default:
      return BoundedInteger.max(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final BoundedInteger getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
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
   * @see AdtDomain#isValueModifiable(boolean)
   */
  public final boolean isValueModifiable(boolean isProponent) {
    return isProponent;
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getName()
   */
  public String getName() {
    return Options.getMsg("adtdomain.minskill.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.minskill.description");

    final String vd = "<nobr>{0,\u2026,k}\u222A{\u221E}</nobr>";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)",
            "min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(BoundedInteger,BoundedInteger)
   */
  public final BoundedInteger cp(final BoundedInteger a, final BoundedInteger b) {
    return BoundedInteger.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(BoundedInteger,BoundedInteger)
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
