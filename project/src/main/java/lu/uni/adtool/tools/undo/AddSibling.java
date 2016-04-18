package lu.uni.adtool.tools.undo;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.util.ArrayList;
import java.util.List;

public class AddSibling extends EditAction {


  public AddSibling(Node target, boolean onLeft) {
    this.targetPath = target.toPath();
    this.onLeft = onLeft;
  }

  public void undo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    Node parent = target.getParent();
    List<Node> siblings = parent.getNotNullChildren();
    int index = siblings.indexOf(target);
    if (!onLeft) {
      ++index;
    }
    Node toRemove = parent.getNotNullChildren().get(index);
    if (target instanceof SandNode) {
      ((SandTreeCanvas)canvas).removeTree(toRemove);
    }
    else {
      ((ADTreeCanvas)canvas).removeTree(toRemove);
    }
    canvas.undoGetNewLabel();
  }

  public void redo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandTreeCanvas)canvas).addSibling(target, onLeft);
    }
    else {
      ((ADTreeCanvas)canvas).addSibling(target, onLeft);
    }
  }


  public String getName(){
    return Options.getMsg("action.addsibling");
  }

  private ArrayList<Integer> targetPath;
  private boolean onLeft;
}
