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

public abstract class GuiNode extends Node {

  public GuiNode() {
    super();
    folded = false;
    aboveFolded = false;
  }

  public GuiNode(String name) {
    super(name);
    folded = false;
    aboveFolded = false;
  }

  /**
   * Returns parent of the node.
   *
   * @param ignoreFold
   *          if true we return parent even if the node is folded from above
   * @return the parent node or null
   */
  public GuiNode getParent(boolean ignoreFold) {
    if (!ignoreFold && isAboveFolded()) {
      return null;
    }
    return (GuiNode) getParent();
  }

  /**
   * @return the folded
   */
  public boolean isFolded() {
    return folded;
  }

  /**
   * @param folded
   *          the folded to set
   */
  public void setFolded(boolean folded) {
    this.folded = folded;
  }

  /**
   * @return the aboveFolded
   */
  public boolean isAboveFolded() {
    return aboveFolded;
  }

  /**
   * @param aboveFolded
   *          the aboveFolded to set
   */
  public void setAboveFolded(boolean aboveFolded) {
    this.aboveFolded = aboveFolded;
  }

  /**
   * @return the leftSibling
   */
  public GuiNode getLeftSibling() {
    return leftSibling;
  }

  /**
   * @param leftSibling
   *          the leftSibling to set
   */
  public void setLeftSibling(Node leftSibling) {
    this.leftSibling = (GuiNode) leftSibling;
  }

  /**
   * @return the rightSibling
   */
  public GuiNode getRightSibling() {
    return rightSibling;
  }

  /**
   * Return a middle child - only when node is not folded
   *
   * @param node
   * @return
   */
  public GuiNode getMiddleChild() {
    if (this.isFolded()) {
      return null;
    }
    if (children == null) {
      return null;
    }
    if (children.size() == 0) {
      return null;
    }
    return (GuiNode) children.get(children.size() / 2);
  }

  /**
   * @param rightSibling
   *          the rightSibling to set
   */
  public void setRightSibling(Node rightSibling) {
    this.rightSibling = (GuiNode) rightSibling;
  }

  private boolean folded;
  private boolean aboveFolded;
  private GuiNode leftSibling;
  private GuiNode rightSibling;
  private static final long serialVersionUID = 1285171599983300177L;
}
