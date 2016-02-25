package lu.uni.adtool.ui;

import lu.uni.adtool.ADToolMain;
import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.CCP;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.GuiNode;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.tree.TreeLayout;
import lu.uni.adtool.tree.XmlConverter;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.printview.JPrintPreviewDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.common.event.CFocusListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;

public final class MainController implements CControlListener, CFocusListener {
  public MainController(ADToolMain newFrame, CControl control) {
    this.frame = newFrame;
    this.control = control;
    // this.areas = new HashMap<Integer, TreeWorkArea>();
    mouseHandler = new MouseHandler();
    toDisableItems = new ArrayList<JMenuItem>();
    menu = null; // create it later
    lastFocusedTree = null;
    fh = new FileHandler(this);
    copyHandler = new CCP(control);
  }

  public void focusGained(CDockable dockable) {
    copyHandler.setFocus(dockable);
    AbstractTreeCanvas lf = CCP.getCanvas(dockable, this.control);
    if (lf != null) {
      setLastFocused(lf);
    }
  }

  /**
   * Sets the lastFocused for this instance.
   *
   * @param lastFocused
   *          The lastFocused.
   */
  public void setLastFocused(AbstractTreeCanvas lastFocusedTree) {
    if (this.lastFocusedTree != null && lastFocusedTree != this.lastFocusedTree) {
      this.lastFocusedTree.setFocus(null);
    }
    this.lastFocusedTree = lastFocusedTree;
    for (JMenuItem item : toDisableItems) {
      item.setEnabled(lastFocusedTree != null);
    }
    this.getFrame().getValuationsView().setCanvas(lastFocusedTree);
    this.getFrame().getDetailsView().assignCanvas(lastFocusedTree);
    if (lastFocusedTree != null) {
      this.getFrame().getRankingView().setFocus(lastFocusedTree, lastFocusedTree.getFocused(),
          false);
    }
    else {
      this.getFrame().getRankingView().setFocus(lastFocusedTree, null, false);
    }
    // ((ValuationView) views[2].getComponent()).assignCanvas(lastFocused);
    // ((DetailsView) views[3].getComponent()).assignCanvas(lastFocused);
    // }
  }

  public void focusLost(CDockable dockable) {
    // do nothing atm.
  }

  /**
   * Called when <code>dockable</code> has been made public.
   *
   * @param control
   *          the caller
   * @param dockable
   *          the element that is now known
   */

  public void added(CControl control, CDockable dockable) {
    if (dockable instanceof DefaultSingleCDockable) {
      // dockable).getUniqueId());
    }

  }

  /**
   * Called when <code>dockable</code> has been removed.
   *
   * @param control
   *          the caller
   * @param dockable
   *          the element that is no longer known
   */
  public void removed(CControl control, CDockable dockable) {
    if (dockable instanceof TreeDockable && lastFocusedTree instanceof AbstractTreeCanvas) {
      if (((TreeDockable) dockable).getId() == lastFocusedTree.getId()) {
        setLastFocused(null);
      }
    }
    else if (dockable instanceof PermaDockable) {
      // LogView/ValuationsView - ignore
    }
    else if (dockable instanceof DomainDockable) {
      this.removeDomain((DomainDockable) dockable);
    }
    else if (dockable instanceof DefaultSingleCDockable) {
      String uid = ((DefaultSingleCDockable) dockable).getUniqueId();
      if (lastFocusedTree != null && uid.equals(TreeDockable.TREE_ID
          + Integer.toString(lastFocusedTree.getId()) + TreeDockable.TREEVIEW_ID)) {
        setLastFocused(null);
      }
    }
    else if (dockable instanceof DefaultMultipleCDockable) {
      // TODO - handle domains
    }
  }

  /**
   * Called when <code>dockable</code> has been made visible.
   *
   * @param control
   *          the caller
   * @param dockable
   *          the element that is now visible
   * @see CDockable#isVisible()
   */
  public void opened(CControl control, CDockable dockable) {
    if (dockable instanceof DefaultSingleCDockable) {
    }
  }

