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

import java.util.ArrayList;

import lu.uni.adtool.domains.adtpredefined.AdtRankingDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.domains.sandpredefined.SandRankingDomain;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;

public class Ranker<Type extends Ring> {
  public Ranker(AdtDomain<Type> domain) {
    this.atdDomain = domain;
    this.sandDomain = null;
  }

  public Ranker(SandDomain<Type> domain) {
    this.atdDomain = null;
    this.sandDomain = domain;
  }

  /**
   * Do bottom up evaluation.
   *
   * @param root
   *          node from which we do evaluation
   * @param valuesMap
   *          mapping between node names and values.
   * @param maxItems
   *          maximum number of results when doing ranking
   * @return Array of best maxItems attacks.
   */
  public final ArrayList<RankNode<Type>> rank(final ADTNode root,
      final ValueAssignement<Type> valuesMap, int maxItems) {
    this.lastNode = root;
    if (valuesMap == null || root == null || this.atdDomain == null
        || !(this.atdDomain instanceof AdtRankingDomain)) {
      Debug.log("NULL result");
      return null;
    }
    this.lastResult = rankRecursive(root, valuesMap, maxItems);
    return this.lastResult;

    // return rankRecursive(root, newProMap, newOppMap, maxItems);
  }

  /**
   * Do bottom up evaluation.
   *
   * @param root
   *          node from which we do evaluation
   * @param valuesMap
   *          mapping between node names and values.
   * @param maxItems
   *          maximum number of results when doing ranking
   * @return Array of best maxItems attacks.
   */
  public final ArrayList<RankNode<Type>> rank(final SandNode root,
      final ValueAssignement<Type> valueAssigment, int maxItems) {
    this.lastNode = root;
    if (valueAssigment == null || root == null || sandDomain == null ||
        !(this.sandDomain instanceof SandRankingDomain<?>)) {
      Debug.log("NULL result");
      return null;
    }
    this.lastResult = rankRecursive(root, valueAssigment, maxItems);
    return this.lastResult;
  }

  private ArrayList<RankNode<Type>> rankRecursive(final ADTNode root,
      ValueAssignement<Type> valuesMap, int maxItems) {
    ArrayList<RankNode<Type>> result = new ArrayList<RankNode<Type>>();
    int c = 0;
    if (root.isCountered()) {
      c = 1;
    }
    if (root.hasDefault()) {
      Type value;
      value = valuesMap.get(root.getRole() == ADTNode.Role.PROPONENT, root.getName());
      if (value == null) {
        value = atdDomain.getDefaultValue(root);
        valuesMap.put(root.getRole() == ADTNode.Role.PROPONENT, root.getName(), value);
      }
      result.add(new RankNode<Type>(value));
    }
    else {
      ArrayList<ArrayList<RankNode<Type>>> list = new ArrayList<ArrayList<RankNode<Type>>>();
      list.ensureCapacity(root.getChildren().size() - c);
      for (int i = 0; i < (root.getChildren().size() - c); i++) {
        list.add(rankRecursive((ADTNode) root.getChildren().get(i), valuesMap, maxItems));
      }
      if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(root.getType())) {
        result = ((AdtRankingDomain<Type>) this.atdDomain).minOp(list, maxItems, root.getType());
      }
      else {
        result =
            ((AdtRankingDomain<Type>) this.atdDomain).conjunctiveOp(list, maxItems, root.getType());
      }
    }
    if (c == 1) {
      ArrayList<ArrayList<RankNode<Type>>> list = new ArrayList<ArrayList<RankNode<Type>>>();
      list.ensureCapacity(2);
      list.add(result);
      list.add(rankRecursive((ADTNode) root.getChildren().get(root.getChildren().size() - 1),
          valuesMap, maxItems));
      if (root.getRole() == ADTNode.Role.OPPONENT) {
        // assuming AND_OPP == CO
        if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_OPP)) {
          result =
              ((AdtRankingDomain<Type>) this.atdDomain).minOp(list, maxItems, ADTNode.Type.AND_OPP);
        }
        else {
          result = ((AdtRankingDomain<Type>) this.atdDomain).conjunctiveOp(list, maxItems,
              ADTNode.Type.AND_OPP);
        }
      }
      else {
        // assuming AND_OPP == CP
        if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_PRO)) {
          result =
              ((AdtRankingDomain<Type>) this.atdDomain).minOp(list, maxItems, ADTNode.Type.AND_PRO);
        }
        else {
          result = ((AdtRankingDomain<Type>) this.atdDomain).conjunctiveOp(list, maxItems,
              ADTNode.Type.AND_PRO);
        }
      }
    }
    return result;
  }

  private ArrayList<RankNode<Type>> rankRecursive(final SandNode root, ValueAssignement<Type> map,
      int maxItems) {
    ArrayList<RankNode<Type>> result = new ArrayList<RankNode<Type>>();
    if (root.isLeaf()) {
      Type value = map.get(true, root.getName());
      if (value == null) {
        value = sandDomain.getDefaultValue(root);
        map.put(true, root.getName(), value);
      }
      result.add(new RankNode<Type>(value));
    }
    else {
      ArrayList<ArrayList<RankNode<Type>>> list = new ArrayList<ArrayList<RankNode<Type>>>();
      list.ensureCapacity(root.getChildren().size());
      for (int i = 0; i < (root.getChildren().size()); i++) {
        list.add(rankRecursive((SandNode) root.getChildren().get(i), map, maxItems));
      }
      if (((SandRankingDomain<Type>) this.sandDomain).isOrType(root.getType())) {
        result = ((SandRankingDomain<Type>) this.sandDomain).minOp(list, maxItems, root.getType());
      }
      else {
        result =
            ((SandRankingDomain<Type>) this.sandDomain).conjunctiveOp(list, maxItems, root.getType());
      }
    }
    return result;
  }
