package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.Node;

public interface NodeRanker {
  void rankNode(Node node, Ring value);
}
