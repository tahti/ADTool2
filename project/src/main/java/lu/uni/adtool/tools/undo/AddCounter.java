package lu.uni.adtool.tools.undo;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.util.ArrayList;

public class AddCounter extends EditAction {
  public AddCounter(Node parent) {
    this.parentPath = parent.toPath();
  }
  public void undo(AbstractTreeCanvas canvas) {
    Node parent = canvas.getTree().getRoot(true).fromPath(parentPath, 0);
    Node toRemove = parent.getNotNullChildren().get(parent.getNotNullChildren().size() - 1);
    canvas.removeTree(toRemove);
    canvas.undoGetNewLabel();
  }

  public void redo(AbstractTreeCanvas canvas) {
    Node parent = canvas.getTree().getRoot(true).fromPath(parentPath, 0);
    ((ADTreeCanvas)canvas).addCounter(parent);
  }

  public String getName(){
    return Options.getMsg("action.counter");
  }

  private ArrayList<Integer> parentPath;
}
