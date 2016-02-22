package lu.uni.adtool.ui.canvas;

import lu.uni.adtool.tree.Node;

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
    this.dragScroll = false;
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
    if (e.isControlDown() && node != null) {
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
    if (this.canvas.getNode(e.getX(), e.getY()) != null) {
      this.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      dragScroll = false;
    }
    else {
      this.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      dragScroll = true;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
   */
  public void mouseDragged(MouseEvent e) {
    if (dragStart != null) {
      if (dragScroll) {
        Point p = this.canvas.scrollTo(e.getX() - dragStart.getX(), (e.getY() - dragStart.getY()));
        dragStart = new Point(e.getX(), e.getY());
        ((Point) dragStart).translate((int) -p.getX(), (int) -p.getY());
      }
      else {
        Point2D p = new Point(e.getX(), e.getY());
        p = this.canvas.transform(p);
        Point2D p2 = this.canvas.transform(dragStart);
        this.canvas.moveTree(p.getX() - p2.getX(), p.getY() - p2.getY());
        dragStart = new Point(e.getX(), e.getY());
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseReleased(MouseEvent)
   */
  public void mouseReleased(final MouseEvent e) {
    dragStart = null;
    dragScroll = true;
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
   * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
   */
  public void mouseMoved(final MouseEvent e) {
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

  protected AbstractTreeCanvas canvas;

  protected Node               menuNode;
  protected JPopupMenu         pmenu;
  private Point2D              dragStart;
  private boolean              dragScroll;

}
