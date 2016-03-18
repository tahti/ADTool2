package lu.uni.adtool.domains;

import lu.uni.adtool.domains.adtpredefined.RankingDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;

import java.util.ArrayList;

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
   * @param newmap
   *          mapping between node names and values.
   * @return true if evaluation was successful.
   */
  public final ArrayList<RankNode<Type>> rank(final ADTNode root,
      final ValueAssignement<Type> valuesMap, int maxItems) {
    this.lastNode = root;
    if (valuesMap == null || root == null || this.atdDomain == null
        || !(this.atdDomain instanceof RankingDomain)) {
      Debug.log("NULL result");
      return null;
    }
    this.lastResult = rankRecursive(root, valuesMap, maxItems);
    return this.lastResult;

    // return rankRecursive(root, newProMap, newOppMap, maxItems);
  }

  public final ArrayList<RankNode<Type>> rank(final SandNode root,
      final ValueAssignement<Type> valueAssigment, int maxItems) {
    this.lastNode = root;
    if (valueAssigment == null || root == null || sandDomain == null) {
      Debug.log("NULL result");
      return null;
    }
    this.lastResult = rankRecursive(root, valueAssigment, maxItems);
    return this.lastResult;
  }

  public final void initGetRanking(final Node root, final ValueAssignement<Type> valuesMap,
      int maxItems) {
    if (valuesMap == null || root == null || this.atdDomain == null
        || !(this.atdDomain instanceof RankingDomain)) {
      Debug.log("NULL result");
      return;
    }
    if (root instanceof SandNode) {
      rankRecursive((SandNode) root, valuesMap, maxItems);
    }
    else {
      rankRecursive((ADTNode) root, valuesMap, maxItems);
    }
  }

  /**
   * Function used when exporting ranking to XML file. Should be called after
   * initGetRanking(Node root)
   */
  public final ArrayList<Ring> getRanking(final Node node) {
    return null;
  }

  public final void finishGetRanking(final Node root) {
  }

  public void rankNode(Node node) {
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
      }
      result.add(new RankNode<Type>(value));
    }
    else {
      ArrayList<ArrayList<RankNode<Type>>> list = new ArrayList<ArrayList<RankNode<Type>>>();
      list.ensureCapacity(root.getChildren().size() - c);
      for (int i = 0; i < (root.getChildren().size() - c); i++) {
        list.add(rankRecursive((ADTNode) root.getChildren().get(i), valuesMap, maxItems));
      }
      if (((RankingDomain<Type>) this.atdDomain).isOrType(root.getType())) {
        result = ((RankingDomain<Type>) this.atdDomain).minOp(list, maxItems, root.getType());
      }
      else {
        result =
            ((RankingDomain<Type>) this.atdDomain).conjunctiveOp(list, maxItems, root.getType());
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
        if (((RankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_OPP)) {
          result =
              ((RankingDomain<Type>) this.atdDomain).minOp(list, maxItems, ADTNode.Type.AND_OPP);
        }
        else {
          result = ((RankingDomain<Type>) this.atdDomain).conjunctiveOp(list, maxItems,
              ADTNode.Type.AND_OPP);
        }
      }
      else {
        // assuming AND_OPP == CP
        if (((RankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_PRO)) {
          result =
              ((RankingDomain<Type>) this.atdDomain).minOp(list, maxItems, ADTNode.Type.AND_PRO);
        }
        else {
          result = ((RankingDomain<Type>) this.atdDomain).conjunctiveOp(list, maxItems,
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
      }
      result.add(new RankNode<Type>(value));
    }
    else {
      ArrayList<ArrayList<RankNode<Type>>> list = new ArrayList<ArrayList<RankNode<Type>>>();
      list.ensureCapacity(root.getChildren().size());
      for (Node child : root.getChildren()) {
        list.add(rankRecursive((SandNode) child, map, maxItems));
      }
      switch (root.getType()) {
      case AND:
        result = sandDomain.and(list, maxItems);
        break;
      case OR:
        result = sandDomain.or(list, maxItems);
        break;
      case SAND:
        result = sandDomain.sand(list, maxItems);
        break;
      default:
        System.err.println(Options.getMsg("error.evaluation.noType"));
        return null;
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
    Type value;
    if (node.getChildren() != null && node.getChildren().size() > 0) {
      if (node instanceof SandNode) {
        switch (((SandNode) node).getType()) {
        case OR:
          int index = orChoices.get(orChoices.size() - 1);
          orChoices.remove(orChoices.size() - 1);
          value = markRecursive(node.getChildren().get(index), consumer, map, orChoices);
          break;
        case SAND:
          value = markRecursive(node.getChildren().get(0), consumer, map, orChoices);
          for (int i = 1; i < node.getChildren().size(); i++) {
            value = this.sandDomain.sand(value,
                markRecursive(node.getChildren().get(i), consumer, map, orChoices));
          }
          break;
        case AND:
        default:
          value = markRecursive(node.getChildren().get(0), consumer, map, orChoices);
          for (int i = 1; i < node.getChildren().size(); i++) {
            value = this.sandDomain.and(value,
                markRecursive(node.getChildren().get(i), consumer, map, orChoices));
          }
          break;
        }
      }
      else {
        ADTNode n = (ADTNode) node;
        if (n.isCountered()) {
          boolean doOr = false;
          if (n.getRole() == ADTNode.Role.PROPONENT) {
            if (((RankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_PRO)) {
              doOr = true;
            }
          }
          else {
            if (((RankingDomain<Type>) this.atdDomain).isOrType(ADTNode.Type.AND_OPP)) {
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
                if (((RankingDomain<Type>) this.atdDomain).isOrType(n.getType())) {
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
              if (((RankingDomain<Type>) this.atdDomain).isOrType(n.getType())) {
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
          if (((RankingDomain<Type>) this.atdDomain).isOrType(n.getType())) {
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
