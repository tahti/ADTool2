package lu.uni.adtool.ui;

import lu.uni.adtool.domains.rings.Bool;
import lu.uni.adtool.domains.rings.BoundedInteger;
import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.domains.rings.LMHValue;
import lu.uni.adtool.domains.rings.RealG0;
import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import bibliothek.util.Path;

public class ValuationsDockable extends PermaDockable
    implements KeyListener, ListSelectionListener {

  public ValuationsDockable() {
    super(new Path(ID_VALUATIONVIEW), ID_VALUATIONVIEW, Options.getMsg("windows.valuations.txt"));
    this.pane = new JPanel(new BorderLayout());
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
  public void setCanvas(AbstractTreeCanvas canvas) {
    pane.removeAll();
    this.canvas = canvas;
    if (canvas instanceof AbstractDomainCanvas) {
      JScrollPane scrollPane = new JScrollPane(this.createTable((AbstractDomainCanvas) canvas));
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

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }

  public Ring editValue(boolean proponent, String key) {
    Ring value;
    InputDialog dialog;
    value = ((AbstractDomainCanvas) getCanvas()).getValues().get(proponent, key);

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

  public static final String ID_VALUATIONVIEW = "val_view";

  private JPanel createTable(AbstractDomainCanvas canvas) {
    JPanel result = new JPanel();
    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
    ValuationTableModel tableModel = new ValuationTableModel(canvas.isSand());
    tableModel.setCanvas(canvas);
    this.table = new JTable(tableModel) {
      public boolean editCellAt(int row, int column, EventObject e) {
        // scrollPane.setVisible(false);
        if (!isCellSelected(row, column)) {
          return false;
        }
        if (getCanvas().isSand()) {
          String key = (String) getValueAt(row, 0);

          int[] selection = table.getSelectedRows();
          if (selection.length > 0) {
            Ring value = editValue(true, key);
            if (value != null) {
              selection = table.getSelectedRows();
              for (int i = 0; i < selection.length; i++) {
                key = (String) getValueAt(selection[i], 0);
                ((AbstractDomainCanvas) getCanvas()).getValues().setValue(true, key, value);
              }
              ((AbstractDomainCanvas) getCanvas()).valuesUpdated();
            }
          }
        }
        else {
          String key = (String) getValueAt(row, 1);
          boolean proponent = getValueAt(row, 0).equals(Options.getMsg("tablemodel.proponent"));

          int[] selection = table.getSelectedRows();
          if (selection.length > 0) {
            Ring value = editValue(proponent, key);
            if (value != null) {
              selection = table.getSelectedRows();
              for (int i = 0; i < selection.length; i++) {
                key = (String) getValueAt(selection[i], 1);
                proponent = getValueAt(selection[i], 0).equals(Options.getMsg("tablemodel.proponent"));
                ((AbstractDomainCanvas) getCanvas()).getValues().setValue(proponent, key, value);
              }
              ((AbstractDomainCanvas) getCanvas()).valuesUpdated();
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
    };
    ListSelectionModel listSelectionModel = table.getSelectionModel();
    listSelectionModel.addListSelectionListener(this);
    table.setSelectionModel(listSelectionModel);
    table.setRowSorter(null);
    table.setAutoCreateRowSorter(false);
    table.setDefaultRenderer(Ring.class, new ValuationRenderer());
    table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));
    table.setFillsViewportHeight(true);
    result.add(new JLabel(((AbstractDomainCanvas) getCanvas()).getDomain().getName()));
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
      }
      sand = canvas.isSand();
    }

    private void updateRowData(AbstractDomainCanvas canvas) {
      if (canvas == null) {
        return;
      }
      if (sand) {
        Set<String> keys = canvas.getValues().sandKeySet();
        setRowCount(0);
        for (String key : keys) {
          Vector<Comparable> v = new Vector<Comparable>();
          v.add(key);
          v.add(canvas.getValues().get(true, key));
          addRow(v);
        }
      }
      else {
        Set<String> keys = canvas.getValues().sandKeySet();
        setRowCount(0);
        for (String key : keys) {
          Vector<Comparable> v = new Vector<Comparable>();
          v.add(Options.getMsg("tablemodel.proponent"));
          v.add(key);
          v.add(canvas.getValues().get(true, key));
          addRow(v);
        }
        keys = canvas.getValues().oppKeySet();
        for (String key : keys) {
          Vector<Comparable> v = new Vector<Comparable>();
          v.add(Options.getMsg("tablemodel.opponent"));
          v.add(key);
          v.add(canvas.getValues().get(false, key));
          addRow(v);
        }
      }
    }

    private boolean sand;
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
  }

  public void valueChanged(ListSelectionEvent e) {
    if (getCanvas() == null) return;
    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
    ((AbstractDomainCanvas) getCanvas()).unmarkAll();
    if (!lsm.isSelectionEmpty()) {
      // Find out which indexes are selected.
      int minIndex = lsm.getMinSelectionIndex();
      int maxIndex = lsm.getMaxSelectionIndex();
      for (int i = minIndex; i <= maxIndex; i++) {
        if (lsm.isSelectedIndex(i)) {
          int j = table.convertRowIndexToModel(i);
          String key = (String) (table.getModel().getValueAt(j, 0));
          ((AbstractDomainCanvas) getCanvas()).markLabel(key);
        }
      }
    }
    getCanvas().repaint();
  }

  private JPanel             pane;
  private AbstractTreeCanvas canvas;
  private JTable             table;
}
