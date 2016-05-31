package lu.uni.adtool.domains.custom;

import java.util.ArrayList;
import java.util.Stack;

import lu.uni.adtool.domains.rings.Int;
import lu.uni.adtool.tools.Debug;

public class IntExpression {
  public enum Token {
    Y, X, LPAREN, RPAREN, COMMA, PLUS, MINUS, MUL, DIV, MODULO, MAX, MIN, ABS, INTEGER, NEG
  }

  public void add(Term term) {
    this.terms.add(term);
  }

  public Int evaluate(Int x, Int y) {
    Stack<Int> stack = new Stack<Int>();
    int args = 0;
    Int result;
    for (Term term : this.terms) {
      switch (term.type) {
      case X:
        stack.push(x);
        break;
      case Y:
        stack.push(y);
        break;
      case PLUS:
        result = stack.pop();
        stack.push(new Int(stack.pop().getValue() + result.getValue()));
        break;
      case MINUS:
        result = stack.pop();
        stack.push(new Int(stack.pop().getValue() - result.getValue()));
        break;
      case MUL:
        result = stack.pop();
        stack.push(new Int(stack.pop().getValue() * result.getValue()));
        break;
      case DIV:
        result = stack.pop();
        stack.push(new Int(stack.pop().getValue() / result.getValue()));
        break;
      case MODULO:
        result = stack.pop();
        stack.push(new Int(stack.pop().getValue() % result.getValue()));
        break;
      case MAX:
        args = term.value - 1;
        result = stack.pop();
        while (args > 0) {
          result = new Int(Math.max(stack.pop().getValue(), result.getValue()));
          args = args - 1;
        }
        stack.push(result);
        break;
      case MIN:
        args = term.value - 1;
        result = stack.pop();
        while (args > 0) {
          result = new Int(Math.min(stack.pop().getValue(), result.getValue()));
          args = args - 1;
        }
        stack.push(result);
        break;
      case ABS:
        stack.push(new Int(Math.abs(stack.pop().getValue())));
        break;
      case NEG:
        stack.push(new Int(-1 * (stack.pop().getValue())));
        break;
      case INTEGER:
        stack.push(new Int(term.value));
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
      Int r = stack.pop();
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
      case INTEGER:
      case X:
      case Y:
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
        x = stack.pop();
        precX = getPrecedence(precStack.pop());
        x = termToString(new Term(Token.LPAREN, 0)) + x + termToString(new Term(Token.RPAREN, 0));
        stack.push(termToString(term) + x);
        precStack.push(term.type);
        break;
      case MIN:
      case MAX:
        int args = term.value - 1;
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
    case INTEGER:
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

  public IntExpression() {
    this.terms = new ArrayList<Term>();
  }

  public static boolean isFunction(Token token) {
    switch (token) {
    case ABS:
    case MIN:
    case MAX:
      return true;
    default:
      return false;
    }
  }

  public static boolean checkArgumentCount(Term term) {
    switch (term.type) {
    case ABS:
    case NEG:
      return term.value == 1;
    case MIN:
    case MAX:
      return term.value > 0;
    case PLUS:
    case DIV:
    case MUL:
    case MODULO:
    case MINUS:
      return term.value == 2;
    default:
      return true;
    }
  }

  public static String termToString(Term term) {
    if (term == null) return "";
    switch (term.type) {
    case INTEGER:
      return Integer.toString(term.value);
    case X:
      return "x";
    case Y:
      return "y";
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
//     Y, X, LPAREN, RPAREN, COMMA, PLUS, MINUS, MUL, DIV, MODULO, MAX, MIN, ABS, INTEGER, NEG

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
    try {
      int value = Integer.parseInt(term);
      return new Term(Token.INTEGER, value);
    }
    catch (NumberFormatException e) {
    }
    return null;
  }

  public static class Term {
    public Term(Token type, int value) {
      this.type = type;
      this.value = value;
    }

    public Token type;
    public int   value;
  }

  private ArrayList<Term> terms;
}
