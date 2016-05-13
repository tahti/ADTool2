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
package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

import java.util.ArrayList;
import java.util.HashMap;

public class RankExporter implements NodeRanker {
  public RankExporter(Node root, ValueAssignement<Ring> valuesMap, Ranker<Ring> ranker, int maxItems) {
    ArrayList<RankNode<Ring>> result;
    if (root instanceof SandNode) {
      result = ranker.rank((SandNode)root, valuesMap, maxItems);
    }
    else {
      result = ranker.rank((ADTNode)root, valuesMap, maxItems);
    }
    this.ranking = new ArrayList<HashMap<Node, Ring>>();
    for (int i = 0; i < result.size(); i++) {
      this.ranking.add(new HashMap<Node, Ring>());
      ArrayList<Integer> choices = new ArrayList<Integer>(result.get(i).getList());
      ranker.markRecursive(root, this, valuesMap, choices);
    }
  }

  public void rankNode(Node node, Ring value) {
    Debug.log("Name:"+node.getName() + " val:"+value.toString());
    this.ranking.get(this.ranking.size() - 1).put(node, value);
  }

  public Ring getValue(Node node, int rank) {
    if (this.ranking != null && rank < this.ranking.size()) {
      return this.ranking.get(rank).get(node);
    }
    else {
      return null;
    }
  }

  private ArrayList<HashMap<Node, Ring>> ranking;
}

