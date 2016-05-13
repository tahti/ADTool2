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
package lu.uni.adtool.tree;

import java.util.ArrayList;
import java.util.List;

import org.abego.treelayout.util.AbstractTreeForTreeLayout;

public class TreeForLayout extends AbstractTreeForTreeLayout<Node> {
  public TreeForLayout(GuiNode root) {
    super(root);
  }

  @Override
  public List<Node> getChildrenList(Node node) {
    if(node == null) return null;
    if (((GuiNode) node).isFolded()) {
      return new ArrayList<Node>();
    }
    return node.getNotNullChildren();
  }

  /**
   * {@inheritDoc}
   *
   * @see AbstractTreeForTreeLayout#getParent(GuiNode)
   */
  @Override
  public Node getParent(Node node) {
    return ((GuiNode) node).getParent(false);
  }
}
