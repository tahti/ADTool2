package lu.uni.adtool.domains.custom;

import java.util.ArrayList;
import java.util.Stack;

import lu.uni.adtool.domains.rings.Real;
import lu.uni.adtool.tools.Debug;

public class RealExpression {
  public enum Token {
    Y, X, LPAREN, RPAREN, COMMA, PLUS, MINUS, MUL, DIV, MODULO, MAX, MIN, ABS, REAL, NEG, POW, LOG, SIN, COS, TAN, ATAN, PI, E, SQRT
  }

  public void add(Term term) {
    this.terms.add(term);
  }

  public Real evaluate(Real x, Real y) {
    Stack<Real> stack = new Stack<Real>();
    int args = 0;
    Real result;
    for (Term term : this.terms) {
      switch (term.type) {
      case X:
        stack.push(x);
        break;
      case Y:
        stack.push(y);
        break;
      case PI:
        stack.push(new Real(Math.PI));
        break;
      case E:
        stack.push(new Real(Math.E));
        break;
      case PLUS:
        result = stack.pop();
        stack.push(new Real(stack.pop().getValue() + result.getValue()));
        break;
      case MINUS:
        result = stack.pop();
        stack.push(new Real(stack.pop().getValue() - result.getValue()));
        break;
      case MUL:
        result = stack.pop();
        stack.push(new Real(stack.pop().getValue() * result.getValue()));
        break;
      case DIV:
        result = stack.pop();
        stack.push(new Real(stack.pop().getValue() / result.getValue()));
        break;
      case MODULO:
        result = stack.pop();
        stack.push(new Real(stack.pop().getValue() % result.getValue()));
        break;
      case MAX:
        args = (int)term.value - 1;
        result = stack.pop();
        while (args > 0) {
          result = new Real(Math.max(stack.pop().getValue(), result.getValue()));
          args = args - 1;
        }
        stack.push(result);
        break;
      case MIN:
        args = (int)term.value - 1;
        result = stack.pop();
        while (args > 0) {
          result = new Real(Math.min(stack.pop().getValue(), result.getValue()));
          args = args - 1;
        }
        stack.push(result);
        break;
      case ABS:
        stack.push(new Real(Math.abs(stack.pop().getValue())));
        break;
      case NEG:
        stack.push(new Real(-1 * (stack.pop().getValue())));
        break;
      case REAL:
        stack.push(new Real(term.value));
        break;
      case POW:
        result = stack.pop();
        stack.push(new Real(Math.pow(stack.pop().getValue(), result.getValue())));
        break;
      case LOG:
        result = stack.pop();
        stack.push(new Real(Math.log(stack.pop().getValue()) / Math.log(result.getValue())));
        break;
      case SIN:
        stack.push(new Real(Math.sin(stack.pop().getValue())));
        break;
      case COS:
        stack.push(new Real(Math.cos(stack.pop().getValue())));
        break;
      case TAN:
        stack.push(new Real(Math.tan(stack.pop().getValue())));
        break;
      case ATAN:
        stack.push(new Real(Math.atan(stack.pop().getValue())));
        break;
      case SQRT:
        stack.push(new Real(Math.sqrt(stack.pop().getValue())));
        break;
      case COMMA:
      case LPAREN:
      case RPAREN:
        Debug.log("Bad expression");
        break;
      }
    }
    if (stack.size() != 1) {
      return null;
    }
    else {
      Real r = stack.pop();
      return r;
    }
  }

  public String toString() {
//     String todebug = "";
//     for (Term term: this.terms) {
//       todebug = todebug + term.type + "("+ term.value+ "), ";
//     }
//     Debug.log(todebug);
    Stack<String> stack = new Stack<String>();
    Stack<Token> precStack = new Stack<Token>();
    String x = "";
    String y = "";
    int precX;
    for (Term term : this.terms) {
      switch (term.type) {
      case REAL:
      case X:
      case Y:
      case E:
      case PI:
        stack.push(termToString(term));
        precStack.push(term.type);
        break;
      case NEG:
        x = stack.pop();
        precX = getPrecedence(precStack.pop());
        if (precX <= getPrecedence(term.type)) {
          x = termToString(new Term(Token.LPAREN, 0)) + x + termToString(new Term(Token.RPAREN, 0));
        }
        stack.push(termToString(term) + x);
        precStack.push(term.type);
        break;
      case ABS:
      case SIN:
      case COS:
      case TAN:
      case ATAN:
      case SQRT:
        x = stack.pop();
        precX = getPrecedence(precStack.pop());
        x = termToString(new Term(Token.LPAREN, 0)) + x + termToString(new Term(Token.RPAREN, 0));
        stack.push(termToString(term) + x);
        precStack.push(term.type);
        break;
      case MIN:
      case MAX:
        int args = (int)term.value - 1;
        x = stack.pop();
        precStack.pop();
        while (args > 0) {
          x = stack.pop() + ", " + x;
          precStack.pop();
          args = args - 1;
        }
        stack.push(termToString(term) + termToString(new Term(Token.LPAREN, 0)) + x
            + termToString(new Term(Token.RPAREN, 0)));
        precStack.push(term.type);
        break;
      case POW:
      case LOG:
        y = stack.pop();
        x = stack.pop();
        getPrecedence(precStack.pop());
        getPrecedence(precStack.pop());
        stack.push(termToString(term) + termToString(new Term(Token.LPAREN, 0)) + x + termToString(new Term(Token.COMMA, 0))+ " "+ y +
            termToString(new Term(Token.RPAREN, 0)));
        precStack.push(term.type);
        break;
      case PLUS:
      case MINUS:
      case MUL:
      case DIV:
      case MODULO:
        y = stack.pop();
        x = stack.pop();
        int precY = getPrecedence(precStack.pop());
        precX = getPrecedence(precStack.pop());
        if (precX <= getPrecedence(term.type)) {
          x = termToString(new Term(Token.LPAREN, 0)) + x + termToString(new Term(Token.RPAREN, 0));
        }
        if (precY <= getPrecedence(term.type)) {
          y = termToString(new Term(Token.LPAREN, 0)) + y + termToString(new Term(Token.RPAREN, 0));
        }
        stack.push(x + " " + termToString(term) + " " + y);
        precStack.push(term.type);
        break;
      case LPAREN:
      case RPAREN:
      case COMMA:
        //those should not be in the term
        break;
      }
    }
    return stack.pop();
  }

