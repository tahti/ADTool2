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
package lu.uni.adtool.ui.canvas;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JScrollPane;

import org.abego.treelayout.util.DefaultConfiguration;

import lu.uni.adtool.adtree.ADTreeNode;
import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.NodeRanker;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.custom.AdtRealDomain;
import lu.uni.adtool.domains.rings.Bool;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.domains.rings.Int;
import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.domains.rings.LMHValue;
import lu.uni.adtool.domains.rings.Real;
import lu.uni.adtool.domains.rings.RealG0;
import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tools.undo.SetValuation;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.RankingDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.ValuationsDockable;
import lu.uni.adtool.ui.inputdialogs.BoundedIntegerDialog;
import lu.uni.adtool.ui.inputdialogs.InputDialog;
import lu.uni.adtool.ui.inputdialogs.IntDialog;
import lu.uni.adtool.ui.inputdialogs.LMHDialog;
import lu.uni.adtool.ui.inputdialogs.LMHEDialog;
import lu.uni.adtool.ui.inputdialogs.RealDialog;
import lu.uni.adtool.ui.inputdialogs.RealG0Dialog;
import lu.uni.adtool.ui.inputdialogs.RealZeroOneDialog;

public class AbstractDomainCanvas<Type extends Ring> extends AbstractTreeCanvas implements NodeRanker {

  public AbstractDomainCanvas(MainController mc, ValuationDomain values) {
    super(null, mc);
    if (values.getDomain() instanceof SandDomain) {
      this.listener = new SandDomainHandler<Type>(this);
    } else {
      this.listener = new AdtDomainHandler<Type>(this);
    }
    this.addMouseListener(listener);
    this.addMouseMotionListener(listener);
    this.addKeyListener(listener);
    this.configuration = new DefaultConfiguration<Node>(Options.canv_gapBetweenLevels, Options.canv_gapBetweenNodes);
    this.setFocus(null);
    this.values = values;
    this.marked = new HashMap<Node, Color>();
    this.markEditable = true;
    this.history = null;
  }

  /**
   * Constructor used to export tree without showing it in a dockable
   */
  public AbstractDomainCanvas(ValuationDomain values) {
    super(null);
    this.configuration = new DefaultConfiguration<Node>(Options.canv_gapBetweenLevels, Options.canv_gapBetweenNodes);
    this.setFocus(null);
    this.values = values;
    this.marked = new HashMap<Node, Color>();
    this.markEditable = false;
  }

  @SuppressWarnings("unchecked")
  public Domain<Type> getDomain() {
    return (Domain<Type>) this.values.getDomain();
  }

  public void setTree(NodeTree tree) {
    if (this.tree == null && !this.localExtentProvider) {
      tree.getSharedExtentProvider().registerCanvas(this);
    }
    this.tree = tree;
    this.values.setTreeId(tree.getLayout().getTreeId());
    if (this.isSand()) {
      this.values.treeChanged((SandNode) tree.getRoot(true));
    } else {
      this.values.treeChanged((ADTNode) tree.getRoot(true));
    }
    if (!this.localExtentProvider) {
      Debug.log("before updateTreeSize");
      this.getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
      this.getSharedExtentProvider().notifyTreeChanged();
    } else {
      this.treeChanged();
    }
  }

  public void setScrollPane(JScrollPane pane) {
    this.scrollPane = pane;
    this.scrollPane.addMouseWheelListener(listener);
    this.scrollPane.addComponentListener(listener);
    this.viewPortSize = this.scrollPane.getViewport().getExtentSize();
    this.setScale(1);
  }

