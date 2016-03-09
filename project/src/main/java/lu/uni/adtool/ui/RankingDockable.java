package lu.uni.adtool.ui;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.RankNode;
import lu.uni.adtool.domains.Ranker;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValueAssignement;
import lu.uni.adtool.domains.adtpredefined.RankingDomain;
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
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import bibliothek.util.Path;

public class RankingDockable extends PermaDockable implements KeyListener, ListSelectionListener {

  public RankingDockable() {
    super(new Path(ID_RANKINGVIEW), ID_RANKINGVIEW, Options.getMsg("windows.ranking.txt"));
    this.pane = new JPanel(new BorderLayout());
    pane.add(getEmptyMessage());
    add(pane);
    ImageIcon icon = new IconFactory().createImageIcon("/icons/ranking_16x16.png",
        Options.getMsg("windows.ranking.txt"));
    this.setTitleIcon(icon);
  }

  /**
   * @param canvas
   *          the canvas to set
   */
  public void setCanvas(AbstractTreeCanvas canvas) {
    pane.removeAll();
    this.canvas = canvas;
    if (this.getCanvas() instanceof AbstractDomainCanvas) {
      if (canvas.getTree() != null && (canvas.isSand() || (canvas instanceof AbstractDomainCanvas
          && ((AbstractDomainCanvas) canvas).getValues().getDomain() instanceof RankingDomain))) {
        JScrollPane scrollPane = new JScrollPane(this.createTable((AbstractDomainCanvas) canvas));
        pane.add(scrollPane);
        pane.revalidate();
      }
      else {
        pane.add(getNotImplementedMsg());
      }
    }
    else {
      pane.add(getEmptyMessage());
    }
    pane.revalidate();
  }

