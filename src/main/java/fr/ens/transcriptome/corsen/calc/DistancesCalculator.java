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

package fr.ens.transcriptome.corsen.calc;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.util.GuidedLoopHandler;
import fr.ens.transcriptome.corsen.util.MinMaxList;

/**
 * This class implements the iterator over particles list to calc all the
 * distances.
 * @author Laurent Jourdren
 */
public class DistancesCalculator {

  private Logger logger = Logger.getLogger(Globals.APP_NAME);
  private DistanceProcessor processorA;
  private DistanceProcessor processorB;

  private float zCoordinatesFactor = 1.0f;
  private float coordinatesFactor = 1.0f;

  private UpdateStatus updateStatus;
  private UncaughtExceptionHandler uceh;
  private CorsenResult result;

  /**
   * This class define the multithreaded version of the calcOneThread method.
   * @author Laurent Jourdren
   */
  private final class CalcThread extends GuidedLoopHandler {

    private DistanceProcessor currentProcessorB;
    private List<Particle3D> listB;
    private Particle3D currentParticleA;
    private AbstractListPoint3D currentPointsA;
    private List<Distance> distances;

    CalcThread(int start, int end, int min, int threads) {
      super(start, end, min, threads);
    }

    /**
     * The method that is multithreaded.
     * @param start start index of the range
     * @param end end index of the range
     */
    public void loopDoRange(final int start, final int end) {

      final List<Distance> distances = new MinMaxList<Distance>();

      for (int i = start; i < end; i++) {

        Particle3D parB = this.listB.get(i);
        distances.addAll(this.currentProcessorB.calcDistance(parB,
            currentPointsA, currentParticleA));
      }

      synchronized (this.distances) {
        this.distances.addAll(distances);
      }
    }

    /**
     * The multithreaded version of the calcOneThread method
     * @param processorA processor for particles A
     * @param processorB processor for particles B
     * @param listA list of particles A
     * @param listB list of particles B
     * @param mins map of min distances
     * @param maxs map of max distances
     */
    public void calc(final DistanceProcessor processorA,
        final DistanceProcessor processorB, final List<Particle3D> listA,
        final List<Particle3D> listB, Map<Particle3D, Distance> mins,
        Map<Particle3D, Distance> maxs) {

      this.currentProcessorB = processorB;
      this.listB = listB;

      int count = 0;
      int lastShow = 0;
      final int listBSize = listB.size();
      final int calcsToDoNumber = listA.size() * listBSize;
      final int calcsBeforeUpdateInfo = calcsToDoNumber / 100;

      logger.info("Thread number for distance computation: " + this.numThreads);
      final long startCalcs = System.currentTimeMillis();

      if (calcsBeforeUpdateInfo != 0)
        for (Particle3D parA : listA) {

          this.currentParticleA = parA;
          this.currentPointsA =
              processorA.getPresentationPointsA(parA.getInnerPoints());
          this.distances = new MinMaxList<Distance>();

          this.setRange(0, listBSize);
          loopProcess();

          count += listBSize;

          if (count - lastShow >= calcsBeforeUpdateInfo) {

            lastShow = count;
            sendEvent(
                ProgressEventType.PROGRESS_CALC_DISTANCES_EVENT,
                (int) (((double) count / (double) calcsToDoNumber) * ProgressEvent.INDEX_IN_PHASE_MAX));
          }

          mins.put(parA, Collections.min(distances));
          maxs.put(parA, Collections.max(distances));
        }

      logger.info("Calc "
          + count + " distances in "
          + (System.currentTimeMillis() - startCalcs) + " ms.");
    }

  }

  /**
   * Get the zFactor.
   * @return Returns the zFactor
   */
  public float getZCoordinatesFactor() {
    return this.zCoordinatesFactor;
  }

  /**
   * Get the factor for the coordinates.
   * @return Returns the factor
   */
  public float getCoordinatesFactor() {
    return this.coordinatesFactor;
  }

  //
  // Setters
  //

