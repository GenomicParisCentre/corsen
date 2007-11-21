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

package fr.ens.transcriptome.corsen.model;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import fr.ens.transcriptome.corsen.util.Util;

public class Particle3DBuilder {

  private float pixelWidth;
  private float pixelHeight;
  private float pixelDepth;

  private Particle3D particle;
  private double volume = -1;
  private double area = -1;
  private double sphericity = -1;
  private long intensity = -1;

  /**
   * Add a particle 2D to the particle 3D.
   * @param particle particle 2D to add
   * @param slice Number of the slice in the stack
   */
  public void add(final Particle2D particle, final int slice) {

    init();

    final AbstractListPoint3D surfacePoints =
        this.particle.getModifiableSurfacePoints();
    final AbstractListPoint3D innerPoints =
        this.particle.getModifiableInnerPoints();
    final float pixelDepth = this.particle.getPixelDepth();

    int n = particle.surfacePointsCount();
    surfacePoints.ensureCapacity(n);

    for (int i = 0; i < n; i++) {
      final Point2D pt = particle.getSurfacePoint(i);
      addSurfacePoint(pt.getX(), pt.getY(), slice * pixelDepth);
    }

    n = particle.innerPointsCount();
    innerPoints.ensureCapacity(n);

    for (int i = 0; i < n; i++) {
      final Point2D pt = particle.getInnerPoint(i);
      addInnerPoint(pt.getX(), pt.getY(), slice * pixelDepth, pt.getI());
    }

    this.volume += particle.getArea() * pixelDepth;
    this.intensity += particle.getIntensity();

  }

  /**
   * Add the points of a particle to this particle.
   * @param particle Particle to add
   */
  public void add(final Particle3D particle) {

    init();

    if (this.particle.getId() == particle.getId())
      throw new RuntimeException("add the same particle to particle !!!");

    final AbstractListPoint3D surfacePoints =
        this.particle.getModifiableSurfacePoints();
    final AbstractListPoint3D innerPoints =
        this.particle.getModifiableInnerPoints();

    // Update the capacities of the lists of points
    surfacePoints.ensureCapacity(particle.surfacePointsCount());
    innerPoints.ensureCapacity(particle.innerPointsCount());

    // Add the points to the new particle
    surfacePoints.addAll(particle.getSurfacePoints());
    innerPoints.addAll(particle.getInnerPoints());

    // Update the volume and the intensity
    this.volume += particle.getVolume();
    this.intensity += particle.getIntensity();
  }

  public void addInnerPoint(final float x, final float y, final float z,
      final int i) {

    init();

    final AbstractListPoint3D innerPoints =
        this.particle.getModifiableInnerPoints();
    innerPoints.add(x, y, z, i);
    this.intensity += i;
  }

  /**
   * Add an inner point to the particle.
   * @param p Point to add
   */
  public void addInnerPoint(final Point3D p) {

    init();

    addInnerPoint(p, false);
  }

  /**
   * Add an inner point to the particle.
   * @param p Point to add
   */
  public void addInnerPoint(final Point3D p, final boolean testIfExists) {

    if (p == null)
      return;

    init();

    final AbstractListPoint3D innerPoints =
        this.particle.getModifiableInnerPoints();

    if (testIfExists && innerPoints.contains(p))
      return;

    innerPoints.add(p);
    this.intensity += p.getI();
  }

  public void addSurfacePoint(final float x, final float y, final float z) {

    init();

    final AbstractListPoint3D surfacePoints =
        this.particle.getModifiableSurfacePoints();
    surfacePoints.add(x, y, z, 0);
  }

  /**
   * Add a surface point to the particle.
   * @param p Point to add
   */
  public void addSurfacePoint(final Point3D p) {

    init();

    addSurfacePoint(p, false);
  }

  /**
   * Add a surface point to the particle.
   * @param p Point to add
   */
  public void addSurfacePoint(final Point3D p, final boolean testIfExists) {

    if (p == null)
      return;

    init();

    final AbstractListPoint3D surfacePoints =
        this.particle.getModifiableSurfacePoints();

    if (testIfExists && surfacePoints.contains(p))
      return;

    surfacePoints.add(p);
  }

