package lu.uni.adtool.tools.undo;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.util.ArrayList;

public class RemoveChildren extends EditAction {
  public RemoveChildren(Node target) {
    this.children = new ArrayList<Node>();
    for (Node child:target.getNotNullChildren()){
      children.add(child);
    }
    this.targetPath = target.toPath();
  }

  public void undo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node target = tree.getRoot(true).fromPath(targetPath, 0);
    for (Node child:children){
      tree.addChild(target, child);
    }
    canvas.notifyAllTreeChanged();
    canvas.updateTerms();
  }

  public void redo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node target = tree.getRoot(true).fromPath(targetPath, 0);
    this.children = new ArrayList<Node>();
    for (Node child:target.getNotNullChildren()){
      children.add(child);
    }
    canvas.removeChildren(target);
  }

  public String getName(){
    return Options.getMsg("action.removechildren");
  }

  private ArrayList<Integer> targetPath;
  private ArrayList<Node> children;
}
