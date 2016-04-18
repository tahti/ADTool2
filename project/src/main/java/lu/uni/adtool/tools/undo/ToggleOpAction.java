package lu.uni.adtool.tools.undo;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.util.ArrayList;

public class ToggleOpAction extends EditAction {


  public ToggleOpAction(Node target) {
    this.targetPath = target.toPath();
  }

  public void undo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandNode) target).toggleOp();
      ((SandTreeCanvas)canvas).toggleOp(target);
    }
    else {
      ((ADTreeCanvas)canvas).toggleOp(target);
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandTreeCanvas)canvas).toggleOp(target);
    }
    else {
      ((ADTreeCanvas)canvas).toggleOp(target);
    }
  }


  public String getName(){
    return Options.getMsg("action.toggleop");
  }

  private ArrayList<Integer> targetPath;
}
