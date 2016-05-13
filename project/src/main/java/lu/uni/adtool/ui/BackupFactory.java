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
