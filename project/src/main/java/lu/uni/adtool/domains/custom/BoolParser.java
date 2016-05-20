/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>) Date:
 * 10/12/2015 Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty
 * of Science, Technology and Communication FSTC All rights reserved. Licensed
 * under GNU Affero General Public License 3.0; This program is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package lu.uni.adtool.domains.custom;

import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tree.Parser;

import java.util.ArrayList;
import java.util.Stack;

public class BoolParser extends Parser {

  public BoolParser() {
  }

  public BoolExpression parseString(String toParse) {
    Debug.log("parsing:" + toParse);
    ArrayList<BoolExpression.Token> tokens = tokenize(toParse);
    if (tokens == null || tokens.size() == 0) {
      Debug.log("tokens zero or null");
      return null;
    }
    BoolExpression expression = new BoolExpression();
    Stack<BoolExpression.Token> stack = new Stack<BoolExpression.Token>();
    int args = 0;
    BoolExpression.Token last = null;
    for (BoolExpression.Token token : tokens) {
      switch (token) {
      case TRUE:
      case FALSE:
      case X:
      case Y:
        args = args + 1;
        expression.add(token);
        break;
      case NEG:
        expression.add(token);
        break;
      case OR:
      case NOR:
      case AND:
      case NAND:
      case XOR:
      case EQ:
      case NEQ:
      case IMP:
      case CIMP:
        while (!stack.empty()) {
          last = stack.peek();
          // assuming left-associativity
          if (BoolExpression.getPrecedence(token) <= BoolExpression.getPrecedence(last)) {
            expression.add(stack.pop());
            args = args - 1;
          }
          else {
            break;
          }
        }
        stack.push(token);
        break;
      case LPAREN:
        stack.push(token);
        break;
      case RPAREN:
        while ((!stack.empty()) && (stack.peek() != BoolExpression.Token.LPAREN)) {
          expression.add(stack.pop());
          args = args - 1;
        }
        if (stack.empty()) {
          setError(1, "parser.unmatched");
          Debug.log("unmatched parenthesis");
          return null;
        }
        stack.pop();
        break;
      }
    }
    if (!stack.empty()) {
      while (!stack.empty() && stack.peek() != BoolExpression.Token.LPAREN
          && stack.peek() != BoolExpression.Token.RPAREN) {
        expression.add(stack.pop());
        args = args - 1;
      }
    }
    if (!stack.empty()) {
      setError(1, "parser.unmatched");
      Debug.log("unmatched parenthesis 2");
      return null;
    }
    if (args != 1) {
      setError(1, "parser.missingarg");
      Debug.log("missing arg " + " arg:"+ Integer.toString(args));
      return null;
    }
    if (expression.size() == 0) {
      return null;
    }
    return expression;
  }

  private ArrayList<BoolExpression.Token> tokenize(String toParse) {
    ArrayList<BoolExpression.Token> tokens = new ArrayList<BoolExpression.Token>();
    BoolExpression.Token token = null;
    String name = "";
    for (position = 0; position < toParse.length(); position++) {
      char ch = toParse.charAt(position);
      switch (ch) {
      case '(':
      case ')':
        if (name.length() > 0) {
          token = BoolExpression.getToken(name.trim());
          if (token == null) {
            setError(name.trim().length(), "parser.nokeyword", name.trim());
            Debug.log("No token:"+name.trim());
            return null;
          }
          tokens.add(token);
        }
        name = "" + ch;
        break;
      case '\t':
      case '\n':
      case ' ':
        if (name.length() > 0) {
          token = BoolExpression.getToken(name.trim());
          token = BoolExpression.getToken(name);
          Debug.log("break:" + ch + " name:" + name);
          if (token == null) {
            setError(name.trim().length(), "parser.nokeyword", name.trim());
            Debug.log("No token2:"+name.trim());
            return null;
          }
          tokens.add(token);
          name = "";
        }
        break;
      case '=':
      case '>':
      case '<':
      case '!':
        if (name.length() > 0 && this.isAlpha(name)) {
          token = BoolExpression.getToken(name);
          if (token == null) {
            setError(name.trim().length(), "parser.nokeyword", name.trim());
            Debug.log("No token3:"+name.trim());
            return null;
          }
          tokens.add(token);
          name = "" + ch;
        }
        else {
          name = name + ch;
        }
        break;
      default:
        if (name.length() > 0 && (!this.isAlpha(name))) {
          token = BoolExpression.getToken(name);
          if (token == null) {
            setError(name.trim().length(), "parser.nokeyword", name.trim());
            Debug.log("No token4:"+name.trim());
            return null;
          }
          tokens.add(token);
          name = "" + ch;
        }
        else {
          name = name + ch;
          Debug.log("default:" + ch + " name:" + name);
        }
        break;
      }
    }
    if (!name.equals("")) {
      token = BoolExpression.getToken(name);
      if (token == null) {
        setError(name.trim().length(), "parser.nokeyword", name.trim());
        return null;
      }
      tokens.add(token);
    }
    return tokens;
  }

  public boolean isAlpha(String name) {
    char[] chars = name.toCharArray();

    for (char c : chars) {
      if (!Character.isLetter(c)) {
        return false;
      }
    }
    return true;
  }
}
