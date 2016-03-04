package lu.uni.adtool;

import lu.uni.adtool.tools.Clo;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.TreeFactory;
import lu.uni.adtool.tree.XmlConverter;
import lu.uni.adtool.ui.BackupFactory;
import lu.uni.adtool.ui.DetailsView;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.OptionPane;
import lu.uni.adtool.ui.PermaDockable;
import lu.uni.adtool.ui.RankingDockable;
import lu.uni.adtool.ui.StatusLine;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.ValuationsDockable;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockFrontend.DockInfo;
import bibliothek.gui.DockFrontend.RootInfo;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.intern.layout.CLayoutChangeStrategy;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.frontend.DockFrontendInternals;
import bibliothek.gui.dock.frontend.MissingDockableStrategy;
import bibliothek.gui.dock.layout.DockLayoutComposition;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.PredefinedDockSituation;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.util.Path;
import bibliothek.util.xml.XException;

public final class ADToolMain extends JFrame {

  public ADToolMain(Clo clo) {
    Options.loadOptions();
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    // Use enter as space not pressing the default button
    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
    final JFrame frame = this;
    frame.addWindowListener(new WindowAdapter() {
      public final void windowClosing(final WindowEvent e) {
        int result = OptionPane.showYNDialog(frame, Options.getMsg("closedialog.txt"),
            Options.getMsg("closedialog.title"));
        if (result == JOptionPane.YES_OPTION) {
          Options.saveLayout(controller.getControl());
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
      }
    });
    CControl control = new CControl(this);
    control.intern().setLayoutChangeStrategy(new LayoutChangeStrategy(control));
    control.getController().getProperties().set(PlaceholderStrategy.PLACEHOLDER_STRATEGY,
        new PermaPlaceholderStrategy());
    trees = new HashMap<Integer, CContentArea>();
    this.status = new StatusLine();
    status.setBorder(BorderFactory.createEtchedBorder());
    this.controller = new MainController(this, control);
    control.addControlListener(controller);
    control.addFocusListener(controller);
    this.treeFactory = new TreeFactory(this.controller);
    control.addMultipleDockableFactory(this.treeFactory.getId(), this.treeFactory);
    this.domainFactory = new DomainFactory(this.controller);
    control.addMultipleDockableFactory(this.domainFactory.getId(), this.domainFactory);
    BackupFactory backup = new BackupFactory(control);
    control.addSingleDockableFactory(backup, backup);
    this.setTheme(control);
    this.add(control.getContentArea());

    work = control.createWorkingArea("work");
    this.valuationsView = new ValuationsDockable(controller.getCopyHandler());
    this.rankingView = new RankingDockable();
    this.detailsView = new DetailsView();

    CGrid grid = new CGrid(control);

    grid.add(0, 0, 3, 2, work);
    grid.add(3, 0, 1, 2, status.getLogViewDockable());
    grid.add(4, 0, 1, 2, this.valuationsView);
    grid.add(5, 0, 1, 2, this.rankingView);
    grid.add(6, 0, 1, 2, this.detailsView);
    control.getContentArea().deploy(grid);
    this.valuationsView.doClose();
    this.rankingView.doClose();
    this.detailsView.doClose();
    super.setJMenuBar(this.controller.getMenu());
    if (clo.getToOpen() == null) {
      Options.tryLoadLayout(control, this);
    }
    else {
      XmlConverter converter = new XmlConverter();
      for (String fileName:clo.getToOpen()) {
        FileInputStream in;
        try {
          in = new FileInputStream(fileName);
          converter.importFrom(in, this.controller);
          in.close();
        }
        catch (FileNotFoundException e) {
          System.out.println(Options.getMsg("error.xmlimport.fail") + e.getLocalizedMessage());
        }
        catch (IOException e) {
          System.out.println(Options.getMsg("error.xmlimport.fail") + e.getLocalizedMessage());
        }
      }
    }
    this.pack();
    this.setVisible(true);
//     this.setExtendedState(getExtendedState() |JFrame.MAXIMIZED_BOTH);
//     this.setVisible(true);
  }

  public TreeFactory getTreeFactory() {
    return treeFactory;
  }

  public DomainFactory getDomainFactory() {
    return domainFactory;
  }

  private void setTheme(CControl control) {
    IconFactory factory = new IconFactory();
    IconManager im = control.intern().getController().getIcons();
    im.setIconClient("locationmanager.normalize",
        factory.createImageIcon("/icons/theme/normalize.png"));
    im.setIconClient("locationmanager.maximize",
        factory.createImageIcon("/icons/theme/maximize.png"));
    im.setIconClient("locationmanager.externalize",
        factory.createImageIcon("/icons/theme/externalize.png"));
    im.setIconClient("locationmanager.minimize",
        factory.createImageIcon("/icons/theme/minimize.png"));
    im.setIconClient("close", factory.createImageIcon("/icons/theme/close_active.png"));
    im.setIconClient("flap.hold", factory.createImageIcon("/icons/theme/pin_active.png"));
    im.setIconClient("flap.free", factory.createImageIcon("/icons/theme/unpin_active.png"));
    im.setIconClient("overflow.menu", factory.createImageIcon("/icons/theme/overflow_menu.png"));
    control.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
  }

  public static void main(String[] args) {
    Clo clo = new Clo();
    if(clo.parse(args)) {;
      ADToolMain adtool = new ADToolMain(clo);
      Debug.log(adtool.getClass().getPackage().getImplementationVersion());
      Debug.log(Options.getAppFolder().toString());
      adtool.setBounds(20, 20, 800, 600);
      adtool.setVisible(true);
    }
  }

  public StatusLine getStatusBar() {
    return this.status;
  }

  public CWorkingArea getWorkArea() {
    return this.work;
  }

  public CContentArea getContentArea(int id) {
    CContentArea area = trees.get(new Integer(id));
    if (area == null) {
      area = new CContentArea(controller.getControl(), TreeDockable.getContentId(id));
      controller.getControl().addStationContainer(area);
    }
    trees.put(new Integer(id), area);
    return area;
  }

  public ValuationsDockable getValuationsView() {
    return valuationsView;
  }

  public RankingDockable getRankingView() {
    return rankingView;
  }

  public DetailsView getDetailsView() {
    return detailsView;
  }

  /**
   * Status bar
   */
  private StatusLine                     status;
  private MainController                 controller;
  private ValuationsDockable             valuationsView;
  private RankingDockable                rankingView;
  private DetailsView                    detailsView;
  private static final long              serialVersionUID = 2554846105872850570L;
  private MainController                 controlller;
  private TreeFactory                    treeFactory;
  private DomainFactory                  domainFactory;
  private CWorkingArea                   work;
  private HashMap<Integer, CContentArea> trees;

  /*
   * This is our very simple PlaceholderStrategy. It only recognizes our custom
   * Dockable and returns its placeholder.
   */
  private static class PermaPlaceholderStrategy implements PlaceholderStrategy {
    public void addListener(PlaceholderStrategyListener listener) {
      // ignore
    }

    public Path getPlaceholderFor(Dockable dockable) {
      if (dockable instanceof PermaDockable) {
        return ((PermaDockable) dockable).getPlaceholder();
      }
      return null;
    }

    public void install(DockStation station) {
      // ignore
    }

    public boolean isValidPlaceholder(Path placeholder) {
      return true;
    }

    public void removeListener(PlaceholderStrategyListener listener) {
      // ignore
    }

    public void uninstall(DockStation station) {
      // ignore
    }
  }

  private class LayoutChangeStrategy extends CLayoutChangeStrategy {
    public LayoutChangeStrategy(CControl control) {
      super(control);
    }

    /**
     * Applies the layout described in <code>setting</code> to the visible
     * elements. This implementation tries to estimate the location of missing
     * dockables using
     * {@link #listEstimateLocations(DockSituation, DockLayoutComposition)}. It
     * also checks and adds stations and work areas that are not present.
     *
     * @param frontend
     *          the caller of this method
     * @param situation
     *          used to convert the layout
     * @param setting
     *          the new layout
     * @param entry
     *          whether the layout is a full or regular layout
     * @throws IOException
     *           if the layout cannot be converted
     * @throws XException
     *           if the layout cannot be converted
     */
    protected void applyLayout(DockFrontendInternals frontend, DockSituation situation,
        SettingAccess setting, boolean entry) throws IOException, XException {
      Debug.log("applyLayout");
      DockFrontend dockFrontend = frontend.getFrontend();
      MissingDockableStrategy missingDockable = frontend.getMissingDockableStrategy();
      CControl control = controller.getControl();
      for (String key : setting.getRootKeys()) {
        if (frontend.getFrontend().getRoot(key) == null) {
          if (key.startsWith("tree") && key.contains("_")) {
            Integer id = new Integer(Integer.parseInt(key.substring(4, key.indexOf("_"))));
            final String workId = TreeDockable.getWorkAreaId(id.intValue());
            CWorkingArea workArea =
                (CWorkingArea) controller.getControl().getSingleDockable(workId);
            if (workArea == null) {
              workArea = control.createWorkingArea(workId);
              ((PredefinedDockSituation) situation).put(
                  DockFrontend.DOCKABLE_KEY_PREFIX + "single " + workId,
                  workArea.asDockable().intern());
            }
            CContentArea area = trees.get(id);
            if (area == null) {
              area = getContentArea(id);
              for (int i = 0, n = area.getStationCount(); i < n; i++) {
                ((PredefinedDockSituation) situation).put(
                    DockFrontend.ROOT_KEY_PREFIX + area.getStation(i).getUniqueId(),
                    area.getStation(i).getStation());
              }
            }
            CGrid grid = new CGrid(control);
            grid.add(0, 0, 1, 1, workArea);
            area.deploy(grid);
          }
        }
      }

      for (RootInfo info : frontend.getRoots()) {
        DockLayoutComposition layout = setting.getRoot(info.getName());
        if (layout != null) {
          layout = situation.fillMissing(layout);

          Map<String, DockableProperty> missingLocations = listEstimateLocations(situation, layout);
          if (missingLocations != null) {
            for (Map.Entry<String, DockableProperty> missing : missingLocations.entrySet()) {
              String key = missing.getKey();
              DockInfo dockInfo = frontend.getInfo(key);

              if (dockInfo == null && missingDockable.shouldStoreShown(key)) {
                dockFrontend.addEmpty(key);
                dockInfo = frontend.getInfo(key);
              }

              if (dockInfo != null) {
                dockInfo.setLocation(info.getName(), missing.getValue());
                dockInfo.setShown(true);
              }
            }
          }

          Map<String, DockLayoutComposition> missingLayouts = listLayouts(situation, layout);

          if (missingLayouts != null) {
            for (Map.Entry<String, DockLayoutComposition> missing : missingLayouts.entrySet()) {
              String key = missing.getKey();
              DockInfo dockInfo = frontend.getInfo(key);

              if (dockInfo == null && missingDockable.shouldStoreShown(key)) {
                dockFrontend.addEmpty(key);
                dockInfo = frontend.getInfo(key);
              }

              if (dockInfo != null) {
                dockInfo.setShown(true);
                if (!entry || dockInfo.isEntryLayout()) {
                  dockInfo.setLayout(missing.getValue());
                }
              }
            }

          }

          situation.convert(layout);
        }
      }
    }

    protected Collection<Dockable> approveClosing(DockFrontendInternals frontend,
        DockSituation situation, SettingAccess setting) {
      // check whether some elements really should be closed
      Set<Dockable> remainingVisible = new HashSet<Dockable>();
      for (RootInfo info : frontend.getRoots()) {
        DockLayoutComposition layout = setting.getRoot(info.getName());
        if (layout != null) {
          Set<Dockable> visible = estimateVisible(frontend, situation, layout);
          if (visible != null) {
            remainingVisible.addAll(visible);
          }
        }
      }

      Collection<Dockable> closing = getClosingDockables(frontend, remainingVisible);

      if (!closing.isEmpty()) {
        if (!frontend.getVetos().expectToHide(closing, true)) {
          // do not cancel the operation
          return closing;
        }
      }
      return closing;
    }

  }

}