  /**
   * Set the z factor for the coordinates.
   * @param factor The zFactor to set
   */
  public void setZCoordinatesFactor(final float factor) {
    this.zCoordinatesFactor = factor;
  }

  /**
   * Set the factor for the coordinates.
   * @param factor The factor to set
   */
  public void setCoordinatesFactor(final float factor) {
    this.coordinatesFactor = factor;
  }

  //
  // Other methods
  //

  private UncaughtExceptionHandler getUncaughtExceptionHandler(
      final UpdateStatus updateStatus) {

    if (this.uceh == null)

      this.uceh = new Thread.UncaughtExceptionHandler() {

        public void uncaughtException(Thread t, Throwable e) {
          // TODO Auto-generated method stub

          System.err.println(t.getName() + " Exception: " + e.toString());
          e.printStackTrace();

          if (updateStatus == null)
            return;

          updateStatus.showError(t.getName() + " Exception: " + e.toString());
          // updateStatus.showError(e.toString());

        }

      };

    return this.uceh;
  }

  /**
   * Determine the processor to use for a particles object and set the source
   * particles.
   * @param particles Input Particles
   * @return a new Processor object
   */
  private DistanceProcessor getProcessor(final Particles3D particles) {

    if (particles == null)
      return null;

    DistanceProcessor result = particles.getType().getDistanceProcessor();
    result.setSourceParticles(particles);

    return result;
  }

  /**
   * Change if needed the coordinates of the particles.
   */
  private void changeFactors(final Particles3D particles) {

    if (particles == null)
      return;

    particles.changeZCoord(zCoordinatesFactor);
    particles.changeAllCoord(coordinatesFactor);
  }

  /**
   * Load and transform the coordinates of a particles file.
   * @throws IOException if an error occurs while reading the file
   */
  public void loadParticles() throws IOException {

    final CorsenResult result = this.result;
    final String unit = result.getSettings().getUnit();

    // Read messengers
    sendEvent(ProgressEventType.START_READ_PARTICLES_A_FILE_EVENT);
    Particles3D particlesA = new Particles3D(result.getMessengersStream());
    particlesA.setName(result.getSettings().getParticlesAName());
    logger.info("ParticlesA: "
        + particlesA.getInnerPointCount() + " inner points, "
        + particlesA.getSurfacePointCount() + " sufrace points.");

    // Transform coordinates of messengers
    sendEvent(ProgressEventType.START_CHANGE_PARTICLES_A_COORDINATES_EVENT);
    changeFactors(particlesA);

    // Read mitos
    sendEvent(ProgressEventType.START_READ_PARTICLES_B_FILE_EVENT);
    Particles3D particlesB = new Particles3D(result.getMitosStream());
    particlesB.setName(result.getSettings().getParticlesBName());
    logger.info("ParticlesB: "
        + particlesB.getInnerPointCount() + " inner points, "
        + particlesB.getSurfacePointCount() + " sufrace points.");

    // Transform coordinates of messengers
    sendEvent(ProgressEventType.START_CHANGE_PARTICLES_B_COORDINATES_EVENT);
    changeFactors(particlesB);

    // Change unit if necessary
    if (unit != null && !"".equals(unit)) {
      particlesA.setUnitOfLength(unit);
      particlesB.setUnitOfLength(unit);
    }

    // Apply filters
    sendEvent(ProgressEventType.START_FILTER_PARTICLES_A_EVENT);
    result.setParticlesA(particlesA.filter(result.getParticlesAFilter()));

    sendEvent(ProgressEventType.START_FILTER_PARTICLES_B_EVENT);
    result.setParticlesB(particlesB.filter(result.getParticlesBFilter()));
  }

