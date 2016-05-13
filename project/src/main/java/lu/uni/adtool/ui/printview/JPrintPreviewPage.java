/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>)
 * Date:   10/12/2015
 * Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty of Science,
 *     Technology and Communication FSTC
 * All rights reserved.
 * Licensed under GNU Affero General Public License 3.0;
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.ui.printview;

import lu.uni.adtool.tools.Options;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * Renderer for a printable component.
 *
 * @author Piotr Kordy
 */
public class JPrintPreviewPage extends JComponent {
  /**
   * Default constructor.
   *
   * @param newPageable
   *          component to be previewed.
   * @param newPageNumber
   *          page to be previewed.
   */
  public JPrintPreviewPage(final Pageable newPageable, final int newPageNumber) {
    this.pageable = newPageable;
    this.pageformat = pageable.getPageFormat(pageNumber);
    this.pageNumber = newPageNumber;
    prefWidth = (int) pageformat.getWidth();
    prefHeight = (int) pageformat.getHeight();
    scaledWidth = prefWidth;
    scaledHeight = prefHeight;
    imageableArea = new Rectangle((int) pageformat.getImageableX(),
        (int) pageformat.getImageableY(), (int) (pageformat.getImageableWidth() + 0.1),
        (int) (pageformat.getImageableHeight() + 0.1));
    if (Options.printview_showPageNumbers) {
      pageNumberRenderer = (PageNumberRenderer) new DefaultPageNumberRenderer();
      final Component numberRenderer = pageNumberRenderer.getRenderer(pageNumber + 1, false, false);
      rHeight = (int) numberRenderer.getPreferredSize().height;
      border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, rHeight, 0),
          new JPrintPreviewBorder());
    }
    else {
      border = new JPrintPreviewBorder();
    }

    setBorder(border);
    this.setScale(1.0, 1.0);
  }

  /**
   * {@inheritDoc}
   *
   * @see JComponent#getPreferredSize()
   */
  public final Dimension getPreferredSize() {
    return new Dimension(
        (int) ((scaledWidth + border.getBorderInsets(this).right
            + border.getBorderInsets(this).left)),
        (int) ((scaledHeight + border.getBorderInsets(this).top
            + border.getBorderInsets(this).bottom)));
  }

  /**
   * {@inheritDoc}
   *
   * @see JComponent#getMaximumSize()
   */
  public final Dimension getMaximumSize() {
    return getPreferredSize();
  }

  /**
   * {@inheritDoc}
   *
   * @see JComponent#getMinimumSize()
   */
  public final Dimension getMinimumSize() {
    return getPreferredSize();
  }

  /**
   * {@inheritDoc}
   *
   * @see JComponent#paintComponent(Graphics)
   */
  public final void paintComponent(final Graphics g) {
    final Graphics2D graphics = (Graphics2D) g.create();
    if (cachedImage == null) {
      cachedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
      final Graphics2D g2 = cachedImage.createGraphics();
      g2.setColor(Options.printview_paper);
      g2.fillRect(0, 0, scaledWidth, scaledHeight);
      g2.scale(scaleX, scaleY);
      g2.clip(imageableArea);
      try {
        pageable.getPrintable(pageNumber).print(g2, pageformat, pageNumber);
      }
      catch (IndexOutOfBoundsException e) {
        e.printStackTrace();
      }
      catch (PrinterException e) {
        e.printStackTrace();
      }
      g2.dispose();
    }
    graphics.drawImage(cachedImage, null, 0, 0);
    cachedImage.flush();
    final int height = (int) getPreferredSize().getHeight();
    if (pageNumberRenderer != null && Options.printview_showPageNumbers) {
      graphics.translate(0, height - rHeight);
      final Component numberRenderer = pageNumberRenderer.getRenderer(pageNumber + 1, false, false);
      graphics.clipRect(0, 0, scaledWidth, rHeight);
      numberRenderer.setBounds(0, 0, scaledWidth, rHeight);
      numberRenderer.paint(graphics);
      graphics.translate(0, -(height - rHeight));
    }
    graphics.dispose();
  }

  /**
   * Sets the new scale.
   *
   * @param scaleX
   *          scale for x-axis.
   * @param scaleY
   *          scale for y-axis.
   */
  public final void setScale(final double scaleX, final double scaleY) {
    this.scaleX = scaleX;
    this.scaleY = scaleY;
    scaledWidth = (int) (prefWidth * scaleX);
    scaledHeight = (int) (prefHeight * scaleY);
    cachedImage = null;
    repaint();
    // revalidate();
  }
  static final long          serialVersionUID = 3420168465142358486L;
  private Pageable           pageable;
  private PageFormat         pageformat;
  private int                pageNumber;
  private int                scaledWidth;
  private int                scaledHeight;
  private int                prefWidth;
  private int                prefHeight;
  private double             scaleX;
  private double             scaleY;
  private Border             border;
  private int                rHeight;
  private PageNumberRenderer pageNumberRenderer;
  private BufferedImage      cachedImage;
  private Rectangle          imageableArea;

}
