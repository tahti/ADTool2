package lu.uni.adtool.tree;

import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.TreeDockable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TxtImporter {
  public void importFrom(InputStream in, MainController controller) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder out = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      out.append(line);
    }
    Node newRoot = null;
    Parser parser = null;
    boolean isAdt = false;

    parser = new SandParser();
    newRoot = (Node)parser.parseString(out.toString());
    if (newRoot == null) {
      parser = new ADTParser();
      newRoot = (Node)parser.parseString(out.toString());
      if (newRoot == null) {
        throw (new IOException("Wrong format of the file"));
      }
      isAdt = true;
    }
    final TreeLayout layout = new TreeLayout(controller.getFrame().getTreeFactory().getNewUniqueId(), newRoot);
    final TreeDockable treeDockable = new TreeDockable(controller.getFrame().getTreeFactory(), layout, false);
    controller.addTreeDockable(treeDockable);
    if(!isAdt) {
      controller.report(Options.getMsg("status.status.importTxt"));
    }

    reader.close();
  }
}
