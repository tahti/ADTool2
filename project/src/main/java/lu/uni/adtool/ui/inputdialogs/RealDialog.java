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

import lu.uni.adtool.domains.rings.Real;
import lu.uni.adtool.tools.Options;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

/**
 * Dialog to edit values for the attribute domains of real numbers greater than
 * 0..
 *
 * @author Piotr Kordy
 */
public class RealDialog extends InputDialog {

  /**
   * Constructs a new instance.
   */
  public RealDialog(final Frame frame) {
    super(frame, Options.getMsg("inputdialog.realnumber.title"));
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
      times(0.001);
    }
    else if ("/100".equals(e.getActionCommand())) {
      times(0.01);
    }
    else if ("/10".equals(e.getActionCommand())) {
      times(0.1);
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
    else if (Options.getMsg("inputdialog.infinity").equals(e.getActionCommand())) {
      setValue(Double.POSITIVE_INFINITY);
    }
    else if (Options.getMsg("inputdialog.zero").equals(e.getActionCommand())) {
      setValue(0);
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
  protected final boolean isValid(final double d) {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see InputDialog#setValue(double)
   */
  protected final void setValue(final double d) {
    value = new Real(d);
    valueField.setValue(d);
  }

  /*
   * /** {@inheritDoc}
   *
   * @see InputDialog#createLayout()
   */
  protected void createLayout(boolean showDefault) {
    errorMsg.setText(Options.getMsg("inputdialog.realnumber.txt"));
    final DecimalFormat f = new DecimalFormat();
    f.setMaximumFractionDigits(50);
    valueField = new JFormattedTextField(f);
    valueField.addKeyListener(this);
    if (showDefault) {
      valueField.setValue(new Double(((Real) value).getValue()));
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
    inputPane.add(button, c);
    c.gridx = 4;
    button = new JButton(Options.getMsg("inputdialog.infinity"));
    button.setActionCommand(Options.getMsg("inputdialog.infinity"));
    button.addActionListener(this);
    inputPane.add(button, c);
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
    if (!isValid(d + i) || d == Double.POSITIVE_INFINITY) {
      return;
    }
    d = d + i;
    setValue(d);
  }

  private void times(final double i) {
    final Number num = (Number) valueField.getValue();
    if (num == null) {
      return;
    }
    double d = num.doubleValue();
    if (!isValid(d * i) || d == Double.POSITIVE_INFINITY) {
      return;
    }
    d = d * i;
    setValue(d);
  }

  static final long serialVersionUID = 458586363646948463L;
}
