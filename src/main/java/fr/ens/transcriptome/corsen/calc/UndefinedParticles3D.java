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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.model.ListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Point3D;

class UndefinedParticles3D extends DistanceProcessor {

  private static String TYPE = "Undefined";

  @Override
  public String getPreProcessorType() {

    return TYPE;
  }

  @Override
  public Map<Particle3D, List<Particle3D>> defineDestParticles(
      final ProgressEventType eventType) {

    Particles3D pars = getSourceParticles();

    if (pars == null)
      return null;

    Map<Particle3D, List<Particle3D>> result = new HashMap<Particle3D, List<Particle3D>>();

    int count = 0;
    final int countMax = pars.getParticles().size();

    for (Particle3D par : pars.getParticles()) {
      result.put(par, Collections.singletonList(par));
      count++;

      final double p = (double) count / (double) countMax
          * ProgressEvent.INDEX_IN_PHASE_MAX;
      sendEvent(eventType, (int) p);
    }

    return result;
  }

  @Override
  List<Distance> calcDistance(Particle3D particle, Point3D point,
      List<Distance> result) {

    if (particle == null)
      throw new NullPointerException("Particle to test is null");

    if (point == null)
      throw new NullPointerException("Point to test is null");

    if (result == null)
      result = new ArrayList<Distance>();
    else
      result.clear();

    for (Point3D p : particle.getInnerPoints())
      result.add(new Distance(p, point, p.distance(point)));

    return result;
  }

  @Override
  ListPoint3D getPresentationPoints(ListPoint3D points) {

    return points;
  }

}
