package lu.uni.adtool.tree;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Node implements Serializable{
  public Node() {
    this.name = "root";
  }

  public Node(String name) {
    this.name = name;
  }

  public abstract String toString();

  public final void setName(final String n) {
    this.name = n;
  }

  public String getName() {
    return this.name;
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

  public final void addChildAt(Node child, final int indexAt, final int noChildren) {
    // final Node child = new Node();
    // child.setType(type);
    // child.setName(name);
    ArrayList<Node> newChildren = null;
    if (noChildren > 0) {
      newChildren = new ArrayList<Node>(children.subList(indexAt, indexAt + noChildren));
      this.children.removeAll(newChildren);
      for (Node n : newChildren) {
        if (n != null) {
          n.setParent(child);
        }
      }
    }
    this.children.add(indexAt, child);
    child.setParent(this);
    child.setChildren(newChildren);
  }

  protected ArrayList<Node> children;
  private String            name;
  private Node              parent;
}
