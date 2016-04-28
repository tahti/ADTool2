package lu.uni.adtool.tools.undo;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.util.ArrayList;

public class RemoveTree extends EditAction {
  public RemoveTree(Node target) {
    this.child = target;
    Node parent = target.getParent();
    this.parentPath = parent.toPath();
    this.index = parent.getChildren().indexOf(target);
  }

  public void undo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node parent = tree.getRoot(true).fromPath(parentPath, 0);
    parent.addChildAt(this.child, index);
    tree.getLayout().refreshValues();
    tree.getSharedExtentProvider().updateTreeSize(parent);
    canvas.notifyAllTreeChanged();
    canvas.updateTerms();
  }

  public void redo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node parent = tree.getRoot(true).fromPath(parentPath, 0);
    this.child = parent.getChildren().get(index);
    canvas.removeTree(this.child);
  }

  public String getName(){
    return Options.getMsg("action.removesubtree");
  }

  private ArrayList<Integer> parentPath;
  private Node child;
  private int index;
}
