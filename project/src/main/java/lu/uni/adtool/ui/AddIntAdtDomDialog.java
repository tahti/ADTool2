package lu.uni.adtool.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lu.uni.adtool.domains.custom.AdtIntDomain;
import lu.uni.adtool.domains.custom.IntParser;
import lu.uni.adtool.domains.rings.Int;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;

public class AddIntAdtDomDialog extends JDialog implements ActionListener, DocumentListener {

  public enum FieldType {
    AP, AO, OP, OO, CP, CO, NAME, DESCR, NONE, DEFAULTP, DEFAULTO
  }

  public AddIntAdtDomDialog(Frame frame, AdtIntDomain domain) {
    super(frame, Options.getMsg("adtdomain.custom.dialogtitle"), true);
    this.domain = domain;
    setAlwaysOnTop(true);
    this.setLocationRelativeTo(frame);
    setLocation(160, 160);
    setSize(800, 600);
  }

  public AdtIntDomain showDialog() {
    if (this.domain != null) {
      this.createLayout();
      this.setVisible(true);
    }
    return this.domain;
  }

  protected void createLayout() {
    JButton cancelButton = new JButton(Options.getMsg("button.cancel"));
    this.errorLabel = new JLabel("");
    cancelButton.addActionListener(this);
    this.errorLabel = new JLabel("");
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
    buttonPane.add(Box.createRigidArea(new Dimension(10, 30)));
    buttonPane.add(cancelButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 30)));
    buttonPane.add(okButton);
    if (this.domain.getName() != null && !this.domain.getName().equals(Options.getMsg("adtdomain.custom.int.name"))) {
      this.name = new JTextField(this.domain.getName());
    }
    else {
      this.name = new JTextField("");
    }
    if (this.domain.getShortDescription() != null
        && !this.domain.getShortDescription().equals(Options.getMsg("adtdomain.custom.int.description"))) {
      this.description = new JTextField(this.domain.getShortDescription());
    }
    else {
      this.description = new JTextField("");
    }
    this.cp = new JTextField(this.domain.getCp());
    this.co = new JTextField(this.domain.getCo());
    this.ap = new JTextField(this.domain.getAp());
    this.ao = new JTextField(this.domain.getAo());
    this.op = new JTextField(this.domain.getOp());
    this.oo = new JTextField(this.domain.getOo());
    this.defaulto = new JTextField(this.domain.getOppDefault());
    this.defaulto.getDocument().addDocumentListener(this);
    this.defaulto.getDocument().putProperty("parent", defaulto);
    this.defaulto.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.DEFAULTO);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });

    this.defaultp = new JTextField(this.domain.getProDefault());
    this.defaultp.getDocument().putProperty("parent", defaultp);
    this.defaultp.getDocument().addDocumentListener(this);
    this.defaultp.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.DEFAULTP);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });

    this.modifiableo = new JCheckBox(Options.getMsg("dialog.adddomain.modifiableo"));
    this.modifiableo.setSelected(domain.isOppModifiable());
    this.modifiablep = new JCheckBox(Options.getMsg("dialog.adddomain.modifiablep"));
    this.modifiablep.setSelected(domain.isProModifiable());
    this.name.getDocument().addDocumentListener(this);
    this.description.getDocument().addDocumentListener(this);
    this.cp.getDocument().addDocumentListener(this);
    this.cp.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.CP);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.co.getDocument().addDocumentListener(this);
    this.co.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.CO);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.ap.getDocument().addDocumentListener(this);
    this.ap.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.AP);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.ao.getDocument().addDocumentListener(this);
    this.ao.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.AO);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.op.getDocument().addDocumentListener(this);
    this.op.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.OP);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.oo.getDocument().addDocumentListener(this);
    this.oo.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.OO);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.name.getDocument().putProperty("parent", name);
    this.name.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.NAME);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.description.getDocument().putProperty("parent", description);
    this.description.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.DESCR);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.cp.getDocument().putProperty("parent", cp);
    this.co.getDocument().putProperty("parent", co);
    this.ap.getDocument().putProperty("parent", ap);
    this.ao.getDocument().putProperty("parent", ao);
    this.op.getDocument().putProperty("parent", op);
    this.oo.getDocument().putProperty("parent", oo);
    try {
      Integer.parseInt(this.defaultp.getText());
      this.defaultp.setBackground(validColor);
    }
    catch (NumberFormatException e) {
      this.defaultp.setBackground(invalidColor);
    }
    try {
      Integer.parseInt(this.defaulto.getText());
      this.defaulto.setBackground(validColor);
    }
    catch (NumberFormatException e) {
      this.defaulto.setBackground(invalidColor);
    }
    if (name.getText().length() > 0) {
      name.setBackground(validColor);
    } else {
      name.setBackground(invalidColor);
    }
    if (description.getText().length() > 0) {
      description.setBackground(validColor);
    } else {
      description.setBackground(invalidColor);
    }
    checkValid(this.cp);
    checkValid(this.co);
    checkValid(this.ap);
    checkValid(this.ao);
    checkValid(this.op);
    checkValid(this.oo);

    JPanel inputContent = new JPanel();
    inputContent.setLayout(new GridLayout(0, 2, 10, 10));
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.defaultp")));
    inputContent.add(this.defaultp);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.defaulto")));
    inputContent.add(this.defaulto);
    inputContent.add(this.modifiablep);
    inputContent.add(this.modifiableo);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.name")));
    inputContent.add(this.name);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.description")));
    inputContent.add(this.description);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.op")));
    inputContent.add(this.op);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.oo")));
    inputContent.add(this.oo);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.ap")));
    inputContent.add(this.ap);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.ao")));
    inputContent.add(this.ao);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.cp")));
    inputContent.add(this.cp);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.co")));
    inputContent.add(this.co);
    JPanel mainContent = new JPanel();
    mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.LINE_AXIS));
    mainContent.add(Box.createRigidArea(new Dimension(10, 0)));
    mainContent.add(inputContent);
    mainContent.add(Box.createRigidArea(new Dimension(10, 0)));
    Container contentPane = getContentPane();
    this.helpPane = new JPanel();
    helpPane.setLayout(new BoxLayout(helpPane, BoxLayout.PAGE_AXIS));
    setHelpContent(FieldType.NONE);
    JPanel allPane = new JPanel();
    allPane.setLayout(new BorderLayout());
    allPane.add(this.helpPane, BorderLayout.WEST);
    allPane.add(mainContent, BorderLayout.EAST);
    allPane.add(buttonPane, BorderLayout.SOUTH);
    contentPane.add(new JScrollPane(allPane), BorderLayout.CENTER);
    pack();
  }

  /**
   * {@inheritDoc}
   *
   * @see ActionListener#actionPerformed(ActionEvent) Handle clicks on the Set
   *      and Cancel buttons.
   */
  public void actionPerformed(ActionEvent e) {
    if (Options.getMsg("button.cancel").equals(e.getActionCommand())) {
      escPressed();
    } else if (Options.getMsg("button.ok").equals(e.getActionCommand())) {
      enterPressed();
    }
  }

  public void insertUpdate(DocumentEvent e) {
    this.checkValid((JTextField) e.getDocument().getProperty("parent"));
  }

  public void removeUpdate(DocumentEvent e) {
    this.checkValid((JTextField) e.getDocument().getProperty("parent"));
  }

  public void changedUpdate(DocumentEvent e) {
    this.checkValid((JTextField) e.getDocument().getProperty("parent"));
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
    domain.setProModifiable(this.modifiablep.isSelected());
    domain.setOppModifiable(this.modifiableo.isSelected());
    if ((!this.domain.setName(this.name.getText())) || (!this.domain.setCp(this.cp.getText()))
        || (!this.domain.setCo(this.co.getText())) || (!this.domain.setAp(this.ap.getText()))
        || (!this.domain.setAo(this.ao.getText())) || (!this.domain.setOp(this.op.getText()))
        || (!this.domain.setOo(this.oo.getText())) || (!domain.setProDefault(this.defaultp.getText()))
        || (!this.domain.setOppDefault(this.defaulto.getText()))
        || (!this.domain.setDescription(this.description.getText()))) {
      okButton.setEnabled(false);
    } else {
      setVisible(false);
      dispose();
    }
  }

  public boolean checkValid(JTextField field) {
    okButton.setEnabled(false);
    this.errorLabel.setText("");
    if (field == this.name || field == this.description) {
      if (field.getText().length() > 0) {
        field.setBackground(validColor);
      } else {
        field.setBackground(invalidColor);
      }
    } else if (field == this.defaulto || field == this.defaultp) {
      Int temp = new Int(0);
      if (temp.updateFromString(field.getText())) {
        field.setBackground(validColor);
      }
      else {
        field.setBackground(invalidColor);
        this.errorLabel.setText(Options.getMsg("dialog.adddomain.wrongint"));
      }
    } else {
      IntParser parser = new IntParser();
      if (field.getText().length() > 0 && parser.parseString(field.getText()) != null) {
        field.setBackground(validColor);
      } else {
        this.errorLabel.setText(parser.getErrorMessage());
        field.setBackground(invalidColor);
      }
    }
    if (this.name.getBackground() == AddIntAdtDomDialog.validColor
        && this.defaultp.getBackground() == AddIntAdtDomDialog.validColor
        && this.defaulto.getBackground() == AddIntAdtDomDialog.validColor
        && this.description.getBackground() == AddIntAdtDomDialog.validColor
        && this.ao.getBackground() == AddIntAdtDomDialog.validColor
        && this.ap.getBackground() == AddIntAdtDomDialog.validColor
        && this.oo.getBackground() == AddIntAdtDomDialog.validColor
        && this.op.getBackground() == AddIntAdtDomDialog.validColor
        && this.co.getBackground() == AddIntAdtDomDialog.validColor
        && this.cp.getBackground() == AddIntAdtDomDialog.validColor) {
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

    return rootPane;
  }

  private void setHelpContent(FieldType field) {
    switch (field) {
    case AP:
      this.setHelpText("ap");
      break;
    case AO:
      this.setHelpText("ao");
      break;
    case OP:
      this.setHelpText("op");
      break;
    case OO:
      this.setHelpText("oo");
      break;
    case CP:
      this.setHelpText("cp");
      break;
    case CO:
      this.setHelpText("co");
      break;
    case NAME:
      this.setHelpText("name");
      break;
    case DESCR:
      this.setHelpText("description");
      break;
    case DEFAULTP:
    case DEFAULTO:
    case NONE:
      this.setHelpText("");
      break;
    }
  }

  private void setHelpText(String op) {
    this.helpPane.removeAll();
    JLabel head = new JLabel(Options.getMsg("dialog.adddomain.help"));
    head.setFont(new Font("Serif", Font.BOLD, 14));
    Border border = head.getBorder();
    Border margin = new EmptyBorder(5, 5, 5, 5);
    head.setBorder(new CompoundBorder(border, margin));
    this.helpPane.add(head);
    if (op.length() > 0) {
      if (op.equals("name")) {
      }
      else if (op.equals("description")) {
      }
      else {
        head = new JLabel(Options.getMsg("dialog.adddomain." + op + ".text"));
        head.setFont(new Font("Serif", Font.PLAIN, 14));
        margin = new EmptyBorder(5, 10, 5, 5);
        head.setBorder(new CompoundBorder(border, margin));
        this.helpPane.add(head);
        IconFactory iconFactory = new IconFactory();
        ImageIcon icon = iconFactory.createImageIcon("/images/" + op + ".png");
        this.helpPane.add(new JLabel(icon));
      }
    }
    else {
      head = new JLabel(Options.getMsg("dialog.addintdomain.syntax"));
      head.setFont(new Font("Serif", Font.PLAIN, 14));
      margin = new EmptyBorder(5, 10, 5, 5);
      head.setBorder(new CompoundBorder(border, margin));
      this.helpPane.add(head);
    }
    this.helpPane.revalidate();
    this.helpPane.repaint();
  }

  private AdtIntDomain      domain;
  private JTextField        name;
  private JTextField        description;
  private JTextField        ao;
  private JTextField        ap;
  private JTextField        oo;
  private JTextField        op;
  private JTextField        cp;
  private JTextField        co;
  private JTextField        defaulto;
  private JTextField        defaultp;
  private JCheckBox         modifiableo;
  private JCheckBox         modifiablep;
  private JButton           okButton;
  private JLabel            errorLabel;
  private JPanel            helpPane;

  private static Color      validColor       = new Color(170, 255, 170);
  private static Color      invalidColor     = new Color(255, 170, 170);
  private static final long serialVersionUID = 3852122176888687082L;
}
