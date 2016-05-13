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

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.util.ArrayList;

public class RemoveTree extends EditAction {
  public RemoveTree(Node target) {
    this.child = target;
    Node parent = target.getParent();
    this.parentPath = parent.toPath();
    this.index = parent.getChildren().indexOf(target);
  }

  public void undo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node parent = tree.getRoot(true).fromPath(parentPath, 0);
    parent.addChildAt(this.child, index);
    tree.getLayout().refreshValues();
    tree.getSharedExtentProvider().updateTreeSize(parent);
    canvas.notifyAllTreeChanged();
    canvas.updateTerms();
  }

  public void redo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node parent = tree.getRoot(true).fromPath(parentPath, 0);
    this.child = parent.getChildren().get(index);
    canvas.removeTree(this.child);
  }

  public String getName(){
    return Options.getMsg("action.removesubtree");
  }

  private ArrayList<Integer> parentPath;
  private Node child;
  private int index;
}
