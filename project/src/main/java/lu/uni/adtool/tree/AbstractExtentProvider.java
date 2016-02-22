package lu.uni.adtool.tree;

import lu.uni.adtool.tools.Options;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import org.abego.treelayout.NodeExtentProvider;

/**
 * A {@link NodeExtentProvider} for nodes of type {@link TextInBox}.
 * <p>
 * As one would expect this NodeExtentProvider returns the width and height.
 *
 * @author Piot Kordy
 */
public abstract class AbstractExtentProvider implements NodeExtentProvider<Node> {
  /**
   * Constructs a new instance.
   *
   * @param owner
   *          canvas owning this instarce.
   */
  public AbstractExtentProvider() {
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getWidth(Node)
   */
  public abstract double getWidth(final Node node);
  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getHeight(Node)
   */
  public abstract double getHeight(final Node node);

  /**
   * Calculates the width and height of array of lines
   *
   * @param labels
   *          array or strings
   */
  protected final Point2D.Double getSizeOfLabels(String[] labels) {
    @SuppressWarnings("deprecation")
    final FontMetrics m = Toolkit.getDefaultToolkit().getFontMetrics(Options.canv_Font);
    Point2D.Double result = new Point2D.Double();
    for (String line : labels) {
      result.setLocation(Math.max(result.getX(), m.stringWidth(line)),
          result.getY() + m.getHeight());
    }
    return new Point2D.Double(result.getX() + X_PADDING, result.getY() + Y_PADDING);
  }

  /**
   * Increases the size if the shape is oval.
   *
   */
  protected double correctForOval(double x, Node node) {
    Options.ShapeType shape;
    double result = x;
    shape = Options.canv_ShapeAtt;// allow attacker only for the moment
    switch (shape) {
    case OVAL:
      result = (2 * x) / Math.sqrt(2);
      break;
    case RECTANGLE:
    default:
      break;
    }
    return result;
  }

  private final static int   X_PADDING = 5;
  private final static int   Y_PADDING = 5;
}
