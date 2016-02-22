package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;

import java.util.ArrayList;

/**
 * An interface for domains.
 *
 * @author Piot Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface SandRank<Type extends Ring> {
  public ArrayList<RankNode<Type>> or(ArrayList<ArrayList<RankNode<Type>>> children, int maxItems);

  /**
   * Conjuncive refinement of the node.
   *
   * @param children
   *          list of RanNodes
   * @param maxItem
   *          number of ranked attacks
   * @return list of best maxItems attacks
   */
  public ArrayList<RankNode<Type>> and(ArrayList<ArrayList<RankNode<Type>>> children, int maxItems);

  public ArrayList<RankNode<Type>> sand(ArrayList<ArrayList<RankNode<Type>>> children, int maxItems);
}
