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
import lu.uni.adtool.domains.Domain;
import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.ValuationDomain;
import lu.uni.adtool.domains.adtpredefined.DiffLMH;
import lu.uni.adtool.domains.adtpredefined.DiffLMHE;
import lu.uni.adtool.domains.adtpredefined.MinCost;
import lu.uni.adtool.domains.adtpredefined.MinSkill;
import lu.uni.adtool.domains.adtpredefined.MinTimePar;
import lu.uni.adtool.domains.adtpredefined.MinTimeSeq;
import lu.uni.adtool.domains.adtpredefined.PowerCons;
import lu.uni.adtool.domains.adtpredefined.ProbSucc;
import lu.uni.adtool.domains.adtpredefined.ReachPar;
import lu.uni.adtool.domains.adtpredefined.ReachSeq;
import lu.uni.adtool.domains.adtpredefined.SatOpp;
import lu.uni.adtool.domains.adtpredefined.SatProp;
import lu.uni.adtool.domains.adtpredefined.SatScenario;
import lu.uni.adtool.domains.custom.AdtBoolDomain;
import lu.uni.adtool.domains.custom.AdtIntDomain;
import lu.uni.adtool.domains.custom.AdtRealDomain;
import lu.uni.adtool.domains.custom.SandBoolDomain;
import lu.uni.adtool.domains.custom.SandIntDomain;
import lu.uni.adtool.domains.custom.SandRealDomain;
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.domains.sandpredefined.MinTime;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.ui.DomainDockable;
import lu.uni.adtool.ui.MainController;
import lu.uni.adtool.ui.TreeDockable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.reflections.Reflections;

import bibliothek.gui.dock.common.MultipleCDockableFactory;

public class DomainFactory implements MultipleCDockableFactory<DomainDockable, ValuationDomain> {

  public DomainFactory(MainController controller) {
    this.domainDockables = new HashMap<Integer, ArrayList<DomainDockable>>();
    this.controller = controller;
  }

  public String getId() {
    return DOMAIN_FACTORY_ID;
  }

  /*
   * An empty layout is required to read a layout from an XML file or from a
   * byte stream
   */
  public ValuationDomain create() {
    return new ValuationDomain();
  }

  public boolean match(DomainDockable dockable, ValuationDomain values) {
    return dockable.getValues().equals(values);
  }

  /* Called when applying a stored layout */
  public DomainDockable read(ValuationDomain values) {
    if (controller.getControl().getSingleDockable("tree1_treeView") != null)
      Debug.log("Work area:" + controller.getControl().getSingleDockable("tree1_treeView").getWorkingArea());
    Debug.log("Reading domain treeId " + values.getTreeId() + " domainId " + values.getDomainId());
    DomainDockable dockable = new DomainDockable(this, values);
    Integer treeId = new Integer(values.getTreeId());
    ArrayList<DomainDockable> d = this.domainDockables.get(treeId);
    if (d == null) {
      d = new ArrayList<DomainDockable>();
    }
    d.add(dockable);
    Debug.log("adding tree of domains with id:" + treeId);
    this.domainDockables.put(treeId, d);
    TreeDockable treeDockable = (TreeDockable) controller.getControl()
        .getMultipleDockable(TreeDockable.getUniqueId(treeId.intValue()));
    if (treeDockable != null) {
      dockable.getCanvas().setTree(treeDockable.getCanvas().getTree());
      if (treeDockable.getCanvas().isSand()) {
        dockable.hideShowAll();
      }
      if (treeDockable.getLayout().getDomain(values.getDomainId()) == null) {
        treeDockable.getLayout().addDomain(values);
      }
      dockable.setWorkingArea(treeDockable.getWorkArea());
    }
    return dockable;
  }

  /* Called when storing the current layout */
  public ValuationDomain write(DomainDockable dockable) {
    return dockable.getValues();
  }

  public MainController getController() {
    return this.controller;
  }

