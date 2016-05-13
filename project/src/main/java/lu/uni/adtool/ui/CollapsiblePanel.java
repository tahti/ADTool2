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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author rgd
 */
public class CollapsiblePanel extends JPanel {


  public CollapsiblePanel(String title) {
    this.title = title;
    border = BorderFactory.createTitledBorder(title);
    setBorder(border);
    BorderLayout borderLayout = new BorderLayout();
    setLayout(borderLayout);
    addMouseListener(mouseListener);
  }

  MouseListener     mouseListener            = new MouseAdapter() {
                                               @Override
                                               public void mouseClicked(MouseEvent e) {
                                                 toggleVisibility();
                                               }
                                             };

  ComponentListener contentComponentListener = new ComponentAdapter() {
                                               @Override
                                               public void componentShown(ComponentEvent e) {
                                                 updateBorderTitle();
                                               }

                                               @Override
                                               public void componentHidden(ComponentEvent e) {
                                                 updateBorderTitle();
                                               }
                                             };

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    firePropertyChange("title", this.title, this.title = title);
  }

  @Override
  public Component add(Component comp) {
    comp.addComponentListener(contentComponentListener);
    Component r = super.add(comp);
    updateBorderTitle();
    return r;
  }

  @Override
  public Component add(String name, Component comp) {
    comp.addComponentListener(contentComponentListener);
    Component r = super.add(name, comp);
    updateBorderTitle();
    return r;
  }

  @Override
  public Component add(Component comp, int index) {
    comp.addComponentListener(contentComponentListener);
    Component r = super.add(comp, index);
    updateBorderTitle();
    return r;
  }

  @Override
  public void add(Component comp, Object constraints) {
    comp.addComponentListener(contentComponentListener);
    super.add(comp, constraints);
    updateBorderTitle();
  }

  @Override
  public void add(Component comp, Object constraints, int index) {
    comp.addComponentListener(contentComponentListener);
    super.add(comp, constraints, index);
    updateBorderTitle();
  }

  @Override
  public void remove(int index) {
    Component comp = getComponent(index);
    comp.removeComponentListener(contentComponentListener);
    super.remove(index);
  }

  @Override
  public void remove(Component comp) {
    comp.removeComponentListener(contentComponentListener);
    super.remove(comp);
  }

  @Override
  public void removeAll() {
    for (Component c : getComponents()) {
      c.removeComponentListener(contentComponentListener);
    }
    super.removeAll();
  }

  protected void toggleVisibility() {
    toggleVisibility(hasInvisibleComponent());
  }

  protected void toggleVisibility(boolean visible) {
    for (Component c : getComponents()) {
      c.setVisible(visible);
    }
    updateBorderTitle();
  }

  protected void updateBorderTitle() {
    String arrow = "";
    if (getComponentCount() > 0) {
      arrow = (hasInvisibleComponent() ? "▽" : "△");
    }
    border.setTitle(title + " " + arrow);
    repaint();
  }

  protected final boolean hasInvisibleComponent() {
    for (Component c : getComponents()) {
      if (!c.isVisible()) {
        return true;
      }
    }
    return false;
  }

  private String       title;
  private TitledBorder border;
  private static final long serialVersionUID = -8281128253938039955L;

}
