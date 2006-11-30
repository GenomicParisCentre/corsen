package fr.ens.transcriptome.corsen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particle3DUtil;
import fr.ens.transcriptome.corsen.model.Particles3D;

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

public class CorsenResult {

  private UpdateStatus updateStatus;
  private File messengersFile;
  private File mitosFile;
  private Particles3D messengers;
  private Particles3D mitos;
  private Particles3D cuboidsMessengers;
  private Particles3D cuboidsMitos;
  private Map<Particle3D, Distance> minDistances;
  private Map<Particle3D, Distance> maxDistances;
  private DistanceCalculator distanceCalculator;

  //
  // Getters
  //

  /**
   * Get the messengers cuboids particles.
   * @return Returns the cuboidsMessengers
   */
  public Particles3D getCuboidsMessengersParticles() {
    return cuboidsMessengers;
  }

  /**
   * Get the mitos cuboids.
   * @return Returns the cuboidsMitos
   */
  public Particles3D getCuboidsMitosParticles() {
    return cuboidsMitos;
  }

  /**
   * Get the max distances.
   * @return Returns the maxDistances
   */
  public Map<Particle3D, Distance> getMaxDistances() {
    return maxDistances;
  }

  /**
   * Get the messengers particles.
   * @return Returns the messengers
   */
  public Particles3D getMessengersParticles() {
    return messengers;
  }

  /**
   * Get the min distances.
   * @return Returns the minDistances
   */
  public Map<Particle3D, Distance> getMinDistances() {
    return minDistances;
  }

  /**
   * Get the mito particles.
   * @return Returns the mitos
   */
  public Particles3D getMitosParticles() {
    return mitos;
  }

  /**
   * Get the updateStatus.
   * @return Returns the updateStatus
   */
  private UpdateStatus getUpdateStatus() {
    return updateStatus;
  }

  //
  // Setters
  //

  /**
   * Set the update status.
   * @param updateStatus The updateStatus to set
   */
  public void setUpdateStatus(final UpdateStatus updateStatus) {

    this.updateStatus = updateStatus;
  }

  /**
   * Set the mitos cuboids particles.
   * @param particles Particles to Set
   */
  public void setCuboidsMitosParticles(final Particles3D particles) {

    this.cuboidsMitos = particles;
  }

  //
  // Action methods
  //

  /**
   * Load messengers particles file
   * @throws IOException
   */
  public void loadMessengersParticlesFile() throws IOException {

    this.messengers = new Particles3D(this.messengersFile);
  }

  /**
   * Load mitos particles file
   * @throws IOException
   */
  public void loadMitosPartcilesFile() throws IOException {

    this.mitos = new Particles3D(this.mitosFile);
  }

  /**
   * Apply a transformation on z coodinates of messengers and mitos particles.
   * @param zFactor factor of the transformation to apply
   */
  public void changeZCoordinates(final float zFactor) {

    if (zFactor == 1.0f)
      return;

    messengers.changeZCoord(zFactor);
    mitos.changeZCoord(zFactor);
  }

  /**
   * Apply a transformation on all coodinates of messengers and mitos particles.
   * @param factor factor of the transformation to apply
   */
  public void changeAllCoordinates(final float factor) {

    if (factor == 1.0f)
      return;

    messengers.changeAllCoord(factor);
    mitos.changeAllCoord(factor);
  }

  /**
   * Calc messengers cuboids particles.
   */
  public void calcMessengerCuboids() {

    this.cuboidsMessengers = calcMessengersCuboidInternal(getMessengersParticles());

  }

  /**
   * Calc mitos cuboids particles.
   */
  public void calcMitosCuboids() {

    this.distanceCalculator = new DistanceCalculator(this);
    this.distanceCalculator.setUpdateStatus(getUpdateStatus());
  }

