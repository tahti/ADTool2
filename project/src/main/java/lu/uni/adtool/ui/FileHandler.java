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
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Provides static methods for choosing file to open or save.
 *
 * @author Piotr Kordy
 */
public class FileHandler {

  /**
   * Constructs a new instance.
   */
  public FileHandler(MainController controller) {
    this.treeFileName = null;
    this.tempFileName = "";
    this.controller = controller;
    this.initFileChooser();
  }

  /**
   * Shows save dialog.
   *
   * @return stream to wchich we can write or null.
   */
  public ObjectOutputStream getSaveTreeStream() {
    fc.setAcceptAllFileFilterUsed(true);
    FileFilter filter = new FileNameExtensionFilter("ADTree file", "adt", "ADT", "Adt");
    saveLayout.setVisible(true);
    exportDomains.setVisible(false);
    exportRanking.setVisible(false);
    exportCalculatedValues.setVisible(false);
    fc.setSelectedFile(new File(getTreeFileNameWithExt("adt")));
    fc.setDialogTitle("Save Attack Defence Tree...");
    ObjectOutputStream result = getSaveStream(filter);
    if (result != null) {
      Options.fc_save_file = tempFileName;
    }
    return result;
  }

  /**
   * Shows export dialog.
   *
   * @return stream to which we can write or null.
   */
  public FileOutputStream getExportTreeStream(String extension, AbstractTreeCanvas canvas) {
    FileFilter filter = null;
    if (canvas == null) return null;
    if (canvas.getTree() == null) return null;
    if (canvas.getTree().getLayout() == null) return null;
    int id = canvas.getTree().getLayout().getTreeId();
    exportDomains.setVisible(false);
    exportRanking.setVisible(false);
    exportCalculatedValues.setVisible(false);
    saveLayout.setVisible(false);
    if (extension.equals("pdf")) {
      filter = new FileNameExtensionFilter("Pdf file", "pdf", "PDF", "Pdf");
    }
    else if (extension.equals("xml")) {
      filter = new FileNameExtensionFilter("XML file", "xml", "XML", "Xml");
      ArrayList<DomainDockable> domains = controller.getFrame().getDomainFactory().getDomains(id);
      if (domains != null && domains.size() > 0) {
        exportDomains.setVisible(true);
        exportRanking.setVisible(true);
        exportCalculatedValues.setVisible(true);
      }
    }
    else if (extension.equals("png")) {
      filter = new FileNameExtensionFilter("PNG image", "png", "PNG", "Png");
    }
    else if (extension.equals("jpg")) {
      filter =
          new FileNameExtensionFilter("JPEG image", "jpg", "JPG", "Jpg", "jpeg", "JPEG", "Jpeg");
    }
    else if (extension.equals("tex")) {
      filter = new FileNameExtensionFilter("LaTeX file", "tex", "TEX", "Tex", "latex", "LATEX",
          "LaTeX", "Latex");
    }
    fc.setDialogTitle(Options.getMsg("filehandler.export.title"));
    FileOutputStream out = null;
    fc.setSelectedFile(new File(getTreeFileNameWithExt(extension)));
    fc.resetChoosableFileFilters();
    fc.setAcceptAllFileFilterUsed(false);
    fc.setFileFilter(filter);
    int returnVal = fc.showSaveDialog(this.controller.getFrame());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      try {
        out = new FileOutputStream(file);
        tempFileName = file.getName();
        Options.fc_save_file = tempFileName;
        controller.report(Options.getMsg("filehandler.exported.msg") + " " + file.getName() + ".");
      }
      catch (FileNotFoundException e) {
        controller.reportError(
            Options.getMsg("filehandler.filenotfound") + " \"" + file.getName() + "\".");
      }
      // catch (IOException e) {
      // controller.reportError("There was IO problem opening a
      // file:\""+file.getName()+"\".");
      // }
    }
    else {
      controller.reportWarning(Options.getMsg("filehandler.exportcancelled"));
    }
    return out;
  }

  /**
   * Shows import dialog.
   *
   * @return stream from which we can read or null.
   */
  public FileInputStream getImportTreeStream(String extension) {
    FileInputStream in = null;
    FileFilter filter = null;
    if (extension.equals("xml")) {
      filter = new FileNameExtensionFilter("Xml file", "xml", "XML", "Xml");
    }
    else if (extension.equals("txt")) {
      filter = new FileNameExtensionFilter("AD Term file", "txt", "TXT", "Txt");
    }
    exportDomains.setVisible(false);
    exportRanking.setVisible(false);
    exportCalculatedValues.setVisible(false);
    saveLayout.setVisible(false);
    if (extension.equals("xml")) {
      fc.setDialogTitle(Options.getMsg("file.import.dialog.xml"));
    }
    else if (extension.equals("txt")) {
      fc.setDialogTitle(Options.getMsg("file.import.dialog.txt"));
    }
    fc.setSelectedFile(new File(getTreeFileNameWithExt(extension)));
    fc.resetChoosableFileFilters();
    fc.setFileFilter(filter);
    int returnVal = fc.showOpenDialog(this.controller.getFrame());

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      tempFileName = file.getName();
      Options.fc_save_file = tempFileName;
      // This is where a real application would open the file.
      try {
        in = new FileInputStream(file);
        controller.report(Options.getMsg("filehandler.imported") + " " + file.getName() + ".");
      }
      catch (FileNotFoundException e) {
        controller.reportError(
            Options.getMsg("filehandler.filenotfound") + " \"" + file.getName() + "\".");
      }
      // catch (IOException e) {
      // controller.reportError("There was IO problem opening a
      // file:\""+file.getName()+"\"");
      // }
    }
    else {
      controller.reportWarning(Options.getMsg("filehandler.opencancelled"));
    }
    return in;
  }

  /**
   * Shows save dialog.
   *
   * @return stream to which we can write or null.
   */
  public ObjectOutputStream getSaveLayoutStream() {
    fc.setAcceptAllFileFilterUsed(true);
    FileFilter filter = new FileNameExtensionFilter("Layout file", "adl", "ADL", "Adl");
    exportDomains.setVisible(false);
    exportRanking.setVisible(false);
    exportCalculatedValues.setVisible(false);
    saveLayout.setVisible(false);
    fc.setSelectedFile(new File(getTreeFileNameWithExt("adl")));
    fc.setDialogTitle("Save Layout...");
    return getSaveStream(filter);
  }

  /**
   * Shows save dialog.
   *
   * @return stream to wchich we can write or null.
   */
  public ObjectOutputStream getSaveStream(FileFilter filter) {
    ObjectOutputStream out = null;
    fc.resetChoosableFileFilters();
    fc.setFileFilter(filter);
    int returnVal = fc.showSaveDialog(this.controller.getFrame());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      // This is where a real application would open the file.
      try {
        out = new ObjectOutputStream(new FileOutputStream(file));
        tempFileName = file.getName();
        controller.report(Options.getMsg("filehandler.savedto") + " " + file.getName() + ".");
      }
      catch (FileNotFoundException e) {
        controller.reportError(
            Options.getMsg("filehandler.filenotfound") + " \"" + file.getName() + "\".");
      }
      catch (IOException e) {
        controller.reportError("There was IO problem opening a file:\"" + file.getName() + "\".");
      }
    }
    else {
      controller.reportWarning("Save command cancelled by the user.");
    }
    return out;
  }

  /**
   * Shows open file dialog.
   *
   * @return Stream from which we can read or null if no file was open.
   */
  public ObjectInputStream getLoadTreeStream() {
    FileFilter filter = new FileNameExtensionFilter("Tree file", "adt", "ADT", "Adt");
    exportDomains.setVisible(false);
    exportRanking.setVisible(false);
    exportCalculatedValues.setVisible(false);
    saveLayout.setVisible(false);
    fc.setSelectedFile(new File(getTreeFileNameWithExt("adt")));
    fc.setDialogTitle("Load Atack Defence Tree...");
    ObjectInputStream result = getLoadStream(filter);
    if (result != null) {
      Options.fc_save_file = tempFileName;
    }
    return result;
  }

  /**
   * Shows open file dialog.
   *
   * @return Stream from which we can read or null if no file was open.
   */
  public ObjectInputStream getLoadLayoutStream() {
    FileFilter filter = new FileNameExtensionFilter("Layout file", "adl", "ADL", "Adl");
    fc.setDialogTitle("Load Layout...");
    fc.setSelectedFile(new File(getTreeFileNameWithExt("adl")));
    exportDomains.setVisible(false);
    exportRanking.setVisible(false);
    exportCalculatedValues.setVisible(false);
    saveLayout.setVisible(false);
    return getLoadStream(filter);
  }

  /**
   * Shows open file dialog.
   *
   * @return Stream from which we can read or null if no file was open.
   */
  private ObjectInputStream getLoadStream(FileFilter filter) {
    ObjectInputStream in = null;
    fc.resetChoosableFileFilters();
    fc.setFileFilter(filter);
    int returnVal = fc.showOpenDialog(this.controller.getFrame());

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      tempFileName = file.getName();
      // This is where a real application would open the file.
      try {
        in = new ObjectInputStream(new FileInputStream(file));
        controller.report("Loaded tree from file: " + file.getName() + ".");
      }
      catch (FileNotFoundException e) {
        controller.reportError(
            Options.getMsg("filehandler.filenotfound") + " \"" + file.getName() + "\".");
      }
      catch (IOException e) {
        controller.reportError("There was IO problem opening a file:\"" + file.getName() + "\"");
      }
    }
    else {
      controller.reportWarning("Open command cancelled by the user.");
    }
    return in;
  }

  /**
   * Initializes fileChooser
   *
   * @return new checkbox
   */
  private void initFileChooser() {
    JPanel jp = new JPanel();
    fc = new JFileChooser() {

      public void approveSelection() {
        File f = getSelectedFile();
        if (f.exists() && getDialogType() == SAVE_DIALOG) {
          int result = OptionPane.showYNCDialog(controller.getFrame(),
              "The file exists, overwrite?", "Existing file");
          switch (result) {
          case JOptionPane.YES_OPTION:
            super.approveSelection();
            return;
          case JOptionPane.NO_OPTION:
            return;
          case JOptionPane.CLOSED_OPTION:
            return;
          case JOptionPane.CANCEL_OPTION:
            cancelSelection();
            return;
          }
        }
        super.approveSelection();
      };

      private static final long serialVersionUID = 7266914590804770955L;
    };

    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    // Add "show hidden files" CheckBoxMenuItem
    JCheckBox showHiddenCheckBox = new JCheckBox();
    exportDomains = new JCheckBox();
    exportDomains.setText(Options.getMsg("file.export.xml.include"));
    exportDomains.setSelected(Options.main_saveDomains);
    exportDomains.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        Options.main_saveDomains = source.isSelected();
        if (!Options.main_saveDomains) {
          Options.main_saveDerivedValues = false;
          Options.main_saveRanking = false;
          exportCalculatedValues.setSelected(Options.main_saveDerivedValues);
          exportRanking.setSelected(Options.main_saveRanking);
        }
        exportCalculatedValues.setEnabled(Options.main_saveDomains);
        exportRanking.setEnabled(Options.main_saveDomains);
      }
    });
    exportRanking = new JCheckBox();
    exportRanking.setText(Options.getMsg("file.export.xml.ranking"));
    exportRanking.setSelected(Options.main_saveRanking);
    exportRanking.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        Options.main_saveRanking= source.isSelected();
      }
    });

    exportCalculatedValues = new JCheckBox();
    exportCalculatedValues.setText(Options.getMsg("file.export.xml.derived"));
    exportCalculatedValues.setSelected(Options.main_saveDerivedValues);
    exportCalculatedValues.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        Options.main_saveDerivedValues = source.isSelected();
      }
    });
    saveLayout = new JCheckBox();
    saveLayout.setText(Options.getMsg("file.export.layout"));
    saveLayout.setSelected(Options.main_saveLayout);
    saveLayout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        Options.main_saveLayout = source.isSelected();
      }
    });

    showHiddenCheckBox.setText(Options.getMsg("file.export.hidden.txt"));
    showHiddenCheckBox.setMnemonic(KeyStroke.getKeyStroke(Options.getMsg("file.export.hidden.key")).getKeyCode());
    showHiddenCheckBox.setSelected(!fc.isFileHidingEnabled());
    showHiddenCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JCheckBox source = (JCheckBox) e.getSource();
        boolean showHidden = source.isSelected();
        // fc.firePropertyChange(JFileChooser.FILE_HIDING_CHANGED_PROPERTY,
        // showHidden, !showHidden);
        fc.setFileHidingEnabled(!showHidden);
      }
    });
    // fc.setFileHidingEnabled(false);
    jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
    saveLayout.setVisible(true);
    jp.add(saveLayout);
    jp.add(showHiddenCheckBox);
    jp.add(exportDomains);
    jp.add(exportCalculatedValues);
    jp.add(exportRanking);
    fc.setAccessory(jp);
  }


  private String getTreeFileNameWithExt(String ext) {
    String s = getTreeFileNameForStream();
    if (s.contains(".")) {
      return s.substring(0, s.lastIndexOf('.')) + '.' + ext;
    }
    else {
      return s + '.' + ext;
    }
  }

  /**
   * Get the name of the file that is used in load/save/export dialogs. If
   * treeFileName is null then return the name of the root
   *
   * @return name used in load/save dialogs
   */
  private String getTreeFileNameForStream() {
    if (Options.fc_save_file != null) {
      return Options.fc_save_file;
    }
    else {
      String s;
      if ( controller.getLastFocusedTree() != null && controller.getLastFocusedTree().getTree() != null) {
        s = controller.getLastFocusedTree().getRootLabel();
      }
      else {
        s = "Tree";
      }
      // s=s.replace(' ','_')+".adt";
      s = s.replace(' ', '_');
      // return s.replace('\n','_')+".adt";
      return new File(new File(System.getProperty("user.dir")), s.replace('\n', '_')).toString();
    }
  }

  protected MainController controller;
  protected String         treeFileName;
  protected JFileChooser   fc;
  protected JCheckBox      saveLayout;
  protected JCheckBox      exportDomains;
  protected JCheckBox      exportRanking;
  protected JCheckBox      exportCalculatedValues;
  private String           tempFileName;

}
