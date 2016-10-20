package lu.uni.adtool.tools.undo;

import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.custom.SandCustomDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class EditSandDomain extends EditAction {

  public EditSandDomain(int domainId, SandCustomDomain domain) {
    this.domainId = domainId;
    this.name = domain.getName();
    this.description = domain.getShortDescription();
    this.or = domain.getOr();
    this.and = domain.getAnd();
    this.sand = domain.getSand();
    this.defaultValue = domain.getDefault();
  }

  public void undo(AbstractTreeCanvas canvas) {
    AbstractDomainCanvas<Ring> domainCanvas = getDomainDockable(this.domainId, canvas).getCanvas();
    Domain<Ring> d= domainCanvas.getDomain();
    if (d != null && d instanceof SandCustomDomain) {
      SandCustomDomain domain = (SandCustomDomain) d;
      String temp = domain.getName();
      domain.setName(this.name);
      this.name = temp;
      temp = domain.getShortDescription();
      domain.setDescription(this.description);
      this.description = temp;
      temp = domain.getOr();
      domain.setOr(this.or);
      this.or= temp;
      temp = domain.getAnd();
      domain.setAnd(this.and);
      this.and= temp;
      temp = domain.getSand();
      domain.setSand(this.sand);
      this.sand= temp;
      temp = domain.getDefault();
      domain.setDefault(this.defaultValue);
      this.defaultValue= temp;
      domainCanvas.valuesUpdated(false);
    }
  }

  public void redo(AbstractTreeCanvas canvas) {
    this.undo(canvas);
  }

  public String getName() {
    return Options.getMsg("action.editdomain");
  }

  private int     domainId;
  private String  name;
  private String  description;
  private String  or;
  private String  and;
  private String  sand;
  private String  defaultValue;

}
