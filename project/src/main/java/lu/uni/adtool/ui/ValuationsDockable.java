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
package lu.uni.adtool.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import bibliothek.util.Path;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.ValueAssignement;
import lu.uni.adtool.domains.rings.Bool;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.domains.rings.LMHValue;
import lu.uni.adtool.domains.rings.RealG0;
import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tools.undo.SetValuations;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.CCP;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.inputdialogs.BoundedIntegerDialog;
import lu.uni.adtool.ui.inputdialogs.InputDialog;
import lu.uni.adtool.ui.inputdialogs.LMHDialog;
import lu.uni.adtool.ui.inputdialogs.LMHEDialog;
import lu.uni.adtool.ui.inputdialogs.RealG0Dialog;
import lu.uni.adtool.ui.inputdialogs.RealZeroOneDialog;

public class ValuationsDockable extends PermaDockable implements ListSelectionListener {

  public ValuationsDockable(CCP copyHandler) {
    super(new Path(ID_VALUATIONVIEW), ID_VALUATIONVIEW, Options.getMsg("windows.valuations.txt"));
    this.pane = new JPanel(new BorderLayout());
    this.copyHandler = copyHandler;
    pane.add(getEmptyMessage());
    add(pane);
    ImageIcon icon = new IconFactory().createImageIcon("/icons/table_16x16.png",
        Options.getMsg("windows.valuations.txt"));
    this.setTitleIcon(icon);
  }

  /**
   * @param canvas
   *          the canvas to set
   */
  @SuppressWarnings("unchecked")
  public void setCanvas(AbstractTreeCanvas canvas) {
    pane.removeAll();
    this.canvas = canvas;
    if (canvas != null && canvas instanceof AbstractDomainCanvas) {
      JScrollPane scrollPane = new JScrollPane(this.createTable((AbstractDomainCanvas<Ring>) canvas));
      pane.add(scrollPane);
      pane.revalidate();
    }
    else {
      pane.add(getEmptyMessage());
    }
  }

  /**
   * @return the canvas
   */
  public AbstractTreeCanvas getCanvas() {
    return canvas;
  }

