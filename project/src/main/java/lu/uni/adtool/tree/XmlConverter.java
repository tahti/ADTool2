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
  public void exportTo(FileOutputStream fileStream, TreeLayout layout) //, ArrayList<ValuationDomain> domains)
      throws IOException {
    XElement rootXML = null;
    if (layout.isSand())  {
      rootXML = new XElement("sandtree");
      rootXML.addElement(((SandNode) layout.getRoot()).exportXml(layout.getDomains()));
      if (layout.getDomains() != null && Options.main_saveDomains) {
        for (ValuationDomain d : layout.getDomains()) {
          d.exportXML(rootXML);
        }
      }
    }
    else {
      rootXML = new XElement("adtree");
      XElement rootNode = ((ADTNode) layout.getRoot()).exportXml(layout.getDomains());
      if (layout.getSwitchRole())  {
        rootNode.addString("switchRole", "yes");
      }
      rootXML.addElement(rootNode);
      if (layout.getDomains() != null && Options.main_saveDomains) {
        for (ValuationDomain d : layout.getDomains()) {
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
    try {
      treeLayout.importXml(element, treeId);
    }
    catch (IllegalArgumentException e) {
      controller.report(Options.getMsg("error.xmlimport.fail") +  e.getMessage());
    }
    TreeDockable treeDockable = treeFactory.load(treeLayout);
    controller.addTreeDockable(treeDockable);

  }
}
