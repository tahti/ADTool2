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

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import bibliothek.util.Path;

public class DetailsView extends PermaDockable {
  public static final String ID_VIEW = "details_view";

  public DetailsView() {
    super(new Path(ID_VIEW), ID_VIEW, Options.getMsg("windows.details.txt"));
    initLayout();
    ImageIcon icon = new IconFactory().createImageIcon("/icons/info_16x16.png",
        Options.getMsg("windows.details.txt"));
    this.setTitleIcon(icon);
  }

  private void initLayout() {
    JPanel panel = new JPanel(new BorderLayout());
    text = new JLabel(Options.getMsg("windows.details.nochosen")) {

      public Dimension getPreferredSize() {
        return new Dimension(400, 300);
      }

      public Dimension getMinimumSize() {
        return new Dimension(400, 300);
      }

      public Dimension getMaximumSize() {
        return new Dimension(400, 300);
      }
      private static final long serialVersionUID = -6129269462785233124L;
    };
    text.setVerticalAlignment(SwingConstants.TOP);
    text.setFont(new Font("Sans", Font.TRUETYPE_FONT, 13));
    text.setHorizontalAlignment(SwingConstants.LEFT);

    JScrollPane descPane = new JScrollPane(text);
    descPane.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 10),
        BorderFactory.createTitledBorder(Options.getMsg("windows.details.domain"))));
    descPane.setAutoscrolls(true);
    panel.add(descPane);
    add(panel);
  }

  /**
   * Assign canvas to this view.
   *
   * @param canvas
   *          canvas.
   */
  @SuppressWarnings("unchecked")
  public void assignCanvas(AbstractTreeCanvas canvas) {
    if (canvas instanceof AbstractDomainCanvas) {
      text.setText(((AbstractDomainCanvas<Ring>) canvas).getDomain().getDescription());
      // this.canvas = (DomainCanvas<Ring>)canvas;
    }
    else {
      text.setText(Options.getMsg("windows.details.nochosen"));
      // this.canvas=null;
    }
  }

  private JLabel text;

}
