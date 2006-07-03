import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.StringTokenizer;

public final class Particle2D {

  public static int count;

  private final int id = count;
  private String name = "" + this.id;

  private final ListPoint2D surfacePoints = new ListPoint2D();
  private final ListPoint2D innerPoints = new ListPoint2D();

  private float pixelWidth = 1.0f;
  private float pixelHeight = 1.0f;

  private long intensity;
  private double mean;

  private double area;

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
  public double getMean() {
    return this.mean;
  }

  /**
   * Get the area of the particle.
   * @return Returns the area of the particle
   */
  public double getArea() {
    return this.area;
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

  public void addInnerPoint(final Point2D p) {

    if (p != null)
      addInnerPoint(p.getY(), p.getY(), p.getI());
  }

  public void addInnerPoint(final float x, final float y, final int i) {

    this.innerPoints.add(x, y, i);
    this.intensity += i;
  }

  public void addSurfacePoint(final Point2D p) {

    if (p != null)
      addSurfacePoint(p.getY(), p.getY(), p.getI());
  }

  public void addSurfacePoint(final float x, final float y, final int i) {

    this.surfacePoints.add(x, y, i);

  }

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
   * Add a particle 2D to the particle 3D.
   * @param imp Image of the particle to add
   * @param roi particle 2D to add
   */
  public void add(final ImagePlus imp, final PolygonRoi roi) {

    imp.setRoi(roi);
    final ImageStatistics stats = imp.getStatistics(); // mesurement

    // Get the x0 and y0 of the Roi
    final Rectangle r = roi.getBounds();
    final int nPoints = roi.getNCoordinates();
    final int[] xp = roi.getXCoordinates();
    final int[] yp = roi.getYCoordinates();

    final int x0 = r.x;
    final int y0 = r.y;

    // Get the inner points
    final ImageProcessor ip = imp.getMask();

    final int height = ip.getHeight();
    final int width = ip.getWidth();

    for (int i = 0; i < height; i++)
      for (int j = 0; j < width; j++)
        if (ip.getPixel(j, i) > 0) {

          final int val = imp.getProcessor().getPixel(j, i);
          addInnerPoint((j + x0 + 0.5f) * this.pixelWidth, (i + y0 + 0.5f)
              * this.pixelWidth, val);
        }

    // double[][] polygon = new double[nPoints][];

    this.surfacePoints.ensureCapacity(nPoints + this.surfacePoints.size());

    for (int i = 0; i < nPoints; i++) {

      // Add the point to the particle 2D

      final float x = (x0 + xp[i]) * this.pixelWidth;
      final float y = (y0 + yp[i]) * this.pixelHeight;

      addSurfacePoint(x, y);

      // System.out.println(x + "," + y);

      // Add the point to the polygon to calc the aera
      // final double[] coord = new double[2];
      // coord[0] = x;
      // coord[1] = y;
      // polygon[i] = coord;
    }

    this.area = stats.area;
    this.mean = stats.mean;

    if (false)
      isGoodSegmentation(5);

    // final double area = polygonArea(polygon);

    // System.out.println("Add Roi (" + nPoints + " surfacePoints) to particle "
    // + getId() + " Area=" + area + " Volume=" + volume);
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
  public boolean intersect(final Particle2D particle) {

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
  public boolean innnerPointIntersect(final Particle2D particle) {

    final int n = particle.innerPointsCount();

    if (particle == null || n == 0)
      return false;

    for (int i = 0; i < n; i++) {

      final Point2D p = particle.getInnerPoint(i);
      if (isInnerPoint(p.getX(), p.getY()))
        return true;
    }

    return false;
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
      if (this.innerPoints.isPoint(x, y))
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
      if (this.surfacePoints.isPoint(x, y))
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
   * Calc the surface of the particle.
   * @return The area of the particle
   */
  public double calcArea() {

    int i, j;
    double area = 0;

    final int n = surfacePointsCount();

    for (i = 0; i < n; i++) {
      j = (i + 1) % n;

      final Point2D pi = getSurfacePoint(i);
      final Point2D pj = getSurfacePoint(j);

      area += pi.getX() * pj.getY();
      area -= pi.getY() * pj.getX();
    }
    area /= 2.0;

    return area < 0 ? -area : area;
  }

  /**
   * Test if the segmentation is correcy.
   * @param distanceMax Distance maximal
   * @return true if the segmentation is correct
   */
  public boolean isGoodSegmentation(final float distanceMax) {

    final ListPoint2D listA = this.surfacePoints.copy();
    final ListPoint2D listB = new ListPoint2D();

    final Point2D p0 = listA.get(0);
    listA.remove(p0);
    listB.add(p0);

    for (int i = 0; i < listB.size(); i++) {

      final Point2D p1 = listB.get(i);

      for (int j = 0; j < listA.size(); j++) {

        final Point2D p2 = listA.get(j);

        if (p1.distance(p2) <= distanceMax) {
          listB.add(p2);
          listA.remove(p2);
        }

      }
    }

    if (listA.size() != 0) {
      System.out.println("WARNING : BAD Segmentation (" + listA.size() + "/"
          + listB.size() + ")");
      return false;
    }

    return true;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public Particle2D() {
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
  public Particle2D(final float pixelWidth, final float pixelHeight,
      final ImagePlus imp, final PolygonRoi roi) {

    this(pixelWidth, pixelHeight);
    add(imp, roi);
  }

  /**
   * Public constructor
   * @param s String to parse
   */
  public Particle2D(final String s) {

    this();
    parse(s);
  }

}
