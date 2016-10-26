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
package lu.uni.adtool.domains.adtpredefined;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.RankNode;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.ADTNode;

import java.util.ArrayList;

/**
 * A helper class to do the ranking for ADTDomains
 *
 */

public abstract class AdtRankingDomain<Type extends Ring> implements AdtDomain<Type> {

  public AdtRankingDomain() {
  }

  public abstract boolean isOrType(ADTNode.Type operation);

  public ArrayList<RankNode<Type>> minOp(ArrayList<ArrayList<RankNode<Type>>> children,
      int maxItems, ADTNode.Type type) {
    ArrayList<RankNode<Type>> result = new ArrayList<RankNode<Type>>();
    result.ensureCapacity(maxItems);
    int[] indices = new int[children.size()];
    for (int i = 0; i < children.size(); i++) {
      indices[i] = 0;
    }
    int ir = 0;
    while (ir < maxItems) {
      int i = 0;
      while (i < children.size() && indices[i] >= children.get(i).size()) {
        i++;
      }
      if (i >= children.size()) {
        break;
      }
      Type minValue = (Type) children.get(i).get(indices[i]).value;
      int index = i;

      for (i = 1; i < children.size(); i++) {
        if (indices[i] < children.get(i).size()) {
          Type newMinValue = this.calc(children.get(i).get(indices[i]).value, minValue, type);
          if (newMinValue.compareTo(minValue) != 0) {
            index = i;
            minValue = newMinValue;
          }
        }
      }
      result.add(new RankNode<Type>(children.get(index).get(indices[index]), index));
      indices[index]++;
      ir++;
    }
    return result;
  }

  public ArrayList<RankNode<Type>> conjunctiveOp(ArrayList<ArrayList<RankNode<Type>>> children,
      int maxItems, ADTNode.Type type) {
    ArrayList<RankNode<Type>> result = children.get(0);
    for (int i = 1; i < children.size(); i++) {
      result = this.conjuctiveBinary(result, children.get(i), maxItems, type);
    }
    return result;

  }

  private ArrayList<RankNode<Type>> conjuctiveBinary(ArrayList<RankNode<Type>> a,
      ArrayList<RankNode<Type>> b, int maxItems, ADTNode.Type type) {
    ArrayList<RankNode<Type>> result = new ArrayList<RankNode<Type>>();
    result.ensureCapacity(maxItems);
    int ia = 0;
    int ib = 0;
    int ir = 0;
    while (ir < maxItems && ia < a.size() && ib < b.size()) {
      result.add(new RankNode<Type>(this.calc(a.get(ia).value, b.get(ib).value, type), a.get(ia),
          b.get(ib)));
      ir++;
      if ((ia + 1) >= a.size() && (ib + 1) >= b.size()) break;
      if (((ib + 1) >= b.size())
          || (((ia + 1) < a.size()) && (a.get(ia + 1).value.compareTo(b.get(ib + 1).value) < 0))) {
        ia++;
        for (int i = 0; i < ib && ir < maxItems; i++) {
          result.add(new RankNode<Type>(this.calc(a.get(ia).value, b.get(i).value, type), a.get(ia),
              b.get(i)));
          ir++;
        }
      }
      else {
        ib++;
        for (int i = 0; i < ia && ir < maxItems; i++) {
          result.add(new RankNode<Type>(this.calc(a.get(i).value, b.get(ib).value, type), a.get(i),
              b.get(ib)));
          ir++;
        }
      }
    }
    return result;
  }

  private static final long serialVersionUID = 363858497457035591L;
}
