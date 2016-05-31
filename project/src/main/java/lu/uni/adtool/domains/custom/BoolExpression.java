package lu.uni.adtool.domains.custom;

import lu.uni.adtool.domains.rings.Bool;

import java.util.ArrayList;
import java.util.Stack;

public class BoolExpression {
  public enum Token {
    OR, NOR, AND, NAND, XOR, EQ, NEQ, IMP, CIMP/* converse implication */, NEG, TRUE, FALSE, Y, X, LPAREN, RPAREN
  }

  public BoolExpression() {
    this.tokens = new ArrayList<Token>();
  }

  public void add(Token token) {
    this.tokens.add(token);
  }

  public String toString(boolean useUnicode) {
    Stack<String> stack = new Stack<String>();
    Stack<Token> precStack = new Stack<Token>();
    String x = "";
    String y = "";
    int precX;
    for (Token token : this.tokens) {
      switch (token) {
      case TRUE:
      case FALSE:
      case X:
      case Y:
        stack.push(tokenToString(token, useUnicode));
        precStack.push(token);
        break;
      case NEG:
        x = stack.pop();
        precX = getPrecedence(precStack.pop());
        if (precX <= getPrecedence(token)) {
          x = tokenToString(Token.LPAREN, useUnicode) + x + tokenToString(Token.RPAREN, useUnicode);
        }

        stack.push(tokenToString(token, useUnicode) + " " + x);
        precStack.push(token);
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
        y = stack.pop();
        x = stack.pop();
        int precY = getPrecedence(precStack.pop());
        precX = getPrecedence(precStack.pop());
        if (precX <= getPrecedence(token)) {
          x = tokenToString(Token.LPAREN, useUnicode) + x + tokenToString(Token.RPAREN, useUnicode);
        }
        if (precY <= getPrecedence(token)) {
          y = tokenToString(Token.LPAREN, useUnicode) + y + tokenToString(Token.RPAREN, useUnicode);
        }
        stack.push(x + " " + tokenToString(token, useUnicode) + " " + y);
        precStack.push(token);
        break;
      case LPAREN:
      case RPAREN:
        break;
      }
    }
    return stack.pop();
  }

  public Bool evaluate(Bool x, Bool y) {
    Stack<Bool> stack = new Stack<Bool>();
    Bool top = null;
    for (Token token : this.tokens) {
      switch (token) {
      case TRUE:
        stack.push(new Bool(true));
        break;
      case FALSE:
        stack.push(new Bool(false));
        break;
      case X:
        stack.push(x);
        break;
      case Y:
        stack.push(y);
        break;
      case OR:
        top = stack.pop();
        stack.push(Bool.or(stack.pop(), top));
        break;
      case NOR:
        top = stack.pop();
        stack.push(Bool.nor(stack.pop(), top));
        break;
      case AND:
        top = stack.pop();
        stack.push(Bool.and(stack.pop(), top));
        break;
      case NAND:
        top = stack.pop();
        stack.push(Bool.nand(stack.pop(), top));
        break;
      case XOR:
        top = stack.pop();
        stack.push(Bool.xor(stack.pop(), top));
        break;
      case EQ:
        top = stack.pop();
        stack.push(Bool.eq(stack.pop(), top));
        break;
      case NEQ:
        top = stack.pop();
        stack.push(Bool.neq(stack.pop(), top));
        break;
      case IMP:
        top = stack.pop();
        stack.push(Bool.implication(stack.pop(), top));
        break;
      case CIMP:
        top = stack.pop();
        stack.push(Bool.counterImplication(stack.pop(), top));
        break;
      case NEG:
        stack.push(Bool.not(stack.pop()));
        break;
      default:
        return null;
      }
    }
    if (stack.size() != 1) {
      return null;
    }
    else {
      return stack.pop();
    }
  }

  public static int getPrecedence(Token token) {
    switch (token) {
    case TRUE:
    case FALSE:
    case X:
    case Y:
      return 4;
    case NEG:
      return 3;
    case OR:
    case NOR:
    case AND:
    case NAND:
    case XOR:
      return 2;
    case EQ:
    case NEQ:
    case IMP:
    case CIMP:
      return 1;
    case LPAREN:
    case RPAREN:
    default:
      return 0;
    }
  }

  public int size() {
    return tokens.size();
  }

  public static Token getToken(String token) {
    if (token.toLowerCase().equals("x")) {
      return Token.X;
    }
    if (token.toLowerCase().equals("y")) {
      return Token.Y;
    }
    if (token.equals("(")) {
      return Token.LPAREN;
    }
    if (token.equals(")")) {
      return Token.RPAREN;
    }
    if (token.toLowerCase().equals("true")) {
      return Token.TRUE;
    }
    if (token.toLowerCase().equals("false")) {
      return Token.FALSE;
    }
    if (token.toLowerCase().equals("not") || token.equals("\u00AC")) {
      return Token.NEG;
    }
    if (token.toLowerCase().equals("or") || token.equals("\u2228")) {
      return Token.OR;
    }
    if (token.toLowerCase().equals("nor") || token.equals("\u22BC")) {
      return Token.NOR;
    }
    if (token.toLowerCase().equals("and") || token.equals("\u2227")) {
      return Token.AND;
    }
    if (token.toLowerCase().equals("nand") || token.equals("\u22BC")) {
      return Token.NAND;
    }
    if (token.toLowerCase().equals("xor") || token.equals("\u2295")) {
      return Token.XOR;
    }
    if (token.equals("==") || token.equals("\u21D4") || token.equals("<=>")
        || token.equals("=")) {
      return Token.EQ;
    }
    if (token.equals("!=") || token.equals("\u21CE")) {
      return Token.NEQ;
    }
    if (token.equals("=>") || token.equals("\u21D2")) {
      return Token.IMP;
    }
    if (token.equals("<=") || token.equals("\u21D0")) {
      return Token.CIMP;
    }
    return null;
  }

  public static String tokenToString(Token token, boolean useUnicode) {
    if (token == null) return "";
    if (useUnicode) {
      switch (token) {
      case X:
        return "x";
      case Y:
        return "y";
      case TRUE:
        return "true";
      case FALSE:
        return "false";
      case NEG:
        return "\u00AC";
      case OR:
        return "\u2228";
      case NOR:
        return "\u22BC";
      case AND:
        return "\u2227";
      case NAND:
        return "\u22BC";
      case XOR:
        return "\u2295";
      case EQ:
        return "\u21D4";
      case NEQ:
        return "\u21CE";
      case IMP:
        return "\u21D2";
      case CIMP:
        return "\u21D0";
      case LPAREN:
        return "(";
      case RPAREN:
        return ")";
      default:
        return "";
      }
    }
    else {
      switch (token) {
      case X:
        return "x";
      case Y:
        return "y";
      case TRUE:
        return "true";
      case FALSE:
        return "false";
      case NEG:
        return "not";
      case OR:
        return "or";
      case NOR:
        return "nor";
      case AND:
        return "and";
      case NAND:
        return "nand";
      case XOR:
        return "xor";
      case EQ:
        return "==";
      case NEQ:
        return "!=";
      case IMP:
        return "=>";
      case CIMP:
        return "<=";
      case LPAREN:
        return "(";
      case RPAREN:
        return ")";
      default:
        return "";
      }
    }
  }

  private ArrayList<Token> tokens;
}
