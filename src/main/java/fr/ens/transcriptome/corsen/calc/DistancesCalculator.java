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

package fr.ens.transcriptome.corsen.calc;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.ListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.util.MinMaxList;

public class DistancesCalculator {

  private Logger logger = Logger.getLogger(DistancesCalculator.class.getName());
  private DistanceProcessor processorA;
  private DistanceProcessor processorB;

  private float zFactor = 1.0f;
  private float factor = 1.0f;

  private UpdateStatus updateStatus;

  private CorsenResult result;

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
    result.setMessengers(particlesA);

    // Read mitos
    sendEvent(ProgressEventType.START_READ_MITOS_EVENT);
    Particles3D particlesB = new Particles3D(result.getMitosStream());

    // Transform coordinates of messengers
    sendEvent(ProgressEventType.START_CHANGE_MITOS_COORDINATES_EVENT);
    changeFactors(particlesB);
    result.setMitos(particlesB);
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

    final int calcsToDoNumber = listA.size() * listB.size();
    final int calcsBeforeUpdateInfo = calcsToDoNumber / 100;

    logger.info("calcsToDoNumber: " + calcsToDoNumber);
    logger.info("calcsBeforeUpdateInfo: " + calcsBeforeUpdateInfo);

    int count = 0;

    for (Particle3D parA : listA) {

      ListPoint3D pointsA = this.processorA.getPresentationPoints(parA
          .getInnerPoints());
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

        // if (distances.size() > 200)
        // filterExtremeDistances(distances);
      }

      mins.put(parA, Collections.min(distances));
      maxs.put(parA, Collections.max(distances));

    }

    this.result.setMinDistances(mins);
    this.result.setMaxDistances(maxs);
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
