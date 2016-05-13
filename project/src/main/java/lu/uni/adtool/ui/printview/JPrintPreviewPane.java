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
package lu.uni.adtool.ui.printview;

import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.Pageable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

/**
 * Prieview pane for pageable component.
 *
 * @author Piotr Kordy
 */
public class JPrintPreviewPane extends JPanel
    implements MouseWheelListener, KeyListener, MouseMotionListener, MouseListener {

  /**
   * Creates Prieview Panel.
   *
   * @param pageable
   *          component to preview.
   */
  public JPrintPreviewPane(final Pageable newPageable, JPrintPreviewDialog newParent) {
    dragStart = null;
    scrollPane = null;
    parent = newParent;
    setLayout(new BorderLayout());
    pageable = newPageable;
    final String[] zoomTexts = new String[zoomLevels.length];
    for (int i = 0; i < zoomLevels.length; i++) {
      zoomTexts[i] = zoomLevels[i] + "%";
    }
    c3 = new JComboBox<String>(zoomTexts);
    // Set a desired width
    c3.setPrototypeDisplayValue("XXXXX");
    c3.setMaximumSize(c3.getMinimumSize());
    c3.setSelectedIndex(scaleIndex);
    // Get icon
    final ImageIcon zoomIn = new IconFactory().createImageIcon("/icons/toolbar/zoom_in_24x24.png");
    Action zoomInAction = new AbstractAction("Zoom in", zoomIn) {
      private static final long serialVersionUID = -3304596518856743876L;

      // This method is called when the button is pressed
      public void actionPerformed(final ActionEvent evt) {
        zoomIn();
        // Perform action
      }
    };
    final ImageIcon zoomOutIcon = new IconFactory().createImageIcon("/icons/toolbar/zoom_out_24x24.png");
    Action zoomOutAction = new AbstractAction("Zoom out", zoomOutIcon) {
      private static final long serialVersionUID = -4090989151938598959L;

      // This method is called when the button is pressed
      public void actionPerformed(final ActionEvent evt) {
        zoomOut();
        // Perform action
      }
    };
    final ImageIcon zoom1Icon = new IconFactory().createImageIcon("/icons/toolbar/zoom100_24x24.png");
    Action zoom1Action = new AbstractAction("1:1 view", zoom1Icon) {
      private static final long serialVersionUID = 1997054292801905527L;

      // This method is called when the button is pressed
      public void actionPerformed(final ActionEvent evt) {
        resetZoom();
        // Perform action
      }
    };
    final ImageIcon printIcon = new IconFactory().createImageIcon("/icons/toolbar/print_24x24.png");

    Action printAction = new AbstractAction(Options.getMsg("toolbar.print"), printIcon) {
      private static final long serialVersionUID = 7813036159028737943L;

      // This method is called when the button is pressed
      public void actionPerformed(final ActionEvent evt) {
        print();
      }
    };
    final ImageIcon printSetupIcon = new IconFactory().createImageIcon("/icons/toolbar/printerSetup_24x24.png");

    final JButton print = new JButton(printAction);
    print.setText(null);
    toolbar.add(print);
    print.setMargin(new Insets(0, 0, 0, 0));

    Action printSetupAction = new AbstractAction("Printer Setup", printSetupIcon) {
      private static final long serialVersionUID = 6649324943641291125L;

      // This method is called when the button is pressed
      public void actionPerformed(final ActionEvent evt) {
        ((AbstractTreeCanvas) pageable).showPrintDialog(false);
        refreshContent();
        requestFocus();
      }
    };
    final JButton printSetup = new JButton(printSetupAction);
    printSetup.setText(null);
    toolbar.add(printSetup);
    printSetup.setMargin(new Insets(0, 0, 0, 0));

    toolbar.add(Box.createHorizontalStrut(10));

    final JButton c1 = new JButton(zoomOutAction);
    c1.setText(null);
    c1.setMargin(new Insets(0, 0, 0, 0));
    toolbar.add(c1);

    final JButton c1a = new JButton(zoom1Action);
    c1a.setText(null);
    c1a.setMargin(new Insets(0, 0, 0, 0));
    toolbar.add(c1a);

    // Add a toggle button; remove the label and margin before adding
    final JButton c2 = new JButton(zoomInAction);
    c2.setText(null);
    c2.setMargin(new Insets(0, 0, 0, 0));
    toolbar.add(c2);

    toolbar.add(Box.createHorizontalStrut(10));
    // Add a combobox

    toolbar.add(c3);
    c3.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        // Perform action
        scaleIndex = c3.getSelectedIndex();
        for (Object obj : content.getComponents()) {
          if (obj instanceof JPrintPreviewPage) {
            ((JPrintPreviewPage) obj).setScale(zoomLevels[scaleIndex] / 100.0,
                zoomLevels[scaleIndex] / 100.0);
          }
        }
        content.revalidate();
        requestFocus();
      }
    });
    toolbar.add(Box.createHorizontalStrut(10));
    toolbar.add(new JLabel(Options.getMsg("toolbar.noPages")));
    toolbar.add(new InputPages(this));
    toolbar.add(Box.createHorizontalStrut(6));
    parent.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JButton close = new JButton();
    close.setText(Options.getMsg("toolbar.close"));

    close.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        closeActionPerformed();
      }
    });
    toolbar.add(close);

    add(toolbar, BorderLayout.NORTH);
    createContent();
    setFocusTraversalKeysEnabled(true);
    addKeyListener(this);
    setFocusable(true);
    requestFocus();
  }

  public void refreshContent() {
    remove(scrollPane);
    createContent();
  }

  /**
   * Set the zoom to 100%.
   *
   */
  private void resetZoom() {
    scaleIndex = 5;
    scaleUpdated();
    requestFocus();
  }

  private void zoomIn() {
    scaleIndex--;
    scaleIndex = Math.max(0, scaleIndex);
    scaleUpdated();
    requestFocus();
  }

  private void zoomOut() {
    scaleIndex++;
    scaleIndex = Math.min(zoomLevels.length - 1, scaleIndex);
    scaleUpdated();
    requestFocus();
  }

  /**
   * Method called when zoom has changed.
   *
   */
  private void scaleUpdated() {
    c3.setSelectedIndex(scaleIndex);
    for (Object obj : content.getComponents()) {
      if (obj instanceof JPrintPreviewPage) {
        ((JPrintPreviewPage) obj).setScale(zoomLevels[scaleIndex] / 100.0,
            zoomLevels[scaleIndex] / 100.0);
      }
    }
  }

  /**
   * Gets the toolbar for this instance.
   *
   * @return The toolbar.
   */
  public JToolBar getToolbar() {
    return this.toolbar;
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    final int notches = e.getWheelRotation();
    if (notches < 0) {
      zoomIn();
    }
    else {
      zoomOut();
    }
  }

  /**
   * Scrolls to the given point on the canvas.
   *
   * @param x
   *          x-coordinate of the point.
   * @param y
   *          y-coordinate of the point.
   */
  public void scrollBy(int x, int y) {
    JScrollBar bar = scrollPane.getVerticalScrollBar();
    bar.setValue(bar.getValue() - y);
    bar = scrollPane.getHorizontalScrollBar();
    bar.setValue(bar.getValue() - x);
  }

  private void createScrollPane() {
    scrollPane = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.getVerticalScrollBar().setUnitIncrement(25);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(25);
    add(scrollPane, BorderLayout.CENTER);
    scrollPane.setWheelScrollingEnabled(false);
    scrollPane.addMouseWheelListener(this);
    scrollPane.addMouseMotionListener(this);
    scrollPane.addMouseListener(this);
  }

  /**
   * {@inheritDoc}
   *
   * @see KeyListener#keyPressed(KeyEvent)
   */
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
    case KeyEvent.VK_ENTER:
      break;
    case KeyEvent.VK_UP:
      scrollUnit(scrollPane.getVerticalScrollBar(), -1);
      break;
    case KeyEvent.VK_DOWN:
      scrollUnit(scrollPane.getVerticalScrollBar(), 1);
      break;
    case KeyEvent.VK_LEFT:
      scrollUnit(scrollPane.getHorizontalScrollBar(), -1);
      break;
    case KeyEvent.VK_RIGHT:
      scrollUnit(scrollPane.getHorizontalScrollBar(), 1);
      break;
    case KeyEvent.VK_PAGE_UP:
      if (e.isShiftDown()) {
        scrollBlock(scrollPane.getHorizontalScrollBar(), -1);
      }
      else {
        scrollBlock(scrollPane.getVerticalScrollBar(), -1);
      }
      break;
    case KeyEvent.VK_PAGE_DOWN:
      if (e.isShiftDown()) {
        scrollBlock(scrollPane.getHorizontalScrollBar(), 1);
      }
      else {
        scrollBlock(scrollPane.getVerticalScrollBar(), 1);
      }
      break;
    case KeyEvent.VK_PLUS:
    case KeyEvent.VK_ADD:
    case KeyEvent.VK_EQUALS:
      zoomIn();
      break;
    case KeyEvent.VK_SUBTRACT:
    case KeyEvent.VK_MINUS:
      zoomOut();
      break;
    case KeyEvent.VK_ESCAPE:
      closeActionPerformed();
      break;
    case KeyEvent.VK_P:
      print();
      break;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see KeyListener#keyTyped(KeyEvent)
   */
  public void keyTyped(final KeyEvent e) {

  }

  /**
   * {@inheritDoc}
   *
   * @see KeyListener#keyReleased(KeyEvent)
   */
  public void keyReleased(final KeyEvent e) {

  }

  /**
   * Initialize content.
   *
   * @return new panel with content.
   */
  private void createContent() {
    content = new JPanel();
    Point dim = ((AbstractTreeCanvas) pageable).getColsRows(pageable.getNumberOfPages());
    int x = (int) dim.getX();
    int y = (int) dim.getY();
    content.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    content.setBackground(Options.printview_background);
    c.insets = new Insets(0, 8, 0, 0);
    for (int j = 0; j < y; j++) {
      for (int i = 0; i < x; i++) {
        // panel.add(Box.createRigidArea(new Dimension(5, 5)));
        c.gridx = i;
        c.gridy = j;
        JPrintPreviewPage p = new JPrintPreviewPage(pageable, j * x + i);
        p.setScale(zoomLevels[scaleIndex] / 100.0, zoomLevels[scaleIndex] / 100.0);
        content.add(p, c);
      }
      createScrollPane();
    }
    revalidate();
  }

  /**
   * Close action performed.
   *
   * @param evt
   *          the evt
   */
  private void closeActionPerformed() {
    parent.dispose();
  }

  private void scrollUnit(JScrollBar bar, int direction) {
    int amount = bar.getUnitIncrement(direction);
    bar.setValue(bar.getValue() + direction * amount);
  }

  private void scrollBlock(JScrollBar bar, int direction) {
    int amount = bar.getBlockIncrement(direction);
    bar.setValue(bar.getValue() + direction * amount);
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseClicked(MouseEvent)
   */
  public void mouseClicked(final MouseEvent e) {
    requestFocusInWindow();
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mousePressed(MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    dragStart = new Point(e.getX(), e.getY());
    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseMotionListener#mouseMoved(MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
  }

  /**
   * {@inheritDoc}
   *
   * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
   */
  public void mouseDragged(MouseEvent e) {
    if (dragStart != null) {
      scrollBy((int) e.getX() - (int) dragStart.getX(), (int) (e.getY() - (int) dragStart.getY()));
      dragStart = new Point(e.getX(), e.getY());
      // ((Point) dragStart).translate((int) -p.getX(), (int) -p.getY());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseReleased(MouseEvent)
   */
  public void mouseReleased(final MouseEvent e) {
    dragStart = null;
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    // canvas.revalidate();
    // this.canvas.setLastLeftPos(this.canvas.getLeftPos());
    // this.canvas.setLastLevel(this.canvas.getLevel());
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseExited(MouseEvent)
   */
  public void mouseExited(final MouseEvent e) {
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseEntered(MouseEvent)
   */
  public void mouseEntered(final MouseEvent e) {
  }

  private void print() {
    if (((AbstractTreeCanvas) pageable).showPrintDialog(true)) {
      refreshContent();
      closeActionPerformed();
    }
    scrollPane.requestFocus();
  }

  /** Generated serialVersionUID */
  private static final long   serialVersionUID = 1446429545528508602L;
  private static final int[]  zoomLevels       =
      new int[] {1000, 500, 300, 250, 200, 150, 100, 75, 50, 25, 15, 10, 5};
  private JPanel              content;
  private final JToolBar      toolbar          = new JToolBar();
  private JScrollPane         scrollPane;
  private int                 scaleIndex       = 6;
  private JComboBox<String>   c3;
  private Pageable            pageable;
  private JPrintPreviewDialog parent;
  private Point               dragStart;
}
