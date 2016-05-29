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
import java.util.ArrayList;
import java.util.List;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

public class AddSibling extends EditAction {


  public AddSibling(Node target, boolean onLeft) {
    this.targetPath = target.toPath();
    this.onLeft = onLeft;
  }

@SuppressWarnings("unchecked")
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
      ((SandTreeCanvas<Ring>)canvas).removeTree(toRemove);
    }
    else {
      ((ADTreeCanvas<Ring>)canvas).removeTree(toRemove);
    }
    canvas.undoGetNewLabel();
  }

@SuppressWarnings("unchecked")
  public void redo(AbstractTreeCanvas canvas) {
    Node target = canvas.getTree().getRoot(true).fromPath(targetPath, 0);
    if (target instanceof SandNode) {
      ((SandTreeCanvas<Ring>)canvas).addSibling(target, onLeft);
    }
    else {
      ((ADTreeCanvas<Ring>)canvas).addSibling(target, onLeft);
    }
  }


  public String getName(){
    return Options.getMsg("action.addsibling");
  }

  private ArrayList<Integer> targetPath;
  private boolean onLeft;
}
