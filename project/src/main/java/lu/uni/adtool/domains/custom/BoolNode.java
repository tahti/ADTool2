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
package lu.uni.adtool.domains.custom;

import lu.uni.adtool.domains.rings.Bool;

public class BoolNode {
  public enum Operation {
    OR, NOR, AND, NAND, XOR, EQ, NEQ, IMP, CIMP/*converse implication*/, NEG, TRUE, FALSE, Y, X
  }

  public BoolNode () {
    this.left = null;
    this.right = null;
  }

  public boolean evaluate(Bool x, Bool y) {
    BoolNode.x = x.getValue();
    BoolNode.y = y.getValue();
    return evaluate();
  }

  public int getPriority () {
    switch (this.op) {
    case OR:
    case NOR:
    case AND:
    case NAND:
    case XOR:
      return 1;
    case EQ:
    case NEQ:
    case IMP:
    case CIMP:
    default:
      return 0;
    case NEG:
      return 2;
    case TRUE:
    case FALSE:
    case X:
    case Y:
      return 3;
    }
  }

  public String toUnicode() {
    String l = "";
    String r = "";
    switch (this.op) {
    case X:
      return "x";
    case Y:
      return "y";
    case TRUE:
      return "True";
    case FALSE:
      return "False";
    case NEG:
      if (getPriority() < left.getPriority()) {
        return "\u00AC" + left.toUnicode();
      }
      else {
        return "\u00AC(" + left.toUnicode()+")";
      }
    default:
    l = left.toUnicode();
      if (getPriority() > left.getPriority()) {
        l = "("+ l + ")";
      }
      r = right.toUnicode();
      if (getPriority() > right.getPriority()) {
        r = "("+ r + ")";
      }
      break;
    }
    switch (this.op) {
    case OR:
      return l + " \u2228 " + r;
    case NOR:
      return l + " \u22BC " + r;
    case AND:
      return l + " \u2227 " + r;
    case NAND:
      return l + " \u22BC " + r;
    case XOR:
      return l + " \u2295 " + r;
    case EQ:
      return l + " \u21D4 " + r;
    case NEQ:
      return l + " \u21CE " + r;
    case IMP:
      return l + " \u21D2 " + r;
    case CIMP:
      return l + " \u21D0 " + r;
    default:
      return "";
    }
  }

  private boolean evaluate() {
    switch (this.op) {
    case OR:
      return left.evaluate() || right.evaluate();
    case NOR:
      return !(left.evaluate() || right.evaluate());
    case AND:
      return left.evaluate() && right.evaluate();
    case NAND:
      return !(left.evaluate() && right.evaluate());
    case XOR:
      return left.evaluate() ^ right.evaluate();
    case EQ:
      return left.evaluate() == right.evaluate();
    case NEQ:
      return left.evaluate() != right.evaluate();
    case IMP:
      return !left.evaluate() || right.evaluate();
    case CIMP:
      return left.evaluate() || !right.evaluate();
    case NEG:
      return !this.left.evaluate();
    case TRUE:
      return true;
    case FALSE:
      return false;
    case X:
      return BoolNode.x;
    case Y:
      return BoolNode.y;
    }
    return false;
  }

  private static boolean x;
  private static boolean y;
  private BoolNode left;
  private BoolNode right;
  private Operation op;
}
