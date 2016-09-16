/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>) Date:
 * 10/12/2015 Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty
 * of Science, Technology and Communication FSTC All rights reserved. Licensed
 * under GNU Affero General Public License 3.0; This program is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.domains;

import lu.uni.adtool.domains.custom.AdtCustomDomain;
import lu.uni.adtool.domains.custom.SandCustomDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.util.xml.XElement;

public class ValuationDomain implements MultipleCDockableLayout {

  public ValuationDomain() {
    this.domainId = -1;
    this.domain = null;
    this.evaluator = null;
    this.valuesMap = new ValueAssignement<Ring>();
    this.showAllLabels = true;
  }

  public ValuationDomain(int treeId, int domainId, final AdtDomain<Ring> domain) {
    this.domainId = domainId;
    this.treeId = treeId;
    this.domain = domain;
    this.evaluator = new Evaluator<Ring>(domain);
    this.valuesMap = new ValueAssignement<Ring>();
    this.showAllLabels = true;
  }

  public ValuationDomain(int treeId, int domainId, final SandDomain<Ring> domain) {
    this.domainId = domainId;
    this.treeId = treeId;
    this.domain = domain;
    this.evaluator = new Evaluator<Ring>(domain);
    this.valuesMap = new ValueAssignement<Ring>();
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    return false;
  }

  public void readStream(DataInputStream in) throws IOException {
    String domainName = in.readUTF();
    Node node = null;
    domain = DomainFactory.createFromString(domainName);
    if (domain == null) {
      throw new IOException("No domain with name:" + domainName);
    }
    if (DomainFactory.isSandDomain(domainName)) {
      node = new SandNode();
    }
    else {
      node = new ADTNode();
    }
    this.treeId = in.readInt();
    this.domainId = in.readInt();
    Debug
        .log("Read domain " + domainName + " treeId:" + this.treeId + " domainId:" + this.domainId);
    int counter = in.readInt();
    this.valuesMap.clear();
    for (int i = 0; i < counter; i++) {
      String name = in.readUTF();
      String value = in.readUTF();
      Ring v = domain.getDefaultValue(node);
      v.updateFromString(value);
      this.setValue(true, name, v);
    }
    if (domain instanceof AdtDomain) {
      counter = in.readInt();
      for (int i = 0; i < counter; i++) {
        String name = in.readUTF();
        String value = in.readUTF();
        Ring v = domain.getDefaultValue(node);
        v.updateFromString(value);
        this.setValue(false, name, v);
      }
      this.evaluator = new Evaluator<Ring>((AdtDomain<Ring>) domain);
    }
    else {
      this.evaluator = new Evaluator<Ring>((SandDomain<Ring>) domain);
    }
  }

  public void readXML(XElement element) {
    String domainName = element.getElement("domain").getString();
    Node node = null;
    domain = DomainFactory.createFromString(domainName);
    if (DomainFactory.isSandDomain(domainName)) {
      node = new SandNode();
    }
    else {
      node = new ADTNode();
    }
    this.treeId = element.getElement("treeId").getInt();
    this.domainId = element.getElement("domainId").getInt();
    Debug.log(" layout with treeId " + treeId + " domainId:" + domainId);
    if (domain instanceof SandCustomDomain) {
      SandCustomDomain custom = (SandCustomDomain) domain;
      custom.setName(element.getElement("name").getString());
      custom.setDescription(element.getElement("description").getString());
      custom.setOr(element.getElement("or").getString());
      custom.setAnd(element.getElement("and").getString());
      custom.setSand(element.getElement("sand").getString());
      custom.setDefault(element.getElement("defaultValue").getString());
    }
    else if (domain instanceof AdtCustomDomain) {
      AdtCustomDomain custom = (AdtCustomDomain) domain;
      custom.setName(element.getElement("name").getString());
      custom.setDescription(element.getElement("description").getString());
      custom.setOp(element.getElement("op").getString());
      custom.setOo(element.getElement("oo").getString());
      custom.setAp(element.getElement("ap").getString());
      custom.setAo(element.getElement("ao").getString());
      custom.setCp(element.getElement("cp").getString());
      custom.setCo(element.getElement("co").getString());
      custom.setOppDefault(element.getElement("opponentDefault").getString());
      custom.setProDefault(element.getElement("proponentDefault").getString());
      custom.setProModifiable(
          !element.getElement("proModifiable").getString().toUpperCase().equals("FALSE"));
      custom.setOppModifiable(
          !element.getElement("oppModifiable").getString().toUpperCase().equals("FALSE"));
    }
    XElement v = element.getElement("values");
    if (node != null && v != null) {
      this.valuesMap.clear();
      int size = v.getElementCount();
      for (int i = 0; i < size; i = i + 2) {
        String key = v.getElement(i).getString();
        String value = v.getElement(i + 1).getString();
        Ring r = domain.getDefaultValue(node);
        r.updateFromString(value);
        this.setValue(true, key, r);
      }
    }
    if (domain instanceof AdtDomain) {
      int size = v.getElementCount();
      for (int i = 0; i < size; i = i + 2) {
        String key = v.getElement(i).getString();
        String value = v.getElement(i + 1).getString();
        Ring r = domain.getDefaultValue(node);
        r.updateFromString(value);
        this.setValue(false, key, r);
      }
      this.evaluator = new Evaluator<Ring>((AdtDomain<Ring>) domain);
    }
    else {
      this.evaluator = new Evaluator<Ring>((SandDomain<Ring>) domain);
    }
    // fileName = element.getElement( "name" ).getString();
    // fileContent = element.getElement( "content" ).getString();
    // background = new Color( element.getElement( "background" ).getInt() );
  }

