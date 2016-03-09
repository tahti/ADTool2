package lu.uni.adtool.ui;

import lu.uni.adtool.ADToolMain;
import lu.uni.adtool.tools.Options;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog with list of all examples.
 *
 * @author Piot Kordy
 */
public class LoadExampleDialog extends JDialog implements ActionListener, ListSelectionListener {
  private static final long serialVersionUID = 1223325877545646416L;
  private ADToolMain        frame;
  private JButton           setButton;
  private JList             list;
  private JLabel            description;
  private final String[]    examplesList     = {Options.getMsg("example.auctionfraud.txt"),
      Options.getMsg("example.rfiddos.txt"), Options.getMsg("example.rfidblock.txt"),
      Options.getMsg("example.breakwarehouse.txt"), Options.getMsg("example.rfidwarehouse.txt"),
      Options.getMsg("example.data.txt"), Options.getMsg("example.bankaccount.txt")};

  // "Breaking into a Warehouse",
  // "RFID Dos Attack in Warehouse", "Data Confidentiality", "Bank Account"};
  private final String[]    examplesFileName =
      {"/examples/AuctionFraud.xml", "/examples/RFIDDos.xml", "/examples/RFIDBlock.xml",
          "/examples/BreakingWarehouse.xml", "/examples/RFIDWarehouse.xml",
          "/examples/DataConfidentiality.xml", "/examples/BankAccount.xml"};

  private final String[]    examplesDesc     = {Options.getMsg("example.auctionfraud.desc"),
      Options.getMsg("example.rfiddos.desc"), Options.getMsg("example.rfidblock.desc"),
      Options.getMsg("example.breakwarehouse.desc"), Options.getMsg("example.rfidwarehouse.desc"),
      Options.getMsg("example.data.desc"), Options.getMsg("example.bankaccount.desc")};
  private String            result;

  /**
   * Constructs a new instance.
   */
  public LoadExampleDialog(final ADToolMain parentFrame) {
    super(parentFrame, Options.getMsg("example.title"), true);
    frame = parentFrame;
    setAlwaysOnTop(true);
    this.setLocationRelativeTo(frame);
    setLocation(60, 60);
  }

  /**
   * Show dialog where user chooses a predefined domain.
   *
   * @return return file name from which to read the tree.
   *
   */
  public String showDialog() {
    createLayout();
    this.setVisible(true);
    return result;
    // return chosenDomain;
  }

  /**
   * {@inheritDoc}
   *
   * @see ActionListener#actionPerformed(ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    if (Options.getMsg("button.cancel").equals(e.getActionCommand())) {
      setValue(-1);
    }
    dispose();
  }

  private void createLayout() {
    JButton cancelButton = new JButton(Options.getMsg("button.cancel"));
    cancelButton.addActionListener(this);
    setButton = new JButton(Options.getMsg("button.load"));
    setButton.setActionCommand(Options.getMsg("button.load"));
    setButton.addActionListener(this);
    getRootPane().setDefaultButton(setButton);
    list = new JList(examplesList) {
      // Subclass JList to workaround bug 4832765, which can cause the
      // scroll pane to not let the user easily scroll up to the beginning
      // of the list. An alternative would be to set the unitIncrement
      // of the JScrollBar to a fixed value. You wouldn't get the nice
      // aligned scrolling, but it should work.
      public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        int row;
        if (orientation == SwingConstants.VERTICAL && direction < 0
            && (row = getFirstVisibleIndex()) != -1) {
          Rectangle r = getCellBounds(row, row);
          if ((r.y == visibleRect.y) && (row != 0)) {
            Point loc = r.getLocation();
            loc.y--;
            int prevIndex = locationToIndex(loc);
            Rectangle prevR = getCellBounds(prevIndex, prevIndex);

            if (prevR == null || prevR.y >= r.y) {
              return 0;
            }
            return prevR.height;
          }
        }
        return super.getScrollableUnitIncrement(visibleRect, orientation, direction);
      }

      private static final long serialVersionUID = -5216109258424750213L;
    };
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setVisibleRowCount(15);
    list.setSelectedIndex(0);
    list.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          setButton.doClick(); // emulate button click
        }
      }
    });
    list.addListSelectionListener(this);
    JScrollPane listScroller = new JScrollPane(list);
    listScroller.setPreferredSize(new Dimension(350, 80));
    listScroller.setAlignmentX(LEFT_ALIGNMENT);
    // Create a container so that we can add a title around
    // the scroll pane. Can't add a title directly to the
    // scroll pane because its background would be white.
    // Lay out the label and scroll pane from top to bottom.
    JPanel listPane = new JPanel();
    listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
    JLabel label = new JLabel(Options.getMsg("example.examples"));
    label.setLabelFor(list);
    listPane.add(label);
    listPane.add(Box.createRigidArea(new Dimension(0, 5)));
    listPane.add(listScroller);
    listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    description = new JLabel(Options.getMsg("example.noinit")) {

      public Dimension getPreferredSize() {
        return new Dimension(400, 300);
      }

      public Dimension getMinimumSize() {
        return new Dimension(400, 300);
      }

      public Dimension getMaximumSize() {
        return new Dimension(400, 300);
      }
      private static final long serialVersionUID = -6683205667737229246L;
    };
    description.setVerticalAlignment(SwingConstants.TOP);
    description.setFont(new Font("Sans", Font.TRUETYPE_FONT, 13));
    description.setHorizontalAlignment(SwingConstants.LEFT);

    JScrollPane descPane = new JScrollPane(description);
    descPane.setBorder(
        BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(Options.getMsg("example.description"))));

    // Lay out the buttons from left to right.
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    buttonPane.add(Box.createHorizontalGlue());
//     buttonPane.add(new JLabel("<html><b>Warning! Current tree will be discarded!</b></html>"));
    buttonPane.add(cancelButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(setButton);
    // Put everything together, using the content pane's BorderLayout.
    Container contentPane = getContentPane();
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
    // GridBagConstraints c = new GridBagConstraints();
    //// content.setBackground(Options.printview_background);
    // c.insets = new Insets(0,8,0,0);
    // c.gridx = 0;
    // c.gridy = 0;
    content.add(listPane);
    // c.gridx = 1;
    content.add(descPane);
    contentPane.add(content, BorderLayout.CENTER);
    contentPane.add(buttonPane, BorderLayout.PAGE_END);
    // Initialize values.
    setValue(0);

    pack();
  }

  /**
   * {@inheritDoc}
   *
   * @see ListSelectionListener#valueChanged(ListSelectionEvent)
   */
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting() == false) {

      if (list.getSelectedIndex() == -1) {
        // No selection, disable fire button.
        setButton.setEnabled(false);
      }
      else {
        // Selection, enable the fire button.
        setValue(list.getSelectedIndex());
        setButton.setEnabled(true);
      }
    }
  }

  private void setValue(int i) {
    if (0 <= i && i < examplesFileName.length) {
      result = examplesFileName[i];
      list.setSelectedValue(examplesList[i], true);
      description.setText("<html>" + examplesDesc[i] + "<br></html>");
    }
    else {
      result = null;
      description.setText(Options.getMsg("example.noexample"));
    }
  }
}
