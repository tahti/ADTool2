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
package lu.uni.adtool.tree;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.intern.CDockable;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.ValueAssignement;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.undo.SetValuation;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.PermaDockable;
import lu.uni.adtool.ui.TreeDockable;
import lu.uni.adtool.ui.ValuationsDockable;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

/**
 * class to hold Cut Copy Paste structure
 *
 * @author Piotr Kordy
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

@SuppressWarnings("unchecked")
  public void paste(AbstractTreeCanvas lastFocusedTree) {
    if (lastFocused != null) {
      Debug.log("paste");
      try {
        Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (lastFocused instanceof ValuationsDockable) {
          if (contents.isDataFlavorSupported(ValuationSelection.valuationFlavor)) {
            ValueAssignement<Ring> copy =
              (ValueAssignement<Ring>) contents.getTransferData(ValuationSelection.valuationFlavor);
            ((ValuationsDockable) lastFocused).paste(copy);
          }
          if (contents.isDataFlavorSupported(ValueSelection.valueFlavor)) {
            Ring copy = (Ring) contents.getTransferData(ValueSelection.valueFlavor);
            ((ValuationsDockable) lastFocused).paste(copy);
          }
        }
        else {
          AbstractTreeCanvas canv = getCanvas(lastFocused, this.control);
          if (contents != null && canv != null && canv.getFocused() != null) {
            if (canv instanceof AbstractDomainCanvas
                && contents.isDataFlavorSupported(ValueSelection.valueFlavor)) {
              Ring copy = ((Ring) contents.getTransferData(ValueSelection.valueFlavor));
              ValuationDomain vd =((AbstractDomainCanvas<Ring>) canv).getValues();
              Ring oldValue;
              if (canv.getFocused() instanceof ADTNode) {
                oldValue = vd.getValue((ADTNode)canv.getFocused());
              }
              else {
                oldValue = vd.getValue((SandNode)canv.getFocused());
              }
              if (copy != oldValue) {
                String key = canv.getFocused().getName();
                boolean proponent = true;
                if (canv.getFocused() instanceof ADTNode) {
                  proponent = (((ADTNode)canv.getFocused()).getRole() == ADTNode.Role.PROPONENT);
                }
                if (canv.getFocused() instanceof SandNode ||
                    ((AdtDomain<Ring>)vd.getDomain()).isValueModifiable(proponent)) {

                  canv.addEditAction(new SetValuation( copy,
                                                       oldValue,
                                                       key,
                                                       proponent,
                                                       vd.getDomainId()));
                  vd.setValue(canv.getFocused(), copy);

                  ((AbstractDomainCanvas<Ring>) canv).valuesUpdated(false);
                }
              }
            }
            else if (canv.isSand() && contents.isDataFlavorSupported(NodeSelection.sandFlavor)) {
              SandNode copy = ((SandNode) contents.getTransferData(NodeSelection.sandFlavor));
              ((SandTreeCanvas<Ring>) canv).paste(copy);
            }
            else if (!canv.isSand() && contents.isDataFlavorSupported(NodeSelection.adtFlavor)) {
              ADTNode copy = ((ADTNode) contents.getTransferData(NodeSelection.adtFlavor));
              ((ADTreeCanvas<Ring>) canv).paste(copy);
            }
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

@SuppressWarnings("unchecked")
  public boolean copy() {
    if (lastFocused != null) {
      Debug.log("copy");
      Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (lastFocused instanceof ValuationsDockable) {
        Object copy = ((ValuationsDockable) lastFocused).copy();
        if (copy != null && copy instanceof ValueAssignement) {
          cb.setContents(new ValuationSelection((ValueAssignement<Ring>) copy), null);
        }
        else if (copy != null && copy instanceof Ring) {
          cb.setContents(new ValueSelection((Ring)copy), null);
        }
      }
      else {
        AbstractTreeCanvas canv = getCanvas(lastFocused, this.control);
        if (canv != null && canv.getFocused() != null) {
          if (canv instanceof AbstractDomainCanvas) {
            Ring copy;
            if (canv.getFocused() instanceof ADTNode) {
              copy =
                  ((AbstractDomainCanvas<Ring>) canv).getValues().getValue((ADTNode) canv.getFocused());
              if (copy == null) {
                copy =
                  ((AbstractDomainCanvas<Ring>) canv).getValues().getTermValue((ADTNode) canv.getFocused());
              }
            }
            else {
              copy =
                  ((AbstractDomainCanvas<Ring>) canv).getValues().getTermValue((SandNode) canv.getFocused());
            }
            cb.setContents(new ValueSelection(copy), null);
            return true;
          }
          else {
            if (canv.getFocused() instanceof ADTNode) {
              ADTNode copy = ((ADTNode) canv.getFocused()).deepCopy();
              if (canv.getTree().getLayout().getSwitchRole()) {
                copy.toggleRoleRecursive();
              }
              cb.setContents(new NodeSelection(copy), null);
              return true;
            }
            else if (canv.getFocused() instanceof SandNode) {
              SandNode copy = ((SandNode) canv.getFocused()).deepCopy();
              cb.setContents(new NodeSelection(copy), null);
              return true;
            }
          }
        }
      }
    }
    return false;
  }

@SuppressWarnings("unchecked")
  public void cut(AbstractTreeCanvas lastFocusedTree) {
    if (lastFocused != null) {
      Debug.log("cut");
      Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
      AbstractTreeCanvas canv = getCanvas(lastFocused, this.control);
      if (lastFocused instanceof ValuationsDockable) {
        Object copy = ((ValuationsDockable) lastFocused).copy();
        if (copy instanceof ValueAssignement) {
          cb.setContents(new ValuationSelection((ValueAssignement<Ring>) copy), null);
        }
        else if (copy instanceof Ring) {
          cb.setContents(new ValueSelection((Ring)copy), null);
        }
      }
      else {
        if (canv != null && canv.getFocused() != null) {
          if (canv instanceof AbstractDomainCanvas) {
            Ring copy;
            ValuationDomain values = ((AbstractDomainCanvas<Ring>) canv).getValues();
            if (canv.getFocused() instanceof ADTNode) {
              ADTNode node =(ADTNode) canv.getFocused();
              copy = values.getValue(node);
              Ring newValue = values.getDomain().getDefaultValue(node);
              values.setDefaultValue(node);
              if (newValue != (Ring)copy) {
                canv.addEditAction(new SetValuation(newValue,
                                                    (Ring) copy,
                                                   node.getName(),
                                                   node.getRole() == ADTNode.Role.PROPONENT,
                                                   values.getDomainId()));
                ((AbstractDomainCanvas<Ring>) canv).valuesUpdated(false);
              }
            }
            else {
              SandNode node =(SandNode) canv.getFocused();
              copy = ((AbstractDomainCanvas<Ring>) canv).getValues().getValue(node);
              Ring newValue = values.getDomain().getDefaultValue(node);
              values.setDefaultValue(node);
              if (newValue != (Ring)copy) {
                canv.addEditAction(new SetValuation(newValue,
                                                    (Ring) copy,
                                                   node.getName(),
                                                   true,
                                                   values.getDomainId()));
                ((AbstractDomainCanvas<Ring>) canv).valuesUpdated(false);
              }

            }
            cb.setContents(new ValueSelection(copy), null);
          }
          else { //not domain canvas but tree canvas
            if (canv.getFocused() instanceof ADTNode) {
              cb.setContents(new NodeSelection(((ADTNode) canv.getFocused()).deepCopy()), null);
              ((ADTreeCanvas<Ring>) canv).removeTree((ADTNode) canv.getFocused()); //undo actions added from canvas
            }
            else if (canv.getFocused() instanceof SandNode) {
              cb.setContents(new NodeSelection(((SandNode) canv.getFocused()).deepCopy()), null);
              ((SandTreeCanvas<Ring>) canv).removeTree((SandNode) canv.getFocused()); //undo actions added from canvas
            }
          }
        }
      }
    }
  }

  static class NodeSelection implements Transferable {
    // Transferable class constructor
    public NodeSelection(Node node) {
      this.node = node;
    }

    @SuppressWarnings("deprecation")
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
      // If text/rtf flavor is requested
      if (adtFlavor.equals(df)) {
        if (node instanceof ADTNode) {
          // Return text/rtf data
          return ((ADTNode) node).deepCopy();
        }
        else {
          return ((SandNode) node).adtCopy();
        }
      }
      else if (sandFlavor.equals(df)) {
        if (node instanceof SandNode) {
          return ((SandNode) node).deepCopy();
        }
        else {
          return ((ADTNode) node).sandCopy();
        }
        // If plain flavor is requested
      }
      else if (DataFlavor.stringFlavor.equals(df)) {
        if (node instanceof ADTNode) {
          return ((ADTNode) node).toTerms();
        }
        else {
          return ((SandNode) node).toTerms();
        }
      }
      else if (DataFlavor.plainTextFlavor.equals(df)) {
        if (node instanceof ADTNode) {
          return new StringReader(((ADTNode) node).toTerms());
        }
        else {
          return new StringReader(((SandNode) node).toTerms());
        }
      }
      else {
        throw new UnsupportedFlavorException(df);
      }
    }

    @SuppressWarnings("deprecation")
    public boolean isDataFlavorSupported(DataFlavor df) {
      // If the flavor is text/rtf or tet/plain return true
      if (adtFlavor.equals(df) || sandFlavor.equals(df) ||
          DataFlavor.stringFlavor.equals(df) ||
          DataFlavor.plainTextFlavor.equals(df)) {
        return true;
      }
      return false;
    }

    @SuppressWarnings("deprecation")
    public DataFlavor[] getTransferDataFlavors() {
      // Return array of flavors
      return new DataFlavor[] {adtFlavor, sandFlavor, DataFlavor.stringFlavor, DataFlavor.plainTextFlavor};
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

  static class ValuationSelection implements Transferable {
    // Transferable class constructor
    public ValuationSelection(ValueAssignement<?> va) {
      this.values = va;
    }

    @SuppressWarnings("deprecation")
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
      // If text/rtf flavor is requested
      if (valuationFlavor.equals(df)) {
        return this.values;
      }
      else if (DataFlavor.stringFlavor.equals(df)) {
        return this.values.toString();
      }
      else if (DataFlavor.plainTextFlavor.equals(df)) {
        return new StringReader(this.values.toString());
      }
      else {
        throw new UnsupportedFlavorException(df);
      }
    }

    @SuppressWarnings("deprecation")
    public boolean isDataFlavorSupported(DataFlavor df) {
      // If the flavor is text/rtf or tet/plain return true
      if (valuationFlavor.equals(df) ||
          DataFlavor.stringFlavor.equals(df) ||
          DataFlavor.plainTextFlavor.equals(df)) {
        return true;
      }
      return false;
    }

    @SuppressWarnings("deprecation")
    public DataFlavor[] getTransferDataFlavors() {
      // Return array of flavors
      return new DataFlavor[] {valuationFlavor, DataFlavor.stringFlavor, DataFlavor.plainTextFlavor};
    }

    private ValueAssignement<?>    values;

    public static final DataFlavor valuationFlavor;

    static {
      DataFlavor d1 = null;
      try {
        d1 = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
            + ";class=lu.uni.adtool.domains.ValueAssignement");
      }
      catch (ClassNotFoundException e) {
      }
      finally {
        valuationFlavor = d1;
      }
    }
  }

  static class ValueSelection implements Transferable {
    // Transferable class constructor
    public ValueSelection(Ring value) {
      this.value = value;
    }

    @SuppressWarnings("deprecation")
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
      // If text/rtf flavor is requested
      if (valueFlavor.equals(df)) {
        return this.value;
      }
      else if (DataFlavor.stringFlavor.equals(df)) {
        return this.value.toString();
      }
      else if (DataFlavor.plainTextFlavor.equals(df)) {
        return new StringReader(this.value.toString());
      }
      else {
        throw new UnsupportedFlavorException(df);
      }
    }

    @SuppressWarnings("deprecation")
    public boolean isDataFlavorSupported(DataFlavor df) {
      // If the flavor is text/rtf or tet/plain return true
      if (valueFlavor.equals(df) ||
          DataFlavor.stringFlavor.equals(df) ||
          DataFlavor.plainTextFlavor.equals(df)) {
        return true;
      }
      return false;
    }

    @SuppressWarnings("deprecation")
    public DataFlavor[] getTransferDataFlavors() {
      // Return array of flavors
      return new DataFlavor[] {valueFlavor, DataFlavor.stringFlavor, DataFlavor.plainTextFlavor};
    }

    // ADTNode
    private Ring                   value;

    public static final DataFlavor valueFlavor;

    static {
      DataFlavor d1 = null;
      try {
        d1 = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType + ";class=lu.uni.adtool.domains.rings.Ring");
      }
      catch (ClassNotFoundException e) {
      }
      finally {
        valueFlavor = d1;
      }
    }
  }

  private CDockable lastFocused;
  private CControl  control;
}
