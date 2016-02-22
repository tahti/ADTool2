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
