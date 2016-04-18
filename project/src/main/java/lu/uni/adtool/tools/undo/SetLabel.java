package lu.uni.adtool.tools.undo;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.util.ArrayList;

public class SetLabel extends EditAction {

  public SetLabel(Node target, String oldLabel, String oldComment, String newLabel,  String newComment) {
    this.targetPath = target.toPath();
    this.oldLabel = oldLabel;
    this.newLabel = newLabel;
    this.oldComment = oldComment;
    this.newComment = newComment;
  }

  public void undo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandTreeCanvas<Ring>)canvas).setLabel(target, oldLabel, oldComment);
    }
    else {
      ((ADTreeCanvas<Ring>)canvas).setLabel(target, oldLabel, oldComment);
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandTreeCanvas<Ring>)canvas).setLabel(target, newLabel, newComment);
    }
    else {
      ((ADTreeCanvas<Ring>)canvas).setLabel(target, newLabel, newComment);
    }
  }

  public String getName(){
    return Options.getMsg("action.toggleop");
  }

  private ArrayList<Integer> targetPath;

  private String oldLabel;
  private String newLabel;
  private String oldComment;
  private String newComment;

}
