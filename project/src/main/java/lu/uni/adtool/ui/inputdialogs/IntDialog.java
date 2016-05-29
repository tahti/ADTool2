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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import lu.uni.adtool.domains.rings.Int;
import lu.uni.adtool.tools.Options;

/**
 * Dialog to edit values for the attribute domains of real numbers greater than
 * 0..
 *
 * @author Piotr Kordy
 */
public class IntDialog extends InputDialog {

  /**
   * Constructs a new instance.
   *
   * @param frame
   *          parent frame.
   */
  public IntDialog(final Frame frame) {
    super(frame, Options.getMsg("inputdialog.intnumber.txt"));
  }

  /**
   * Constructs a new instance.
   *
   * @param frame
   *          parent frame.
   * @param title
   *          window title.
   */
  public IntDialog(final Frame frame, final String title) {
    super(frame, title);
  }

  /**
   * Handle clicks on the various buttons. {@inheritDoc}
   *
   * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(final ActionEvent e) {
    if ("-100".equals(e.getActionCommand())) {
      add(-100);
    }
    else if ("-10".equals(e.getActionCommand())) {
      add(-10);
    }
    else if ("-1".equals(e.getActionCommand())) {
      add(-1);
    }
    else if ("+1".equals(e.getActionCommand())) {
      add(1);
    }
    else if ("+10".equals(e.getActionCommand())) {
      add(10);
    }
    else if ("+100".equals(e.getActionCommand())) {
      add(100);
    }
    else if ("/1000".equals(e.getActionCommand())) {
      divide(1000);
    }
    else if ("/100".equals(e.getActionCommand())) {
      divide(100);
    }
    else if ("/10".equals(e.getActionCommand())) {
      divide(10);
    }
    else if ("x10".equals(e.getActionCommand())) {
      times(10);
    }
    else if ("x100".equals(e.getActionCommand())) {
      times(100);
    }
    else if ("x1000".equals(e.getActionCommand())) {
      times(1000);
    }
    else if (Options.getMsg("inputdialog.zero").equals(e.getActionCommand())) {
      valueField.setValue(new Double(0));
      sync();
    }
    else {
      super.actionPerformed(e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see InputDialog#isValid(double)
   */
  protected boolean isValid(final double d) {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see InputDialog#setValue(double)
   */
  protected final void setValue(final double d) {
    value = new Int((int) d);
    valueField.setValue(d);
  }

  /**
   * {@inheritDoc}
   *
   * @see InputDialog#createLayout()
   */
  protected void createLayout(final boolean showDefault) {
    final NumberFormat f = NumberFormat.getInstance();
    f.setParseIntegerOnly(true);
    valueField = new JFormattedTextField(f);
    valueField.addKeyListener(this);
    double d = new Double(((Int) value).getValue());
    if (showDefault) {
      valueField.setValue(d);
    }
    valueField.setColumns(15);
    valueField.addPropertyChangeListener("value", this);
    final JPanel inputPane = new JPanel();
    inputPane.setLayout(new GridBagLayout());
    final GridBagConstraints c = new GridBagConstraints();
    JButton button;
    c.insets = new Insets(0, 8, 0, 0);
    c.gridy = 0;
    c.gridx = 0;
    button = new JButton("-100");
    button.setActionCommand("-100");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 1;
    button = new JButton("-10");
    button.setActionCommand("-10");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 2;
    button = new JButton("-1");
    button.setActionCommand("-1");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 3;
    c.gridwidth = 2;
    inputPane.add(valueField, c);
    c.gridwidth = 1;
    c.gridx = 5;
    button = new JButton("+1");
    button.setActionCommand("+1");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 6;
    button = new JButton("+10");
    button.setActionCommand("+10");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 7;
    button = new JButton("+100");
    button.setActionCommand("+100");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridy = 1;
    c.gridx = 0;
    button = new JButton("/1000");
    button.setActionCommand("/1000");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 1;
    button = new JButton("/100");
    button.setActionCommand("/100");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 2;
    button = new JButton("/10");
    button.setActionCommand("/10");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 3;
    button = new JButton(Options.getMsg("inputdialog.zero"));
    button.setActionCommand(Options.getMsg("inputdialog.zero"));
    button.addActionListener(this);
    c.gridwidth = 2;
    inputPane.add(button, c);
    c.gridwidth = 1;
    c.gridx = 5;
    button = new JButton("x10");
    button.setActionCommand("x10");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 6;
    button = new JButton("x100");
    button.setActionCommand("x100");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 7;
    button = new JButton("x1000");
    button.setActionCommand("x1000");
    button.addActionListener(this);
    inputPane.add(button, c);
    contentPane.add(inputPane, BorderLayout.CENTER);
    pack();
  }

  private void add(final int i) {
    final Number num = (Number) valueField.getValue();
    if (num == null) {
      return;
    }
    double d = num.doubleValue();
    if (d == Double.NEGATIVE_INFINITY|| d == Double.POSITIVE_INFINITY) {
      return;
    }
    d = d + i;
    setValue(d);
  }

  private void times(final int i) {
    final Number num = (Number) valueField.getValue();
    if (num == null) {
      return;
    }
    double d = num.doubleValue();
    if (d == Double.NEGATIVE_INFINITY|| d == Double.POSITIVE_INFINITY) {
      return;
    }
    d = d * i;
    setValue(d);
  }

  private void divide(final int i) {
    final Number num = (Number) valueField.getValue();
    if (num == null) {
      return;
    }
    double d = num.doubleValue();
    if (d == Double.NEGATIVE_INFINITY|| d == Double.POSITIVE_INFINITY) {
      return;
    }
    d = (int) d / i;
    setValue(d);
  }
  static final long serialVersionUID = 35393957497521213L;
}
