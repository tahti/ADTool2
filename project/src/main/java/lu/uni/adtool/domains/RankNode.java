package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;

import java.util.ArrayList;

public class RankNode<Type extends Ring> {

  public RankNode(RankNode<Type> node, int newIndex) {
    this.value = node.value;
    this.list = new ArrayList<Integer>(node.getList());
    this.list.add(newIndex);
  }

  public RankNode(Type value) {
    this.value = value;
    this.list = new ArrayList<Integer>();
  }

  public RankNode(Type value, RankNode left, RankNode right) {
      this.value = value;
      this.list = new ArrayList<Integer>();
      //add right first as we will be taking numbers from the end
      this.list.addAll(right.getList());
      this.list.addAll(left.getList());
    }

  public ArrayList<Integer> getList() {
    return list;
  }

  public Type                value;
  private ArrayList<Integer> list;
}
