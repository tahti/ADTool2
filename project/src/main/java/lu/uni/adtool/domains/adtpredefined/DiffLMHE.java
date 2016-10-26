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
import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A domain to calculate minimal skill level needed for the proponent.
 *
 * @author Piotr Kordy
 */
public class DiffLMHE extends AdtRankingDomain<LMHEValue> {

  /**
   * Constructs a new instance.
   */
  public DiffLMHE() {
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

  public LMHEValue calc(LMHEValue a, LMHEValue b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return LMHEValue.max(a, b);
    case AND_OPP:
      return LMHEValue.min(a, b);
    case OR_PRO:
      return LMHEValue.min(a, b);
    case AND_PRO:
      return LMHEValue.max(a, b);
    default:
      return LMHEValue.max(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(LMHEValue,LMHEValue)
   */
  public final LMHEValue cp(final LMHEValue a, final LMHEValue b) {
    return LMHEValue.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(LMHEValue,LMHEValue)
   */
  public final LMHEValue co(final LMHEValue a, final LMHEValue b) {
    return LMHEValue.min(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final LMHEValue getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new LMHEValue(4);
    }
    else {
      return new LMHEValue(LMHEValue.INF);
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
    return Options.getMsg("adtdomain.difflmhe.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.difflmhe.description");
    final String vd = "<nobr>{L,M,H,E,\u221E}</nobr>";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)",
            "min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }

  static final long serialVersionUID = 14682694465266848L;
}
