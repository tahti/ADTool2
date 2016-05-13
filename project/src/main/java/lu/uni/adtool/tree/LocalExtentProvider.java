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

import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import org.abego.treelayout.NodeExtentProvider;

/**
 * A {@link NodeExtentProvider} for nodes of type {@link TextInBox}.
 * <p>
 * As one would expect this NodeExtentProvider returns the width and height.
 *
 * @author Piotr Kordy
 */
public class LocalExtentProvider extends AbstractExtentProvider {
  /**
   * Constructs a new instance.
   *
   * @param owner
   *          canvas owning this instarce.
   */
  public LocalExtentProvider(AbstractTreeCanvas owner) {
    this.owner = owner;
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getWidth(Node)
   */
  public double getWidth(final Node node) {
    String[] labels = owner.getLabelLines(node);
    double result = getSizeOfLabels(labels).x;
    // no vertical ellipses
    return Math.max(correctForOval(result, node), getHeight(node));
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getHeight(Node)
   */
  public double getHeight(final Node node) {
    String[] labels = owner.getLabelLines(node);
    double result = getSizeOfLabels(labels).y;
    return correctForOval(result, node);
  }

  /**
   * Gets the owner for this instance.
   *
   * @return The owner.
   */
  public AbstractTreeCanvas getOwner() {
    return this.owner;
  }

  private AbstractTreeCanvas owner;
}
