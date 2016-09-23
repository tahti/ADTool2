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
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

import java.util.HashMap;

/**
 * Evaluate value of each node for given domain and value assignement map. Works
 * with both SandDomain and AtdDomain
 *
 * @author Piotr Kordy
 */
public class Evaluator<Type extends Ring> {
  /**
   * Constructs a new instance.
   *
   */
  public Evaluator(AdtDomain<Type> domain) {
    resultMap = null;
    this.atdDomain = domain;
    this.sandDomain = null;
  }

  public Evaluator(SandDomain<Type> domain) {
    resultMap = null;
    this.atdDomain = null;
    this.sandDomain = domain;
  }

  /**
   * Returns previously calculated value for a given node.
   *
   * @param node
   *          a node
   * @return value at a node
   */
  public final Type getValue(final Node node) {
    if (resultMap == null || node == null) {
      return null;
    }
    else {
      return resultMap.get(node);
    }
  }

  /**
   * Do bottom up evaluation.
   *
   * @param root
   *          node from which we do evaluation
   * @param newmap
   *          mapping between node names and values.
   * @return true if evaluation was successful.
   */
  public final Type reevaluate(final ADTNode root, final ValueAssignement<Type> valuesMap) {
    if (valuesMap == null) {
      System.err.println("NULL result");
      return null;
    }
    resultMap = new HashMap<Node, Type>();
    return this.evaluate(root, valuesMap);
  }

  public final Type reevaluate(final SandNode root, final ValueAssignement<Type> valueAssigment) {
    if (valueAssigment == null || root == null) {
      System.err.println("NULL result");
      return null;
    }
    resultMap = new HashMap<Node, Type>();
    return evaluate(root, valueAssigment);
  }

  /**
   * Do bottom up evaluation.
   *
   * @param root
   * @return
   */
  private Type evaluate(final ADTNode root, ValueAssignement<Type> valuesMap) {
    Type result = null;
    int c = 0;
    // if last element is counter - skip it
    if (root.isCountered()) {
      c = 1;
    }
    if (root.hasDefault()) {
      result = valuesMap.get(root.getRole() == ADTNode.Role.PROPONENT, root.getName());
      if (result == null) {
        result = atdDomain.getDefaultValue(root);
      }
    }
    else {
      for (int i = 0; i < (root.getChildren().size() - c); i++) {
        if (result == null) {
          result = evaluate((ADTNode) root.getChildren().get(i), valuesMap);
        }
        else {
          result = atdDomain.calc(result, evaluate((ADTNode) root.getChildren().get(i), valuesMap), root.getType());
        }
      }
    }
    if (root.isCountered()) {
      if (root.getRole() == ADTNode.Role.OPPONENT) {
        result = atdDomain.co(result, evaluate(
            (ADTNode) root.getChildren().get(root.getChildren().size() - 1), valuesMap));
      }
      else {
        result = atdDomain.cp(result, evaluate(
            (ADTNode) root.getChildren().get(root.getChildren().size() - 1), valuesMap));
      }
    }
    resultMap.put(root, result);
    return result;
  }

  private Type evaluate(final SandNode root, ValueAssignement<Type> map) {
    Type result = null;
    if (root.isLeaf()) {
      result = map.get(true, root.getName());
      if (result == null) {
        result = sandDomain.getDefaultValue(root);
      }
    }
    else {
      for (Node child : root.getChildren()) {
        if (result == null) {
          result = evaluate((SandNode) child, map);
        }
        else {
          result = sandDomain.calc(result,  evaluate((SandNode) child, map), root.getType());
        }
      }
    }
    resultMap.put(root, result);
    return result;
  }

  private HashMap<Node, Type> resultMap;
  private AdtDomain<Type>     atdDomain;
  private SandDomain<Type>    sandDomain;
}
