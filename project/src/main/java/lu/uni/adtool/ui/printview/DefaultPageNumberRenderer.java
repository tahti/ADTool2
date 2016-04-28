package lu.uni.adtool.ui.printview;

import lu.uni.adtool.tools.Options;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;

/**
 * Default implementation of the PageNumberRenderer interface.
 *
 * @author Piotr Kordy
 */
public class DefaultPageNumberRenderer extends JLabel implements PageNumberRenderer {

  /**
   * {@inheritDoc}
   *
   * @see JLabel#DefaultPageNumberRenderer()
   */
  public DefaultPageNumberRenderer() {
    super();
    setForeground(Color.BLACK);
    setBackground(Options.printview_background);
  }

  /**
   * Get page number renderer configured with the given pagenumber.
   *
   * @param pageNumber
   *          the pagenumber.
   * @param isSelected
   *          is the page selected?
   * @param hasFocus
   *          has the page focus?
   *
   * @return this component (the default pagenumber renderer.
   */
  public final Component getRenderer(final int pageNumber, final boolean isSelected,
      final boolean hasFocus) {
    setValue("- " + pageNumber + " -");
    setHorizontalAlignment(JLabel.CENTER);
    setOpaque(true);
    return this;
  }

  /**
   * Sets the String for this page
   *
   * @param value
   *          the String value for this page; if value is <code>null</code> it
   *          sets the text value to an empty String.
   */
  protected final void setValue(final String value) {
    if (value != null) {
      setText(value);
    }
    else {
      setText("");
    }
  }
  /** Generated serialVersionUID */
  static final long serialVersionUID = 1863364566991026090L;
}
