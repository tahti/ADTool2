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

import java.io.Serializable;

/**
 * An interface for domains. Obsolete - used just to import adt file.
 *
 * @author Piotr Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface Domain<Type> extends Serializable
{
  /**
   * Gets the default value with which to initialise the node.
   *
   * @param proponent true if the default value is for proponent
   * @return a default value.
   */
  Type getDefaultValue(boolean proponent);
  /**
   * Checks if the value is allowed to be modified by the user.
   *
   * @param proponent if true we check the values for proponent and for
   * opponent otherwise.
   * @return true if user can modify the value and false otherwise.
   */
  boolean isValueModifiable(boolean proponent);

  /**
   * Returns a name of a domain.
   *
   * @return a string containing a name.
   */
  String getName();

  /**
   * Returns a descripiton of a domain.
   *
   * @return a description.
   */
  String getDescription();

  /**
   * Disjunctive refinement of the proponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of disjunctive refinement.
   */
  Type op(Type a, Type b);

  /**
   * Disjunctive refinement of the oppponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of disjunctive refinement.
   */
  Type oo(Type a, Type b);

  /**
   * Conjunctive refinement of the proponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of conjunctive refinement.
   */
  Type ap(Type a, Type b);

  /**
   * Conjunctive refinement of the oppponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of conjunctive refinement.
   */
  Type ao(Type a, Type b);

  /**
   * Counter of the proponent type of node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of a counter.
   */
  Type cp(Type a, Type b);

  /**
   * Counter of the opponent type of node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of a counter.
   */
  Type co(Type a, Type b);
}
