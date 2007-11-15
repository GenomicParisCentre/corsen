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

package fr.ens.transcriptome.corsen.calc;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.util.MinMaxList;

public class DistancesCalculator {

  private Logger logger = Logger.getLogger(DistancesCalculator.class.getName());
  private DistanceProcessor processorA;
  private DistanceProcessor processorB;

  private float zFactor = 1.0f;
  private float factor = 1.0f;

  private AbstractListPoint3D listPointsAForThreads;
  private int freeThreads;

  private UpdateStatus updateStatus;
  private UncaughtExceptionHandler uceh;
  private CorsenResult result;

  private final class CalcThread2 extends Thread {

    private int count = 0;

    private List<Particle3D> listB;

    private DistanceProcessor processorB;

    private List<Distance> distancesAllThreads;

    private int threadNumber;
    private int threadsCount;

    volatile boolean keepRunning = true;
    boolean waitBeforeNextParticle;

    public void pleaseStop() {

      this.keepRunning = false;
    }

    public int getCount() {

      return this.count;
    }

    @Override
    public void run() {

      final List<Particle3D> listB = this.listB;

      final DistanceProcessor processorB = this.processorB;
      final int threadNumber = this.threadNumber;
      final int threadsCount = this.threadsCount;

      AbstractListPoint3D listPointsA = null;

      while (this.keepRunning) {

        while (listPointsAForThreads == null)
          Thread.yield();

        /*
         * if (!this.keepRunning) continue;
         */

        listPointsA = listPointsAForThreads;
        // final int globalCount = allThreadsCount;

        // final List<Distance> distances = new LinkedList<Distance>();
        final List<Distance> distances = new MinMaxList<Distance>();

        int i = 0;

        for (Particle3D parB : listB) {

          if (i++ % threadsCount != threadNumber)
            continue;

          distances.addAll(processorB.calcDistance(parB, listPointsA));
          this.count++;

        }

        this.distancesAllThreads.addAll(distances);

        freeThreads++;

        while (listPointsA == listPointsAForThreads && this.keepRunning)
          Thread.yield();
      }

      logger.info("fin thread " + this.getName() + " (" + count + " calcs).");
    }

    public CalcThread2(final DistanceProcessor processorB,
        final List<Particle3D> listB, final List<Distance> distancesAllThreads,
        final int threadNumber, final int threadsCount) {

      this.processorB = processorB;

      this.listB = listB;
      this.distancesAllThreads = distancesAllThreads;
      this.threadNumber = threadNumber;
      this.threadsCount = threadsCount;

    }

  }

  private final class CalcThread3 extends Thread {

    private int count = 0;

    private List<Particle3D> listA;
    private List<Particle3D> listB;

    private DistanceProcessor processorA;
    private DistanceProcessor processorB;

    private int threadNumber;
    private int threadsCount;

    private Map<Particle3D, Distance> mins =
        new HashMap<Particle3D, Distance>();
    private Map<Particle3D, Distance> maxs =
        new HashMap<Particle3D, Distance>();

    public int getCount() {

      return this.count;
    }

    public Map<Particle3D, Distance> getMinsDistances() {

      return this.mins;
    }

    public Map<Particle3D, Distance> getMaxsDistances() {

      return this.maxs;
    }

    @Override
    public void run() {

      int i = 0;

      for (Particle3D parA : listA) {

        if (i++ % threadsCount != threadNumber)
          continue;

        final AbstractListPoint3D pointsA =
            this.processorA.getPresentationPoints(parA.getInnerPoints());
        // final List<Distance> distances = new LinkedList<Distance>();
        final List<Distance> distances = new MinMaxList<Distance>();

        for (Particle3D parB : listB) {

          distances.addAll(this.processorB.calcDistance(parB, pointsA));
          this.count++;
        }

        mins.put(parA, Collections.min(distances));
        maxs.put(parA, Collections.max(distances));
      }

      freeThreads++;
    }

    public CalcThread3(final List<Particle3D> listA,
        final List<Particle3D> listB, final DistanceProcessor processorA,
        final DistanceProcessor processorB, final int threadNumber,
        final int threadsCount) {

      this.listA = listA;
      this.listB = listB;

      this.processorA = processorA;
      this.processorB = processorB;

      this.threadNumber = threadNumber;
      this.threadsCount = threadsCount;
    }

  }

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

    DistanceProcessor result;

    switch (particles.getType()) {

    case TINY:
      result = new TinyParticles3D();
      break;

    case HUGE:
      result = new HugeParticles3D();
      break;

    default:
      result = new UndefinedParticles3D();
      break;

    }