  public boolean edit(int row) {
    Debug.log("edit1");
    if (!table.isCellSelected(row, 1)) {
      return false;
    }
    Debug.log("edit2");

    ValuationDomain vd = ((AbstractDomainCanvas<?>) getCanvas()).getValues();
    String key = (String) table.getValueAt(row, 0);
    int[] selection = table.getSelectedRows();
    ValueAssignement<Ring> oldKeys = new ValueAssignement<Ring>();
    ValueAssignement<Ring> newKeys = new ValueAssignement<Ring>();
    boolean addUndo = false;
    AbstractDomainCanvas<?> c = (AbstractDomainCanvas<?>) getCanvas();
    boolean proponent = true;
    if (!getCanvas().isSand()) {
      proponent = key.equals(Options.getMsg("tablemodel.proponent"));
      key = (String) table.getValueAt(row, 1);
    }
    Ring value = editValue(proponent, key);
    if (selection.length > 0) {
      Debug.log("edit3");
      if (value != null) {
        Debug.log("edit4");
        for (int i = 0; i < selection.length; i++) {
          Debug.log("edit i:"+ i);
          if (!getCanvas().isSand()) {
            proponent =
              table.getValueAt(selection[i], 0).equals(Options.getMsg("tablemodel.proponent"));
            key = (String) table.getValueAt(selection[i], 1);
          }
          else {
            key = (String) table.getValueAt(selection[i], 0);
          }
          if (getCanvas().isSand() ||
              ((AdtDomain<Ring>)vd.getDomain()).isValueModifiable(proponent)) {
            Debug.log("inside edit key:"+ key);
            Ring oldValue = vd.get(proponent, key);
            oldKeys.put(proponent, key, oldValue);
            newKeys.put(proponent, key, value);
            vd.setValue(proponent, key, value);
            addUndo = true;
          }
        }
        ((AbstractDomainCanvas<?>) getCanvas()).valuesUpdated(false);
        if (addUndo) {
          c.getTreeCanvas().addEditAction(new SetValuations(newKeys, oldKeys, vd.getDomainId(), false));
        }

      }
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        table.requestFocus();
      }
    });
    return false;
  }

  public Ring editValue(boolean proponent, String key) {
    Debug.log("editvalue prop:"+proponent);
    Ring value;
    InputDialog dialog;
    ValuationDomain vd = ((AbstractDomainCanvas<?>) getCanvas()).getValues();
    if (!getCanvas().isSand()) {
      if (!((AdtDomain<Ring>)vd.getDomain()).isValueModifiable(proponent)) {
        return null;
      }
    }
    Debug.log("editvalue key:"+key);
    value = vd.get(proponent, key);

    if (value instanceof Bool) {
      value = (Ring) Bool.not((Bool) value);
    }
    else if (value instanceof RealG0) {
      dialog = new RealG0Dialog(getCanvas().getFrame());
      value = (Ring) (dialog.showInputDialog(value));
    }
    else if (value instanceof RealZeroOne) {
      dialog = new RealZeroOneDialog(getCanvas().getFrame());
      value = (Ring) (dialog.showInputDialog(value));
    }
    else if (value instanceof LMHValue) {
      dialog = new LMHDialog(getCanvas().getFrame());
      value = (Ring) (dialog.showInputDialog(value));
    }
    else if (value instanceof LMHEValue) {
      dialog = new LMHEDialog(getCanvas().getFrame());
      value = (Ring) (dialog.showInputDialog(value));
    }
    else if (value instanceof BoundedInteger) {
      dialog = new BoundedIntegerDialog(getCanvas().getFrame());
      value = (Ring) (dialog.showInputDialog(value));
    }
    return value;
  }

  /**
   * Paste Ring value to the selected rows. Adds Undo action and checks if values are modifiable in current domain.
   *
   * @param copy - value to be pasted
   */
  public void paste(Ring copy) {
    int[] selection = table.getSelectedRows();
    boolean proponent = true;
    if (selection.length > 0) {
      ValuationDomain vd = ((AbstractDomainCanvas<?>) getCanvas()).getValues();
      Ring v = null;
      if (getCanvas().isSand()) {
        SandNode node = new SandNode();
        v = vd.getDomain().getDefaultValue(node);
        if (!v.updateFromString(copy.toString())) {
          return;
        }
      }
      else {
        ADTNode node = new ADTNode();
        v = vd.getDomain().getDefaultValue(node);
        if (!v.updateFromString(copy.toString())) {
          return;
        }
      }
      ValueAssignement<Ring> oldKeys = new ValueAssignement<Ring>();
      ValueAssignement<Ring> newKeys = new ValueAssignement<Ring>();
      boolean addUndo = false;
      String key;
      for (int i = 0; i < selection.length; i++) {
        if (!getCanvas().isSand()) {
          proponent =
            table.getValueAt(selection[i], 0).equals(Options.getMsg("tablemodel.proponent"));
          key = (String) table.getValueAt(selection[i], 1);
        }
        else {
          key = (String) table.getValueAt(selection[i], 0);
        }
        if (getCanvas().isSand() ||
            ((AdtDomain<Ring>)vd.getDomain()).isValueModifiable(proponent)) {
          Ring oldValue = vd.get(proponent, key);
          oldKeys.put(proponent, key, oldValue);
          newKeys.put(proponent, key, v);
          vd.setValue(proponent, key, v);
          addUndo = true;
        }
      }
      if (addUndo) {
        ((AbstractDomainCanvas<?>) getCanvas()).valuesUpdated(false);
        ((AbstractDomainCanvas<?>) getCanvas()).getTreeCanvas().
          addEditAction(new SetValuations(newKeys, oldKeys, vd.getDomainId(), false));
      }
    }
  }

  public void paste(ValueAssignement<Ring> copy) {
    ValuationTableModel tm = (ValuationTableModel) table.getModel();
    ValueAssignement<Ring> oldKeys = new ValueAssignement<Ring>();
    ValueAssignement<Ring> newKeys = new ValueAssignement<Ring>();
    ValuationDomain vd = ((AbstractDomainCanvas<?>) getCanvas()).getValues();

    boolean changed = false;
    boolean proponent;
    for (int i = 0; i < tm.getRowCount(); i++) {
      String key;
      if (getCanvas().isSand()) {
        proponent = true;
        key = (String) (tm.getValueAt(i, 0));
      }
      else {
        proponent = tm.getValueAt(i, 0).equals(Options.getMsg("tablemodel.proponent"));
        key = (String) (tm.getValueAt(i, 1));
      }
      Ring value = copy.get(proponent, key);
      if (value != null) {
        Ring v = null;
        if (getCanvas().isSand()) {
          v = vd.getDomain().getDefaultValue(new SandNode());
        }
        else {
          ADTNode node = new ADTNode();
          if (proponent) {
            node.setType(ADTNode.Type.AND_PRO);
          }
          else {
            node.setType(ADTNode.Type.AND_OPP);
          }
          v = vd.getDomain().getDefaultValue(node);
        }
        if (v.updateFromString(value.toString()) &&
            (getCanvas().isSand() ||
             ((AdtDomain<Ring>)vd.getDomain()).isValueModifiable(proponent))) {
          Ring oldValue = vd.get(proponent, key);
          oldKeys.put(proponent, key, oldValue);
          newKeys.put(proponent, key, v);
          vd.setValue(proponent, key, v);
          changed = true;
        }
      }
    }
    if (changed) {
      ((AbstractDomainCanvas<?>) getCanvas()).valuesUpdated(false);
      ((AbstractDomainCanvas<?>) getCanvas()).getTreeCanvas().
        addEditAction(new SetValuations(newKeys, oldKeys, vd.getDomainId(), false));
    }
  }

  @SuppressWarnings("unchecked")
  public Object copy() {
    Object copy = null;
    int[] selection = table.getSelectedRows();
    if (selection.length > 0) {
      if (selection.length == 1) {
        if (getCanvas().isSand()) {
          String key = (String) (table.getModel().getValueAt(selection[0], 0));
          copy = ((AbstractDomainCanvas<?>) getCanvas()).getValues().get(true, key);
        }
        else {
          boolean proponent = table.getModel().getValueAt(selection[0], 0)
              .equals(Options.getMsg("tablemodel.proponent"));
          String key = (String) table.getModel().getValueAt(selection[0], 1);
          copy = ((AbstractDomainCanvas<?>) getCanvas()).getValues().get(proponent, key);
        }
      }
      else {
        copy = new ValueAssignement<Ring>();
        for (int i = 0; i < selection.length; i++) {
          if (getCanvas().isSand()) {
            String key = (String) (table.getModel().getValueAt(selection[i], 0));
            Ring value = ((AbstractDomainCanvas<?>) getCanvas()).getValues().get(true, key);
            ((ValueAssignement<Ring>) copy).put(true, key, value);
          }
          else {
            boolean proponent = table.getModel().getValueAt(selection[i], 0)
                .equals(Options.getMsg("tablemodel.proponent"));
            String key = (String) table.getModel().getValueAt(selection[i], 1);
            Ring value = ((AbstractDomainCanvas<?>) getCanvas()).getValues().get(proponent, key);
            ((ValueAssignement<Ring>) copy).put(proponent, key, value);
          }
        }
      }
    }
    return copy;
  }

  public static final String ID_VALUATIONVIEW = "val_view";

  private JPanel createTable(AbstractDomainCanvas<Ring> newCanvas) {
    JPanel result = new JPanel();
    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
    ValuationTableModel tableModel = new ValuationTableModel(newCanvas.isSand());
    tableModel.setCanvas(newCanvas);
    this.table = new JTable(tableModel) {
      private static final long serialVersionUID = -8834465928956864103L;
    };
    MouseListener ml[] = table.getMouseListeners();
    for (MouseListener m : ml) {
      table.removeMouseListener(m);
    }
    ListSelectionModel listSelectionModel = table.getSelectionModel();
    listSelectionModel.addListSelectionListener(this);
    table.setSelectionModel(listSelectionModel);
    table.setRowSorter(null);
    table.setAutoCreateRowSorter(false);
    table.setDefaultRenderer(Ring.class, new ValuationRenderer());
    table.setFillsViewportHeight(true);
    table.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        int row = table.rowAtPoint(evt.getPoint());
        int col = table.columnAtPoint(evt.getPoint());
        if (row >= 0 && col >= 0) {
          if (evt.isShiftDown()) {
            table.changeSelection(row, col, evt.isControlDown(), true);
          }
          else if (evt.isControlDown()) {
            table.changeSelection(row, col, true, false);
          }
          else if (evt.isMetaDown() || evt.isAltDown()) {
            return;
          }
          else {
            if (table.isCellSelected(row, col)) {
              edit(row);
            }
            table.changeSelection(row, col, false, false);
          }
        }
      }
    });
    InputMap im = table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    im.put(key, "edit");
    key = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
    im.put(key, "edit");
