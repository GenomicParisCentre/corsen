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

import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.Point3D;

class AllPointsParticles3DType extends DistanceProcessor {

  private static String TYPE = "Allpoints";

  @Override
  public String getPreProcessorType() {

    return TYPE;
  }

  @Override
  public Map<Particle3D, List<Particle3D>> computePreprocessedParticles(
      final ProgressEventType eventType) {

    Particles3D pars = getSourceParticles();

    if (pars == null)
      return null;

    Map<Particle3D, List<Particle3D>> result =
        new HashMap<Particle3D, List<Particle3D>>();

    int count = 0;
    final int countMax = pars.getParticles().size();

    // Input particles : n particles, p points
    // Output particles : n particles (same particles), p points (same particles
    // points)

    for (Particle3D par : pars.getParticles()) {
      result.put(par, Collections.singletonList(par));
      count++;

      final double p =
          (double) count / (double) countMax * ProgressEvent.INDEX_IN_PHASE_MAX;
      sendEvent(eventType, (int) p);
    }

    return result;
  }

  @Override
  List<Distance> calcDistance(Particle3D particle, Point3D point,
      final Particle3D particleOfPoint, List<Distance> result) {

    if (particle == null)
      throw new NullPointerException("Particle to test is null");

    if (point == null)
      throw new NullPointerException("Point to test is null");

    if (result == null)
      result = new ArrayList<Distance>();
    else
      result.clear();

    // Compute standard distance
    for (Point3D p : particle.getInnerPoints())
      result.add(new Distance(p, point, particleOfPoint, particle, p
          .distance(point)));

    return result;
  }

  @Override
  AbstractListPoint3D getPresentationPointsA(AbstractListPoint3D pointsA) {

    // Get all the points of the particles
    return pointsA;
  }

  @Override
  protected void setProperties(final Properties properties) {

  }

}
