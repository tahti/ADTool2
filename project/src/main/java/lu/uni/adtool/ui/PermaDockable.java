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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.event.CDockableLocationEvent;
import bibliothek.gui.dock.common.event.CDockableLocationListener;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

/*
 * This dockable remembers its location if it is closed and can open itself at
 * the former location
 */
public class PermaDockable extends DefaultSingleCDockable implements CDockableLocationListener, CVetoClosingListener {

  public PermaDockable(Path placeholder, String id, String title) {
    super(id, title);
    this.placeholder = placeholder;
    this.addCDockableLocationListener(this);
    this.addVetoClosingListener(this);
    this.setCloseable(true);
  }

  public Path getPlaceholder() {
    return placeholder;
  }

  /**
   * Called if the visibility and/or the location of a {@link CDockable}
   * changed.
   *
   * @param event
   *          detailed information about the event
   */
  public void changed(CDockableLocationEvent event) {
    if (event.getDockable() instanceof PermaDockable) {
      PermaDockable d = (PermaDockable) event.getDockable();
      if (d.getMenuItem() != null) {
        d.getMenuItem().setSelected(event.getNewShowing());
      }
    }
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
      doClose();
      event.cancel();
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

  public JMenuItem getMenuItem() {
    return menuItem;
  }

  public void updateMenu() {
    if (this.isVisible()) {
    }
  }

  /* creates a checkbox for opening/closing this dockable */
  public JMenuItem createMenuItem() {
    this.menuItem = new JCheckBoxMenuItem(getTitleText(), getTitleIcon());
    this.menuItem.setSelected(intern().getDockParent() != null);
    this.menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (menuItem.isSelected()) {
          doShow();
        }
        else {
          doClose();
        }
      }
    });
    return menuItem;
  }

  public void doClose() {
    DockStation parent = intern().getDockParent();
    if (parent != null) {
      /* remember the old location... */
      root = DockUtilities.getRoot(this.intern());
      location = DockUtilities.getPropertyChain(this.intern());
      /* ... then close */
      parent.drag(this.intern());
    }
  }

  public void doShow() {
    if (intern().getDockParent() == null) {
      /* drop this at the former location */
      if (!root.drop(this.intern(), location)) {
        root.drop(this.intern());
      }
      location = null;
    }
  }

  private Path             placeholder;
  private DockableProperty location;
  private DockStation      root;
  private JMenuItem        menuItem;

}
