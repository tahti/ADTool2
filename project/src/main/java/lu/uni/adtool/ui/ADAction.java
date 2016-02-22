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
