package lu.uni.adtool.ui.printview;

import lu.uni.adtool.tools.Options;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * A border with shadow effect.
 *
 * @author Piot Kordy
 */
public class JPrintPreviewBorder extends AbstractBorder {

  /**
   * Constructs a border with defaults for all attributes.
   */
  public JPrintPreviewBorder() {
    this(5);
  }

  /**
   * Constructs a border with the specified distance for the shadow.
   *
   * @param newDistance
   *          The distance of the shadow to the component.
   */
  public JPrintPreviewBorder(final int newDistance) {
    this(1, newDistance);
  }

  /**
   * Constructs a border with the specified attributes.
   *
   * @param width
   *          The width of the border.
   * @param newDistance
   *          The distance of the shadow to the component.
   */
  public JPrintPreviewBorder(final int width, final int newDistance) {
    this.distance = newDistance;
    this.borderWidth = width;
    // top left bottom right
    insets = new Insets(width, width, width + distance,
        (int) (width + (Math.ceil((double) distance / 1.5))));
  }

  /**
   * Gets the insets for this instance.
   *
   * @return The insets.
   */
  public final Insets getInsets() {
    return this.insets;
  }

  /**
   * Paints the border for the specified component with the specified position
   * and size.
   *
   * @param c
   *          the component for which this border is being painted
   * @param g
   *          the paint graphics
   * @param x
   *          the x position of the painted border
   * @param y
   *          the y position of the painted border
   * @param width
   *          the width of the painted border
   * @param height
   *          the height of the painted border
   */
  public final void paintBorder(final Component c, final Graphics g, final int x, final int y,
      final int width, final int height) {
    // Border around the component
    for (int i = 0; i < borderWidth; i++) {
      g.setColor(Options.printview_border);
      g.drawRect(x + i, y + i, width - insets.right + borderWidth - i - i - 1,
          height - insets.bottom + borderWidth - i - i - 1);
    }

    // Shadow to the right and bottom
    g.setColor(Options.printview_shadow);
    // Bottom
    g.fillRect(insets.right - borderWidth, height - insets.bottom + borderWidth,
        width - insets.right + borderWidth, insets.bottom - borderWidth);
    // Right
    g.fillRect(width - insets.right + borderWidth, insets.bottom - borderWidth,
        insets.right - borderWidth, height - insets.bottom + borderWidth);

    g.setColor(Options.printview_background);
    g.fillRect(0, height - insets.bottom + borderWidth, insets.right - borderWidth,
        insets.bottom - borderWidth);
    g.fillRect(width - insets.right + borderWidth, 0, insets.right - borderWidth,
        insets.bottom - borderWidth);

    // If distance is greater than component height fill the gap with the
    // background color.
    final int compHeight = c.getHeight() - insets.bottom + insets.top;
    if (compHeight < distance) {
      final int h = c.getHeight() - compHeight - compHeight;
      g.fillRect(0, compHeight, width, h);
    }

  }

  /** Generated serialVersionUID */
  private static final long serialVersionUID = 4666462256643686970L;

  /** the insets of the border */
  private Insets            insets;

  /** the distance from the shadow to the component */
  private int               distance;

  /** the width of the border */
  private int               borderWidth;

}
