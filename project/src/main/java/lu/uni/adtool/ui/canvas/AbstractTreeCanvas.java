/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>) Date:
 * 10/12/2015 Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty
 * of Science, Technology and Communication FSTC All rights reserved. Licensed
 * under GNU Affero General Public License 3.0; This program is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.ui.canvas;

import lu.uni.adtool.ADToolMain;
import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tools.undo.AddDomain;
import lu.uni.adtool.tools.undo.EditAction;
import lu.uni.adtool.tools.undo.FoldAction;
import lu.uni.adtool.tools.undo.History;
import lu.uni.adtool.tools.undo.RemoveChildren;
import lu.uni.adtool.tools.undo.RemoveDomain;
import lu.uni.adtool.tools.undo.RemoveTree;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.GuiNode;
import lu.uni.adtool.tree.LocalExtentProvider;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.tree.SharedExtentProvider;
import lu.uni.adtool.tree.TreeChangeListener;
import lu.uni.adtool.tree.TreeLayout;
import lu.uni.adtool.tree.XmlConverter;
import lu.uni.adtool.ui.ADAction;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.TreeDockable;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.util.DefaultConfiguration;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public abstract class AbstractTreeCanvas extends JPanel
    implements Scrollable, TreeChangeListener, Printable, Pageable {

  public AbstractTreeCanvas(NodeTree tree, MainController mc) {
    super(new BorderLayout());
    this.tree = tree;
    this.controller = mc;
    this.localExtentProvider = false;
    if (tree != null) {
      this.getSharedExtentProvider().registerCanvas(this);
    }
    this.setBackground(Options.canv_BackgroundColor);
    this.scrollPane = null;
    this.printAttr = new HashPrintRequestAttributeSet();
    this.printAttr.add(MediaSizeName.ISO_A4);
    this.pageFormat = new PageFormat();
    this.history = new History();
    this.scale = 1;
    setScale(scale);
  }

  /**
   * Constructor used to export tree without showing it in a dockable
   */
  public AbstractTreeCanvas(NodeTree tree) {
    super(new BorderLayout());
    this.tree = tree;
    this.controller = null;
    this.localExtentProvider = true;
    this.setBackground(Options.canv_BackgroundColor);
    this.scrollPane = null;
    this.printAttr = new HashPrintRequestAttributeSet();
    this.printAttr.add(MediaSizeName.ISO_A4);
    this.pageFormat = new PageFormat();
    this.history = new History();
    this.scale = 1;
    setScale(scale);
  }

  public boolean isSand() {
    if (tree != null) {
      return tree.getLayout().isSand();
    }
    else {
      return true;
    }
  }

  public void treeChanged() {
    this.recalculateLayout();
    this.repaint();
  }

  public void notifyAllTreeChanged() {
    controller.getFrame().getDomainFactory().notifyAllTreeChanged(this.getTreeId());
    this.treeChanged();
  }

  /**
   * Gets the scrollPane for this instance.
   *
   * @return The scrollPane.
   */
  public JScrollPane getScrollPane() {
    return this.scrollPane;
  }

  public void addEditAction(EditAction a) {
    AbstractTreeCanvas canvas = this.getTreeCanvas();
    if (canvas != null) {
      canvas.history.addAction(a);
      canvas.updateUndoRedoItems();
    }
  }

  public void undo() {
    AbstractTreeCanvas canvas = this.getTreeCanvas();
    if (canvas != null) {
      canvas.history.undo(canvas);
      canvas.updateUndoRedoItems();
    }
  }

  public void redo() {
    AbstractTreeCanvas canvas = this.getTreeCanvas();
    if (canvas != null) {
      canvas.history.redo(canvas);
      canvas.updateUndoRedoItems();
    }
  }

  public abstract void setScrollPane(JScrollPane pane);

  public abstract void repaintAll();

  /**
   * Gets the label separated into lines
   *
   * @return The array of labels with each line as a separate entry
   */
  public abstract String getLabel(Node node);

  /**
   * Gets the label separated into lines
   *
   * @return The array of labels with each line as a separate entry
   */
  public String[] getLabelLines(Node node) {
    return getLabel(node).split("\n");
  }

  /**
   * Gets the node that covers a given point.
   *
   * @param x
   *          x-coordinate of the point.
   * @param y
   *          y-coordinate of the point.
   * @return
   */
  public Node getNode(double x, double y) {
    if (tree != null) {
      try {
        Point2D point = viewTransform.inverseTransform(new Point2D.Double(x, y), null);
        x = point.getX();
        y = point.getY();
      }
      catch (NoninvertibleTransformException e) {
        System.err.println("Cannot translate click point!!");
      }
      if (x > sizeX || y > sizeY || x < 0 || y < 0) {
        return null;
      }
      Shape shape;
      for (Node node : bufferedLayout.keySet()) {
        Rectangle2D rect = bufferedLayout.get(node);
        switch (Options.canv_ShapeDef) {
        case RECTANGLE:
          shape = rect;
          break;
        case OVAL:
          shape = new Ellipse2D.Double(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
          break;
        // case ROUNDRECT:
        default:
          shape = new RoundRectangle2D.Double(rect.getX(), rect.getY(), rect.getWidth(),
              rect.getHeight(), Options.canv_ArcSize, Options.canv_ArcSize);
          break;
        }
        if (shape.contains(x, y)) {
          return node;
        }
      }
    }
    return null;
  }

  public NodeTree getTree() {
    return this.tree;
  }

  public void report(String msg) {
    controller.report(msg);
  }

  public void reportError(String msg) {
    controller.reportError(msg);
  }

  public MainController getController() {
    return controller;
  }

  public SharedExtentProvider getSharedExtentProvider() {
    return tree.getSharedExtentProvider();
  }

  public void moveTree(double x, double y) {
    Rectangle r = scrollPane.getViewport().getViewRect();
    this.setMoveX(moveX + x);
    this.setMoveY(moveY + y);
    this.updateSize();
    this.scrollPane.scrollRectToVisible(r);
    this.repaint();
  }

  /**
   * Gets the scale for this instance.
   *
   * @return The scale.
   */
  public double getScale() {
    return this.scale;
  }

  public void zoomIn() {
    if (scale < 100) {
      this.setScale(scale * Options.canv_scaleFactor);
      this.repaint();
    }
  }

  /**
   * Returns label of the root node
   */
  public String getRootLabel() {
    return tree.getRoot(true).getName();
  }

  /**
   * Zoom out.
   *
   */
  public void zoomOut() {
    Point2D.Double point = new Point2D.Double(sizeX + borderPad / 2, sizeY + borderPad / 2);
    this.viewTransform.transform(point, point);
    if (Math.max(point.getX() - moveX * getScale(), point.getY() - moveY * getScale()) > 20) {
      this.setScale(scale / Options.canv_scaleFactor);
      this.repaint();
    }
  }

  /**
   * Reset zoom;
   *
   */
  public void resetZoom() {
    setScale(1);
    this.repaint();
  }

  /**
   * Sets the scale for this instance.
   *
   * @param scale
   *          The scale.
   */
  public void setScale(double scale) {
    this.scale = scale;
    this.viewTransform = new AffineTransform();
    this.viewTransform.scale(scale, scale);
    this.viewTransform.translate(moveX, moveY);
    this.viewTransform.translate(borderPad / 2, borderPad / 2);
    this.updateSize();
  }

  /**
   * Scales and centeres the tree to fit the window.
   *
   */
  public void fitToWindow() {
    int x = viewPortSize.width;
    int y = viewPortSize.height;
    double sX = sizeX + borderPad;
    double sY = sizeY + borderPad;
    double newScale = getScale() * Math.min(x / (sX * getScale()), y / (sY * getScale()));
    setScale(newScale);
    setMoveX((x / getScale() - (sX)) / 2.0);
    setMoveY((y / getScale() - (sY)) / 2.0);
    this.updateSize();
    this.repaint();
  }

  /**
   * Sets the focus for the node.
   *
   * @param focused
   *          The focused node
   */
  public void setFocus(Node focused) {
    if (this.focused != null) {
      lastFocused = this.focused;
    }
    // TODO - ensure unique focus?
    // if (focused != null){
    // tree.defocusAll();
    // }
    this.focused = (GuiNode) focused;
    if (focused != null) {
      Rectangle2D b2 = bufferedLayout.get(focused);
      if (b2 == null) {
        this.recalculateLayout();
        b2 = bufferedLayout.get(focused);
      }
      Point2D p1 = new Point2D.Double(b2.getX() - borderPad / 2, b2.getY() - borderPad / 2);
      Point2D p2 = new Point2D.Double((b2.getWidth() + borderPad) * scale,
          (b2.getHeight() + borderPad) * scale);
      viewTransform.transform(p1, p1);
      if (p1.getX() < 0) {
        setMoveX(getMoveX() - p1.getX() / scale);
        p1.setLocation(0, p1.getY());
      }
      if (p1.getY() < 0) {
        setMoveY(getMoveY() - p1.getY() / scale);
        p1.setLocation(p1.getX(), 0);
      }
      this.updateSize();
      scrollRectToVisible(
          new Rectangle((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY()));
    }
    this.repaint();
    if (controller != null && this instanceof AbstractDomainCanvas) {
      controller.getFrame().getRankingView().setFocus(this, focused, false);
    }
  }

  /**
   * Show print dialog and print canvas.
   *
   * @param doPrint
   * @return true if user clicked ok, false otherwise
   *
   */
  public boolean showPrintDialog(boolean doPrint) {
    if (tree != null) {
      try {
        PrinterJob pjob = PrinterJob.getPrinterJob();
        pjob.setPageable(this);
        printAttr.add(new PageRanges(1, getNumberOfPages()));
        if (doPrint) {
          if (pjob.printDialog(printAttr)) {
            pageFormat = pjob.getPageFormat(printAttr);
            pjob.print();
            return true;
          }
        }
        else {
          PageFormat page = pjob.pageDialog(getPageFormat(0));
          if (page != getPageFormat(0)) {
            setPageFormat(page);
            return true;
          }
        }
      }
      catch (PrinterException exc) {
        reportError(exc.getLocalizedMessage());
      }
    }
    return false;
  }

  /**
   * Scrolls canvas by a given vector.
   *
   * @param xShift
   *          x-coordinate of the point.
   * @param yShift
   *          y-coordinate of the point.
   * @return returns by how much canvas was really scrolled
   */
  public Point scrollTo(double xShift, double yShift) {
    Rectangle r = scrollPane.getViewport().getViewRect();
    Point p = r.getLocation();
    r.translate((int) -xShift, (int) -yShift);
    scrollRectToVisible(r);
    Rectangle r2 = scrollPane.getViewport().getViewRect();
    p = new Point((int) (p.getX() - r2.getX()), (int) (p.getY() - r2.getY()));
    return p;
  }

  /**
   * Removes the subtree with node as root.
   *
   * @param node
   *          root of a subtree.
   */
  public void removeTree(Node node) {
    if (!node.equals(tree.getRoot(true))) {
      this.addEditAction(new RemoveTree(node));
      if (lastFocused != null && lastFocused.equals(node)) {
        lastFocused = ((GuiNode) node).getParent(true);
      }
      if (lastFocused == null) {
        lastFocused = (GuiNode) tree.getRoot(true);
      }
      if (focused != null) {
        if (focused.equals(node)) {
          setFocus(((GuiNode) node).getParent(true));
        }
      }
      tree.removeTree(node);
      this.notifyAllTreeChanged();
      this.updateTerms();
    }
  }

  public void removeChildren(Node node) {
    this.addEditAction(new RemoveChildren(node));
    tree.removeAllChildren(node);
    this.notifyAllTreeChanged();
    this.updateTerms();
  }

  public Point2D transform(Point2D point) {
    try {
      return viewTransform.inverseTransform(point, null);
    }
    catch (NoninvertibleTransformException e) {
      return point;
    }
  }

  /**
   * Sets the viewPortSize for this instance.
   *
   * @param viewPortSize
   *          The viewPortSize.
   */
  public void setViewPortSize(Dimension viewPortSize) {
    this.viewPortSize = viewPortSize;
    // make up for dissapearing scrollbars
    if (this.scrollPane != null) {
      if (this.scrollPane.getHorizontalScrollBar().isVisible()) {
        this.viewPortSize.height +=
            this.scrollPane.getHorizontalScrollBar().getPreferredSize().height;
      }
      if (this.scrollPane.getVerticalScrollBar().isVisible()) {
        this.viewPortSize.width += this.scrollPane.getVerticalScrollBar().getPreferredSize().width;
      }
    }
    this.updateSize();
  }

  /**
   * {@inheritDoc}
   *
   * @see Scrollable#getPreferredScrollableViewportSize()
   */
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  /**
   * Sets the moveX for this instance.
   *
   * @param moveX
   *          The moveX.
   */
  public void setMoveX(double moveX) {
    if ((moveX) < 0) {
      moveX = 0;
    }
    this.viewTransform.translate(moveX - this.moveX, 0);
    this.moveX = moveX;
  }

  /**
   * Gets the moveX for this instance.
   *
   * @return The moveX.
   */
  public double getMoveX() {
    return this.moveX;
  }

  /**
   * Sets the moveY for this instance.
   *
   * @param moveY
   *          The moveY.
   */
  public void setMoveY(double moveY) {
    if ((moveY) < 0) {
      moveY = 0;
    }
    this.viewTransform.translate(0, moveY - this.moveY);
    this.moveY = moveY;
  }

  /**
   * Gets the moveY for this instance.
   *
   * @return The moveY.
   */
  public double getMoveY() {
    return this.moveY;
  }

  /**
   * Recalculates the positions of all nodes.
   *
   */
  protected void recalculateLayout() {
    Debug.log("tree " + tree);
    this.sizeX = 0;
    this.sizeY = 0;
    bufferedLayout = null;
    NodeExtentProvider<Node> extentProvider;
    if (localExtentProvider) {
      extentProvider = new LocalExtentProvider(this);
    }
    else {
      Debug.log("tree:" + tree);
      extentProvider = tree.getSharedExtentProvider();
    }
    org.abego.treelayout.TreeLayout<Node> treeLayout = new org.abego.treelayout.TreeLayout<Node>(
        tree.getTreeForLayout(), (NodeExtentProvider<Node>) extentProvider, configuration);
    bufferedLayout = treeLayout.getNodeBounds();
    for (Rectangle2D.Double rect : bufferedLayout.values()) {
      sizeX = Math.max(sizeX, rect.getMaxX());
      sizeY = Math.max(sizeY, rect.getMaxY());
    }
    setScale(scale);
  }

  /**
   * Gets the focused node for this instance.
   *
   * @return The focused node.
   */
  public GuiNode getFocused() {
    return this.focused;
  }

  /**
   * Gets the lastFocused for this instance.
   *
   * @return The lastFocused.
   */
  public GuiNode getLastFocused() {
    return this.lastFocused;
  }

  /**
   * Get the right sibling of a node.
   *
   * @param node
   * @return
   */
  public GuiNode getRightSibling(Node node) {
    return ((GuiNode) node).getRightSibling();
  }

  /**
   * Get the left sibling of a node.
   *
   * @param node
   * @return
   */
  public GuiNode getLeftSibling(Node node) {
    return ((GuiNode) node).getLeftSibling();
  }

  public GuiNode getMiddleChild(Node node) {
    return ((GuiNode) node).getMiddleChild();
  }

  public GuiNode getParentNode(Node node) {
    return ((GuiNode) node).getParent(false);
  }

  public void toggleFold(Node node) {
    this.addEditAction(new FoldAction(node, false));
    if (node != null && node.getChildren().size() > 0) {
      tree.toggleFold(node, true);
      notifyAllTreeChanged();
    }
  }

  public void toggleAboveFold(Node node) {
    this.addEditAction(new FoldAction(node, true));
    if (node.getParent() != null) {
      tree.toggleAboveFold(node, true);
      notifyAllTreeChanged();
    }
  }

  public void createPdf(FileOutputStream fileStream) {
    double oldScale = getScale();
    Dimension dim = getPreferredSize();
    if (dim.width > 14399 || dim.height > 14399) {
      setScale(14000.0 / Math.max(dim.width, dim.height) * oldScale);
      dim = getPreferredSize();
    }
    try {
      Document document = new Document(new com.itextpdf.text.Rectangle(dim.width, dim.height));
      PdfWriter writer = PdfWriter.getInstance(document, fileStream);
      document.open();
      PdfContentByte canv = writer.getDirectContent();
      Graphics2D g2 = new PdfGraphics2D(canv, dim.width, dim.height);
      this.paintComponent(g2);
      g2.dispose();
      document.close();
      fileStream.close();
    }
    catch (DocumentException e) {
      reportError(Options.getMsg("error.exportingpdf") + e);
    }
    catch (IOException e) {
      reportError(Options.getMsg("error.exportingpdf") + e);
    }
    setScale(oldScale);
  }

  /**
   * Save tree as an image
   *
   * @param fileStream
   *          stream to which we write
   * @param formatName
   *          informal name of the format e. g. "jpg" or "png"
   */
  public void createImage(FileOutputStream fileStream, String formatName) {
    Dimension dim = getPreferredSize();
    BufferedImage bufferedImage =
        new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.setColor(Options.canv_BackgroundColor);
    Rectangle r = new Rectangle(dim);
    if (r != null) {
      g2d.fillRect(r.x, r.y, r.width, r.height);
    }
    this.paintComponent(g2d);
    g2d.dispose();
    try {
      ImageIO.write(bufferedImage, formatName, fileStream);
      fileStream.close();
    }
    catch (IOException e) {
      reportError(Options.getMsg("error.export.fail") + e);
    }
  }

  /**
   * Save tree in XML format
   *
   * @param fileStream
   *          stream to which we write
   */
  public void createXml(FileOutputStream fileStream) {
    XmlConverter converter = new XmlConverter();
    try {
      TreeLayout layout = tree.getLayout();
      if (Options.main_saveDomains) {
        Set<Integer> ids= new TreeSet<Integer>();
        for (ValuationDomain values : layout.getDomains()) {
          ids.add(values.getDomainId());
        }
        converter.exportTo(fileStream, layout, ids);
      }
      else {
        converter.exportTo(fileStream, layout, null);
      }
    }
    catch (IOException e) {
      reportError(Options.getMsg("error.xmlexport.fail") + e);
    }
  }

  /**
   * Save tree in Latex format
   *
   * @param fileStream
   *          stream to which we write
   */
  public void createLatex(FileOutputStream fileStream) {
    XmlConverter converter = new XmlConverter();
    try {
      TreeLayout layout = tree.getLayout();
      converter.exportLatex(fileStream, layout);
    }
    catch (IOException e) {
      reportError(Options.getMsg("error.latexexport.fail") + e);
    }
  }

  /**
   * Get string representing term of the tree.
   */
  public String getTermsString() {
    if (this.tree.getRoot(true) instanceof SandNode) {
      return ((SandNode) this.tree.getRoot(true)).toTerms();
    }
    else {
      return ((ADTNode) this.tree.getRoot(true)).toTerms();
    }
  }

  public void createTxt(FileOutputStream fileStream) throws IOException {
    Node root = this.tree.getRoot(true);
    if (root instanceof SandNode) {
      fileStream.write(((SandNode) root).toTerms().getBytes(Charset.forName("UTF-8")));
    }
    else {
      fileStream.write(((ADTNode) root).toTerms().getBytes(Charset.forName("UTF-8")));
    }
    fileStream.close();
  }

  /**
   * {@inheritDoc}
   *
   * @see Scrollable#getScrollableUnitIncrement(Rectangle,int,int)
   */
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    return 0;
  }

  /**
   * {@inheritDoc}
   *
   * @see Scrollable#getScrollableBlockIncrement(Rectangle,int,int)
   */
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
    int maxUnitIncrement = 1;
    if (orientation == SwingConstants.HORIZONTAL)
      return visibleRect.width - maxUnitIncrement;
    else
      return visibleRect.height - maxUnitIncrement;
  }

  /**
   * {@inheritDoc}
   *
   * @see Scrollable#getScrollableTracksViewportWidth()
   */
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @see Scrollable#getScrollableTracksViewportHeight()
   */
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @see Printable#print(Graphics,PageFormat,int)
   */
  public int print(Graphics g, PageFormat pf, int page) {
    double pW = pf.getImageableWidth();
    double pH = pf.getImageableHeight();
    Point p = getColsRows(pW, pH, getNumberOfPages());
    if (page >= (int) (p.getX() * p.getY())) {
      return NO_SUCH_PAGE;
    }
    // store focus and set it to null
    GuiNode tFocus = focused;
    focused = null;
    double printScaleX = (pW * p.getX()) / (sizeX + Options.canv_LineWidth);
    double printScaleY = (pH * p.getY()) / (sizeY + Options.canv_LineWidth);
    if (Options.print_perserveAspectRatio) {
      if (printScaleX < printScaleY) {
        printScaleY = printScaleX;
      }
      else {
        printScaleX = printScaleY;
      }
    }
    int shiftX = page % (int) p.getX();
    int shiftY = page / (int) p.getX();
    // align origin
    g.translate((int) (pf.getImageableX() - (shiftX * pW)),
        (int) (pf.getImageableY() - (shiftY * pH)));
    ((Graphics2D) g).scale(printScaleX, printScaleY);
    g.translate(Options.canv_LineWidth, Options.canv_LineWidth);
    this.paintComponent((Graphics2D) g, tree.getRoot(false));
    // restore focus
    focused = tFocus;
    return PAGE_EXISTS;
  }

  /**
   * Paint the canvas starting at startNode.
   *
   * @param g2
   *          graphics context.
   * @param startNode
   *          root node from which we paint.
   */
  public void paintComponent(final Graphics2D g2, Node startNode) {
    g2.setStroke(basicStroke);
    if (Options.canv_DoAntialiasing) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    else {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
    paintEdges(g2, startNode);
    // paint the boxes
    paintBox(g2, startNode);
    if (focused != null) {
      paintFocus(g2, focused);
    }
  }

  public abstract void updateTerms();

  /**
   * {@inheritDoc}
   *
   * @see javax.swing.JComponent#paintComponent(Graphics)
   */
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);
    final Graphics2D g2 = (Graphics2D) g;
    g2.setColor(Options.canv_BackgroundColor);
    Rectangle r = g2.getClipBounds();
    if (r != null) {
      g2.fillRect(r.x, r.y, r.width, r.height);
    }
    else {
      Debug.log("null clip bounds");
    }
    g2.transform(viewTransform);
    // g2.clearRect(-borderPad, -borderPad, (int) sizeX + borderPad, (int) sizeY
    // + borderPad);
    if (tree != null) {
      paintComponent(g2, tree.getRoot(false));
    }
  }

  public void undoGetNewLabel() {
    labelCounter = labelCounter - 1;
  }

  public void updateUndoRedoItems() {
    ADAction undo = controller.getUndoItem();
    ADAction redo = controller.getRedoItem();
    AbstractTreeCanvas canvas = this.getTreeCanvas();
    if (canvas != null) {
      String text = canvas.history.getUndoText();
      if (text != null) {
        undo.setEnabled(true);
        undo.setName(text);
      }
      else {
        undo.setEnabled(false);
        undo.setName(Options.getMsg("edit.undo.txt"));
      }
      text = canvas.history.getRedoText();
      if (text != null) {
        redo.setEnabled(true);
        redo.setName(text);
      }
      else {
        redo.setEnabled(false);
        redo.setName(Options.getMsg("edit.redo.txt"));
      }
    }
    else {
      undo.setEnabled(false);
      undo.setName(Options.getMsg("edit.undo.txt"));
      redo.setEnabled(false);
      redo.setName(Options.getMsg("edit.redo.txt"));
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void addDomain(Domain<Ring> domain) {
    TreeDockable currentTree = (TreeDockable) this.controller.getControl()
        .getMultipleDockable(TreeDockable.TREE_ID + Integer.toString(this.getTreeId()));
    if (currentTree != null) {
      int domainId = tree.getLayout().getNewDomainId();
      this.addEditAction(new AddDomain(domainId, domain));
      DomainDockable d = null;
      DomainFactory factory = controller.getFrame().getDomainFactory();
      if (domain instanceof SandDomain) {
        d = factory.read(new ValuationDomain(this.getTreeId(), domainId, (SandDomain) domain));
      }
      else {
        d = factory.read(new ValuationDomain(this.getTreeId(), domainId, (AdtDomain) domain));
      }
      Debug.log("Adding domain to control with id:" + d.getUniqueId());
      controller.getControl().addDockable(d.getUniqueId(), d);
      currentTree.showDomain(d);
    }
  }

  public void removeDomain(DomainDockable dockable) {
    boolean localExtentProvider = dockable.getCanvas().hasLocalExtentProvider();
    if (!localExtentProvider) {
      dockable.getCanvas().setLocalExtentProvider(true);
    }
    if (this.getTree().getLayout().removeValuation(dockable.getCanvas().getValues())) {
      this.addEditAction(new RemoveDomain(dockable.getCanvas().getValues(), localExtentProvider));
      this.controller.getFrame().getDomainFactory().removeDomain(dockable);
    }
  }

  public abstract AbstractTreeCanvas getTreeCanvas();

  protected String getNewLabel() {
    labelCounter = labelCounter + 1;
    return LABEL_PREFIX + labelCounter;
  }

  /* * * * print functions * * * * * * * * * * * * * * * * * * * */
  /**
   * {@inheritDoc}
   *
   * @see Pageable#getNumberOfPages()
   */
  public int getNumberOfPages() {
    // return 1;
    return Options.print_noPages;
  }

  /**
   * {@inheritDoc}
   *
   * @see Pageable#getPrintable(int)
   */
  public Printable getPrintable(int pageIndex) {
    if (pageIndex >= Options.print_noPages) {
      throw new IndexOutOfBoundsException("No page with number " + pageIndex);
    }
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @see Pageable#getPageFormat(int)
   */
  public PageFormat getPageFormat(int pageIndex) {
    return pageFormat;
  }

  /**
   * Sets the pageFormat for this instance.
   *
   * @param pageFormat
   *          The pageFormat.
   */
  public void setPageFormat(PageFormat pageFormat) {
    this.pageFormat = pageFormat;
  }

  public Point getColsRows(int noMaxPages) {
    PageFormat pf = getPageFormat(0);
    return getColsRows(pf.getImageableWidth(), pf.getImageableHeight(), noMaxPages);
  }

  public int getTreeId() {
    if (tree == null) {
      if (this instanceof AbstractDomainCanvas) {
        return ((AbstractDomainCanvas<?>) this).values.getTreeId();
      }
      return -1;
    }
    return tree.getLayout().getTreeId();
  }

  /**
   * Paints the node labels.
   *
   * @param g
   * @param node
   * @param textCol
   *          color for the text.
   */
  protected void paintLabels(final Graphics2D g, final Node node, final Color textCol) {
    final Rectangle2D.Double box = bufferedLayout.get(node);
    int x = (int) box.x;
    int y = (int) box.y;
    // draw the text on top of the box (possibly multiple lines)
    final String[] lines = this.getLabelLines(node);
    final FontMetrics m = getFontMetrics(Options.canv_Font);
    // center vertically
    y = (int) (y + box.height / 2 - (lines.length * m.getHeight()) / 2) - 1;
    // center horizontally
    x = x + (int) (box.width / 2);
    // draw strings
    y += m.getAscent() + m.getLeading() + 1;
    g.setColor(textCol);
    g.setFont(Options.canv_Font);
    for (int i = 0; i < lines.length; i++) {
      g.drawString(lines[i], x - (m.stringWidth(lines[i])) / 2, y);
      y += m.getHeight();
    }
  }

  /**
   * Paints the node box and text.
   *
   * @param g
   * @param node
   */
  protected void paintBox(final Graphics2D g, final Node node) {
    // draw the box in the background
    Options.ShapeType shape;
    Color fillCol;
    Color borderCol;
    Color textCol;
    fillCol = getFillColor(node);
    // ATTACKER type
    if (node instanceof ADTNode) {
      ADTNode.Role defender =
          tree.getLayout().getSwitchRole() ? ADTNode.Role.PROPONENT : ADTNode.Role.OPPONENT;
      if (((ADTNode) node).getRole() == defender) {
        borderCol = Options.canv_BorderColorDef;
        textCol = Options.canv_TextColorDef;
        shape = Options.canv_ShapeDef;
      }
      else {
        // ATTACKER type
        borderCol = Options.canv_BorderColorAtt;
        textCol = Options.canv_TextColorAtt;
        shape = Options.canv_ShapeAtt;
      }
    }
    else {
      borderCol = Options.canv_BorderColorAtt;
      textCol = Options.canv_TextColorAtt;
      shape = Options.canv_ShapeAtt;
    }

    // get position of node
    final Rectangle2D.Double box = bufferedLayout.get(node);
    int x = (int) box.x;
    int y = (int) box.y;
    g.setColor(Options.canv_EdgesColor);
    if (((GuiNode) node).isFolded()) {
      Polygon triangle = new Polygon();
      triangle.addPoint(x + (int) (box.width / 2.0), y);
      triangle.addPoint(x, y + (int) box.height + 6);
      triangle.addPoint(x + (int) box.width, y + (int) box.height + 6);
      g.fillPolygon(triangle);
    }
    if (((GuiNode) node).isAboveFolded()) {
      Polygon triangle = new Polygon();
      triangle.addPoint(x + (int) (box.width / 2.0), y - 10);
      triangle.addPoint(x + (int) (box.width / 2.0) - 6, y);
      triangle.addPoint(x + (int) (box.width / 2.0) + 6, y);
      g.fillPolygon(triangle);
    }
    g.setColor(fillCol);
    g.setStroke(basicStroke);
    switch (shape) {
    case RECTANGLE:
      g.fillRect(x, y, (int) box.width - 1, (int) box.height - 1);
      g.setColor(borderCol);
      g.drawRect(x, y, (int) box.width - 1, (int) box.height - 1);
      break;
    case OVAL:
      g.fillOval(x, y, (int) box.width - 1, (int) box.height - 1);
      g.setColor(borderCol);
      g.drawOval(x, y, (int) box.width - 1, (int) box.height - 1);
      break;
    // case ROUNDRECT:
    // default:
    // g.fillRoundRect(x, y, (int) box.width - 1, (int) box.height - 1,
    // Options.canv_ArcSize,
    // Options.canv_ArcSize);
    // g.setColor(borderCol);
    // g.drawRoundRect(x, y, (int) box.width - 1, (int) box.height - 1,
    // Options.canv_ArcSize,
    // Options.canv_ArcSize);
    // break;
    }
    paintLabels(g, node, textCol);
    for (Node child : tree.getChildrenList(node, false)) {
      paintBox(g, child);
    }
  }

  /**
   * Draws the edges of the tree
   *
   * @param g2
   * @param parent
   */
  protected void paintEdges(final Graphics2D g2, final Node parent) {
    if (parent.getNotNullChildren().size() != 0) {
      final Rectangle2D.Double b1 = bufferedLayout.get(parent);
      final double x1 = b1.getCenterX();
      final double y1 = b1.getCenterY();
      double maxX = 0;
      double minX = x1;
      int noChildren = 0;
      g2.setColor(Options.canv_EdgesColor);
      Rectangle2D.Double b2 = new Rectangle2D.Double(0, 0, 0, 0);
      for (Node child : tree.getChildrenList(parent, false)) {
        b2 = bufferedLayout.get(child);
        if (parent instanceof ADTNode
            && ((ADTNode) child).getRole() != ((ADTNode) parent).getRole()) {
          g2.setStroke(counterStroke);
        }
        else {
          g2.setStroke(basicStroke);
        }
        if (!(parent instanceof ADTNode
            && ((ADTNode) child).getRole() != ((ADTNode) parent).getRole())) {
          maxX = Math.max(maxX, b2.getCenterX());
          minX = Math.min(minX, b2.getCenterX());
          noChildren++;
        }
        g2.drawLine((int) x1, (int) y1, (int) b2.getCenterX(), (int) b2.getCenterY());
      }
      g2.setStroke(basicStroke);
      boolean drawArc = false;
      boolean drawArrow = false;

      if (parent instanceof SandNode) {
        if ((((SandNode) parent).getType() == SandNode.Type.AND
            || ((SandNode) parent).getType() == SandNode.Type.SAND) && noChildren > 1) {
          drawArc = true;
          if (((SandNode) parent).getType() == SandNode.Type.SAND) {
            drawArrow = true;
          }
        }
      }
      else if (parent instanceof ADTNode) {
        if ((((ADTNode) parent).getType() == ADTNode.Type.AND_OPP
            || ((ADTNode) parent).getType() == ADTNode.Type.AND_PRO) && noChildren > 1) {
          drawArc = true;
        }
      }
      if (drawArc) {
        double tangens1 = (b2.getCenterY() - (double) y1) / (minX - (double) x1);
        double tangens2 = (b2.getCenterY() - (double) y1) / (maxX - (double) x1 - 1);
        double shear = (double) (b1.getWidth() + Options.canv_ArcPadding)
            / (double) (b1.getHeight() + Options.canv_ArcPadding);
        double startAngle = -Math.toDegrees(Math.atan(tangens2 * shear));
        g2.setColor(Options.canv_ArcColor);
        if (startAngle > 0) {
          startAngle = startAngle - 180;
        }
        double endAngle = -180 - Math.toDegrees(Math.atan(tangens1 * shear));
        double a = b1.getWidth() + Options.canv_ArcPadding;
        double b = b1.getHeight() + Options.canv_ArcPadding;
        Arc2D arc = new Arc2D.Double(b1.getX() - Options.canv_ArcPadding / 2,
            b1.getY() - Options.canv_ArcPadding / 2, a, b, startAngle, endAngle - startAngle,
            Arc2D.OPEN);

        if (arc != null) {
          g2.draw(arc);
        }
        if (drawArrow) {
          double cos = Math.cos(Math.toRadians(startAngle));
          double sin = Math.sin(Math.toRadians(startAngle));
          double cos2 = Math.cos(Math.toRadians(startAngle - 75));
          double sin2 = Math.sin(Math.toRadians(startAngle - 75));
          double cos3 = Math.cos(Math.toRadians(startAngle - 125));
          double sin3 = Math.sin(Math.toRadians(startAngle - 125));
          g2.draw(new Line2D.Double(x1 + a * cos / (double) 2, y1 - b * sin / (double) 2,
              x1 + a * cos / (double) 2 + 8 * cos2, y1 - b * sin / (double) 2 - 8 * sin2));
          g2.draw(new Line2D.Double(x1 + a * cos / (double) 2, y1 - b * sin / (double) 2,
              x1 + a * cos / (double) 2 + 8 * cos3, y1 - b * sin / (double) 2 - 8 * sin3));
        }
      }
      for (Node child : tree.getChildrenList(parent, false)) {
        paintEdges(g2, child);
      }
    }
    g2.setStroke(basicStroke);
  }

  protected abstract Color getFillColor(Node node);

  /**
   * Draws the node focus indication.
   *
   * @param g2
   * @param node
   */
  protected void paintFocus(final Graphics2D g2, final Node node) {
    final Rectangle2D.Double box = bufferedLayout.get(node);
    if (box == null) {
      return;
    }
    int x = (int) box.x - focusPad / 2;
    int y = (int) box.y - focusPad / 2;
    g2.setColor(Color.blue);
    g2.setStroke(selectionStroke);
    // ATTACKER type
    Options.ShapeType shape = Options.canv_ShapeAtt;
    if (node instanceof ADTNode) {
      ADTNode.Role defender =
          tree.getLayout().getSwitchRole() ? ADTNode.Role.PROPONENT : ADTNode.Role.OPPONENT;
      if (((ADTNode) node).getRole() == defender) {
        shape = Options.canv_ShapeDef;
      }
    }
    switch (shape) {
    case RECTANGLE:
      g2.drawRect(x, y, (int) box.width + focusPad - 1, (int) box.height + focusPad - 1);
      break;
    case OVAL:
      g2.drawOval(x, y, (int) box.width + focusPad - 1, (int) box.height + focusPad - 1);
      break;
    // case ROUNDRECT:
    // default:
    // g2.drawRoundRect(x, y, (int) box.width + focusPad - 1, (int) box.height +
    // focusPad - 1,
    // Options.canv_ArcSize + 1, Options.canv_ArcSize + 1);
    // break;
    }
  }

  /**
   * Recalculates the total size of the tree.
   *
   */
  protected void updateSize() {
    Point2D.Double point =
        new Point2D.Double(this.sizeX + this.borderPad / 2, this.sizeY + this.borderPad / 2);
    this.viewTransform.transform(point, point);
    int x = 0;
    int y = 0;
    if (this.viewPortSize != null) {
      x = this.viewPortSize.width + x;
      y = this.viewPortSize.height + y;
    }
    Dimension dim = new Dimension(Math.max((int) point.getX(), x), Math.max((int) point.getY(), y));

    this.setPreferredSize(dim);
    this.setMinimumSize(dim);
    this.revalidate();
  }

  public ADToolMain getFrame() {
    return controller.getFrame();
  }

  /**
   * Calculates optimal number of rows and columns - used for printing.
   *
   * @param pageHeight
   *          height of the page.
   * @param pageWidth
   *          width of the page.
   * @param noPages
   *          maximum number of pages.
   * @return
   */
  private Point getColsRows(double pageHeight, double pageWidth, int noPages) {
    double ratio = (pageWidth / pageHeight) / (sizeY / sizeX);
    int rows = 1;
    int cols = 1;
    while (true) {
      if (ratio * ((double) rows / cols) > 1) {
        cols++;
        if (cols * rows > noPages) {
          cols--;
          return new Point((int) cols, (int) rows);
        }
      }
      else {
        rows++;
        if (cols * rows > noPages) {
          rows--;
          return new Point((int) cols, (int) rows);
        }
      }
    }
  }

  protected NodeTree                      tree;
  /**
   * Size of canvas
   */
  protected double                        sizeX            = 0;
  /**
   * Size of canvas after transformation
   */
  protected double                        sizeY            = 0;
  /**
   * If true then we do not synchronize our size with other canvases.
   */
  protected boolean                       localExtentProvider;

  /**
   * A page format used for printing.
   */
  protected PageFormat                    pageFormat;
  protected PrintRequestAttributeSet      printAttr;
  /**
   * Holds a size of the viewPort
   */
  protected Dimension                     viewPortSize;
  /**
   * A map between nodes and its positions. Used to buffer the result of Walkers
   * algorithm.
   */
  protected Map<Node, Rectangle2D.Double> bufferedLayout;
  protected DefaultConfiguration<Node>    configuration;
  protected GuiNode                       focused;
  /**
   * If focused is null, this holds the value of last focused node - never null.
   */
  protected GuiNode                       lastFocused;
  /**
   * Transformation doing scaling and moving.
   */
  protected AffineTransform               viewTransform    = new AffineTransform();

  /**
   * A scale factor for drawing.
   */
  protected double                        scale            = 1;
  protected final int                     borderPad        = 20;
  /**
   * How much we move the canvas horizontally.
   */
  protected double                        moveX            = 0;
  /**
   * How much we move the canvas vertically.
   */
  protected double                        moveY            = 0;
  /**
   * How much a focus circle is padded.
   */
  protected final int                     focusPad         = 10;
  /**
   * JScrollPane that handles canvas scrolling.
   */
  protected JScrollPane                   scrollPane;
  /**
   * Parameters for the Walkers algorithm.
   */
  protected final BasicStroke             selectionStroke  =
      new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {6, 10}, 0);
  protected final BasicStroke             basicStroke      =
      new BasicStroke(Options.canv_LineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  private final BasicStroke               counterStroke    = new BasicStroke(Options.canv_LineWidth,
      BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] {0, 6}, 0);
  protected MainController                controller;
  protected History                       history;

  static final long                       serialVersionUID = 158222312311522883L;
  protected int                           labelCounter;

  protected static final String           LABEL_PREFIX     = "N_";

}
