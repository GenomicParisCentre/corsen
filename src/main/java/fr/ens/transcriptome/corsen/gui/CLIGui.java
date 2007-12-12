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

import fr.ens.transcriptome.corsen.Corsen;
import fr.ens.transcriptome.corsen.CorsenCore;
import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.util.Util;

public class CLIGui {

  private static final class CLIUpdateStatus implements UpdateStatus {

    private long timeStartCells;
    private long timeStartCell;

    public void endProcess(CorsenResult result) {

      long timeToDoACell = System.currentTimeMillis() - this.timeStartCell;
      System.out.println(result.getMinAnalyser());
      System.out.println("Process cell in "
          + Util.toTimeHumanReadable(timeToDoACell) + " (" + timeToDoACell
          + "ms).");
    }

    public void showError(String msg) {
      System.err.println(msg);
      System.exit(1);
    }

    public void showMessage(String msg) {
      // TODO Auto-generated method stub

    }

    public void updateStatus(ProgressEvent e) {

      switch (e.getType()) {

      case START_CELL_EVENT:
        this.timeStartCell = System.currentTimeMillis();
        System.out.println("Particle A file: " + e.getStringValue1());
        System.out.println("Particle B file: " + e.getStringValue2());
        break;

      case START_CELLS_EVENT:
        this.timeStartCells = System.currentTimeMillis();
        break;

      case END_CELLS_SUCCESSFULL_EVENT:
        final long timeToDoCells =
            System.currentTimeMillis() - this.timeStartCells;

        System.out.println("Process all cells in "
            + Util.toTimeHumanReadable(timeToDoCells) + " (" + timeToDoCells
            + "ms).");

        break;

      }

    }

    /**
     * Move to a thread. Needed by Qt.
     * @param thread Thread to move
     */
    public void moveToThread(Thread thread) {
    }

    /**
     * Chain the update status for the differents threads. Needed by Qt.
     * @return an UpdateStatus instance
     */
    public UpdateStatus chain() {

      return this;
    }

    /**
     * Create a new Thread.
     * @param runnable Runnable Object for the thread
     * @return a new Thread
     */
    public Thread newThread(final Runnable runnable) {

      return new Thread(runnable);
    }

  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    final Settings s = Corsen.getSettings();
    CorsenCore cc = new CorsenCore();

    cc.setSettings(s);
    cc.setUpdateStatus(new CLIUpdateStatus());

    if (Corsen.isBatchMode()) {

      cc.setMultipleFiles(true);
      cc.setDirFiles(new File(args[2]));

      s.setParticlesABatchPrefix(args[0]);
      s.setParticlesBBatchPrefix(args[1]);
    } else {

      cc.setMultipleFiles(false);
      cc.setParticlesAFile(new File(args[0]));
      cc.setParticlesBFile(new File(args[1]));
      cc.setResultFile(new File(args[2]));
    }

    new Thread(cc).start();

  }

}
