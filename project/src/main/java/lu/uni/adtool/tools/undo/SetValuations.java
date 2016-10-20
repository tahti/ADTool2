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
import lu.uni.adtool.domains.ValueAssignement;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class SetValuations extends EditAction {


  public SetValuations(ValueAssignement<Ring> newKeys, ValueAssignement<Ring> oldKeys, int domainId, boolean clearOld) {
    this.newKeys = newKeys;
    this.oldKeys = oldKeys;
    this.domainId = domainId;
    this.clearOld = clearOld;
  }

  public void undo(AbstractTreeCanvas canvas) {
    ValuationDomain vd = canvas.getTree().getLayout().getDomain(domainId);
    for (String key : this.oldKeys.keySet(true)) {
      vd.setValue(true, key, this.oldKeys.get(true, key));
    }
    for (String key : this.oldKeys.keySet(false)) {
      vd.setValue(false, key, this.oldKeys.get(false, key));
    }
    for (String key : this.oldKeys.keySet(true)) {
      vd.setValue(true, key, this.oldKeys.get(true, key));
    }
    for (String key : this.oldKeys.keySet(false)) {
      vd.setValue(false, key, this.oldKeys.get(false, key));
    }
    DomainDockable dockable = (DomainDockable) canvas.getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(vd.getTreeId())
            + DomainDockable.DOMAIN_ID + Integer.toString(vd.getDomainId()));
    dockable.getCanvas().valuesUpdated(this.clearOld);
  }

  public void redo(AbstractTreeCanvas canvas) {
    ValuationDomain vd = canvas.getTree().getLayout().getDomain(domainId);
    for (String key : this.newKeys.keySet(true)) {
      vd.setValue(true, key, this.newKeys.get(true, key));
    }
    for (String key : this.newKeys.keySet(false)) {
      vd.setValue(false, key, this.newKeys.get(false, key));
    }
    for (String key : this.newKeys.keySet(true)) {
      vd.setValue(true, key, this.newKeys.get(true, key));
    }
    for (String key : this.newKeys.keySet(false)) {
      vd.setValue(false, key, this.newKeys.get(false, key));
    }
    DomainDockable dockable = (DomainDockable) canvas.getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(vd.getTreeId())
            + DomainDockable.DOMAIN_ID + Integer.toString(vd.getDomainId()));
    dockable.getCanvas().valuesUpdated(this.clearOld);
  }


  public String getName(){
    return Options.getMsg("action.setvaluations");
  }

  private ValueAssignement<Ring> oldKeys;
  private ValueAssignement<Ring> newKeys;
  private int domainId;
  private boolean clearOld;
}
