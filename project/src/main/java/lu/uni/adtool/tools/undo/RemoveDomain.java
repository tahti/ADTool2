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
package lu.uni.adtool.tools.undo;

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class RemoveDomain extends EditAction {

  public RemoveDomain(ValuationDomain values, boolean localExtentProvider) {
    this.localExtentProvider = localExtentProvider;
    this.values = values;
  }

  public void undo(AbstractTreeCanvas canvas) {
    TreeDockable currentTree = (TreeDockable) canvas.getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(canvas.getTreeId()));
    if (currentTree != null) {
      Debug.log("Undo remove damain");
      DomainFactory factory = canvas.getController().getFrame().getDomainFactory();
      DomainDockable d = factory.read(this.values);
      canvas.getController().getControl().addDockable(d.getUniqueId(), d);
      AbstractDomainCanvas<Ring> dc = d.getCanvas();
      if (dc.hasLocalExtentProvider() != this.localExtentProvider) {
        dc.setLocalExtentProvider(this.localExtentProvider);
      }
      currentTree.showDomain(d);
    }
    else {
      Debug.log("could not find window with id :" + TreeDockable.TREE_ID + Integer.toString(canvas.getTreeId()));
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    DomainDockable dockable = (DomainDockable) canvas.getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(this.values.getTreeId())
            + DomainDockable.DOMAIN_ID + Integer.toString(this.values.getDomainId()));
    if (dockable != null) {
      Debug.log("Redo  remove damain");
      canvas.getTree().getLayout().removeValuation(dockable.getCanvas().getValues());
      canvas.getController().getFrame().getDomainFactory().removeDomain(dockable);
      dockable.setVisible(false);
    }
    else {
      Debug.log("could not find window with id :"+TreeDockable.TREE_ID + Integer.toString(this.values.getTreeId())
                + DomainDockable.DOMAIN_ID + Integer.toString(this.values.getDomainId()));
    }

  }

  public String getName() {
    return Options.getMsg("action.removedomain");
  }

  private ValuationDomain values;
  private boolean localExtentProvider;
}