    result.setSourceParticles(particles);

    return result;
  }

  /**
   * Change if needed the coordinates of the particles.
   */
  private void changeFactors(final Particles3D particles) {

    if (particles == null)
      return;

    particles.changeZCoord(zFactor);
    particles.changeAllCoord(factor);
  }

  /**
   * Load and transform the coordinates of a particles file.
   * @throws IOException if an error occurs while reading the file
   */
  public void loadParticles() throws IOException {

    final CorsenResult result = this.result;

    // Read messengers
    sendEvent(ProgressEventType.START_READ_MESSENGERS_EVENT);
    Particles3D particlesA = new Particles3D(result.getMessengersStream());

    // Transform coordinates of messengers
    sendEvent(ProgressEventType.START_CHANGE_MESSENGERS_COORDINATES_EVENT);
    changeFactors(particlesA);
    result.setParticlesA(particlesA);

    // Read mitos
    sendEvent(ProgressEventType.START_READ_MITOS_EVENT);
    Particles3D particlesB = new Particles3D(result.getMitosStream());

    // Transform coordinates of messengers
    sendEvent(ProgressEventType.START_CHANGE_MITOS_COORDINATES_EVENT);
    changeFactors(particlesB);
    result.setParticlesB(particlesB);
  }

  private Thread preprocessExecution(final DistanceProcessor processor,
      final boolean useThreads, final ProgressEventType event,
      final String threadName) {

    final long start = System.currentTimeMillis();

    if (!useThreads) {
      processor.preprocess(event);
      logger.info("End "
          + threadName + " (process in " + (System.currentTimeMillis() - start)
          + " ms.");
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

    final Particles3D particlesA = this.result.getMessengersParticles();
    final Particles3D particlesB = this.result.getMitosParticles();

    if (particlesA == null || particlesB == null)
      return false;

    // Define the processor for particle A
    this.processorA = getProcessor(particlesA);
    this.processorA.setUpdateStatus(this.updateStatus.chain());

    // Start the preprocess of the particle A
    sendEvent(ProgressEventType.START_CALC_MESSENGERS_CUBOIDS_EVENT);
    /*
     * this.processorA
     * .preprocess(ProgressEventType.PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT);
     */
    final Thread tpA =
        preprocessExecution(this.processorA, useThreads,
            ProgressEventType.PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT,
            "preprocess Particle A");

    // Define the processor for particle B
    this.processorB = getProcessor(particlesB);
    this.processorB.setUpdateStatus(this.updateStatus.chain());

    // Start the preprocess of the particle A
    sendEvent(ProgressEventType.START_CALC_MITOS_CUBOIDS_EVENT);
    /*
     * this.processorB
     * .preprocess(ProgressEventType.PROGRESS_CALC_MITOS_CUBOIDS_EVENT);
     */
    final Thread tpB =
        preprocessExecution(this.processorB, useThreads,
            ProgressEventType.START_CALC_MITOS_CUBOIDS_EVENT,
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

    this.result.setCuboidsMessengersParticles(new Particles3D(particlesA,
        this.processorA.getDestParticles()));

    this.result.setCuboidsMitosParticles(new Particles3D(particlesB,
        this.processorB.getDestParticles()));

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
   * Calc the distances
   * @throws IOException
   */
  public void calc() throws IOException {

    final int threadNumber = getThreadNumber();

    if (!preprocess(threadNumber > 1))
      return;

    // Start the computation of all distances
    sendEvent(ProgressEventType.START_CALC_MIN_DISTANCES_EVENT);

    List<Particle3D> listA = this.processorA.getDestParticles();
    List<Particle3D> listB = this.processorB.getDestParticles();

    Map<Particle3D, Distance> mins = new HashMap<Particle3D, Distance>();
    Map<Particle3D, Distance> maxs = new HashMap<Particle3D, Distance>();

    if (threadNumber > 1)
      calcMultiThreads2(listA, listB, mins, maxs, threadNumber);
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
  }

  private void calcOneThread(final List<Particle3D> listA,
      final List<Particle3D> listB, Map<Particle3D, Distance> mins,
      Map<Particle3D, Distance> maxs) {

    int count = 0;
    final int calcsToDoNumber = listA.size() * listB.size();
    final int calcsBeforeUpdateInfo = calcsToDoNumber / 100;

    logger.info("Thread number for distance computation: 1");
    final long startCalcs = System.currentTimeMillis();

    for (Particle3D parA : listA) {

      final AbstractListPoint3D pointsA =
          this.processorA.getPresentationPoints(parA.getInnerPoints());
      // final List<Distance> distances = new LinkedList<Distance>();
      final List<Distance> distances = new MinMaxList<Distance>();

      for (Particle3D parB : listB) {

        distances.addAll(this.processorB.calcDistance(parB, pointsA));

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

  private void calcMultiThreads2(final List<Particle3D> listA,
      final List<Particle3D> listB, Map<Particle3D, Distance> mins,
      Map<Particle3D, Distance> maxs, final int threadNumber) {

    int count = 0;

    final int calcsToDoNumber = listA.size() * listB.size();
    final int calcsBeforeUpdateInfo = calcsToDoNumber / 100;

    logger.info("Thread number for distance computation: " + threadNumber);

    // final List<Distance> distancesAllThreads = Collections
    // .synchronizedList(new LinkedList<Distance>());

    final List<Distance> distancesAllThreads =
        Collections.synchronizedList(new MinMaxList<Distance>());

    final CalcThread2[] threads = new CalcThread2[threadNumber];

    this.freeThreads = 0;
    final long startCalcs = System.currentTimeMillis();

    for (int i = 0; i < threadNumber; i++) {
      threads[i] =
          new CalcThread2(this.processorB, listB, distancesAllThreads, i,
              threadNumber);
      threads[i].setName("Distance computation thread #" + i);

      UpdateStatus ups = this.updateStatus.chain();
      final UncaughtExceptionHandler uceh = getUncaughtExceptionHandler(ups);
      ups.moveToThread(threads[i]);
      threads[i].setUncaughtExceptionHandler(uceh);
      threads[i].start();
    }

    for (final Particle3D parA : listA) {

      this.freeThreads = 0;

      this.listPointsAForThreads =
          this.processorA.getPresentationPoints(parA.getInnerPoints());

      if (this.listPointsAForThreads == null)
        System.err.println("listPointsAForThreads is null !!!");

      while (this.freeThreads != threadNumber)
        Thread.yield();

      final int countBefore = count;
      count = 0;

      for (int i = 0; i < threadNumber; i++)
        count += threads[i].getCount();

      mins.put(parA, Collections.min(distancesAllThreads));
      maxs.put(parA, Collections.max(distancesAllThreads));
      distancesAllThreads.clear();

      if ((count - countBefore) > calcsBeforeUpdateInfo) {
        sendEvent(
            ProgressEventType.PROGRESS_CALC_DISTANCES_EVENT,
            (int) (((double) count / (double) calcsToDoNumber) * ProgressEvent.INDEX_IN_PHASE_MAX));
      }

    }

    logger.info("Calc "
        + count + " distances in " + (System.currentTimeMillis() - startCalcs)
        + " ms.");

    for (int i = 0; i < threadNumber; i++)
      threads[i].pleaseStop();

  }

  private void calcMultiThreads3(final List<Particle3D> listA,
      final List<Particle3D> listB, Map<Particle3D, Distance> mins,
      Map<Particle3D, Distance> maxs, final int threadNumber) {

    int count = 0;
    final int calcsToDoNumber = listA.size() * listB.size();
    final int calcsBeforeUpdateInfo = calcsToDoNumber / 100;

    logger.info("Thread number for distance computation: " + threadNumber);
    final long startCalcs = System.currentTimeMillis();

    final CalcThread3[] threads = new CalcThread3[threadNumber];

    for (int i = 0; i < threadNumber; i++) {
      threads[i] =
          new CalcThread3(listA, listB, this.processorA, this.processorB, i,
              threadNumber);
      threads[i].setName("Distance computation thread #" + i);

      UpdateStatus ups = this.updateStatus.chain();
      final UncaughtExceptionHandler uceh = getUncaughtExceptionHandler(ups);
      ups.moveToThread(threads[i]);

      threads[i].setUncaughtExceptionHandler(uceh);
      threads[i].start();
    }

    while (this.freeThreads != threadNumber) {

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {

        logger.severe("InterruptedException during sleeping");
      }

      count = 0;
      for (int i = 0; i < threadNumber; i++)
        count += threads[i].getCount();

      sendEvent(
          ProgressEventType.PROGRESS_CALC_DISTANCES_EVENT,
          (int) (((double) count / (double) calcsToDoNumber) * ProgressEvent.INDEX_IN_PHASE_MAX));

    }
    count = 0;
    for (int i = 0; i < threadNumber; i++) {
      CalcThread3 t = threads[i];
      mins.putAll(t.getMinsDistances());
      maxs.putAll(t.getMaxsDistances());
      count += threads[i].getCount();
    }

    logger.info("Calc "
        + count + " distances in " + (System.currentTimeMillis() - startCalcs)
        + " ms.");
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
