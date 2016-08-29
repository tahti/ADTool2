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
package lu.uni.adtool.ui.canvas;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.ui.InfoBalloon;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BoundedRangeModel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * Abstract mouse and keyboard handler for the this.canvas.
 *
 * @author Piotr Kordy
 */
public abstract class AbstractCanvasHandler implements MouseListener, KeyListener,
    MouseWheelListener, MouseMotionListener, ComponentListener {

  /**
   * Constructs a new instance.
   *
   * @param canvas
   */
  public AbstractCanvasHandler(final AbstractTreeCanvas canvas) {
    this.canvas = canvas;
    this.dragStart = null;
    // this.dragScroll = false;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.awt.event.MouseWheelListener#mouseWheelMoved(MouseWheelEvent) We
   *      zoom when we use mouse wheel.
   */
  public void mouseWheelMoved(final MouseWheelEvent e) {
    final JScrollPane scrollPane = this.canvas.getScrollPane();
    final int notches = e.getWheelRotation();
    final Rectangle pos = scrollPane.getViewport().getViewRect();
    double scale = this.canvas.getScale();
    JScrollBar bar = scrollPane.getHorizontalScrollBar();
    if (bar.isVisible() && e.getY() > pos.getHeight()
        && e.getY() < pos.getHeight() + bar.getPreferredSize().height) {
      bar.setValue(bar.getValue()
          + (int) (scale * (notches + 4 * Math.signum(notches)) + Math.signum(notches)));
      return;
    }
    bar = scrollPane.getVerticalScrollBar();
    if (bar.isVisible() && e.getX() > pos.getWidth()
        && e.getX() < pos.getWidth() + bar.getPreferredSize().width) {
      bar.setValue(bar.getValue()
          + (int) (scale * (notches + 4 * Math.signum(notches)) + Math.signum(notches)));
      return;
    }

    if (notches < 0) {
      this.canvas.zoomIn();
    }
    else {
      this.canvas.zoomOut();
    }
    scale = this.canvas.getScale() / scale;

    int xPos = (int) (pos.getX() + ((pos.getX() + e.getX()) * (scale - 1)));
    int yPos = (int) (pos.getY() + ((pos.getY() + e.getY()) * (scale - 1)));
    int moveX = 0;
    int moveY = 0;
    final BoundedRangeModel mX = scrollPane.getHorizontalScrollBar().getModel();
    final BoundedRangeModel mY = scrollPane.getVerticalScrollBar().getModel();
    final double maxScrollX = mX.getMaximum() - mX.getExtent();
    final double maxScrollY = mY.getMaximum() - mY.getExtent();
    if (xPos > maxScrollX) {
      moveX = xPos - (int) maxScrollX;
      xPos = (int) maxScrollX;
    }
    else if (xPos < 0) {
      moveX = xPos;
      xPos = 0;
    }
    if (yPos > maxScrollY) {
      moveY = yPos - (int) maxScrollY;
      yPos = (int) maxScrollY;
    }
    else if (yPos < 0) {
      moveY = yPos;
      yPos = 0;
    }
    scrollPane.getViewport().setViewPosition(new Point(xPos, yPos));
    this.canvas.moveTree(-moveX / this.canvas.getScale(), -moveY / this.canvas.getScale());
  }

  /**
   * {@inheritDoc}
   *
   * @see KeyListener#keyPressed(KeyEvent)
   */
  public void keyPressed(KeyEvent e) {
    final Node node = this.canvas.getFocused();
    if (e.isControlDown()) {
      switch (e.getKeyCode()) {
      case KeyEvent.VK_T:
        // expand node
        break;
      case KeyEvent.VK_PLUS:
      case KeyEvent.VK_ADD:
      case KeyEvent.VK_EQUALS:
        this.canvas.zoomIn();
        break;
      case KeyEvent.VK_MINUS:
      case KeyEvent.VK_SUBTRACT:
        this.canvas.zoomOut();
        break;
      case KeyEvent.VK_O:
        this.canvas.resetZoom();
        break;
      default:

      }
    }
    else {
      Node tempNode = null;
      switch (e.getKeyCode()) {
      case KeyEvent.VK_UP:
        tempNode = this.canvas.getParentNode(node);
        break;
      case KeyEvent.VK_LEFT:
        tempNode = this.canvas.getLeftSibling(node);
        break;
      case KeyEvent.VK_RIGHT:
        tempNode = this.canvas.getRightSibling(node);
        break;
      case KeyEvent.VK_DOWN:
        tempNode = this.canvas.getMiddleChild(node);
        break;
      // case KeyEvent.VK_DELETE:
      // this.canvas.removeNode(node);
      // break;
      default:
      }
      if (tempNode != null) {
        setFocus(tempNode);
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseClicked(MouseEvent)
   */
  public void mouseClicked(final MouseEvent e) {
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mousePressed(MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    dragStart = new Point(e.getX(), e.getY());
    this.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    // if (this.canvas.getNode(e.getX(), e.getY()) != null) {
    // this.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    // dragScroll = false;
    // }
    // else {
    // this.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    // dragScroll = true;
    // }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
   */
  public void mouseDragged(MouseEvent e) {
    if (dragStart != null) {
      // if (dragScroll) {
      Point p1 = this.canvas.scrollTo(e.getX() - dragStart.getX(), e.getY() - dragStart.getY());
//       dragStart = new Point(e.getX(), e.getY());
//       ((Point) dragStart).translate((int) - p1.getX(), (int) - p1.getY());
      // }
      // else {
      Point2D p = new Point(e.getX(), e.getY());
      ((Point) p).translate((int) - p1.getX(), (int) - p1.getY());
      p = this.canvas.transform(p);
      Point2D p2 = this.canvas.transform(dragStart);
      this.canvas.moveTree(p.getX() - p2.getX(), p.getY() - p2.getY());
      dragStart = new Point(e.getX(), e.getY());
      ((Point) dragStart).translate((int) - p1.getX(), (int) - p1.getY());
      // }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseReleased(MouseEvent)
   */
  public void mouseReleased(final MouseEvent e) {
    dragStart = null;
    // dragScroll = true;
    this.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    this.canvas.repaint();
  }

  /**
   * Set new focus and update context menu visibility.
   *
   * @param node
   */
  public void setFocus(final Node node) {
    this.canvas.setFocus(node);
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
   * {@inheritDoc}
   *
   * @see MouseListener#mouseEntered(MouseEvent)
   */
  public void mouseEntered(final MouseEvent e) {
    final Runnable tooltipShower = new Runnable() {
      public void run() {
        Node node = canvas.getNode(mouseX, mouseY);
        if (node != null && node.getComment().equals("") ){
          node = null;
        }
        if (node != null )  {
            balloon.showBalloon(canvas.getController().getFrame(), node);
        }
        else if (node == null) {
          balloon.hideBalloon();
        }
      }
    };
    balloonHandler = scheduler.scheduleAtFixedRate(tooltipShower, Options.canv_tooltipTime,
        Options.canv_tooltipTime, TimeUnit.MILLISECONDS);
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseExited(MouseEvent)
   */
  public void mouseExited(final MouseEvent e) {
    if (balloon.isVisible()) {
      balloon.hideBalloon();
    }
    balloonHandler.cancel(true);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
   */
  public void mouseMoved(final MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  public void componentHidden(ComponentEvent e) {
  }

  public void componentMoved(ComponentEvent e) {
  }

  public void componentResized(ComponentEvent e) {
    this.canvas.setViewPortSize(((JScrollPane) e.getComponent()).getViewport().getExtentSize());
  }

  public void componentShown(ComponentEvent e) {
  }

  protected AbstractTreeCanvas           canvas;

  protected Node                         menuNode;
  protected JPopupMenu                   pmenu;
  private Point2D                        dragStart;
//   protected Node                         hoverNode   = null;
//   public boolean                      windowShown = false;
  protected InfoBalloon                  balloon     = new InfoBalloon();
  private final ScheduledExecutorService scheduler   = Executors.newScheduledThreadPool(1);
  protected Future<?>                    balloonHandler;
  protected double                       mouseX, mouseY;
  
  protected boolean                      ping;
  // private boolean dragScroll;

}