  @SuppressWarnings("all")
  public static Vector<Domain<?>> getPredefinedDomains(boolean forSand) {
    Vector<Domain<?>> result = new Vector<Domain<?>>();
    if (forSand) {
      result.add(new SandBoolDomain());
      result.add(new SandIntDomain());
      result.add(new SandRealDomain());
      Reflections reflections = new Reflections(sandDomainsPrefix);
      Set<Class<? extends SandDomain>> m = reflections.getSubTypesOf(SandDomain.class);
      for (Class<? extends SandDomain> c : m) {
        SandDomain<Ring> d = null;
        Constructor<SandDomain<Ring>>[] ct = (Constructor<SandDomain<Ring>>[]) c.getDeclaredConstructors();
        try {
          if (ct.length == 1) {
            d = ct[0].newInstance();
            result.add((SandDomain<Ring>) d);
          }
        }
        catch (InstantiationException e) {
          e.printStackTrace();
          return null;
        }
        catch (IllegalAccessException e) {
          System.err.println(e.getStackTrace());
          return null;
        }
        catch (InvocationTargetException e) {
          System.err.println(e.getStackTrace());
          return null;
        }
      }
      // fixing not loading classes under webstart
      if (result.size() == 0) {
        result.add(new MinTime());
      }
    }
    else {
      // add custom domains first
      result.add(new AdtBoolDomain());
      result.add(new AdtIntDomain());
      result.add(new AdtRealDomain());
      // add other domains
      Reflections reflections = new Reflections(adtDomainsPrefix);
      Set<Class<? extends AdtDomain>> m = reflections.getSubTypesOf(AdtDomain.class);
      for (Class<? extends AdtDomain> c : m) {
        Debug.log(" for c:" + c);
        if (c.getSimpleName().equals("RankingDomain")) {
          continue;
        }

        AdtDomain<Ring> d = null;
        Constructor<AdtDomain<Ring>>[] ct = (Constructor<AdtDomain<Ring>>[]) c.getDeclaredConstructors();
        try {
          if (ct.length == 1) {
            d = ct[0].newInstance();
            result.add((AdtDomain<Ring>) d);
          }
        }
        catch (InstantiationException e) {
          e.printStackTrace();
          return null;
        }
        catch (IllegalAccessException e) {
          System.err.println(e);
          return null;
        }
        catch (InvocationTargetException e) {
          System.err.println(e);
          return null;
        }
      }
    }
    // fixing not loading classes under webstart
    if (result.size() == 0) {
      result.add(new SatProp());
      result.add(new ReachPar());
      result.add(new MinTimeSeq());
      result.add(new SatOpp());
      result.add(new MinTimePar());
      result.add(new SatScenario());
      result.add(new DiffLMH());
      result.add(new PowerCons());
      result.add(new MinSkill());
      result.add(new DiffLMHE());
      result.add(new ProbSucc());
      result.add(new ReachSeq());
      result.add(new MinCost());
    }
    return result;
  }

