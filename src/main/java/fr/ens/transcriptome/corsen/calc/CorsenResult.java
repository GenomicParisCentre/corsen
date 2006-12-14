package fr.ens.transcriptome.corsen.calc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.model.Particle3D;

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
  private Settings settings;
  private String messengersFilename;
  private String mitosFilename;
  private InputStream messengersStream;
  private InputStream mitosStream;
  private Particles3D messengers;
  private Particles3D mitos;
  private Particles3D cuboidsMessengers;
  private Particles3D cuboidsMitos;
  private Map<Particle3D, Distance> minDistances;
  private Map<Particle3D, Distance> maxDistances;

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
  UpdateStatus getUpdateStatus() {
    return updateStatus;
  }

  /**
   * Get the messengers file.
   * @return Returns the messengersFile
   */
  String getMessengersFilename() {
    return messengersFilename;
  }

  /**
   * Get the mitos file.
   * @return Returns the mitosFile
   */
  String getMitosFilename() {
    return mitosFilename;
  }

  /**
   * Get the messengers stream.
   * @return Returns the messengersFile
   */
  InputStream getMessengersStream() {
    return messengersStream;
  }

  /**
   * Get the mitos file.
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
  
  //
  // Setters
  //

  /**
   * Set the messengers particles.
   * @param messengers The messengers to set
   */
  void setMessengers(Particles3D messengers) {
    this.messengers = messengers;
  }

  /**
   * Set the mitos particles.
   * @param mitos The mitos to set
   */
  void setMitos(Particles3D mitos) {
    this.mitos = mitos;
  }

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
  void setCuboidsMitosParticles(final Particles3D particles) {

    this.cuboidsMitos = particles;
  }

  /**
   * Set the messengers cuboids particles.
   * @param particles Particles to Set
   */
  void setCuboidsMessengersParticles(final Particles3D particles) {

    this.cuboidsMessengers = particles;
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
   * @param mitosFile File of the mitos particles
   * @param messengersFile File of the messengers particles
   * @param updateStatus The updateStatus
   * @throws FileNotFoundException
   */
  public CorsenResult(final File messengersFile, final File mitosFile,
      final Settings settings, final UpdateStatus updateStatus)
      throws FileNotFoundException {

    this(messengersFile.getAbsolutePath(), mitosFile.getAbsolutePath(),
        new FileInputStream(messengersFile), new FileInputStream(mitosFile),
        settings, updateStatus);

  }

  /**
   * Public constructor.
   * @param mitosFile File of the mitos particles
   * @param messengersFile File of the messengers particles
   * @param updateStatus The updateStatus
   */
  public CorsenResult(final String messengersFilename,
      final String mitosFilename, final InputStream messengersStream,
      final InputStream mitosStream, final Settings settings,
      final UpdateStatus updateStatus) {

    if (mitosStream == null)
      throw new RuntimeException("Unable to find mito file");
    if (messengersStream == null)
      throw new RuntimeException("Unable to find messenger file");

    this.mitosFilename = mitosFilename;
    this.messengersFilename = messengersFilename;
    this.mitosStream = mitosStream;
    this.messengersStream = messengersStream;
    this.settings = settings;

    setUpdateStatus(updateStatus);
  }

}
