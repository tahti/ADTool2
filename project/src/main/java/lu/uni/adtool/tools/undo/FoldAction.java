package lu.uni.adtool.tools.undo;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.util.ArrayList;

public class FoldAction extends EditAction {

  public FoldAction(Node target, boolean above) {
    this.targetPath = target.toPath();
    this.above = above;
  }

  public void undo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (this.above) {
      canvas.toggleAboveFold(target);
    }
    else {
      canvas.toggleFold(target);
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    this.undo(canvas);
  }


  public String getName(){
    if (this.above) {
      return Options.getMsg("action.foldabove");
    }
    else {
      return Options.getMsg("action.foldbelow");
    }
  }

  private ArrayList<Integer> targetPath;
  private boolean above;

}
