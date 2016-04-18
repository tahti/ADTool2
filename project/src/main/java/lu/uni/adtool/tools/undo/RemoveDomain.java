package lu.uni.adtool.tools.undo;

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class RemoveDomain extends EditAction {

  public RemoveDomain(ValuationDomain values) {
    this.values = values;
  }

  public void undo(AbstractTreeCanvas canvas) {
    TreeDockable currentTree = (TreeDockable) canvas.getController().getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(canvas.getId()));
    if (currentTree != null) {
      Debug.log("Undo remove damain");
      DomainFactory factory = canvas.getController().getFrame().getDomainFactory();
      DomainDockable d = factory.read(this.values);
      canvas.getController().getControl().addDockable(d.getUniqueId(), d);
      currentTree.showDomain(d);
    }
    else {
      Debug.log("could not find window with id :"+TreeDockable.TREE_ID + Integer.toString(canvas.getId()));
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
}
