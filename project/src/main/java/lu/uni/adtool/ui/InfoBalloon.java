package lu.uni.adtool.ui;

import lu.uni.adtool.tools.Options;

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

public class InfoBalloon{


  public InfoBalloon() {
//       add(new JLabel("Please Enter Number"));
//       add(textField);

   }
  public void setText(String msg) {
    msgPane.setText(msg);
  }

  public void showBalloon(Window parent, String msg) {
    if (errorWindow == null) {
      msgPane= new JTextPane();
      msgPane.setEditable(false);
      msgPane.setFocusable(false);
      msgPane.setText(msg);
      msgPane.setBackground(Options.canv_tooltipBackground);
      msgPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
      errorWindow = new JWindow(parent);
      JPanel contentPane = (JPanel) errorWindow.getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.setBorder(new EmptyBorder(4,4,4,4));
      contentPane.add(msgPane, BorderLayout.CENTER);
      errorWindow.pack();
    }
    else {
      msgPane.setText(msg);
    }

    Point loc = MouseInfo.getPointerInfo().getLocation();
    errorWindow.setVisible(true);
    errorWindow.setLocation(loc.x + 10, loc.y + 15);
  }

  public void hideBalloon() {
    if (errorWindow != null) {
      errorWindow.setVisible(false);
    }
  }
  public boolean isVisible() {
    if (errorWindow == null) {
      return false;
    }
    else {
      return errorWindow.isVisible();
    }
  }
  private JTextPane msgPane;
  private JWindow errorWindow;

}
