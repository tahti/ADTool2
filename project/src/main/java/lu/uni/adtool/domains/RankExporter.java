package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

import java.util.ArrayList;
import java.util.HashMap;

public class RankExporter implements NodeRanker {
  public RankExporter(Node root, ValueAssignement<Ring> valuesMap, Ranker<Ring> ranker, int maxItems) {
    this.maxItems = maxItems;
    ArrayList<RankNode<Ring>> result;
    if (root instanceof SandNode) {
      result = ranker.rank((SandNode)root, valuesMap, maxItems);
    }
    else {
      result = ranker.rank((ADTNode)root, valuesMap, maxItems);
    }
    this.ranking = new ArrayList<HashMap<Node, Ring>>();
    for (int i = 0; i < result.size(); i++) {
      this.ranking.add(new HashMap<Node, Ring>());
      ArrayList<Integer> choices = new ArrayList<Integer>(result.get(i).getList());
      ranker.markRecursive(root, this, valuesMap, choices);
    }
  }

  public void rankNode(Node node, Ring value) {
    Debug.log("Name:"+node.getName() + " val:"+value.toString());
    this.ranking.get(this.ranking.size() - 1).put(node, value);
  }

  public Ring getValue(Node node, int rank) {
    if (this.ranking != null && rank < this.ranking.size()) {
      return this.ranking.get(rank).get(node);
    }
    else {
      return null;
    }
  }

  private ArrayList<HashMap<Node, Ring>> ranking;
  private int maxItems;
}

