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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.Point3D;

public class HugeParticles3D extends DistanceProcessor {

  private static String TYPE = "Huge";

  @Override
  public String getPreProcessorType() {

    return TYPE;
  }

  @Override
  protected Map<Particle3D, List<Particle3D>> defineDestParticles(
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

    for (Particle3D mito : mitoParticles.getParticles()) {

      final List<Particle3D> cuboids =
          CuboidUtil.defineCuboids(mito, xlen, ylen, zlen);

      final double p = (double) ++i / (double) n * 1000.0;
      sendEvent(eventType, (int) p);

      mapCuboids.put(mito, cuboids);
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
      final Point3D point, boolean isNeg, List<Distance> result) {

    final AbstractListPoint3D listPoints = mito.getInnerPoints();

    if (result == null)
      result = new ArrayList<Distance>(listPoints.size());
    else
      result.clear();

    for (final Point3D p : listPoints) {

      final float d = p.distance(point);

      result.add(new Distance(p, point, isNeg ? -d : d));
    }

    return result;
  }

  @Override
  List<Distance> calcDistance(Particle3D particle, Point3D point,
      List<Distance> result) {

    if (point == null)
      throw new NullPointerException("Point is null");

    if (particle == null)
      throw new NullPointerException("Particle is null");

    return calcAllDistances(particle, point, particle.getBitMapParticle()
        .isPointInParticle(point), result);
  }

  @Override
  AbstractListPoint3D getPresentationPoints(AbstractListPoint3D points) {

    return points;
  }

  @Override
  protected void setProperties(final Properties properties) {

  }

}
