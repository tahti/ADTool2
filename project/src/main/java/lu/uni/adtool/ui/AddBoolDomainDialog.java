package lu.uni.adtool.ui;

import lu.uni.adtool.domains.custom.AdtBoolDomain;
import lu.uni.adtool.domains.custom.BoolParser;
import lu.uni.adtool.tools.Options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AddBoolDomainDialog extends JDialog implements ActionListener, DocumentListener{

  public AddBoolDomainDialog(Frame frame, AdtBoolDomain domain) {
    super(frame, Options.getMsg("adtdomain.custom.bool.dialogtitle"), true);
    this.domain = domain;
    setAlwaysOnTop(true);
    this.setLocationRelativeTo(frame);
    setLocation(160, 160);
    setSize(800, 600);
  }

  public AdtBoolDomain showDialog() {
    this.createLayout();
    this.setVisible(true);
    return this.domain;
    // return chosenDomain;
  }

//   public void propertyChange(PropertyChangeEvent e) {
//     Object source = e.getSource();
//     if (source instanceof JTextField) {
//       if (source == this.name) {
//         Debug.log("name field changed");
//       }
//       else if (source == this.description) {
//         Debug.log("description field changed");
//       }
//       else {
//         Debug.log("other field changed");
//       }
//     }
//   }

  protected void createLayout() {
    JButton cancelButton = new JButton(Options.getMsg("button.cancel"));
    this.errorLabel = new JLabel("");
    cancelButton.addActionListener(this);
    this.okButton = new JButton(Options.getMsg("button.ok"));
    this.okButton.setEnabled(false);
    this.okButton.setActionCommand(Options.getMsg("button.ok"));
    this.okButton.addActionListener(this);
    getRootPane().setDefaultButton(okButton);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    buttonPane.add(errorLabel);
    buttonPane.add(Box.createHorizontalGlue());
    buttonPane.add(cancelButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 30)));
    buttonPane.add(okButton);
    this.name = new JTextField();
    this.description = new JTextField();
    this.cp = new JTextField();
    this.co = new JTextField();
    this.ap = new JTextField();
    this.ao = new JTextField();
    this.op = new JTextField();
    this.oo = new JTextField();
    String[] valueList = {"true", "false"};
    this.defaulto = new JComboBox(valueList);
    this.defaultp = new JComboBox(valueList);
    this.modifiableo = new JCheckBox(Options.getMsg("dialog.addbooldomain.modifiableo"));
    this.modifiableo.setSelected(true);
    this.modifiablep = new JCheckBox(Options.getMsg("dialog.addbooldomain.modifiablep"));
    this.modifiablep.setSelected(true);
    this.name.getDocument().addDocumentListener(this);
    this.description.getDocument().addDocumentListener(this);
    this.cp.getDocument().addDocumentListener(this);
    this.co.getDocument().addDocumentListener(this);
    this.ap.getDocument().addDocumentListener(this);
    this.ao.getDocument().addDocumentListener(this);
    this.op.getDocument().addDocumentListener(this);
    this.oo.getDocument().addDocumentListener(this);
    this.name.getDocument().putProperty("parent", name);
    this.description.getDocument().putProperty("parent", description);
    this.cp.getDocument().putProperty("parent", cp);
    this.co.getDocument().putProperty("parent", co);
    this.ap.getDocument().putProperty("parent", ap);
    this.ao.getDocument().putProperty("parent", ao);
    this.op.getDocument().putProperty("parent", op);
    this.oo.getDocument().putProperty("parent", oo);

    this.name.setBackground(invalidColor);
    this.description.setBackground(invalidColor);
    this.cp.setBackground(invalidColor);
    this.co.setBackground(invalidColor);
    this.ap.setBackground(invalidColor);
    this.ao.setBackground(invalidColor);
    this.op.setBackground(invalidColor);
    this.oo.setBackground(invalidColor);

    JPanel inputContent = new JPanel();
    inputContent.setLayout(new GridLayout(0, 2, 10, 10));
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.defaultp")));
    inputContent.add(this.defaultp);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.defaulto")));
    inputContent.add(this.defaulto);
    inputContent.add(this.modifiablep);
    inputContent.add(this.modifiableo);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.name")));
    inputContent.add(this.name);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.description")));
    inputContent.add(this.description);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.op")));
    inputContent.add(this.op);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.oo")));
    inputContent.add(this.oo);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.ap")));
    inputContent.add(this.ap);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.ao")));
    inputContent.add(this.ao);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.cp")));
    inputContent.add(this.cp);
    inputContent.add(new JLabel(Options.getMsg("dialog.addbooldomain.co")));
    inputContent.add(this.co);
    JPanel mainContent = new JPanel();
    mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.LINE_AXIS));
    mainContent.add(Box.createRigidArea(new Dimension(10, 0)));
    mainContent.add(inputContent);
    mainContent.add(Box.createRigidArea(new Dimension(10, 0)));
    Container contentPane = getContentPane();
    JPanel allPane = new JPanel();
    allPane.setLayout(new BorderLayout());
    allPane.add(mainContent, BorderLayout.CENTER);
    allPane.add(buttonPane, BorderLayout.PAGE_END);
    contentPane.add(new JScrollPane(allPane), BorderLayout.CENTER);
    pack();
  }

  // Handle clicks on the Set and Cancel buttons.
  /**
   * {@inheritDoc}
   *
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    if (Options.getMsg("button.cancel").equals(e.getActionCommand())) {
      escPressed();
    }
    else if (Options.getMsg("button.ok").equals(e.getActionCommand())) {
      enterPressed();
    }
  }

  /** Handle the key typed event from the text field. */
  public void keyTyped(KeyEvent e) {
  }

  /** Handle the key pressed event from the text field. */
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == 10) {
      enterPressed();
    }
    if (e.getKeyCode() == 27) {
      escPressed();
    }
  }

  public void insertUpdate(DocumentEvent e) {
    this.checkValid((JTextField)e.getDocument().getProperty("parent"));
  }

  public void removeUpdate(DocumentEvent e) {
    this.checkValid((JTextField)e.getDocument().getProperty("parent"));
  }

  public void changedUpdate(DocumentEvent e) {
    this.checkValid((JTextField)e.getDocument().getProperty("parent"));
  }

  /** Handle the key released event from the text field. */
  public void keyReleased(KeyEvent e) {
  }

  public void escPressed() {
    this.domain = null;
    setVisible(false);
    dispose();
  }

  public void enterPressed() {
    domain.setProDefault(this.defaultp.getSelectedIndex() == 0);
    domain.setOppDefault(this.defaulto.getSelectedIndex() == 0);
    domain.setProModifiable(this.modifiablep.isSelected());
    domain.setOppModifiable(this.modifiableo.isSelected());
    if ((!this.domain.setName(this.name.getText())) || (!this.domain.setCp(this.cp.getText()))
        || (!this.domain.setCo(this.co.getText())) || (!this.domain.setAp(this.ap.getText()))
        || (!this.domain.setAo(this.ao.getText())) || (!this.domain.setOp(this.op.getText()))
        || (!this.domain.setOo(this.oo.getText()))
        || (!this.domain.setDescription(this.description.getText()))) {
      okButton.setEnabled(false);
    }
    else {
      setVisible(false);
      dispose();
    }
  }

  public boolean checkValid(JTextField field) {
    okButton.setEnabled(false);
    this.errorLabel.setText("");
    if(field == this.name || field == this.description) {
      if (field.getText().length() > 0) {
        field.setBackground(validColor);
      }
      else {
        field.setBackground(invalidColor);
      }
    }
    else {
      BoolParser parser = new BoolParser();
      if (field.getText().length() > 0 && parser.parseString(field.getText()) != null) {
        field.setBackground(validColor);
      }
      else {
        this.errorLabel.setText(parser.getErrorMessage());
        field.setBackground(invalidColor);
      }
    }
    if (this.name.getBackground() == AddBoolDomainDialog.validColor
        && this.description.getBackground() == AddBoolDomainDialog.validColor
        && this.ao.getBackground() == AddBoolDomainDialog.validColor
        && this.ap.getBackground() == AddBoolDomainDialog.validColor
        && this.oo.getBackground() == AddBoolDomainDialog.validColor
        && this.op.getBackground() == AddBoolDomainDialog.validColor
        && this.co.getBackground() == AddBoolDomainDialog.validColor
        && this.cp.getBackground() == AddBoolDomainDialog.validColor) {
      okButton.setEnabled(true);
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @see JDialog#createRootPane()
   */
  protected JRootPane createRootPane() {
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        escPressed();
      }
    };
    JRootPane rootPane = new JRootPane();
    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        enterPressed();
      }
    };
    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    // rootPane.registerKeyboardAction(new EnterListener(), strokeEnter,
    // JComponent.WHEN_IN_FOCUSED_WINDOW);

    return rootPane;
  }

  private AdtBoolDomain domain;
  private JTextField    name;
  private JTextField    description;
  private JTextField    ao;
  private JTextField    ap;
  private JTextField    oo;
  private JTextField    op;
  private JTextField    cp;
  private JTextField    co;
  private JComboBox     defaulto;
  private JComboBox     defaultp;
  private JCheckBox     modifiableo;
  private JCheckBox     modifiablep;
  private JButton       okButton;
  private JLabel        errorLabel;
  private static Color  validColor   = new Color(170, 255, 170);
  private static Color  invalidColor = new Color(255, 170, 170);
}