//     for (KeyStroke tkey : im.allKeys()) {
//       Debug.log("key:" + tkey.toString());
//       Debug.log("a:" + im.get(tkey).toString());
//     }
    ActionMap am = table.getActionMap();
    am.put("copy", new AbstractAction() {

      public void actionPerformed(ActionEvent evt) {
        copyHandler.copy();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            table.requestFocus();
          }
        });
      }
      private static final long serialVersionUID = 823562942713655994L;
    });
    am.put("cut", new AbstractAction() {
      public void actionPerformed(ActionEvent evt) {
        copyHandler.cut(canvas);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            table.requestFocus();
          }
        });
      }
      private static final long serialVersionUID = -7011637827296467946L;
    });
    am.put("paste", new AbstractAction() {

      public void actionPerformed(ActionEvent evt) {
        copyHandler.paste(canvas);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            table.requestFocus();
          }
        });
      }
      private static final long serialVersionUID = -5767425987370641468L;
    });
    am.put("edit", new AbstractAction() {
      public void actionPerformed(ActionEvent evt) {
        edit(table.getSelectionModel().getLeadSelectionIndex());
      }
      private static final long serialVersionUID = -7359453212974793708L;
    });
    result.add(new JLabel(((AbstractDomainCanvas<?>) newCanvas).getDomain().getName()));
    result.add(table.getTableHeader());
    result.add(table);
    return result;
  }

  private JLabel getEmptyMessage() {
    JLabel result;
    result = new JLabel(Options.getMsg("windows.valuations.nodomain"));
    return result;
  }

  class ValuationTableModel extends DefaultTableModel {

    public ValuationTableModel(boolean sand) {
      super();
      this.sand = sand;
      if (!sand) {
        super.addColumn(Options.getMsg("tablemodel.nodetype"));
      }
      super.addColumn(Options.getMsg("tablemodel.nodelabel"));
      super.addColumn(Options.getMsg("tablemodel.value"));
    }

    /**
     * {@inheritDoc}
     *
     * @see TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
      int ringColumn = 2;
      if (sand) {
        ringColumn = 1;
      }
      if (columnIndex == ringColumn) {
        return Ring.class;
      }
      else {
        return getValueAt(0, columnIndex).getClass();
      }
    }

    /**
     * {@inheritDoc}
     *
     * @see TableModel#isCellEditable(int,int)
     */
    public boolean isCellEditable(int row, int col) {
      if (sand) {
        if (col == 1) {
          return true;
        }
        else {
          return false;
        }
      }
      else {
        if (col > 1) {
          return true;
        }
        else {
          return false;
        }

      }
    }

    /**
     * {@inheritDoc}
     *
     * @see DefaultTableModel#setValueAt(Object value, int row, int col)
     */
    public void setValueAt(Object value, int row, int col) {
      super.setValueAt(value, row, col);
      fireTableCellUpdated(row, col);
    }

    public void setCanvas(AbstractDomainCanvas<Ring> canvas) {
      // this.canvas = canvas;
      if (canvas != null) {
        updateRowData(canvas);
        fireTableDataChanged();
        sand = canvas.isSand();
      }
    }

    private void updateRowData(AbstractDomainCanvas<?> canvas) {
      if (canvas == null) {
        return;
      }
      if (sand) {
        ArrayList<String> keys = canvas.getValues().sandKeySet();
        setRowCount(0);
        for (String key : keys) {
          Vector<Comparable<?>> v = new Vector<Comparable<?>>();
          v.add(key);
          v.add(canvas.getValues().get(true, key));
          addRow(v);
        }
      }
      else {
        ArrayList<String> keys = canvas.getValues().sandKeySet();
        setRowCount(0);
        for (String key : keys) {
          Vector<Comparable<?>> v = new Vector<Comparable<?>>();
          v.add(Options.getMsg("tablemodel.proponent"));
          v.add(key);
          v.add(canvas.getValues().get(true, key));
          addRow(v);
        }
        keys = canvas.getValues().oppKeySet();
        for (String key : keys) {
          Vector<Comparable<?>> v = new Vector<Comparable<?>>();
          v.add(Options.getMsg("tablemodel.opponent"));
          v.add(key);
          v.add(canvas.getValues().get(false, key));
          addRow(v);
        }
      }
    }

    private boolean sand;
    private static final long serialVersionUID = -1732533423111800331L;
  }

  /**
   * Renderer for table cells.
   */
  class ValuationRenderer extends DefaultTableCellRenderer implements TableCellRenderer {


    public ValuationRenderer() {
      super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
      if (value instanceof Ring) {
        return super.getTableCellRendererComponent(table, ((Ring) value).toUnicode(), isSelected,
            hasFocus, row, column);
      }
      else {
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      }
    }
    private static final long serialVersionUID = 8345195096257288633L;
  }

  public void valueChanged(ListSelectionEvent e) {
    if (getCanvas() == null) return;
    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
    ((AbstractDomainCanvas<?>) getCanvas()).unmarkAll();
    if (!lsm.isSelectionEmpty()) {
      // Find out which indexes are selected.
      int minIndex = lsm.getMinSelectionIndex();
      int maxIndex = lsm.getMaxSelectionIndex();
      for (int i = minIndex; i <= maxIndex; i++) {
        if (lsm.isSelectedIndex(i)) {
          int j = table.convertRowIndexToModel(i);
          String key = "";
          if (getCanvas().isSand()) {
            key = (String) (table.getModel().getValueAt(j, 0));
          }
          else {
            key = (String) (table.getModel().getValueAt(j, 1));
          }
          ((AbstractDomainCanvas<?>) getCanvas()).markLabel(key);
        }
      }
    }
    getCanvas().repaint();
  }

  private JPanel             pane;
  private AbstractTreeCanvas canvas;
  private JTable             table;
  private CCP                copyHandler;
}
