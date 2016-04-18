package lu.uni.adtool.tools.undo;


import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class AddDomain extends EditAction {


  public AddDomain(int domainId, Domain<Ring> domain) {
    this.domain = domain;
    this.domainId = domainId;
  }

  public void undo(AbstractTreeCanvas canvas) {
    DomainDockable dockable = (DomainDockable) canvas.getController().getControl()
      .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(canvas.getId()) + DomainDockable.DOMAIN_ID
                           + Integer.toString(domainId));
    if (dockable != null) {
      canvas.getTree().getLayout().removeValuation(dockable.getCanvas().getValues());
      canvas.getController().getFrame().getDomainFactory().removeDomain(dockable);
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    TreeDockable currentTree = (TreeDockable) canvas.getController().getControl()
      .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(canvas.getId()));
    if (currentTree != null) {
      DomainFactory factory = canvas.getController().getFrame().getDomainFactory();
      DomainDockable d = null;
      if (domain instanceof SandDomain) {
        d = factory.read(new ValuationDomain(canvas.getId(),
            this.domainId, (SandDomain) domain));
      }
      else {
        d = factory.read(new ValuationDomain(canvas.getId(),
            this.domainId, (AdtDomain) domain));
      }
      canvas.getController().getControl().addDockable(d.getUniqueId(), d);
      currentTree.showDomain(d);
    }
  }


  public String getName(){
    return Options.getMsg("action.adddomain");
  }

  private Domain<Ring> domain;
  private int domainId;
}
