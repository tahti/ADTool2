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
package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Options;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Assignment map that associates labels of the nodes with domain valuations.
 * <T> is valuation type
 *
 * @author Piotr Kordy
 * @version
 */
public class ValueAssignement<T extends Ring> implements Serializable {

  /**
   * {@inheritDoc}
   *
   * @see Object#ValueAssignement()
   */
  public ValueAssignement() {
    proponent= new HashMap<String, T>(32);
    opponent= new HashMap<String, T>();
  }

  /**
   * {@inheritDoc}
   * @see Object#toString()
   */
  public String toString() {
    StringBuffer sbf = new StringBuffer();
    if(this.opponent.size() ==0) {
      for (Map.Entry<String, T> entry : this.proponent.entrySet()) {
        sbf.append(entry.getKey());
        sbf.append("\t");
        T value = entry.getValue();
        sbf.append(value.toString());
        sbf.append("\n");
      }
    }
    else {
      for (Map.Entry<String, T> entry : this.proponent.entrySet()) {
        sbf.append(Options.getMsg("tablemodel.proponent"));
        sbf.append("\t");
        sbf.append(entry.getKey());
        sbf.append("\t");
        T value = entry.getValue();
        sbf.append(value.toString());
        sbf.append("\n");
      }
      for (Map.Entry<String, T> entry : this.opponent.entrySet()) {
        sbf.append(Options.getMsg("tablemodel.opponent"));
        sbf.append("\t");
        sbf.append(entry.getKey());
        sbf.append("\t");
        T value = entry.getValue();
        sbf.append(value.toString());
        sbf.append("\n");
      }
    }
    return sbf.toString();
  }

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
