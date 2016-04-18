package lu.uni.adtool.tools.undo;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.util.ArrayList;

public class AddChild extends EditAction {


  public AddChild(Node parent) {
    this.parentPath = parent.toPath();
  }

  public void undo(AbstractTreeCanvas canvas) {
    Node parent = canvas.getTree().getRoot(true).fromPath(parentPath, 0);
    if (parent instanceof SandNode) {
      Node toRemove = parent.getNotNullChildren().get(parent.getNotNullChildren().size() - 1);
      canvas.removeTree(toRemove);
      canvas.undoGetNewLabel();
    }
    else {
      Node toRemove = null;
      if (((ADTNode)parent).isCountered()) {
        toRemove = parent.getNotNullChildren().get(parent.getNotNullChildren().size() - 2);
      }
      else {
        toRemove = parent.getNotNullChildren().get(parent.getNotNullChildren().size() - 1);
      }
      canvas.removeTree(toRemove);
      canvas.undoGetNewLabel();
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    Node parent = canvas.getTree().getRoot(true).fromPath(parentPath, 0);
    if (parent instanceof SandNode) {
      ((SandTreeCanvas)canvas).addChild(parent);
    }
    else {
      ((ADTreeCanvas)canvas).addChild(parent);
    }
  }


  public String getName(){
    return Options.getMsg("action.addchild");
  }

  private ArrayList<Integer> parentPath;
}