  private Thread preprocessExecution(final DistanceProcessor processor,
      final boolean useThreads, final ProgressEventType event,
      final String threadName) {

    final long start = System.currentTimeMillis();

    if (!useThreads) {
      processor.preprocess(event);
      logger.info("End "
          + threadName + " (process in " + (System.currentTimeMillis() - start)
          + " ms).");
      return null;
    }

    final Thread result = new Thread(new Runnable() {

      public void run() {

        processor.preprocess(event);
        logger.info("End "
            + threadName + " thread (process in "
            + (System.currentTimeMillis() - start) + " ms).");
      }

    });

    processor.getUpdateStatus().moveToThread(result);
    result.setName(threadName + " thread");
    result.setUncaughtExceptionHandler(getUncaughtExceptionHandler(processor
        .getUpdateStatus()));
    result.start();

    return result;
  }

  /**
   * Preprocess the computation.
   * @param useThreads true if threads must be used to do the preprocessing
   * @return true if the preprocess is ok
   * @throws IOException if an error occurs while reading input streams
   */
  private boolean preprocess(final boolean useThreads) throws IOException {

    if (this.result == null)
      return false;

    final long start = System.currentTimeMillis();

    final Particles3D particlesA = this.result.getParticlesA();
    final Particles3D particlesB = this.result.getParticlesB();

    if (particlesA == null || particlesB == null)
      return false;

    // Define the processor for particle A
    this.processorA = getProcessor(particlesA);
    this.processorA.setUpdateStatus(this.updateStatus.chain());
    logger.info("Particle A processor: " + particlesA.getType().name());

    // Start the preprocess of the particle A
    sendEvent(ProgressEventType.START_CALC_PARTICLES_A_CUBOIDS_EVENT);
    /*
     * this.processorA
     * .preprocess(ProgressEventType.PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT);
     */
    final Thread tpA =
        preprocessExecution(this.processorA, useThreads,
            ProgressEventType.PROGRESS_CALC_PARTICLES_A_CUBOIDS_EVENT,
            "preprocess Particle A");

    // Define the processor for particle B
    this.processorB = getProcessor(particlesB);
    this.processorB.setUpdateStatus(this.updateStatus.chain());
    logger.info("Particle B processor: " + particlesB.getType().name());

    // Start the preprocess of the particle A
    sendEvent(ProgressEventType.START_CALC_PARTICLES_B_CUBOIDS_EVENT);
    /*
     * this.processorB
     * .preprocess(ProgressEventType.PROGRESS_CALC_MITOS_CUBOIDS_EVENT);
     */
    final Thread tpB =
        preprocessExecution(this.processorB, useThreads,
            ProgressEventType.PROGRESS_CALC_PARTICLES_B_CUBOIDS_EVENT,
            "preprocess Particle B");

    if (tpA != null)
      try {
        tpA.join();
      } catch (InterruptedException e) {
        logger.severe("Error while waiting the end of the preprocessing.");
      }

    if (tpB != null)
      try {
        tpB.join();
      } catch (InterruptedException e) {
        logger.severe("Error while waiting the end of the preprocessing.");
      }

    this.result.setCuboidsParticlesA(new Particles3D(particlesA,
        this.processorA.getPreprocessedParticles()));

    this.result.setCuboidsParticlesB(new Particles3D(particlesB,
        this.processorB.getPreprocessedParticles()));

    logger.info("Preprocessing in "
        + (System.currentTimeMillis() - start) + " ms.");

    return true;
  }

  private int getThreadNumber() {

    int threadNumber = this.result.getSettings().getThreadNumber();

    if (threadNumber == 0)
      threadNumber = Runtime.getRuntime().availableProcessors();

    return threadNumber;
  }

  /**
   * Calc the distances.
   * @throws IOException
   */
  public void calc() throws IOException {

    final int threadNumber = getThreadNumber();

    if (!preprocess(threadNumber > 1))
      return;

    // Start the computation of all distances
    sendEvent(ProgressEventType.START_CALC_MIN_DISTANCES_EVENT);

    List<Particle3D> listA = this.processorA.getPreprocessedParticles();
    List<Particle3D> listB = this.processorB.getPreprocessedParticles();

    Map<Particle3D, Distance> mins = new HashMap<Particle3D, Distance>();
    Map<Particle3D, Distance> maxs = new HashMap<Particle3D, Distance>();

    if (threadNumber > 1)
      calcMultiThreads(listA, listB, mins, maxs, threadNumber);
    else
      calcOneThread(listA, listB, mins, maxs);

    this.result.setMinDistances(mins);
    this.result.setMaxDistances(maxs);

    // Analysis of the results

    sendEvent(ProgressEventType.START_DISTANCES_ANALYSIS);
    final DistanceAnalyser daMins = new DistanceAnalyser(mins);
    daMins.calcAll();

    final DistanceAnalyser daMaxs = new DistanceAnalyser(maxs);
    daMaxs.calcAll();

    this.result.setMinAnalyser(daMins);
    this.result.setMaxAnalyser(daMaxs);

    // Add to history results
    CorsenHistoryResults.getCorsenHistoryResults().addResult(this.result);
  }

