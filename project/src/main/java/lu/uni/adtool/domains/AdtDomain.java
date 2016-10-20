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
package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.ADTNode;

import java.io.Serializable;

/**
 * An interface for domains.
 *
 * @author Piotr Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface AdtDomain<Type extends Ring> extends Domain<Type>, Serializable {
  /**
   * Checks if the value is allowed to be modified by the user.
   *
   * @param proponent
   *          if true we check the values for proponent and for opponent
   *          otherwise.
   * @return true if user can modify the value and false otherwise.
   */
  public boolean isValueModifiable(boolean isProponent);
  public Type calc(Type a, Type b, ADTNode.Type type);
  /**
   * Counter of the proponent type of node.
   *
   * @param a
   *          left argument
   * @param b
   *          right argument
   * @return result of a counter.
   */
  public Type cp(Type a, Type b);

  /**
   * Counter of the opponent type of node.
   *
   * @param a
   *          left argument
   * @param b
   *          right argument
   * @return result of a counter.
   */
  public Type co(Type a, Type b);

}
