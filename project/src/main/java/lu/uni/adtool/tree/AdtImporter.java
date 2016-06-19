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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import lu.uni.adtool.adtree.ADTreeNode;
import lu.uni.adtool.domains.AdtDomain;
import lu.uni.adtool.domains.AtdDomainFactory;
import lu.uni.adtool.domains.predefined.Domain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.TreeDockable;

public class AdtImporter {

  public AdtImporter() {
  }

  public void importFrom(FileInputStream fileStream, MainController controller) throws IOException {
    File tempFile = File.createTempFile("load", ".adt");
    tempFile.deleteOnExit();

    FileInputStream inPatched = patch(fileStream, tempFile);
    ObjectInputStream in;
    in = new ObjectInputStream(inPatched);
    TreeLayout treeLayout = null;
    try {
      Object o = in.readObject();
      if (o.getClass().getSimpleName().equals("Integer")) {
        treeLayout = this.loadAdt(in, controller);
      }
      else if (o.getClass().getSimpleName().equals("ADTreeNode")) {
        treeLayout = this.loadVer1((ADTreeNode) o, in, controller);
      }
      else {
        controller.report(Options.getMsg("error.wrongadtformat"));
        return;
      }
      this.ignoreSomeOptions(in);
      in.readObject();//boolean indicating saved layout - ignored
      Integer noDomains = (Integer)in.readObject();
      Debug.log("OK no domains:" + noDomains);
      for(int i = 0; i < noDomains; i++) {
        AdtDomain<Ring> d = null;
        try {
          @SuppressWarnings("unchecked")
          Domain<Ring> d2 = (lu.uni.adtool.domains.predefined.Domain<Ring>) in.readObject();
          d = AtdDomainFactory.updateDomain(d2);
        }
        catch (ClassNotFoundException e) {
          Debug.log(e.getMessage());
          d = AtdDomainFactory.createFromString(e.getMessage());
        }
        if (d == null) {
          controller.report(Options.getMsg("error.wrongadtformat"));
          return;
        }
        @SuppressWarnings("unchecked")
		lu.uni.adtool.adtree.ValueAssignement<Ring> vass = (lu.uni.adtool.adtree.ValueAssignement<Ring>) in.readObject();
        @SuppressWarnings("unchecked")
		lu.uni.adtool.adtree.ValueAssignement<Ring> vass2 = (lu.uni.adtool.adtree.ValueAssignement<Ring>) in.readObject();
        treeLayout.addAdtDomain(d, vass, vass2, treeLayout.getTreeId(), i);
      }
      TreeFactory treeFactory = controller.getFrame().getTreeFactory();
      TreeDockable treeDockable = treeFactory.load(treeLayout);
      controller.addTreeDockable(treeDockable);
    }
    catch (ClassNotFoundException e1) {
      controller.report(Options.getMsg("error.wrongadtformat"));
     }
  }

  public TreeLayout loadAdt(ObjectInputStream in, MainController controller)
      throws IOException, ClassNotFoundException {
    in.readObject(); // description
    in.readObject(); // comments
    Object o = in.readObject();
    if (o.getClass().getSimpleName().equals("ADTreeNode")) {// old version of
                                                            // save
      return this.loadVer1((ADTreeNode) o, in, controller);
    }
    else {
      controller.report(Options.getMsg("error.wrongadtformat"));
      return null;
    }
  }

@SuppressWarnings("unchecked")
  public TreeLayout loadVer1(ADTreeNode root, ObjectInputStream in, MainController controller)
      throws IOException, ClassNotFoundException {
    Map<ADTreeNode, ArrayList<ADTreeNode>> childrenMap =
        (Map<ADTreeNode, ArrayList<ADTreeNode>>) in.readObject();
    Map<ADTreeNode, ADTreeNode> parents = (Map<ADTreeNode, ADTreeNode>) in.readObject();
    TreeFactory treeFactory = controller.getFrame().getTreeFactory();
    int treeId = treeFactory.getNewUniqueId();
    TreeLayout treeLayout = new TreeLayout(treeId);
    treeLayout.importAdt(root, childrenMap, parents);
    return treeLayout;
    // ignoreOptions(in);
  }

  private void print(MappedByteBuffer buf) {
    int pos = 0;
    while (pos < buf.limit()) {
      System.out.print(buf.get(pos) + " ");
      ++pos;
    }
    System.out.println(" \n");
  }

  private long min(long a, long b) {
    if (a < b) {
      return a;
    }
    return b;
  }

