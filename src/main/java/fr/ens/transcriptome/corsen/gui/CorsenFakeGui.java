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
 * of the �cole Normale Sup�rieure and the individual authors.
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
import java.io.IOException;
import java.io.InputStream;

import fr.ens.transcriptome.corsen.Corsen;
import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.DistanceAnalyser;
import fr.ens.transcriptome.corsen.calc.DistancesCalculator;
import fr.ens.transcriptome.corsen.calc.ParticleType;

public class CorsenFakeGui {

  private static final class FakeGuiUpdateStatus implements UpdateStatus {

    long last = -1;

    public void endProcess(CorsenResult result) {

      final long current = System.currentTimeMillis();

      System.out.println("time: " + (current - last));
    }

    public void showError(String msg) {
      System.err.println("msg: " + msg);

    }

    public void showMessage(String msg) {
      System.out.println("msg: " + msg);

    }

    public void updateStatus(ProgressEvent e) {

      if ("".equals(e.getType().toString()))
        return;

      final long current = System.currentTimeMillis();
      if (last != -1)
        System.out.println("time: " + (current - last));
      last = current;

      System.out.println("Start: " + e.getType().toString());

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

  public static void main(String[] args) throws IOException {

    System.out.println("Java version: " + System.getProperty("java.version"));

    InputStream isA;
    InputStream isB;

    if (true) {
      isA = Corsen.class.getResourceAsStream("/files/atp16cy3c1.par");
      isB = Corsen.class.getResourceAsStream("/files/mitocy35c1.par");
    } else {
      isA = Corsen.class.getResourceAsStream("/files/rna_hs_cy3.par");
      isB = Corsen.class.getResourceAsStream("/files/mito_hs_cy5.par");
    }

    Settings settings = new Settings();

    UpdateStatus updateStatus = new CorsenFakeGui.FakeGuiUpdateStatus();

    CorsenResult result =
        new CorsenResult(new File("atp16"), new File("mito"), isA, isB,
            settings, updateStatus);

    DistancesCalculator dc = new DistancesCalculator(result);
    dc.loadParticles();

    result.getMessengersParticles().setType(ParticleType.TINY);
    result.getMitosParticles().setType(ParticleType.HUGE);

    System.out.println("Messengers inner: "
        + result.getMessengersParticles().countParticlesInnerPoints()
        + " points.");
    System.out.println("Messengers surface: "
        + result.getMessengersParticles().countParticlesSurfacePoints()
        + " points.");
    System.out.println("Messengers image filename: "
        + result.getMessengersParticles().getImageFilename());
    System.out.println("Mitos inner: "
        + result.getMitosParticles().countParticlesInnerPoints() + " points.");
    System.out
        .println("Mitos surface: "
            + result.getMitosParticles().countParticlesSurfacePoints()
            + " points.");
    System.out.println("Mitos image filename: "
        + result.getMitosParticles().getImageFilename());

    long startTime = System.currentTimeMillis();
    dc.calc();
    long endTime = System.currentTimeMillis();

    System.out.println("exec time: " + (endTime - startTime) + " ms.");

    DistanceAnalyser da = new DistanceAnalyser(result.getMinDistances());

    System.out.println("Median: " + da.getMedian());

    settings.setThreadNumber(2);

    startTime = System.currentTimeMillis();
    dc.calc();
    endTime = System.currentTimeMillis();

    System.out.println("exec time: " + (endTime - startTime) + " ms.");

    da = new DistanceAnalyser(result.getMinDistances());

    System.out.println("Median: " + da.getMedian());

  }

}