  public void writeStream(DataOutputStream out) throws IOException {
    out.writeUTF(this.domain.getClass().getSimpleName());
    out.writeInt(this.treeId);
    out.writeInt(this.domainId);
    Set<String> keys = valuesMap.keySet(true);
    out.writeInt(keys.size());
    for (String key : keys) {
      out.writeUTF(key);
      out.writeUTF(valuesMap.get(true, key).toString());
    }
    if (domain instanceof AdtDomain) {
      keys = valuesMap.keySet(false);
      out.writeInt(keys.size());
      for (String key : keys) {
        out.writeUTF(key);
        out.writeUTF(valuesMap.get(false, key).toString());
      }
    }
  }

  public void exportXML(XElement element) {
    XElement dElement = element.addElement("domain");
    dElement.addString("id", getExportXmlId());
    dElement.addElement("class").setString(domain.getClass().getName());
    dElement.addElement("tool").setString("ADTool2");
    if (domain instanceof SandCustomDomain) {
      SandCustomDomain custom = (SandCustomDomain) domain;
      dElement.addElement("name").setString(custom.getName());
      dElement.addElement("description").setString(custom.getShortDescription());
      dElement.addElement("or").setString(custom.getOr());
      dElement.addElement("and").setString(custom.getAnd());
      dElement.addElement("sand").setString(custom.getSand());
      dElement.addElement("defaultValue").setString(custom.getDefault());
    }
    else if (domain instanceof AdtCustomDomain) {
      AdtCustomDomain custom = (AdtCustomDomain) domain;
      dElement.addElement("name").setString(custom.getName());
      dElement.addElement("description").setString(custom.getShortDescription());
      dElement.addElement("op").setString(custom.getOp());
      dElement.addElement("oo").setString(custom.getOo());
      dElement.addElement("ap").setString(custom.getAp());
      dElement.addElement("ao").setString(custom.getAo());
      dElement.addElement("cp").setString(custom.getCp());
      dElement.addElement("co").setString(custom.getCo());
      dElement.addElement("opponentDefault").setString(custom.getOppDefault());
      dElement.addElement("proponentDefault").setString(custom.getProDefault());
      if (custom.isOppModifiable()) {
        dElement.addElement("oppModifiable").setString("true");
      }
      else {
        dElement.addElement("oppModifiable").setString("false");
      }
      if (custom.isProModifiable()) {
        dElement.addElement("proModifiable").setString("true");
      }
      else {
        dElement.addElement("proModifiable").setString("false");
      }
    }
    else if (domain instanceof Parametrized) {
      dElement.addElement("range").setString(((Parametrized) domain).getParameter().toString());
    }

  }

