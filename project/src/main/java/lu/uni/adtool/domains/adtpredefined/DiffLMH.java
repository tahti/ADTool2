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
import lu.uni.adtool.domains.rings.LMHValue;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A domain to calculate minimal skill level needed for the proponent.
 *
 * @author Piotr Kordy
 */
public class DiffLMH  extends AdtRankingDomain<LMHValue> {
  /**
   * Constructs a new instance.
   */
  public DiffLMH() {
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

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final LMHValue getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new LMHValue(3);
    }
    else {
      return new LMHValue(LMHValue.INF);
    }
  }

  public LMHValue calc(LMHValue a, LMHValue b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return LMHValue.max(a, b);
    case AND_OPP:
      return LMHValue.min(a, b);
    case OR_PRO:
      return LMHValue.min(a, b);
    case AND_PRO:
      return LMHValue.max(a, b);
    default:
      return LMHValue.max(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(LMHValue,LMHValue)
   */
  public final LMHValue cp(final LMHValue a, final LMHValue b) {
    return LMHValue.max(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(LMHValue,LMHValue)
   */
  public final LMHValue co(final LMHValue a, final LMHValue b) {
    return LMHValue.min(a, b);
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
    return Options.getMsg("adtdomain.difflmh.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.difflmh.description");

    final String vd = "<nobr>{L,M,H,\u221E}</nobr>";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)",
            "min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }


  static final long serialVersionUID = 14645614725266844L;
}
