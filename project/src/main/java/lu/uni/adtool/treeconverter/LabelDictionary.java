/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>)
 * Date:   10/12/2015
 * Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty of Science,
 *     Technology and Communication FSTC
 * All rights reserved.
 * Licensed under GNU Affero General Public License 3.0;
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
