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
import lu.uni.adtool.tools.Options;

/**
 * A AtdDomain defined on booleans.
 *
 * @author Piotr Kordy
 */
public class MinCost extends MinTimeSeq {
  // number 6

  /**
   * Constructs a new instance.
   */
  public MinCost() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getName()
   */
  public final String getName() {
    return Options.getMsg("adtdomain.mincost.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see AdtDomain#getDescription()
   */
  public final String getDescription() {
    final String name = Options.getMsg("adtdomain.mincost.description");
    final String vd = "&#x211D;\u208A\u222A{\u221E}";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "<i>x</i>&nbsp;+&nbsp;<i>y</i>", "<i>x</i>&nbsp;+&nbsp;<i>y</i>",
            "min(<i>x</i>,<i>y</i>)", "<i>x</i>&nbsp;+&nbsp;<i>y</i>", "min(<i>x</i>,<i>y</i>)",};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }
  static final long serialVersionUID = 665945232556446846L;
}
