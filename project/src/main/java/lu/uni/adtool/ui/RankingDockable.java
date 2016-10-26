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

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.RankNode;
import lu.uni.adtool.domains.Ranker;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.SandRank;
import lu.uni.adtool.domains.ValueAssignement;
import lu.uni.adtool.domains.adtpredefined.AdtRankingDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import bibliothek.util.Path;

public class RankingDockable extends PermaDockable implements KeyListener, ListSelectionListener, ChangeListener {

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
  @SuppressWarnings("unchecked")
  public void setCanvas(AbstractTreeCanvas canvas) {
    pane.removeAll();
    this.canvas = canvas;
    if (this.getCanvas() instanceof AbstractDomainCanvas) {
      if (canvas.getTree() != null && (((AbstractDomainCanvas<Ring>) canvas)
              .getValues().getDomain() instanceof SandRank
          || (((AbstractDomainCanvas<Ring>) canvas)
              .getValues().getDomain() instanceof AdtRankingDomain))) {
        JScrollPane scrollPane =
            new JScrollPane(this.createTable((AbstractDomainCanvas<Ring>) canvas));
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

  @SuppressWarnings("unchecked")
  public void setFocus(AbstractTreeCanvas canvas, Node root, boolean recalculate) {
    if (canvas != this.canvas || this.table == null || root == null) {
      Debug.log("Setting canvas with no node - using root");
      setCanvas(canvas);
    }
    else {
      if (canvas instanceof AbstractDomainCanvas && (((AbstractDomainCanvas<Ring>) canvas)
          .getValues().getDomain() instanceof AdtRankingDomain) ||
          ((AbstractDomainCanvas<Ring>) canvas)
              .getValues().getDomain() instanceof SandRank) {
        Debug.log("Root used with name:" + root.getName());
        RankingTableModel model = ((RankingTableModel) this.table.getModel());
        if (model != null) {
          Debug.log("has model");
//           this.updateTitlePane(root);
          this.rootLabel.setText(Options.getMsg("windows.ranking.labeltitle",
              ((AbstractDomainCanvas<Ring>)canvas).getDomain().getName(), root.getName()));

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

  public void stateChanged(ChangeEvent e) {
    SpinnerModel model = this.spinner.getModel();
    Debug.log("value of spinner changed");
    if (model instanceof SpinnerNumberModel) {
      Debug.log("value of spinner updated");
      Options.rank_noRanked = ((SpinnerNumberModel)model).getNumber().intValue();
      this.setCanvas(this.canvas);
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

  // public Ring editValue(String key) {
  // Ring value;
  // InputDialog dialog;
  // value = ((AbstractDomainCanvas) getCanvas()).getValues().get(key);
  //
  // if (value instanceof Bool) {
  // value = (Ring) Bool.not((Bool) value);
  // }
  // else if (value instanceof RealG0) {
  // dialog = new RealG0Dialog(getCanvas().getFrame());
  // value = (Ring) (dialog.showInputDialog(value));
  // }
  // else if (value instanceof RealZeroOne) {
  // dialog = new RealZeroOneDialog(getCanvas().getFrame());
  // value = (Ring) (dialog.showInputDialog(value));
  // }
  // else if (value instanceof LMHValue) {
  // dialog = new LMHDialog(getCanvas().getFrame());
  // value = (Ring) (dialog.showInputDialog(value));
  // }
  // else if (value instanceof LMHEValue) {
  // dialog = new LMHEDialog(getCanvas().getFrame());
  // value = (Ring) (dialog.showInputDialog(value));
  // }
  // else if (value instanceof BoundedInteger) {
  // // if(getDomain() instanceof MinSkill){
  // // dialog = new BoundedIntegerInfDialog(getMainWindow());
  // // }
  // // else{
  // dialog = new BoundedIntegerDialog(getCanvas().getFrame());
  // // }
  // value = (Ring) (dialog.showInputDialog(value));
  // }
  // return value;
  // }

  public static final String ID_RANKINGVIEW = "rank_view";

  @SuppressWarnings("unchecked")
  private JPanel createTable(AbstractDomainCanvas<Ring> canvas) {
    JPanel result = new JPanel();
    if (canvas.isSand()) {
      this.ranker = new Ranker<Ring>((SandDomain<Ring>) canvas.getValues().getDomain());
    }
    else {
      this.ranker = new Ranker<Ring>((AdtDomain<Ring>) canvas.getValues().getDomain());
    }
    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
    RankingTableModel tableModel = new RankingTableModel();
    tableModel.setCanvas(canvas, canvas.getTree().getRoot(false), true);
    this.table = new JTable(tableModel) {
      public boolean editCellAt(int row, int column, EventObject e) {
        // scrollPane.setVisible(false);
        return false;
      }
      private static final long serialVersionUID = 4413320872836262912L;
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
    /* create title */
    this.spinner = new JSpinner(new SpinnerNumberModel(Options.rank_noRanked, 0, 999, 1));
    spinner.addChangeListener(this);
    JFormattedTextField ftf = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
    ftf.setColumns(4);
//     ftf.setHorizontalAlignment(JTextField.RIGHT);

    JPanel titlePane = new JPanel();
    titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));
    JPanel top = new JPanel();
    top.setBorder(new EmptyBorder(0, 0, 0, 0));
//     top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
    JLabel l = new JLabel(Options.getMsg("windows.ranking.labeltitleleft"));
    l.setHorizontalAlignment(SwingConstants.LEFT);
    JLabel r = new JLabel(Options.getMsg("windows.ranking.labeltitleright"));
    r.setHorizontalAlignment(SwingConstants.LEFT);
    top.add(l);
    top.add(spinner);
    top.add(r);
//     top.add(Box.createHorizontalGlue());
    top.setAlignmentX( Component.LEFT_ALIGNMENT );
//     top.setPreferredSize(new Dimension(40,100));
//     top.setMaximumSize(new Dimension(40,100));
    top.setMaximumSize( top.getPreferredSize() );
    titlePane.add(top);
    this.rootLabel = new JLabel(Options.getMsg("windows.ranking.labeltitle",
        ((AbstractDomainCanvas<Ring>) this.getCanvas()).getDomain().getName(),
                                  getCanvas().getTree().getRoot(true).getName()));
    this.rootLabel.setHorizontalAlignment(SwingConstants.LEFT);
    titlePane.add(rootLabel);
    titlePane.setAlignmentX( Component.LEFT_ALIGNMENT );
    titlePane.setBorder(new EmptyBorder(0, 0, 0, 0));

//     this.updateTitlePane(getCanvas().getTree().getRoot(true));
//     this.titlePane = new JPanel();
//     titlePane.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
//     this.titleLabel = new JLabel(Options.getMsg("windows.ranking.labeltitle",
//         ((AbstractDomainCanvas<Ring>) getCanvas()).getDomain().getName(),
//         getCanvas().getTree().getRoot(true).getName()));
    result.add(titlePane);
//     result.add(this.spinner);
    result.add(table.getTableHeader());
    result.add(table);
    result.add(Box.createVerticalGlue());
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

    private void updateRowData(AbstractDomainCanvas<Ring> canvas, Node root, boolean recalculate) {
      if (canvas == null || root == null) {
        return;
      }
      if (((AbstractDomainCanvas<Ring>) canvas)
              .getValues().getDomain() instanceof SandRank) {
        ValueAssignement<Ring> va = canvas.getValues().getValueMap();
        ArrayList<RankNode<Ring>> ranking;
        ranking = ranker.rank((SandNode) root, va, Options.rank_noRanked);
        setRowCount(0);
        int i = 0;
        if (ranking != null) {
          for (RankNode<Ring> attack : ranking) {
            i++;
            Vector<Comparable<?>> v = new Vector<Comparable<?>>();
            v.add(new Integer(i));
            v.add(attack.value);
            addRow(v);
          }
        }
      }
      else if (((AbstractDomainCanvas<Ring>) canvas).getValues()
          .getDomain() instanceof AdtRankingDomain) {
        ValueAssignement<Ring> va = canvas.getValues().getValueMap();
        ArrayList<RankNode<Ring>> ranking;
        ranking = ranker.rank((ADTNode) root, va, Options.rank_noRanked);
        setRowCount(0);
        int i = 0;
        if (ranking != null) {
          for (RankNode<Ring> attack : ranking) {
            i++;
            Vector<Comparable<?>> v = new Vector<Comparable<?>>();
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

    private static final long serialVersionUID = -1257930650408184329L;
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

    private static final long serialVersionUID = 2175461852890197527L;
  }

  @SuppressWarnings("unchecked")
  public void valueChanged(ListSelectionEvent e) {
    if (getCanvas() == null) return;
    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
    ((AbstractDomainCanvas<?>) getCanvas()).unmarkAll();
    if (!lsm.isSelectionEmpty()) {
      // Find out which indexes are selected.
      int minIndex = lsm.getMinSelectionIndex();
      // Mark
      if (canvas instanceof AbstractDomainCanvas) {
        Integer index = (Integer) (table.getModel().getValueAt(minIndex, 0));
        ranker.mark(index.intValue() - 1, (AbstractDomainCanvas<Ring>) getCanvas());
      }
    }
    getCanvas().repaint();
  }

//   @SuppressWarnings("unchecked")
//   private void updateTitlePane(Node root) {
//     Debug.log("update title page  name:"+root.getName());
// 
//     this.spinner = new JSpinner(new SpinnerNumberModel(Options.rank_noRanked, 0, 999, 1));
// //     this.spinner.getComponent(0).setPreferredSize(new Dimension(4,4));
// //     this.spinner.getComponent(1).setPreferredSize(new Dimension(4,4));
//     this.spinner.addChangeListener(this);
//     JFormattedTextField ftf = ((JSpinner.DefaultEditor)this.spinner.getEditor()).getTextField();
//     ftf.setColumns(4);
// //     ftf.setHorizontalAlignment(JTextField.RIGHT);
// 
//     this.titlePane = new JPanel();
//     titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));
//     JPanel top = new JPanel();
//     top.setBorder(new EmptyBorder(0, 0, 0, 0));
// //     top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
//     JLabel l = new JLabel(Options.getMsg("windows.ranking.labeltitleleft"));
//     l.setHorizontalAlignment(SwingConstants.LEFT);
//     JLabel r = new JLabel(Options.getMsg("windows.ranking.labeltitleright"));
//     r.setHorizontalAlignment(SwingConstants.LEFT);
//     top.add(l);
//     top.add(this.spinner);
//     top.add(r);
// //     top.add(Box.createHorizontalGlue());
//     top.setAlignmentX( Component.LEFT_ALIGNMENT );
// //     top.setPreferredSize(new Dimension(40,100));
// //     top.setMaximumSize(new Dimension(40,100));
//     top.setMaximumSize( top.getPreferredSize() );
//     this.titlePane.add(top);
//     r = new JLabel(Options.getMsg("windows.ranking.labeltitle",
//         ((AbstractDomainCanvas<Ring>) this.getCanvas()).getDomain().getName(),
//                                   root.getName()));
//     r.setHorizontalAlignment(SwingConstants.LEFT);
//     this.titlePane.add(r);
//     this.titlePane.setAlignmentX( Component.LEFT_ALIGNMENT );
//     this.titlePane.setBorder(new EmptyBorder(0, 0, 0, 0));
// //     titlePane.setMaximumSize( titlePane.getPreferredSize() );
//   }

  private JPanel             pane;
  private JSpinner           spinner;
  private AbstractTreeCanvas canvas;
  private JLabel             rootLabel;
  private JTable             table;
  private Ranker<Ring>       ranker;
}
