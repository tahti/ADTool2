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
 * @author Piotr Kordy
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
      consume = false;
    }
    else {
      menuNode = node;
    }
    if (e.isControlDown()) {
      consume = false;
    }
    else if (consume) {
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