  /**
   * Called when <code>dockable</code> has been made invisible.
   *
   * @param control
   *          the caller
   * @param dockable
   *          the element that is no longer visible
   * @see CDockable#isVisible()
   */
  public void closed(CControl control, CDockable dockable) {
    if (dockable instanceof TreeDockable && lastFocusedTree instanceof AbstractTreeCanvas) {
      if (((TreeDockable) dockable).getId() == lastFocusedTree.getId()) {
        setLastFocused(null);
      }
    }
    else if (dockable instanceof PermaDockable) {
      // LogView - ignore
    }
    else if (dockable instanceof DomainDockable) {
      this.removeDomain((DomainDockable) dockable);
    }
    else if (dockable instanceof DefaultSingleCDockable) {
      String uid = ((DefaultSingleCDockable) dockable).getUniqueId();
      if (lastFocusedTree != null && uid.equals(TreeDockable.TREE_ID
          + Integer.toString(lastFocusedTree.getId()) + TreeDockable.TREEVIEW_ID)) {
        setLastFocused(null);
      }
    }
  }

  /**
   * @return the menu
   */
  public JMenuBar getMenu() {
    if (menu == null) {
      menu = createMenuBar();
    }
    return menu;
  }

  public CControl getControl() {
    return this.control;
  }

  public void addTreeDockable(TreeDockable treeDockable) {
    control.addDockable(TreeDockable.getUniqueId(treeDockable.getId()), treeDockable);
    treeDockable.setWorkingArea(frame.getWorkArea());
    treeDockable.setVisible(true);
    DefaultSingleCDockable dock = (DefaultSingleCDockable) control.getSingleDockable(
        TreeDockable.getUniqueId(treeDockable.getId()) + TreeDockable.TREEVIEW_ID);
    if (dock != null) {
      dock.toFront();
    }
    else {
      treeDockable.toFront();
    }
    DomainFactory factory = getFrame().getDomainFactory();
    if (treeDockable.getLayout().getDomains() != null) {
      for (ValuationDomain values : treeDockable.getLayout().getDomains()) {
        DomainDockable d = factory.read(values);
        d.setWorkingArea(treeDockable.getWorkArea());
        Debug.log("Adding domain to control with id:" + d.getUniqueId());
        getControl().addDockable(d.getUniqueId(), d);
        treeDockable.showDomain(d);
        d.getCanvas().setTree(treeDockable.getCanvas().getTree());
      }
    }
  }

  private JMenuBar createMenuBar() {
    createActions();
    final JMenuBar menu = new JMenuBar();
    menu.add(createFileMenu());
    menu.add(createEditMenu());
    menu.add(createViewMenu());
    menu.add(createWindowsMenu());
    menu.add(createHelpMenu());
    return menu;
  }

  public void report(String message) {
    this.frame.getStatusBar().report(message);
  }

  public void reportError(String message) {
    this.frame.getStatusBar().reportError(message);
  }

  public void reportWarning(String message) {
    this.frame.getStatusBar().reportWarning(message);
  }

  public ADToolMain getFrame() {
    return frame;
  }

  public AbstractTreeCanvas getLastFocusedTree() {
    return lastFocusedTree;
  }

  // public TreeWorkArea getWorkArea(int id) {
  // TreeWorkArea workArea = this.areas.get(new Integer(id));
  // if (workArea == null) {
  // workArea = new TreeWorkArea(this, SandTreeDockable.TREE_ID + id +
  // "_workArea");
  // this.getControl().addDockable(workArea.getWorkArea());
  // this.getControl().addStationContainer(workArea);
  // this.areas.put(new Integer(id), workArea);
  // }
  // return workArea;
  // }

