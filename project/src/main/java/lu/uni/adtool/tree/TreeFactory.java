package lu.uni.adtool.tree;

import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.TreeDockable;

import java.util.ArrayList;

import bibliothek.gui.dock.common.MultipleCDockableFactory;

public class TreeFactory implements MultipleCDockableFactory<TreeDockable, TreeLayout> {

  public TreeFactory(MainController controller) {
    this.idCount = 0;
    this.controller = controller;
  }

  public String getId() {
    return TREE_FACTORY_ID;
  }

  /*
   * An empty layout is required to read a layout from an XML file or from a
   * byte stream
   */
  public TreeLayout create() {
    return new TreeLayout(-1);
  }

  /*
   * An optional method allowing to reuse 'dockable' when loading a new layout
   */
  public boolean match(TreeDockable dockable, TreeLayout layout) {
    return dockable.getLayout().equals(layout);
  }

  /* Called when applying a stored layout */
  public TreeDockable read(TreeLayout layout) {
    if (layout.getRoot() == null) return null;
    Debug.log("reading, treeId:" + layout.getId());
    this.idCount = Math.max(this.idCount, layout.getId());
    TreeDockable dockable = new TreeDockable(this, layout, true);
    DomainFactory factory = controller.getFrame().getDomainFactory();
    ArrayList<DomainDockable> domains = factory.getDomains(new Integer(layout.getId()));
    if (domains != null) {
      // final ArrayList<ValuationDomain> domainArray = layout.getDomains();
      int size = domains.size();
      Debug.log("Domains size:" + size);
      for (int i = 0; i < size; i++) {
        DomainDockable domain = domains.get(i);
        domain.getCanvas().setTree(dockable.getCanvas().getTree());
        dockable.getLayout().addDomain(domain.getCanvas().getValues());
        Debug.log("Connecting domain domainId:" + domain.getValues().getDomainId());
      }
    }
    return dockable;
  }

  /**
   * Used to import from TreeLayout where positions of new windows is not saved
   */
  public TreeDockable load(TreeLayout layout) {
    this.idCount = Math.max(this.idCount, layout.getId());
    TreeDockable dockable = new TreeDockable(this, layout, false);
    DomainFactory factory = controller.getFrame().getDomainFactory();
//     if(layout.getDomains() != null){
//       for(ValuationDomain values:layout.getDomains()){
//         DomainDockable d = factory.read(values);
//           d.setWorkingArea(dockable.getWorkArea());
//           Debug.log("Adding domain to control with id:" + d.getUniqueId());
//           controller.getControl().addDockable(d.getUniqueId(), d);
//           dockable.showDomain(d);
//           d.getCanvas().setTree(dockable.getCanvas().getTree());
//       }
//     }
    return dockable;
  }

  /* Called when storing the current layout */
  public TreeLayout write(TreeDockable dockable) {
    return dockable.getLayout();
  }

  public MainController getController() {
    return controller;
  }

  public int getNewUniqueId() {
    idCount = idCount + 1;
    return idCount;
  }

  public static final String TREE_FACTORY_ID = "sand_fact";
  private int                idCount;
  private MainController     controller;
}
