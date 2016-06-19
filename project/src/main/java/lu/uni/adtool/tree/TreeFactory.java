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
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.TreeDockable;

import java.util.ArrayList;

import bibliothek.gui.dock.common.MultipleCDockableFactory;

public class TreeFactory implements MultipleCDockableFactory<TreeDockable, TreeLayout> {

  public TreeFactory(MainController controller) {
    this.idCount = 0;
    this.controller = controller;
  }

  public String getId() {
    return TREE_FACTORY_ID;
  }

  /*
   * An empty layout is required to read a layout from an XML file or from a
   * byte stream
   */
  public TreeLayout create() {
    return new TreeLayout(-1);
  }

  /*
   * An optional method allowing to reuse 'dockable' when loading a new layout
   */
  public boolean match(TreeDockable dockable, TreeLayout layout) {
    return dockable.getLayout().equals(layout);
  }

  /* Called when applying a stored layout */
  public TreeDockable read(TreeLayout layout) {
    if (layout.getRoot() == null) return null;
    Debug.log("reading, treeId:" + layout.getTreeId());
    this.idCount = Math.max(this.idCount, layout.getTreeId());
    TreeDockable dockable = new TreeDockable(this, layout, true);
    DomainFactory factory = controller.getFrame().getDomainFactory();
    ArrayList<DomainDockable> domains = factory.getDomains(new Integer(layout.getTreeId()));
    if (domains != null) {
      // final ArrayList<ValuationDomain> domainArray = layout.getDomains();
      int size = domains.size();
      Debug.log("Domains size:" + size);
      for (int i = 0; i < size; i++) {
        DomainDockable domain = domains.get(i);
        domain.getCanvas().setTree(dockable.getCanvas().getTree());
        if (dockable.getCanvas().isSand()) {
          domain.hideShowAll();
        }
        dockable.getLayout().addDomain(domain.getCanvas().getValues());
        Debug.log("Connecting domain domainId:" + domain.getValues().getDomainId());
      }
    }
    return dockable;
  }

  /**
   * Used to import from TreeLayout where positions of new windows is not saved
   */
  public TreeDockable load(TreeLayout layout) {
    this.idCount = Math.max(this.idCount, layout.getTreeId());
    TreeDockable dockable = new TreeDockable(this, layout, false);
    return dockable;
  }

  /* Called when storing the current layout */
  public TreeLayout write(TreeDockable dockable) {
    return dockable.getLayout();
  }

  public MainController getController() {
    return controller;
  }

  public int getNewUniqueId() {
    idCount = idCount + 1;
    return idCount;
  }

  public static final String TREE_FACTORY_ID = "sand_fact";
  private int                idCount;
  private MainController     controller;
}
