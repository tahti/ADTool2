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

import java.util.ArrayList;
import java.util.List;

public class NodeTree {
  public NodeTree(TreeLayout layout) {
    this.layout = layout;
    this.viewRoot = null;
    this.sharedExtentProvider = new SharedExtentProvider();
    recalculateSiblings();
  }

  public TreeForLayout getTreeForLayout() {
    if (viewRoot != null) {
      return new TreeForLayout(viewRoot);
    }
    else {
      return new TreeForLayout((GuiNode) this.getRoot(true));
    }
  }

  /**
   * Gets the children list.
   *
   * @param node
   * @param ignoreFold
   * @return
   */
  public List<Node> getChildrenList(Node node, boolean ignoreFold) {
    if (!ignoreFold && ((GuiNode) node).isFolded()) {
      return new ArrayList<Node>();
    }
    if (node.getChildren() == null) {
      return new ArrayList<Node>();
    }
    else {
      return node.getChildren();
    }
  }

  /**
   * Gets the viewRoot for this instance.
   *
   * @param ignoreFold
   * @return The viewRoot.
   */
  public Node getRoot(final boolean ignoreFold) {
    if (!ignoreFold && this.viewRoot != null) {
      return this.viewRoot;
    }
    else {
      return this.layout.getRoot();
    }
  }

  public TreeLayout getLayout() {
    return layout;
  }

  /**
   */
  public void setRoot(GuiNode root) {
    this.viewRoot = (GuiNode) root;
    this.layout.setRoot(root);
    Debug.log(" before updateTreeSize");
    this.sharedExtentProvider.updateTreeSize(getRoot(true));
    this.recalculateSiblings();
  }

  /**
   * Gets the sharedExtentProvider for this instance.
   *
   * @return The sharedExtentProvider.
   */
  public SharedExtentProvider getSharedExtentProvider() {
    return this.sharedExtentProvider;
  }

  public void setName(Node node, String label) {
    Debug.log("setName ****" + node.getName() + " to " + label);
    this.layout.rename(node, label);
    node.setName(label);
    this.layout.refreshValues();
    this.sharedExtentProvider.updateNodeSize(node);
  }

  public void recalculateSiblings() {
    GuiNode root = (GuiNode) this.getRoot(false);
    root.setLeftSibling(root);
    root.setRightSibling(root);
    this.recalculateSiblings((ArrayList<Node>) (this.getChildrenList(root, false)));
  }

  // /**
  // * Adds a listener for tree changes.
  // *
  // * @param listener
  // */
  // public void addTreeChangeListener(TreeChangeListener listener) {
  // listeners.add(listener);
  // }
  //
  // public void removeTreeChangeListener(TreeChangeListener listener) {
  // listeners.remove(listener);
  // }

  /**
   * Adds a child.
   *
   * @param parentNode
   *          [hasNode(parentNode)]
   * @param node
   *          [!hasNode(node)]
   */
  public void addChild(Node parentNode, Node node) {
    boolean refresh = parentNode.hasDefault();
    parentNode.addChild(node);
    if (!((GuiNode) parentNode).isFolded()) {
      recalculateSiblings();
    }
    if (node.hasDefault()) {
      if (node instanceof SandNode) {
        layout.setDefaultValuation((SandNode) node);
      }
      else {
        layout.setDefaultValuation((ADTNode) node);
      }
    }
    if (refresh) {
      this.layout.refreshValues();
    }
    sharedExtentProvider.updateNodeSize(node);
  }

  /**
   * Removes a subtree from the tree.
   *
   * @param node
   *          a root of a subtree to be removed.
   */
  public void removeTree(Node node) {
    if (node.getParent() != null) {
      unfoldNode(node);
      node.getParent().getChildren().remove(node);
      if (!((GuiNode) node.getParent()).isFolded()) {
        recalculateSiblings();
      }
      layout.refreshValues();
      sharedExtentProvider.clearSizes();
      sharedExtentProvider.updateTreeSize(getRoot(true));
    }
  }

  public void switchSibling(Node node, Node newPos) {
    if (node != null && node.getParent() != null && newPos != null && newPos.getParent() != null) {
      if (node instanceof ADTNode) {
        if (((ADTNode) node).getRole() != ((ADTNode) newPos).getRole()) return;
      }
      int ni = node.getParent().getChildren().indexOf(node);
      int wi = newPos.getParent().getChildren().indexOf(newPos);
      node.getParent().getChildren().set(ni, newPos);
      newPos.getParent().getChildren().set(wi, node);
      Node p = node.getParent();
      node.setParent(newPos.getParent());
      newPos.setParent(p);
      recalculateSiblings();
    }
  }

