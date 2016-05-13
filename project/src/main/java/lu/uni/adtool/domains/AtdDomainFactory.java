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
package lu.uni.adtool.domains;

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
import lu.uni.adtool.domains.rings.Ring;
import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tools.Options;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.Vector;

import org.reflections.Reflections;

/**
 * Creates AtdDomains.
 *
 * @author Piotr Kordy
 */
public abstract class AtdDomainFactory {

  /**
   * Constructs a new instance.
   */
  public AtdDomainFactory() {
  }

  public static Boolean isObsolete(Object domain) {
    return isObsolete(domain.getClass().getSimpleName());
  }

  public static Boolean isObsolete(String domainName) {
    if (Arrays.asList(oldNames).contains(domainName)) {
      return true;
    }
    else {
      return false;
    }
  }

  public static AdtDomain<Ring> updateDomain(lu.uni.adtool.domains.predefined.Domain<Ring> d) {
    String newName = updateAtdDomainName(d.getClass().getSimpleName());
    AdtDomain<Ring> domain = createFromString(updateAtdDomainName(newName));
    if (domain instanceof Parametrized) {
      ((Parametrized)domain).setParameter(((Parametrized)d).getParameter());
    }
    return domain;
  }

  public static String updateAtdDomainName(String name) {
    String result = name;
    for (int i = 0; i < oldNames.length; i++) {
      result = result.replace(oldNames[i], newNames[i]);
    }
    return result;
  }

  /**
   * Creates predefined domain from string name.
   *
   * @param domainName
   *          domain class name
   * @return created domain.
   */
  @SuppressWarnings("unchecked")
  public static AdtDomain<Ring> createFromString(String domainName) {
    Debug.log("create for string" +domainName);
    if (!domainName.startsWith(domainsPrefix)) {
      domainName = domainName.replace("predefined", "adtpredefined");
      Debug.log("n:" + domainName);
      if (!domainName.startsWith(domainsPrefix)) {
        domainName = domainsPrefix + "." + domainName;
        Debug.log("name:" + domainName);
      }
    }
    Constructor<AdtDomain<Ring>>[] ct = null;
    try {
      final Class<?> c = Class.forName(domainName);
      ct = (Constructor<AdtDomain<Ring>>[]) c.getDeclaredConstructors();
    }
    catch (ClassNotFoundException e) {
      System.err.println(Options.getMsg("error.class.notfound") + " " + domainName);
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

  /**
   * Get domain class name as string.
   *
   * @param d
   *          domain.
   * @return domain class name.
   */
  public static String getClassName(Object d) {
    return d.getClass().getSimpleName();
  }

  /**
   * Returns string list of predefined domains.
   *
   * @return array of strings with domain names.
   */
  @SuppressWarnings("all")
  public static Vector<AdtDomain<?>> getPredefinedDomains() {
    Vector<AdtDomain<?>> result = new Vector<AdtDomain<?>>();
    Reflections reflections = new Reflections(domainsPrefix);
    Set<Class<? extends AdtDomain>> m = reflections.getSubTypesOf(AdtDomain.class);
    for (Class<? extends AdtDomain> c : m) {
      AdtDomain<Ring> d = null;
      Constructor<AdtDomain<Ring>>[] ct =
          (Constructor<AdtDomain<Ring>>[]) c.getDeclaredConstructors();
      try {
        if (ct.length == 1) {
          d = ct[0].newInstance();
          if (!AtdDomainFactory.isObsolete(d)) {
            result.add((AdtDomain<Ring>) d);
          }
        }
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

  private static final String[] oldNames      =
      {"BoolOrAndStar", "BoolAndOr", "BoolOrAnd", "IntMinMinMax", "IntMinMax", "IntMinSum",
          "ProbPlusTimes", "RealG0MinSumCost", "RealG0MinSumParallel", "RealG0MinSum",
          "RealG0MaxSum", "SkillAtdDomainLMHE", "SkillAtdDomain"};
  private static final String[] newNames      =
      {"SatScenario", "SattOpp", "SatProp", "ReachPar", "MinSkill", "ReachSeq", "ProbSucc",
          "MinCost", "MinTimePar", "MinTimeSeq", "PowerCons", "DiffLMHE", "DiffLMH"};
  private static final String   domainsPrefix = "lu.uni.adtool.domains.adtpredefined";
}
