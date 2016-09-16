package lu.uni.adtool.ui;

import lu.uni.adtool.domains.custom.IntParser;
import lu.uni.adtool.domains.custom.SandIntDomain;
import lu.uni.adtool.domains.rings.Int;
import lu.uni.adtool.tools.IconFactory;
import lu.uni.adtool.tools.Options;

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

public class AddIntSandDomDialog extends JDialog implements ActionListener, DocumentListener {


  public enum FieldType {
    OR, AND, SAND, NAME, DESCR, NONE, DEFAULT
  }

  public AddIntSandDomDialog(Frame frame, SandIntDomain domain) {
    super(frame, Options.getMsg("adtdomain.custom.dialogtitle"), true);
    this.domain = domain;
    setAlwaysOnTop(true);
    this.setLocationRelativeTo(frame);
    setLocation(160, 160);
    setSize(800, 600);
  }

  public SandIntDomain showDialog() {
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
    this.or = new JTextField(this.domain.getOr());
    this.and = new JTextField(this.domain.getAnd());
    this.sand = new JTextField(this.domain.getSand());
    this.defaultValue = new JTextField(this.domain.getDefault());
    this.defaultValue.getDocument().addDocumentListener(this);
    this.defaultValue.getDocument().putProperty("parent", defaultValue);
    this.defaultValue.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.DEFAULT);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });

    this.name.getDocument().addDocumentListener(this);
    this.description.getDocument().addDocumentListener(this);
    this.or.getDocument().addDocumentListener(this);
    this.or.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.OR);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.and.getDocument().addDocumentListener(this);
    this.and.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.AND);
      }

      public void focusLost(FocusEvent e) {
        setHelpContent(FieldType.NONE);
      }
    });
    this.sand.getDocument().addDocumentListener(this);
    this.sand.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        setHelpContent(FieldType.SAND);
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
    this.or.getDocument().putProperty("parent", or);
    this.and.getDocument().putProperty("parent", and);
    this.sand.getDocument().putProperty("parent", sand);
    try {
      Integer.parseInt(this.defaultValue.getText());
      this.defaultValue.setBackground(validColor);
    }
    catch (NumberFormatException e) {
      this.defaultValue.setBackground(invalidColor);
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
    checkValid(this.or);
    checkValid(this.and);
    checkValid(this.sand);

    JPanel inputContent = new JPanel();
    inputContent.setLayout(new GridLayout(0, 2, 10, 10));
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.defaultValue")));
    inputContent.add(this.defaultValue);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.name")));
    inputContent.add(this.name);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.description")));
    inputContent.add(this.description);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.or")));
    inputContent.add(this.or);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.and")));
    inputContent.add(this.and);
    inputContent.add(new JLabel(Options.getMsg("dialog.adddomain.sand")));
    inputContent.add(this.sand);
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
    if ((!this.domain.setName(this.name.getText())) || (!this.domain.setOr(this.or.getText()))
        || (!this.domain.setAnd(this.and.getText())) || (!this.domain.setSand(this.sand.getText()))
        || (!domain.setDefault(this.defaultValue.getText()))
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
    } else if (field == this.defaultValue) {
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
    if (this.name.getBackground() == AddIntSandDomDialog.validColor
        && this.defaultValue.getBackground() == AddIntSandDomDialog.validColor
        && this.description.getBackground() == AddIntSandDomDialog.validColor
        && this.or.getBackground() == AddIntSandDomDialog.validColor
        && this.and.getBackground() == AddIntSandDomDialog.validColor
        && this.sand.getBackground() == AddIntSandDomDialog.validColor) {
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
    case OR:
      this.setHelpText("or");
      break;
    case AND:
      this.setHelpText("and");
      break;
    case SAND:
      this.setHelpText("sand");
      break;
    case NAME:
      this.setHelpText("name");
      break;
    case DESCR:
      this.setHelpText("description");
      break;
    case DEFAULT:
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

  private SandIntDomain     domain;
  private JTextField        name;
  private JTextField        description;
  private JTextField        or;
  private JTextField        and;
  private JTextField        sand;
  private JTextField        defaultValue;
  private JButton           okButton;
  private JLabel            errorLabel;
  private JPanel            helpPane;

  private static Color      validColor       = new Color(170, 255, 170);
  private static Color      invalidColor     = new Color(255, 170, 170);
  private static final long serialVersionUID = 5891853937349452934L;
}
