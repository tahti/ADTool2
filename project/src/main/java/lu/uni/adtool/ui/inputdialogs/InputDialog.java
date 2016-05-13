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
package lu.uni.adtool.ui.inputdialogs;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Dialog to edit values for the attribute domains. It varies depending on the
 * type of attribute domain.
 *
 * @author Piotr Kordy
 */
public abstract class InputDialog extends JDialog
    implements ActionListener, PropertyChangeListener, KeyListener {

  /**
   * Constructs a new instance.
   */
  public InputDialog(Frame frame, String title) {
    super(frame, title, true);
    setPressed = false;
    this.setLocationRelativeTo(frame);
    setAlwaysOnTop(true);
    setLocation(160, 160);
    setSize(800, 600);
    createCommonLayout();
  }

  // Handle clicks on the Set and Cancel buttons.
  /**
   * {@inheritDoc}
   *
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    if (Options.getMsg("button.cancel").equals(e.getActionCommand())) {
      escPressed();
    }
    else if (Options.getMsg("button.set").equals(e.getActionCommand())) {
      enterPressed();
    }
  }

  /**
   * Called when a field's "value" property changes.
   *
   * @param e
   */
  public void propertyChange(PropertyChangeEvent e) {
    // Ring source = e.getSource();
    if (!sync()) {
    }
  }

  public final Ring showInputDialog(final Ring defaultValue) {
    return showInputDialog(defaultValue, true);
  }

  /**
   * Display dialog.
   *
   * @param defaultValue
   *          defalult value for the input.
   * @return new value entered by user.
   */
  public final Ring showInputDialog(final Ring defaultValue, boolean showDefault) {
    value = defaultValue;
    createLayout(showDefault);
    valueField.requestFocusInWindow();
    valueField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            valueField.selectAll();
          }
        });
      }
    });
    this.setVisible(true);
    if (setPressed) {
      return value;
    }
    else
      return null;
  }

  /**
   * Creates layout.
   *
   * @param showDefault
   *          -true when we want to show the default value.
   */
  abstract protected void createLayout(final boolean showDefault);

  abstract protected boolean isValid(final double d);

  abstract protected void setValue(final double d);

  protected boolean sync() {
    final Number num = (Number) valueField.getValue();
    if (num == null) {
      return false;
    }
    else {
      final double d = num.doubleValue();
      if (!isValid(d)) {
        return false;
      }
      else {
        setValue(d);
      }
      return true;
    }
  }

  private void createCommonLayout() {
    errorMsg = new JLabel("");
    JButton cancelButton = new JButton(Options.getMsg("button.cancel"));
    cancelButton.addActionListener(this);
    setButton = new JButton(Options.getMsg("button.set"));
    setButton.setActionCommand(Options.getMsg("button.set"));
    setButton.addActionListener(this);
    getRootPane().setDefaultButton(setButton);
    // Lay out the buttons from left to right.
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(cancelButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(setButton);
    contentPane = getContentPane();
    contentPane.add(buttonPane, BorderLayout.PAGE_END);
  }

  /**
   * {@inheritDoc}
   *
   * @see JDialog#createRootPane()
   */
  protected JRootPane createRootPane() {
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        escPressed();
      }
    };
    JRootPane rootPane = new JRootPane();
    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    // rootPane.registerKeyboardAction(new EnterListener(), strokeEnter,
    // JComponent.WHEN_IN_FOCUSED_WINDOW);

    return rootPane;
  }

  /** Handle the key typed event from the text field. */
  public void keyTyped(KeyEvent e) {
  }

  /** Handle the key pressed event from the text field. */
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == 10) {
      enterPressed();
    }
    if (e.getKeyCode() == 27) {
      escPressed();
    }
  }

  /** Handle the key released event from the text field. */
  public void keyReleased(KeyEvent e) {
  }

  public void popup() {
    JOptionPane.showMessageDialog(this, errorMsg.getText(),
        Options.getMsg("error.wrongnumberformat"), JOptionPane.ERROR_MESSAGE);
  }

  public void escPressed() {
    value = null;
    setVisible(false);
  }

  public void enterPressed() {
    try {
      valueField.commitEdit();
      if (sync()) {
        setPressed = true;
        setVisible(false);
      }
      else {
        setPressed = false;
        popup();
      }
    }
    catch (ParseException e) {
      popup();
    }
  }

  protected JLabel              errorMsg;
  protected Ring                value;
  protected JButton             setButton;
  protected Container           contentPane;
  protected JFormattedTextField valueField;
  protected boolean             setPressed;
  private static final long     serialVersionUID = 1L;
}
