package lu.uni.adtool.tools.undo;

import java.util.ArrayList;

import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.TreeDockable;

public class EditAdtDomain extends EditAction {
  public EditAdtDomain(int domainId, Domain<Ring> domain) {
    this.domainId = domainId;
  }

  public void undo(AbstractTreeCanvas canvas) {
    AbstractDomainCanvas domainCanvas = ((DomainDockable) canvas.getController().getControl()
      .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(canvas.getId()) + DomainDockable.DOMAIN_ID
                           + Integer.toString(this.domainId))).getCanvas();
    Domain<Ring> domain = domainCanvas.getDomain();
    if (da
  }

  public void redo(AbstractTreeCanvas canvas) {
  AbstractDomainCanvas domainCanvas = ((DomainDockable) canvas.getController().getControl()
      .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(canvas.getId()) + DomainDockable.DOMAIN_ID
                           + Integer.toString(this.domainId))).getCanvas();
    Domain<Ring> domain = domainCanvas.getDomain();
  }

  public String getName(){
    return Options.getMsg("action.editdomain");
  }

  private int domainId;
  private String name;
  private String description;
  private String cp;
  private String co;
  private String ap;
  private String ao;
  private String op;
  private String oo;
  private String defaulto;
  private String defaultp;
  private boolean proModifiable;
  private boolean oppModifiable;

}
