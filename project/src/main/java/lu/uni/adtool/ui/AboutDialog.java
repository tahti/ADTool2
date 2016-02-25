package lu.uni.adtool.ui;

import lu.uni.adtool.ADToolMain;
import lu.uni.adtool.tools.Options;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

class AboutDialog extends JDialog {
  public AboutDialog(Frame frame) {
    super(frame, Options.getMsg("help.about.title"), true);
    setLocationRelativeTo(frame);
    initCloseListener();
    initUI();
  }

  /** Returns an ImageIcon, or null if the path was invalid. */
  private ImageIcon createImageIcon(String path, String description) {
    java.net.URL imgURL = getClass().getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    }
    else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  public final void initUI() {
    JPanel mainPane = new JPanel();
    ImageIcon icon;
    JLabel name;
    Border paneEdge = BorderFactory.createEmptyBorder(20, 20, 20, 20);
    mainPane.setBorder(paneEdge);
    mainPane.setBackground(Color.white);
    mainPane.setOpaque(true);
    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    name = new JLabel("ADTool " + getVersionInfo());
    name.setFont(new Font("Serif", Font.BOLD, 13));
    name.setAlignmentX(0.5f);
    mainPane.add(name);
    mainPane.add(Box.createRigidArea(new Dimension(0, 10)));
    // ADTool icon
    icon = createImageIcon("/icons/ADToolVer2.png", "ADTree Tool");
    JLabel label = new JLabel(icon);
    label.setAlignmentX(0.5f);
    mainPane.add(label);
    mainPane.add(Box.createRigidArea(new Dimension(0, 10)));
    // "Supported by" text
    name = new JLabel(Options.getMsg("help.about.grant"));
    name.setFont(new Font("Serif", Font.PLAIN, 13));
    name.setAlignmentX(0.5f);
    mainPane.add(name);
    mainPane.add(Box.createRigidArea(new Dimension(0, 10)));
    // "Supported by" logos
    JPanel p = new JPanel();
    BoxLayout b = new BoxLayout(p, BoxLayout.X_AXIS);
    p.setLayout(b);
    p.add(Box.createRigidArea(new Dimension(5, 0)));
    icon = createImageIcon("/icons/fnr_logo.png", "FNR logo");
    p.add(new JLabel(icon));
    p.add(Box.createRigidArea(new Dimension(25, 0)));
    icon = createImageIcon("/icons/2eulogos.png", "EU logo");
    p.add(new JLabel(icon));
    p.add(Box.createRigidArea(new Dimension(5, 0)));
    p.setBackground(Color.white);
    p.setOpaque(true);
    mainPane.add(p);
    mainPane.add(Box.createRigidArea(new Dimension(0, 10)));

    JButton close = new JButton(Options.getMsg("toolbar.close"));
    close.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        dispose();
      }
    });
    close.setAlignmentX(0.5f);
    mainPane.add(close);
    add(mainPane);
    setModalityType(ModalityType.APPLICATION_MODAL);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  private void initCloseListener() {
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "close");
    getRootPane().getActionMap().put("close", new AbstractAction() {
      private static final long serialVersionUID = 2143956335533214473L;

      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
  }

  public static String getVersionInfo() {
    String result = "";
    try {
      Class<ADToolMain> clazz = ADToolMain.class;
      String className = clazz.getSimpleName() + ".class";
      String classPath = clazz.getResource(className).toString();
      if (!classPath.startsWith("jar")) {
        // Class not from JAR
        return "No version info";
      }
      String manifestPath =
          classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
      Manifest manifest = new Manifest(new URL(manifestPath).openStream());
      Attributes mainAttribs = manifest.getMainAttributes();
      result += mainAttribs.getValue("Implementation-Version") + " (";
      result += mainAttribs.getValue("Date-Build") + " - ";
      result += mainAttribs.getValue("Implementation-Build").substring(0,6) + ")";
    }
    catch (IOException e1) {
    }
    return result;
  }
}
