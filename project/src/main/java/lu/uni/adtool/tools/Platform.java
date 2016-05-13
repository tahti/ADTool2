/**
 * Author: Piotr Kordy (piotr.kordy@uni.lu <mailto:piotr.kordy@uni.lu>)
 * Date:   10/12/2015
 * Copyright (c) 2015,2013,2012 University of Luxembourg -- Faculty of Science,
 *     Technology and Communication FSTC
 * All rights reserved.
 * Licensed under GNU Affero General Public License 3.0;
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
