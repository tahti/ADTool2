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
    this.lastResult = rankRecursive(root, valuesMap , maxItems);
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

  private ArrayList<RankNode<Type>> rankRecursive(final ADTNode root, ValueAssignement<Type> valuesMap, int maxItems){
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

  public void mark(int index, AbstractDomainCanvas canvas) {
    if (lastNode == null || lastResult == null) {
      return;
    }
    // TODO change interface a bit
    ArrayList<Integer> choices = new ArrayList<Integer>(lastResult.get(index).getList());
    canvas.markNode(lastNode, Options.canv_rankRootMark);
    if (lastNode.getChildren() != null && lastNode.getChildren().size() > 0) {
      if (lastNode instanceof SandNode) {
        if (((SandNode) lastNode).getType() == SandNode.Type.OR) {
          int ind = choices.get(choices.size() - 1);
          choices.remove(choices.size() - 1);
          markRecursive(lastNode.getChildren().get(ind), canvas, choices);
        }
        else {
          for (Node child : lastNode.getChildren()) {
            markRecursive(child, canvas, choices);
          }
        }
      }
      else {
        ADTNode node = (ADTNode) lastNode;
        if (node.isCountered()) {
          boolean doOr = false;
          if (node.getRole() == ADTNode.Role.PROPONENT) {
            if (((RankingDomain) this.atdDomain).isOrType(ADTNode.Type.AND_PRO)) {
              doOr = true;
            }
          }
          else {
            if (((RankingDomain) this.atdDomain).isOrType(ADTNode.Type.AND_OPP)) {
              doOr = true;
            }
          }
          if (doOr) {
            int ind = choices.get(choices.size() - 1);
            choices.remove(choices.size() - 1);
            if (ind == 0) {
              if (((RankingDomain) this.atdDomain).isOrType(node.getType())) {
                ind = choices.get(choices.size() - 1);
                choices.remove(choices.size() - 1);
                markRecursive(lastNode.getChildren().get(ind), canvas, choices);
              }
              else {
                for (int i =0; i < lastNode.getChildren().size() - 1 ; i ++) {
                  markRecursive(lastNode.getChildren().get(i), canvas, choices);
                }
              }
            }
            else {
              //counter
              markRecursive(lastNode.getChildren().get(lastNode.getChildren().size() - 1), canvas, choices);
            }
          }
          else {
            if (((RankingDomain) this.atdDomain).isOrType(node.getType())) {
              int ind = choices.get(choices.size() - 1);
              choices.remove(choices.size() - 1);
              markRecursive(lastNode.getChildren().get(ind), canvas, choices);
            }
            else {
              for (int i = 0; i < lastNode.getChildren().size() - 1 ; i ++) {
                markRecursive(lastNode.getChildren().get(i), canvas, choices);
              }
            }
            markRecursive(lastNode.getChildren().get(lastNode.getChildren().size() - 1), canvas, choices);
          }
        }
        else {
          if (((RankingDomain) this.atdDomain).isOrType(node.getType())) {
            int ind = choices.get(choices.size() - 1);
            choices.remove(choices.size() - 1);
            markRecursive(lastNode.getChildren().get(ind), canvas, choices);
          }
          else {
            for (Node child : lastNode.getChildren()) {
              markRecursive(child, canvas, choices);
            }
          }
        }
      }
    }
  }

  private ArrayList<Integer> markRecursive(Node node, AbstractDomainCanvas canvas,
      ArrayList<Integer> orChoices) {
    // TODO change interface a bit
    if (node.hasDefault()) {
      canvas.markNode(node, Options.canv_rankLeafMark);
    }
    else {
      canvas.markNode(node, Options.canv_rankNodeMark);
    }
    ArrayList<Integer> result = null;
    if (node.getChildren() != null && node.getChildren().size() > 0) {
      if (node instanceof SandNode) {
        if (((SandNode) node).getType() == SandNode.Type.OR) {
          int index = orChoices.get(orChoices.size() - 1);
          orChoices.remove(orChoices.size() - 1);
          result = markRecursive(node.getChildren().get(index), canvas, orChoices);
        }
        else {
          result = markRecursive(node.getChildren().get(0), canvas, orChoices);
          for (int i = 1; i < node.getChildren().size(); i++) {
            result = markRecursive(node.getChildren().get(i), canvas, result);
          }
        }
      }
      else {
        ADTNode n = (ADTNode) node;
        if (n.isCountered()) {
          boolean doOr = false;
          if (n.getRole() == ADTNode.Role.PROPONENT) {
            if (((RankingDomain) this.atdDomain).isOrType(ADTNode.Type.AND_PRO)) {
              doOr = true;
            }
          }
          else {
            if (((RankingDomain) this.atdDomain).isOrType(ADTNode.Type.AND_OPP)) {
              doOr = true;
            }
          }
          if (doOr) {
            int ind = orChoices.get(orChoices.size() - 1);
            orChoices.remove(orChoices.size() - 1);
            if (ind == 0) {
              if (((RankingDomain) this.atdDomain).isOrType(n.getType())) {
                ind = orChoices.get(orChoices.size() - 1);
                orChoices.remove(orChoices.size() - 1);
                markRecursive(node.getChildren().get(ind), canvas, orChoices);
              }
              else {
                for (int i =0; i < node.getChildren().size() - 1 ; i ++) {
                  markRecursive(node.getChildren().get(i), canvas, orChoices);
                }
              }
            }
            else {
              //counter
              markRecursive(node.getChildren().get(node.getChildren().size() - 1), canvas, orChoices);
            }
          }
          else {
            if (((RankingDomain) this.atdDomain).isOrType(n.getType())) {
              int ind = orChoices.get(orChoices.size() - 1);
              orChoices.remove(orChoices.size() - 1);
              markRecursive(node.getChildren().get(ind), canvas, orChoices);
            }
            else {
              for (int i = 0; i < node.getChildren().size() - 1 ; i ++) {
                markRecursive(node.getChildren().get(i), canvas, orChoices);
              }
            }
            markRecursive(node.getChildren().get(node.getChildren().size() - 1), canvas, orChoices);
          }
        }
        else {
          if (((RankingDomain) this.atdDomain).isOrType(n.getType())) {
            int ind = orChoices.get(orChoices.size() - 1);
            orChoices.remove(orChoices.size() - 1);
            markRecursive(node.getChildren().get(ind), canvas, orChoices);
          }
          else {
            for (Node child : node.getChildren()) {
              markRecursive(child, canvas, orChoices);
            }
          }
        }
      }
    }
    else
      return orChoices;
    return result;
  }

  private Node                      lastNode;
  private ArrayList<RankNode<Type>> lastResult;
  private AdtDomain<Type>           atdDomain;
  private SandDomain<Type>          sandDomain;

}
