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
package lu.uni.adtool.adtree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

// import lu.uni.adtool.adtree.ADTNode;
// import lu.uni.adtool.adtree.Node;

/**
 * Assignment map that associates labels of the nodes with domain valuations.
 *
 * @author Piotr Kordy
 * @version
 */
public class ValueAssignement<T> implements Serializable {

  /**
   * {@inheritDoc}
   *
   * @see Object#ValueAssignement()
   */
  public ValueAssignement() {
    map = new HashMap<String, T>(32);
  }

  public String toString() {
    return map.values().toString();
  }

  /**
   * Assigns value to a label.
   *
   * @param value
   * @param label
   */
  public void put(final String label, final T value) {
    map.put(label.replaceAll("\\s+", " "), value);
  }

  /**
   * Remove the label from the map.
   *
   * @param label
   *          label to be removed.
   */
  public final void remove(final String label) {
    map.remove(label.replaceAll("\\s+", " "));
  }

  /**
   * Get the value for the given key.
   *
   * @param label
   *          label of the key
   * @return key
   */
  public final T get(final String label) {
    return map.get(label.replaceAll("\\s+", " "));
  }

  // /**
  // * Sets the default value for labels that do not have value set.
  // *
  // * @param proponent when true we create value map for proponent and when
  // * false for opponent type of nodes.
  // * @param root root of the tree.
  // * @param domain domain
  // */
  // public final void setDefault(final boolean proponent, final ADTNode root,
  // final Domain<T> domain)
  // {
  // final ADTNode.Type type = root.getType();
  // if (type == ADTNode.Type.LEAFO && !proponent){
  // if (get(root.getName()) == null) {
  // put(root.getName(),
  // // domain.getDefaultValue(type == ADTNode.Type.LEAFP));
  // domain.getDefaultValue(proponent));
  // }
  // }
  // else if(type == ADTNode.Type.LEAFP && proponent) {
  // if (get(root.getName()) == null) {
  // put(root.getName(),
  // // domain.getDefaultValue(type == ADTNode.Type.LEAFO));
  // domain.getDefaultValue(proponent));
  // }
  // }
  // else {
  // if (root.getChildren() != null) {
  // for(Node c:root.getChildren()){
  // if (c != null) {
  // setDefault(proponent,(ADTNode)c, domain);
  // }
  // }
  // }
  // }
  // }
  public Set<String> keySet() {
    return map.keySet();
  }

  static final long          serialVersionUID = 975147854358646403L;
  /**
   * s Hash map for internal storage.
   */
  private HashMap<String, T> map;
}
