package lu.uni.adtool.tools;

import lu.uni.adtool.domains.ValuationDomain;
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
import java.util.ArrayList;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

public class ImportExport {
  public ImportExport() {
    exportAllDomains = false;
    exportDomain = -1;
    markEditable = false;
    noLabels = false;
    noComputedValues = false;
    noDerivedValues = false;
    exportDomainStr = null;
    this.viewPortSize = null;
  }

  public boolean doImport(String fileName) {
    InputStream fileStream = null;
    try {
      fileStream = new FileInputStream(new File(fileName));
    }
    catch (FileNotFoundException e) {
      System.err.println(Options.getMsg("error.xmlimport.fail") + " " + e.getLocalizedMessage());
      return false;
    }

    if (fileStream != null) {
      BufferedInputStream in = new BufferedInputStream(fileStream);
      XElement element = null;
      try {
        element = XIO.readUTF(in);
        in.close();
        if (element == null) return false;
        this.tree = new TreeLayout(1);
        this.tree.importXml(element, 1);
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
      catch (IllegalArgumentException e) {
        System.err.println(Options.getMsg("error.xmlimport.fail") + " " + e.getLocalizedMessage());
        this.tree = null;
        return false;
      }
      catch (IOException e) {
        System.err.println(Options.getMsg("error.xmlimport.fail") + " " + e.getLocalizedMessage());
        this.tree = null;
        return false;
      }
      return true;
    }
    return false;
  }

  public void doExport(String fileName) {
    String ext = this.getExtension(fileName);
    if (tree == null || ext == null) {
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
      this.exportXml(out, this.tree);
    }
    else {
      if (ext.toLowerCase().equals("txt")) {
        AbstractTreeCanvas canvas = null;
        if (tree.isSand()) {
          canvas = new SandTreeCanvas(new NodeTree(tree));
        }
        else {
          canvas = new ADTreeCanvas(new NodeTree(tree));
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
        if (exportDomain > -1) {
          AbstractTreeCanvas canvas = null;
          if (exportDomain == 0) {
            if (tree.isSand()) {
              canvas = new SandTreeCanvas(new NodeTree(tree));
            }
            else {
              canvas = new ADTreeCanvas(new NodeTree(tree));
            }
          }
          else {
            ArrayList<ValuationDomain> domains = tree.getDomains();
            if (domains.size() < (exportDomain - 1)) {
              System.err.println(Options.getMsg("clo.nodomain"));
              return;
            }
            canvas = new AbstractDomainCanvas(domains.get(exportDomain - 1));
            ((AbstractDomainCanvas) canvas).setMarkEditable(this.markEditable);
            ((AbstractDomainCanvas) canvas).setShowLabels(!this.noLabels);
            ((AbstractDomainCanvas) canvas).getValues().setShowAllLabels(!this.noComputedValues);
            ((AbstractDomainCanvas) canvas).setTree(new NodeTree(this.tree));
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
      }
      else {
        System.err.println(Options.getMsg("clo.noextError", ext));
        return;
      }
    }
  }

  private void exportXml(FileOutputStream fileStream, TreeLayout layout) {
    boolean oldSaveDerived = Options.main_saveDerivedValues;
    Options.main_saveDerivedValues = exportAllDomains;
    boolean oldSaveDomain = Options.main_saveDomains;
    Options.main_saveDomains = !noDerivedValues;
    try {
      new XmlConverter().exportTo(fileStream, layout);
    }
    catch (IOException e) {
      System.err.println(Options.getMsg("clo.export.fail") + " " + e.getLocalizedMessage());
    }

    Options.main_saveDerivedValues = oldSaveDerived;
    Options.main_saveDomains = !oldSaveDomain;
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
  private TreeLayout tree;
  private Dimension  viewPortSize;
}