  /**
   * Creates the file menu.
   *
   * @return the file menu
   */
  private JMenu createFileMenu() {
    JMenuItem menuItem;
    final JMenu fileMenu = new JMenu();
    fileMenu.setText(Options.getMsg("file.txt"));
    fileMenu.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.key")).getKeyCode());
    menuItem = fileMenu.add(fileNewSand);
    menuItem.addMouseListener(mouseHandler);
    menuItem = fileMenu.add(fileNewADT);
    menuItem.addMouseListener(mouseHandler);

    ADAction fileExample = new ADAction(Options.getMsg("file.loadExample.txt")) {
      public void actionPerformed(final ActionEvent e) {
        loadExample();
      }

      private static final long serialVersionUID = -4300803966363076614L;
    };
    fileExample.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.loadExample.key")));
    fileExample.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.loadExample.acc")));
    IconFactory iconFac = new IconFactory();
    fileExample.setSmallIcon(iconFac.createImageIcon("/icons/forest.png"));
    menuItem = fileMenu.add(fileExample);
    menuItem.addMouseListener(mouseHandler);

    // fileMenu.add(menuItem);
    JMenu exportTo = new JMenu(Options.getMsg("file.export.txt"));
    exportTo.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.key")).getKeyCode());
    menuItem = exportTo.add(fileExportToPdf);
    menuItem.addMouseListener(mouseHandler);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    // exportTo.add(menuItem);
    // menuItem = exportTo.add(fileExportToLatex);
    // menuItem.addMouseListener(mouseHandler);
    // exportTo.add(menuItem);
    menuItem = exportTo.add(fileExportToPng);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    exportTo.add(menuItem);
    menuItem = exportTo.add(fileExportToJpg);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    exportTo.add(menuItem);
    menuItem = exportTo.add(fileExportToTxt);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    exportTo.add(menuItem);
    menuItem = exportTo.add(fileExportToXml);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    exportTo.add(menuItem);
    fileMenu.add(exportTo);

    JMenu importFrom = new JMenu(Options.getMsg("file.import.txt"));
    importFrom.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.import.key")).getKeyCode());
    menuItem = importFrom.add(fileImportFromXml);
    menuItem.addMouseListener(mouseHandler);
    importFrom.add(menuItem);
    fileMenu.add(importFrom);

    menuItem = fileMenu.add(filePrint);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem = fileMenu.add(filePrintPreview);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    fileMenu.addSeparator();
    menuItem = fileMenu.add(fileExit);
    menuItem.addMouseListener(mouseHandler);
    return fileMenu;

  }

