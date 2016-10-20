package lu.uni.adtool.tools.undo;

import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.custom.AdtCustomDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class EditAdtDomain extends EditAction {

  public EditAdtDomain(int domainId, AdtCustomDomain domain) {
    this.domainId = domainId;
    this.name = domain.getName();
    this.description = domain.getShortDescription();
    this.cp = domain.getCp();
    this.co = domain.getCo();
    this.ap = domain.getAp();
    this.ao = domain.getAo();
    this.op = domain.getOp();
    this.oo = domain.getOo();
    this.defaultp = domain.getProDefault();
    this.defaulto = domain.getOppDefault();
    this.proModifiable = domain.isProModifiable();
    this.oppModifiable = domain.isOppModifiable();
  }

  public void undo(AbstractTreeCanvas canvas) {
    AbstractDomainCanvas<Ring> domainCanvas = getDomainDockable(this.domainId, canvas).getCanvas();
    Domain<Ring> d= domainCanvas.getDomain();
    if (d != null && d instanceof AdtCustomDomain) {
      AdtCustomDomain domain = (AdtCustomDomain) d;
      String temp = domain.getName();
      domain.setName(this.name);
      this.name = temp;
      temp = domain.getShortDescription();
      domain.setDescription(this.description);
      this.description = temp;
      temp = domain.getCp();
      domain.setCp(this.cp);
      this.cp= temp;
      temp = domain.getCo();
      domain.setCo(this.co);
      this.co= temp;
      temp = domain.getAp();
      domain.setAp(this.ap);
      this.ap= temp;
      temp = domain.getAo();
      domain.setAo(this.ao);
      this.ao= temp;
      temp = domain.getOp();
      domain.setOp(this.op);
      this.op= temp;
      temp = domain.getOo();
      domain.setOo(this.oo);
      this.oo= temp;
      temp = domain.getProDefault();
      domain.setProDefault(this.defaultp);
      this.defaultp= temp;
      temp = domain.getOppDefault();
      domain.setProDefault(this.defaulto);
      this.defaulto= temp;
      boolean t = domain.isProModifiable();
      domain.setProModifiable(this.proModifiable);
      proModifiable = t;
      t = domain.isOppModifiable();
      domain.setOppModifiable(this.oppModifiable);
      oppModifiable = t;
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
  private String  cp;
  private String  co;
  private String  ap;
  private String  ao;
  private String  op;
  private String  oo;
  private String  defaulto;
  private String  defaultp;
  private boolean proModifiable;
  private boolean oppModifiable;

}
