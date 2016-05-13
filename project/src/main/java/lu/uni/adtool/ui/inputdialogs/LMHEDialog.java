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

import lu.uni.adtool.domains.rings.LMHEValue;
import lu.uni.adtool.tools.Options;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ChoiceFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

/**
 * Dialog to edit values for the attribute domains of values from the set
 * {L,M,H}
 *
 * @author Piotr Kordy
 */
public class LMHEDialog extends InputDialog {

  /**
   * Constructs a new instance.
   *
   * @param frame
   *          parent frame.
   */
  public LMHEDialog(final Frame frame) {
    super(frame,
        Options.getMsg("inputdialog.valuesset.txt") + " {" + Options.getMsg("inputdialog.l") + ", "
            + Options.getMsg("inputdialog.m") + ", " + Options.getMsg("inputdialog.h") + ", "
            + Options.getMsg("inputdialog.e") + "}");
  }

  /**
   * {@inheritDoc}
   *
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public final void actionPerformed(final ActionEvent e) {
    if (Options.getMsg("inputdialog.l").equals(e.getActionCommand())) {
      setValue(1);
    }
    else if (Options.getMsg("inputdialog.m").equals(e.getActionCommand())) {
      setValue(2);
    }
    else if (Options.getMsg("inputdialog.h").equals(e.getActionCommand())) {
      setValue(3);
    }
    else if (Options.getMsg("inputdialog.e").equals(e.getActionCommand())) {
      setValue(4);
    }
    else if (Options.getMsg("inputdialog.infinity").equals(e.getActionCommand())) {
      setValue(LMHEValue.INF);
    }
    else if ("\u221E".equals(e.getActionCommand())) {
      setValue(LMHEValue.INF);
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
    if (d >= 1 && d <= 4) {
      LMHEValue v = new LMHEValue((int) d);
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
    value = new LMHEValue((int) d);
    valueField.setValue(d);
  }

  protected final void createLayout(boolean showDefault) {
    errorMsg.setText(Options.getMsg("inputdialog.valuesset.error") + " {"
        + Options.getMsg("inputdialog.l") + ", " + Options.getMsg("inputdialog.m") + ", "
        + Options.getMsg("inputdialog.h") + ", " + Options.getMsg("inputdialog.e") + "}!");
    double[] choices = {1, 2, 3, 4, 5};
    String[] choiceNames = {Options.getMsg("inputdialog.l"), Options.getMsg("inputdialog.m"),
        Options.getMsg("inputdialog.h"), Options.getMsg("inputdialog.e"), "\u221E"};
    final ChoiceFormat f = new ChoiceFormat(choices, choiceNames);
    valueField = new JFormattedTextField(f);
    valueField.addKeyListener(this);
    if (showDefault) {
      valueField.setValue(new Double(((LMHEValue) value).getValue()));
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
    c.gridwidth = 4;
    inputPane.add(valueField, c);
    c.gridwidth = 1;
    c.gridy = 1;
    button = new JButton(Options.getMsg("inputdialog.l"));
    button.setActionCommand(Options.getMsg("inputdialog.l"));
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridy = 1;
    c.gridx = 1;
    button = new JButton(Options.getMsg("inputdialog.m"));
    button.setActionCommand(Options.getMsg("inputdialog.m"));
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 2;
    button = new JButton(Options.getMsg("inputdialog.h"));
    button.setActionCommand(Options.getMsg("inputdialog.h"));
    button.addActionListener(this);
    inputPane.add(button, c);
    c.gridx = 3;
    button = new JButton(Options.getMsg("inputdialog.e"));
    button.setActionCommand(Options.getMsg("inputdialog.e"));
    button.addActionListener(this);
    inputPane.add(button, c);
    contentPane.add(inputPane, BorderLayout.CENTER);
    pack();
  }

  static final long serialVersionUID = 66412346112819771L;
}
