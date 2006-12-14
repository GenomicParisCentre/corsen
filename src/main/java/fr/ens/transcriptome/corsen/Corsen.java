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

package fr.ens.transcriptome.corsen;

import java.io.IOException;

import fr.ens.transcriptome.corsen.gui.CorsenFakeGui;
import fr.ens.transcriptome.corsen.gui.qt.CorsenQt;
import fr.ens.transcriptome.corsen.gui.swing.CorsenSwing;

public class Corsen {

  public static void main(final String[] args) throws IOException {

    switch (Globals.DEFAULT_GUI) {

    case FAKE:
      CorsenFakeGui.main(args);
      return;

    case SWING:
      CorsenSwing.main(args);
      return;

    case QT:
      CorsenQt.main(args);
      return;

    default:
      System.err.println("No GUI defined.");
      System.exit(1);
      break;
    }

  }

}
