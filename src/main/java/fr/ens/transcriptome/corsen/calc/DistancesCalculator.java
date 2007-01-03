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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.util.MinMaxList;

public class DistancesCalculator {

  private static final int THREAD_QUEUE_LENGTH = 100;
  private Logger logger = Logger.getLogger(DistancesCalculator.class.getName());
  private DistanceProcessor processorA;
  private DistanceProcessor processorB;

  private float zFactor = 1.0f;
  private float factor = 1.0f;

  private AbstractListPoint3D listPointsAForThreads;
  private int freeThreads;
  private int allThreadsCount;

  private UpdateStatus updateStatus;

  private CorsenResult result;

  private final class CalcArgs {

    public Particle3D parB;
    public AbstractListPoint3D pointsA;

    public final void setArgs(final Particle3D parB,
        final AbstractListPoint3D pointsA) {

      this.parB = parB;
      this.pointsA = pointsA;
    }

    CalcArgs(Particle3D parB, AbstractListPoint3D pointsA) {

      setArgs(parB, pointsA);
    }
  }

  private final class CalcThread extends Thread {

    private Queue<CalcArgs> queueTodo;
    private Queue<CalcArgs> queueDone;

    private List<Distance> distances;

    private int count = 0;

    volatile boolean keepRunning = true;

    public void pleaseStop() {

      this.keepRunning = false;
    }

    @Override
    public void run() {

      final long id = Thread.currentThread().getId();

      while (this.keepRunning) {

        final CalcArgs args;

        synchronized (queueTodo) {
          if (queueTodo.size() > 0)
            args = queueTodo.poll();
          else
            args = null;
        }

        if (args != null) {

          final List<Distance> result = processorB.calcDistance(args.parB,
              args.pointsA);
          count++;

          synchronized (this.distances) {
            distances.addAll(result);
          }

          synchronized (this.queueDone) {
            this.queueDone.add(args);
          }

        }

        Thread.yield();
      }

      logger.info("fin thread " + id + " " + count + " process");
    }

    CalcThread(List<Distance> distances, Queue<CalcArgs> queueTodo,
        Queue<CalcArgs> queueDone) {

      super();
      this.distances = distances;
      this.queueTodo = queueTodo;
      this.queueDone = queueDone;
    }

  }

  private final class CalcThread2 extends Thread {

    private int count = 0;

    private List<Particle3D> listA;
    private List<Particle3D> listB;

    private DistanceProcessor processorB;

    private List<Distance> distancesAllThreads;

    private int threadNumber;
    private int threadsCount;
    private int calcsBeforeUpdateInfo;
    private int calcsToDoNumber;

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

      final long id = Thread.currentThread().getId();

      AbstractListPoint3D listPointsA = null;

      while (this.keepRunning) {

        if (listPointsA == listPointsAForThreads)
          Thread.yield();

        listPointsA = listPointsAForThreads;
        // final int globalCount = allThreadsCount;
        this.count = 0;

        // final List<Distance> distances = new LinkedList<Distance>();
        final List<Distance> distances = new MinMaxList<Distance>();

        int i = 0;

        for (Particle3D parB : listB) {

          if (i % threadsCount != threadNumber)
            continue;

          distances.addAll(processorB.calcDistance(parB, listPointsA));
          this.count++;

        }

        synchronized (this.distancesAllThreads) {
          this.distancesAllThreads.addAll(distances);
          freeThreads++;
        }

      }

      logger.info("fin thread " + id + " " + count + " process");
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

  /**
   * Preprocess the computation.
   * @return true if the preprocess is ok
   * @throws IOException if an error occurs while reading input streams
   */
  private boolean preprocess() throws IOException {

    if (this.result == null)
      return false;

    final Particles3D particlesA = this.result.getMessengersParticles();
    final Particles3D particlesB = this.result.getMitosParticles();

    if (particlesA == null || particlesB == null)
      return false;

    // Define the processor for particle A
    this.processorA = getProcessor(particlesA);
    this.processorA.setUpdateStatus(this.updateStatus);
    this.processorA
        .preprocess(ProgressEventType.PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT);

    // Start the preprocess of the particle A
    sendEvent(ProgressEventType.START_CALC_MESSENGERS_CUBOIDS_EVENT);
    this.result.setCuboidsMessengersParticles(new Particles3D(particlesA,
        this.processorA.getDestParticles()));

    // Define the processor for particle B
    this.processorB = getProcessor(particlesB);
    this.processorB.setUpdateStatus(this.updateStatus);

    // Start the preprocess of the particle A
    sendEvent(ProgressEventType.START_CALC_MITOS_CUBOIDS_EVENT);
    this.processorB
        .preprocess(ProgressEventType.PROGRESS_CALC_MITOS_CUBOIDS_EVENT);

    // Start the computation of all distances
    sendEvent(ProgressEventType.START_CALC_MIN_DISTANCES_EVENT);
    this.result.setCuboidsMitosParticles(new Particles3D(particlesB,
        this.processorB.getDestParticles()));

    return true;
  }

