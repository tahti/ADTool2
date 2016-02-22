package lu.uni.adtool.tree;

import lu.uni.adtool.tools.Options;

public abstract class Parser {

  public Parser() {
  }

  public abstract Node parseString(String toParse);

  public String getErrorMessage() {
    return errString;
  }

  public int getStartErr() {
    return startError;
  }

  public int getEndErr() {
    return endError;

  }

  protected void setError(int markLen, String msgId) {
    endError = this.position;
    startError = this.position - markLen;
    errString = Options.getMsg(msgId);
  }

  protected void setError(int markLen, String msgId, String param) {
    endError = this.position;
    startError = this.position - markLen;
    errString = Options.getMsg(msgId, param);
  }

  protected String errString;
  protected int    startError;
  protected int    endError;
  protected int    position;
}
