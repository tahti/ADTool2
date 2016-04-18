package lu.uni.adtool.tools.undo;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

public class SwitchAttacker extends EditAction {
  public SwitchAttacker() {
  }
  public void undo(AbstractTreeCanvas canvas) {
    ((ADTreeCanvas)canvas).switchAttacker();
  }
  public void redo(AbstractTreeCanvas canvas) {
    ((ADTreeCanvas)canvas).switchAttacker();
  }
  public String getName(){
    return Options.getMsg("action.switchattacker");
  }
}
