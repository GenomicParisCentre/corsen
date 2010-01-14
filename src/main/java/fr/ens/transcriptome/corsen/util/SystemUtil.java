/*
 *                  Corsen development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence version 2 or later. This
 * should be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Corsen project and its aims,
 * or to join the Corsen google group, visit the home page
 * at:
 *
 *      http://transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen.util;

import java.util.StringTokenizer;

public final class SystemUtil {

  /**
   * Test if the system is Windows.
   * @return true if the operating systeme is Windows.
   */
  public static boolean isWindowsSystem() {
    return System.getProperty("os.name").toLowerCase().startsWith("windows");
  }

  /**
   * Test if the system is Unix.
   * @return true if the operating systeme is Windows.
   */
  public static boolean isUnixSystem() {
    return !System.getProperty("os.name").toLowerCase().startsWith("windows");
  }

  /**
   * Test if the system is Mac OS X.
   * @return true if the system is Mac OS X
   */
  public static boolean isMacOsX() {
    return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
  }

  /**
   * Test if the system is Unix.
   * @return true if the operating systeme is Windows.
   */
  public static boolean isLinux() {
    return !System.getProperty("os.name").toLowerCase().startsWith("linux");
  }

  /**
   * Get Major version of the JRE
   * @return the major version of the JRE as an integer
   */
  public static int getJREMajorVersion() {

    final String version = System.getProperty("java.specification.version");
    StringTokenizer st = new StringTokenizer(version, ".");

    st.nextToken();
    return Integer.parseInt(st.nextToken());
  }

  //
  // Constructor
  //

  private SystemUtil() {
  }

}