  /**
   * Shows the dialog to edit a value in the tree.
   *
   * @param node
   */
  public void editValue(Node n) {
    boolean editable;
    boolean proponent = true;
    Ring value;
    if (n instanceof SandNode) {
      SandNode node = (SandNode) n;
      editable = node.isEditable();
      value = (Ring) this.values.getValue(node);
    }
    else {
      ADTNode node = (ADTNode) n;
      editable = node.isEditable((AdtDomain<Ring>) values.getDomain());
      proponent = node.getRole() == ADTNode.Role.PROPONENT;
      value = (Ring) this.values.getValue(node);
    }
    Ring oldValue = value;
    String key = n.getName();
    if (editable) {
      InputDialog dialog;
      if (value instanceof Bool) {
        value = (Ring) Bool.not((Bool) value);
      }
      else if (value instanceof RealG0) {
        dialog = new RealG0Dialog(controller.getFrame());
        value = (Ring) (dialog.showInputDialog(value));
      }
      else if (value instanceof RealZeroOne) {
        dialog = new RealZeroOneDialog(controller.getFrame());
        value = (Ring) (dialog.showInputDialog(value));
      }
      else if (value instanceof Real) {
        dialog = new RealDialog(controller.getFrame());
        value = (Ring) (dialog.showInputDialog(value));
      }
      else if (value instanceof LMHValue) {
        dialog = new LMHDialog(controller.getFrame());
          value = (Ring) (dialog.showInputDialog(value));
      }
      else if (value instanceof LMHEValue) {
        dialog = new LMHEDialog(controller.getFrame());
        value = (Ring) (dialog.showInputDialog(value));
      }
      else if (value instanceof BoundedInteger) {
        dialog = new BoundedIntegerDialog(controller.getFrame());
        value = (Ring) (dialog.showInputDialog(value));
      }
      else if (value instanceof Int) {
        dialog = new IntDialog(controller.getFrame());
        value = (Ring) (dialog.showInputDialog(value));
      }
      else {
        Debug.log("Unknown value type " + value);
      }
      if (value != null) {
        addEditAction(new SetValuation(value, oldValue, key, proponent, getDomainId()));
        this.values.setValue(proponent, key, value);
        this.valuesUpdated(false);
      }
    }
  }

  public ValuationDomain getValues() {
    return this.values;
  }

  public void unmarkAll() {
    this.marked.clear();
  }

  public void markLabel(String label) {
    this.markLabel(tree.getRoot(true), label);
  }

  public void rankNode(Node node, Ring value) {
    if (node.hasDefault()) {
      this.markNode(node, Options.canv_rankLeafMark);
    } else {
      this.markNode(node, Options.canv_rankNodeMark);
    }
  }

  public void markNode(Node node, Color color) {
    marked.put(node, color);
  }

  /**
   * Determines if this instance is markEditable.
   *
   * @return The markEditable.
   */
  public boolean isMarkEditable() {
    return this.markEditable;
  }

  /**
   * Sets whether or not this instance is markEditable.
   *
   * @param markEditable
   *          The markEditable.
   */
  public void setMarkEditable(boolean markEditable) {
    this.markEditable = markEditable;
    repaint();
  }

  // public boolean isMarked(Node node) {
  // // return marked.contains(node.getName());
  // return false;
  // }

  protected Color getFillColor(Node node) {
    Color c = marked.get(node);
    if (c != null) {
      return c;
    }
    if (markEditable) {
      if (this.isSand()) {
        if (((SandNode) node).isEditable()) {
          return Options.canv_EditableColor;
        }
      } else {
        if (((ADTNode) node).isEditable((AdtDomain<Ring>) values.getDomain())) {
          return Options.canv_EditableColor;
        }
      }
    }
    return Options.canv_FillColorAtt;
  }

  /**
   * {@inheritDoc}
   *
   * @see ADTreeCanvas#getLabel(ADTreeNode)
   */
  public String getLabel(Node n) {
    String result;
    if (n == null) {
      return "Null node";
    }
    if (showLabels) {
      result = n.getName() + "\n";
    } else {
      result = "";
    }
    if (this.values.hasEvaluator()) {
      if (this.isSand()) {
        result = result + this.valueToStr(this.values.getTermValue((SandNode) n));
      } else {
        if (((ADTNode) n).hasDefault() && ((ADTNode) n).isCountered()) {
          if (values.isShowAllLabels()) {
            result = result + this.valueToStr(this.values.getTermValue((ADTNode) n)) + "\n";
          }
          result = result + this.valueToStr(values.getValue((ADTNode) n));
        } else {
          result = result + this.valueToStr(this.values.getTermValue((ADTNode) n));
        }
      }
    }
    return result;
  }

  /**
   * Function called whenever a new value is assigned to leaf node
   */
  public void valuesUpdated(boolean removeOld) {
    if (this.isSand()) {
      if (removeOld) {
        this.values.treeChanged((SandNode) tree.getRoot(true));
      }
      else {
        this.values.valuesUpdated((SandNode) tree.getRoot(true));
      }
    } else {
      if (removeOld) {
        this.values.treeChanged((ADTNode) tree.getRoot(true));
      }
      else {
        this.values.valuesUpdated((ADTNode) tree.getRoot(true));
      }
    }
    if (controller != null) {
      ValuationsDockable valuationsDockable = (ValuationsDockable) controller.getControl()
          .getSingleDockable(ValuationsDockable.ID_VALUATIONVIEW);
      if (valuationsDockable != null && valuationsDockable.getCanvas() == this) {
        valuationsDockable.setCanvas(this);
        controller.getFrame().getRankingView().setCanvas(this);
      }
      RankingDockable rank = controller.getFrame().getRankingView();
      if (rank != null && rank.getCanvas() == this) {
        rank.setFocus(this, this.getFocused(), true);
      }
    }
    tree.getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
    if (!this.localExtentProvider) {
      tree.getSharedExtentProvider().notifyTreeChanged();
    } else {
      this.treeChanged();
    }
  }

