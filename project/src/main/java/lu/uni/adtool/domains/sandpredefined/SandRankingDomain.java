package lu.uni.adtool.domains.sandpredefined;

import java.util.ArrayList;

import lu.uni.adtool.domains.RankNode;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.SandNode;

public abstract class SandRankingDomain<Type extends Ring> implements SandDomain<Type> {

  public SandRankingDomain() {
  }

  public abstract boolean isOrType(SandNode.Type operation);

  public ArrayList<RankNode<Type>> minOp(ArrayList<ArrayList<RankNode<Type>>> children,
      int maxItems, SandNode.Type type) {
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
      int maxItems, SandNode.Type type) {
    ArrayList<RankNode<Type>> result = children.get(0);
    for (int i = 1; i < children.size(); i++) {
      result = this.conjuctiveBinary(result, children.get(i), maxItems, type);
    }
    return result;

  }

  private ArrayList<RankNode<Type>> conjuctiveBinary(ArrayList<RankNode<Type>> a,
      ArrayList<RankNode<Type>> b, int maxItems, SandNode.Type type) {
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

  private static final long serialVersionUID = 2340648509343831740L;

}
