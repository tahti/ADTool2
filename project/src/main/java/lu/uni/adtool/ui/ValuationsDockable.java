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

import lu.uni.adtool.domains.ValueAssignement;
import lu.uni.adtool.domains.rings.Bool;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.domains.rings.LMHValue;
import lu.uni.adtool.domains.rings.RealG0;
import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
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
    if (!table.isCellSelected(row, 1)) {
      return false;
    }
    if (getCanvas().isSand()) {
      String key = (String) table.getValueAt(row, 0);
      int[] selection = table.getSelectedRows();
      if (selection.length > 0) {
        Ring value = editValue(true, key);
        if (value != null) {
          for (int i = 0; i < selection.length; i++) {
            key = (String) table.getValueAt(selection[i], 0);
            ((AbstractDomainCanvas<?>) getCanvas()).getValues().setValue(true, key, value);
          }
          ((AbstractDomainCanvas<?>) getCanvas()).valuesUpdated();
        }
      }
    }
    else {
      String key = (String) table.getValueAt(row, 1);
      boolean proponent = table.getValueAt(row, 0).equals(Options.getMsg("tablemodel.proponent"));
      int[] selection = table.getSelectedRows();
      if (selection.length > 0) {
        Ring value = editValue(proponent, key);
        if (value != null) {
          for (int i = 0; i < selection.length; i++) {
            key = (String) table.getValueAt(selection[i], 1);
            proponent =
                table.getValueAt(selection[i], 0).equals(Options.getMsg("tablemodel.proponent"));
            ((AbstractDomainCanvas<?>) getCanvas()).getValues().setValue(proponent, key, value);
          }
          ((AbstractDomainCanvas<?>) getCanvas()).valuesUpdated();
        }
      }
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        // table.requestFocusInWindow();
        table.requestFocus();
      }
    });
    return false;
  }

  public Ring editValue(boolean proponent, String key) {
    Ring value;
    InputDialog dialog;
    value = ((AbstractDomainCanvas<?>) getCanvas()).getValues().get(proponent, key);

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
      // if(getDomain() instanceof MinSkill){
      // dialog = new BoundedIntegerInfDialog(getMainWindow());
      // }
      // else{
      dialog = new BoundedIntegerDialog(getCanvas().getFrame());
      // }
      value = (Ring) (dialog.showInputDialog(value));
    }
    return value;
  }

  public void paste(Ring copy) {
    int[] selection = table.getSelectedRows();
    if (selection.length > 0) {
      Ring v = null;
      if (getCanvas().isSand()) {
        SandNode node = new SandNode();
        v = ((AbstractDomainCanvas<?>) getCanvas()).getValues().getDomain().getDefaultValue(node);
        if (!v.updateFromString(copy.toString())) {
          return;
        }
      }
      else {
        ADTNode node = new ADTNode();
        v = ((AbstractDomainCanvas<?>) getCanvas()).getValues().getDomain().getDefaultValue(node);
        if (!v.updateFromString(copy.toString())) {
          return;
        }
      }
      for (int i = 0; i < selection.length; i++) {
        if (getCanvas().isSand()) {
          String key = (String) (table.getModel().getValueAt(selection[i], 0));
          ((AbstractDomainCanvas<?>) getCanvas()).getValues().setValue(true, key, v);
        }
        else {
          boolean proponent = table.getModel().getValueAt(selection[i], 0)
              .equals(Options.getMsg("tablemodel.proponent"));
          String key = (String) (table.getModel().getValueAt(selection[i], 1));
          ((AbstractDomainCanvas<?>) getCanvas()).getValues().setValue(proponent, key, v);
        }
      }
      ((AbstractDomainCanvas<?>) getCanvas()).valuesUpdated();
    }
  }

  public void paste(ValueAssignement<Ring> copy) {
    ValuationTableModel tm = (ValuationTableModel) table.getModel();
    boolean changed = false;
    for (int i = 0; i < tm.getRowCount(); i++) {
      if (getCanvas().isSand()) {
        String key = (String) (tm.getValueAt(i, 0));
        Ring value = copy.get(true, key);
        if (value != null) {
          SandNode node = new SandNode();
          Ring v =
              ((AbstractDomainCanvas<?>) getCanvas()).getValues().getDomain().getDefaultValue(node);
          if (v.updateFromString(value.toString())) {
            ((AbstractDomainCanvas<?>) getCanvas()).getValues().setValue(true, key, v);
            changed = true;
          }
        }
      }
      else {
        boolean proponent = tm.getValueAt(i, 0).equals(Options.getMsg("tablemodel.proponent"));
        String key = (String) tm.getValueAt(i, 1);
        Ring value = copy.get(proponent, key);
        if (value != null) {
          ADTNode node = new ADTNode();
          if (proponent) {
            node.setType(ADTNode.Type.AND_PRO);
          }
          else {
            node.setType(ADTNode.Type.AND_OPP);
          }
          Ring v =
              ((AbstractDomainCanvas<?>) getCanvas()).getValues().getDomain().getDefaultValue(node);
          if (v.updateFromString(value.toString())) {
            ((AbstractDomainCanvas<?>) getCanvas()).getValues().setValue(proponent, key, v);
            changed = true;
          }
        }
      }
    }
    if (changed) {
      ((AbstractDomainCanvas<?>) getCanvas()).valuesUpdated();
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

  private JPanel createTable(AbstractDomainCanvas<Ring> canvas) {
    JPanel result = new JPanel();
    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
    ValuationTableModel tableModel = new ValuationTableModel(canvas.isSand());
    tableModel.setCanvas(canvas);
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
        copyHandler.cut();
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
        copyHandler.paste();
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
    result.add(new JLabel(((AbstractDomainCanvas<?>) getCanvas()).getDomain().getName()));
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
