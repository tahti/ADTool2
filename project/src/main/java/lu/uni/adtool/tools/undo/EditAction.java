package lu.uni.adtool.tools.undo;

import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public abstract class EditAction {
  EditAction() {
  }
  public abstract void undo(AbstractTreeCanvas canvas);
  public abstract void redo(AbstractTreeCanvas canvas);
  public abstract String getName();

}
