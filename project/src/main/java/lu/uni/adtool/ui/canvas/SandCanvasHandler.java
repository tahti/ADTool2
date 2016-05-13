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
import lu.uni.adtool.tree.GuiNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.ui.MultiLineInput;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * A handler for SandTreeCanvas.
 *
 * @author Piotr Kordy
 */
public class SandCanvasHandler extends AbstractCanvasHandler {
  /**
   * Constructs a new instance.
   *
   * @param canvas
   *          parent canvas
   */
  public SandCanvasHandler(final SandTreeCanvas<?> canvas) {
    super(canvas);
    initPopupMenu();
  }

  /**
   * {@inheritDoc}
   *
   * @see KeyListener#keyPressed(KeyEvent)
   */
  public void keyPressed(final KeyEvent e) {
    boolean consume = true;
    final Node node = this.canvas.getFocused();
    if (e.isControlDown()) {
      if (node != null) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
          ((SandTreeCanvas<?>) this.canvas).switchSibling(node, true);
          break;
        case KeyEvent.VK_RIGHT:
          ((SandTreeCanvas<?>) this.canvas).switchSibling(node, false);
          break;
        case KeyEvent.VK_J:
          ((SandTreeCanvas<?>) canvas).toggleOp(node);
          break;
        // case KeyEvent.VK_I:
        // canvas.addCounter(node);
        // break;
        case KeyEvent.VK_A:
          ((SandTreeCanvas<?>) canvas).addChild(node);
          break;
        case KeyEvent.VK_L:
          menuNode = node;
          changeLabelActionPerformed();
          break;
        case KeyEvent.VK_R:
          ((SandTreeCanvas<?>) canvas).removeTree(node);
          break;
        case KeyEvent.VK_S:
          ((SandTreeCanvas<?>) canvas).addSibling(node, !e.isShiftDown());
          break;
        default:
          consume = false;
        }
      }
      else {
        consume = false;
      }
    }
    else if (e.isShiftDown()) {
      if (node != null) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_SPACE:
          if (node != null) {
            menuNode = node;
            canvas.toggleAboveFold(menuNode);
          }
          break;
        case KeyEvent.VK_R:
          if (node != null) {
            menuNode = node;
            ((SandTreeCanvas<?>) canvas).removeChildren(node);
          }
          break;
        default:
          consume = false;
          break;
        }
      }
      else {
        consume = false;
      }
    }
    else {
      switch (e.getKeyCode()) {
      case KeyEvent.VK_ENTER:
        if (node != null) {
          menuNode = node;
          changeLabelActionPerformed();
          canvas.setFocus(menuNode);
        }
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
    if (!consume) {
      super.keyPressed(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see MouseListener#mouseClicked(MouseEvent)
   */
  public final void mouseClicked(final MouseEvent e) {
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
          changeLabelActionPerformed();
          // canvas.toggleExpandNode(node);
          // this.canvas.repaint();
        }
        else {
          setFocus(node);
        }
      }
    }
  }

  /**
   * Set new focus and update context menu visibility.
   *
   * @param node
   *          node to which we set focus.
   */
  public void setFocus(final Node node) {
    if (node != null) {
      final Node parent = canvas.getParentNode(node);
      boolean canAddSibling;
      if (parent == null) {
        canAddSibling = false;
      }
      else {
        canAddSibling = true;
      }
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
      switchLeft.setVisible(
          ((GuiNode) node).getLeftSibling() != null && ((GuiNode) node).getLeftSibling() != node);
      switchRight.setVisible(
          ((GuiNode) node).getRightSibling() != null && ((GuiNode) node).getRightSibling() != node);
      toggleAboveFold.setVisible(canFoldAbove);
      toggleFold.setVisible(canFold);
      // addCounter.setVisible(!node.isCountered());
      addLeft.setVisible(canAddSibling);
      addRight.setVisible(canAddSibling);
      removeTree.setVisible(parent != null);
      removeChildren.setVisible(canvas.getMiddleChild(node) != null);
      // removeNode.setVisible(parent!=null);
      this.pmenu.pack();
    }
    super.setFocus(node);
  }

  /**
   * Initialise context menu.
   *
   */
  private void initPopupMenu() {
    this.pmenu = new JPopupMenu();
    menuNode = null;
    JMenuItem menuItem = new JMenuItem(Options.getMsg("handler.changelabel.txt"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.changelabel.key")));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        changeLabelActionPerformed();
      }
    });
    pmenu.add(menuItem);
    menuItem = new JMenuItem(Options.getMsg("handler.changeoperator.txt"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.changeoperator.key")));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).toggleOp(menuNode);
        }
      }
    });
    pmenu.add(menuItem);
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

    pmenu.addSeparator();

    menuItem = new JMenuItem(Options.getMsg("handler.addchild.txt"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.addchild.key")));
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).addChild(menuNode);
        }
      }
    });
    pmenu.add(menuItem);

    // addCounter = new JMenuItem("Add Countermeasure");
    // addCounter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
    // InputEvent.CTRL_MASK));
    // addCounter.addActionListener(new ActionListener()
    // {
    // public void actionPerformed(final ActionEvent evt)
    // {
    // if (menuNode != null) {
    // canvas.addCounter(menuNode);
    // }
    // }
    // });
    // pmenu.add(addCounter);

    addLeft = new JMenuItem(Options.getMsg("handler.addleftsibling.txt"));
    addLeft.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.addleftsibling.key")));
    addLeft.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).addSibling(menuNode, true);
        }
      }
    });
    pmenu.add(addLeft);

    addRight = new JMenuItem(Options.getMsg("handler.addrightsibling.txt"));
    addRight.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.addrightsibling.key")));
    addRight.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).addSibling(menuNode, false);
        }
      }
    });
    pmenu.add(addRight);
    pmenu.addSeparator();

    switchLeft = new JMenuItem(Options.getMsg("handler.switchleft.txt"));
    switchLeft.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.switchleft.key")));
    switchLeft.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).switchSibling(menuNode, true);
        }
      }
    });
    pmenu.add(switchLeft);

    switchRight = new JMenuItem(Options.getMsg("handler.switchright.txt"));
    switchRight.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.switchright.key")));
    switchRight.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).switchSibling(menuNode, false);
        }
      }
    });
    pmenu.add(switchRight);

    removeTree = new JMenuItem(Options.getMsg("handler.removetree.txt"));
    removeTree.setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.removetree.key")));
    removeTree.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).removeTree(menuNode);
        }
      }
    });
    pmenu.add(removeTree);
    removeChildren = new JMenuItem(Options.getMsg("handler.removechildren.txt"));
    removeChildren
        .setAccelerator(KeyStroke.getKeyStroke(Options.getMsg("handler.removechildren.key")));
    removeChildren.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent evt) {
        if (menuNode != null) {
          ((SandTreeCanvas<?>) canvas).removeChildren(menuNode);
        }
      }
    });
    pmenu.add(removeChildren);
    // pmenu.addSeparator();

    // menuItem = new JMenuItem("Change Basic Assignment");
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
    // InputEvent.CTRL_MASK));
    // menuItem.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent evt) {
    // changeBAActionPerformed(evt);
    // }
    // });
    // pmenu.add(menuItem);
    // pmenu.addSeparator();

    // menuItem = new JMenuItem("Collapse/Expand Node");
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
    // InputEvent.CTRL_MASK));
    // menuItem.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent evt) {
    // collapseActionPerformed();
    // }
    // });
    // pmenu.add(menuItem);
    // pmenu.addSeparator();

    // menuItem = new JMenuItem("Properties");
    // //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
    // InputEvent.CTRL_MASK));
    // menuItem.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent evt) {
    // propertiesActionPerformed(evt);
    // }
    // });
    // pmenu.add(menuItem);
  }

  /**
   * Checks if a string is a valid label
   *
   * @param s
   * @return
   */
  private boolean validLabel(String s) {
    return ((SandTreeCanvas<?>) canvas).validLabel(s);
  }

  /**
   * Displays a dialog to change the label of the node
   *
   */
  private void changeLabelActionPerformed() {
    if (menuNode != null) {
      String s =
          (String) MultiLineInput.showInputDialog(Options.getMsg("handler.dialog.newlabel.txt"),
                                                  Options.getMsg("handler.dialog.newlabel.title"), menuNode.getName(), menuNode.getComment());
      if (s == null) {
        return;
      }
      // If a string was returned, say so.
      while (!validLabel(s.replaceAll("^ +| +$| +\n|\n +|(\n)[ \t]*\n|( )+", "$1"))) {
        s = (String) MultiLineInput.showInputDialog(
            "<html><body><font color=\"red\">" + Options.getMsg("handler.dialog.newlabel.invalid")
                + ".</font> " + Options.getMsg("handler.dialog.newlabel.txt") + "</body></html>",
            Options.getMsg("handler.dialog.newlabel.title"), s.trim(), MultiLineInput.getComment());
        if (s == null) {
          return;
        }
      }
      s = s.replaceAll("(?m)^ +| +$|^[ \t]*\r?\n|( )+", "$1");// replaceAll("(?m)^[
                                                              // \t]*\r?\n",
                                                              // "").replaceAll("^
                                                              // +| +$| +\n|(
                                                              // )+","$1");
      ((SandTreeCanvas<?>) canvas).setLabel(menuNode, s, MultiLineInput.getComment());
      ((SandTreeCanvas<?>) canvas).setFocus(menuNode);
    }
  }

  // private ADTreeCanvas canvas;
  private JMenuItem toggleAboveFold;
  private JMenuItem toggleFold;
  // private JMenuItem addCounter;
  private JMenuItem addLeft;
  private JMenuItem addRight;
  private JMenuItem removeTree;
  private JMenuItem removeChildren;
  private JMenuItem switchLeft;
  private JMenuItem switchRight;
  // private Point2D dragStart;
  // private boolean dragScroll;

}
