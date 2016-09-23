/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>) Date:
 * 10/12/2015 Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty
 * of Science, Technology and Communication FSTC All rights reserved. Licensed
 * under GNU Affero General Public License 3.0; This program is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.ui;

import lu.uni.adtool.ADToolMain;
import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.Parametrized;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.custom.AdtBoolDomain;
import lu.uni.adtool.domains.custom.AdtCustomDomain;
import lu.uni.adtool.domains.custom.AdtIntDomain;
import lu.uni.adtool.domains.custom.AdtRealDomain;
import lu.uni.adtool.domains.custom.SandBoolDomain;
import lu.uni.adtool.domains.custom.SandCustomDomain;
import lu.uni.adtool.domains.custom.SandIntDomain;
import lu.uni.adtool.domains.custom.SandRealDomain;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Objects;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tools.undo.EditAdtDomain;
import lu.uni.adtool.tools.undo.EditSandDomain;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.AdtImporter;
import lu.uni.adtool.tree.CCP;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.GuiNode;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.tree.TreeLayout;
import lu.uni.adtool.tree.TxtImporter;
import lu.uni.adtool.tree.XmlConverter;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.inputdialogs.BoundedIntegerDialog;
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
import java.util.HashSet;
import java.util.Set;
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
    this.treeDockables = new HashSet<Integer>();
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
      this.getFrame().getRankingView().setFocus(lastFocusedTree, lastFocusedTree.getFocused(), false);
    }
    else {
      this.getFrame().getRankingView().setFocus(lastFocusedTree, null, false);
    }
    if (this.lastFocusedTree != null) {
      lastFocusedTree.updateUndoRedoItems();
    }
    else {
      editUndo.setEnabled(false);
      editUndo.setName(Options.getMsg("edit.undo.txt"));
      editRedo.setEnabled(false);
      editRedo.setName(Options.getMsg("edit.redo.txt"));
    }
    if (lastFocusedTree instanceof AbstractDomainCanvas) {
      AbstractDomainCanvas<Ring> c = Objects.cast(lastFocusedTree);
      if (c.getDomain() instanceof AdtCustomDomain || c.getDomain() instanceof SandCustomDomain) {
        editEditDomain.setEnabled(true);
      }
    }
    else {
      editEditDomain.setEnabled(false);
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
//     if (dockable instanceof DefaultSingleCDockable) {
//       Debug.log("added dockable with id:"+ ((DefaultSingleCDockable)dockable).getUniqueId());
//     }
//     else {
//       Debug.log("other dockable added"+dockable.toString());
//     }
    if (dockable instanceof TreeDockable) {
      this.treeDockables.add(((TreeDockable)dockable).getId());
      fileCloseAll.setEnabled(true);
    }

  }

  public ADAction getRedoItem() {
    return editRedo;
  }

  public ADAction getUndoItem() {
    return editUndo;
  }

  public void closeAllTrees() {
    for (Object o:this.treeDockables.toArray()) {
      int i = ((Integer)o).intValue();
      TreeDockable d = (TreeDockable)control.getMultipleDockable(TreeDockable.getUniqueId(i));
      d.setVisible(false);
    }
    fileCloseAll.setEnabled(this.treeDockables.size() > 0);
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
    if (dockable instanceof TreeDockable) {
      this.treeDockables.remove(((TreeDockable)dockable).getId());
      fileCloseAll.setEnabled(this.treeDockables.size() > 0);
    }
    if (dockable instanceof TreeDockable && lastFocusedTree instanceof AbstractTreeCanvas) {
      if (((TreeDockable) dockable).getId() == lastFocusedTree.getTreeId()) {
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
      if (lastFocusedTree != null
          && uid.equals(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getTreeId()) + TreeDockable.TREEVIEW_ID)) {
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
//     if (dockable instanceof TreeDockable) {
//       Debug.log("*** opened tree dockable with id:"+ ((TreeDockable)dockable).getId());
//     }
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
//     if (dockable instanceof TreeDockable) {
//       Debug.log("*** closed tree dockable with id:"+ ((TreeDockable)dockable).getId());
//     }
    if (dockable instanceof TreeDockable && lastFocusedTree instanceof AbstractTreeCanvas) {
      if (((TreeDockable) dockable).getId() == lastFocusedTree.getTreeId()) {
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
      if (lastFocusedTree != null
          && uid.equals(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getTreeId()) + TreeDockable.TREEVIEW_ID)) {
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

  public CCP getCopyHandler() {
    return this.copyHandler;
  }

  public void addTreeDockable(TreeDockable treeDockable) {
    control.addDockable(TreeDockable.getUniqueId(treeDockable.getId()), treeDockable);
    treeDockable.setWorkingArea(frame.getWorkArea());
    treeDockable.setVisible(true);
    DefaultSingleCDockable dock = (DefaultSingleCDockable) control
        .getSingleDockable(TreeDockable.getUniqueId(treeDockable.getId()) + TreeDockable.TREEVIEW_ID);
    if (dock != null) {
      dock.toFront();
    }
    else {
      treeDockable.toFront();
    }
    DomainFactory factory = getFrame().getDomainFactory();
    if (treeDockable.getLayout().hasDomain()) {
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
    menuItem = exportTo.add(fileExportToXml);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    exportTo.add(menuItem);
    menuItem = exportTo.add(fileExportToTex);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    exportTo.add(menuItem);
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
    fileMenu.add(exportTo);

    JMenu importFrom = new JMenu(Options.getMsg("file.import.txt"));
    importFrom.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.import.key")).getKeyCode());
    menuItem = importFrom.add(fileImportFromXml);
    menuItem = importFrom.add(fileImportFromAdt);
    menuItem = importFrom.add(fileImportFromTxt);
    menuItem.addMouseListener(mouseHandler);
    importFrom.add(menuItem);
    fileMenu.add(importFrom);
    menuItem = fileMenu.add(filePrint);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    menuItem = fileMenu.add(filePrintPreview);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    fileMenu.addSeparator();
    menuItem = fileMenu.add(fileClose);
    this.toDisableItems.add(menuItem);
    menuItem.addMouseListener(mouseHandler);
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    menuItem = fileMenu.add(fileCloseAll);
    fileCloseAll.setEnabled(false);
    menuItem.addMouseListener(mouseHandler);
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
    IconFactory iconFac = new IconFactory();
    final JMenu editMenu = new JMenu();
    editMenu.setText(Options.getMsg("edit.txt"));
    editMenu.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.key")).getKeyCode());

    menuItem = editMenu.add(editUndo);
    menuItem.addMouseListener(mouseHandler);
    editMenu.add(menuItem);
    menuItem = editMenu.add(editRedo);
    menuItem.addMouseListener(mouseHandler);
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
    editMenu.addSeparator();

    menuItem = new JMenuItem(Options.getMsg("edit.adddomain.txt"));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chooseDomain();
      }
    });
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.adddomain.key")).getKeyCode());
    menuItem.setIcon(iconFac.createImageIcon("/icons/addDomain.png"));
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
    editMenu.add(menuItem);
    editMenu.add(editEditDomain);
    menuItem = new JMenuItem(Options.getMsg("edit.switchRole.txt"));
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.switchRole.key")).getKeyCode());
    menuItem.addActionListener(new ActionListener() {

      @SuppressWarnings("unchecked")
      public void actionPerformed(ActionEvent e) {
        if (lastFocusedTree != null && !lastFocusedTree.isSand()) {
          AbstractTreeCanvas canvas = lastFocusedTree.getTreeCanvas();

          ((ADTreeCanvas<Ring>) canvas).switchAttacker();
          report(
              Options
                  .getMsg("edit.switchRole.report",
                      (canvas.getTree().getLayout().getSwitchRole()
                          ? Options.getMsg("tablemodel.opponent").toLowerCase()
                          : Options.getMsg("tablemodel.proponent").toLowerCase())));
        }
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.switchRole.acc")));
    menuItem.setIcon(iconFac.createImageIcon("/icons/switch.png"));
    menuItem.setEnabled(false);
    this.toDisableItems.add(menuItem);
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
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.messageLog.key")).getKeyCode());
    windowsMenu.add(menuItem);
    menuItem = this.frame.getValuationsView().createMenuItem();
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.valuations.key")).getKeyCode());
    windowsMenu.add(menuItem);

    menuItem = this.frame.getRankingView().createMenuItem();
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.ranking.key")).getKeyCode());
    windowsMenu.add(menuItem);

    menuItem = this.frame.getDetailsView().createMenuItem();
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("windows.details.key")).getKeyCode());
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
    JRadioButtonMenuItem item = new JRadioButtonMenuItem(Options.getMsg("view.themes.basicTheme.txt"));
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
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("view.fitToWindow.key")).getKeyCode());
    viewMenu.add(menuItem);
    menuItem = new JMenuItem(Options.getMsg("view.zoomin.txt"));
    menuItem.setIcon(iconFac.createImageIcon("/icons/toolbar/zoom_in_24x24.png"));
    this.toDisableItems.add(menuItem);
    menuItem.setEnabled(false);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (lastFocusedTree != null) {
          lastFocusedTree.zoomIn();
        }
      }
    });
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("view.zoomin.key")).getKeyCode());
    menuItem.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("view.zoomin.acc")));

    viewMenu.add(menuItem);
    menuItem = new JMenuItem(Options.getMsg("view.zoomout.txt"));
    menuItem.setIcon(iconFac.createImageIcon("/icons/toolbar/zoom_out_24x24.png"));
    this.toDisableItems.add(menuItem);
    menuItem.setEnabled(false);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (lastFocusedTree != null) {
          lastFocusedTree.zoomOut();
        }
      }
    });
    menuItem.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("view.zoomout.key")).getKeyCode());
    menuItem.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("view.zoomout.acc")));
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
        final TreeLayout layout = new TreeLayout(frame.getTreeFactory().getNewUniqueId(), new SandNode());
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

    fileExportToXml = new ADAction(Options.getMsg("file.export.xml.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("xml");
      }

      private static final long serialVersionUID = 4409590774645645107L;
    };
    fileExportToXml.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.xml.key")));
    fileExportToXml.setSmallIcon(iconFac.createImageIcon("/icons/xml_16x16.png"));

    fileExportToPdf = new ADAction(Options.getMsg("file.export.pdf.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("pdf");
      }

      private static final long serialVersionUID = 4325025687838671271L;
    };
    fileExportToPdf.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.pdf.key")));
    fileExportToPdf.setSmallIcon(iconFac.createImageIcon("/icons/pdf_16x16.png"));

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

      private static final long serialVersionUID = 2156590777160375107L;
    };

    fileExportToPng = new ADAction(Options.getMsg("file.export.png.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("png");
      }
      private static final long serialVersionUID = 2398600083840742200L;
    };
    fileExportToPng.setSmallIcon(iconFac.createImageIcon("/icons/png_16x16.png"));
    fileExportToPng.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.png.key")));

    fileExportToTxt.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.txt.key")));
    // fileExportToPng.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
    // InputEvent.ALT_MASK));
    fileExportToTxt.setSmallIcon(iconFac.createImageIcon("/icons/txt_16x16.png"));


    fileExportToTex = new ADAction(Options.getMsg("file.export.tex.txt")) {
      public void actionPerformed(final ActionEvent e) {
        exportTo("tex");
      }
      private static final long serialVersionUID = -5611960325333019445L;
    };
    fileExportToTex.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.tex.key")));
    // fileExportToPng.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
    // InputEvent.ALT_MASK));
    fileExportToTex.setSmallIcon(iconFac.createImageIcon("/icons/tex_16x16.png"));


    fileImportFromXml = new ADAction(Options.getMsg("file.export.xml.txt")) {
      private static final long serialVersionUID = -3605440604743377670L;

      public void actionPerformed(final ActionEvent e) {
        importFrom("xml");
      }
    };
    fileImportFromXml.setSmallIcon(iconFac.createImageIcon("/icons/xml_16x16.png"));
    fileImportFromXml.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.xml.key")));

    fileImportFromAdt = new ADAction(Options.getMsg("file.import.adt.txt")) {
      private static final long serialVersionUID = -5605440635343377923L;

      public void actionPerformed(final ActionEvent e) {
        importFrom("adt");
      }
    };
    fileImportFromAdt.setSmallIcon(iconFac.createImageIcon("/icons/tree_16x16.png"));
    fileImportFromAdt.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.import.adt.key")));

    fileImportFromTxt = new ADAction(Options.getMsg("file.export.txt.txt")) {
      private static final long serialVersionUID = -5605440635343377923L;

      public void actionPerformed(final ActionEvent e) {
        importFrom("txt");
      }
    };
    fileImportFromTxt.setSmallIcon(iconFac.createImageIcon("/icons/txt_16x16.png"));
    fileImportFromTxt.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.txt.key")));

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
    filePrintPreview.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.printPreview.acc")));

    fileClose = new ADAction(Options.getMsg("file.close.txt")) {
      public void actionPerformed(final ActionEvent e) {
        if (lastFocusedTree != null && lastFocusedTree.getTreeId() != -1) {
          TreeDockable d = (TreeDockable)control.getMultipleDockable(TreeDockable.getUniqueId(lastFocusedTree.getTreeId()));
          if (d != null) {
            d.setVisible(false);
            if(!d.isVisible()){
              setLastFocused(null);
            }
          }
        }
      }
      private static final long serialVersionUID = 2265646886353219901L;
    };
    fileClose.setToolTip(Options.getMsg("file.close.tooltip"));
    fileClose.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.close.key")));
    fileClose.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.close.acc")));
    fileClose.setSmallIcon(iconFac.createImageIcon("/icons/close.png"));

    fileCloseAll = new ADAction(Options.getMsg("file.closeall.txt")) {
      public void actionPerformed(final ActionEvent e) {
        closeAllTrees();
      }
      private static final long serialVersionUID = 1265611132353219697L;
    };
    fileCloseAll.setToolTip(Options.getMsg("file.closeall.tooltip"));
    fileCloseAll.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.closeall.key")));
    fileCloseAll.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("file.closeall.acc")));
    fileCloseAll.setSmallIcon(iconFac.createImageIcon("/icons/closeall.png"));

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

      private static final long serialVersionUID = 229256580368467602L;
    };
    editCut.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.cut.key")));
    editCut.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.cut.acc")));
    editCut.setSmallIcon(iconFac.createImageIcon("/icons/cut.png"));
    editPaste = new ADAction(Options.getMsg("edit.paste.txt")) {

      public void actionPerformed(final ActionEvent e) {
        copyHandler.paste();
      }

      private static final long serialVersionUID = -9187280429559390662L;
    };
    editPaste.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.paste.key")));
    editPaste.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.paste.acc")));
    editPaste.setSmallIcon(iconFac.createImageIcon("/icons/paste.png"));
    editCopy = new ADAction(Options.getMsg("edit.copy.txt")) {

      public void actionPerformed(final ActionEvent e) {
        copyHandler.copy();
      }

      private static final long serialVersionUID = 3236243103402493159L;
    };
    editCopy.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.copy.key")));
    editCopy.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.copy.acc")));
    editCopy.setSmallIcon(iconFac.createImageIcon("/icons/copy.png"));
    editUndo = new ADAction(Options.getMsg("edit.undo.txt")) {

      private static final long serialVersionUID = 2250038237606593189L;

      public void actionPerformed(final ActionEvent e) {
        if (lastFocusedTree != null) {
          lastFocusedTree.undo();
        }
      }
    };
    editUndo.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.undo.key")));
    editUndo.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.undo.acc")));
    editUndo.setSmallIcon(iconFac.createImageIcon("/icons/undo_16x16.png"));

    editRedo = new ADAction(Options.getMsg("edit.redo.txt")) {

      private static final long serialVersionUID = -4219335962479205379L;

      public void actionPerformed(final ActionEvent e) {
        if (lastFocusedTree != null) {
          lastFocusedTree.redo();
        }
      }
    };
    editRedo.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.redo.key")));
    editRedo.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("edit.redo.acc")));
    editRedo.setSmallIcon(iconFac.createImageIcon("/icons/redo_16x16.png"));

    editEditDomain = new ADAction(Options.getMsg("edit.editdomain.txt")) {

      private static final long serialVersionUID = -5219165454659205379L;

      public void actionPerformed(final ActionEvent e) {
        if (lastFocusedTree != null) {
          editDomain();
        }
      }
    };
    editEditDomain.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("edit.editdomain.key")));
    editEditDomain.setSmallIcon(iconFac.createImageIcon("/icons/editDomain.png"));
    editEditDomain.setEnabled(false);
  }

  private void removeDomain(DomainDockable dockable) {
    int id = dockable.getCanvas().getTreeId();
    int domainId = dockable.getCanvas().getDomainId();
    TreeDockable td = (TreeDockable) control.getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(id));
    if (td != null) {
      if (lastFocusedTree instanceof AbstractDomainCanvas) {
        AbstractDomainCanvas<Ring> d = Objects.cast(lastFocusedTree);
        if (id == lastFocusedTree.getTreeId() && d.getDomainId() == domainId) {
          setLastFocused(td.getCanvas());
        }
      }
      td.getCanvas().removeDomain(dockable);
    }
    else {
      System.err.println("No window with id:" + id);
    }
  }

  private void editDomain() {
    if (this.lastFocusedTree instanceof AbstractDomainCanvas) {
      AbstractDomainCanvas<Ring> c = Objects.cast(this.lastFocusedTree);
      Domain<Ring> domain = c.getDomain();
      int domainId = c.getValues().getDomainId();
      if (this.lastFocusedTree.isSand()) {
        EditSandDomain action = new EditSandDomain(domainId, (SandCustomDomain) domain);
        if ((Object)domain instanceof SandBoolDomain) {
          SandBoolDomain d = Objects.cast(domain);
          AddBoolSandDomDialog dialog = new AddBoolSandDomDialog(this.frame, d);
          if (dialog.showDialog() != null) {
            this.lastFocusedTree.addEditAction(action);
            c.valuesUpdated();
          }
        }
        else if ((Object)domain instanceof SandIntDomain) {
          SandIntDomain d = Objects.cast(domain);
          AddIntSandDomDialog dialog = new AddIntSandDomDialog(this.frame, d);
          if (dialog.showDialog() != null) {
            this.lastFocusedTree.addEditAction(action);
            c.valuesUpdated();
          }
        }
        else if ((Object)domain instanceof SandRealDomain) {
          SandRealDomain d = Objects.cast(domain);
          AddRealSandDomDialog dialog = new AddRealSandDomDialog(this.frame, d);
          if (dialog.showDialog() != null) {
            this.lastFocusedTree.addEditAction(action);
            c.valuesUpdated();
          }
        }
      }
      else {
        EditAdtDomain action = new EditAdtDomain(domainId, (AdtCustomDomain) domain);
        if ((Object)domain instanceof AdtBoolDomain) {
          AdtBoolDomain d = Objects.cast(domain);
          AddBoolAdtDomDialog dialog = new AddBoolAdtDomDialog(this.frame, d);
          if (dialog.showDialog() != null) {
            this.lastFocusedTree.addEditAction(action);
            c.valuesUpdated();
          }
        }
        else if ((Object)domain instanceof AdtIntDomain) {
          AdtIntDomain d = Objects.cast(domain);
          AddIntAdtDomDialog dialog = new AddIntAdtDomDialog(this.frame, d);
          if (dialog.showDialog() != null) {
            this.lastFocusedTree.addEditAction(action);
            c.valuesUpdated();
          }
        }
        else if ((Object)domain instanceof AdtRealDomain) {
          AdtRealDomain d = Objects.cast(domain);
          AddRealAdtDomDialog dialog = new AddRealAdtDomDialog(this.frame, d);
          if (dialog.showDialog() != null) {
            this.lastFocusedTree.addEditAction(action);
            c.valuesUpdated();
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void chooseDomain() {
    if (this.lastFocusedTree != null) {
      if (this.lastFocusedTree.isSand()) {
        Vector<Domain<?>> domains = DomainFactory.getPredefinedDomains(true);
        AddSandDomainDialog addDialog = new AddSandDomainDialog(this.frame);
        SandDomain<?> d = addDialog.showDomainDialog(domains);
        if (d == null) {
          return;
        }
        if (d instanceof SandBoolDomain) {
          AddBoolSandDomDialog boolDialog = new AddBoolSandDomDialog(this.frame, (SandBoolDomain) d);
          if (boolDialog.showDialog() == null) {
            return;
          }
        }
        else if (d instanceof SandIntDomain) {
          AddIntSandDomDialog intDialog = new AddIntSandDomDialog(this.frame, (SandIntDomain) d);
          if (intDialog.showDialog() == null) {
            return;
          }
        }
        else if (d instanceof SandRealDomain) {
          AddRealSandDomDialog realDialog = new AddRealSandDomDialog(this.frame, (SandRealDomain) d);
          if (realDialog.showDialog() == null) {
            return;
          }
        }
        TreeDockable currentTree = (TreeDockable) this.control
            .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getTreeId()));
        if (currentTree != null) {
          currentTree.getCanvas().addDomain((Domain<Ring>)d);
          this.report(Options.getMsg("status.newdomain") + " " + d.getName());
        }
      }
      else {
        Vector<Domain<?>> domains = DomainFactory.getPredefinedDomains(false);
        AddAdtDomainDialog addDialog = new AddAdtDomainDialog(this.frame);
        AdtDomain<?> d = addDialog.showDomainDialog(domains);
        if (d == null) {
          return;
        }
        if (d instanceof AdtBoolDomain) {
          AddBoolAdtDomDialog boolDialog = new AddBoolAdtDomDialog(this.frame, (AdtBoolDomain) d);
          if (boolDialog.showDialog() == null) {
            return;
          }
        }
        else if (d instanceof AdtIntDomain) {
          AddIntAdtDomDialog intDialog = new AddIntAdtDomDialog(this.frame, (AdtIntDomain) d);
          if (intDialog.showDialog() == null) {
            return;
          }
        }
        else if (d instanceof AdtRealDomain) {
          AddRealAdtDomDialog realDialog = new AddRealAdtDomDialog(this.frame, (AdtRealDomain) d);
          if (realDialog.showDialog() == null) {
            return;
          }
        }
        else {
          if (d instanceof Parametrized) {
            if (((Parametrized) d).getParameter() instanceof Integer) {
              Integer value = (Integer) ((Parametrized) d).getParameter();
              BoundedIntegerDialog dialog;
              if (d instanceof lu.uni.adtool.domains.adtpredefined.MinSkill) {
                dialog = new BoundedIntegerDialog(this.frame, Options.getMsg("adtdomain.choosemaxskill.txt"));
              }
              else {
                dialog = new BoundedIntegerDialog(this.frame, Options.getMsg("adtdomain.choosek.txt"));
              }
              BoundedInteger result = (BoundedInteger) (dialog
                  .showInputDialog(new BoundedInteger(value, Integer.MAX_VALUE), false));
              if (result != null) {
                value = new Integer(result.getValue());
                if (value == BoundedInteger.INF) {
                  value = Integer.MAX_VALUE;
                }
                ((Parametrized) d).setParameter(value);
              }
              else {
                return;
              }
            }

          }
        }
        TreeDockable currentTree = (TreeDockable) this.control
            .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getTreeId()));
        if (currentTree != null) {
          currentTree.getCanvas().addDomain((AdtDomain<Ring>) d);
          this.report(Options.getMsg("status.newdomain") + " " + d.getName());
        }
      }
    }
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
            .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(lastFocusedTree.getTreeId()));
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
      else if (type.equals("tex")) {
        lastFocusedTree.createLatex(out);
      }
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
      else if (type.equals("adt")) {
        AdtImporter importer = new AdtImporter();
        try {
          importer.importFrom(in, this);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else if (type.equals("txt")) {
        TxtImporter txtImporter = new TxtImporter();
        try {
          txtImporter.importFrom(in, this);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
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
//           lastFocusedTree.fitToWindow();
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
  // private static ADAction fileOpen;
  // private static ADAction fileSave;
  private static ADAction      fileExportToPdf;
  private static ADAction      fileExportToPng;
  private static ADAction      fileExportToJpg;
  private static ADAction      fileExportToTxt;
  private static ADAction      fileExportToXml;
  private static ADAction      fileExportToTex;
  private static ADAction      filePrint;
  private static ADAction      filePrintPreview;
  private static ADAction      fileCloseAll;
  private static ADAction      fileClose;
  private static ADAction      fileExit;
  private static ADAction      fileImportFromXml;
  private static ADAction      fileImportFromAdt;
  private static ADAction      fileImportFromTxt;
  private static ADAction      editCopy;
  private static ADAction      editCut;
  private static ADAction      editPaste;
  private static ADAction      editUndo;
  private static ADAction      editRedo;
  private static ADAction      editEditDomain;

  private AbstractTreeCanvas   lastFocusedTree;

  /**
   * Class for handling displaying dialogs for saving/loading files.
   */
  private FileHandler          fh;

  private CControl             control;
  private CCP                  copyHandler;
  private Set<Integer>         treeDockables;
}
