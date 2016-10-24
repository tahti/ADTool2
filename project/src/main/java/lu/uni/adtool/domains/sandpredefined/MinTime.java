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
package lu.uni.adtool.domains.sandpredefined;

import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.RankNode;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.SandRank;
import lu.uni.adtool.domains.adtpredefined.DescriptionGenerator;
import lu.uni.adtool.domains.rings.RealG0;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

import java.util.ArrayList;

public class MinTime implements SandDomain<RealG0>, SandRank<RealG0>{

  public MinTime() {
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getDefaultValue()
   */
  public final RealG0 getDefaultValue(Node node) {
    return new RealG0(Double.POSITIVE_INFINITY);
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getName()
   */
  public String getName() {
    return Options.getMsg("sanddomain.mintime.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see Domain#getDescription()
   */
  public String getDescription() {
    final String name = Options.getMsg("sanddomain.mintime.description");
    final String vd = "&#x211D;\u208A\u222A{\u221E}";
    final String[] operators =
        {"min(<i>x</i>,<i>y</i>)", "max(<i>x</i>,<i>y</i>)", "<i>x</i>+<i>y</i>"};
    return DescriptionGenerator.generateDescription(this, name, vd, operators);
  }
  public RealG0 calc(RealG0 a, RealG0 b, SandNode.Type type) {
    switch (type) {
    case AND:
      return RealG0.max(a, b);
    case SAND:
      return RealG0.sum(a, b);
    case OR:
      return RealG0.min(a, b);
    default:
      return RealG0.min(a, b);
    }
  }

  @Override
  public ArrayList<RankNode<RealG0>> and(ArrayList<ArrayList<RankNode<RealG0>>> children,
      int maxItems) {
    ArrayList<RankNode<RealG0>> result = children.get(0);
    for (int i = 1; i < children.size(); i++) {
      result = this.and(result, children.get(i), maxItems);
    }
    return result;
  }

  @Override
  public ArrayList<RankNode<RealG0>> sand(ArrayList<ArrayList<RankNode<RealG0>>> children,
      int maxItems) {
    ArrayList<RankNode<RealG0>> result = children.get(0);
    for (int i = 1; i < children.size(); i++) {
      result = this.sand(result, children.get(i), maxItems);
    }
    return result;
  }

  @Override
  public ArrayList<RankNode<RealG0>> or(ArrayList<ArrayList<RankNode<RealG0>>> children,
      int maxItems) {
    ArrayList<RankNode<RealG0>> result = new ArrayList<RankNode<RealG0>>();
    result.ensureCapacity(maxItems);
    int[] indices = new int[children.size()];
    for (int i = 0; i < children.size(); i++) {
      indices[i] = 0;
    }
    int ir = 0;
    Debug.log("OR - num Children:" + children.size());
    while (ir < maxItems) {
      int i = 0;
      while (i < children.size() && indices[i] >= children.get(i).size()) {
        i++;
      }
      if (i >= children.size()) {
        Debug.log("no more children to be aded:" + children.size());
        break;
      }
      RealG0 minValue = (RealG0) children.get(i).get(indices[i]).value;
      int index = i;
      Debug.log("Initial min value:" + minValue.toString() + " index:" + i);

      for (i = 1; i < children.size(); i++) {
        if ((indices[i] < children.get(i).size())
            && (minValue.compareTo(children.get(i).get(indices[i]).value) > 0)) {
          Debug.log("min:" + minValue.toString() + " compare:"
              + minValue.compareTo(children.get(i).get(indices[i]).value) + " index value:"
              + (children.get(i).get(indices[i]).value));
          index = i;
          minValue = children.get(i).get(indices[i]).value;
        }
      }
      Debug.log("Final added min value:" + minValue.toString() + " index:" + i + " ir" + (ir + 1));
      result.add(new RankNode<RealG0>(children.get(index).get(indices[index]), index));
      indices[index]++;
      ir++;
    }
    return result;
  }

  private ArrayList<RankNode<RealG0>> and(ArrayList<RankNode<RealG0>> left,
      ArrayList<RankNode<RealG0>> right, int maxItems) {
    ArrayList<RankNode<RealG0>> result = new ArrayList<RankNode<RealG0>>();
    result.ensureCapacity(maxItems);
    int iLeft = 0;
    int iRight = 0;
    int iResult = 0;
    while (iResult < maxItems && iLeft < left.size() && iRight < right.size()) {
      result.add(
                 new RankNode<RealG0>(this.calc(left.get(iLeft).value, right.get(iRight).value, SandNode.Type.AND), left.get(iLeft), right.get(iRight)));
      iResult++;
      if ((iLeft + 1) >= left.size() && (iRight + 1) >= right.size()) break;
      if (((iRight + 1) >= right.size()) || (((iLeft + 1) < left.size()) && (left.get(iLeft + 1).value.compareTo(right.get(iRight + 1).value) < 0))) {
        iLeft++;
        for (int i = 0; i< iRight && iResult < maxItems; i++) {
          result.add(
                     new RankNode<RealG0>(this.calc(left.get(iLeft).value, right.get(i).value, SandNode.Type.AND), left.get(iLeft), right.get(i)));
          iResult++;
        }
      }
      else {
        iRight++;
        for (int i = 0; i< iLeft && iResult < maxItems; i++) {
          result.add(
                     new RankNode<RealG0>(this.calc(left.get(i).value, right.get(iRight).value, SandNode.Type.AND), left.get(i), right.get(iRight)));
          iResult++;
        }
      }
    }
    return result;
  }

  private ArrayList<RankNode<RealG0>> sand(ArrayList<RankNode<RealG0>> a,
      ArrayList<RankNode<RealG0>> b, int maxItems) {
    ArrayList<RankNode<RealG0>> result = new ArrayList<RankNode<RealG0>>();
    result.ensureCapacity(maxItems);
    int ia = 0;
    int ib = 0;
    int ir = 0;
    while (ir < maxItems && ia < a.size() && ib < b.size()) {
      result.add(
                 new RankNode<RealG0>(this.calc(a.get(ia).value, b.get(ib).value, SandNode.Type.SAND), a.get(ia), b.get(ib)));
      ir++;
      if ((ia + 1) >= a.size() && (ib + 1) >= b.size()) break;
      if (((ib + 1) >= b.size()) || (((ia + 1) < a.size()) && (a.get(ia + 1).value.compareTo(b.get(ib + 1).value) < 0))) {
        ia++;
        for (int i = 0; i< ib && ir < maxItems; i++) {
          result.add(
              new RankNode<RealG0>(this.calc(a.get(ia).value, b.get(i).value, SandNode.Type.SAND), a.get(ia), b.get(i)));
          ir++;
        }
      }
      else {
        ib++;
        for (int i = 0; i< ia && ir < maxItems; i++) {
          result.add(
              new RankNode<RealG0>(this.calc(a.get(i).value, b.get(ib).value, SandNode.Type.SAND), a.get(i), b.get(ib)));
          ir++;
        }
      }
    }
    return result;
  }

  private static final long serialVersionUID = -3603484192500901319L;

}