  public static int getPrecedence(Token token) {
    switch (token) {
    case X:
    case Y:
    case ABS:
    case MIN:
    case MAX:
    case REAL:
    case POW:
    case LOG:
    case SIN:
    case COS:
    case TAN:
    case ATAN:
    case PI:
    case E:
    case SQRT:
      return 5;
    case NEG:
      return 4;
    case MUL:
    case DIV:
      return 3;
    case MINUS:
    case PLUS:
      return 2;
    case COMMA:
      return 1;
    case LPAREN:
    case RPAREN:
    default:
      return 0;
    }
  }

  public int size() {
    return terms.size();
  }

  public RealExpression() {
    this.terms = new ArrayList<Term>();
  }

  public static boolean isFunction(Token token) {
    switch (token) {
    case ABS:
    case MIN:
    case MAX:
    case POW:
    case SIN:
    case COS:
    case TAN:
    case ATAN:
    case LOG:
      return true;
    default:
      return false;
    }
  }

  public static boolean checkArgumentCount(Term term) {
    switch (term.type) {
    case SIN:
    case COS:
    case TAN:
    case ATAN:
    case ABS:
    case NEG:
      return (int)term.value == 1;
    case MIN:
    case MAX:
      return (int)term.value > 0;
    case PLUS:
    case DIV:
    case MUL:
    case MODULO:
    case MINUS:
    case POW:
    case LOG:
      return (int)term.value == 2;
    default:
      return true;
    }
  }

  public static String termToString(Term term) {
    if (term == null) return "";
    switch (term.type) {
    case REAL:
      return Double.toString(term.value);
    case X:
      return "x";
    case Y:
      return "y";
    case PI:
      return "pi";
    case E:
      return "e";
    case PLUS:
      return "+";
    case MINUS:
      return "-";
    case DIV:
      return "/";
    case MUL:
      return "*";
    case MODULO:
      return "%";
    case ABS:
      return "abs";
    case MIN:
      return "min";
    case MAX:
      return "max";
    case POW:
      return "pow";
    case LOG:
      return "log";
    case SIN:
      return "sin";
    case COS:
      return "cos";
    case TAN:
      return "tan";
    case ATAN:
      return "atan";
    case SQRT:
      return "sqrt";
    case LPAREN:
      return "(";
    case RPAREN:
      return ")";
    case COMMA:
      return ",";
    default:
      return "";
    }
  }
//     Y, X, LPAREN, RPAREN, COMMA, PLUS, MINUS, MUL, DIV, MODULO, MAX, MIN, ABS, REAL, NEG

  public static Term getTerm(String term) {
    if (term.toLowerCase().equals("x")) {
      return new Term(Token.X, 0);
    }
    if (term.toLowerCase().equals("y")) {
      return new Term(Token.Y, 0);
    }
    if (term.equals("(")) {
      return new Term(Token.LPAREN, 0);
    }
    if (term.equals(")")) {
      return new Term(Token.RPAREN, 0);
    }
    if (term.equals("+")) {
      return new Term(Token.PLUS, 2);
    }
    if (term.equals("*")) {
      return new Term(Token.MUL, 2);
    }
    if (term.equals(",")) {
      return new Term(Token.COMMA, 0);
    }
    if (term.equals("/")) {
      return new Term(Token.DIV, 2);
    }
    if (term.equals("%")) {
      return new Term(Token.MODULO, 2);
    }
    if (term.equals("-")) {
      return new Term(Token.MINUS, 2);
    }
    if (term.toLowerCase().equals("max")) {
      return new Term(Token.MAX, 1);
    }
    if (term.toLowerCase().equals("min")) {
      return new Term(Token.MIN, 1);
    }
    if (term.toLowerCase().equals("abs")) {
      return new Term(Token.ABS, 1);
    }
    if (term.toLowerCase().equals("pow")) {
      return new Term(Token.POW, 2);
    }
    if (term.toLowerCase().equals("sqrt")) {
      return new Term(Token.SQRT, 1);
    }
    if (term.toLowerCase().equals("log")) {
      return new Term(Token.LOG, 2);
    }
    if (term.toLowerCase().equals("sin")) {
      return new Term(Token.SIN, 1);
    }
    if (term.toLowerCase().equals("cos")) {
      return new Term(Token.COS, 1);
    }
    if (term.toLowerCase().equals("tan")) {
      return new Term(Token.TAN, 1);
    }
    if (term.toLowerCase().equals("atan")) {
      return new Term(Token.ATAN, 1);
    }
    if (term.toLowerCase().equals("e")) {
      return new Term(Token.E, 1);
    }
    if (term.toLowerCase().equals("pi")) {
      return new Term(Token.PI, 1);
    }
    try {
      double value = Double.parseDouble(term);
      return new Term(Token.REAL, value);
    }
    catch (NumberFormatException e) {
    }
    return null;
  }

  public static class Term {
    public Term(Token type, double value) {
      this.type = type;
      this.value = value;
    }

    public Token type;
    public double value;
  }

  private ArrayList<Term> terms;
}
