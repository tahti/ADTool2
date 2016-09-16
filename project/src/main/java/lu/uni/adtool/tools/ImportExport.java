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

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tree.NodeTree;
import lu.uni.adtool.tree.TreeLayout;
import lu.uni.adtool.tree.XmlConverter;
import lu.uni.adtool.ui.canvas.ADTreeCanvas;
import lu.uni.adtool.ui.canvas.AbstractDomainCanvas;
import lu.uni.adtool.ui.canvas.AbstractTreeCanvas;
import lu.uni.adtool.ui.canvas.SandTreeCanvas;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
/**
 * Class used by command line importing/exporting
 */
public class ImportExport {
  public ImportExport() {
    this.exportDomainSet = null;
    markEditable = false;
    noLabels = false;
    noComputedValues = false;
    noDerivedValues = false;
    this.exportDomainIds = null;
    this.viewPortSize = null;
    this.ranking = 0;
  }

  public int countDomains(String fileName) {
    InputStream fileStream = null;
    try {
      fileStream = new FileInputStream(new File(fileName));
    }
    catch (FileNotFoundException e) {
      System.err.println(Options.getMsg("error.xmlimport.fail") + " " + e.getLocalizedMessage());
      return -1;
    }
    try {
      if (fileStream != null) {
        BufferedInputStream in = new BufferedInputStream(fileStream);
        XElement element = null;
        element = XIO.readUTF(in);
        in.close();
        if (element == null) return -1;
        return element.getElements("domain").length;
      }
    }
    catch (IllegalArgumentException e) {
    }
    catch (IOException e) {
    }
    return -1;
  }

  /**
   * Function used to import from command line only - no GUI window created
   *
   * @param fileName
   * @return true if there tree is ready to be exported and false in case of
   * error or e. g. counting domains only
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
        if (this.exportDomainIds != null && this.exportDomainIds.length == 1
            && (this.exportDomainIds[0].equals("?") || this.exportDomainIds[0].equals("q"))) {
          //printing number of domains
          fileStream.close();
          System.out.println(new Integer(element.getElements("domain").length));
          return false;
        }

        this.treeLayout = new TreeLayout(1);
        this.treeLayout.importXml(element, 1);
        if (this.exportDomainIds != null) {
          this.exportDomainSet = new TreeSet<Integer>();
          if (this.exportDomainIds.length == 1 && this.exportDomainIds[0].equals("a")) {
            //add all domains
            for (int i = 1; i<=  element.getElements("domain").length; i++) {
              this.exportDomainSet.add(i);
            }
          }
          else {
            for (String id:this.exportDomainIds) {
              try {
                int no = Integer.parseInt(id);
                this.exportDomainSet.add(no);
              }
              catch (NumberFormatException exception) {
                int i = 1;
                for (XElement domain : element.getElements("domain")) {
                  String domainId = domain.getString("id");
                  if (id != null && id.equals(domainId)) {
                    this.exportDomainSet.add(i);
                    break;
                  }
                  i++;
                }
              }
            }
          }
        }
        else {
          this.exportDomainSet = null;
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
      this.exportXml(out, this.treeLayout, this.exportDomainSet);
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
      if (this.exportDomainSet == null) {
        if (treeLayout.isSand()) {
          canvas = new SandTreeCanvas<Ring>(new NodeTree(treeLayout));
        }
        else {
          canvas = new ADTreeCanvas<Ring>(new NodeTree(treeLayout));
        }
      }
      else {
        Object[] domains = treeLayout.getDomains().toArray();
        if (domains != null && domains.length > 0 && domains[0] != null) {
          ValuationDomain domain = (ValuationDomain) domains[0];
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
          }
          return;
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

  private void exportXml(FileOutputStream fileStream, TreeLayout layout, Set<Integer> ids) {
    boolean oldSaveDerived = Options.main_saveDerivedValues;
    Options.main_saveDerivedValues = !this.noDerivedValues;
    int oldRanking = Options.rank_noRanked;
    Options.rank_noRanked = this.ranking;
    boolean oldSaveRanking = Options.main_saveRanking;
    Options.main_saveRanking = (this.ranking > 0);
    try {
      new XmlConverter().exportTo(fileStream, layout, ids);
    }
    catch (IOException e) {
      System.err.println(Options.getMsg("clo.export.fail") + " " + e.getLocalizedMessage());
    }
    Options.main_saveDerivedValues = oldSaveDerived;
    Options.rank_noRanked  = oldRanking;
    Options.main_saveRanking = oldSaveRanking;
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

  public void setExportDomainStr(String[] domainIds) {
    this.exportDomainIds = domainIds;
  }

  public void setExportRanking(int ranking) {
    this.ranking = ranking;
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

  private String[]   exportDomainIds;
  private Set<Integer> exportDomainSet;
  private boolean    markEditable;
  private boolean    noLabels;
  private boolean    noComputedValues;
  private boolean    noDerivedValues;
  private int        ranking;
  private TreeLayout treeLayout;
  private Dimension  viewPortSize;
}
