package lu.uni.adtool.tree;

/**
 * The class should implement this interface if it want to be notified about
 * the need to recalculate shared tree layout/node sizes.
 *
 * @author Piot Kordy
 */
public interface TreeChangeListener {

  /**
   * Method called when tree has changed.
   *
   */
  void treeChanged();

  /**
   * Sets the focus.
   *
   * @param node
   *          node which will have the focus.
   */
  void setFocus(Node node);
}
