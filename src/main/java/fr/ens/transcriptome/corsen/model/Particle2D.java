package fr.ens.transcriptome.corsen.model;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.StringTokenizer;

public final class Particle2D {

  private static int count;

  private final int id = count;
  private String name = "" + this.id;

  private AbstractListPoint2D surfacePoints;
  private AbstractListPoint2D innerPoints;
  private boolean edgeParticle;

  private float pixelWidth = 1.0f;
  private float pixelHeight = 1.0f;

  private long intensity;

  //
  // Getters
  //

  /**
   * Get the id of the particle.
   * @return The id of the particle
   */
  public final int getId() {
    return this.id;
  }

  /**
   * Get the name of the particle.
   * @return Returns the name of the particle
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get the intensity of the particle.
   * @return Returns the intensity of the particle
   */
  public long getIntensity() {
    return this.intensity;
  }

  /**
   * Get the mean intensity of the particle.
   * @return Returns the mean intensity of the particle
   */
  /*
   * public double getMean() { return this.mean; }
   */

  /**
   * Get the area of the particle.
   * @return Returns the area of the particle
   */
  public double getArea() {
    return this.innerPointsCount() * this.pixelHeight * this.pixelWidth;
  }

  /**
   * Get the pixel height
   * @return Returns the pixelHeight
   */
  public float getPixelHeight() {
    return pixelHeight;
  }

  /**
   * Get the pixel width
   * @return Returns the pixelWidth
   */
  public float getPixelWidth() {
    return pixelWidth;
  }

  /**
   * Test if the particle is an edge particle.
   * @return true if the particle is an edge particle
   */
  public boolean isEdgeParticle() {

    return this.edgeParticle;
  }

  //
  // Setters
  //

  /**
   * Set the name of the particle.
   * @param name the new name of the particle
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Set if the particle is an edgeParticle.
   * @param edgeParticle true if the particle is an edge particle
   */
  void setEdgeParticle(final boolean edgeParticle) {

    this.edgeParticle = edgeParticle;
  }

  //
  // Other methods
  //

  /**
   * Get the number of surface points in the particle.
   * @return The number of surface point in the particle
   */
  public int surfacePointsCount() {

    return this.surfacePoints.size();
  }

  /**
   * Get the number of inner points in the particle.
   * @return The number of inner point in the particle
   */
  public int innerPointsCount() {

    return this.innerPoints.size();
  }

  /**
   * Add an inner point.
   * @param p Point to add
   */
  public void addInnerPoint(final Point2D p) {

    if (p != null)
      addInnerPoint(p.getY(), p.getY(), p.getI());
  }

  /**
   * Add an inner point.
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i Intensity of the point to add
   */
  public void addInnerPoint(final float x, final float y, final int i) {

    this.innerPoints.add(x, y, i);
    this.intensity += i;
  }

  /**
   * Add a surface point.
   * @param x X coordinate of the point to add
   */
  public void addSurfacePoint(final Point2D p) {

    if (p != null)
      addSurfacePoint(p.getY(), p.getY(), p.getI());
  }

  /**
   * Add an inner point.
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i Intensity of the point to add
   */
  public void addSurfacePoint(final float x, final float y, final int i) {

    this.surfacePoints.add(x, y, i);

  }

  /**
   * Add an inner point.
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   */
  public void addSurfacePoint(final float x, final float y) {

    this.surfacePoints.add(x, y);
  }

  /**
   * Get the surface point at index.
   * @param index Index of the point
   * @return The surface point at the index if exists.
   */
  public Point2D getSurfacePoint(final int index) {

    return this.surfacePoints.get(index);
  }

  /**
   * Get the inner point at index.
   * @param index Index of the point
   * @return The inner point at the index if exists.
   */
  public Point2D getInnerPoint(final int index) {

    return this.innerPoints.get(index);
  }

  /**
   * Get the center of the partcle.
   * @return A point with the coordinates of the center
   */
  public Point2D getCenter() {

    double x = 0;
    double y = 0;

    final int n = this.surfacePointsCount();

    for (int i = 0; i < n; i++) {
      final Point2D p = this.getSurfacePoint(i);
      x += p.getX();
      y += p.getY();

    }

    return new SimplePoint2DImpl((float) x / n, (float) y / n);
  }