  /**
   * It is the method that iterate over particles list to do all calculations.
   * @param listA list of particles A
   * @param listB list of particles B
   * @param mins map of min distances
   * @param maxs map of max distances
   */
  private void calcOneThread(final List<Particle3D> listA,
      final List<Particle3D> listB, Map<Particle3D, Distance> mins,
      Map<Particle3D, Distance> maxs) {

    int count = 0;
    final int calcsToDoNumber = listA.size() * listB.size();

    final int calcsBeforeUpdateInfo =
        calcsToDoNumber < 100 ? calcsToDoNumber : calcsToDoNumber / 100;

    logger.info("Thread number for distance computation: 1");
    final long startCalcs = System.currentTimeMillis();

    if (calcsToDoNumber != 0)
      for (Particle3D parA : listA) {

        final AbstractListPoint3D pointsA =
            this.processorA.getPresentationPointsA(parA.getInnerPoints());
        // final List<Distance> distances = new LinkedList<Distance>();
        final List<Distance> distances = new MinMaxList<Distance>();

        for (Particle3D parB : listB) {

          distances.addAll(this.processorB.calcDistance(parB, pointsA, parA));

          count++;

          if (count % calcsBeforeUpdateInfo == 0) {

            sendEvent(
                ProgressEventType.PROGRESS_CALC_DISTANCES_EVENT,
                (int) (((double) count / (double) calcsToDoNumber) * ProgressEvent.INDEX_IN_PHASE_MAX));
          }
        }

        mins.put(parA, Collections.min(distances));
        maxs.put(parA, Collections.max(distances));
      }

    logger.info("Calc "
        + count + " distances in " + (System.currentTimeMillis() - startCalcs)
        + " ms.");

  }

  /**
   * It is the method that iterate over particles list to do all calculations
   * (multithread version).
   * @param listA list of particles A
   * @param listB list of particles B
   * @param mins map of min distances
   * @param maxs map of max distances
   */
  private void calcMultiThreads(final List<Particle3D> listA,
      final List<Particle3D> listB, Map<Particle3D, Distance> mins,
      Map<Particle3D, Distance> maxs, final int threadNumber) {

    new CalcThread(0, 0, 2, threadNumber).calc(processorA, processorB, listA,
        listB, mins, maxs);
  }

  /**
   * Filter the distances to retain only extreme distances.
   * @param distances List of distance to filter
   */
  @SuppressWarnings("unused")
  private void filterExtremeDistances(final List<Distance> distances) {

    if (distances == null)
      return;

    final Distance min = Collections.min(distances);
    final Distance max = Collections.max(distances);

    distances.clear();
    distances.add(min);
    distances.add(max);
  }

  //
  // Other methods
  //

  private void sendEvent(final ProgressEventType type) {

    if (this.updateStatus == null)
      return;

    this.updateStatus.updateStatus(new ProgressEvent(type));
  }

  private void sendEvent(final ProgressEventType type, final int value1) {

    if (this.updateStatus == null)
      return;

    this.updateStatus.updateStatus(new ProgressEvent(type, value1));
  }

  //
  // Constructor
  //

  public DistancesCalculator(final CorsenResult result) {

    if (result == null)
      throw new NullPointerException("The result object is null");

    this.result = result;
    this.updateStatus = result.getUpdateStatus();
  }

}