  /**
   * Calc the distances
   * @throws IOException
   */
  public void calc() throws IOException {

    if (!preprocess())
      return;

    logger.info("Nb processor: " + Runtime.getRuntime().availableProcessors());

    List<Particle3D> listA = this.processorA.getDestParticles();
    List<Particle3D> listB = this.processorB.getDestParticles();

    Map<Particle3D, Distance> mins = new HashMap<Particle3D, Distance>();
    Map<Particle3D, Distance> maxs = new HashMap<Particle3D, Distance>();

    int threadNumber = this.result.getSettings().getThreadNumber();

    if (threadNumber == 0)
      threadNumber = Runtime.getRuntime().availableProcessors();

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

    logger.info("Calc Thread number: 1");

    for (Particle3D parA : listA) {

      final AbstractListPoint3D pointsA = this.processorA
          .getPresentationPoints(parA.getInnerPoints());
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
  }

  private void calcMultiThreads(final List<Particle3D> listA,
      final List<Particle3D> listB, Map<Particle3D, Distance> mins,
      Map<Particle3D, Distance> maxs, final int threadNumber) {

    int count = 0;
    final int calcsToDoNumber = listA.size() * listB.size();
    final int calcsBeforeUpdateInfo = calcsToDoNumber / 100;

    logger.info("Calc Thread number: " + threadNumber);

    final Queue<CalcArgs> queueTodo = new ArrayBlockingQueue<CalcArgs>(
        THREAD_QUEUE_LENGTH);
    final Queue<CalcArgs> queueDone = new LinkedList<CalcArgs>();
    final List<Distance> distances = new MinMaxList<Distance>();

    final CalcThread[] threads = new CalcThread[threadNumber];

    for (int i = 0; i < threadNumber; i++) {
      threads[i] = new CalcThread(distances, queueTodo, queueDone);
      threads[i].start();
    }

    for (final Particle3D parA : listA) {

      final AbstractListPoint3D pointsA = this.processorA
          .getPresentationPoints(parA.getInnerPoints());

      for (final Particle3D parB : listB) {

        boolean offerOk = false;

        while (!offerOk) {

          CalcArgs args = null;

          synchronized (queueDone) {
            args = queueDone.poll();
          }

          if (args == null)
            args = new CalcArgs(parB, pointsA);
          else
            args.setArgs(parB, pointsA);

          synchronized (queueTodo) {
            offerOk = queueTodo.offer(args);
          }

          Thread.yield();
        }

        count++;

        if ((count - queueTodo.size()) % calcsBeforeUpdateInfo == 0) {

          sendEvent(
              ProgressEventType.PROGRESS_CALC_DISTANCES_EVENT,
              (int) (((double) count / (double) calcsToDoNumber) * ProgressEvent.INDEX_IN_PHASE_MAX));
        }
      }

      while (queueTodo.size() != 0)
        Thread.yield();

      mins.put(parA, Collections.min(distances));
      maxs.put(parA, Collections.max(distances));
      distances.clear();
    }

    for (int i = 0; i < threadNumber; i++)
      threads[i].pleaseStop();

  }

  private void calcMultiThreads2(final List<Particle3D> listA,
      final List<Particle3D> listB, Map<Particle3D, Distance> mins,
      Map<Particle3D, Distance> maxs, final int threadNumber) {

    int count = 0;

    final int calcsToDoNumber = listA.size() * listB.size();
    final int calcsBeforeUpdateInfo = calcsToDoNumber / 100;

    logger.info("Calc Thread number: " + threadNumber);

    final List<Distance> distancesAllThreads = new MinMaxList<Distance>();

    final CalcThread2[] threads = new CalcThread2[threadNumber];

    for (int i = 0; i < threadNumber; i++) {
      threads[i] = new CalcThread2(this.processorB, listB, distancesAllThreads,
          i, threadNumber);
      threads[i].start();
    }

    for (final Particle3D parA : listA) {

      this.freeThreads = 0;

      this.listPointsAForThreads = this.processorA.getPresentationPoints(parA
          .getInnerPoints());

      while (this.freeThreads != threadNumber)
        Thread.yield();

      final int countBefore = count;

      for (int i = 0; i < threadNumber; i++)
        count += threads[i].getCount();

      synchronized (distancesAllThreads) {

        mins.put(parA, Collections.min(distancesAllThreads));
        maxs.put(parA, Collections.max(distancesAllThreads));
        distancesAllThreads.clear();
      }

      if ((count - countBefore) > calcsBeforeUpdateInfo) {
        sendEvent(
            ProgressEventType.PROGRESS_CALC_DISTANCES_EVENT,
            (int) (((double) count / (double) calcsToDoNumber) * ProgressEvent.INDEX_IN_PHASE_MAX));
      }

    }

    for (int i = 0; i < threadNumber; i++)
      threads[i].pleaseStop();

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
