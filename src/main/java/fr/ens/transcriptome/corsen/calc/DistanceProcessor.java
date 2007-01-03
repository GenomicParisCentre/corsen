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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Point3D;

public abstract class DistanceProcessor {

  private Particles3D sourceParticles;
  private Map<Particle3D, List<Particle3D>> mapDestParticles;
  private List<Particle3D> destParticles;

  private UpdateStatus updateStatus;

  //
  // Getters
  //

  /**
   * Get the source particles
   * @return Returns the sources particles
   */
  public Particles3D getSourceParticles() {
    return sourceParticles;
  }

  public List<Particle3D> getDestParticles() {

    return destParticles;
  }

  //
  // Setters
  //

  /**
   * Set the source particles
   * @param particles The particles to set
   */
  public void setSourceParticles(Particles3D particles) {
    this.sourceParticles = particles;
  }

  /**
   * Set the dest particles.
   * @param destParticles
   */
  private void setDestParticles(
      final Map<Particle3D, List<Particle3D>> destParticles) {

    this.mapDestParticles = destParticles;
    this.destParticles = new ArrayList<Particle3D>();

    for (Particle3D particles : destParticles.keySet())
      this.destParticles.addAll(destParticles.get(particles));

  }

  /**
   * Set the update status
   * @param updateStatus The updateStatus to set
   */
  public void setUpdateStatus(UpdateStatus updateStatus) {
    this.updateStatus = updateStatus;
  }

  //
  // Other methods
  //

  protected void sendEvent(final ProgressEventType type, final int value1) {

    if (this.updateStatus == null)
      return;

    this.updateStatus.updateStatus(new ProgressEvent(type, value1));
  }

  List<Distance> calcDistance(final Particle3D particle,
      final AbstractListPoint3D points) {

    if ((particle == null || points == null))
      return null;

    List<Distance> result = new ArrayList<Distance>();

    List<Distance> resultForOnePoint = null;
    for (Point3D point : points)
      result.addAll(resultForOnePoint = calcDistance(particle, point,
          resultForOnePoint));

    return result;
  }

  final void preprocess(final ProgressEventType eventType) {

    setDestParticles(defineDestParticles(eventType));
  }

  //
  // Abstract methods
  //

  protected abstract Map<Particle3D, List<Particle3D>> defineDestParticles(
      ProgressEventType eventType);

  public abstract String getPreProcessorType();

  abstract List<Distance> calcDistance(final Particle3D particle,
      final Point3D point, List<Distance> result);

  abstract AbstractListPoint3D getPresentationPoints(final AbstractListPoint3D points);

  abstract void setProperties(Properties properties);

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public DistanceProcessor() {
  }

}