  /**
   * Returns true if subtree was added, false otherwise
   */
  public boolean addSubtree(Node parent, Node subtree) {
    if (parent instanceof SandNode) {
      parent.addChild(subtree);
    }
    else {
      if (((ADTNode) parent).getRole() != ((ADTNode) subtree).getRole()) {
        if (!((ADTNode) parent).isCountered()) {
          ((ADTNode) parent).addCounter((ADTNode) subtree);
        }
        else {
          return false;
        }
      }
      else {
        parent.addChild(subtree);
      }
    }
    recalculateSiblings();
    this.sharedExtentProvider.updateTreeSize(parent);
    this.layout.refreshValues();
    return true;
  }

  public void addCounter(ADTNode parentNode, ADTNode node) {
    boolean refresh = parentNode.hasDefault();
    // checkArg(hasNode(parentNode), "parentNode is not in the tree");
    // checkArg(!hasNode(node), "node is already in the tree");
    parentNode.addCounter(node);
    if (!((GuiNode) parentNode).isFolded()) {
      recalculateSiblings();
    }
    if (((ADTNode) node).hasDefault()) {
      layout.setDefaultValuation((ADTNode) node);
    }
    if (refresh) {
      this.layout.refreshValues();
    }
    sharedExtentProvider.updateNodeSize(node);

  }

  public void removeAllChildren(Node node) {
    unfoldNode(node);
    node.getChildren().clear();
    layout.refreshValues();
  }

  /**
   * @param node
   *          [hasNode(node)]
   * @param sibling
   *          [!hasNode(sibling)]
   * @param onLeft
   */
  public void addSibling(Node node, Node sibling, boolean onLeft) {
    Node parentNode = node.getParent();
    List<Node> siblings = getChildrenList(parentNode, true);
    int index = siblings.indexOf(node);
    if (!onLeft) {
      ++index;
    }
    sibling.setParent(parentNode);
    siblings.add(index, sibling);
    if (sibling instanceof SandNode && ((SandNode) sibling).isEditable()) {
      layout.setDefaultValuation((SandNode) sibling);
    }
    if (sibling instanceof SandNode && ((SandNode) sibling).isEditable()) {
      layout.setDefaultValuation((SandNode) sibling);
    }
    if (sibling instanceof ADTNode && ((ADTNode) sibling).hasDefault()) {
      layout.setDefaultValuation((ADTNode) sibling);
    }
    sharedExtentProvider.updateNodeSize(sibling);
  }

  public void toggleAboveFold(Node node, boolean notify) {
    ((GuiNode) node).setAboveFolded(!((GuiNode) node).isAboveFolded());
    viewRoot = (GuiNode) getNewViewNode(node);
    if (notify) {
      recalculateSiblings();
    }
  }

  public void toggleFold(Node node, boolean notify) {
    if (node != null && node.getChildren().size() > 0) {
      ((GuiNode) node).setFolded(!((GuiNode) node).isFolded());
      if (notify) {
        recalculateSiblings();
      }
    }
  }

  // /**
  // * Notifies registered listeners about tree change.
  // *
  // */
  // public void notifyTreeChanged() {
  // sharedExtentProvider.notifyTreeChanged();
  // }

  private Node getNewViewNode(Node node) {
    if (((GuiNode) node).isAboveFolded()) {
      return node;
    }
    else {
      Node parent = node.getParent();
      if (parent == null) {
        return null;
      }
      else {
        return getNewViewNode(parent);
      }
    }
  }

  private void unfoldNode(Node node) {
    if (((GuiNode) node).isAboveFolded()) {
      toggleAboveFold(node, false);
    }
    if (((GuiNode) node).isFolded()) {
      toggleFold(node, false);
    }
  }

  private void recalculateSiblings(ArrayList<Node> levelList) {
    ArrayList<Node> nextList = new ArrayList<Node>();
    int i = 0;
    while (i < levelList.size()) {
      GuiNode curr = (GuiNode) levelList.get(i);
      curr.setRightSibling(levelList.get((i + 1) % levelList.size()));
      curr.setLeftSibling(levelList.get((i + levelList.size() - 1) % levelList.size()));
      nextList.addAll((ArrayList<Node>) this.getChildrenList(curr, false));
      i++;
    }
    if (nextList.size() > 0) {
      this.recalculateSiblings(nextList);
    }
  }

  private GuiNode                          viewRoot;
  private TreeLayout                       layout;
  transient protected SharedExtentProvider sharedExtentProvider;
  /**
   * Listener that should be notified first (Terms).
   */
  // transient private TreeChangeListener firstListener;

}
