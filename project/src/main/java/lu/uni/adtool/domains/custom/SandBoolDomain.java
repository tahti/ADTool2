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
package lu.uni.adtool.domains.custom;

import lu.uni.adtool.domains.SandDomain;
import lu.uni.adtool.domains.adtpredefined.DescriptionGenerator;
import lu.uni.adtool.domains.rings.Bool;
import lu.uni.adtool.tools.Options;
import lu.uni.adtool.tree.Node;
import lu.uni.adtool.tree.SandNode;

public class SandBoolDomain implements SandDomain<Bool>, SandCustomDomain {


  public SandBoolDomain () {
    name = Options.getMsg("adtdomain.custom.bool.name");
    description = Options.getMsg("adtdomain.custom.bool.description");
    this.parser = new BoolParser();
    this.defaultValue = new Bool(true);
  }

//   public Bool or(Bool a, Bool b) {
//     return calc(a, b, SandNode.Type.OR);
//   }
// 
//   public Bool and(Bool a, Bool b) {
//     return calc(a, b, SandNode.Type.AND);
//   }
// 
//   public Bool sand(Bool a, Bool b) {
//     return calc(a, b, SandNode.Type.SAND);
//   }

  public Bool calc(Bool a, Bool b, SandNode.Type type) {
    switch (type) {
    case AND:
      return this.and.evaluate(a, b);
    case SAND:
      return this.sand.evaluate(a, b);
    case OR:
      return this.or.evaluate(a, b);
    default:
      return this.or.evaluate(a, b);
    }
  }


  public boolean setName(String name) {
    this.name = name;
    if (name != null && (!name.equals(""))) {
      return true;
    }
    return false;
  }


  public boolean setDescription(String description) {
    if (description == null || (description.equals(""))) {
      return false;
    }
    this.description =  description;
    return true;
  }

  public final Bool getDefaultValue(Node node) {
    return (Bool)defaultValue.clone();
  }

  public boolean setOr(String expr) {
    this.or = this.parser.parseString(expr);
    if (this.or == null) {
      return false;
    }
    return true;
  }

  public boolean setAnd(String expr) {
    this.and = this.parser.parseString(expr);
    if (this.and == null) {
      return false;
    }
    return true;
  }

  public boolean setSand(String expr) {
    this.sand = this.parser.parseString(expr);
    if (this.sand == null) {
      return false;
    }
    return true;
  }


  public String getOr() {
    if (this.or != null) {
      return this.or.toString(false);
    }
    else {
      return "";
    }
  }

  public String getAnd() {
    if (this.and != null) {
      return this.and.toString(false);
    }
    else {
      return "";
    }
  }

  public String getSand() {
    if (this.sand != null) {
      return this.sand.toString(false);
    }
    else {
      return "";
    }
  }


  public String getName() {
    return this.name;
  }

  public String getShortDescription() {
    return this.description;
  }

  public String getDescription() {
    try {
      String[] operators = {this.or.toString(true)
                        , this.and.toString(true)
                        , this.sand.toString(true)
                         };
      return DescriptionGenerator.generateDescription(this, description , "{true,&nbsp;false}", operators);
    }
    catch (NullPointerException e) {
      return this.description;
    }
  }

  public boolean setDefault(String value) {
    return this.defaultValue.updateFromString(value);
  }

  public void setDefault(boolean value) {
    this.defaultValue = new Bool(value);
  }


  public String getDefault() {
    if (this.defaultValue != null) {
      return defaultValue.toString();
    }
    else {
      return "";
    }
  }

  private Bool defaultValue;
  private String name;
  private String description;
  private BoolExpression or;
  private BoolExpression and;
  private BoolExpression sand;
  private transient BoolParser parser;
  private static final long serialVersionUID = 651714929451277692L;

}