  public void setShowLabels(boolean showLabels) {
    boolean notify = false;
    if (this.showLabels != showLabels) {
      notify = true;
    }
    this.showLabels = showLabels;
    if (notify && tree != null) {
      if (this.localExtentProvider) {
        this.treeChanged();
      } else {
        getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
        tree.getSharedExtentProvider().notifyTreeChanged();
      }
    }
  }

  public void setShowAllLabels(boolean showAllLabels) {
    values.setShowAllLabels(showAllLabels);
    if (tree != null) {
      tree.getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
      tree.getSharedExtentProvider().notifyTreeChanged();
    }
  }

  public void repaintAll() {
    DomainFactory factory = getController().getFrame().getDomainFactory();
    factory.repaintAllDomains(new Integer(getTreeId()));
    TreeDockable d = (TreeDockable) getController().getControl().getMultipleDockable(TreeDockable.TREE_ID + getTreeId());
    d.getCanvas().repaint();
  }

  /**
   * Sets whether or not this instance has a local Extent Provider.
   *
   * @param localExtentProvider
   *          The localExtentProvider.
   */
  public void setLocalExtentProvider(boolean localExtentProvider) {
    this.localExtentProvider = localExtentProvider;
    if (localExtentProvider) {
      tree.getSharedExtentProvider().deregisterCanvas(this);
      this.treeChanged();
    } else {
      tree.getSharedExtentProvider().registerCanvas(this);
    }
    getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
    tree.getSharedExtentProvider().notifyTreeChanged();
  }

  public boolean hasLocalExtentProvider() {
    return this.localExtentProvider;
  }

  public boolean getShowLabels() {
    return this.showLabels;
  }

  public int getDomainId() {
    return values.getDomainId();
  }

  @Override
  public int getTreeId() {
    return values.getTreeId();
  }

  public boolean isSand() {
    if (tree != null) {
      return tree.getRoot(true) instanceof SandNode;
    }
    return true;
  }

  private void markLabel(Node node, String label) {
    boolean doMark = false;
    if (node instanceof SandNode) {
      if (node.getName().equals(label) && ((SandNode) node).isLeaf()) {
        doMark = true;
      }
    }
    else {
      if (node.getName().equals(label) && ((ADTNode) node).hasDefault()) {
        doMark = true;
      }
    }
    if (doMark) {
      marked.put(node, Options.canv_LabelMarkColor);
    }
    if (node.getChildren() != null) {
      for (Node child : node.getChildren()) {
        markLabel(child, label);
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * Used by addEditAction - overriden so edit actions are shared by tree canvas
   * and domain canvases.
   *
   */
  public AbstractTreeCanvas getTreeCanvas() {
    TreeDockable currentTree = (TreeDockable) getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(getTreeId()));
    if (currentTree != null) {
      return currentTree.getCanvas();
    }
    return null;
  }

  public void updateTerms() {
    throw new ClassCastException ("Wrong class used");
  }

  private String valueToStr(Ring value) {
    if ((value instanceof Real) && ((Object)this.values.getDomain() instanceof AdtRealDomain)) {
      try {
        return ((AdtRealDomain)(Object) this.values.getDomain()).format((Real)value);
      }
      catch (NumberFormatException e) {
      }
      catch (NullPointerException e) {
      }
    }
    if (value instanceof RealG0 || value instanceof RealZeroOne || value instanceof Real) {
      try {
        return Options.canv_precision.format(Double.parseDouble(value.toUnicode()));
      }
      catch (NumberFormatException e) {
        return value.toUnicode();
      }
    }
    else if (value != null) {
      return value.toUnicode();
    }
    else {
      return "null";
    }
  }

  protected AbstractCanvasHandler listener;
  protected ValuationDomain       values;
  private HashMap<Node, Color>    marked;
  private boolean                 markEditable;
  private boolean                 showLabels;
  private static final long       serialVersionUID = -2360795431357785877L;
}
