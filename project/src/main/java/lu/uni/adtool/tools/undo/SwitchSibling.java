package lu.uni.adtool.tools.undo;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.util.ArrayList;

public class SwitchSibling extends EditAction {


  public SwitchSibling(Node target, boolean onLeft) {
    this.targetPath = target.toPath();
    this.onLeft = onLeft;
  }

  public void undo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandTreeCanvas)canvas).switchSibling(target, !onLeft);
    }
    else {
      ((ADTreeCanvas)canvas).switchSibling(target, !onLeft);
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandTreeCanvas)canvas).switchSibling(target, onLeft);
    }
    else {
      ((ADTreeCanvas)canvas).switchSibling(target, onLeft);
    }
  }


  public String getName(){
    return Options.getMsg("action.addsibling");
  }

  private ArrayList<Integer> targetPath;
  private boolean onLeft;
}
