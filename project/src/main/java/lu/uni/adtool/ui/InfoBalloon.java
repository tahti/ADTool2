/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>) Date:
 * 10/12/2015 Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty
 * of Science, Technology and Communication FSTC All rights reserved. Licensed
 * under GNU Affero General Public License 3.0; This program is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.ui;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;

/**
 * Class representing a baloon window showing comments in the nodes.
 *
 * @author Piotr Kordy
 */

public class InfoBalloon {

  public InfoBalloon() {
    // add(new JLabel("Please Enter Number"));
    // add(textField);
    lastNode = null;
  }


  /**
   * Shows window with comment for the node
   *
   * @param parent - parent window
   * @param node - node for which we show comment
   */
  
  public void showBalloon(Window parent, Node node) {
    if (node != lastNode) {
      if (errorWindow != null) {
        errorWindow.setVisible(false);
        errorWindow = null;
      }

      msgPane = new JTextPane();
      msgPane.setEditable(false);
      msgPane.setFocusable(false);
      msgPane.setText(node.getComment());
      msgPane.setBackground(Options.canv_tooltipBackground);
      msgPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
      errorWindow = new JWindow(parent);
      JPanel contentPane = (JPanel) errorWindow.getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.setBorder(new EmptyBorder(4, 4, 4, 4));
      contentPane.add(msgPane, BorderLayout.CENTER);
      errorWindow.pack();
      errorWindow.setVisible(true);

      Point loc = MouseInfo.getPointerInfo().getLocation();
      errorWindow.setLocation(loc.x + 10, loc.y + 15);
      lastNode = node;
    }
  }

  /**
   * Hides the window with comment
   *
   */
  public void hideBalloon() {
    if (errorWindow != null) {
      errorWindow.setVisible(false);
      errorWindow = null;
    }
    lastNode = null;
  }

  public boolean isVisible() {
    if (errorWindow == null) {
      return false;
    }
    else {
      return errorWindow.isVisible();
    }
  }

  private Node      lastNode;
  private JTextPane msgPane;
  private JWindow   errorWindow;

}