  private long find(long startPos, FileChannel channel, ArrayList<Byte> str) {
    if (str.size() == 0) return 0;
    long pos = startPos;
    int bufPos = 0;
    long bufSize = 1024 + str.size();
    long fileSize;
    try {
      fileSize = channel.size();
      while (pos < fileSize) {
        MappedByteBuffer buf = channel.map(MapMode.READ_ONLY, pos, min(bufSize, fileSize - pos));
        bufPos = 0;
        while (bufPos < buf.limit()) {
          if (str.get(0).byteValue() == buf.get(bufPos)) {
            int i = 1;
            while (i < str.size() && (bufPos + i) < buf.limit()) {
              if (str.get(i) != buf.get(bufPos + i)) {
                break;
              }
              ++i;
            }
            if (i == str.size()) {
              return pos;
            }
          }
          ++pos;
          ++bufPos;
        }
      }
    }
    catch (IOException e) {
      return -1;
    }
    return -1;
  }

  private void ignoreSomeOptions(ObjectInputStream in) throws IOException, ClassNotFoundException {
    // Options.canv_BackgroundColor =
    in.readObject();
    // Options.canv_EdgesColor = (Color)
    in.readObject();
    // Options.canv_TextColorAtt = (Color)
    in.readObject();
    // Options.canv_TextColorDef = (Color)
    in.readObject();
    // Options.canv_FillColorAtt = (Color)
    in.readObject();
    // Options.canv_FillColorDef = (Color)
    in.readObject();
    // Options.canv_BorderColorAtt = (Color)
    in.readObject();
    // Options.canv_BorderColorDef = (Color)
    in.readObject();
    // Options.canv_EditableColor = (Color)
    in.readObject();
    // Options.canv_ShapeAtt
    try {
      in.readObject();
    }
    catch (ClassNotFoundException e) {
    }
    // Options.canv_ShapeAtt
    try {
      in.readObject();
    }
    catch (ClassNotFoundException e) {
    }
    // Options.canv_Font = (Font)
    in.readObject();
    // ADTreeNode.Type rootRole = (ADTreeNode.Type)
    ADTreeNode.Type role = (ADTreeNode.Type)in.readObject();
    // Options.canv_ArcSize = (Integer)
    in.readObject();
    // Options.canv_ArcPadding = (Integer)
    in.readObject();
    // Options.canv_LineWidth = (Integer)
    in.readObject();
    // Options.canv_DoAntialiasing = (Boolean)
    in.readObject();
  }

  private boolean copy(FileChannel channel, FileOutputStream out, long pos, long size) {
    MappedByteBuffer buf;
    System.out.println("start:"+pos+" size "+size);
    try {
      buf = channel.map(MapMode.READ_ONLY, pos, size);
      byte[] buffer = new byte[(int)size];
      buf.get(buffer);
      out.write(buffer);
    }
    catch (IOException e) {
      return false;
    }
    return true;
  }

  private FileInputStream patch(FileInputStream in, File tempFile) {
    try {
      FileChannel channel = in.getChannel();
      long fileSize = channel.size();
      final Byte[] b = {115, 114, 0, 17, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 66, 111, 111, 108,
            101, 97, 110};
      long start = this.find(0 ,channel, new ArrayList<Byte>(Arrays.asList(b)));
      if (start < 1) {
        return null;
      }
      start = start + 50;
      MappedByteBuffer buf = channel.map(MapMode.READ_ONLY, start, 100);
      print(buf);
      final Byte[] b2 = {115, 113, 0, 126, 0, 0};
      long end = this.find(start, channel, new ArrayList<Byte>(Arrays.asList(b2)));
      if (end < 1) {
        return null;
      }
      buf = channel.map(MapMode.READ_ONLY, end, 100);
      print(buf);

      FileOutputStream out;

      out = new FileOutputStream(tempFile);
      if (!copy(channel, out, 0, start)) return null;
      start = end;
      final Byte[] vp = {115, 114, 0, 38, 108, 117, 46, 117, 110, 105, 46, 97, 100, 116, 111, 111, 108, 46, 100, 111, 109, 97, 105, 110, 115, 46, 86, 97, 108, 117, 101, 65, 115, 115, 105, 103, 110, 101, 109, 101, 110, 116};
      final byte[] vr = {115, 114, 0, 37, 108, 117, 46, 117, 110, 105, 46, 97, 100, 116, 111, 111, 108, 46, 97, 100, 116, 114, 101, 101, 46, 86, 97, 108, 117, 101, 65, 115, 115, 105, 103, 110, 101, 109, 101, 110, 116};
      end = find(start, channel, new ArrayList<Byte>(Arrays.asList(vp)));
      while (end > 0) {
        if (!copy(channel, out, start, end - start) ) return null;
        out.write(vr);
        start = end + 42;
        end = find(start, channel, new ArrayList<Byte>(Arrays.asList(vp)));
      }
      if (!copy(channel, out, start, fileSize - start)) return null;
      out.close();
      return new FileInputStream(tempFile);
    }
    catch (FileNotFoundException e) {
      return null;
    }
    catch (IOException e) {
      return null;
    }
  }
}
