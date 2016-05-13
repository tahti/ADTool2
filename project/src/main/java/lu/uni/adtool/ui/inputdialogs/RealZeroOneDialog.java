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

import lu.uni.adtool.domains.rings.RealZeroOne;
import lu.uni.adtool.tools.Options;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog to edit values for the attribute domains of real numbers from 0 to 1.
 *
 * @author Piotr Kordy
 */
public class RealZeroOneDialog extends InputDialog implements ChangeListener {

  /**
   * Constructs a new instance.
   *
   * @param frame
   *          parent frame.
   */
  public RealZeroOneDialog(final Frame frame) {
    super(frame, Options.getMsg("inputdialog.zeroone.txt"));
    slider = null;
  }

  /**
   * {@inheritDoc}
   *
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public final void actionPerformed(final ActionEvent e) {
    if ("0".equals(e.getActionCommand())) {
      setValue(0);
    }
    else if ("0.25".equals(e.getActionCommand())) {
      setValue(0.25);
    }
    else if ("0.5".equals(e.getActionCommand())) {
      setValue(0.5);
    }
    else if ("0.75".equals(e.getActionCommand())) {
      setValue(0.75);
    }
    else if ("1".equals(e.getActionCommand())) {
      setValue(1);
    }
    else if ("-0.1".equals(e.getActionCommand())) {
      add(-0.1);
    }
    else if ("+0.1".equals(e.getActionCommand())) {
      add(0.1);
    }
    else {
      super.actionPerformed(e);
    }

  }

  /**
   * {@inheritDoc}
   *
   * @see ChangeListener#stateChanged(ChangeEvent)
   */
  public final void stateChanged(final ChangeEvent e) {
    final JSlider source = (JSlider) e.getSource();
    if (source.getValueIsAdjusting()) {
      final int i = (int) source.getValue();
      valueField.setValue(new Double(i / 100.0));
      sync();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see InputDialog#isValid(double)
   */
  protected final boolean isValid(final double d) {
    if (d >= 0 && d <= 1) {
      RealZeroOne v = new RealZeroOne(d);
      if (v.getValue() == d) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @see InputDialog#setValue(double)
   */
  protected final void setValue(final double d) {
    value = new RealZeroOne(d);
    valueField.setValue(d);
    slider.setValue((int) (d * 100));
  }

  protected final void createLayout(boolean showDefault) {
    errorMsg.setText(Options.getMsg("inputdialog.zeroone.error"));
    slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 1);
    slider.addChangeListener(this);
    slider.setMajorTickSpacing(25);
    slider.setPaintTicks(true);
    // Create the label table
    final Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
    labelTable.put(new Integer(0), new JLabel("0"));
    labelTable.put(new Integer(50), new JLabel("0.5"));
    labelTable.put(new Integer(100), new JLabel("1"));
    slider.setLabelTable(labelTable);
    slider.setPaintLabels(true);
    slider.setValue((int) (new Double(((RealZeroOne) value).getValue()) * 100));

    final DecimalFormat f = new DecimalFormat();
    f.setMaximumFractionDigits(50);
    valueField = new JFormattedTextField(f);
    valueField.addKeyListener(this);
    if (showDefault) {
      valueField.setValue(new Double(((RealZeroOne) value).getValue()));
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
    button = new JButton("-0.1");
    button.setActionCommand("-0.1");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 1;
    c.gridwidth = 3;
    inputPane.add(valueField, c);
    c.gridwidth = 1;
    c.gridx = 4;
    button = new JButton("+0.1");
    button.setActionCommand("+0.1");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridy = 1;
    c.gridx = 0;
    button = new JButton("0");
    button.setActionCommand("0");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 1;
    button = new JButton("0.25");
    button.setActionCommand("0.25");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 2;
    button = new JButton("0.5");
    button.setActionCommand("0.5");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 3;
    button = new JButton("0.75");
    button.setActionCommand("0.75");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 4;
    button = new JButton("1");
    button.setActionCommand("1");
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridy = 2;
    c.gridx = 0;
    c.gridwidth = 5;
    inputPane.add(slider, c);
    contentPane.add(inputPane, BorderLayout.CENTER);
    pack();
  }

  private void add(final double i) {
    final Number num = (Number) valueField.getValue();
    if (num == null) {
      return;
    }
    double d = num.doubleValue();
    if (!isValid(d + i)) {
      return;
    }
    d = d + i;
    valueField.setValue(new Double(d));
    sync();
  }

  static final long serialVersionUID = 118586363646948463L;
  private JSlider   slider;
}