  public static boolean isCustom(String domainName) {
    String name = domainName;
    if (!domainName.startsWith(customDomainsPrefix)) {
      name = customDomainsPrefix + "." + domainName;
    }
    if (   name.equals(customDomainsPrefix + ".AdtBoolDomain")
        || name.equals(customDomainsPrefix + ".AdtIntDomain")
        || name.equals(customDomainsPrefix + ".AdtRealDomain")
        || name.equals(customDomainsPrefix + ".SandBoolDomain")
        || name.equals(customDomainsPrefix + ".SandIntDomain")
        || name.equals(customDomainsPrefix + ".SandRealDomain")
           ) {
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public static boolean isSandDomain(String domainName) {
    String name = domainName;
    if (!domainName.startsWith(sandDomainsPrefix)) {
      if (name.equals(customDomainsPrefix + ".SandBoolDomain")
          || name.equals(customDomainsPrefix + ".SandIntDomain")
          || name.equals(customDomainsPrefix + ".SandRealDomain")) {
        return true;
      }
      else if (name.equals("SandBoolDomain")
          || name.equals("SandIntDomain")
          || name.equals("SandRealDomain")) {
        return true;
      }
      else {
        name = sandDomainsPrefix + "." + domainName;
      }
    }
    Constructor<SandDomain<Ring>>[] ct = null;
    try {
      final Class<?> c = Class.forName(name);
      ct = (Constructor<SandDomain<Ring>>[]) c.getDeclaredConstructors();
    }
    catch (ClassNotFoundException e) {
      return false;
    }
    return ct != null;
  }

  /**
   * Creates predefined domain from string name.
   *
   * @param domainName
   *          domain class name
   * @return created domain.
   */
  @SuppressWarnings("unchecked")
  public static Domain<Ring> createFromString(String domainName) {
    String name = domainName;
    boolean isSand = isSandDomain(domainName);
    if (isSand) {
      if (isCustom(domainName)) {
        if (!domainName.startsWith(customDomainsPrefix)) {
          name = customDomainsPrefix + "." + domainName;
        }
      }
      else {
        if (!domainName.startsWith(sandDomainsPrefix)) {
          name = sandDomainsPrefix + "." + domainName;
        }
      }
      Constructor<SandDomain<Ring>>[] ct = null;
      try {
        final Class<?> c = Class.forName(name);
        ct = (Constructor<SandDomain<Ring>>[]) c.getDeclaredConstructors();
      }
      catch (ClassNotFoundException e) {
        System.err.println(Options.getMsg("error.class.notfound") + " " + name);
        return null;
      }
      SandDomain<Ring> d = null;
      if (ct.length == 1) {
        try {
          d = ct[0].newInstance();
        }
        catch (InstantiationException e) {
          System.err.println(e);
          return null;
        }
        catch (IllegalAccessException e) {
          System.err.println(e);
          return null;
        }
        catch (InvocationTargetException e) {
          System.err.println(e);
          return null;
        }
      }
      return d;
    }
    else {
      if (isCustom(domainName)) {
        if (!domainName.startsWith(customDomainsPrefix)) {
          name = customDomainsPrefix + "." + domainName;
        }
      }
      else {
        if (domainName.startsWith(oldAdtDomainsPrefix)) {
          name = adtDomainsPrefix + domainName.substring(oldAdtDomainsPrefix.length());
          ;
        }
        else if (!domainName.startsWith(adtDomainsPrefix)) {
          name = adtDomainsPrefix + "." + domainName;
        }
      }
      Constructor<AdtDomain<Ring>>[] ct = null;
      try {
        final Class<?> c = Class.forName(name);
        ct = (Constructor<AdtDomain<Ring>>[]) c.getDeclaredConstructors();
      }
      catch (ClassNotFoundException e) {
        System.err.println(Options.getMsg("error.class.notfound") + " " + name);
        return null;
      }
      AdtDomain<Ring> d = null;
      if (ct.length == 1) {
        try {
          d = ct[0].newInstance();
        }
        catch (InstantiationException e) {
          System.err.println(e);
          return null;
        }
        catch (IllegalAccessException e) {
          System.err.println(e);
          return null;
        }
        catch (InvocationTargetException e) {
          System.err.println(e);
          return null;
        }
      }
      return d;
    }
  }

  public void notifyAllTreeChanged(Integer treeId) {
    ArrayList<DomainDockable> domains = domainDockables.get(treeId);
    Debug.log("treeId:" + treeId);
    if (domains != null) {
      Debug.log("domains size:" + domains.size());
      for (DomainDockable domain : domains) {
        NodeTree tree = domain.getCanvas().getTree();
        tree.getSharedExtentProvider().updateTreeSize(tree.getRoot(true));
        domain.getCanvas().treeChanged();
      }
    }
  }

  public void repaintAllDomains(Integer treeId) {
    ArrayList<DomainDockable> domains = domainDockables.get(treeId);
    if (domains != null) {
      for (DomainDockable domain : domains) {
        domain.getCanvas().repaint();
      }
    }
  }

  public void removeDomain(DomainDockable dockable) {
    ArrayList<DomainDockable> d = domainDockables.get(new Integer(dockable.getCanvas().getTreeId()));
    if (d != null) {
      d.remove(dockable);
    }
  }

  public ArrayList<DomainDockable> getDomains(Integer treeId) {
    return domainDockables.get(treeId);
  }

  /**
   * Get domain class name as string.
   *
   * @param d
   *          domain.
   * @return domain class name.
   */
  public static String getClassName(SandDomain<Ring> d) {
    return d.getClass().getSimpleName();
  }

  public static final String                          DOMAIN_FACTORY_ID   = "domain_fact";
  private static final String                         sandDomainsPrefix   = "lu.uni.adtool.domains.sandpredefined";
  private static final String                         oldAdtDomainsPrefix = "lu.uni.adtool.domains.predefined";
  private static final String                         adtDomainsPrefix    = "lu.uni.adtool.domains.adtpredefined";
  private static final String                         customDomainsPrefix = "lu.uni.adtool.domains.custom";
  private MainController                              controller;
  /** map from treeId to list of domains */
  private HashMap<Integer, ArrayList<DomainDockable>> domainDockables;

}
