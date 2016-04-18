package lu.uni.adtool.tools.undo;

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class SetValuation extends EditAction {


  public SetValuation(Ring newValue, Ring oldValue, String key, boolean proponent, int domainId) {
    this.newValue = newValue;
    this.oldValue = oldValue;
    this.key = key;
    this.proponent = proponent;
    this.domainId = domainId;
  }

  public void undo(AbstractTreeCanvas canvas) {
    ValuationDomain vd = canvas.getTree().getLayout().getDomains().get(domainId - 1);
    vd.setValue(proponent, key, oldValue);
    DomainDockable dockable = (DomainDockable) canvas.getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(vd.getTreeId())
            + DomainDockable.DOMAIN_ID + Integer.toString(vd.getDomainId()));
    dockable.getCanvas().valuesUpdated();
  }

  public void redo(AbstractTreeCanvas canvas) {
    ValuationDomain vd = canvas.getTree().getLayout().getDomains().get(domainId - 1);
    vd.setValue(proponent, key, newValue);
    DomainDockable dockable = (DomainDockable) canvas.getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(vd.getTreeId())
            + DomainDockable.DOMAIN_ID + Integer.toString(vd.getDomainId()));
    dockable.getCanvas().valuesUpdated();
  }


  public String getName(){
    return Options.getMsg("action.setvaluation");
  }

  private Ring oldValue;
  private Ring newValue;
  private String key;
  private boolean proponent;
  private int domainId;
}