  /**
   * Creates the windows menu where not shown views can be shown.
   *
   * @return the menu menu
   */
  private JMenu createEditMenu() {
    JMenuItem menuItem;
    final JMenu editMenu = new JMenu();
    editMenu.setText(Options.getMsg("edit.txt"));
    editMenu.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.key")).getKeyCode());
    menuItem = new JMenuItem(Options.getMsg("edit.adddomain.txt"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chooseDomain();
      }
    });
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.adddomain.key")).getKeyCode());
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    editMenu.add(menuItem);
    menuItem = new JMenuItem(Options.getMsg("edit.switchRole.txt"));
    menuItem
        .setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.switchRole.key")).getKeyCode());
    menuItem.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        if (lastFocusedTree != null) {
          lastFocusedTree.getTree().getLayout().toggleRole();
          report(Options.getMsg("edit.switchRole.report",
              (lastFocusedTree.getTree().getLayout().getSwitchRole()
                  ? Options.getMsg("tablemodel.opponent").toLowerCase()
                  : Options.getMsg("tablemodel.proponent").toLowerCase())));
          lastFocusedTree.notifyAllTreeChanged();
        }
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.switchRole.acc")));
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    editMenu.add(menuItem);
    editMenu.addSeparator();
    menuItem = editMenu.add(editCut);
    menuItem.addMouseListener(mouseHandler);
    editMenu.add(menuItem);
    menuItem = editMenu.add(editCopy);
    menuItem.addMouseListener(mouseHandler);
    editMenu.add(menuItem);
    menuItem = editMenu.add(editPaste);
    menuItem.addMouseListener(mouseHandler);
    editMenu.add(menuItem);
    return editMenu;
  }

  private void printCanvas() {
    if (lastFocusedTree != null) {
      lastFocusedTree.showPrintDialog(true);
    }
  }

  private void printPreview() {
    if (lastFocusedTree != null) {
      JPrintPreviewDialog pp = new JPrintPreviewDialog(this, lastFocusedTree);
      pp.setVisible(true);
    }
  }

  /**
   * Creates the windows menu where not shown views can be shown.
   *
   * @return the windows menu
   */
  private JMenu createWindowsMenu() {
    JMenu windowsMenu = new JMenu(Options.getMsg("windows.txt"));
    windowsMenu.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.key")).getKeyCode());
    JMenuItem menuItem = this.frame.getStatusBar().getLogViewDockable().createMenuItem();
    menuItem
        .setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.messageLog.key")).getKeyCode());
    windowsMenu.add(menuItem);
    menuItem = this.frame.getValuationsView().createMenuItem();
    menuItem
        .setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.valuations.key")).getKeyCode());
    windowsMenu.add(menuItem);

    menuItem = this.frame.getRankingView().createMenuItem();
    menuItem
        .setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.ranking.key")).getKeyCode());
    windowsMenu.add(menuItem);

    menuItem = this.frame.getDetailsView().createMenuItem();
    menuItem
        .setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.details.key")).getKeyCode());
    windowsMenu.add(menuItem);

    return windowsMenu;
  }

  /**
   * Creates the help menu
   *
   * @return the help menu
   */
  private JMenu createHelpMenu() {
    JMenuItem menuItem;
    JMenu menu = new JMenu(Options.getMsg("help.txt"));
    menu.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("help.key")).getKeyCode());
    menuItem = new JMenuItem(Options.getMsg("help.about.txt"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        AboutDialog ad = new AboutDialog(getFrame());
        ad.setVisible(true);
      }
    });
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("help.about.key")).getKeyCode());
    menu.add(menuItem);
    return menu;
  }

  /**
   * Create view menu.
   *
   * @return
   */
  private JMenu createViewMenu() {
    IconFactory iconFac = new IconFactory();
    JMenu viewMenu = new JMenu(Options.getMsg("view.txt"));
    viewMenu.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("view.key")).getKeyCode());
    JMenu themesMenu = new JMenu(Options.getMsg("view.themes.txt"));
    themesMenu.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("view.themes.key")).getKeyCode());
    themesMenu.setIcon(iconFac.createImageIcon("/icons/themes_16x16.png"));
    // RootMenuPiece layout = new RootMenuPiece( "Layout", false );
    //
    // layout.add(new CLayoutChoiceMenuPiece( control, true ));
    // viewMenu.add(layout.getMenu());

    ButtonGroup group = new ButtonGroup();
    JRadioButtonMenuItem item =
        new JRadioButtonMenuItem(Options.getMsg("view.themes.basicTheme.txt"));
    item.setSelected(false);
    themesMenu.add(item).addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getControl().setTheme(ThemeMap.KEY_BASIC_THEME);
      }
    });
    group.add(item);

    item = new JRadioButtonMenuItem(Options.getMsg("view.themes.bubbleTheme.txt"));
    item.setSelected(false);
    themesMenu.add(item).addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getControl().setTheme(ThemeMap.KEY_BUBBLE_THEME);
      }
    });
    group.add(item);
    item = new JRadioButtonMenuItem(Options.getMsg("view.themes.smoothTheme.txt"));
    item.setSelected(false);
    themesMenu.add(item).addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getControl().setTheme(ThemeMap.KEY_SMOOTH_THEME);
      }
    });
    group.add(item);

    item = new JRadioButtonMenuItem(Options.getMsg("view.themes.flatTheme.txt"));
    item.setSelected(false);
    themesMenu.add(item).addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getControl().setTheme(ThemeMap.KEY_FLAT_THEME);
      }
    });
    group.add(item);
    item = new JRadioButtonMenuItem(Options.getMsg("view.themes.eclipseTheme.txt"));
    item.setSelected(true);
    themesMenu.add(item).addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getControl().setTheme(ThemeMap.KEY_ECLIPSE_THEME);
      }
    });
    group.add(item);
    viewMenu.add(themesMenu);
    JMenuItem menuItem = new JMenuItem(Options.getMsg("view.fitToWindow.txt"));
    menuItem.setIcon(iconFac.createImageIcon("/icons/fit_16x16.png"));
    this.toDisableItems.add(menuItem);
    menuItem.setEnabled(false);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (lastFocusedTree != null) {
          lastFocusedTree.fitToWindow();
        }
      }
    });
    menuItem
        .setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("view.fitToWindow.key")).getKeyCode());
    viewMenu.add(menuItem);
    return viewMenu;
  }

  /**
   * Create all actions used in application.
   */
  private void createActions() {
    IconFactory iconFac = new IconFactory();
    fileNewSand = new ADAction(Options.getMsg("file.newSand.txt")) {
      public void actionPerformed(final ActionEvent e) {
        final TreeLayout layout =
            new TreeLayout(frame.getTreeFactory().getNewUniqueId(), new SandNode());
        final TreeDockable treeDockable = new TreeDockable(frame.getTreeFactory(), layout, false);
        addTreeDockable(treeDockable);
        report(Options.getMsg("status.newSandTree"));
      }

      private static final long serialVersionUID = 1555040302346025737L;
    };
    fileNewSand.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.newSand.key")));
    fileNewSand.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.newSand.acc")));
    fileNewSand.setSmallIcon(iconFac.createImageIcon("/icons/new.png"));
    fileNewSand.setToolTip(Options.getMsg("file.newSand.tooltip"));

    fileNewADT = new ADAction(Options.getMsg("file.newADT.txt")) {
      public void actionPerformed(final ActionEvent e) {
        TreeLayout layout = new TreeLayout(frame.getTreeFactory().getNewUniqueId(), new ADTNode());
        final TreeDockable treeDockable = new TreeDockable(frame.getTreeFactory(), layout, false);
        addTreeDockable(treeDockable);
        report(Options.getMsg("status.newADTree"));
      }

      private static final long serialVersionUID = 1555040302346025737L;
    };
    fileNewADT.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.newADT.key")));
    fileNewADT.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.newADT.acc")));
    fileNewADT.setSmallIcon(iconFac.createImageIcon("/icons/new.png"));
    fileNewADT.setToolTip(Options.getMsg("file.newADT.tooltip"));

    fileExportToPdf = new ADAction(Options.getMsg("file.export.pdf.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("pdf");
      }

      private static final long serialVersionUID = 4325025687838671271L;
    };
    fileExportToPdf.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.pdf.key")));
    fileExportToPdf.setSmallIcon(iconFac.createImageIcon("/icons/pdf_16x16.png"));

    fileExportToPng = new ADAction(Options.getMsg("file.export.png.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("png");
      }

      private static final long serialVersionUID = 2398600083840742200L;
    };
    fileExportToPng.setSmallIcon(iconFac.createImageIcon("/icons/png_16x16.png"));
    fileExportToPng.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.png.key")));

    fileExportToJpg = new ADAction(Options.getMsg("file.export.jpeg.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("jpg");
      }

      private static final long serialVersionUID = 8409590777160375107L;
    };
    fileExportToJpg.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.jpeg.key")));
    // fileExportToPng.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
    // InputEvent.ALT_MASK));
    fileExportToJpg.setSmallIcon(iconFac.createImageIcon("/icons/jpg_16x16.png"));

    fileExportToTxt = new ADAction(Options.getMsg("file.export.txt.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("txt");
      }

      private static final long serialVersionUID = 8409590777160375107L;
    };
    fileExportToTxt.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.txt.key")));
    // fileExportToPng.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
    // InputEvent.ALT_MASK));
    fileExportToTxt.setSmallIcon(iconFac.createImageIcon("/icons/txt_16x16.png"));

    fileExportToXml = new ADAction(Options.getMsg("file.export.xml.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("xml");
      }

      private static final long serialVersionUID = 8409590777160375107L;
    };
    fileExportToXml.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.xml.key")));
    // fileExportToPng.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
    // InputEvent.ALT_MASK));
    fileExportToXml.setSmallIcon(iconFac.createImageIcon("/icons/xml_16x16.png"));

    fileImportFromXml = new ADAction(Options.getMsg("file.export.xml.txt")) {
      private static final long serialVersionUID = -3605440604743377670L;

      public void actionPerformed(final ActionEvent e) {
        importFrom("xml");
      }
    };
    fileImportFromXml.setSmallIcon(iconFac.createImageIcon("/icons/xml_16x16.png"));
    fileImportFromXml.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.xml.key")));

    filePrint = new ADAction(Options.getMsg("file.print.txt")) {
      public void actionPerformed(final ActionEvent e) {
        printCanvas();
      }

      private static final long serialVersionUID = 7365498990462507356L;
    };
    filePrint.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.print.key")));
    // filePrintPreview.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.print.acc")));
    filePrint.setSmallIcon(iconFac.createImageIcon("/icons/print.png"));

    filePrintPreview = new ADAction(Options.getMsg("file.printPreview.txt")) {
      public void actionPerformed(final ActionEvent e) {
        printPreview();
      }

      private static final long serialVersionUID = -8710097506678812443L;
    };
    filePrintPreview.setSmallIcon(iconFac.createImageIcon("/icons/preview.png"));
    filePrintPreview.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.printPreview.key")));
    filePrintPreview
        .setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.printPreview.acc")));

    fileExit = new ADAction(Options.getMsg("file.exit.txt")) {
      public void actionPerformed(final ActionEvent e) {
        WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
        frame.dispatchEvent(windowClosing);
      }

      private static final long serialVersionUID = 1566817922515699697L;
    };
    fileExit.setToolTip(Options.getMsg("file.exit.tooltip"));
    fileExit.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.exit.key")));
    fileExit.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.exit.acc")));
    fileExit.setSmallIcon(iconFac.createImageIcon("/icons/exit.png"));

    editCut = new ADAction(Options.getMsg("edit.cut.txt")) {
      public void actionPerformed(final ActionEvent e) {
        copyHandler.cut();
      }
    };
    editCut.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.cut.key")));
    editCut.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.cut.acc")));
    editCut.setSmallIcon(iconFac.createImageIcon("/icons/cut.png"));
    editPaste = new ADAction(Options.getMsg("edit.paste.txt")) {
      public void actionPerformed(final ActionEvent e) {
        copyHandler.paste();
      }
    };
    editPaste.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.paste.key")));
    editPaste.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.paste.acc")));
    editPaste.setSmallIcon(iconFac.createImageIcon("/icons/paste.png"));
    editCopy = new ADAction(Options.getMsg("edit.copy.txt")) {
      public void actionPerformed(final ActionEvent e) {
        copyHandler.copy();
      }
    };
    editCopy.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.copy.key")));
    editCopy.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.copy.acc")));
    editCopy.setSmallIcon(iconFac.createImageIcon("/icons/copy.png"));

  }

  private void removeDomain(DomainDockable dockable) {
    int id = dockable.getCanvas().getId();
    if (lastFocusedTree != null && id == lastFocusedTree.getId()) {
      setLastFocused(null);
    }
    TreeDockable d =
        (TreeDockable) control.getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(id));
    if (d != null) {
      d.removeDomain(dockable);
    }
    else {
      System.err.println("No window with id:" + id);
    }
  }

  private void chooseDomain() {
    if (lastFocusedTree != null) {
      // int pos = lastFocusedTree.getUniqueId().indexOf("_");
      // if (pos < 5) return;
      // String id = lastFocusedTree.getUniqueId().substring(0, pos);
      if (lastFocusedTree.isSand()) {
        Vector<Domain<?>> domains = DomainFactory.getPredefinedDomains(true);
        AddSandDomainDialog addDialog = new AddSandDomainDialog(this.frame);
        SandDomain<?> d = addDialog.showDomainDialog(domains);
        if (d == null) {
          return;
        }
        TreeDockable currentTree = (TreeDockable) this.control
            .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getId()));
        if (currentTree != null) {
          currentTree.addDomain(d);
          this.report(Options.getMsg("status.newdomain") + " " + d.getName());
        }
      }
      else {
        Vector<Domain<?>> domains = DomainFactory.getPredefinedDomains(false);
        Debug.log(" domains size:" + domains.size());
        AddAdtDomainDialog addDialog = new AddAdtDomainDialog(this.frame);
        AdtDomain<?> d = addDialog.showDomainDialog(domains);
        if (d == null) {
          return;
        }
        TreeDockable currentTree = (TreeDockable) this.control
            .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getId()));
        if (currentTree != null) {
          currentTree.addDomain(d);
          this.report(Options.getMsg("status.newdomain") + " " + d.getName());
        }
      }
    }
    else
      return;
  }

  private void exportTo(String type) {
    if (lastFocusedTree == null) {
      return;
    }
    GuiNode tempFocus = lastFocusedTree.getFocused();
    lastFocusedTree.setFocus(null);
    FileOutputStream out = fh.getExportTreeStream(type, lastFocusedTree);
    if (out != null) {
      if (type.equals("pdf")) {
        lastFocusedTree.createPdf(out);
      }
      // else if (type.equals("tex")) {
      // canvas.createLatex(out);
      // }
      else if (type.equals("png") || type.equals("jpg")) {
        lastFocusedTree.createImage(out, type);
      }
      else if (type.equals("txt")) {
        TreeDockable currentTree = (TreeDockable) this.control
            .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getId()));
        if (currentTree != null) {
          try {
            currentTree.getCanvas().createTxt(out);
          }
          catch (IOException e) {
            reportError(e.getLocalizedMessage());
          }
        }
      }
      else if (type.equals("xml")) {
        lastFocusedTree.createXml(out);
      }
      // }
      lastFocusedTree.setFocus(tempFocus);
    }
  }

  private void importFrom(String type) {
    FileInputStream in = fh.getImportTreeStream(type);
    if (in != null) {
      if (type.equals("xml")) {
        XmlConverter converter = new XmlConverter();
        try {
          converter.importFrom(in, this);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else if (type.equals("txt")) {
      }

    }
  }

  private void loadExample() {
    LoadExampleDialog dialog = new LoadExampleDialog(getFrame());
    String fileName = dialog.showDialog();
    if (fileName != null) {
      try {
        URL url = this.getClass().getResource(fileName);
        InputStream in = url.openStream();
        XmlConverter converter = new XmlConverter();
        converter.importFrom(in, this);
        this.report(Options.getMsg("example.loaded"));
      }
      catch (IOException e) {
        this.reportError(e.getMessage());
      }
    }
  }

  /**
   * This adapter is constructed to handle mouse over component events.
   */
  private static class MouseHandler extends MouseAdapter {

    /**
     * ctor for the adapter.
     *
     * @param label
     *          the JLabel which will recieve value of the
     *          Action.LONG_DESCRIPTION key.
     */
    public MouseHandler() {
    }

    public void mouseEntered(MouseEvent evt) {
    }
  }

  private JMenuBar             menu;
  private ADToolMain           frame;
  private MouseHandler         mouseHandler;
  private ArrayList<JMenuItem> toDisableItems;
  private static ADAction      fileNewSand;
  private static ADAction      fileNewADT;
  private static ADAction      fileOpen;
  private static ADAction      fileSave;
  private static ADAction      fileExportToPdf;
  private static ADAction      fileExportToPng;
  private static ADAction      fileExportToJpg;
  private static ADAction      fileExportToTxt;
  private static ADAction      fileExportToXml;
  private static ADAction      filePrint;
  private static ADAction      filePrintPreview;
  private static ADAction      fileExit;
  private static ADAction      fileImportFromXml;
  private static ADAction      editCopy;
  private static ADAction      editCut;
  private static ADAction      editPaste;

  private AbstractTreeCanvas   lastFocusedTree;

  /**
   * Class for handling displaying dialogs for saving/loading files.
   */
  private FileHandler          fh;

  private CControl             control;
  private CCP                  copyHandler;
}
