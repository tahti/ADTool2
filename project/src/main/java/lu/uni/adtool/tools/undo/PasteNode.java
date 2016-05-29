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

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class PasteNode extends EditAction {
  public PasteNode(Node target, Node subtree) {
    this.subtree = subtree;
    this.targetPath  = target.toPath();
  }

  public void redo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node target = tree.getRoot(true).fromPath(this.targetPath, 0);
    tree.addSubtree(target, this.subtree);
    canvas.notifyAllTreeChanged();
    canvas.updateTerms();
  }

  public void undo(AbstractTreeCanvas canvas) {
    NodeTree tree = canvas.getTree();
    Node target = tree.getRoot(true).fromPath(this.targetPath, 0);
    int index = target.getChildren().size() - 1;
    if (target instanceof ADTNode && ((ADTNode) target).isCountered() &&
        ((ADTNode) target).getRole() == ((ADTNode) this.subtree).getRole()) {
      index = index - 1;
    }
    Node child = target.getChildren().get(index);
    canvas.removeTree(child);
  }

  public String getName(){
    return Options.getMsg("action.pastenode");
  }

  private ArrayList<Integer> targetPath;
  private Node subtree;
}
