package lu.uni.adtool.tools;

/** Provide simplified platform information. */
public final class Platform {
  public static final int UNSPECIFIED = -1;
  public static final int MAC = 0;
  public static final int LINUX = 1;
  public static final int WINDOWS = 2;
  public static final int WINDOWSCE = 6;


  private static final int osType;

  public static final int getOSType() {
    return osType;
  }
  public static final boolean isMac() {
    return osType == MAC;
  }
  public static final boolean isLinux() {
    return osType == LINUX;
  }
  public static final boolean isWindowsCE() {
    return osType == WINDOWSCE;
  }
  /** Returns true for any windows variant. */
  public static final boolean isWindows() {
    return osType == WINDOWS || osType == WINDOWSCE;
  }

  static {
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Linux")) {
      osType = LINUX;
    }
    else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
      osType = MAC;
    }
    else if (osName.startsWith("Windows CE")) {
      osType = WINDOWSCE;
    }
    else if (osName.startsWith("Windows")) {
      osType = WINDOWS;
    }
    else {
      osType = UNSPECIFIED;
    }
  }

  private Platform() { }

}
