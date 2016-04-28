package lu.uni.adtool.ui.printview;

import lu.uni.adtool.tools.Options;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

/**
 * Prieview pane for pageable component.
 *
 * @author Piotr Kordy
 */
public class InputPages extends JTextField implements MouseListener, FocusListener, KeyListener {

  /**
   * Constructs a new instance.
   */
  public InputPages(JPrintPreviewPane pane) {
    super(new Integer(Options.print_noPages).toString());
    parent = pane;
    addKeyListener(this);
    addFocusListener(this);
    addMouseListener(this);
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseClicked(MouseEvent)
   */
  public void mouseClicked(MouseEvent e) {
    setEditable(true);
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseEntered(MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseExited(MouseEvent)
   */
  public void mouseExited(MouseEvent e) {
    // setEditable(false);
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mousePressed(MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseReleased(MouseEvent)
   */
  public void mouseReleased(MouseEvent e) {
  }

  /**
   * {@inheritDoc}
   *
   * @see FocusListener#focusGained(FocusEvent)
   */
  public void focusGained(FocusEvent e) {
    setEditable(true);
  }

  /**
   * {@inheritDoc}
   *
   * @see FocusListener#focusLost(FocusEvent)
   */
  public void focusLost(FocusEvent e) {
    updateData();
  }

  /**
   * {@inheritDoc}
   *
   * @see KeyListener#keyPressed(KeyEvent)
   */
  public void keyPressed(KeyEvent e) {
    if (e.isControlDown()) {
      switch (e.getKeyCode()) {
      }
    }
    else {
      switch (e.getKeyCode()) {
      case KeyEvent.VK_ENTER:
        updateData();
        break;
      case KeyEvent.VK_ESCAPE:
        setText(new Integer(Options.print_noPages).toString());
        setEditable(false);
        break;
      }
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
   * Try to set number of pages from the text field.
   *
   */
  private void updateData() {
    try {
      int noPages = Integer.parseInt(getText());
      if (noPages < 1 && noPages > 5000) {
        throw new NumberFormatException(Options.getMsg("error.numberPages"));
      }
      if (Options.print_noPages != noPages) {
        Options.print_noPages = noPages;
        parent.refreshContent();
      }
    }
    catch (NumberFormatException e) {
      setText(new Integer(Options.print_noPages).toString());
    }
    parent.requestFocus();
  }
  private static final long serialVersionUID = -8002905579485573764L;
  private JPrintPreviewPane parent;
}
