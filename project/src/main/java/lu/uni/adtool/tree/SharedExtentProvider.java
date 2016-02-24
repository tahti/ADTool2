package lu.uni.adtool.tree;

import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.abego.treelayout.NodeExtentProvider;

public class SharedExtentProvider extends AbstractExtentProvider {
  public SharedExtentProvider() {
    canvasSet = new HashSet<AbstractTreeCanvas>();
    sizes = new HashMap<Node, Point2D.Double>();
  }

  public void registerCanvas(AbstractTreeCanvas canvas) {
    Debug.log("with tree id :"+ canvas.getId());
    canvasSet.add(canvas);
  }

  public void deregisterCanvas(AbstractTreeCanvas canvas) {
    canvasSet.remove(canvas);
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getWidth(Node)
   */
  @Override
  public final double getWidth(final Node node) {
    return sizes.get(node).x;
  }

  public void notifyTreeChanged() {
    for (AbstractTreeCanvas listener : canvasSet) {
      listener.treeChanged();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see NodeExtentProvider#getHeight(Node)
   */
  @Override
  public final double getHeight(final Node node) {
    return sizes.get(node).y;
  }

  /**
   * Deletes all calculated sizes.
   *
   * @param node
   *
   */
  public final void clearSizes() {
    sizes.clear();
  }

  /**
   * Calculates new size for the node.
   *
   * @param node
   *
   */
  public void updateNodeSize(final Node node) {
    Point2D.Double size = new Point2D.Double(0, 0);
    String[] labels;
    for (AbstractTreeCanvas canvas : canvasSet) {
      labels = canvas.getLabelLines(node);
      Point2D.Double newSize = getSizeOfLabels(labels);
      size.x = Math.max(size.x, newSize.x);
      size.y = Math.max(size.y, newSize.y);
    }
    size.x = correctForOval(size.x, node);
    size.y = correctForOval(size.y, node);
    // no vertical ellipses/rectangle - use cicle/square then
    if (size.x < size.y) {
      size.x = size.y;
    }
    sizes.put(node, size);
  }

  public void updateTreeSize(final Node node) {
    Debug.log("nn name:"+node.getName());
    this.updateNodeSize(node);
    if (node.getChildren() != null) {
      for (Node child : node.getChildren()) {
        this.updateTreeSize(child);
      }
    }
  }

  /**
   * Gets the sizes for this instance.
   *
   * @return The sizes.
   */
  public HashMap<Node, Point2D.Double> getSizes() {
    return this.sizes;
  }

  private Set<AbstractTreeCanvas>       canvasSet;
  private HashMap<Node, Point2D.Double> sizes;

}
