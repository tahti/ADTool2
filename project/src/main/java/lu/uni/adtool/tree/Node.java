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

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Node implements Serializable{

  public Node() {
    this.name = "root";
    this.parent = null;
    this.comment = "";
  }

  public Node(String name) {
    this.parent = null;
    this.name = name;
    this.comment = "";
  }

  public abstract String toString();

  public final void setName(final String n) {
    this.name = n;
  }

  public String getName() {
    return this.name;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public final void setParent(final Node p) {
    this.parent = p;
  }

  public final boolean isLeaf() {
    if (children == null) return true;
    if (children.size() == 0) return true;
    return false;
  }

  public boolean hasDefault() {
    return isLeaf();
  }

  public final Node getParent() {
    return this.parent;
  }

  public void addChild(Node child) {
    getNotNullChildren().add(child);
    child.setParent(this);
  }

  public ArrayList<Node> getChildren() {
    return this.children;
  }

  public ArrayList<Integer> toPath() {
    if (parent == null ) {
      return new ArrayList<Integer>();
    }
    else {
      ArrayList<Integer> result = parent.toPath();
      final int index = parent.getChildren().indexOf(this);
      result.add(new Integer(index));
      return result;
    }
  }

  public Node fromPath(ArrayList<Integer> path, int index) {
    if (path.size() == 0) {
      return this;
    }
    if (path.size() == index + 1) {
      return children.get(path.get(index));
    }
    else {
      Node child = children.get(path.get(index));
      return child.fromPath(path, index + 1);
    }
  }

  public ArrayList<Node> getNotNullChildren() {
    if (this.children == null) {
      this.children = new ArrayList<Node>();
    }
    return this.getChildren();
  }

  public void setChildren(ArrayList<Node> newChildren) {
    this.children = newChildren;
  }

  /**
   * Removes all children of a node.
   *
   * @param node
   *          node of which children should be removed.
   */
  public final void removeAllChildren(final Node node) {
    for (Node n : children) {
      removeAllChildren(n);
    }
    children.clear();
  }

  /**
   * Removes a node and moves children to the current node.
   *
   * @param child
   *          node to be removed from the list of children.
   */
  public final void removeChild(final Node child) {
    if (children == null) {
      System.err.println("Tried to remove child from node with no children");
      return;
    }
    final int index = children.indexOf(child);
    if (index == -1) {
      System.err.println("Tried to remove child from that" + " is not contained in children");
      return;
    }
    final ArrayList<Node> newChildren = ((Node) child).children;
    children.remove(index);
    children.addAll(index, newChildren);
    for (Node c : newChildren) {
      c.setParent(this);
    }
  }

  /**
   * Adds a child at a specified index and assigns to it number of children.
   *
   * @param child
   *          child to be added
   * @param indexAt
   *          index at which we add child (from 0 to number of children parent
   *          has)
   * @param noChildren
   *          number of children to be transfered.
   */

  public final void addChildAt(Node child, final int indexAt) {
    // final Node child = new Node();
    // child.setType(type);
    // child.setName(name);
    this.children.add(indexAt, child);
    child.setParent(this);
//     child.setChildren(newChildren);
  }

  protected ArrayList<Node> children;
  private String            name;
  private String            comment;
  private Node              parent;
  private static final long serialVersionUID = -983678473499189388L;
}
