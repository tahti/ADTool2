/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>)
 * Date:   10/12/2015
 * Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty of Science,
 *     Technology and Communication FSTC
 * All rights reserved.
 * Licensed under GNU Affero General Public License 3.0;
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
      if (undoList.size() > this.maxActions) {
        undoList.removeLast();
      }
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
  private int maxActions = 1000;
}
