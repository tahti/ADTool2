package lu.uni.adtool.tree;

public abstract class GuiNode extends Node {

  public GuiNode() {
    super();
    folded = false;
    aboveFolded = false;
  }

  public GuiNode(String name) {
    super(name);
    folded = false;
    aboveFolded = false;
  }

  /**
   * Returns parent of the node.
   *
   * @param ignoreFold
   *          if true we return parent even if the node is folded from above
   * @return the parent node or null
   */
  public GuiNode getParent(boolean ignoreFold) {
    if (!ignoreFold && isAboveFolded()) {
      return null;
    }
    return (GuiNode) getParent();
  }

  /**
   * @return the folded
   */
  public boolean isFolded() {
    return folded;
  }

  /**
   * @param folded
   *          the folded to set
   */
  public void setFolded(boolean folded) {
    this.folded = folded;
  }

  /**
   * @return the aboveFolded
   */
  public boolean isAboveFolded() {
    return aboveFolded;
  }

  /**
   * @param aboveFolded
   *          the aboveFolded to set
   */
  public void setAboveFolded(boolean aboveFolded) {
    this.aboveFolded = aboveFolded;
  }

  /**
   * @return the leftSibling
   */
  public GuiNode getLeftSibling() {
    return leftSibling;
  }

  /**
   * @param leftSibling
   *          the leftSibling to set
   */
  public void setLeftSibling(Node leftSibling) {
    this.leftSibling = (GuiNode) leftSibling;
  }

  /**
   * @return the rightSibling
   */
  public GuiNode getRightSibling() {
    return rightSibling;
  }

  /**
   * Return a middle child - only when node is not folded
   *
   * @param node
   * @return
   */
  public GuiNode getMiddleChild() {
    if (this.isFolded()) {
      return null;
    }
    if (children == null) {
      return null;
    }
    if (children.size() == 0) {
      return null;
    }
    return (GuiNode) children.get(children.size() / 2);
  }

  /**
   * @param rightSibling
   *          the rightSibling to set
   */
  public void setRightSibling(Node rightSibling) {
    this.rightSibling = (GuiNode) rightSibling;
  }

  private boolean folded;
  private boolean aboveFolded;
  private GuiNode leftSibling;
  private GuiNode rightSibling;
  private static final long serialVersionUID = 1285171599983300177L;
}
