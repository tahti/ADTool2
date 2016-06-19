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
package lu.uni.adtool.tools;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.tree.TreeLayout;
import lu.uni.adtool.tree.XmlConverter;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;
/**
 * Class used by command line importing/exporting
 */
public class ImportExport {
  public ImportExport() {
    exportAllDomains = false;
    this.exportDomain = -1;
    markEditable = false;
    noLabels = false;
    noComputedValues = false;
    noDerivedValues = false;
    this.exportDomainStr = null;
    this.viewPortSize = null;
  }

  /**
   * Function used to import from command line only - no GUI window created
   */
  public boolean doImport(String fileName) {
    InputStream fileStream = null;
    try {
      fileStream = new FileInputStream(new File(fileName));
    }
    catch (FileNotFoundException e) {
      System.err.println(Options.getMsg("error.xmlimport.fail") + " " + e.getLocalizedMessage());
      return false;
    }
    try {
      if (fileStream != null) {
        BufferedInputStream in = new BufferedInputStream(fileStream);
        XElement element = null;
        element = XIO.readUTF(in);
        in.close();
        if (element == null) return false;
        this.treeLayout = new TreeLayout(1);
        this.treeLayout.importXml(element, 1);
        if (this.exportDomainStr != null) {
          try {
            this.exportDomain = Integer.parseInt(this.exportDomainStr);
          }
          catch (NumberFormatException exception) {
            this.exportDomain = 0;
            int i = 1;
            for (XElement domain : element.getElements("domain")) {
              String domainId = domain.getString("id");
              if (this.exportDomainStr != null && this.exportDomainStr.equals(domainId)) {
                this.exportDomain = i;
                break;
              }
              i++;
            }
          }
        }
        else {
          this.exportDomain = 0;
        }
      }
      fileStream.close();
      return true;
    }
    catch (IllegalArgumentException e) {
      System.err.println(Options.getMsg("error.xmlimport.fail") + " " + e.getLocalizedMessage());
      this.treeLayout = null;
      return false;
    }
    catch (IOException e) {
      System.err.println(Options.getMsg("error.xmlimport.fail") + " " + e.getLocalizedMessage());
      this.treeLayout = null;
      return false;
    }
  }


