package lu.uni.adtool.domains;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Assignment map that associates labels of the nodes with domain valuations.
 * <T> is valuation type
 *
 * @author Piot Kordy
 * @version
 */
public class ValueAssignement<T> implements Serializable {

  /**
   * {@inheritDoc}
   *
   * @see Object#ValueAssignement()
   */
  public ValueAssignement() {
    map = new HashMap<String, T>(32);
  }

  public String toString() {
    return map.values().toString();
  }

  /**
   * Assigns value to a label.
   *
   * @param value
   * @param label
   */
  public void put(final String label, final T value) {
    map.put(label.replaceAll("\\s+", " "), value);
  }

  /**
   * Remove the label from the map.
   *
   * @param label
   *          label to be removed.
   */
  public final void remove(final String label) {
    map.remove(label.replaceAll("\\s+", " "));
  }

  /**
   * Get the value for the given key.
   *
   * @param label
   *          label of the key
   * @return key
   */
  public final T get(final String label) {
    return map.get(label.replaceAll("\\s+", " "));
  }

  public Set<String> keySet() {
    return map.keySet();
  }
  public void clear() {
    map.clear();
  }
  static final long          serialVersionUID = 975147854358646403L;
  /**
   * Hash map for internal storage mapping labels to domain values
   */
  private HashMap<String, T> map;
}
