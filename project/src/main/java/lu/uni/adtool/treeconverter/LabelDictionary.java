package lu.uni.adtool.treeconverter;

import java.util.Map;
import java.util.Hashtable;

/**
 * This provides a way of using small int values to represent String labels, as
 * opposed to storing the labels directly.
 *
 * @author Denilson Barbosa, Nikolaus Augsten from approxlib, available at
 *         http://www.inf.unibz.it/~augsten/src/ modified by Piotr Kordy
 */
public class LabelDictionary {

  /**
   * Creates a new blank dictionary.
   *
   * @throws Exception
   */
  public LabelDictionary() {
    count = 0;
    strInt = new Hashtable<String, Integer>();
    intStr = new Hashtable<Integer, String>();
  }

  /**
   * Adds a new label to the dictionary if it has not been added yet. Returns
   * the ID of the new label in the dictionary.
   *
   * @param label
   *          add this label to the dictionary if it does not exist yet
   * @return ID of label in the dictionary
   */
  public final int store(final String label) {
    if (strInt.containsKey(label)) {
      return strInt.get(label).intValue();
    }
    else {
      final Integer intKey = new Integer(count++);
      strInt.put(label, intKey);
      intStr.put(intKey, label);
      return intKey.intValue();
    }
  }

  /**
   * Returns the label with a given ID in the dictionary.
   *
   * @param labelID
   *          label ID
   * @return the label with the specified labelID, or null if this dictionary
   *         contains no label for labelID
   */
  public final String read(final int labelID) {
    return intStr.get(new Integer(labelID));
  }

  /**
   * A dummy label.
   */
  // public static final int KEY_DUMMY_LABEL = -1;
  private int                  count;
  private Map<String, Integer> strInt;
  private Map<Integer, String> intStr;
  // private boolean newLabelsAllowed = true;

}