  @SuppressWarnings("unchecked")
  public void doExport(String fileName) {
    String ext = this.getExtension(fileName);
    if (treeLayout == null || ext == null) {
      Debug.log("tree null:");
      return;
    }
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(fileName);
    }
    catch (FileNotFoundException e) {
      System.err.println(Options.getMsg("clo.export.fail") + e.getLocalizedMessage());
      return;
    }
    if (ext.toLowerCase().equals("xml")) {
      this.exportXml(out, this.treeLayout);
    }
    else if (ext.toLowerCase().equals("txt")) {
      AbstractTreeCanvas canvas = null;
      if (treeLayout.isSand()) {
        canvas = new SandTreeCanvas<Ring>(new NodeTree(treeLayout));
      }
      else {
        canvas = new ADTreeCanvas<Ring>(new NodeTree(treeLayout));
      }
      try {
        if (this.viewPortSize != null) {
          canvas.setViewPortSize(this.viewPortSize);
          canvas.fitToWindow();
        }
        canvas.createTxt(out);
      }
      catch (IOException e) {
        System.err.println(Options.getMsg("clo.export.fail") + e.getLocalizedMessage());
      }
    }
    else if (ext.toLowerCase().equals("pdf") || ext.toLowerCase().equals("png")
             || ext.toLowerCase().equals("jpg") || ext.toLowerCase().equals("jpeg")) {
      AbstractTreeCanvas canvas = null;
      if (this.exportDomain == 0) {
        if (treeLayout.isSand()) {
          canvas = new SandTreeCanvas<Ring>(new NodeTree(treeLayout));
        }
        else {
          canvas = new ADTreeCanvas<Ring>(new NodeTree(treeLayout));
        }
      }
      else {
        ValuationDomain domain = treeLayout.getDomain(this.exportDomain);
        if (domain != null) {
          canvas = new AbstractDomainCanvas<Ring>(domain);
          ((AbstractDomainCanvas<Ring>) canvas).setMarkEditable(this.markEditable);
          ((AbstractDomainCanvas<Ring>) canvas).setShowLabels(!this.noLabels);
          ((AbstractDomainCanvas<Ring>) canvas).getValues().setShowAllLabels(!this.noComputedValues);
          ((AbstractDomainCanvas<Ring>) canvas).setTree(new NodeTree(this.treeLayout));
        }
        else {
          System.err.println(Options.getMsg("clo.nodomainError", new Integer(treeLayout.getNewDomainId() - 1).toString()));
          try {
            out.close();
          }
          catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return;
          }
        }
      }
      if (this.viewPortSize != null) {
        canvas.setViewPortSize(this.viewPortSize);
        canvas.fitToWindow();
      }
      if (ext.toLowerCase().equals("pdf")) {
        canvas.createPdf(out);
      }
      else {
        canvas.createImage(out, ext.toLowerCase());
      }
    }
    else {
      System.err.println(Options.getMsg("clo.noextError", ext));
      try {
        out.close();
      }
      catch (IOException e) {
        System.err.println(e.getLocalizedMessage());
      }
      return;
    }
  }

  private void exportXml(FileOutputStream fileStream, TreeLayout layout) {
    boolean oldSaveDerived = Options.main_saveDerivedValues;
    Options.main_saveDerivedValues = exportAllDomains;
    boolean oldSaveRanking = Options.main_saveRanking;
    Options.main_saveRanking = this.exportRanking;
    boolean oldSaveDomain = Options.main_saveDomains;
    Options.main_saveDomains = !noDerivedValues;
    try {
      new XmlConverter().exportTo(fileStream, layout);
    }
    catch (IOException e) {
      System.err.println(Options.getMsg("clo.export.fail") + " " + e.getLocalizedMessage());
    }

    Options.main_saveDerivedValues = oldSaveDerived;
    Options.main_saveRanking = oldSaveRanking;
    Options.main_saveDomains = oldSaveDomain;
  }

  /**
   * @param exportAllDomains
   *          the exportAllDomains to set
   */
  public void setExportAllDomains(boolean exportAllDomains) {
    this.exportAllDomains = exportAllDomains;
  }

  /**
   * @param markEditable
   *          the markEditable to set
   */
  public void setMarkEditable(boolean markEditable) {
    this.markEditable = markEditable;
  }

  /**
   * @param noLabels
   *          the noLabels to set
   */
  public void setNoLabels(boolean noLabels) {
    this.noLabels = noLabels;
  }

  /**
   * @param noComputedValues
   *          the noComputedValues to set
   */
  public void setNoComputedValues(boolean noComputedValues) {
    this.noComputedValues = noComputedValues;
  }

  /**
   * @param noDerivedValues
   *          the noDerivedValues to set
   */
  public void setNoDerivedValues(boolean noDerivedValues) {
    this.noDerivedValues = noDerivedValues;
  }

  public void setViewPortSize(Dimension dim) {
    this.viewPortSize = dim;
  }

  public void setExportDomainStr(String domainId) {
    this.exportDomainStr = domainId;
  }

  public void setExportRanking(boolean exportRanking) {
    this.exportRanking = exportRanking;
  }

  private String getExtension(String filename) {
    if (filename == null) {
      return null;
    }
    int extensionPos = filename.lastIndexOf('.');
    int lastSeparator = filename.lastIndexOf(java.io.File.separator);
    if (lastSeparator > extensionPos) {
      return "";
    }
    else {
      return filename.substring(extensionPos + 1);
    }
  }

  private boolean    exportAllDomains;
  private String     exportDomainStr;
  private int        exportDomain;
  private boolean    markEditable;
  private boolean    noLabels;
  private boolean    noComputedValues;
  private boolean    noDerivedValues;
  private boolean    exportRanking;
  private TreeLayout treeLayout;
  private Dimension  viewPortSize;
}