  /**
   * Test the intersection of two particle.
   * @param particle Particle to test
   * @return true if an intersection exists
   */
  public boolean surfacePointIntersect(final Particle2D particle) {

    if (particle == null)
      return false;

    int n = surfacePointsCount();

    final Polygon p1 = new Polygon();
    for (int i = 0; i < n; i++) {
      final Point2D pt = getSurfacePoint(i);
      p1.addPoint((int) pt.getX(), (int) pt.getY());
    }

    n = particle.surfacePointsCount();

    final Polygon p2 = new Polygon();
    for (int i = 0; i < n; i++) {
      final Point2D pt = particle.getSurfacePoint(i);
      p2.addPoint((int) pt.getX(), (int) pt.getY());
    }

    final Area a1 = new Area(p1);
    final Area a2 = new Area(p2);
    a1.intersect(a2);

    return !a1.isEmpty();
  }

  /**
   * Test the intersection of two particles.
   * @param particle Particle to test
   * @return true if there is an intersection of the two particles
   */
  public boolean innerPointIntersect(final Particle2D particle) {

    if (particle == null)
      return false;

    AbstractListPoint2D l1 = this.innerPoints;
    AbstractListPoint2D l2 = particle.innerPoints;

    return l1.intersect(l2);
  }

  /**
   * Find the nearest inner point of the particle from a another point.
   * @param p Point to Test
   * @return the nearest point of the particle
   */
  public Point2D findNearestInnerPoint(final Point2D p) {

    final int n = innerPointsCount();

    if (p == null || n == 0)
      return null;

    float minDistance = Float.MAX_VALUE;
    Point2D nearestPoint = null;

    for (int i = 0; i < n; i++) {

      Point2D ip = getInnerPoint(i);
      final float distance = ip.distance(p);
      if (distance < minDistance) {
        minDistance = distance;
        nearestPoint = ip;
      }
    }

    return nearestPoint;
  }

  /**
   * Find the nearest surface point of the particle from a another point.
   * @param p Point to Test
   * @return the nearest point of the particle
   */
  public Point2D findNearestSurfacePoint(final Point2D p) {

    final int n = surfacePointsCount();

    if (p == null || n == 0)
      return null;

    float minDistance = Float.MAX_VALUE;
    Point2D nearestPoint = null;

    for (int i = 0; i < n; i++) {

      Point2D ip = getSurfacePoint(i);
      final float distance = ip.distance(p);
      if (distance < minDistance) {
        minDistance = distance;
        nearestPoint = ip;
      }
    }

    return nearestPoint;
  }

  /**
   * Find the nearests surface points of the particle from a another point.
   * @param p Point to Test
   * @param d Maximal distance
   * @return an array of the nearests points of the particle
   */
  public Point2D[] findNearestsSurfacePoints(final Point2D p,
      final float maxDistance) {

    final int n = surfacePointsCount();

    if (p == null || n == 0)
      return null;

    ArrayList<Point2D> al = new ArrayList<Point2D>();

    for (int i = 0; i < n; i++) {

      Point2D ip = getSurfacePoint(i);
      final float distance = ip.distance(p);
      if (distance < maxDistance) {
        al.add(ip);
      }
    }

    Point2D[] result = new Point2D[al.size()];
    al.toArray(result);

    return result;
  }

  /**
   * Test if the particle contains a point
   * @param x X coordinate of the point to test
   * @param y Y coordinate of the point to test
   * @return true if the particle contains the point
   */
  public boolean isInnerPoint(final float x, final float y) {

    final int n = innerPointsCount();

    for (int i = 0; i < n; i++)
      if (this.innerPoints.contains(x, y))
        return true;

    return false;
  }

  /**
   * Test if the particle contains a point
   * @param x X coordinate of the point to test
   * @param y Y coordinate of the point to test
   * @return true if the particle contains the point
   */
  public boolean isSurfacePoint(final float x, final float y) {

    final int n = surfacePointsCount();

    for (int i = 0; i < n; i++)
      if (this.surfacePoints.contains(x, y))
        return true;

    return false;
  }

