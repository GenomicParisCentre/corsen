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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particle3DUtil;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.Point3D;
import fr.ens.transcriptome.corsen.model.SingletonListPoint3D;

public class DecompostionParticles3DType extends DistanceProcessor {

  private static String TYPE = "Decomposition";

  // private Particles3D cuboids;

  @Override
  public String getPreProcessorType() {

    return TYPE;
  }

  @Override
  public Map<Particle3D, List<Particle3D>> computePreprocessedParticles(
      final ProgressEventType eventType) {

    final Particles3D particles = getSourceParticles();

    if (particles == null)
      return null;

    final Map<Particle3D, List<Particle3D>> mapCuboids =
        new HashMap<Particle3D, List<Particle3D>>();

    final int countMax =
        Particle3DUtil.countInnerPointsInParticles(particles.getParticles());
    int count = 0;

    // Input particles : n particles, p points (p=sum of points in all
    // particles)
    // Output particles : n' particles (the cuboids, n'>=n), p points (same
    // number of total points)

    for (Particle3D par : particles.getParticles()) {

      float len = particles.getPixelDepth();
      if (particles.getPixelWidth() > len)
        len = particles.getPixelWidth();
      if (particles.getPixelHeight() > len)
        len = particles.getPixelHeight();

      len = len * Globals.CUBOID_SIZE_FACTOR;

      List<Particle3D> cuboids =
          Particle3DUtil.createCuboidToArrayList(par, len, len, len);

      mapCuboids.put(par, cuboids);

      count += par.innerPointsCount();
      final double p =
          (double) count / (double) countMax * ProgressEvent.INDEX_IN_PHASE_MAX;
      sendEvent(eventType, (int) p);
    }

    return mapCuboids;
  }

  @Override
  List<Distance> calcDistance(Particle3D particle, Point3D point,
      final Particle3D particleOfPoint, List<Distance> result) {

    if (particle == null)
      throw new NullPointerException("Particle to test is null");

    if (point == null)
      throw new NullPointerException("Point to test is null");

    final AbstractListPoint3D list = particle.getInnerPoints();

    if (result == null)
      result = new ArrayList<Distance>(list.size());
    else
      result.clear();

    // Compute standard distances
    for (Point3D p : list)
      result.add(new Distance(p, point, particleOfPoint, particle, p
          .distance(point)));

    return result;
  }

  @Override
  AbstractListPoint3D getPresentationPointsA(AbstractListPoint3D pointsA) {

    if (pointsA == null)
      return null;

    // Return the barycenter of Cuboids
    return new SingletonListPoint3D(pointsA.getBarycenter());
  }

  @Override
  protected void setProperties(final Properties properties) {

  }

}
