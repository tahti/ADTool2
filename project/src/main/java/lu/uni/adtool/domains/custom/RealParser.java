package lu.uni.adtool.domains.custom;

import java.util.ArrayList;
import java.util.Stack;

import lu.uni.adtool.tree.Parser;

public class RealParser extends Parser {
  public RealParser() {
  }

  public RealExpression parseString(String toParse) {
//     Debug.log("parsing:" + toParse);
    ArrayList<RealExpression.Term> terms = tokenize(toParse);
    if (terms == null || terms.size() == 0) {
      return null;
    }
    RealExpression expression = new RealExpression();
    Stack<RealExpression.Term> stack = new Stack<RealExpression.Term>();
    Stack<Integer> arg = new Stack<Integer>();
    arg.push(new Integer(0));
    RealExpression.Term last = null;
    for (RealExpression.Term term : terms) {
      switch (term.type) {
      case X:
      case Y:
      case REAL:
      case PI:
      case E:
        arg.push(new Integer(arg.pop().intValue() + 1));
        expression.add(term);
        break;
      case NEG:
        expression.add(term);
        break;
      case ABS:
      case MIN:
      case MAX:
      case POW:
      case LOG:
      case SIN:
      case COS:
      case TAN:
      case ATAN:
      case SQRT:
        arg.push(new Integer(0));
        term.value = 0; // start counting arguments from 0
        stack.push(term);
        break;
      case MUL:
      case DIV:
      case MINUS:
      case PLUS:
      case MODULO:
        while (!stack.empty()) {
          last = stack.peek();
          if (RealExpression.getPrecedence(term.type) <= RealExpression.getPrecedence(last.type)) {
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
        if(stack.empty()||(!RealExpression.isFunction(stack.peek().type))) {
          arg.push(new Integer(0));
        }
        stack.push(term);
        break;

      case RPAREN:
        while ((!stack.empty()) && (stack.peek().type != RealExpression.Token.LPAREN)) {
          expression.add(stack.pop());
          arg.push(new Integer(arg.pop().intValue() - 1));
        }
        if (stack.empty()) {
          setError(1, "parser.unmatched");
          return null;
        }
        stack.pop();
        if (arg.peek().intValue() != 1) {
          setError(1, "parser.missingarg");
          return null;
        }
        if (!stack.empty() && RealExpression.isFunction(stack.peek().type)) {
          RealExpression.Term t = stack.pop();
          t.value = t.value + 1;
          if (RealExpression.checkArgumentCount(t)) {
            expression.add(t);
            arg.pop();
          }
          else {
            setError(1, "parser.missingarg");
            return null;
          }
          arg.push(new Integer(arg.pop().intValue() + 1));
        }
        else {
          arg.pop();
          arg.push(new Integer(arg.pop().intValue() + 1));
        }
        break;
      case COMMA:
        while ((!stack.empty()) && (stack.peek().type != RealExpression.Token.LPAREN)) {
          expression.add(stack.pop());
          arg.push(new Integer(arg.pop().intValue() - 1));
        }
        if (stack.empty()) {
          setError(1, "parser.misplaced");
          return null;
        }
        if (arg.peek().intValue() != 1) {
          setError(1, "parser.missingarg");
          return null;
        }
        else {
          stack.pop();
          if (!RealExpression.isFunction(stack.peek().type)) {
            setError(1, "parser.misplaced");
            return null;
          }
          stack.peek().value = stack.peek().value + 1;
          arg.pop();
          arg.push(new Integer(0));
          stack.push(new RealExpression.Term(RealExpression.Token.LPAREN, 0));
        }
        break;
      }
    }
    if (!stack.empty()) {
      while (!stack.empty() && stack.peek().type != RealExpression.Token.LPAREN
          && stack.peek().type != RealExpression.Token.RPAREN) {
        expression.add(stack.pop());
        arg.push(new Integer(arg.pop().intValue() - 1));
      }
    }
    if (!stack.empty()) {
      setError(1, "parser.unmatched");
      return null;
    }
    if (arg.size() != 1) {
      setError(1, "parser.missingarg");
      return null;
    }
    else {
      Integer i = arg.pop();
      if (i.intValue() != 1) {
        setError(1, "parser.missingarg");
        return null;
      }
    }
    return expression;
  }

  private ArrayList<RealExpression.Term> tokenize(String toParse) {
    ArrayList<RealExpression.Term> terms = new ArrayList<RealExpression.Term>();
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

  private boolean addTerm(String term, ArrayList<RealExpression.Term> terms) {
    RealExpression.Term t = RealExpression.getTerm(term.trim());
    if (t == null) {
      setError(term.trim().length(), "parser.nokeyword", term.trim());
      return false;
    }
    // chaneg binary minus to unary minus operator in some cases
    if (t.type == RealExpression.Token.MINUS
        && (terms.size() == 0 || terms.get(terms.size() - 1).type == RealExpression.Token.PLUS
            || terms.get(terms.size() - 1).type == RealExpression.Token.LPAREN
            || terms.get(terms.size() - 1).type == RealExpression.Token.MUL
            || terms.get(terms.size() - 1).type == RealExpression.Token.DIV
            || terms.get(terms.size() - 1).type == RealExpression.Token.MODULO
            || terms.get(terms.size() - 1).type == RealExpression.Token.COMMA)) {
      t.type = RealExpression.Token.NEG;
      t.value = 1;
    }
    // add unary minus to the number itself
    if (t.type == RealExpression.Token.REAL && t.value > 0 && terms.size() > 0
        && terms.get(terms.size() - 1).type == RealExpression.Token.NEG) {
      t.value = -t.value;
      terms.set(terms.size() - 1, t);
    }
    else {
      terms.add(t);
    }
    return true;
  }
}
