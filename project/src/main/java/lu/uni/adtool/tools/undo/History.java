package lu.uni.adtool.tools.undo;

import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.util.ArrayDeque;

public class History {
  public History() {
    undoList = new ArrayDeque<EditAction>();
    redoList = new ArrayDeque<EditAction>();
    noUpdates = false;
  }

  public void addAction(EditAction action) {
    if (!noUpdates) {
      undoList.addFirst(action);
      redoList.clear();
      Debug.log("Added undo action:"+action.getName());
    }
  }

  public String getUndoText() {
    EditAction action = undoList.peek();
    if (action != null) {
      return Options.getMsg("edit.undo.text", action.getName());
    }
    return null;
  }

  public String getRedoText() {
    EditAction action = redoList.peek();
    if (action != null) {
      return Options.getMsg("edit.redo.text", action.getName());
    }
    return null;
  }

  public void undo(AbstractTreeCanvas canvas) {
    EditAction action = undoList.poll();
    if (action != null) {
      Debug.log("Undo  action:"+action.getName());
      noUpdates = true;
      action.undo(canvas);
      redoList.addFirst(action);
      noUpdates = false;
    }
    else {
      Debug.log("No Undo  action available.");
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    EditAction action = redoList.poll();
    if (action != null) {
      Debug.log("Redo  action:" + action.getName());
      noUpdates = true;
      action.redo(canvas);
      undoList.addFirst(action);
      noUpdates = false;
    }
  }

  private ArrayDeque<EditAction> undoList;
  private ArrayDeque<EditAction> redoList;
  private boolean noUpdates;
}
