package lu.uni.adtool.ui;

import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import bibliothek.util.Path;

/**
 * Class showing the window with the log of messages.
 *
 * @author Piotr Kordy
 */
public class LogDockable extends PermaDockable {
  public static final String ID_LOGVIEW = "log_view";
  /**
   * {@inheritDoc}
   *
   * @see JPanel#LogView()
   */
  public LogDockable() {
    super(new Path(ID_LOGVIEW),
        ID_LOGVIEW, Options.getMsg("windows.messageLog.txt"));
    JPanel panel = new JPanel(new BorderLayout());
    messages = new LinkedList<String>();
    messages.clear();
    initLayout(panel);
    add(panel);
    ImageIcon icon = new IconFactory().createImageIcon("/icons/messageLog.png",
        Options.getMsg("windows.messageLog.txt"));
    this.setTitleIcon(icon);
  }

  public void addMessage(String s) {
    if (messages.size() > Options.log_noLinesSaved) {
      messages.removeFirst();
    }
    messages.add("<font color='gray'>" + getTimeStamp() + " - </font>" + s);
    updateText();
  }

  /**
   * Initialize layout.
   *
   */
  private void initLayout(JPanel panel) {
    log = new JTextPane();
    log.setEditable(false);
    log.setContentType("text/html");
    scrollPane = new JScrollPane(log);
//     scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Message Log:"),
//         BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    panel.add(scrollPane);
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
    JButton clearLog = new JButton("Clear Log");
    clearLog.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearLog();
      }
    });
    buttonPanel.add(clearLog);
    panel.add(buttonPanel, BorderLayout.PAGE_END);
  }

  /**
   * Transfer text from private vector messages to the JTextPane log.
   *
   */
  private void updateText() {
    String text = "<html>";
    for (String s : messages) {
      text += s + "<br>";
    }
    log.setText(text + "</html>");
    // ((JComponent) scrollPane.getParent()).revalidate();
  }

  /**
   * Clear all messages
   *
   */
  private void clearLog() {
    messages.clear();
    updateText();
  }

  private String getTimeStamp() {
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date);
  }

  private LinkedList<String> messages;
  private JTextPane log;
  private JScrollPane scrollPane;
}
