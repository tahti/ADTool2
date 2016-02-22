package lu.uni.adtool.ui;

import lu.uni.adtool.tools.Options;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.util.Filter;

public class BackupFactory implements SingleCDockableFactory, Filter<String> {
  public BackupFactory(CControl control) {
    this.control = control;
  }

  /**
   * Creates a backup of a {@link SingleCDockable}.
   *
   * @param id
   *          the unique id that the result must have
   * @return the backup dockable or <code>null</code> if no dockable can be
   *         created
   */
  public SingleCDockable createBackup(String id) {
//     String intId = id.substring(4, id.indexOf("_"));
    SingleCDockable dockable = control.getSingleDockable(id);
    if (dockable != null ) {
      return dockable;
    }
    if (id.endsWith("_treeView")) {
      dockable = new DefaultSingleCDockable(id, Options.getMsg("window.treeView.txt"));
      control.addDockable(dockable);
    }
    else if (id.endsWith("_workArea")) {
      dockable = control.createWorkingArea(id);
    }
    else if (id.endsWith("_termView")) {
      dockable = new DefaultSingleCDockable(id, Options.getMsg("window.termView.txt"));
      control.addDockable(dockable);
    }
    return dockable;
  }

  public boolean includes(String item) {
    if (item == null) return false;
    if (item.startsWith("tree")
        && (   item.endsWith("_treeView")
            || item.endsWith("_workArea")
            || item.endsWith("_termView"))) {
      return true;
    }
    return false;
  };
  private CControl control;
}
