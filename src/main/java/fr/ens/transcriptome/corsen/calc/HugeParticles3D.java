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
import fr.ens.transcriptome.corsen.model.Point3D;

public class HugeParticles3D extends DistanceProcessor {

  private static String TYPE = "Huge";

  // private Map<Particle3D, List<Particle3D>> mapCuboids = new
  // HashMap<Particle3D, List<Particle3D>>();

  private float xlen;
  private float ylen;
  private float zlen;
  private float radius;
  private Map<Point3D, Float> exclusionPoints;

  private Point3D lastPoint;
  private boolean lastPointInside;

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

    this.xlen = xlen;
    this.ylen = ylen;
    this.zlen = zlen;
    this.radius = (float) Math.sqrt(xlen * xlen + ylen * ylen + zlen * zlen);
    this.exclusionPoints = new HashMap<Point3D, Float>();

    // ArrayList<Particle3D> al = new ArrayList<Particle3D>();
    Map<Particle3D, List<Particle3D>> mapCuboids = new HashMap<Particle3D, List<Particle3D>>();

    final int n = mitoParticles.getParticles().size();
    int i = 0;

    for (Particle3D mito : mitoParticles.getParticles()) {

      final double p = (double) i / (double) n * 1000.0;
      sendEvent(eventType, (int) p);

      final List<Particle3D> cuboids = CuboidUtil.defineCuboids(mito, xlen,
          ylen, zlen);

      /*
       * for (int j = 0; j < cuboids.length; j++) { al.add(cuboids[j]); }
       */

      mapCuboids.put(mito, cuboids);
    }

    return mapCuboids;
  }

  /**
   * Test if a point is include in the space of a mito particle.
   * @param mitos The mitos particle
   * @param xlen x min length
   * @param ylen y min length
   * @param zlen z min length
   * @return true if the point is include in the mito
   */
  private final float isPointInMito(final Particles3D mitos,
      final Point3D point, final float xlen, final float ylen, final float zlen) {

    if (mitos == null)
      throw new NullPointerException("The particles is null");
    if (point == null)
      throw new NullPointerException("The point to test is null is null");

    // Point3D nearestAllParticles = null;
    float distanceMinAllParticles = Float.MAX_VALUE;

    final float radius = this.radius;

    for (Particle3D mito : mitos.getParticles()) {

      Point3D nearest = mito.getNearestInnerPoint(point);

      if (nearest == null)
        continue;

      final float d = nearest.distance(point);

      if (d < distanceMinAllParticles)
        distanceMinAllParticles = d;

      if (d > radius)
        continue;

      int count = 0;

      for (Point3D p : mito.getInnerPoints())
        if (point.isNear(p, xlen, ylen, zlen))
          count++;

      // if (count > 3)
      if (count > 1)
        return -distanceMinAllParticles;

      return distanceMinAllParticles;
    }

    return distanceMinAllParticles;
  }

  /**
   * Test if a point is include in the space of a mito particle.
   * @param mitos The mito particles
   * @param xlen x min length
   * @param ylen y min length
   * @param zlen z min length
   * @return true if the point is include in the mito
   */
  private final boolean isPointInMitoCache(final Particles3D mitos,
      final Point3D point, final float xlen, final float ylen, final float zlen) {

    if (point.equals(this.lastPoint))
      return this.lastPointInside;

    if (true)
      return saveLastPointResult(point, isPointInMito(mitos, point, xlen, ylen,
          zlen) < 0);

    Point3D nearest = null;
    float nearestDistance = Float.MAX_VALUE;

    for (Point3D p : this.exclusionPoints.keySet()) {

      float d = point.distance(p);
      if (d < nearestDistance) {
        nearest = p;
        nearestDistance = d;
      }
    }

    boolean testResult = false;

    float nearestDistanceToMito = Float.NaN;
    if (nearest != null) {

      // float nearestDistanceToMito = this.exclusionPoints.get(nearest);
      nearestDistanceToMito = this.exclusionPoints.get(nearest);
      if (nearestDistance < (Math.abs(nearestDistanceToMito) / 2))
        // return saveLastPointResult(point, nearestDistanceToMito < 0);
        testResult = saveLastPointResult(point, nearestDistanceToMito < 0);
    }

    float d = isPointInMito(mitos, point, xlen, ylen, zlen);

    this.exclusionPoints.put(point, d);

    boolean result = saveLastPointResult(point, d < 0);

    if (nearest != null && result != testResult) {
      System.err.println("error optimisation !!!\td=" + d + "\tnearstDistance="
          + nearestDistance + "\tnearestDistanceToMito="
          + nearestDistanceToMito);

    }

    return result;
  }

  private final boolean saveLastPointResult(final Point3D point, boolean result) {

    this.lastPoint = point;
    this.lastPointInside = result;

    return result;
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

    final float radius = this.radius;

    for (final Point3D p : listPoints) {

      final float d = p.distance(point);
      result.add(new Distance(p, point, (isNeg && d < radius) ? -d : d));
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

    Particles3D mitos = this.getSourceParticles();

    return calcAllDistances(particle, point, isPointInMitoCache(mitos, point,
        this.xlen, this.ylen, this.zlen), result);

    // return calcAllDistances(particle, point, false);

  }

  @Override
  AbstractListPoint3D getPresentationPoints(AbstractListPoint3D points) {

    return points;
  }

  @Override
  protected void setProperties(final Properties properties) {

  }

}
