package lu.uni.adtool.tree;

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.TreeDockable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

public class XmlConverter {

  public XmlConverter() {
  }

  /**
   * Exports tree into the xml file.
   *
   * @param fileStream
   *          stream to which we write - we assume stream is open and we close
   *          it.
   */
  public void exportTo(FileOutputStream fileStream, Node root, ArrayList<ValuationDomain> domains)
      throws IOException {
    if (root == null) return;
    XElement rootXML = null;
    if (root instanceof SandNode) {
      rootXML = new XElement("sandtree");
      rootXML.addElement(((SandNode) root).exportXml(domains));
      Debug.log("Exporting domains:" + Options.main_saveDomains + " size:" + domains.size());
      if (domains != null && Options.main_saveDomains) {
        for (ValuationDomain d : domains) {
          d.exportXML(rootXML);
        }
      }
    }
    else {
      rootXML = new XElement("adtree");
      rootXML.addElement(((ADTNode) root).exportXml(domains));
      if (domains != null && Options.main_saveDomains) {
        for (ValuationDomain d : domains) {
          d.exportXML(rootXML);
        }
      }
    }
    if (rootXML != null) {
      BufferedOutputStream out = new BufferedOutputStream(fileStream);
      XIO.writeUTF(rootXML, out);
      out.close();
    }
    else {
      Debug.log("export to xml failed");
      fileStream.close();
    }
  }

  public void importFrom(InputStream fileStream, MainController controller) throws IOException {
    BufferedInputStream in = new BufferedInputStream(fileStream);
    XElement element = XIO.readUTF(in);
    in.close();
    TreeFactory treeFactory = controller.getFrame().getTreeFactory();
    int treeId = treeFactory.getNewUniqueId();
    TreeLayout treeLayout = new TreeLayout(treeId);
    if (element.getName().equals("sandtree")) {
      SandNode root = new SandNode();
      try {
        ArrayList<ValuationDomain> domains = root.importXml(element, treeId);
        treeLayout.setRoot(root);
        treeLayout.setDomains(domains);
      }
      catch (IllegalArgumentException e) {
        controller.report(Options.getMsg("error.xmlimport.fail") +  e.getMessage());
      }
    }
    else if (element.getName().equals("adtree")) {
      ADTNode root = new ADTNode();
      try {
        ArrayList<ValuationDomain> domains = root.importXml(element, treeId);
        treeLayout.setRoot(root);
        treeLayout.setDomains(domains);
      }
      catch (IllegalArgumentException e) {
        controller.report(Options.getMsg("error.xmlimport.fail") +  e.getMessage());
      }
    }
    else {
      controller.report(Options.getMsg("error.xmlimport.nonode"));
    }
    TreeDockable treeDockable = treeFactory.load(treeLayout);
    controller.addTreeDockable(treeDockable);

  }
}
