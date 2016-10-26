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
package lu.uni.adtool.domains.sandpredefined;

import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.adtpredefined.DescriptionGenerator;
import lu.uni.adtool.domains.rings.RealG0;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

public class MinTime extends SandRankingDomain<RealG0>{

  public MinTime() {
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getDefaultValue()
   */
  public final RealG0 getDefaultValue(Node node) {
    return new RealG0(Double.POSITIVE_INFINITY);
  }

  public boolean isOrType(SandNode.Type operation) {
    return operation == SandNode.Type.OR;
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getName()
   */
  public String getName() {
    return Options.getMsg("sanddomain.mintime.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getDescription()
   */
  public String getDescription() {
    final String name = Options.getMsg("sanddomain.mintime.description");
    final String vd = "&#x211D;\u208A\u222A{\u221E}";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "<i>x</i>+<i>y</i>"};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }
  public RealG0 calc(RealG0 a, RealG0 b, SandNode.Type type) {
    switch (type) {
    case AND:
      return RealG0.max(a, b);
    case SAND:
      return RealG0.sum(a, b);
    case OR:
      return RealG0.min(a, b);
    default:
      return RealG0.min(a, b);
    }
  }


  private static final long serialVersionUID = -3603484192500901319L;

}