  public void writeXML(XElement element) {
    String domainName = domain.getClass().getSimpleName();
    element.addElement("domain").setString(domainName);
    element.addElement("treeId").setInt(this.treeId);
    element.addElement("domainId").setInt(this.domainId);
    if (domain instanceof SandCustomDomain) {
      SandCustomDomain custom = (SandCustomDomain) domain;
      element.addElement("name").setString(custom.getName());
      element.addElement("description").setString(custom.getShortDescription());
      element.addElement("or").setString(custom.getOr());
      element.addElement("and").setString(custom.getAnd());
      element.addElement("sand").setString(custom.getSand());
      element.addElement("defaultValue").setString(custom.getDefault());
    }
    else if (domain instanceof AdtCustomDomain) {
      AdtCustomDomain custom = (AdtCustomDomain) domain;
      element.addElement("name").setString(custom.getName());
      element.addElement("description").setString(custom.getShortDescription());
      element.addElement("op").setString(custom.getOp());
      element.addElement("oo").setString(custom.getOo());
      element.addElement("ap").setString(custom.getAp());
      element.addElement("ao").setString(custom.getAo());
      element.addElement("cp").setString(custom.getCp());
      element.addElement("co").setString(custom.getCo());
      element.addElement("opponentDefault").setString(custom.getOppDefault());
      element.addElement("proponentDefault").setString(custom.getProDefault());
      if (custom.isOppModifiable()) {
        element.addElement("oppModifiable").setString("true");
      }
      else {
        element.addElement("oppModifiable").setString("false");
      }
      if (custom.isProModifiable()) {
        element.addElement("proModifiable").setString("true");
      }
      else {
        element.addElement("proModifiable").setString("false");
      }
    }
    XElement v = new XElement("values");
    Set<String> keys = valuesMap.keySet(true);
    for (String key : keys) {
      v.addElement("label").setString(key);
      v.addElement("value").setString(valuesMap.get(true, key).toString());
    }
    element.addElement(v);
    if (domain instanceof AdtDomain) {
      v = new XElement("valuesOpp");
      keys = valuesMap.keySet(true);
      for (String key : keys) {
        v.addElement("label").setString(key);
        v.addElement("value").setString(valuesMap.get(true, key).toString());
      }
      element.addElement(v);
    }
  }

  public Ring getValue(ADTNode node) {
    return valuesMap.get(node.getRole() == ADTNode.Role.PROPONENT, node.getName());
  }

  public Ring getValue(SandNode node) {
    return valuesMap.get(true, node.getName());
  }

  public Ring get(boolean proponent, String key) {
    return valuesMap.get(proponent, key);
  }

  public ArrayList<String> sandKeySet() {
    ArrayList<String> r = new ArrayList<String>(valuesMap.keySet(true));
    java.util.Collections.sort(r);
    return r;
  }

  public ArrayList<String> oppKeySet() {
    ArrayList<String> r = new ArrayList<String>(valuesMap.keySet(false));
    java.util.Collections.sort(r);
    return r;
  }

  public final Ring getTermValue(final ADTNode node) {
    Ring value = (Ring) evaluator.getValue(node);
    if (value == null) {
      return (Ring) evaluator.reevaluate(node, valuesMap);
    }
    else {
      return value;
    }
  }

  public final Ring getTermValue(final SandNode node) {
    Ring value = (Ring) evaluator.getValue(node);
    if (value == null) {
      return (Ring) evaluator.reevaluate(node, valuesMap);
    }
    else {
      return value;
    }
  }

  public void setValue(boolean proponent, String key, Ring value) {
    valuesMap.put(proponent, key, value);
  }

  public void setValue(Node node, Ring value) {
    Ring v = getDomain().getDefaultValue(node);
    if (v.updateFromString(value.toString())) {
      if (node instanceof ADTNode) {
        valuesMap.put(((ADTNode) node).getRole() == ADTNode.Role.PROPONENT, node.getName(), v);
      }
      else {
        valuesMap.put(true, node.getName(), value);
      }
    }
  }

  public void setDefaultValue(ADTNode node) {
    Ring value = getDomain().getDefaultValue(node);
    valuesMap.put(node.getRole() == ADTNode.Role.PROPONENT, node.getName(), value);
  }

  public void setDefaultValue(SandNode node) {
    Ring value = getDomain().getDefaultValue(node);
    valuesMap.put(true, node.getName(), value);
  }

  public boolean hasEvaluator() {
    return evaluator != null;
  }

