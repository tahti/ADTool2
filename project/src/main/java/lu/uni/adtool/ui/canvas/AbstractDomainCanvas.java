package lu.uni.adtool.ui.canvas;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Bool;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.domains.rings.LMHValue;
import lu.uni.adtool.domains.rings.RealG0;
import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
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
import lu.uni.adtool.ui.inputdialogs.LMHDialog;
import lu.uni.adtool.ui.inputdialogs.LMHEDialog;
import lu.uni.adtool.ui.inputdialogs.RealG0Dialog;
import lu.uni.adtool.ui.inputdialogs.RealZeroOneDialog;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JScrollPane;

import org.abego.treelayout.util.DefaultConfiguration;

public class AbstractDomainCanvas<Type extends Ring> extends AbstractTreeCanvas {
  public AbstractDomainCanvas(MainController mc, ValuationDomain values) {
    super(null, mc);
    if (values.getDomain() instanceof SandDomain) {
      this.listener = new SandDomainHandler<Type>(this);
    }
    else {
      this.listener = new AdtDomainHandler<Type>(this);
    }
    this.addMouseListener(listener);
    this.addMouseMotionListener(listener);
    this.addKeyListener(listener);
    this.configuration =
        new DefaultConfiguration<Node>(Options.canv_gapBetweenLevels, Options.canv_gapBetweenNodes);
    this.setFocus(null);
    this.values = values;
    this.marked = new HashMap<Node, Color>();
    this.markEditable = true;
  }

  public Domain<Type> getDomain() {
    return (Domain<Type>) this.values.getDomain();
  }

