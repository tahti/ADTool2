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

import java.util.HashMap;
import java.util.Stack;

public class ADTParser extends Parser {
  public ADTParser() {
  }

  public ADTNode parseString(String toParse) {
    this.counterMap = new HashMap<Integer, Counter>();
    String name = "";
    String token = "";
    ADTNode result = null;
    ADTNode last = null;
    Stack<ADTNode> stack = new Stack<ADTNode>();
    int lparen = 0;
    ADTNode paren = null;
    for (position = 0; position < toParse.length(); position++) {
      char ch = toParse.charAt(position);
      switch (ch) {
     case '(':
        lparen++;
        token = name.trim();
        name = "";
        ADTNode n = null;
        if (token.equals("oo")) {
          n = new ADTNode(ADTNode.Type.OR_OPP);
          n.setName("oo");
        }
        else if (token.equals("op")) {
          n = new ADTNode(ADTNode.Type.OR_PRO);
          n.setName("op");
        }
        else if (token.equals("ao")) {
          n = new ADTNode(ADTNode.Type.AND_OPP);
          n.setName("ao");
        }
        else if (token.equals("ap")) {
          n = new ADTNode(ADTNode.Type.AND_PRO);
          n.setName("ap");
        }
        else if (token.equals("co")) {
          if (getDefense(lparen - 1) == Counter.CO) {
            setError(1, "parse.adt.counteredalready");
            return null;
          }
          if (getDefense(lparen) != Counter.NONE) {
            setError(1, "parser.internal");
            return null;
          }
          setDefense(lparen, Counter.CO);
        }
        else if (token.equals("cp")) {
          if (getDefense(lparen - 1) == Counter.CP) {
            setError(1, "parse.adt.counteredalready");
            return null;
          }
          if (getDefense(lparen) != Counter.NONE) {
            setError(1, "parser.internal");
            return null;
          }
          setDefense(lparen, Counter.CP);
        }
        else {
          setError(token.length(), "parser.adt.keyword", token);
          return null;
        }
        // cp and co are not real nodes - we do not add them to stack
        if (getDefense(lparen) == Counter.NONE) {
          if (!stack.empty()) {
          }
          else if (result == null) { // stack empty - setting root
            if (n.getRole() != ADTNode.Role.PROPONENT) {
              setError(1, "parser.adt.wrongrootrole");
              return null;
            }
            result = n;
          }
          else {
            setError(1, "parser.oneroot");
            return null;
          }
          stack.push(n);
        }
        break;

      case ',':
        if (getDefense(lparen) == Counter.CO2 || getDefense(lparen) == Counter.CP2) {
          setError(1, "parser.adt.twoparams");
          return null;
        }
        token = getIdentifier(name, position);
        name = "";
        if (last == null) {
          if (token == null) {
            // Error set in getIdentifier
            return null;
          }
          last = new ADTNode(); // leaf
          last.setName(token);
        }
        else {
          if (token != null && token.length() != 0) {
            setError(token.length(), "parser.unexpected", token);
            return null;
          }
        }
        if (getDefense(lparen) != Counter.NONE) {
          if (getDefense(lparen) == Counter.CO) {
            setDefense(lparen, Counter.CO2);
          }
          else {
            setDefense(lparen, Counter.CP2);
          }
          if (!stack.empty()) {
            // no adding here - node will be added later as counter
          }
          else if (last.hasDefault()) { // stack empty -
            if (result == null) {
              if (last.getRole() != ADTNode.Role.PROPONENT) {
                last.toggleRole();
              }
              result = last;
            }
            else {
              setError(1, "parser.oneroot");
              return null;
            }
          }
          else if (result == null) {
            setError(1, "parser.internal");
            return null;
          }
          stack.push(last);
          //Debug.log(" , stack push:" + last.getName());
          last = null;
        }
        else {
          if (stack.empty()) {
            if (token != null) {
              setError(last.getName().length(), "parser.noparent", last.getName());
              return null;
            }
          }
          paren = stack.peek();
          if (!this.addChild(paren, last)) return null;
          last = null;
        }
        //Debug.log(", end" + getDefense(lparen) + " lparen:" + lparen);
        break;

      case ')':
        //Debug.log(")" + getDefense(lparen) + " lparen:" + lparen + " )-" + getDefense(lparen - 1)
//             + " lparen:" + lparen);
        if (lparen <= 0) {
          setError(1, "parser.unmatched");
          return null;
        }
        lparen--;
        if (getDefense(lparen + 1) == Counter.CO || getDefense(lparen + 1) == Counter.CP) {
          setError(1, "parser.adt.twoparams");
          return null;
        }
        token = getIdentifier(name, position);
        name = "";
        if (last == null) { // expecting label as last is empty
          if (token == null) {
            return null;
          }
          last = new ADTNode(); // leaf - determine role later - type does not
                                // matter
          last.setName(token);
        }
        else {
          if (token != null && token.length() != 0) {
            setError(token.length(), "parser.unexpected", token);
            return null;
          }
        }
        if (stack.empty()) {
          setError(token.length(), "parser.noparent", token);
          return null;
        }
        paren = stack.peek();
        if (getDefense(lparen + 1) == Counter.NONE) {
          if (!addChild(paren, last)) return null;
        }
        else {
          if (!addCounter(paren, last, getDefense(lparen + 1))) return null;
          setDefense(lparen + 1, Counter.NONE);
        }
        last = stack.pop();
        //Debug.log(" ) last = stack pop:" + last.getName());
        //Debug.log(") end" + getDefense(lparen) + " lparen:" + lparen);
        break;

      case '\n': // change end of line and tab to space
      case '\t':
        ch = ' ';
      default:
        name = name + ch;
      }
    }
    // cleaning up
    token = getIdentifier(name, position);
    if (result != null) {
      if (token != null && token.length() != 0) {
        setError(token.length(), "parser.noend", token);
        return null;
      }
      if (lparen != 0 || !stack.empty()) {
        setError(1, "parser.unmatched", token);
        return null;
      }
      // //Debug.log("End of parse");
      return result;
    }
    else {
      if (token == null) {
        return null;
      }
      result = new ADTNode();
      result.setName(token);
      result.setType(ADTNode.Type.AND_PRO);
      return result;
    }
  }