  /**
   * Overide toString() method.
   * @return A string describing the point.
   */
  public String toString() {

    final StringBuffer sb = new StringBuffer();
    sb.append(getName());
    sb.append(';');
    sb.append(getCenter());
    sb.append(';');

    int n = surfacePointsCount();

    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(',');
      sb.append('(');
      sb.append(getSurfacePoint(i).toString());
      sb.append(')');
    }

    sb.append(';');

    n = innerPointsCount();

    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(',');
      sb.append('(');
      sb.append(getInnerPoint(i).toString());
      sb.append(')');
    }

    return sb.toString();
  }

  /**
   * Parse a String to generate a Particle2D.
   * @param s String to parse
   * @return A new Particle 2D generated from a string
   */
  public static Particle2D parse(final String s) {

    if (s == null)
      return null;

    final Particle2D par = new Particle2D();

    final StringTokenizer st = new StringTokenizer(s, ";");

    if (st.hasMoreElements())
      par.setName(st.nextToken());

    if (st.hasMoreElements())
      st.nextToken(); // center

    if (st.hasMoreElements()) {

      final String points = st.nextToken();

      final StringTokenizer st2 = new StringTokenizer(points, ")");

      boolean first = true;
      while (st2.hasMoreTokens()) {

        String s2 = st2.nextToken();

        if (first) {
          s2 = s2.substring(1, s2.length());
          first = false;
        } else
          s2 = s2.substring(2, s2.length());

        final Point2D p = Point2D.parse(s2);

        par.surfacePoints.add(p.getX(), p.getY());
      }
    }

    if (st.hasMoreElements()) {

      final String points = st.nextToken();

      final StringTokenizer st2 = new StringTokenizer(points, ")");

      boolean first = true;
      while (st2.hasMoreTokens()) {

        String s2 = st2.nextToken();

        if (first) {
          s2 = s2.substring(1, s2.length());
          first = false;
        } else
          s2 = s2.substring(2, s2.length());

        final Point2D p = Point2D.parse(s2);

        par.innerPoints.add(p.getX(), p.getY(), p.getI());
      }
    }

    return par;
  }

  /**
   * Apply a factor to all values of the x coordinates.
   * @param xFactor factor to apply
   */
  public void applyXFactor(final float xFactor) {

    this.surfacePoints.applyXFactor(xFactor);
    this.innerPoints.applyXFactor(xFactor);
  }

  /**
   * Apply a factor to all values of the y coordinates.
   * @param yFactor factor to apply
   */
  public void applyYFactor(final float yFactor) {

    this.surfacePoints.applyYFactor(yFactor);
    this.innerPoints.applyYFactor(yFactor);
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  private Particle2D() {
    count++;
  }

  /**
   * Public constructor
   * @param pixelWidth The width of a pixel
   * @param pixelHeight The height of a pixel
   */
  public Particle2D(final float pixelWidth, final float pixelHeight) {

    this();

    this.pixelHeight = pixelHeight;
    this.pixelWidth = pixelWidth;
    // this.surfacePoints = new AbstractListPoint2D(this.pixelWidth,
    // this.pixelHeight);
    this.surfacePoints =
        ListPoint2DFactory.createListPoint2D(this.pixelWidth, this.pixelHeight);
    this.innerPoints =
        ListPoint2DFactory.createListPoint2D(this.pixelWidth, this.pixelHeight);
  }

  /**
   * Public constructor
   * @param pixelWidth The width of a pixel
   * @param pixelHeight The height of a pixel
   * @param s String to parse
   */
  public Particle2D(final float pixelWidth, final float pixelHeight,
      final String s) {

    this(pixelWidth, pixelHeight);
    parse(s);
  }

  /**
   * Public constructor
   * @param pixelWidth The width of a pixel
   * @param pixelHeight The height of a pixel
   * @param imp Image of the polygon to add
   * @param roi particle 2D to add
   */
  /*
   * public Particle2D(final float pixelWidth, final float pixelHeight, final
   * ImagePlus imp, final PolygonRoi roi) { this(pixelWidth, pixelHeight);
   * add(imp, roi); }
   */

}
