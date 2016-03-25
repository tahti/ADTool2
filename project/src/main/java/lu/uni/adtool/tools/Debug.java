package lu.uni.adtool.tools;
public final class Debug {

  public static void log (String msg){
    if(Options.debug_enable) {
      StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      if (stack.length > 2) {
        msg = stack[2].getFileName() + ":"+ stack[2].getLineNumber() + " " +stack[2].getMethodName() + "(): \t" + msg;
//         if (stack.length > 3) {
//         msg += "\n    "+stack[3].getFileName() + ":"+ stack[3].getLineNumber() + " " +stack[3].getMethodName() + "(): \t";
//         }
      }
      System.out.println(msg);
    }
  }
}