  /**
   * Calc minimal distances.
   */
  public void calcMinimalDistances() {

    final Particle3D[] mes = getCuboidsMessengersParticles().getParticles();
    final DistanceCalculator dc = this.distanceCalculator;
    this.minDistances = new HashMap<Particle3D, Distance>();

    for (int i = 0; i < mes.length; i++) {
      this.minDistances.put(mes[i], dc.minimalDistance(mes[i]));

      if (i % 10 == 0) {

        final double progress = (double) i / (double) mes.length
            * ProgressEvent.INDEX_IN_PHASE_MAX;

        sendEvent(ProgressEvent.PROGRESS_CALC_MIN_DISTANCES_EVENT,
            (int) progress);
      }

    }

  }

  /**
   * Calc maximal distances.
   */
  public void calcMaximalDistances() {

    final Particle3D[] mes = getCuboidsMessengersParticles().getParticles();
    final DistanceCalculator dc = this.distanceCalculator;
    this.maxDistances = new HashMap<Particle3D, Distance>();

    for (int i = 0; i < mes.length; i++) {
      this.maxDistances.put(mes[i], dc.maximalDistance(mes[i]));

      if (i % 10 == 0) {

        final double progress = (double) i / (double) mes.length
            * ProgressEvent.INDEX_IN_PHASE_MAX;

        sendEvent(ProgressEvent.PROGRESS_CALC_MAX_DISTANCES_EVENT,
            (int) progress);
      }

    }
  }

  //
  // Other methods
  //

  /**
   * Create cuboids particles from messengers particles.
   * @param messengers Messengers
   * @param lenght of the cuboid
   * @return An array of Particle3D
   */
  private Particles3D calcMessengersCuboidInternal(final Particles3D particles) {

    if (particles == null)
      return null;

    ArrayList al = null;
    float volume = 0;

    final int nParticles = particles.getParticlesNumber();
    final int countMax = Particle3DUtil.countInnerPointsInParticles(particles
        .getParticles());
    int count = 0;

    for (int m = 0; m < nParticles; m++) {

      final Particle3D messenger = particles.getParticle(m);

      float len = particles.getPixelDepth();
      if (particles.getPixelWidth() > len)
        len = particles.getPixelWidth();
      if (particles.getPixelHeight() > len)
        len = particles.getPixelHeight();

      len = len * Globals.CUBOID_SIZE_FACTOR;
      volume = len*len*len;

      ArrayList cuboids = Particle3DUtil.createCuboidToArrayList(messenger,
          len, len, len);

      if (cuboids != null) {
        if (al == null)
          al = new ArrayList();
        al.addAll(cuboids);
      }

      count += messenger.innerPointsCount();
      final double p = (double) count / (double) countMax
          * ProgressEvent.INDEX_IN_PHASE_MAX;
      sendEvent(ProgressEvent.PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT, (int) p);
    }

    if (al == null)
      return null;
    final Particle3D[] result = new Particle3D[al.size()];
    al.toArray(result);

    for (int i = 0; i < result.length; i++) {
      result[i].setIntensityFromInnerPoints();
      result[i].setVolume(volume);
    }

    Particles3D pars = new Particles3D(particles);

    pars.setParticles(result);

    return pars;
  }

  /**
   * Send an event to update the status
   * @param id Event id
   * @param value1 value of the first parameter of the event
   */
  private void sendEvent(final int id, final int value1) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.updateStatus(new ProgressEvent(id, value1));
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param mitosFile File of the mitos particles
   * @param messengersFile File of the messengers particles
   * @param updateStatus The updateStatus
   */
  public CorsenResult(final File mitosFile, final File messengersFile,
      final UpdateStatus updateStatus) {

    if (mitosFile == null)
      throw new RuntimeException("Unable to find mito file");
    if (messengersFile == null)
      throw new RuntimeException("Unable to find messenger file");

    this.mitosFile = mitosFile;
    this.messengersFile = messengersFile;
    setUpdateStatus(updateStatus);

  }

}
