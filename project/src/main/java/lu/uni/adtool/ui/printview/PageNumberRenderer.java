package lu.uni.adtool.ui.printview;

import java.awt.Component;

/**
 * PageNumberRenderer public interface.
 *
 * @author Piot Kordy
 */
public interface PageNumberRenderer {
  /**
   * Get component with rendered page number.
   *
   * @param pageNumber
   *          page number.
   * @param isSelected
   *          is the page selected?
   * @param hasFocus
   *          has the page focus?
   * @return Component.
   */
  Component getRenderer(int pageNumber, boolean isSelected, boolean hasFocus);
}
