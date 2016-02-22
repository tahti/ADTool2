package lu.uni.adtool.tools;
public final class Debug {

  public static void log (String msg){
    if(Options.debug_enable) {
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      if (stack.length > 2) {
        msg = stack[2].getFileName() + ":"+ stack[2].getLineNumber() + " " +stack[2].getMethodName() + "(): \t" + msg;
      }
      System.out.println(msg);
    }
  }
}
