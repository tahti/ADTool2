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

import lu.uni.adtool.tools.Debug;

import java.util.Stack;

public class SandParser extends Parser {
  public SandParser() {
  }

  public SandNode parseString(String toParse) {
    String name = "";
    String token = "";
    SandNode result = null;
    SandNode last = null;
    Stack<SandNode> stack = new Stack<SandNode>();
    int lparen = 0;
    SandNode paren = null;
    for (position = 0; position < toParse.length(); position++) {
      char ch = toParse.charAt(position);
      switch (ch) {
      // case '"':
      // errString = Options.getMsg("parser.notallowed");
      // endError = position;
      // startError = position - 1;
      // return null;
      case '(':
        lparen++;
        token = name.trim();
        name = "";
        SandNode n = null;
        if (token.equals("OR")) {
          n = new SandNode(SandNode.Type.OR);
        }
        else if (token.equals("AND")) {
          n = new SandNode(SandNode.Type.AND);
        }
        else if (token.equals("SAND")) {
          n = new SandNode(SandNode.Type.SAND);
        }
        else {
          setError(token.length(), "parser.sand.keyword", token);
          return null;
        }
        if (!stack.empty()) {
        }
        else if (result == null) {
          result = n;
        }
        else {
          setError(1, "parser.oneroot");
          return null;
        }
        stack.push(n);
        break;

      case ',':
        Debug.log(" , getIdentifier name:" + name);
        token = getIdentifier(name, position);
        name = "";
        if (last == null) {
          if (token == null) {
            // Error set in getIdentifier
            return null;
          }
          last = new SandNode();
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
        paren.getNotNullChildren().add(last);
        last.setParent(paren);
        last = null;
        break;

      case ')':
        if (lparen <= 0) {
          setError(1, "parser.unmatched");
          return null;
        }
        lparen--;
        Debug.log(" ) getIdentifier name:" + name);
        token = getIdentifier(name, position);
        name = "";
        if (last == null) {
          if (token == null) {
            // Error set in getIdentifier
            return null;
          }
          last = new SandNode(); // leaf
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
        paren.getNotNullChildren().add(last);
        last.setParent(paren);

        last = stack.pop();
        break;

      case '\n': // change end of line and tab to space
      case '\t':
        ch = ' ';
      default:
        name = name + ch;
      }
    }
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
      // Debug.log("End of parse");
      return result;
    }
    else {
      if (token == null) {
        return null;
      }
      result = new SandNode();
      result.setName(token);
      return result;
    }
  }

  private String getIdentifier(String token, int position) {
    token = token.trim();
    if (token.equals("OR") || token.equals("AND") || token.equals("SAND")) {
      setError(token.length(), "parser.after", token);
      return null;
    }
    Debug.log("token len =" + token.length());
    if (token.length() == 0) {
      // not necessaryly an error
      setError(1, "parser.noempty");
      return null;
    }
    return token;
  }
}
