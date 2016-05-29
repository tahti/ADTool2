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
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;

public class AddIntDomainDialog extends JDialog implements ActionListener, DocumentListener {

  public enum FieldType {
    AP, AO, OP, OO, CP, CO, NAME, DESCR, NONE, DEFAULTP, DEFAULTO
  }

  public AddIntDomainDialog(Frame frame, AdtIntDomain domain) {
    super(frame, Options.getMsg("adtdomain.custom.dialogtitle"), true);
    this.domain = domain;
    setAlwaysOnTop(true);
    this.setLocationRelativeTo(frame);
    setLocation(160, 160);
    setSize(800, 600);
  }

  public AdtIntDomain showDialog() {
    this.createLayout();
    this.setVisible(true);
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
    this.name = new JTextField();
    this.description = new JTextField();
    this.cp = new JTextField();
    this.co = new JTextField();
    this.ap = new JTextField();
    this.ao = new JTextField();
    this.op = new JTextField();
    this.oo = new JTextField();
    this.defaulto = new JTextField("");
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

    this.defaultp = new JTextField("");
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
    this.modifiableo.setSelected(true);
    this.modifiablep = new JCheckBox(Options.getMsg("dialog.adddomain.modifiablep"));
    this.modifiablep.setSelected(true);
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

    this.defaultp.setBackground(invalidColor);
    this.defaulto.setBackground(invalidColor);
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
      try {
        Integer.parseInt(field.getText());
        field.setBackground(validColor);
      } catch (NumberFormatException e) {
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
    if (this.name.getBackground() == AddIntDomainDialog.validColor
        && this.defaultp.getBackground() == AddIntDomainDialog.validColor
        && this.defaulto.getBackground() == AddIntDomainDialog.validColor
        && this.description.getBackground() == AddIntDomainDialog.validColor
        && this.ao.getBackground() == AddIntDomainDialog.validColor
        && this.ap.getBackground() == AddIntDomainDialog.validColor
        && this.oo.getBackground() == AddIntDomainDialog.validColor
        && this.op.getBackground() == AddIntDomainDialog.validColor
        && this.co.getBackground() == AddIntDomainDialog.validColor
        && this.cp.getBackground() == AddIntDomainDialog.validColor) {
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
      this.setHelpText("desc");
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
      } else if (op.equals("desc")) {
      } else {
        head = new JLabel(Options.getMsg("dialog.adddomain." + op + ".text"));
        head.setFont(new Font("Serif", Font.PLAIN, 14));
        margin = new EmptyBorder(5, 10, 5, 5);
        head.setBorder(new CompoundBorder(border, margin));
        this.helpPane.add(head);
        IconFactory iconFactory = new IconFactory();
        ImageIcon icon = iconFactory.createImageIcon("/images/" + op + ".png");
        this.helpPane.add(new JLabel(icon));
      }
    } else {
      head = new JLabel(Options.getMsg("dialog.addintdomain.syntax"));
      head.setFont(new Font("Serif", Font.PLAIN, 14));
      margin = new EmptyBorder(5, 10, 5, 5);
      head.setBorder(new CompoundBorder(border, margin));
      this.helpPane.add(head);
    }
    this.helpPane.revalidate();
    this.helpPane.repaint();
  }

  private AdtIntDomain domain;
  private JTextField name;
  private JTextField description;
  private JTextField ao;
  private JTextField ap;
  private JTextField oo;
  private JTextField op;
  private JTextField cp;
  private JTextField co;
  private JTextField defaulto;
  private JTextField defaultp;
  private JCheckBox modifiableo;
  private JCheckBox modifiablep;
  private JButton okButton;
  private JLabel errorLabel;
  private JPanel helpPane;

  private static Color validColor = new Color(170, 255, 170);
  private static Color invalidColor = new Color(255, 170, 170);
  private static final long serialVersionUID = 3852122176888687082L;
}
