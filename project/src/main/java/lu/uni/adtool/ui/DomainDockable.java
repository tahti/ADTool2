package lu.uni.adtool.ui;

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.DomainFactory;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.event.CVetoClosingEvent;
import bibliothek.gui.dock.common.event.CVetoClosingListener;
import bibliothek.gui.dock.common.intern.CDockable;

public class DomainDockable extends DefaultMultipleCDockable
    implements CVetoClosingListener, ItemListener {
  public static final String DOMAIN_ID = "_domain";

  public DomainDockable(DomainFactory factory, ValuationDomain values) {
    super(factory);
    this.setCloseable(true);
    this.canvas = new AbstractDomainCanvas(factory.getController(), values);
    ImageIcon icon = new IconFactory().createImageIcon("/icons/treeEx_16x16.png", getTitleText());
    this.setTitleIcon(icon);
    if (values.getDomain() != null) {
      this.setTitleText("Domain  - " + values.getDomain().getName());
    }
    else {
      this.setTitleText("Null Domain");
    }
    addVetoClosingListener(this);
    initLayout();
  }

  public ValuationDomain getValues() {
    return this.canvas.getValues();
  }

  /**
   * Called before a set of {@link CDockable}s gets closed. This method may be
   * invoked with events that are already canceled, check the
   * {@link CVetoClosingEvent#isCanceled()} property.
   *
   * @param event
   *          the event that will happen but may be canceled
   */
  public void closing(CVetoClosingEvent event) {
    if (event.isCancelable()) {
      int result = OptionPane.showYNDialog(this.canvas.getController().getFrame(),
          Options.getMsg("domainclosedialog.txt"), Options.getMsg("domainclosedialog.title"));
      if (result != JOptionPane.YES_OPTION) {
        event.cancel();
      }
    }
  }

  /**
   * Called after a set of {@link CDockable}s has been closed. This method may
   * be called without {@link #closing(CVetoClosingEvent)} been called
   * beforehand.
   *
   * @param event
   *          the event that has already happened
   */
  public void closed(CVetoClosingEvent event) {
    // do nothing
  }

  public AbstractDomainCanvas getCanvas() {
    return canvas;
  }

  public String getUniqueId() {
    return TreeDockable.TREE_ID + Integer.toString(canvas.getId()) + DOMAIN_ID
        + Integer.toString(canvas.getDomainId());
  }

  public void hideShowAll() {
    Debug.log("hiding show all");
    allLabelBox.setVisible(false);
  }

  /**
   * Listens to the check boxes.
   *
   * @param e
   *          event.
   */
  public final void itemStateChanged(final ItemEvent e) {
    if (canvas == null) {
      System.err.println(Options.getMsg("sanddomain.error.cavsasnull"));
      return;
    }
    final Object source = e.getItemSelectable();
    if (source == labelBox) {
      if (e.getStateChange() == ItemEvent.DESELECTED) {
        canvas.setShowLabels(false);
      }
      else {
        canvas.setShowLabels(true);
      }
    }
    if (source == allLabelBox) {
      if (e.getStateChange() == ItemEvent.DESELECTED) {
        canvas.setShowAllLabels(false);
      }
      else {
        canvas.setShowAllLabels(true);
      }
    }
    else if (source == markEditableBox) {
      if (e.getStateChange() == ItemEvent.DESELECTED) {
        canvas.setMarkEditable(false);
      }
      else {
        canvas.setMarkEditable(true);
      }
    }
    else if (source == nodeSizeBox) {
      if (e.getStateChange() == ItemEvent.DESELECTED) {
        canvas.setLocalExtentProvider(false);
      }
      else {
        canvas.setLocalExtentProvider(true);
      }
    }
  }

  private void initLayout() {
    this.getContentPane().setLayout(new BorderLayout());
    labelBox = new JCheckBox(Options.getMsg("sanddomain.showlabels.txt"));
    labelBox.setMnemonic(
        KeyStroke.getKeyStroke(Options.getMsg("sanddomain.showlabels.key")).getKeyCode());
    labelBox.setSelected(false);
    labelBox.addItemListener(this);
    allLabelBox = new JCheckBox(Options.getMsg("sanddomain.alllabels.txt"));
    allLabelBox.setMnemonic(
    KeyStroke.getKeyStroke(Options.getMsg("sanddomain.alllabels.key")).getKeyCode());
    allLabelBox.setSelected(true);
    allLabelBox.addItemListener(this);
    markEditableBox = new JCheckBox(Options.getMsg("sanddomain.markeditable.txt"));
    markEditableBox.setMnemonic(
        KeyStroke.getKeyStroke(Options.getMsg("sanddomain.markeditable.key")).getKeyCode());
    markEditableBox.setSelected(this.canvas.isMarkEditable());
    markEditableBox.addItemListener(this);
    this.canvas.setShowLabels(false);
    this.canvas.setShowAllLabels(true);
    nodeSizeBox = new JCheckBox(Options.getMsg("sanddomain.localsize.txt"));
    nodeSizeBox.setMnemonic(KeyEvent.VK_S);
    nodeSizeBox.setMnemonic(
        KeyStroke.getKeyStroke(Options.getMsg("sanddomain.localsize.key")).getKeyCode());
    nodeSizeBox.setSelected(false);
    nodeSizeBox.addItemListener(this);
    final CollapsiblePanel optionsPanel =
        new CollapsiblePanel(Options.getMsg("sanddomain.options.txt"));
    final JPanel checkBoxPanel = new JPanel();
    checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.LINE_AXIS));
    checkBoxPanel.add(labelBox);
    checkBoxPanel.add(nodeSizeBox);
    checkBoxPanel.add(markEditableBox);
    checkBoxPanel.add(allLabelBox);
    optionsPanel.add(checkBoxPanel);
    optionsPanel.toggleVisibility(true);
    final JScrollPane scrollPane = new JScrollPane(this.canvas);
    this.canvas.setVisible(true);
    scrollPane.setAutoscrolls(true);
    this.canvas.setScrollPane(scrollPane);
    this.add(scrollPane);
    this.add(optionsPanel, BorderLayout.PAGE_END);
  }

  private AbstractDomainCanvas canvas;
  private JCheckBox            labelBox;
  private JCheckBox            allLabelBox;
  private JCheckBox            nodeSizeBox;
  private JCheckBox            markEditableBox;

}
