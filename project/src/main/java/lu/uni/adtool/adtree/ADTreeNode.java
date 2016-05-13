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
package lu.uni.adtool.adtree;

import java.io.Serializable;

/**
 * Represents a node in the Atack Defense Tree. Used to import old adtfiles only.
 *
 * @author Piotr Kordy
 */
public class ADTreeNode implements Serializable {
  static final long         serialVersionUID = 19696324774341496L;
  private static int        idCounter        = 1;
  private boolean           folded;
  private boolean           aboveFolded;
  /**
   * The type of the node.
   */
  private Type              type;
  private RefinementType    refinementType;
  private boolean           countered;
  private int               id;
  private int               level;
  /**
   * Label of the node.
   */
  private String            label;

  /**
   * Identifies the type of node.
   */
  public enum Type {
    /**
     * opponent type of node.
     */
    OPPONENT,
    /**
     * proponent type of node.
     */
    PROPONENT
  }

  /**
   * Identifies the type of refinement.
   */
  public enum RefinementType {
    DISJUNCTIVE, CONJUNCTIVE
  }

  /**
   * Default constructor. {@inheritDoc}
   *
   * @see Object#ADTreeNode()
   */
  public ADTreeNode() {
  }

  /**
   * A constructor.
   *
   * @param type
   * @param refinementType
   *
   */
  public ADTreeNode(final Type type, final RefinementType refinementType) {
    this(type, refinementType, "N_" + idCounter);
  }

  /**
   * A constructor.
   *
   * @param type
   * @param refinementType
   * @param label
   *
   */
  public ADTreeNode(final Type type, final RefinementType refinementType, final String label) {

    if (label != null) {
      setLabel(label);
    }
    else {
      setLabel("");
    }
    level = 0;
    this.type = type;
    this.refinementType = refinementType;
    this.countered = false;
    id = idCounter;
    idCounter++;
    folded = false;
    aboveFolded = false;
  }

  public static void resetCounter(int number) {
    idCounter = number;
  }

  /**
   * Checks if two nodes are equal.
   *
   * @param node
   * @return
   */
  public boolean equals(ADTreeNode node) {
    if (node != null) {
      return id == node.getId();
    }
    else {
      return false;
    }
  }

  /**
   * Sets the label for this instance.
   *
   * @param label
   *          The label.
   */
  public void setLabel(String label) {
    this.label = label.trim().replaceAll("(?m)^ +| +$|^[ \t]*\r?\n|( )+", "$1");
  }

  /**
   * Sets the refinementType for this instance.
   *
   * @param refinementType
   *          The refinementType.
   */
  public void setRefinementType(RefinementType refinementType) {
    this.refinementType = refinementType;
  }

  /**
   * Determines if this instance is folded.
   *
   * @return The folded.
   */
  public final boolean isFolded() {
    return this.folded;
  }

  /**
   * Sets whether or not this instance is folded.
   *
   * @param folded
   *          The folded.
   */
  public void setFolded(boolean folded) {
    this.folded = folded;
  }

  /**
   * Determines if this instance is aboveFolded.
   *
   * @return The aboveFolded.
   */
  public boolean isAboveFolded() {
    return this.aboveFolded;
  }

  /**
   * Sets whether or not this instance is aboveFolded.
   *
   * @param aboveFolded
   *          The aboveFolded.
   */
  public void setAboveFolded(boolean aboveFolded) {
    this.aboveFolded = aboveFolded;
  }

  /**
   * Gets the type for this instance.
   *
   * @return The type.
   */
  public final Type getType() {
    return this.type;
  }

  /**
   * Toggles the refinement type of node.
   */
  public void changeOp() {
    if (getRefinmentType() == RefinementType.DISJUNCTIVE) {
      setRefinementType(RefinementType.CONJUNCTIVE);
    }
    else {
      setRefinementType(RefinementType.DISJUNCTIVE);
    }
  }

  /**
   * Toggles the type of node.
   */
  public void changeType() {
    if (getType() == Type.OPPONENT) {
      setType(Type.PROPONENT);
    }
    else {
      setType(Type.OPPONENT);
    }
  }

  /**
   * Sets the type for this instance.
   *
   * @param type
   *          The type.
   */
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * Gets the refinementType for this instance.
   *
   * @return The refinementType.
   */
  public RefinementType getRefinmentType() {
    return this.refinementType;
  }


  /**
   * Determines if this instance is countered.
   *
   * @return The countered.
   */
  public boolean isCountered() {
    return this.countered;
  }

  /**
   * Sets whether or not this instance is countered.
   *
   * @param countered
   *          The countered.
   */
  public void setCountered(boolean countered) {
    this.countered = countered;
  }

  /**
   * Gets the id for this instance.
   *
   * @return The id.
   */
  public int getId() {
    return this.id;
  }

  /**
   * Sets the level for this instance.
   *
   * @param level
   *          The level.
   */
  public void setLevel(int level) {
    this.level = level;
  }

  /**
   * Gets the level for this instance.
   *
   * @return The level.
   */
  public int getLevel() {
    return this.level;
  }

  /**
   * Gets the label of the node
   *
   * @return The label.
   */
  public String getLabel() {
    return label;
  }
}
