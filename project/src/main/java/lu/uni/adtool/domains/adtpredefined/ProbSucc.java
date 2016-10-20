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
import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;

/**
 * A AtdDomain defined on booleans.
 *
 * @author Piotr Kordy
 */
public class ProbSucc implements AdtDomain<RealZeroOne> {

  /**
   * Constructs a new instance.
   */
  public ProbSucc() {
  }
  public RealZeroOne calc(RealZeroOne a, RealZeroOne b, ADTNode.Type type) {
    switch (type) {
    case OR_OPP:
      return RealZeroOne.plusProb(a, b);
    case AND_OPP:
      return RealZeroOne.times(a, b);
    case OR_PRO:
      return RealZeroOne.plusProb(a, b);
    case AND_PRO:
      return RealZeroOne.times(a, b);
    default:
      return RealZeroOne.times(a, b);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDefaultValue()
   */
  public final RealZeroOne getDefaultValue(Node node) {
    if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
      return new RealZeroOne(0);
    }
    else {
      return new RealZeroOne(0);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#isValueModifiable(boolean)
   */
  public final boolean isValueModifiable(boolean isProponent) {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getName()
   */
  public final String getName() {
    return Options.getMsg("adtdomain.probsucc.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.probsucc.description");
    final String vd = "[0,1]";
    final String[] operators = {"<i>x</i>&nbsp;+&nbsp;<i>y</i>&nbsp;-&nbsp;<i>x</i><i>y</i>",
        "<i>x</i><i>y</i>", "<i>x</i>&nbsp;+&nbsp;<i>y</i>&nbsp;-&nbsp;<i>x</i><i>y</i>",
        "<i>x</i><i>y</i>", "<i>x</i>(1&nbsp;-&nbsp;<i>y</i>)", "<i>x</i>(1&nbsp;-&nbsp;<i>y</i>)"};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }


  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#cp(RealZeroOne,RealZeroOne)
   */
  public final RealZeroOne cp(final RealZeroOne a, final RealZeroOne b) {
    return RealZeroOne.minusProb(a, b);
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#co(RealZeroOne,RealZeroOne)
   */
  public final RealZeroOne co(final RealZeroOne a, final RealZeroOne b) {
    return RealZeroOne.minusProb(a, b);
  }
  static final long serialVersionUID = 865945232556446848L;
}