  private enum Counter {
    NONE, CP, CO, CP2, CO2
  }

  /* sets the state of counter measure token (co, cp) for given bracket level */
  private void setDefense(int level, Counter c) {
    this.counterMap.put(new Integer(level), c);
  }

  private Counter getDefense(int level) {
    Counter c = this.counterMap.get(new Integer(level));
    if (c == null) {
      c = Counter.NONE;
    }
    return c;
  }

  private boolean addCounter(ADTNode paren, ADTNode child, Counter mode) {
    //Debug.log("adding node " + child.getName() + " to  " + paren.getName() + " as counter");
    if (mode == Counter.CP2) {
      if (child.getRole() == ADTNode.Role.PROPONENT) {
        if (child.hasDefault()) {
          child.toggleRole();
        }
        else {
          setError(1, "parser.adt.role", child.getName());
          return false;
        }
      }
      if (paren.getRole() == ADTNode.Role.OPPONENT) {
        if (paren.hasDefault()) {
          paren.toggleRole();
        }
        else {
          setError(1, "parser.adt.role", paren.getName());
          return false;
        }
      }
    }
    else {
      if (child.getRole() == ADTNode.Role.OPPONENT) {
        if (child.hasDefault()) {
          child.toggleRole();
        }
        else {
          setError(1, "parser.adt.role", child.getName());
          return false;
        }
      }
      if (paren.getRole() == ADTNode.Role.PROPONENT) {
        if (paren.hasDefault()) {
          paren.toggleRole();
        }
        else {
          setError(1, "parser.adt.role", paren.getName());
          return false;
        }
      }
    }
    paren.addCounter(child);
    return true;
  }

  private boolean addChild(ADTNode paren, ADTNode child) {
    paren.addChild(child);
    //Debug.log("adding node " + child.getName() + " to " + paren.getName() + " as child");
    if (paren.getRole() != child.getRole()) {
      if (child.hasDefault()) {
        child.toggleRole();
      }
      else {
        setError(1, "parser.adt.role", child.getName() + "N");
        setError(1, "parser.adt.role", child.getName());
        return false;
      }
    }
    return true;
  }

  private String getIdentifier(String token, int position) {
    token = token.trim();
    if (token.equals("ap") || token.equals("ao") || token.equals("oo") || token.equals("op")
        || token.equals("cp") || token.equals("co")) {
      setError(token.length(), "parser.after", token);
      return null;
    }
    if (token.length() == 0) {
      setError(1, "parser.noempty");
      return null;
    }
    return token;
  }

  private HashMap<Integer, Counter> counterMap;
}
