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

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class OptionPane {
  public OptionPane() {
  }

  public int showYNDialog(String question, String title) {
    return showYNDialog(null, question, title);
  }

  public static int showYNDialog(JFrame frame, String question, String title) {
    return showDialog(frame, question, title,JOptionPane.YES_NO_OPTION);
  }

  public static int showYNCDialog(JFrame frame, String question, String title) {
    return showDialog(frame, question, title,JOptionPane.YES_NO_CANCEL_OPTION);
  }

  private static int showDialog(JFrame frame, String question, String title, int option) {
    JOptionPane optionPane =
        new JOptionPane(question, JOptionPane.QUESTION_MESSAGE, option);
    JDialog dialog = optionPane.createDialog(frame, title);
    Set<AWTKeyStroke> focusTraversalKeys = new HashSet<AWTKeyStroke>(
        dialog.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
    focusTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.VK_UNDEFINED));
    dialog.setFocusTraversalKeys(0, focusTraversalKeys);

    Set<AWTKeyStroke> backwardTraversalKeys =
        new HashSet<AWTKeyStroke>(dialog.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
    backwardTraversalKeys
        .add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_LEFT, KeyEvent.VK_UNDEFINED));
    dialog.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
        backwardTraversalKeys);
    dialog.setVisible(true);
    if (optionPane.getValue() instanceof Integer)
      return ((Integer) optionPane.getValue()).intValue();
    return -1;
  }
}
