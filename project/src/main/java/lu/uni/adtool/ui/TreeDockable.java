package lu.uni.adtool.ui;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.tree.TreeFactory;
import lu.uni.adtool.tree.TreeLayout;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.CDockable;

public class TreeDockable extends DefaultMultipleCDockable implements CVetoClosingListener {
  public static final String TREEVIEW_ID = "_treeView";
  public static final String TREE_ID     = "tree";

  public TreeDockable(TreeFactory factory, TreeLayout layout, boolean preloaded) {
    super(factory);
    this.controller = factory.getController();
    this.setCloseable(true);
    if (layout.isSand()) {
      this.setTitleText("Sand Tree - " + layout.getRoot().getName());
    }
    else {
      this.setTitleText("ADTree - " + layout.getRoot().getName());
    }
    ImageIcon icon = new IconFactory().createImageIcon("/icons/tree_16x16.png", getTitleText());
    this.setTitleIcon(icon);
    DefaultSingleCDockable canvasDockable = createTreeCanvas(layout);
    DefaultSingleCDockable termDockable = createTermDockable();
    int id = this.treeCanvas.getId();
    CControl control = controller.getControl();
    CContentArea center = controller.getFrame().getContentArea(id);
    this.workArea = (CWorkingArea) controller.getControl().getSingleDockable(getWorkAreaId(id));
    if (this.workArea == null) {
      this.workArea = control.createWorkingArea(getWorkAreaId(id));
    }
    CGrid grid2 = new CGrid(control);
    if (!preloaded) {
      grid2.add(0, 0, 1, 1, termDockable);
      grid2.add(0, 1, 1, 1, canvasDockable);
      this.workArea.deploy(grid2);
      grid2 = new CGrid(control);
      grid2.add(0, 0, 1, 1, workArea);
      center.deploy(grid2);
    }
    add(center);
    addVetoClosingListener(this);
  }

  public TreeLayout getLayout() {
    return treeCanvas.getTree().getLayout();
  }

  public AbstractTreeCanvas getCanvas() {
    return treeCanvas;
  }

  public MainController getMainController() {
    return controller;
  }

  public int getId() {
    return this.treeCanvas.getId();
  }

  public static String getUniqueId(int id) {
    return TREE_ID + id;
  }

  public static String getContentId(int id) {
    return getUniqueId(id) + "_content";
  }

  public static String getTermId(int id) {
    return getUniqueId(id) + "_termView";
  }

  public static String getWorkAreaId(int id) {
    return getUniqueId(id) + "_workArea";
  }

  public void removeDomain(DomainDockable dockable) {
    this.getLayout().removeValuation(dockable.getCanvas().getValues());
    this.controller.getFrame().getDomainFactory().removeDomain(dockable);
  }

  /**
   * Called before a set of {@link CDockable}s gets closed. This method may be
   * invoked with events that are already canceled, check the
   * {@link CVetoClosingEvent#isCanceled()} property.
   *
   * @param event
   *          the event that will happen but may be canceled
   */
  public void closing(CVetoClosingEvent event) {
    if (event.isCancelable()) {
      int result = OptionPane.showYNDialog(this.controller.getFrame(),
          Options.getMsg("treeclosedialog.txt"), Options.getMsg("treeclosedialog.title"));
      if (result != JOptionPane.YES_OPTION) {
        event.cancel();
      }
    }
  }

  /**
   * Called after a set of {@link CDockable}s has been closed. This method may
   * be called without {@link #closing(CVetoClosingEvent)} been called
   * beforehand.
   *
   * @param event
   *          the event that has already happened
   */
  public void closed(CVetoClosingEvent event) {
    // do nothing
  }

  public CWorkingArea getWorkArea() {
    return this.workArea;
  }

  public void showDomain(DomainDockable dockable) {
//     this.toFront();
    dockable.setLocation(this.workArea.getDropLocation());
    dockable.setVisible(true);
  }

  public void addDomain(SandDomain domain) {
    DomainFactory factory = controller.getFrame().getDomainFactory();
    DomainDockable d = factory.read(new ValuationDomain(this.treeCanvas.getId(),
        factory.getNewUniqueId(new Integer(this.treeCanvas.getId())), domain));
    d.setWorkingArea(this.workArea);
    Debug.log("Adding domain to control with id:" + d.getUniqueId());
    controller.getControl().addDockable(d.getUniqueId(), d);
    this.showDomain(d);
  }

  public void addDomain(AdtDomain domain) {
    DomainFactory factory = controller.getFrame().getDomainFactory();
    DomainDockable d = factory.read(new ValuationDomain(this.treeCanvas.getId(),
        factory.getNewUniqueId(new Integer(this.treeCanvas.getId())), domain));
    d.setWorkingArea(this.workArea);
    Debug.log("Adding domain to control with id:" + d.getUniqueId());
    controller.getControl().addDockable(d.getUniqueId(), d);
    this.showDomain(d);
  }

  private DefaultSingleCDockable createTreeCanvas(TreeLayout layout) {
    String id = TREE_ID + Integer.toString(layout.getId()) + TREEVIEW_ID;
    DefaultSingleCDockable dockable =
        (DefaultSingleCDockable) controller.getControl().getSingleDockable(id);
    if (dockable == null) {
      dockable = new DefaultSingleCDockable(id, Options.getMsg("window.treeView.txt"));
    }
    else {
      dockable.getContentPane().removeAll();
    }
    if (layout.isSand()) {
      this.treeCanvas = new SandTreeCanvas(new NodeTree(layout), this.controller);
    }
    else {
      this.treeCanvas = new ADTreeCanvas(new NodeTree(layout), this.controller);
    }
    final JScrollPane scrollPane = new JScrollPane(treeCanvas);
    scrollPane.setAutoscrolls(true);
    treeCanvas.setScrollPane(scrollPane);
    dockable.add(scrollPane);
    controller.getControl().addDockable(dockable);
    return dockable;
  }

  private DefaultSingleCDockable createTermDockable() {
    String id =  getTermId(this.treeCanvas.getId());
    DefaultSingleCDockable dockable =
        (DefaultSingleCDockable) controller.getControl().getSingleDockable(id);
    if (dockable == null) {
      dockable = new DefaultSingleCDockable(id, Options.getMsg("window.termView.txt"));
    }
    else {
      dockable.getContentPane().removeAll();
    }
    TermView termView;
    if (treeCanvas instanceof SandTreeCanvas) {
      termView = new TermView((SandTreeCanvas)treeCanvas);
      ((SandTreeCanvas)treeCanvas).setTerms(termView);
    }
    else {
      termView = new TermView((ADTreeCanvas)treeCanvas);
      ((ADTreeCanvas)treeCanvas).setTerms(termView);
    }
    dockable.add(termView);
    controller.getControl().addDockable(dockable);
    return dockable;
  }

  private CWorkingArea   workArea;
  private AbstractTreeCanvas treeCanvas;
  private MainController controller;
}
