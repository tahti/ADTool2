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
package lu.uni.adtool.ui;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.ADTNode;
import lu.uni.adtool.tree.ADTParser;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.tree.Parser;
import lu.uni.adtool.tree.SandNode;
import lu.uni.adtool.tree.SandParser;
import lu.uni.adtool.treeconverter.ADEulerTree;
import lu.uni.adtool.treeconverter.SandEulerTree;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

public class TermView extends JPanel {

  public TermView(SandTreeCanvas<?> canvas) {
    super(new BorderLayout());
    this.canvas = canvas;
    initLayout(canvas.getTermsString());
  }

  public TermView(ADTreeCanvas<?> canvas) {
    super(new BorderLayout());
    this.canvas = canvas;
    initLayout(canvas.getTermsString());
  }

  /**
   * Recalculate the terms based on new tree.
   *
   */
  public void updateTerms() {
    this.editTerms.setText(canvas.getTermsString());
  }

  public void setFocus(Node n) {
    // do nothing
  }

  /**
   * Initialize the layout of a panel.
   *
   */
  private void initLayout(String termString) {
    editTerms = new JTextArea(termString);
    editTerms.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        editTerms.getHighlighter().removeAllHighlights();
        validate.setEnabled(editTerms.getText().length() > 0);
      }

      public void removeUpdate(DocumentEvent e) {
        editTerms.getHighlighter().removeAllHighlights();
        validate.setEnabled(editTerms.getText().length() > 0);
      }

      public void insertUpdate(DocumentEvent e) {
        editTerms.getHighlighter().removeAllHighlights();
        validate.setEnabled(editTerms.getText().length() > 0);
      }
    });
    errorOutput = new JTextArea();
    errorOutput.setEditable(false);
    JScrollPane errorScroll = new JScrollPane(errorOutput);
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, errorScroll, new JScrollPane(editTerms));
    splitPane.setOneTouchExpandable(true);
    splitPane.setResizeWeight(0);
    splitPane.setDividerLocation(0.0);
    add(splitPane);
    add(createButtonPane(), BorderLayout.PAGE_END);

    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK),
        "doValidate");
    Action pressedAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        parse();
      }

      private static final long serialVersionUID = -4345934200261947700L;

    };
    getActionMap().put("doValidate", pressedAction);
  }

  /**
   * Initialise the button panel.
   *
   * @return created button panel.
   */
  private JPanel createButtonPane() {
    JPanel buttonPane = new JPanel();
    this.validate = new JButton(Options.getMsg("window.termView.validate"));
    this.revert = new JButton(Options.getMsg("window.termView.revert"));
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
    buttonPane.add(validate);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(revert);
    this.revert.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Execute when button is pressed
        revert();
      }
    });
    this.validate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Execute when button is pressed
        parse();
      }
    });
    this.revert.setEnabled(false);
    return buttonPane;
  }

  private void revert() {
    editTerms.setText(canvas.getTermsString());
    revert.setEnabled(false);
    splitPane.setDividerLocation(0.0);
    errorOutput.setText("");
  }

  public void parse() {
    NodeTree tree = canvas.getTree();
    Node newRoot = tree.getRoot(true);
    Parser parser = null;
    if (newRoot instanceof SandNode) {
      parser = new SandParser();
    }
    else if (newRoot instanceof ADTNode) {
      parser = new ADTParser();
    }
    if (editTerms.getText().length() > 0) {
      newRoot = (Node)parser.parseString(editTerms.getText());
      if (newRoot == null) {
        // parsing failed - display error messages
        this.handleError(parser);
      }
      else {
        revert.setEnabled(false);
        // transfer new tree to the canvas
        // get new formated
        // editTerms.setText(newRoot.toTerms());
        errorOutput.setText("");
        if (newRoot instanceof SandNode) {
          SandEulerTree et = new SandEulerTree();
          et.transferLabels((SandNode) newRoot, ((SandTreeCanvas<?>) canvas).getRoot());
          ((SandTreeCanvas<?>) canvas).setRoot((SandNode) newRoot);
        }
        else {
          ADEulerTree et = new ADEulerTree();
          et.transferLabels((ADTNode) newRoot, ((ADTreeCanvas<?>) canvas).getRoot());
          ((ADTreeCanvas<?>) canvas).setRoot((ADTNode) newRoot);
        }
        tree.getLayout().refreshValues();
        // canvas.getMainWindow().getStatusBar().report("Validation of terms was
        // successful");
      }
    }
  }

  private void handleError(Parser parser) {
    errorOutput.setText(parser.getErrorMessage());
    // canvas.getMainWindow().getStatusBar().reportError("Validation of terms
    // was not possible: " + m);
    splitPane.setDividerLocation(Math.max(1 / 2,
        (errorOutput.getPreferredSize().getHeight() + 5) / splitPane.getSize().height));
    try {
      int offset = parser.getStartErr();
      int endOffset = parser.getEndErr();
      editTerms.scrollRectToVisible(editTerms.modelToView(offset));
      editTerms.setCaretPosition(endOffset);
      highlight(offset, endOffset);
      editTerms.requestFocus();
    }
    catch (BadLocationException err) {// TODO - update status
      // canvas.getMainWindow().getStatusBar().reportError(err.getLocalizedMessage());
    }
    revert.setEnabled(true);
  }

  private void highlight(int startPos, int endPos) {
    if (startPos == endPos) {
      if (startPos > 0) startPos--;
    }
    DefaultHighlighter.DefaultHighlightPainter highlightPainter =
        new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
    try {
      editTerms.getHighlighter().addHighlight(startPos, endPos, highlightPainter);
    }
    catch (BadLocationException err) { // TODO set update method
      // canvas.getMainWindow().getStatusBar().reportError(err.getLocalizedMessage());
    }
  }

  static final long          serialVersionUID = 17266535905153654L;
  private JTextArea          errorOutput;
  private JSplitPane         splitPane;
  private JTextArea          editTerms;
  private AbstractTreeCanvas canvas;
  private JButton            revert;
  private JButton            validate;
}
