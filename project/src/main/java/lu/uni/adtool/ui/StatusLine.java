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
package lu.uni.adtool.ui;

import lu.uni.adtool.tools.Options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

public class StatusLine extends JLabel implements ActionListener {

  public StatusLine() {
    super(Options.getMsg("status.ready"));
    this.logDockable = new LogDockable();
  }

  public void report(final String s) {
    setText(s);
    logDockable.addMessage(s);
  }

  public void reportError(String message) {
    report("<html><font color='red'> " + Options.getMsg("error.txt") + ": </font>" + message
        + "<html>");
  }

  public void reportWarning(String message) {
    report("<html><font color='orange'> " + Options.getMsg("warning.txt") + ": </font>" + message
        + "<html>");
  }

  public void actionPerformed(ActionEvent e) {
    setText("");
  }

  /**
   * Used by main window to display log
   */
  public LogDockable getLogViewDockable() {
    return this.logDockable;
  }

  private static final long serialVersionUID = 4285019474424274296L;
  private LogDockable       logDockable;
}
