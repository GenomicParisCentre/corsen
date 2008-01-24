package fr.ens.transcriptome.corsen.calc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.model.Particle3D;
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

public class CorsenResult {

  private UpdateStatus updateStatus;
  private Settings settings;
  private File messengersFile;
  private File mitosFile;
  private InputStream messengersStream;
  private InputStream mitosStream;
  private Particles3D particlesA;
  private Particles3D particlesB;
  private Particles3D particlesACuboids;
  private Particles3D particlesBCuboids;
  private Map<Particle3D, Distance> minDistances;
  private Map<Particle3D, Distance> maxDistances;
  private DistanceAnalyser minAnalyser;
  private DistanceAnalyser maxAnalyser;

  //
  // Getters
  //

  /**
   * Get the particlesA cuboids particles.
   * @return Returns the particlesACuboids
   */
  public Particles3D getCuboidsMessengersParticles() {
    return particlesACuboids;
  }

  /**
   * Get the particlesB cuboids.
   * @return Returns the particlesBCuboids
   */
  public Particles3D getCuboidsMitosParticles() {
    return particlesBCuboids;
  }

  /**
   * Get the max distances.
   * @return Returns the maxDistances
   */
  public Map<Particle3D, Distance> getMaxDistances() {
    return maxDistances;
  }

  /**
   * Get the particlesA particles.
   * @return Returns the particlesA
   */
  public Particles3D getMessengersParticles() {
    return particlesA;
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
   * @return Returns the particlesB
   */
  public Particles3D getMitosParticles() {
    return particlesB;
  }

  /**
   * Get the updateStatus.
   * @return Returns the updateStatus
   */
  UpdateStatus getUpdateStatus() {
    return updateStatus;
  }

  /**
   * Get the particlesA file.
   * @return Returns the messengersFile
   */
  public File getMessengersFile() {
    return messengersFile;
  }

  /**
   * Get the particlesB file.
   * @return Returns the mitosFile
   */
  public File getMitosFile() {
    return mitosFile;
  }

  /**
   * Get the particlesA stream.
   * @return Returns the messengersFile
   */
  InputStream getMessengersStream() {
    return messengersStream;
  }

  /**
   * Get the particlesB file.
   * @return Returns the mitosFile
   */
  InputStream getMitosStream() {
    return mitosStream;
  }

  /**
   * Get the settings.
   * @return the settings
   */
  Settings getSettings() {
    return this.settings;
  }

  /**
   * Get the analyser for max distances.
   * @return Returns the maxAnalyser
   */
  public DistanceAnalyser getMaxAnalyser() {
    return maxAnalyser;
  }

  /**
   * Get the analyser for min distances.
   * @return Returns the minAnalyser
   */
  public DistanceAnalyser getMinAnalyser() {
    return minAnalyser;
  }

  //
  // Setters
  //

  /**
   * @param maxAnalyser The maxAnalyser to set
   */
  public void setMaxAnalyser(DistanceAnalyser maxAmalyser) {
    this.maxAnalyser = maxAmalyser;
  }

  /**
   * @param minAnalyser The minAnalyser to set
   */
  public void setMinAnalyser(DistanceAnalyser minAmalyser) {
    this.minAnalyser = minAmalyser;
  }

  /**
   * Set the particlesA particles.
   * @param particlesA The particlesA to set
   */
  void setParticlesA(Particles3D particlesA) {
    this.particlesA = particlesA;
  }

  /**
   * Set the particlesB particles.
   * @param particlesB The particlesB to set
   */
  void setParticlesB(Particles3D particlesB) {
    this.particlesB = particlesB;
  }

  /**
   * Set the update status.
   * @param updateStatus The updateStatus to set
   */
  public void setUpdateStatus(final UpdateStatus updateStatus) {

    this.updateStatus = updateStatus;
  }

  /**
   * Set the particlesB cuboids particles.
   * @param particles Particles to Set
   */
  void setCuboidsMitosParticles(final Particles3D particles) {

    this.particlesBCuboids = particles;
  }

  /**
   * Set the particlesA cuboids particles.
   * @param particles Particles to Set
   */
  void setCuboidsMessengersParticles(final Particles3D particles) {

    this.particlesACuboids = particles;
  }

  /**
   * Set the maximal distances.
   * @param maxDistances The maxDistances to set
   */
  void setMaxDistances(Map<Particle3D, Distance> maxDistances) {
    this.maxDistances = maxDistances;
  }

  /**
   * Set the minimal distances.
   * @param minDistances The minDistances to set
   */
  void setMinDistances(Map<Particle3D, Distance> minDistances) {
    this.minDistances = minDistances;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param particlesBFile File of the particlesB particles
   * @param particlesAFile File of the particlesA particles
   * @param updateStatus The updateStatus
   * @throws FileNotFoundException
   */
  public CorsenResult(final File particlesAFile, final File particlesBFile,
      final Settings settings, final UpdateStatus updateStatus)
      throws FileNotFoundException {

    this(particlesAFile, particlesBFile, new FileInputStream(particlesAFile),
        new FileInputStream(particlesBFile), settings, updateStatus);

  }

  /**
   * Public constructor.
   * @param mitosFile File of the particlesB particles
   * @param messengersFile File of the particlesA particles
   * @param updateStatus The updateStatus
   */
  public CorsenResult(final File particlesAFile, final File particlesBFile,
      final InputStream messengersStream, final InputStream mitosStream,
      final Settings settings, final UpdateStatus updateStatus) {

    if (mitosStream == null)
      throw new RuntimeException("Unable to find mito file");
    if (messengersStream == null)
      throw new RuntimeException("Unable to find messenger file");

    this.mitosFile = particlesBFile;
    this.messengersFile = particlesAFile;
    this.mitosStream = mitosStream;
    this.messengersStream = messengersStream;
    this.settings = settings;

    setUpdateStatus(updateStatus);
  }

}
