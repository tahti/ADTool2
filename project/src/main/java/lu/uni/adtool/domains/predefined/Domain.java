package lu.uni.adtool.domains.predefined;

import java.io.Serializable;

/**
 * An interface for domains. Obsolete - used just to import adt file.
 *
 * @author Piotr Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface Domain<Type> extends Serializable
{
  /**
   * Gets the default value with which to initialise the node.
   *
   * @param proponent true if the default value is for proponent
   * @return a default value.
   */
  Type getDefaultValue(boolean proponent);
  /**
   * Checks if the value is allowed to be modified by the user.
   *
   * @param proponent if true we check the values for proponent and for
   * opponent otherwise.
   * @return true if user can modify the value and false otherwise.
   */
  boolean isValueModifiable(boolean proponent);

  /**
   * Returns a name of a domain.
   *
   * @return a string containing a name.
   */
  String getName();

  /**
   * Returns a descripiton of a domain.
   *
   * @return a description.
   */
  String getDescription();

  /**
   * Disjunctive refinement of the proponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of disjunctive refinement.
   */
  Type op(Type a, Type b);

  /**
   * Disjunctive refinement of the oppponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of disjunctive refinement.
   */
  Type oo(Type a, Type b);

  /**
   * Conjunctive refinement of the proponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of conjunctive refinement.
   */
  Type ap(Type a, Type b);

  /**
   * Conjunctive refinement of the oppponent node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of conjunctive refinement.
   */
  Type ao(Type a, Type b);

  /**
   * Counter of the proponent type of node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of a counter.
   */
  Type cp(Type a, Type b);

  /**
   * Counter of the opponent type of node.
   *
   * @param a left argument
   * @param b right argument
   * @return result of a counter.
   */
  Type co(Type a, Type b);
}
