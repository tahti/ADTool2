package lu.uni.adtool.tree;

import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.PermaDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * class to hold Cut Copy Paste structure
 */
public class CCP {
  public CCP(CControl control) {
    this.control = control;
    lastFocused = null;
  }

  public static AbstractTreeCanvas getCanvas(CDockable dockable, CControl contr) {
    if (dockable instanceof TreeDockable) {
      return ((TreeDockable) dockable).getCanvas();
    }
    else if (dockable instanceof DomainDockable) {
      return (((DomainDockable) dockable).getCanvas());
    }
    else if (dockable instanceof PermaDockable) {
      return null;
    }
    else if (dockable instanceof DefaultSingleCDockable) {
      String id = ((DefaultSingleCDockable) dockable).getUniqueId();
      if (id.endsWith(TreeDockable.TREEVIEW_ID)) {
        TreeDockable d = (TreeDockable) contr.getMultipleDockable(id.substring(0, id.indexOf('_')));
        if (d == null) {
          return null;
        }
        else {
          return d.getCanvas();
        }
      }
    }
    return null;
  }

  public void setFocus(CDockable dockable) {
    lastFocused = dockable;
  }

  public void paste() {
    if (lastFocused != null) {
      AbstractTreeCanvas canv = getCanvas(lastFocused, this.control);
      Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
      try {
        if (contents != null && canv != null && canv.getFocused() != null) {
          if (canv instanceof AbstractDomainCanvas) {
          }
          else if (canv.isSand() && contents.isDataFlavorSupported(NodeSelection.sandFlavor)) {
            SandNode copy = ((SandNode) contents.getTransferData(NodeSelection.adtFlavor));
            ((SandTreeCanvas) canv).paste(copy);
          }
          else if (!canv.isSand() && contents.isDataFlavorSupported(NodeSelection.adtFlavor)) {
            ADTNode copy = ((ADTNode) contents.getTransferData(NodeSelection.adtFlavor));
            ((ADTreeCanvas) canv).paste(copy);
          }
        }
      }
      catch (UnsupportedFlavorException e) {
        e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public boolean copy() {
    if (lastFocused != null) {
      Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
      AbstractTreeCanvas canv = getCanvas(lastFocused, this.control);
      if (canv != null && canv.getFocused() != null) {
        if (canv instanceof AbstractDomainCanvas) {
        }
        else {
          if(canv.getFocused() instanceof ADTNode) {
            cb.setContents(new NodeSelection(((ADTNode)canv.getFocused()).deepCopy()), null);
            return true;
          }
        }
      }
    }
    return false;
  }

  public void cut() {
  }

  static class NodeSelection implements Transferable {
    // Transferable class constructor
    public NodeSelection(Node node) {
      this.node = node;
    }

    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
      // If text/rtf flavor is requested
      if (adtFlavor.equals(df)) {
        // Return text/rtf data
        return node;
      }
      else if (sandFlavor.equals(df)) {
        return node;
        // If plain flavor is requested
      }
      else if (DataFlavor.stringFlavor.equals(df)) {
        return ((ADTNode) node).toTerms();
      }
      else {
        throw new UnsupportedFlavorException(df);
      }
    }

    public boolean isDataFlavorSupported(DataFlavor df) {
      // If the flavor is text/rtf or tet/plain return true
      if (adtFlavor.equals(df) || sandFlavor.equals(df) || DataFlavor.stringFlavor.equals(df)) {
        return true;
      }
      return false;
    }

    public DataFlavor[] getTransferDataFlavors() {
      // Return array of flavors
      return new DataFlavor[] {adtFlavor, sandFlavor, DataFlavor.stringFlavor};
    }

    // ADTNode
    private Node                   node;

    public static final DataFlavor adtFlavor;
    public static final DataFlavor sandFlavor;

    static {
      DataFlavor d1 = null;
      DataFlavor d2 = null;
      try {
        d1 = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType + ";class=lu.uni.adtool.tree.ADTNode");
        d2 = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType + ";class=lu.uni.adtool.tree.SandNode");
      }
      catch (ClassNotFoundException e) {
      }
      finally {
        adtFlavor = d1;
        sandFlavor = d2;
      }
    }
  }

  private CDockable lastFocused;
  private CControl  control;
}
