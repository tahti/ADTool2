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
  void OptionPane() {
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