  public void rename(Node node, String name) {
    Ring value = null;
    Ring toOverwriteValue = null;
    if (node instanceof SandNode) {
      value = valuesMap.get(true, node.getName());
      toOverwriteValue = valuesMap.get(true, name);
      if (value == null) {
        value = getDomain().getDefaultValue(node);
      }
      if (toOverwriteValue == null || toOverwriteValue == getDomain().getDefaultValue(node)) {
        valuesMap.put(true, name, value);
      }
    }
    else {
      value =
          this.valuesMap.get(((ADTNode) node).getRole() == ADTNode.Role.PROPONENT, node.getName());
      toOverwriteValue =
          this.valuesMap.get(((ADTNode) node).getRole() == ADTNode.Role.PROPONENT, name);
      if (value == null) {
        value = getDomain().getDefaultValue(node);
      }
      if (toOverwriteValue == null || toOverwriteValue == getDomain().getDefaultValue(node)) {
        valuesMap.put(((ADTNode) node).getRole() == ADTNode.Role.PROPONENT, name, value);
      }
    }

  }

  public void refreshAllValues(ADTNode root) {
    ValueAssignement<Ring> vm = new ValueAssignement<Ring>();
    this.refreshAllValues(root, vm);
    this.valuesMap = vm;
  }

  public void refreshAllValues(SandNode root) {
    ValueAssignement<Ring> vm = new ValueAssignement<Ring>();
    this.refreshAllValues(root, vm);
    this.valuesMap = vm;
  }

  public void valuesUpdated(ADTNode root) {
    evaluator.reevaluate(root, valuesMap);
  }

  public void valuesUpdated(SandNode root) {
    evaluator.reevaluate(root, this.valuesMap);
  }

  public void treeChanged(ADTNode root) {
    Debug.log("reevaluating");
    this.evaluator.reevaluate(root, this.valuesMap);
    this.refreshAllValues(root);
  }

  public void treeChanged(SandNode root) {
    this.evaluator.reevaluate(root, this.valuesMap);
    this.refreshAllValues(root);
  }

  /**
   * Gets the domain for this instance.
   *
   * @return The domain.
   */
  public lu.uni.adtool.domains.Domain<Ring> getDomain() {
    return domain;
  }

  /**
   * Sets the valueAssPro for this instance.
   *
   * @param newValueAssPro
   *          new value assignement.
   */
  public void setValueMap(ValueAssignement<Ring> newValueAss, ADTNode root) {
    this.valuesMap = newValueAss;
    evaluator.reevaluate(root, this.valuesMap);
  }

  /**
   * Gets the valueAssPro for this instance.
   *
   * @return The valueAssPro.
   */
  public ValueAssignement<Ring> getValueMap() {
    return this.valuesMap;
  }

  /**
   * @return the showAllLabels
   */
  public boolean isShowAllLabels() {
    return showAllLabels;
  }

  /**
   * @param showAllLabels
   *          the showAllLabels to set
   */
  public void setShowAllLabels(boolean showAllLabels) {
    this.showAllLabels = showAllLabels;
  }

  public int getDomainId() {
    return this.domainId;
  }

  public int getTreeId() {
    return this.treeId;
  }

  public void setTreeId(int treeId) {
    this.treeId = treeId;
  }

  public String getExportXmlId() {
    return getDomain().getClass().getSimpleName() + Integer.toString(this.domainId);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void refreshAllValues(final ADTNode node, final ValueAssignement mapNew) {
    String name = node.getName();
    boolean proponent = node.getRole() == ADTNode.Role.PROPONENT;
    if (node.hasDefault()) {
      Ring value = (Ring) this.valuesMap.get(proponent, name);
      if (value == null) {
        mapNew.put(proponent, name, getDomain().getDefaultValue(node));
      }
      else {
        mapNew.put(proponent, name, value);
      }
    }
    if (!node.isLeaf()) {
      for (Node child : node.getChildren()) {
        if (child != null) {
          refreshAllValues((ADTNode) child, mapNew);
        }
      }
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void refreshAllValues(final SandNode node, final ValueAssignement mapNew) {
    String name = node.getName();
    if (node.isLeaf()) {
      Ring value = (Ring) valuesMap.get(true, name);
      if (value == null) {
        mapNew.put(true, name, getDomain().getDefaultValue(node));
      }
      else {
        mapNew.put(true, name, value);
      }
    }
    else {
      for (Node child : node.getChildren()) {
        if (child != null) {
          refreshAllValues((SandNode) child, mapNew);
        }
      }
    }
  }

  private Domain<Ring>              domain;
  transient private Evaluator<Ring> evaluator;
  private ValueAssignement<Ring>    valuesMap;
  /** if true we show calculated values for countered nodes */
  private boolean                   showAllLabels;
  private int                       treeId;
  private int                       domainId;
  static final long                 serialVersionUID = 665464411570251703L;
}