  private void parse(final String s) {

    if (s == null)
      return;

    init();

    final StringTokenizer st = new StringTokenizer(s, "\t");

    if (st.hasMoreElements())
      this.particle.setName(st.nextToken());

    if (st.hasMoreElements())
      st.nextToken(); // center

    if (st.hasMoreElements())
      st.nextToken(); // barycenter

    if (st.hasMoreElements())
      this.area = Double.parseDouble(st.nextToken());

    if (st.hasMoreElements())
      this.volume = Double.parseDouble(st.nextToken());

    if (st.hasMoreElements())
      this.sphericity = Double.parseDouble(st.nextToken());

    if (st.hasMoreElements())
      this.intensity = Long.parseLong(st.nextToken());

    final Set<String> existingPoints = new HashSet<String>();

    if (st.hasMoreElements()) {

      final String points = st.nextToken();
      final int nbPoints = Util.charCount(points, '(');
      this.particle.getModifiableSurfacePoints().ensureCapacity(nbPoints);

      final StringTokenizer st2 = new StringTokenizer(points, ")");

      boolean first = true;
      while (st2.hasMoreTokens()) {

        String s2 = st2.nextToken();

        if (first) {
          s2 = s2.substring(1, s2.length());
          first = false;
        } else
          s2 = s2.substring(2, s2.length());

        final Point3D p = Point3D.parse(s2);
        final String key = p.toStringWithoutIntensity();

        if (!existingPoints.contains(key)) {
          addSurfacePoint(p, false);
          existingPoints.add(key);
        }
      }
    }

    existingPoints.clear();

    if (st.hasMoreElements()) {

      final String points = st.nextToken();
      final int nbPoints = Util.charCount(points, '(');
      this.particle.getModifiableInnerPoints().ensureCapacity(nbPoints);

      final StringTokenizer st2 = new StringTokenizer(points, ")");

      boolean first = true;
      while (st2.hasMoreTokens()) {

        String s2 = st2.nextToken();

        if (first) {
          s2 = s2.substring(1, s2.length());
          first = false;
        } else
          s2 = s2.substring(2, s2.length());

        final Point3D p = Point3D.parse(s2);
        final String key = p.toStringWithoutIntensity();

        if (!existingPoints.contains(key)) {
          addInnerPoint(p, false);
          existingPoints.add(key);
        }

      }

    }

  }

  /**
   * Get the number of inner points in the particle.
   * @return the number of inner points in the particle
   */
  public int innerPointsCount() {

    init();

    return particle.getModifiableInnerPoints().size();
  }

  /**
   * Get the number of surface points in the particle.
   * @return the number of inner points in the particle
   */
  public int surfacePointsCount() {

    init();

    return particle.getModifiableSurfacePoints().size();
  }

  /**
   * Get the id of the particle.
   * @return The id of the particle
   */
  public int getId() {

    init();

    return this.particle.getId();
  }

  /**
   * Set the name of the particle.
   * @param name The name of the particle to set
   */
  public void setName(final String name) {

    init();

    this.particle.setName(name);
  }

  private void init() {

    if (this.particle != null)
      return;

    this.particle =
        new Particle3D(this.pixelWidth, this.pixelHeight, this.pixelDepth);
    this.volume = 0;
    this.intensity = 0;

  }

  /**
   * Get the final particle. After calling this method, a new particle will be
   * created.
   * @return The final particle.
   */
  public Particle3D getParticle() {

    if (this.particle == null)
      return null;

    Particle3D result = this.particle;
    result.getModifiableInnerPoints().trimToSize();
    result.getModifiableSurfacePoints().trimToSize();
    result.setVolume(this.volume);
    result.setIntensity(this.intensity);
    result.setArea(this.area);
    result.setSphericity(this.sphericity);

    this.particle = null;

    return result;
  }

  //
  // Constructor
  //

  /**
   * Public constructor
   * @param pixelWidth The voxel Width
   * @param pixelHeight The pixel Height
   * @param pixelDepth The voxel Depth
   */
  public Particle3DBuilder(final float pixelWidth, final float pixelHeight,
      final float pixelDepth) {

    this.pixelWidth = pixelWidth;
    this.pixelHeight = pixelHeight;
    this.pixelDepth = pixelDepth;
  }

  /**
   * Public constructor
   * @param pixelWidth The voxel Width
   * @param pixelHeight The pixel Height
   * @param pixelDepth The voxel Depth
   * @param s String to parse
   */
  public Particle3DBuilder(final float pixelWidth, final float pixelHeight,
      final float pixelDepth, final String s) {

    this(pixelWidth, pixelHeight, pixelDepth);

    parse(s);
  }

}
