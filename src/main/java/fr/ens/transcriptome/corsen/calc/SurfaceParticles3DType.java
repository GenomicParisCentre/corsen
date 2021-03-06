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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.BitMapParticle3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particle3DBuilder;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.Point3D;

public class SurfaceParticles3DType extends DistanceProcessor {

  private static String TYPE = "Surface";

  @Override
  public String getPreProcessorType() {

    return TYPE;
  }

  @Override
  protected Map<Particle3D, List<Particle3D>> computePreprocessedParticles(
      final ProgressEventType eventType) {

    final Particles3D mitoParticles = getSourceParticles();

    if (mitoParticles == null)
      throw new NullPointerException("The source Particle is null");

    final float xlen = mitoParticles.getPixelWidth();
    final float ylen = mitoParticles.getPixelHeight();
    final float zlen = mitoParticles.getPixelDepth();

    Map<Particle3D, List<Particle3D>> mapCuboids =
        new HashMap<Particle3D, List<Particle3D>>();

    final int n = mitoParticles.getParticles().size();
    int i = 0;

    for (Particle3D par : mitoParticles.getParticles()) {

      Particle3DBuilder builder = new Particle3DBuilder(xlen, ylen, zlen);

      final BitMapParticle3D bitmap = par.getBitMapParticle();

      // Input particles : n particles, p points (p=sum of points in all
      // particles)
      // Output particles : n particles, p' point (p'<<=p, only the surface
      // points)

      for (Point3D p : bitmap.getSurfacePoints()) {

        builder.addInnerPoint(p);
        builder.addSurfacePoint(p);
      }
      builder.setBitMapParticle(par.getBitMapParticle());
      final List<Particle3D> surface =
          Collections.singletonList(builder.getParticle());

      final double p = (double) ++i / (double) n * 1000.0;
      sendEvent(eventType, (int) p);

      mapCuboids.put(par, surface);
    }

    return mapCuboids;
  }

  /**
   * Calc the minimal distance between the point and a mito (inner point), in
   * the case where the point is inside the mito.
   * @param mito The mito particle
   * @return the distance between the point and the mito
   */
  private List<Distance> calcAllDistances(final Particle3D mito,
      final Point3D point, final Particle3D particleOfPoint, boolean isNeg,
      List<Distance> result) {

    final AbstractListPoint3D listPoints = mito.getInnerPoints();

    if (result == null)
      result = new ArrayList<Distance>(listPoints.size());
    else
      result.clear();

    // Compute distance to surface (can be negative)
    for (final Point3D p : listPoints) {

      final float d = p.distance(point);
      result.add(new Distance(p, point, particleOfPoint, mito, isNeg ? -d : d));
    }

    return result;
  }

  @Override
  List<Distance> calcDistance(Particle3D particle, Point3D point,
      final Particle3D particleOfPoint, List<Distance> result) {

    if (point == null)
      throw new NullPointerException("Point is null");

    if (particle == null)
      throw new NullPointerException("Particle is null");

    // Compute standard distances
    return calcAllDistances(particle, point, particleOfPoint, particle
        .getBitMapParticle().isPointInParticle(point), result);
  }

  @Override
  AbstractListPoint3D getPresentationPointsA(AbstractListPoint3D points) {

    // Get all the point the surface of the particle
    return points;
  }

  @Override
  protected void setProperties(final Properties properties) {

  }

}
