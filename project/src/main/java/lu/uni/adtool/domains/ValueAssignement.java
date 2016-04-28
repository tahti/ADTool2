package lu.uni.adtool.domains;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Assignment map that associates labels of the nodes with domain valuations.
 * <T> is valuation type
 *
 * @author Piotr Kordy
 * @version
 */
public class ValueAssignement<T> implements Serializable {

  /**
   * {@inheritDoc}
   *
   * @see Object#ValueAssignement()
   */
  public ValueAssignement() {
    proponent= new HashMap<String, T>(32);
    opponent= new HashMap<String, T>();
  }

//   public String toString() {
//     return map.values().toString();
//   }

  /**
   * Assigns value to a label.
   *
   * @param value
   * @param label
   */
  public void put(boolean prop, final String label, final T value) {
    if (prop) {
      proponent.put(label.replaceAll("\\s+", " "), value);
    }
    else {
      opponent.put(label.replaceAll("\\s+", " "), value);
    }
  }

  /**
   * Remove the label from the map.
   *
   * @param label
   *          label to be removed.
   */
  public final void remove(boolean prop, final String label) {
    if (prop) {
      proponent.remove(label.replaceAll("\\s+", " "));
    }
    else {
      opponent.remove(label.replaceAll("\\s+", " "));
    }
  }

  /**
   * Get the value for the given key.
   *
   * @param label
   *          label of the key
   * @return key
   */
  public final T get(boolean prop, final String label) {
    if (prop) {
      return proponent.get(label.replaceAll("\\s+", " "));
    }
    else {
      return opponent.get(label.replaceAll("\\s+", " "));
    }
  }

  public Set<String> keySet(boolean prop) {
    if (prop) {
      return proponent.keySet();
    }
    else {
      return opponent.keySet();
    }
  }
  public void clear() {
    proponent.clear();
    opponent.clear();
  }
  static final long          serialVersionUID = 975147854358646403L;
  /**
   * Hash map for internal storage mapping labels to domain values
   */
  private HashMap<String, T> proponent;
  private HashMap<String, T> opponent;
}
