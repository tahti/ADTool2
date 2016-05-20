package lu.uni.adtool.domains.custom;

import lu.uni.adtool.tools.Debug;
import lu.uni.adtool.tree.Parser;

import java.util.ArrayList;
import java.util.Stack;

public class IntParser extends Parser {
  public IntParser() {
  }

  public IntExpression parseString(String toParse) {
    Debug.log("parsing:" + toParse);
    ArrayList<IntExpression.Term> tokens = tokenize(toParse);
    if (tokens == null || tokens.size() == 0) {
      Debug.log("tokens zero or null");
      return null;
    }
    IntExpression expression = new IntExpression();
    Stack<IntExpression.Term> stack = new Stack<IntExpression.Term>();
    Stack<Integer> arg = new Stack<Integer>();
    arg.push(new Integer(0));
    IntExpression.Term last = null;
    int args = 0;
    for (IntExpression.Term term : tokens) {
      switch (term.type) {
      case X:
      case Y:
      case INTEGER:
        arg.push(new Integer(arg.pop().intValue() + 1));
        expression.add(term);
        break;
      case NEG:
        expression.add(term);
        break;
      case ABS:
      case MIN:
      case MAX:
        arg.push(new Integer(0));
        term.value = 0; // start counting arguments from 0
        stack.push(term);
        break;
      case MUL:
      case DIV:
      case MINUS:
      case PLUS:
        while (!stack.empty()) {
          last = stack.peek();
          if (IntExpression.getPrecedence(term.type) <= IntExpression.getPrecedence(last.type)) {
            expression.add(stack.pop());
            arg.push(new Integer(arg.pop().intValue() - 1));
          }
          else {
            break;
          }
        }
        stack.push(term);
        break;

      case LPAREN:
        stack.push(term);

      case RPAREN:
        while ((!stack.empty()) && (stack.peek().type != IntExpression.Token.LPAREN)) {
          expression.add(stack.pop());
          arg.push(new Integer(arg.pop().intValue() - 1));
        }
        if (stack.empty()) {
          setError(1, "parser.unmatched");
          Debug.log("unmatched parenthesis");
          return null;
        }
        stack.pop();
        if (arg.peek().intValue() != 1) {
          setError(1, "parser.missingarg");
          Debug.log("missing arg 2" + " arg:" + Integer.toString(args));
          return null;
        }
        if (!stack.empty() && IntExpression.isFunction(stack.peek().type)) {
          IntExpression.Term t = stack.pop();
          t.value = t.value + 1;
          if (IntExpression.checkArgumentCount(t)) {
            expression.add(t);
            arg.pop();
          }
          else {
            setError(1, "parser.missingarg");
            Debug.log("missing arg 4a" + " arg:" + Integer.toString(t.value));
            return null;
          }
        }
        break;

      case COMMA:
        while ((!stack.empty()) && (stack.peek().type != IntExpression.Token.LPAREN)) {
          expression.add(stack.pop());
          arg.push(new Integer(arg.pop().intValue() - 1));
        }
        if (stack.empty()) {
          setError(1, "parser.misplaced");
          Debug.log("misplaced comma");
          return null;
        }
        if (arg.peek().intValue() != 1) {
          setError(1, "parser.missingarg");
          Debug.log("missing arg 4" + " arg:" + Integer.toString(args));
          return null;
        }
        else {
          stack.pop();
          if (!IntExpression.isFunction(stack.peek().type)) {
            setError(1, "parser.misplaced");
            Debug.log("misplaced comma 2");
            return null;
          }
          stack.peek().value = stack.peek().value + 1;
          arg.pop();
          arg.push(new Integer(0));
          stack.push(new IntExpression.Term(IntExpression.Token.LPAREN, 0));
        }
        break;
      }
    }
    if (!stack.empty()) {
      while (!stack.empty() && stack.peek().type != IntExpression.Token.LPAREN
          && stack.peek().type != IntExpression.Token.RPAREN) {
        expression.add(stack.pop());
        arg.push(new Integer(arg.pop().intValue() - 1));
      }
    }
    if (!stack.empty()) {
      setError(1, "parser.unmatched");
      Debug.log("unmatched parenthesis 2");
      return null;
    }
    if (arg.size() != 1 || arg.pop().intValue() != 1) {
      setError(1, "parser.missingarg");
      Debug.log("missing arg ");
      return null;
    }
    return expression;
  }

  private ArrayList<IntExpression.Term> tokenize(String toParse) {
    ArrayList<IntExpression.Term> terms = new ArrayList<IntExpression.Term>();
    IntExpression.Term term = null;
    String name = "";
    for (position = 0; position < toParse.length(); position++) {
      char ch = toParse.charAt(position);
      switch (ch) {
      case '(':
      case ')':
      case '+':
      case '-':
      case '/':
      case '*':
      case '%':
      case ',':
        if (name.length() > 0 && !this.addTerm(name, terms)) {
          return null;
        }
        name = "" + ch;
        break;
      case '\t':
      case '\n':
      case ' ':
        if (name.length() > 0 && !this.addTerm(name, terms)) {
          return null;
        }
        name = "";
        break;
      default:
        if (name.length() == 1
            && (name.equals("(") || name.equals(")") || name.equals("+") || name.equals("-")
                || name.equals("*") || name.equals("/") || name.equals("%") || name.equals(","))) {
          if (!this.addTerm(name, terms)) {
            return null;
          }
          else {
            name = "";
          }
        }
        name = name + ch;
        break;
      }
    }
    if (name.length() > 0 && !this.addTerm(name, terms)) {
      return null;
    }

    return terms;
  }

  private boolean addTerm(String term, ArrayList<IntExpression.Term> terms) {
    IntExpression.Term t = IntExpression.getTerm(term.trim());
    if (t == null) {
      setError(term.trim().length(), "parser.nokeyword", term.trim());
      Debug.log("No token:" + term.trim());
      return false;
    }
    // chaneg binary minus to unary minus operator in some cases
    if (t.type == IntExpression.Token.MINUS
        && (terms.size() == 0 || terms.get(terms.size() - 1).type == IntExpression.Token.PLUS
            || terms.get(terms.size() - 1).type == IntExpression.Token.LPAREN
            || terms.get(terms.size() - 1).type == IntExpression.Token.MUL
            || terms.get(terms.size() - 1).type == IntExpression.Token.DIV
            || terms.get(terms.size() - 1).type == IntExpression.Token.MODULO
            || terms.get(terms.size() - 1).type == IntExpression.Token.COMMA)) {
      t.type = IntExpression.Token.NEG;
      t.value = 1;
    }
    // add unary minus to the number itself
    if (t.type == IntExpression.Token.INTEGER && t.value > 0 && terms.size() > 0
        && terms.get(terms.size() - 1).type == IntExpression.Token.NEG) {
      t.value = -t.value;
      terms.set(terms.size() - 1, t);
    }
    else {
      terms.add(t);
    }
    return true;
  }
}
