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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * ADAction represents an action that is used in application.
 */
public abstract class ADAction extends AbstractAction {

  /**
   * Defines an ADAction object with the specified descripiton and a default
   * icon.
   *
   * @param text
   *          text to be displayed
   */
  public ADAction(final String text) {
    super(text);
  }

  /**
   * Sets accelerator for the action.
   *
   * @param accelerator
   *          new accelerator
   */
  public final void setAccelerator(final KeyStroke accelerator) {
    putValue(ACCELERATOR_KEY, accelerator);
  }

  /**
   * Sets the new small icon for the action.
   *
   * @param icon
   *          new icon
   */
  public final void setSmallIcon(final Icon icon) {
    putValue(SMALL_ICON, icon);
  }

  public final void setName(final String name) {
    putValue(NAME, name);
  }

  /**
   * Sets tooltip for the action.
   *
   * @param text
   *          new tooltip text
   */
  public final void setToolTip(final String text) {
    putValue(SHORT_DESCRIPTION, text);
  }

  /**
   * Sets long description for the action.
   *
   * @param text
   */
  public final void setDescription(final String text) {
    putValue(LONG_DESCRIPTION, text);
  }

  /**
   * Sets mnemonic for the action.
   *
   * @param mnemonic
   *          new mnemonic
   */
  public final void setMnemonic(final KeyStroke mnemonic) {
    putValue(MNEMONIC_KEY, mnemonic.getKeyCode());
  }

  /**
   * {@inheritDoc}
   *
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public abstract void actionPerformed(final ActionEvent e);

  private static final long serialVersionUID = 8109441685693338016L;
}
