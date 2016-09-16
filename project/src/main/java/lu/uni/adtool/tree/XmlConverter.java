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
package lu.uni.adtool.tree;

import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.RankExporter;
import lu.uni.adtool.domains.Ranker;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.rings.Ring;
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
import java.util.Set;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XException;
import bibliothek.util.xml.XIO;

public class XmlConverter {

  public XmlConverter() {
  }

  /**
   * Exports tree to the file stream in XML format.
   *
   * @param fileStream - file stream to which we want to export the tree in XML format
   * @param layout - layout containing the tree and the associated domains
   * @param idToExport - set od domain IDs to export
   *
   * @throws IOException
   */
  public void exportTo(FileOutputStream fileStream, TreeLayout layout, Set<Integer> idToExport) //, ArrayList<ValuationDomain> domains)
      throws IOException {
    XElement rootXML = null;
    if (layout.isSand())  {
      rootXML = new XElement("sandtree");
      ArrayList<RankExporter> rankers = null;
      if (Options.main_saveRanking && idToExport != null) {
        rankers = new ArrayList<RankExporter>();
        for (ValuationDomain values : layout.getDomains()) {
          if (idToExport.contains(values.getDomainId())) {
            rankers.add(new RankExporter(layout.getRoot(), values.getValueMap(),
                                       new Ranker<Ring>((SandDomain<Ring>)values.getDomain()),
                                       Options.rank_noRanked));
          }
        }
      }
    rootXML.addElement(((SandNode) layout.getRoot()).exportXml(layout.getDomains(), rankers, idToExport));
      if (layout.hasDomain() && idToExport != null) {
        for (ValuationDomain d : layout.getDomains()) {
          if (idToExport.contains(d.getDomainId())) {
            d.exportXML(rootXML);
          }
        }
      }
    }
    else {
      rootXML = new XElement("adtree");
      ArrayList<RankExporter> rankers = null;
      if (Options.main_saveRanking && idToExport != null) {
        rankers = new ArrayList<RankExporter>();
        for (ValuationDomain values : layout.getDomains()) {
          if (idToExport.contains(values.getDomainId())) {
            rankers.add(new RankExporter(layout.getRoot(), values.getValueMap(),
                                       new Ranker<Ring>((AdtDomain<Ring>)values.getDomain()), Options.rank_noRanked));
          }
        }
      }
      XElement rootNode = ((ADTNode) layout.getRoot()).exportXml(layout.getDomains(), rankers, idToExport);
      if (layout.getSwitchRole())  {
        rootNode.addString("switchRole", "yes");
        Debug.log("exporting switch role");
      }
      rootXML.addElement(rootNode);
//       if (layout.hasDomain() && Options.main_saveDomains) {
      if (layout.hasDomain() && idToExport != null) {
        for (ValuationDomain d : layout.getDomains()) {
          if (idToExport.contains(d.getDomainId())) {
            d.exportXML(rootXML);
          }
        }
      }
    }
    BufferedOutputStream out = new BufferedOutputStream(fileStream);
    XIO.writeUTF(rootXML, out);
    out.close();
  }

  public void importFrom(InputStream fileStream, MainController controller) throws IOException {
    final TreeDockable treeDockable;
    BufferedInputStream in = new BufferedInputStream(fileStream);
    XElement element = XIO.readUTF(in);
    in.close();
    TreeFactory treeFactory = controller.getFrame().getTreeFactory();
    int treeId = treeFactory.getNewUniqueId();
    TreeLayout treeLayout = new TreeLayout(treeId);
    try {
      treeLayout.importXml(element, treeId);
      treeDockable = treeFactory.load(treeLayout);
      controller.addTreeDockable(treeDockable);
    }
    catch (IllegalArgumentException e) {
      controller.reportError(Options.getMsg("error.xmlimport.fail") +  e.getMessage());
    }
    catch (XException e) {
      controller.reportError(Options.getMsg("error.xmlimport.fail") +  e.getMessage());
    }
  }
}