@SuppressWarnings("unchecked")
  public void mark(int index, AbstractDomainCanvas<Type> canvas) {
    if (lastNode == null || lastResult == null) {
      return;
    }
    // TODO change interface a bit
    ArrayList<Integer> choices = new ArrayList<Integer>(lastResult.get(index).getList());
    Debug.log("mark: choices size:" + choices.size());
    markRecursive(lastNode, canvas, (ValueAssignement<Type>)canvas.getValues().getValueMap(), choices);
    canvas.markNode(lastNode, Options.canv_rankRootMark);
  }

  public Type markRecursive(Node node, NodeRanker consumer, ValueAssignement<Type> map,
      ArrayList<Integer> orChoices) {
    Type value = null;
    if (node.getChildren() != null && node.getChildren().size() > 0) {
      if (node instanceof SandNode) {
        if (((SandRankingDomain<Type>) this.sandDomain).isOrType(((SandNode)node).getType())) {
          int index = orChoices.get(orChoices.size() - 1);
          orChoices.remove(orChoices.size() - 1);
          value = markRecursive(node.getChildren().get(index), consumer, map, orChoices);
        }
        else {
          for (int i = 1; i < node.getChildren().size(); i++) {
            value = this.sandDomain.calc(value
                                         , markRecursive(node.getChildren().get(i) , consumer, map , orChoices)
                                         , ((SandNode) node).getType());
//             value = this.sandDomain.sand(value,
//                 markRecursive(node.getChildren().get(i), consumer, map, orChoices));
          }
        }
      }
      else {
        ADTNode n = (ADTNode) node;
        if (n.isCountered()) {
          boolean doOr = false;
          if (n.getRole() == ADTNode.Role.PROPONENT) {
            if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_PRO)) {
              doOr = true;
            }
          }
          else {
            if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_OPP)) {
              doOr = true;
            }
          }
          if (doOr) {
            int ind = orChoices.get(orChoices.size() - 1);
            orChoices.remove(orChoices.size() - 1);
            if (ind == 0) {
              if (node.hasDefault()) {
                value = this.atdDomain.getDefaultValue(node);
              }
              else {
                if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(n.getType())) {
                  ind = orChoices.get(orChoices.size() - 1);
                  orChoices.remove(orChoices.size() - 1);
                  value = markRecursive(node.getChildren().get(ind), consumer, map, orChoices);
                }
                else {
                  value = markRecursive(node.getChildren().get(0), consumer, map, orChoices);
                  for (int i = 1; i < node.getChildren().size() - 1; i++) {
                    value = this.atdDomain.calc(value,
                        markRecursive(node.getChildren().get(i), consumer, map, orChoices),
                        ((ADTNode) node).getType());
                  }
                }
              }
            }
            else {
              // counter
              value = markRecursive(node.getChildren().get(node.getChildren().size() - 1), consumer,
                  map, orChoices);
            }
          }
          else {
            if (node.hasDefault()) {
              value = this.atdDomain.getDefaultValue(node);
            }
            else {
              if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(n.getType())) {
                int ind = orChoices.get(orChoices.size() - 1);
                orChoices.remove(orChoices.size() - 1);
                value = markRecursive(node.getChildren().get(ind), consumer, map, orChoices);
              }
              else {
                value = markRecursive(node.getChildren().get(0), consumer, map, orChoices);
                for (int i = 1; i < node.getChildren().size() - 1; i++) {
                  this.atdDomain.calc(value,
                      markRecursive(node.getChildren().get(i), consumer, map, orChoices),
                      ((ADTNode) node).getType());
                }
              }
            }
            if (((ADTNode) node).getRole() == ADTNode.Role.OPPONENT) {
              value = this.atdDomain.co(value, markRecursive(
                  node.getChildren().get(node.getChildren().size() - 1), consumer, map, orChoices));
            }
            else {
              value = this.atdDomain.cp(value, markRecursive(
                  node.getChildren().get(node.getChildren().size() - 1), consumer, map, orChoices));
            }
          }
        }
        else {
          if (((AdtRankingDomain<Type>) this.atdDomain).isOrType(n.getType())) {
            int ind = orChoices.get(orChoices.size() - 1);
            orChoices.remove(orChoices.size() - 1);
            value = markRecursive(node.getChildren().get(ind), consumer, map, orChoices);
          }
          else {
            value = null;
            for (Node child : node.getChildren()) {
              if (value == null) {
                value = markRecursive(child, consumer, map, orChoices);
              }
              else {
                value = atdDomain.calc(value,
                    markRecursive(child, consumer, map, orChoices), ((ADTNode) node).getType());
              }
            }
          }
        }
      }
    }
    else {
      if (node instanceof SandNode) {
        value =  map.get(true, node.getName());
      }
      else {
        value = map.get(((ADTNode) node).getRole() == ADTNode.Role.PROPONENT, node.getName());
      }
    }
    consumer.rankNode(node, value);
    return value;
  }

  private Node                      lastNode;
  private ArrayList<RankNode<Type>> lastResult;
  private AdtDomain<Type>           atdDomain;
  private SandDomain<Type>          sandDomain;

}
