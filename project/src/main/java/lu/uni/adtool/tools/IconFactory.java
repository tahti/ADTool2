package lu.uni.adtool.tools;

import javax.swing.ImageIcon;

public class IconFactory {
  public ImageIcon createImageIcon(String path, String description) {
    java.net.URL imgURL = getClass().getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL, description);
    }
    else {
      System.err.println(Options.getMsg("error.println.noFile") + " " + path);
      return null;
    }
  }
  public ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = getClass().getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    }
    else {
      System.err.println(Options.getMsg("error.println.noFile") + " " + path);
      return null;
    }
  }
}
