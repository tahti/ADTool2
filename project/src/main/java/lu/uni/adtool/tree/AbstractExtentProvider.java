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
 * @author Piotr Kordy
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
//     result = 1.4142136 * x;
//     Options.ShapeType shape;
//     double result = x;
//     shape = Options.canv_ShapeAtt;// allow attacker only for the moment TODO - fix
//     if ((node instanceof ADTNode) && (((ADTNode) node).getRole() == ADTNode.Role.OPPONENT)) {
//       shape = Options.canv_ShapeDef;// allow attacker only for the moment
//     }
//     switch (shape) {
//     case OVAL:
//       result = 1.4142136 * x;
//       break;
//     case RECTANGLE:
//     default:
//       result = x + 2*X_PADDING;
//       break;
//     }
    return 1.4142136 * x;
  }

  private final static int   X_PADDING = 5;
  private final static int   Y_PADDING = 5;
}
