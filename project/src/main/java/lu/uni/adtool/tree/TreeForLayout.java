package lu.uni.adtool.tree;

import java.util.ArrayList;
import java.util.List;

import org.abego.treelayout.util.AbstractTreeForTreeLayout;

public class TreeForLayout extends AbstractTreeForTreeLayout<Node> {
  public TreeForLayout(GuiNode root) {
    super(root);
  }

  @Override
  public List<Node> getChildrenList(Node node) {
    if(node == null) return null;
    if (((GuiNode) node).isFolded()) {
      return new ArrayList<Node>();
    }
    return node.getNotNullChildren();
  }

  /**
   * {@inheritDoc}
   *
   * @see AbstractTreeForTreeLayout#getParent(GuiNode)
   */
  @Override
  public Node getParent(Node node) {
    return ((GuiNode) node).getParent(false);
  }
}
