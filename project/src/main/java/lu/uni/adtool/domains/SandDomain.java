package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;

import java.io.Serializable;

/**
 * An interface for domains.
 *
 * @author Piotr Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface SandDomain<Type extends Ring> extends Domain<Type>, SandRank<Type>, Serializable {
  /**
   * Sequential refinement of the proponent node.
   *
   * @param a
   *          left argument
   * @param b
   *          right argument
   * @return result of sequential refinement.
   */
  public Type or(Type a, Type b);
  /**
   * Conjuncive refinement of the node.
   *
   * @param a
   *          left argument
   * @param b
   *          right argument
   * @return result of disjunctive refinement.
   */
  public Type and(Type a, Type b);

  /**
   * Disjunctive refinement of the node.
   *
   * @param a
   *          left argument
   * @param b
   *          right argument
   * @return result of disjunctive refinement.
   */
  public Type sand(Type a, Type b);
}
