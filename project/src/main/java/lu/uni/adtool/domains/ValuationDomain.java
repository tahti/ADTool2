package lu.uni.adtool.domains;

import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.tree.SandNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.util.xml.XElement;

public class ValuationDomain implements MultipleCDockableLayout {

  public ValuationDomain() {
    this.domainId = -1;
    this.domain = null;
    this.evaluator = null;
    this.valueAssPro = new ValueAssignement<Ring>();
    this.valueAssOpp = null;
    this.showAllLabels = true;
  }

  public ValuationDomain(int treeId, int domainId, final AdtDomain<Ring> domain) {
    this.domainId = domainId;
    this.treeId = treeId;
    this.domain = domain;
    this.evaluator = new Evaluator<Ring>(domain);
    this.valueAssPro = new ValueAssignement<Ring>();
    this.valueAssOpp = new ValueAssignement<Ring>();
    this.showAllLabels = true;
  }

  public ValuationDomain(int treeId, int domainId, final SandDomain<Ring> domain) {
    this.domainId = domainId;
    this.treeId = treeId;
    this.domain = domain;
    this.evaluator = new Evaluator<Ring>(domain);
    this.valueAssPro = new ValueAssignement<Ring>();
    this.valueAssOpp = null;
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
      valueAssOpp = null;
      node = new SandNode();
    }
    else {
      node = new ADTNode();
      this.valueAssOpp = new ValueAssignement<Ring>();
    }
    this.treeId = in.readInt();
    this.domainId = in.readInt();
    Debug
        .log("Read domain " + domainName + " treeId:" + this.treeId + " domainId:" + this.domainId);
    if (node != null) {
      int counter = in.readInt();
      this.valueAssPro.clear();
      for (int i = 0; i < counter; i++) {
        String name = in.readUTF();
        String value = in.readUTF();
        Ring v = domain.getDefaultValue(node);
        v.updateFromString(value);
        this.setValue(name, v);
      }
      if (domain instanceof AdtDomain) {
        counter = in.readInt();
        this.valueAssOpp.clear();
        for (int i = 0; i < counter; i++) {
          String name = in.readUTF();
          String value = in.readUTF();
          Ring v = domain.getDefaultValue(node);
          v.updateFromString(value);
          this.setValue(false, name, v);
        }
        this.evaluator = new Evaluator<Ring>((AdtDomain) domain);
      }
      else {
        this.evaluator = new Evaluator<Ring>((SandDomain) domain);
      }
    }
    else {
      System.err.println("Error reading stream");
    }
  }

  public void readXML(XElement element) {
    String domainName = element.getElement("domain").getString();
    Node node = null;
    domain = DomainFactory.createFromString(domainName);
    if (DomainFactory.isSandDomain(domainName)) {
      valueAssOpp = null;
      node = new SandNode();
    }
    else {
      node = new ADTNode();
      this.valueAssOpp = new ValueAssignement<Ring>();
    }
    this.treeId = element.getElement("treeId").getInt();
    this.domainId = element.getElement("domainId").getInt();
    Debug.log(" layout with treeId " + treeId + " domainId:" + domainId);
    XElement v = element.getElement("values");
    if (node != null && v != null) {
      this.valueAssPro.clear();
      int size = v.getElementCount();
      for (int i = 0; i < size; i = i + 2) {
        String key = v.getElement(i).getString();
        String value = v.getElement(i + 1).getString();
        Ring r = domain.getDefaultValue(node);
        r.updateFromString(value);
        this.setValue(key, r);
      }
    }
    if (domain instanceof AdtDomain) {
      this.valueAssOpp.clear();
      int size = v.getElementCount();
      for (int i = 0; i < size; i = i + 2) {
        String key = v.getElement(i).getString();
        String value = v.getElement(i + 1).getString();
        Ring r = domain.getDefaultValue(node);
        r.updateFromString(value);
        this.setValue(false, key, r);
      }
      this.evaluator = new Evaluator<Ring>((AdtDomain) domain);
    }
    else {
      this.evaluator = new Evaluator<Ring>((SandDomain) domain);
    }
    // fileName = element.getElement( "name" ).getString();
    // fileContent = element.getElement( "content" ).getString();
    // background = new Color( element.getElement( "background" ).getInt() );
  }

  public void writeStream(DataOutputStream out) throws IOException {
    out.writeUTF(this.domain.getClass().getSimpleName());
    out.writeInt(this.treeId);
    out.writeInt(this.domainId);
    Set<String> keys = valueAssPro.keySet();
    out.writeInt(keys.size());
    for (String key : keys) {
      out.writeUTF(key);
      out.writeUTF(valueAssPro.get(key).toString());
    }
    if (domain instanceof AdtDomain) {
      keys = valueAssOpp.keySet();
      out.writeInt(keys.size());
      for (String key : keys) {
        out.writeUTF(key);
        out.writeUTF(valueAssOpp.get(key).toString());
      }
    }
  }

  public void exportXML(XElement element) {
    XElement dElement = element.addElement("domain");
    dElement.addString("id", getExportXmlId());
    dElement.addElement("class").setString(domain.getClass().getName());
    dElement.addElement("tool").setString("ADTool2");
    if (domain instanceof Parametrized) {
      dElement.addElement("range").setString(((Parametrized) domain).getParameter().toString());
    }

  }

  public void writeXML(XElement element) {
    element.addElement("domain").setString(domain.getClass().getSimpleName());
    element.addElement("treeId").setInt(this.treeId);
    element.addElement("domainId").setInt(this.domainId);
    XElement v = new XElement("values");
    Set<String> keys = valueAssPro.keySet();
    for (String key : keys) {
      v.addElement("label").setString(key);
      v.addElement("value").setString(valueAssPro.get(key).toString());
    }
    element.addElement(v);
    if (domain instanceof AdtDomain) {
      v = new XElement("valuesOpp");
      keys = valueAssOpp.keySet();
      for (String key : keys) {
        v.addElement("label").setString(key);
        v.addElement("value").setString(valueAssOpp.get(key).toString());
      }
      element.addElement(v);
    }
  }

  public Ring getValue(ADTNode node) {
    Ring result = null;
    String key = node.getName();
    if (node.getRole() == ADTNode.Role.PROPONENT) {
      result = valueAssPro.get(key);
    }
    else {
      result = valueAssOpp.get(key);
    }
    return result;
  }

  public Ring getValue(SandNode node) {
    String key = node.getName();
    return valueAssPro.get(key);
  }

  public Ring get(String key) {
    return (Ring) valueAssPro.get(key);
  }

  public Ring getOpp(String key) {
    return (Ring) valueAssOpp.get(key);
  }

  public Set<String> sandKeySet() {
    return valueAssPro.keySet();
  }

  public Set<String> oppKeySet() {
    return valueAssOpp.keySet();
  }

  public final Ring getTermValue(final ADTNode node) {
    Ring value = (Ring) evaluator.getValue(node);
    if (value == null) {
      return (Ring) evaluator.reevaluate(node, valueAssPro, valueAssOpp);
    }
    else {
      return value;
    }
  }

  public final Ring getTermValue(final SandNode node) {
    Ring value = (Ring) evaluator.getValue(node);
    if (value == null) {
      return (Ring) evaluator.reevaluate(node, valueAssPro);
    }
    else {
      return value;
    }
  }

  public void setValue(boolean proponent, String key, Ring value) {
    if (proponent) {
      valueAssPro.put(key, value);
    }
    else {
      valueAssOpp.put(key, value);
    }
  }

  public void setDefaultValue(ADTNode node) {
    Ring value = getDomain().getDefaultValue(node);
    if (node.getRole() == ADTNode.Role.PROPONENT) {
      valueAssPro.put(node.getName(), value);
    }
    else {
      valueAssOpp.put(node.getName(), value);
    }
  }

  public void setDefaultValue(SandNode node) {
    Ring value = getDomain().getDefaultValue(node);
    valueAssPro.put(node.getName(), value);
  }

  public void setValue(String key, Ring value) {
    valueAssPro.put(key, value);
  }

  public boolean hasEvaluator() {
    return evaluator != null;
  }

  public void rename(Node node, String name) {
    Ring value = null;
    if (node instanceof SandNode) {
      value = valueAssPro.get(node.getName());
      if (value == null) {
        value = getDomain().getDefaultValue(node);
      }
      valueAssPro.put(name, value);
    }
    else {
      if (((ADTNode) node).getRole() == ADTNode.Role.PROPONENT) {
        value = valueAssPro.get(node.getName());
        if (value == null) {
          value = getDomain().getDefaultValue(node);
        }
        valueAssPro.put(name, value);
      }
      else {
        value = valueAssOpp.get(node.getName());
        if (value == null) {
          value = getDomain().getDefaultValue(node);
        }
        valueAssOpp.put(name, value);
      }
    }
  }

  public void refreshAllValues(ADTNode root) {
    ValueAssignement<Ring> proNew = new ValueAssignement<Ring>();
    ValueAssignement<Ring> oppNew = new ValueAssignement<Ring>();
    refreshAllValues(root, proNew, oppNew);
    valueAssPro = proNew;
    valueAssOpp = oppNew;
  }

  public void refreshAllValues(SandNode root) {
    ValueAssignement<Ring> proNew = new ValueAssignement<Ring>();
    refreshAllValues(root, proNew);
    valueAssPro = proNew;
    valueAssOpp = null;
  }

  public void valuesUpdated(ADTNode root) {
    evaluator.reevaluate(root, valueAssPro, valueAssOpp);
  }

  public void valuesUpdated(SandNode root) {
    evaluator.reevaluate(root, valueAssPro);
  }

  public void treeChanged(ADTNode root) {
    this.evaluator.reevaluate(root, valueAssPro, valueAssOpp);
    this.refreshAllValues(root);
  }

  public void treeChanged(SandNode root) {
    this.evaluator.reevaluate(root, valueAssPro);
    this.refreshAllValues(root);
  }

  /**
   * Gets the domain for this instance.
   *
   * @return The domain.
   */
  public Domain<Ring> getDomain() {
    return domain;
  }

  /**
   * Sets the valueAssPro for this instance.
   *
   * @param newValueAssPro
   *          new value assignement.
   */
  public void setValueAssPro(ValueAssignement<Ring> newValueAss, ADTNode root) {
    this.valueAssPro = newValueAss;
    evaluator.reevaluate(root, valueAssPro, valueAssOpp);
  }

  /**
   * Sets the valueAssOpp for this instance.
   *
   * @param newValueAssOpp
   *          new value assignement.
   */
  public void setValueAssOpp(ValueAssignement<Ring> newValueAss, ADTNode root) {
    this.valueAssOpp = newValueAss;
    evaluator.reevaluate(root, valueAssPro, valueAssOpp);
  }

  /**
   * Gets the valueAssPro for this instance.
   *
   * @return The valueAssPro.
   */
  public ValueAssignement<Ring> getValueAssPro() {
    return this.valueAssPro;
  }

  /**
   * Gets the valueAssOpp for this instance.
   *
   * @return The valueAssOpp.
   */
  public ValueAssignement<Ring> getValueAssOpp() {
    return this.valueAssOpp;
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
  private void refreshAllValues(final ADTNode node, final ValueAssignement proNew,
      final ValueAssignement oppNew) {
    String name = node.getName();
    if (node.hasDefault()) {
      if (node.getRole() == ADTNode.Role.OPPONENT) {
        Ring value = (Ring) valueAssOpp.get(name);
        if (value == null) {
          oppNew.put(name, getDomain().getDefaultValue(node));
        }
        else {
          oppNew.put(name, value);
        }
      }
      else {
        Ring value = (Ring) valueAssPro.get(name);
        if (value == null) {
          proNew.put(name, getDomain().getDefaultValue(node));
        }
        else {
          proNew.put(name, value);
        }
      }
    }
    if (!node.isLeaf()) {
      for (Node child : node.getChildren()) {
        if (child != null) {
          refreshAllValues((ADTNode) child, proNew, oppNew);
        }
      }
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void refreshAllValues(final SandNode node, final ValueAssignement proNew) {
    String name = node.getName();
    if (node.isLeaf()) {
      Ring value = (Ring) valueAssPro.get(name);
      if (value == null) {
        proNew.put(name, getDomain().getDefaultValue(node));
      }
      else {
        proNew.put(name, value);
      }
    }
    else {
      for (Node child : node.getChildren()) {
        if (child != null) {
          refreshAllValues((SandNode) child, proNew);
        }
      }
    }
  }

  private Domain<Ring>              domain;
  transient private Evaluator<Ring> evaluator;
  private ValueAssignement<Ring>    valueAssPro;
  private ValueAssignement<Ring>    valueAssOpp;
  /** if true we show calculated values for countered nodes */
  private boolean                   showAllLabels;
  private int                       treeId;
  private int                       domainId;
  static final long                 serialVersionUID = 665464411570251703L;
}
