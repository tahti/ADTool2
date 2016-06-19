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

import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.abego.treelayout.NodeExtentProvider;

public class SharedExtentProvider extends AbstractExtentProvider {
  public SharedExtentProvider() {
    canvasSet = new HashSet<AbstractTreeCanvas>();
    sizes = new HashMap<Node, Point2D.Double>();
  }

  public void registerCanvas(AbstractTreeCanvas canvas) {
    Debug.log("with tree id :"+ canvas.getTreeId());
    canvasSet.add(canvas);
  }

  public void deregisterCanvas(AbstractTreeCanvas canvas) {
    canvasSet.remove(canvas);
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getWidth(Node)
   */
  @Override
  public final double getWidth(final Node node) {
    return sizes.get(node).x;
  }

  public void notifyTreeChanged() {
    for (AbstractTreeCanvas listener : canvasSet) {
      listener.treeChanged();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getHeight(Node)
   */
  @Override
  public final double getHeight(final Node node) {
    return sizes.get(node).y;
  }

  /**
   * Deletes all calculated sizes.
   *
   * @param node
   *
   */
  public final void clearSizes() {
    sizes.clear();
  }

  /**
   * Calculates new size for the node.
   *
   * @param node
   *
   */
  public void updateNodeSize(final Node node) {
    Point2D.Double size = new Point2D.Double(0, 0);
    String[] labels;
    for (AbstractTreeCanvas canvas : canvasSet) {
      labels = canvas.getLabelLines(node);
      Point2D.Double newSize = getSizeOfLabels(labels);
      size.x = Math.max(size.x, newSize.x);
      size.y = Math.max(size.y, newSize.y);
    }
    size.x = correctForOval(size.x, node);
    size.y = correctForOval(size.y, node);
    // no vertical ellipses/rectangle - use cicle/square then
    if (size.x < size.y) {
      size.x = size.y;
    }
    sizes.put(node, size);
  }

  public void updateTreeSize(final Node node) {
    this.updateNodeSize(node);
    if (node.getChildren() != null) {
      for (Node child : node.getChildren()) {
        this.updateTreeSize(child);
      }
    }
  }

  /**
   * Gets the sizes for this instance.
   *
   * @return The sizes.
   */
  public HashMap<Node, Point2D.Double> getSizes() {
    return this.sizes;
  }

  private Set<AbstractTreeCanvas>       canvasSet;
  private HashMap<Node, Point2D.Double> sizes;

}
