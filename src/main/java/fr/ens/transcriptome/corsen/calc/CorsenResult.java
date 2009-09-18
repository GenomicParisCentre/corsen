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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.Particles3DFilter;

/**
 * This class define the result class.
 * @author Laurent Jourdren
 */
public class CorsenResult {

  private UpdateStatus updateStatus;
  private Settings settings;
  private File particlesAFile;
  private File particlesBFile;
  private File resultPath;
  private InputStream particlesAStream;
  private InputStream particlesBStream;
  private Particles3D particlesA;
  private Particles3D particlesB;
  private Particles3D particlesACuboids;
  private Particles3D particlesBCuboids;
  private Map<Particle3D, Distance> minDistances;
  private Map<Particle3D, Distance> maxDistances;
  private DistanceAnalyser minAnalyser;
  private DistanceAnalyser maxAnalyser;
  private Particles3DFilter particlesAFilter;
  private Particles3DFilter particlesBFilter;

  //
  // Getters
  //

  /**
   * Get the particlesA cuboids particles.
   * @return Returns the particlesACuboids
   */
  public Particles3D getCuboidsParticlesA() {
    return particlesACuboids;
  }

  /**
   * Get the particlesB cuboids.
   * @return Returns the particlesBCuboids
   */
  public Particles3D getCuboidsParticlesB() {
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
  public Particles3D getParticlesA() {
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
   * Get the particles B.
   * @return Returns the particlesB
   */
  public Particles3D getParticlesB() {
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
    return particlesAFile;
  }

  /**
   * Get the particlesB file.
   * @return Returns the mitosFile
   */
  public File getMitosFile() {
    return particlesBFile;
  }

  /**
   * Get the result path
   * @return a file object with the path of results
   */
  public File getResultsPath() {
    return resultPath;
  }

  /**
   * Get the particlesA stream.
   * @return Returns the messengersFile
   */
  InputStream getMessengersStream() {
    return particlesAStream;
  }

  /**
   * Get the particlesB file.
   * @return Returns the mitosFile
   */
  InputStream getMitosStream() {
    return particlesBStream;
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

  /**
   * Get the Particles3DFilter for particlesA
   * @return a Particles3DFilter
   */
  public Particles3DFilter getParticlesAFilter() {
    return particlesAFilter;
  }

  /**
   * Get the Particles3DFilter for particlesB
   * @return a Particles3DFilter
   */
  public Particles3DFilter getParticlesBFilter() {
    return particlesBFilter;
  }

  //
  // Setters
  //

  /**
   * Set the DistanceAnalyser for maximal.
   * @param maxAnalyser The maxAnalyser to set
   */
  public void setMaxAnalyser(final DistanceAnalyser maxAnalyser) {
    this.maxAnalyser = maxAnalyser;
  }

  /**
   * Set the DistanceAnalyser for minimal.
   * @param minAnalyser The minAnalyser to set
   */
  public void setMinAnalyser(final DistanceAnalyser minAnalyser) {
    this.minAnalyser = minAnalyser;
  }

  /**
   * Set the particlesA particles.
   * @param particlesA The particlesA to set
   */
  void setParticlesA(final Particles3D particlesA) {
    this.particlesA = particlesA;
  }

  /**
   * Set the particlesB particles.
   * @param particlesB The particlesB to set
   */
  void setParticlesB(final Particles3D particlesB) {
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
  void setCuboidsParticlesB(final Particles3D particles) {

    this.particlesBCuboids = particles;
  }

  /**
   * Set the particlesA cuboids particles.
   * @param particles Particles to Set
   */
  void setCuboidsParticlesA(final Particles3D particles) {

    this.particlesACuboids = particles;
  }

  /**
   * Set the maximal distances.
   * @param maxDistances The maxDistances to set
   */
  void setMaxDistances(final Map<Particle3D, Distance> maxDistances) {
    this.maxDistances = maxDistances;
  }

  /**
   * Set the minimal distances.
   * @param minDistances The minDistances to set
   */
  void setMinDistances(final Map<Particle3D, Distance> minDistances) {
    this.minDistances = minDistances;
  }

  /**
   * Set the Particles3DFilter for particles A
   * @param particlesAFilter the filter to set
   */
  public void setParticlesAFilter(final Particles3DFilter particlesAFilter) {
    this.particlesAFilter = particlesAFilter;
  }

  /**
   * Set the Particles3DFilter for particles B
   * @param particlesBFilter the filter to set
   */
  public void setParticlesBFilter(final Particles3DFilter particlesBFilter) {
    this.particlesBFilter = particlesBFilter;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param particlesBFile File of the particlesB particles
   * @param particlesAFile File of the particlesA particles
   * @param resultsPath path for results files
   * @param settings Setting of the computation
   * @param updateStatus The updateStatus
   * @throws FileNotFoundException if an error occurs while reading or writing
   *             files
   */
  public CorsenResult(final File particlesAFile, final File particlesBFile,
      final File resultsPath, final Settings settings,
      final UpdateStatus updateStatus) throws FileNotFoundException {

    this(particlesAFile, particlesBFile, new FileInputStream(particlesAFile),
        new FileInputStream(particlesBFile), resultsPath, settings,
        updateStatus);

  }

  /**
   * Public constructor.
   * @param particlesAFile File of the particlesA
   * @param particlesBFile File of the particlesB
   * @param particlesAStream Stream of the particlesA
   * @param particlesBStream Stream of the particlesB
   * @param resultsPath path for results files
   * @param settings Setting of the computation
   * @param updateStatus The updateStatus
   */
  public CorsenResult(final File particlesAFile, final File particlesBFile,
      final InputStream particlesAStream, final InputStream particlesBStream,
      final File resultsPath, final Settings settings,
      final UpdateStatus updateStatus) {

    if (particlesBStream == null)
      throw new RuntimeException("Unable to find particles A file");
    if (particlesAStream == null)
      throw new RuntimeException("Unable to find particles B file");

    this.particlesBFile = particlesBFile;
    this.particlesAFile = particlesAFile;
    this.particlesBStream = particlesBStream;
    this.particlesAStream = particlesAStream;
    this.resultPath = resultsPath;
    this.settings = settings;

    setUpdateStatus(updateStatus);
  }

}
