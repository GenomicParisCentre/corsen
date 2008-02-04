/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen.gui;

import java.io.File;

public final class GuiUtils {

  public static final String shortPath(final File file, final int maxLen) {

    if (file == null)
      return "";

    final String path = file.getAbsolutePath();
    final int len = path.length();

    if (len > maxLen) {

      int threshold = (int) (maxLen * 0.5);

      return path.substring(0, threshold > len ? len : threshold)
          + "..." + File.separator + file.getName();
    }

    return path;
  }

  public static final String shortPath(final String filename, final int maxLen) {

    if (filename == null || "".equals(filename))
      return "";

    return shortPath(new File(filename), maxLen);
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private GuiUtils() {
  }

}
