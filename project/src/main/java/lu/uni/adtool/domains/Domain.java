package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.Node;

import java.io.Serializable;

/**
 * An interface for domains.
 *
 * @author Piot Kordy
 * @version
 * @param <Type>
 *          type of data
 */
public interface Domain<Type extends Ring> extends Serializable {
  /**
   * Gets the default value with which to initialise the node.
   *
   * @param proponent
   *          true if the default value is for proponent
   * @return a default value.
   */
  public Type getDefaultValue(Node node);


  /**
   * Returns a name of a domain.
   *
   * @return a string containing a name.
   */
  public String getName();

  /**
   * Returns a descripiton of a domain.
   *
   * @return a description.
   */
  public String getDescription();

}
