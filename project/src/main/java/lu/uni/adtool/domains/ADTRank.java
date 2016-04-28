package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;

import java.util.ArrayList;

/**
 * An interface for domains.
 *
 * @author Piotr Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface ADTRank<Type extends Ring> {
  public ArrayList<RankNode<Type>> op(ArrayList<RankNode<Type>> a,ArrayList<RankNode<Type>> b);
  public ArrayList<RankNode<Type>> oo(ArrayList<RankNode<Type>> a,ArrayList<RankNode<Type>> b);
  public ArrayList<RankNode<Type>> ap(ArrayList<RankNode<Type>> a,ArrayList<RankNode<Type>> b);
  public ArrayList<RankNode<Type>> ao(ArrayList<RankNode<Type>> a,ArrayList<RankNode<Type>> b);
  public ArrayList<RankNode<Type>> cp(ArrayList<RankNode<Type>> a,ArrayList<RankNode<Type>> b);
  public ArrayList<RankNode<Type>> co(ArrayList<RankNode<Type>> a,ArrayList<RankNode<Type>> b);
}
