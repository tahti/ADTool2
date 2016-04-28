package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.ADTNode;

import java.io.Serializable;

/**
 * An interface for domains.
 *
 * @author Piotr Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface AdtDomain<Type extends Ring> extends Domain<Type>, Serializable {
  /**
   * Checks if the value is allowed to be modified by the user.
   *
   * @param proponent
   *          if true we check the values for proponent and for opponent
   *          otherwise.
   * @return true if user can modify the value and false otherwise.
   */
  public boolean isValueModifiable(ADTNode node);
  public Type calc(Type a, Type b, ADTNode.Type type);
  /**
   * Counter of the proponent type of node.
   *
   * @param a
   *          left argument
   * @param b
   *          right argument
   * @return result of a counter.
   */
  public Type cp(Type a, Type b);

  /**
   * Counter of the opponent type of node.
   *
   * @param a
   *          left argument
   * @param b
   *          right argument
   * @return result of a counter.
   */
  public Type co(Type a, Type b);

}