  public void setFocus(AbstractTreeCanvas canvas, Node root, boolean recalculate) {
    if (canvas != this.canvas || this.table == null || root == null) {
      Debug.log("Setting canvas with no node - using root");
      setCanvas(canvas);
    }
    else {
      if (canvas.isSand() || (canvas instanceof AbstractDomainCanvas
          && ((AbstractDomainCanvas) canvas).getValues().getDomain() instanceof RankingDomain)) {
        Debug.log("Root used with name:" + root.getName());
        RankingTableModel model = ((RankingTableModel) this.table.getModel());
        if (model != null) {
          this.titleLabel.setText(Options.getMsg("windows.ranking.labeltitle",
              ((AbstractDomainCanvas) getCanvas()).getDomain().getName(), root.getName()));

          model.setCanvas((AbstractDomainCanvas<Ring>) canvas, root, recalculate);
        }
        else {
          setCanvas(canvas);
        }
      }
      else {
        // TODO - put message?
      }
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

//   public Ring editValue(String key) {
//     Ring value;
//     InputDialog dialog;
//     value = ((AbstractDomainCanvas) getCanvas()).getValues().get(key);
// 
//     if (value instanceof Bool) {
//       value = (Ring) Bool.not((Bool) value);
//     }
//     else if (value instanceof RealG0) {
//       dialog = new RealG0Dialog(getCanvas().getFrame());
//       value = (Ring) (dialog.showInputDialog(value));
//     }
//     else if (value instanceof RealZeroOne) {
//       dialog = new RealZeroOneDialog(getCanvas().getFrame());
//       value = (Ring) (dialog.showInputDialog(value));
//     }
//     else if (value instanceof LMHValue) {
//       dialog = new LMHDialog(getCanvas().getFrame());
//       value = (Ring) (dialog.showInputDialog(value));
//     }
//     else if (value instanceof LMHEValue) {
//       dialog = new LMHEDialog(getCanvas().getFrame());
//       value = (Ring) (dialog.showInputDialog(value));
//     }
//     else if (value instanceof BoundedInteger) {
//       // if(getDomain() instanceof MinSkill){
//       // dialog = new BoundedIntegerInfDialog(getMainWindow());
//       // }
//       // else{
//       dialog = new BoundedIntegerDialog(getCanvas().getFrame());
//       // }
//       value = (Ring) (dialog.showInputDialog(value));
//     }
//     return value;
//   }

  public static final String ID_RANKINGVIEW = "rank_view";

  private JPanel createTable(AbstractDomainCanvas<Ring> canvas) {
    JPanel result = new JPanel();
    if (canvas.isSand()) {
      this.ranker = new Ranker((SandDomain<Ring>) canvas.getValues().getDomain());
    }
    else {
      this.ranker = new Ranker((AdtDomain<Ring>) canvas.getValues().getDomain());
    }
    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
    RankingTableModel tableModel = new RankingTableModel();
    tableModel.setCanvas(canvas, canvas.getTree().getRoot(false), true);
    this.table = new JTable(tableModel) {
      public boolean editCellAt(int row, int column, EventObject e) {
        // scrollPane.setVisible(false);
        return false;
      }
    };
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel listSelectionModel = table.getSelectionModel();
    listSelectionModel.addListSelectionListener(this);
    table.setSelectionModel(listSelectionModel);
    table.setRowSorter(null);
    table.setAutoCreateRowSorter(false);
    table.setDefaultRenderer(Ring.class, new RankingRenderer());
    // table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));
    table.setFillsViewportHeight(true);
    this.titleLabel = new JLabel(Options.getMsg("windows.ranking.labeltitle",
        ((AbstractDomainCanvas) getCanvas()).getDomain().getName(),
        getCanvas().getTree().getRoot(true).getName()));
    result.add(titleLabel);
    result.add(table.getTableHeader());
    result.add(table);
    return result;
  }

  private JLabel getEmptyMessage() {
    JLabel result;
    result = new JLabel(Options.getMsg("windows.valuations.nodomain"));
    return result;
  }

  private JLabel getNotImplementedMsg() {
    JLabel result;
    result = new JLabel("This funtionality is not implemented yet for this type of domain");
    return result;
  }

  class RankingTableModel extends DefaultTableModel {

    public RankingTableModel() {
      super();
      super.addColumn(Options.getMsg("tablemodel.number"));
      super.addColumn(Options.getMsg("tablemodel.calculated"));
    }

    /**
     * {@inheritDoc}
     *
     * @see TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0) {
        return Integer.class;
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
      return false;
    }

    public void setCanvas(AbstractDomainCanvas<Ring> canvas, Node root, boolean recalculate) {
      // this.canvas = canvas;
      if (canvas != null) {
        updateRowData(canvas, root, recalculate);
        fireTableDataChanged();
      }
    }

    private void updateRowData(AbstractDomainCanvas canvas, Node root, boolean recalculate) {
      if (canvas == null || root == null) {
        return;
      }
      if (canvas.isSand()) {
        ValueAssignement<Ring> va = canvas.getValues().getValueMap();
        ArrayList<RankNode<Ring>> ranking;
        ranking = ranker.rank((SandNode) root, va, Options.rank_noRanked);
        setRowCount(0);
        int i = 0;
        if (ranking != null) {
          for (RankNode<Ring> attack : ranking) {
            i++;
            Vector<Comparable> v = new Vector<Comparable>();
            v.add(new Integer(i));
            v.add(attack.value);
            addRow(v);
          }
        }
      }
      else if (((AbstractDomainCanvas) canvas).getValues().getDomain() instanceof RankingDomain) {
        ValueAssignement<Ring> va = canvas.getValues().getValueMap();
        ArrayList<RankNode<Ring>> ranking;
        ranking = ranker.rank((ADTNode) root, va, Options.rank_noRanked);
        setRowCount(0);
        int i = 0;
        if (ranking != null) {
          for (RankNode<Ring> attack : ranking) {
            i++;
            Vector<Comparable> v = new Vector<Comparable>();
            v.add(new Integer(i));
            v.add(attack.value);
            addRow(v);
          }
        }
      }
      else {
        setRowCount(0);
      }
    }
  }

  /**
   * Renderer for table cells.
   */
  class RankingRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

    public RankingRenderer() {
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
      // Mark
      if (canvas instanceof AbstractDomainCanvas) {
        Integer index = (Integer) (table.getModel().getValueAt(minIndex, 0));
        ranker.mark(index.intValue() - 1, (AbstractDomainCanvas) getCanvas());
      }
    }
    getCanvas().repaint();
  }

  private JPanel             pane;
  private AbstractTreeCanvas canvas;
  private JLabel             titleLabel;
  private JTable             table;
  private Ranker             ranker;
}
