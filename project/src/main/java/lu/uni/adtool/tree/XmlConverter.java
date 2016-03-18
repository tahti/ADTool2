package lu.uni.adtool.tree;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.RankExporter;
import lu.uni.adtool.domains.Ranker;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
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
      ArrayList<RankExporter> rankers = null;
      if (Options.main_saveRanking) {
        rankers = new ArrayList<RankExporter>();
        for (int i = 0; i <layout.getDomains().size(); i++) {
          ValuationDomain values = layout.getDomains().get(i);
          rankers.add(new RankExporter(layout.getRoot(), values.getValueMap(),
                                       new Ranker<Ring>((SandDomain<Ring>)values.getDomain()), Options.rank_noRanked));
        }
      }
      rootXML.addElement(((SandNode) layout.getRoot()).exportXml(layout.getDomains(), rankers));
      if (layout.getDomains() != null && Options.main_saveDomains) {
        for (ValuationDomain d : layout.getDomains()) {
          d.exportXML(rootXML);
        }
      }
    }
    else {
      rootXML = new XElement("adtree");
      ArrayList<RankExporter> rankers = null;
      if (Options.main_saveRanking) {
        rankers = new ArrayList<RankExporter>();
        for (int i = 0; i <layout.getDomains().size(); i++) {
          ValuationDomain values = layout.getDomains().get(i);
          rankers.add(new RankExporter(layout.getRoot(), values.getValueMap(),
                                       new Ranker<Ring>((AdtDomain<Ring>)values.getDomain()), Options.rank_noRanked));
        }
      }
      XElement rootNode = ((ADTNode) layout.getRoot()).exportXml(layout.getDomains(),rankers);
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
    BufferedOutputStream out = new BufferedOutputStream(fileStream);
    XIO.writeUTF(rootXML, out);
    out.close();
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
