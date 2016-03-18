package lu.uni.adtool.ui.canvas;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.GuiNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * A mouse handler for SandDomainCanvas.
 *
 * @author Piot Kordy
 */
public class SandDomainHandler<Type extends Ring> extends AbstractCanvasHandler {
  public SandDomainHandler(final AbstractDomainCanvas<Type> canvas) {
    super(canvas);
    initPopupMenu();
  }

  public void setFocus(final Node node) {
    if (node != null) {
      // final ADTNode.Type t = node.getTerm().getType();
      editValueItem.setEnabled(((SandNode) node).isEditable());
      boolean canFold;
      boolean canFoldAbove;
      if (((GuiNode) node).isFolded()) {
        toggleFold.setText(Options.getMsg("handler.expandbelow.txt"));
        canFold = true;
      }
      else {
        toggleFold.setText(Options.getMsg("handler.foldbelow.txt"));
        canFold = (node.getChildren().size() > 0);
      }
      if (((GuiNode) node).isAboveFolded()) {
        toggleAboveFold.setText(Options.getMsg("handler.expandabove.txt"));
        canFoldAbove = true;
      }
      else {
        toggleAboveFold.setText(Options.getMsg("handler.foldabove.txt"));
        canFoldAbove = (node.getParent() != null);
      }
      toggleAboveFold.setVisible(canFoldAbove);
      toggleFold.setVisible(canFold);
      separator.setVisible(canFold && canFoldAbove);
      pmenu.pack();
    }
    super.setFocus(node);
  }

  public void keyPressed(final KeyEvent e) {
    boolean consume = true;
    final GuiNode node = canvas.getFocused();
    if (node == null) {
      return;
    }
    else {
      menuNode = node;
    }
    if (e.isControlDown()) {
      consume = false;
    }
    else {
      if (e.isShiftDown()) {

        switch (e.getKeyCode()) {
        case KeyEvent.VK_SPACE:
          if (node != null) {
            menuNode = node;
            canvas.toggleAboveFold(menuNode);
          }
          break;
        default:
          consume = false;
          break;
        }
      }
      else {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
          if (node != null) {
            menuNode = node;
            changeValueActionPerformed();
          }
          break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_ADD:
        case KeyEvent.VK_EQUALS:
          canvas.zoomIn();
          break;
        case KeyEvent.VK_MINUS:
        case KeyEvent.VK_SUBTRACT:
          canvas.zoomOut();
          break;
        case KeyEvent.VK_O:
          canvas.resetZoom();
          break;
        case KeyEvent.VK_SPACE:
          if (node != null) {
            menuNode = node;
            canvas.toggleFold(menuNode);
          }
          break;
        default:
          consume = false;
        }
      }
    }
    if (!consume) {
      super.keyPressed(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseClicked(MouseEvent)
   */
  @SuppressWarnings("unchecked")
  public void mouseClicked(final MouseEvent e) {
    canvas.requestFocusInWindow();
    final Node node = this.canvas.getNode(e.getX(), e.getY());
    if (node != null) {
      if (e.getModifiers() == InputEvent.BUTTON3_MASK || e.getModifiers() == InputEvent.CTRL_MASK) {
        menuNode = node;
        this.pmenu.show(e.getComponent(), e.getX(), e.getY());
        setFocus(node);
      }
      else {
        if (node.equals(canvas.getFocused())) {
          menuNode = node;
          ((AbstractDomainCanvas<Type>) canvas).editValue(menuNode);
        }
        else {
          setFocus(node);
        }
      }
    }
  }

  /**
   * Initialise context menu.
   *
   */
  private void initPopupMenu() {
    this.menuNode = null;
    this.pmenu = new JPopupMenu();
    editValueItem = new JMenuItem(Options.getMsg("handler.editvalue.txt"));
    editValueItem.setAccelerator(KeyStroke.getKeyStroke('\n'));
    editValueItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        changeValueActionPerformed();
      }
    });
    pmenu.add(editValueItem);
    separator = new JSeparator();
    pmenu.add(separator);
    toggleAboveFold = new JMenuItem(Options.getMsg("handler.foldabove.txt"));
    toggleAboveFold.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.foldabove.key")));
    toggleAboveFold.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          canvas.toggleAboveFold(menuNode);
        }
      }
    });
    pmenu.add(toggleAboveFold);
    toggleFold = new JMenuItem(Options.getMsg("handler.foldbelow.txt"));
    toggleFold.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.foldbelow.key")));
    toggleFold.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          canvas.toggleFold(menuNode);
        }
      }
    });
    pmenu.add(toggleFold);

  }

  @SuppressWarnings("unchecked")
  private void changeValueActionPerformed() {
    if (menuNode != null) {
      ((AbstractDomainCanvas<Type>) canvas).editValue(menuNode);
    }
  }

  private JMenuItem  toggleAboveFold;
  private JMenuItem  toggleFold;
  private JSeparator separator;
  private JMenuItem  editValueItem;

}
