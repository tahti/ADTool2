package lu.uni.adtool.tree;

import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import org.abego.treelayout.NodeExtentProvider;

/**
 * A {@link NodeExtentProvider} for nodes of type {@link TextInBox}.
 * <p>
 * As one would expect this NodeExtentProvider returns the width and height.
 *
 * @author Piotr Kordy
 */
public class LocalExtentProvider extends AbstractExtentProvider {
  /**
   * Constructs a new instance.
   *
   * @param owner
   *          canvas owning this instarce.
   */
  public LocalExtentProvider(AbstractTreeCanvas owner) {
    this.owner = owner;
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getWidth(Node)
   */
  public double getWidth(final Node node) {
    String[] labels = owner.getLabelLines(node);
    double result = getSizeOfLabels(labels).x;
    // no vertical ellipses
    return Math.max(correctForOval(result, node), getHeight(node));
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getHeight(Node)
   */
  public double getHeight(final Node node) {
    String[] labels = owner.getLabelLines(node);
    double result = getSizeOfLabels(labels).y;
    return correctForOval(result, node);
  }

  /**
   * Gets the owner for this instance.
   *
   * @return The owner.
   */
  public AbstractTreeCanvas getOwner() {
    return this.owner;
  }

  private AbstractTreeCanvas owner;
}