  public void setTree(NodeTree tree) {
    if (this.tree == null && !this.localExtentProvider) {
      tree.getSharedExtentProvider().registerCanvas(this);
    }
    this.tree = tree;
    this.values.setTreeId(tree.getLayout().getId());
    if (this.isSand()) {
      this.values.treeChanged((SandNode) tree.getRoot(true));
    }
    else {
      this.values.treeChanged((ADTNode) tree.getRoot(true));
    }
    if (!this.localExtentProvider) {
      Debug.log("before updateTreeSize");
      this.getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
      this.getSharedExtentProvider().notifyTreeChanged();
    }
    else {
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
    if (n instanceof SandNode) {
      SandNode node = (SandNode) n;
      if (node.isEditable()) {
        InputDialog dialog;
        Ring value = (Ring) this.values.getValue(node);
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
        else if (value instanceof LMHValue) {
          dialog = new LMHDialog(controller.getFrame());
          value = (Ring) (dialog.showInputDialog(value));
        }
        else if (value instanceof LMHEValue) {
          dialog = new LMHEDialog(controller.getFrame());
          value = (Ring) (dialog.showInputDialog(value));
        }
        else if (value instanceof BoundedInteger) {
          // if(getDomain() instanceof MinSkill){
          // dialog = new BoundedIntegerInfDialog(getMainWindow());
          // }
          // else{
          dialog = new BoundedIntegerDialog(controller.getFrame());
          // }
          value = (Ring) (dialog.showInputDialog(value));
        }
        else {
          Debug.log("Unknown value type " + value);
        }
        if (value != null) {
          String key = node.getName();
          putNewValue(key, value);
          this.valuesUpdated();
        }
      }
    }
    else if (n instanceof ADTNode) {
      ADTNode node = (ADTNode) n;
      if (node.isEditable((AdtDomain) values.getDomain())) {
        InputDialog dialog;
        Ring value = (Ring) this.values.getValue(node);
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
        else if (value instanceof LMHValue) {
          dialog = new LMHDialog(controller.getFrame());
          value = (Ring) (dialog.showInputDialog(value));
        }
        else if (value instanceof LMHEValue) {
          dialog = new LMHEDialog(controller.getFrame());
          value = (Ring) (dialog.showInputDialog(value));
        }
        else if (value instanceof BoundedInteger) {
          // if(getDomain() instanceof MinSkill){
          // dialog = new BoundedIntegerInfDialog(getMainWindow());
          // }
          // else{
          dialog = new BoundedIntegerDialog(controller.getFrame());
          // }
          value = (Ring) (dialog.showInputDialog(value));
        }
        else {
          Debug.log("Unknown value type " + value);
        }
        if (value != null) {
          String key = node.getName();
          putNewValue(key, value);
          this.valuesUpdated();
        }
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

  public void markNode(Node node, Color color) {
    marked.put(node, color);
  }

  /**
   * Assigns a new value to a node.
   *
   * @param key
   */
  public void putNewValue(String key, Ring value) {
    this.values.setValue(key, value);
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

//   public boolean isMarked(Node node) {
//     // return marked.contains(node.getName());
//     return false;
//   }

  protected Color getFillColor(Node node) {
    Color c = marked.get(node);
    if (c != null) {
      return c;
    }
    if (markEditable) {
      if (this.isSand()){
        if (((SandNode) node).isEditable()) {
          return Options.canv_EditableColor;
        }
      }
      else {
        if (((ADTNode) node).isEditable((AdtDomain)values.getDomain())) {
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
    @SuppressWarnings("unchecked")
    String result;
    if (n == null) {
      return "Null node";
    }
    if (showLabels) {
      result = n.getName() + "\n";
    }
    else {
      result = "";
    }
    Ring value = null;
    if (this.values.hasEvaluator()) {
      if (this.isSand()) {
        value = this.values.getTermValue((SandNode) n);
      }
      else {
        value = this.values.getTermValue((ADTNode) n);
      }
      result += value.toUnicode();
    }
    return result;
  }

  /**
   * Function called whenever a new value is assigned to leaf node
   */
  public void valuesUpdated() {
    if (this.isSand()) {
      this.values.valuesUpdated((SandNode) tree.getRoot(true));
    }
    else {
      this.values.valuesUpdated((ADTNode) tree.getRoot(true));
    }
    ValuationsDockable valuationsDockable = (ValuationsDockable) controller.getControl()
        .getSingleDockable(ValuationsDockable.ID_VALUATIONVIEW);
    if (valuationsDockable != null && valuationsDockable.getCanvas() == this) {
      valuationsDockable.setCanvas(this);
      controller.getFrame().getRankingView().setCanvas(this);
    }
    RankingDockable rank = controller.getFrame().getRankingView();
    if (rank != null && rank.getCanvas() == this) {
      Debug.log("updating rank");
      rank.setFocus(this, this.getFocused(), true);
    }
    else{
      Debug.log("NOT updating rank");
    }
    Debug.log("before updateTreeSize");
    tree.getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
    if (!this.localExtentProvider) {
      tree.getSharedExtentProvider().notifyTreeChanged();
    }
    else {
      this.treeChanged();
    }
  }

  public void setShowLabels(boolean showLabels) {
    boolean notify = false;
    if (this.showLabels != showLabels) {
      notify = true;
    }
    this.showLabels = showLabels;
    if (notify) {
      if (this.localExtentProvider) {
        this.treeChanged();
      }
      else {
        Debug.log("before updateTreeSize");
        getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
        tree.getSharedExtentProvider().notifyTreeChanged();
      }
    }
  }

  // public void setShowAllLabels(boolean showAllLabels) {
  // values.setShowAllLabels(showAllLabels);
  // tree.notifyTreeChanged(TreeChangeListener.Change.LABEL_CHANGE);
  // }

  public void repaintAll() {
    DomainFactory factory = getController().getFrame().getDomainFactory();
    factory.repaintAllDomains(new Integer(getId()));
    TreeDockable d = (TreeDockable) getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + getId());
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
    }
    else {
      tree.getSharedExtentProvider().registerCanvas(this);
    }
    getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
    tree.getSharedExtentProvider().notifyTreeChanged();
  }

  public boolean getShowLabels() {
    return this.showLabels;
  }

  public int getDomainId() {
    return values.getDomainId();
  }

  @Override
  public int getId() {
    return values.getTreeId();
  }

  public boolean isSand() {
    return tree.getRoot(true) instanceof SandNode;
  }

  private void markLabel(Node node, String label) {
    boolean doMark = false;
    if (node instanceof SandNode) {
      if (node.getName().equals(label) && ((SandNode)node).isLeaf()) {
        doMark = true;
      }
    }
    else {
      if (node.getName().equals(label) && ((ADTNode)node).hasDefault()) {
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

  protected AbstractCanvasHandler listener;
  protected ValuationDomain       values;
  private HashMap<Node, Color>    marked;
  private boolean                 markEditable;
  private boolean                 showLabels;
}
