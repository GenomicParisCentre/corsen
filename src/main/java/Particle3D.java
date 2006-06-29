import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * By Laurent Jourdren
 */

public final class Particle3D {

  private static int count;

  private int id = count;
  private String name = "" + id;

  private final ListPoint3D surfacePoints;
  private final ListPoint3D innerPoints;

  private float pixelDepth = 1.0f;

  private double volume;
  private long intensity;

  //
  // Getters
  //

  /**
   * Return the volume of the particle.
   * @return The volume of the particle
   */
  public final double getVolume() {
    return this.volume;
  }

  /**
   * Get the id of the particle.
   * @return The id of the particle
   */
  public final int getId() {
    return id;
  }

  /**
   * Get the name of the particle.
   * @return Returns the name of the particle
   */
  public String getName() {
    return name;
  }

  /**
   * Get the intensity of the particle.
   * @return Returns the intensity of the particle
   */
  public long getIntensity() {
    return intensity;
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
   * Test if the particle contains an inner point.
   * @param p Inner point to test
   * @return true if the particle contains the inner point
   */
  public final boolean containsInnerPoint(final Point3D p) {

    if (p == null)
      return false;

    final int n = innerPointsCount();

    for (int i = 0; i < n; i++)
      if (getInnerPoint(i).equals(p))
        return true;

    return false;
  }

  /**
   * Test if the particle contains a surface point.
   * @param p Surface point to test
   * @return true if the particle contains the surface point
   */
  public final boolean containsSurfacePoint(final Point3D p) {

    if (p == null)
      return false;

    final int n = surfacePointsCount();

    for (int i = 0; i < n; i++)
      if (getSurfacePoint(i).equals(p))
        return true;

    return false;
  }

  /**
   * Add a surface point to the particle.
   * @param p Point to add
   */
  public final void addSurfacePoint(final Point3D p) {

    addSurfacePoint(p, false);
  }

  /**
   * Add a surface point to the particle.
   * @param p Point to add
   */
  public final void addSurfacePoint(final Point3D p, final boolean testIfExists) {

    if (p == null)
      return;

    if (testIfExists && containsSurfacePoint(p))
      return;

    this.surfacePoints.add(p);
  }

  public final void addSurfacePoint(final float x, final float y, final float z) {

    this.surfacePoints.add(x, y, z);
  }

  /**
   * Add an inner point to the particle.
   * @param p Point to add
   */
  public final void addInnerPoint(final Point3D p) {

    addInnerPoint(p, false);
  }

  /**
   * Add an inner point to the particle.
   * @param p Point to add
   */
  public final void addInnerPoint(final Point3D p, final boolean testIfExists) {

    if (p == null)
      return;

    if (testIfExists && containsInnerPoint(p))
      return;

    this.innerPoints.add(p);
  }

  public final void addInnerPoint(final float x, final float y, final float z,
      final int i) {

    this.innerPoints.add(x, y, z, i);
  }

  /**
   * Add the points of a particle to this particle.
   * @param particle Particle to add
   */
  public final void add(final Particle3D particle) {

    if (particle == null)
      return;

    if (this.id == particle.id) {
      System.out.println("add the same particle to particle !!!");
      return;
    }

    final int ns = particle.surfacePointsCount();
    this.surfacePoints.ensureCapacity(ns);

    this.surfacePoints.merge(particle.surfacePoints);

    /*
     * for (int i = 0; i < ns; i++) { final Point3D p =
     * particle.getSurfacePoint(i); addSurfacePoint(p); }
     */

    final int ni = particle.innerPointsCount();
    this.innerPoints.ensureCapacity(ni);

    this.innerPoints.merge(particle.innerPoints);

    /*
     * for (int i = 0; i < ni; i++) { final Point3D p =
     * particle.getInnerPoint(i); addInnerPoint(p); }
     */

    this.volume += particle.volume;
    this.intensity += particle.intensity;
  }

  /**
   * Get the number of surface points in the particle.
   * @return The number of point in the particle
   */
  public final int surfacePointsCount() {

    return this.surfacePoints.size();
  }

  /**
   * Get the number of surfacePoints in the particle.
   * @return The number of point in the particle
   */
  public final int innerPointsCount() {

    return this.innerPoints.size();
  }

  /**
   * Get the surface point at index.
   * @param index Index of the point
   * @return The point at the index if exists.
   */
  public final Point3D getSurfacePoint(final int index) {

    return this.surfacePoints.get(index);
  }

  /**
   * Get the inner point at index.
   * @param index Index of the point
   * @return The point at the index if exists.
   */
  public final Point3D getInnerPoint(final int index) {

    return this.innerPoints.get(index);
  }

  /**
   * Add a particle 2D to the particle 3D.
   * @param particle particle 2D to add
   * @param slice Number of the slice in the stack
   */
  public void add(final Particle2D particle, final int slice) {

    if (particle == null)
      return;

    int n = particle.surfacePointsCount();
    this.surfacePoints.ensureCapacity(n);

    for (int i = 0; i < n; i++) {
      Point2D pt = particle.getSurfacePoint(i);
      addSurfacePoint(pt.getX(), pt.getY(), slice * this.pixelDepth);
    }

    n = particle.innerPointsCount();
    this.innerPoints.ensureCapacity(n);

    for (int i = 0; i < n; i++) {
      Point2D pt = particle.getInnerPoint(i);
      addInnerPoint(pt.getX(), pt.getY(), slice * this.pixelDepth, pt.getI());
    }

    this.volume += particle.getArea() * this.pixelDepth;
    this.intensity += particle.getIntensity();
  }

  /**
   * Add a particle 2D to the particle 3D.
   * @param roi particle 2D to add
   * @param slice Number of the slice in the stack
   */
  /*
   * public void add(final PolygonRoi roi, final int slice) { Rectangle r =
   * roi.getBounds(); final int nPoints = roi.getNCoordinates(); int[] xp =
   * roi.getXCoordinates(); int[] yp = roi.getYCoordinates(); int x0 = r.x; int
   * y0 = r.y; double[][] polygon = new double[nPoints][]; for (int i = 0; i <
   * nPoints; i++) { // Add the point to the particle 3D final double x = (x0 +
   * xp[i]) * this.pixelWidth; final double y = (y0 + yp[i]) * this.pixelHeight;
   * final double z = slice * this.pixelDepth; final Point3D p = new Point3D(x,
   * y, z); add(p); // Add the point to the polygon to calc the aera final
   * double[] coord = new double[2]; coord[0] = x; coord[1] = y; polygon[i] =
   * coord; } final double area = polygonArea(polygon); this.volume += area *
   * this.pixelDepth; // System.out.println("Add Roi (" + nPoints + "
   * surfacePoints) to particle " // + getId() + " Area=" + area + " Volume=" +
   * volume); }
   */

  /**
   * Get the center of the partcle.
   * @return A point with the coordinates of the center
   */
  public Point3D getCenter() {

    double x = 0;
    double y = 0;
    double z = 0;

    final int n = this.surfacePointsCount();

    for (int i = 0; i < n; i++) {
      final Point3D p = this.getSurfacePoint(i);
      x += p.getX();
      y += p.getY();
      z += p.getZ();
    }

    return new SimplePoint3DImpl((float) x / n, (float) y / n, (float) z / n);
  }

  /**
   * Get the barycenter of the partcle.
   * @return A point with the coordinates of the center
   */
  public Point3D getBarycenter() {

    double x = 0;
    double y = 0;
    double z = 0;

    final int n = this.innerPointsCount();
    int sum = 0;

    for (int i = 0; i < n; i++) {
      final Point3D p = this.getInnerPoint(i);
      final int val = p.getI();
      sum += val;
      x += p.getX() * val;
      y += p.getY() * val;
      z += p.getZ() * val;
    }

    if (n == 0)
      return getCenter();

    return new SimplePoint3DImpl((float) x / sum, (float) y / sum, (float) z
        / sum, sum / n);
  }

  /**
   * Calc the distance between 2 objects from the surface.
   * @param p Particle to get the distance
   * @return The distance between the 2 objects
   */
  public double getSurfaceToSurfaceDistance(final Particle3D p) {

    double min = Double.MAX_VALUE;

    if (p == null)
      return min;

    final int n = surfacePointsCount();
    final int pn = p.surfacePointsCount();
    for (int i = 0; i < n; i++) {

      final Point3D p1 = getSurfacePoint(i);

      for (int j = 0; j < pn; j++) {

        final Point3D p2 = p.getSurfacePoint(j);

        double d = p1.distance(p2);

        if (d < min)
          min = d;
      }
    }

    return min;
  }

  /**
   * Calc the distance between 2 objects from the center to the surface of the
   * second object.
   * @param p Particle to get the distance
   * @return The distance between the 2 objects
   */
  public double getBarycenterToSurfaceDistance(final Particle3D p) {

    double min = Double.MAX_VALUE;

    if (p == null)
      return min;

    final int pn = p.surfacePointsCount();

    final Point3D p1 = getBarycenter();

    for (int i = 0; i < pn; i++) {

      final Point3D p2 = p.getSurfacePoint(i);

      double d = p1.distance(p2);
      if (d < min)
        min = d;
    }

    return min;
  }

  /**
   * Calc the distance between 2 objects from the center to the surface of the
   * second object.
   * @param p Particle to get the distance
   * @return The distance between the 2 objects
   */
  public double getBarycenterToInnerDistance(final Particle3D p) {

    double min = Double.MAX_VALUE;

    if (p == null)
      return min;

    final int pn = p.innerPointsCount();

    final Point3D p1 = getBarycenter();

    for (int i = 0; i < pn; i++) {

      final Point3D p2 = p.getInnerPoint(i);

      double d = p1.distance(p2);
      if (d < min)
        min = d;
    }

    return min;
  }

  /**
   * Calc the distance between 2 objects from the center of the objects.
   * @param p Particle to get the distance
   * @return The distance between the 2 objects
   */
  final public double getCenterToCenterDistance(final Particle3D p) {

    double min = Double.MAX_VALUE;

    if (p == null)
      return min;

    final Point3D p1 = getCenter();
    final Point3D p2 = p.getCenter();

    return p1.distance(p2);
  }

  private static Map getSurfacePointSlices(final Particle3D particle) {

    Map slices = new HashMap();

    final int nPoints = particle.surfacePointsCount();

    for (int i = 0; i < nPoints; i++) {
      Point3D p = particle.getSurfacePoint(i);
      String key = "" + p.getZ();
      Particle2D par = (Particle2D) slices.get(key);
      if (par == null) {
        par = new Particle2D();
        slices.put(key, par);
      }
      par.addSurfacePoint(p.getX(), p.getY());
    }

    return slices;
  }

  private static Map getSurfaceAndInnerPointSlices(final Particle3D particle) {

    Map slices = new HashMap();

    final int nSurfacePoints = particle.surfacePointsCount();

    for (int i = 0; i < nSurfacePoints; i++) {
      Point3D p = particle.getSurfacePoint(i);
      String key = "" + p.getZ();
      Particle2D par = (Particle2D) slices.get(key);
      if (par == null) {
        par = new Particle2D();
        slices.put(key, par);
      }
      par.addSurfacePoint(p.getX(), p.getY());
    }

    final int nInnerPoints = particle.innerPointsCount();

    for (int i = 0; i < nInnerPoints; i++) {
      Point3D p = particle.getInnerPoint(i);
      String key = "" + p.getZ();
      Particle2D par = (Particle2D) slices.get(key);
      if (par == null) {
        par = new Particle2D();
        slices.put(key, par);
      }
      par.addInnerPoint(p.getX(), p.getY(), p.getI());
    }

    return slices;
  }

  /**
   * Transfort the particle in an array of particles2D.
   * @return An array of Particles 2D
   */
  public Particle2D[] toParticles2D() {

    Map slices = getSurfaceAndInnerPointSlices(this);

    if (slices == null || slices.size() == 0)
      return null;

    final int n = slices.size();

    float[] keys = new float[n];

    Iterator it = slices.keySet().iterator();

    int i = 0;
    while (it.hasNext()) {

      keys[i] = Float.parseFloat((String) it.next());
      i++;
    }

    Arrays.sort(keys);

    Particle2D[] result = new Particle2D[n];

    for (int j = 0; j < n; j++)
      result[j] = (Particle2D) slices.get("" + keys[j]);

    int innerCount = 0;
    int surfaceCount = 0;
    for (int j = 0; j < result.length; j++) {

      innerCount += result[j].innerPointsCount();
      surfaceCount += result[j].surfacePointsCount();
    }

    if (surfaceCount != this.surfacePointsCount())
      System.out.println("### Invalid Surface point Particle3D: "
          + this.surfacePointsCount() + "/" + surfaceCount + " (Particle2D[])");

    if (innerCount != this.innerPointsCount())
      System.out.println("### Inner point Particle3D: "
          + this.innerPointsCount() + "/" + innerCount + " (Particle2D[])");

    return result;
  }

  /**
   * Test the intersection of two particle.
   * @param particle Particle to test
   */
  public boolean intersect(final Particle3D particle) {

    if (particle == null)
      return false;

    Map slices = getSurfacePointSlices(this);
    Map particlesSlices = getSurfacePointSlices(particle);

    Iterator it = slices.keySet().iterator();
    while (it.hasNext()) {
      String z = (String) it.next();

      Particle2D p1 = (Particle2D) slices.get(z);
      Particle2D p2 = (Particle2D) particlesSlices.get(z);

      if (p1 != null && p2 != null && p1.intersect(p2))
        return true;

    }

    return false;
  }

  /**
   * Overide toString() method.
   * @return A string describing the point.
   */
  public String toString() {

    StringBuffer sb = new StringBuffer();
    sb.append(getName());
    sb.append('\t');
    sb.append(getCenter());
    sb.append('\t');
    sb.append(getBarycenter());
    sb.append('\t');
    sb.append(getVolume());
    sb.append('\t');
    sb.append(getIntensity());
    sb.append('\t');

    int n = surfacePointsCount();

    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(',');
      sb.append('(');
      sb.append(getSurfacePoint(i).toString());
      sb.append(')');
    }

    sb.append('\t');
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

  private void parse(final String s) {

    if (s == null)
      return;

    StringTokenizer st = new StringTokenizer(s, "\t");

    if (st.hasMoreElements()) {
      setName(st.nextToken());
    }

    if (st.hasMoreElements()) {
      st.nextToken(); // center
    }

    if (st.hasMoreElements()) {
      st.nextToken(); // barycenter
    }

    if (st.hasMoreElements()) {
      this.volume = (Double.parseDouble(st.nextToken()));
    }
    if (st.hasMoreElements()) {
      this.intensity = (Long.parseLong(st.nextToken()));
    }

    Set existingPoints = new HashSet();

    if (st.hasMoreElements()) {

      final String points = st.nextToken();

      StringTokenizer st2 = new StringTokenizer(points, ")");

      boolean first = true;
      while (st2.hasMoreTokens()) {

        String s2 = st2.nextToken();

        if (first) {
          s2 = s2.substring(1, s2.length());
          first = false;
        } else
          s2 = s2.substring(2, s2.length());

        final Point3D p = SimplePoint3DImpl.parse(s2);
        String key = p.toStringWithoutIntensity();

        if (!existingPoints.contains(key)) {
          addSurfacePoint(p, false);
          existingPoints.add(key);
        }
      }
    }

    existingPoints.clear();

    if (st.hasMoreElements()) {

      final String points = st.nextToken();

      StringTokenizer st2 = new StringTokenizer(points, ")");

      boolean first = true;
      while (st2.hasMoreTokens()) {

        String s2 = st2.nextToken();

        if (first) {
          s2 = s2.substring(1, s2.length());
          first = false;
        } else
          s2 = s2.substring(2, s2.length());

        final Point3D p = SimplePoint3DImpl.parse(s2);
        String key = p.toStringWithoutIntensity();

        if (!existingPoints.contains(key)) {
          addInnerPoint(p, false);
          existingPoints.add(key);
        }

      }

    }

  }

  /**
   * Generate R code to plot the inners points
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @return A String with the R code to plot the points
   */
  /*
   * public String innerPointstoR(final float size, final String colorName) {
   * final int n = innerPointsCount(); if (n == 0) return ""; StringBuffer sb =
   * new StringBuffer(); sb.append("x <- c("); for (int i = 0; i < n; i++) { if
   * (i > 0) sb.append(","); final Point3D p; p = getInnerPoint(i);
   * sb.append(p.getX()); } sb.append(")\ny <- c("); for (int i = 0; i < n; i++) {
   * if (i > 0) sb.append(","); final Point3D p; p = getInnerPoint(i);
   * sb.append(p.getY()); } sb.append(")\nz <- c("); for (int i = 0; i < n; i++) {
   * if (i > 0) sb.append(","); final Point3D p; p = getInnerPoint(i);
   * sb.append(p.getZ()); } sb.append(")\n"); sb.append("points3d(x, y, z,
   * size="); sb.append(size); if (colorName != null) { sb.append(",color=\"");
   * sb.append(colorName); sb.append("\""); } sb.append(")\n"); return
   * sb.toString(); }
   */

  /**
   * Generate R code to plot the inners points
   * @param out Writer used to write data
   * @throws IOException if an error occurs while write data
   */
  public void innerPointstoRData(final Writer out) throws IOException {

    if (out == null)
      return;

    final int n = innerPointsCount();

    if (n == 0)
      return;

    out.write("x <- c(");

    for (int i = 0; i < n; i++) {
      if (i > 0)
        out.write(",");

      final Point3D p;

      p = getInnerPoint(i);

      out.write("" + p.getX());
    }

    out.write(")\ny <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        out.write(",");

      final Point3D p;

      p = getInnerPoint(i);

      out.write("" + p.getY());
    }

    out.write(")\nz <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        out.write(",");

      final Point3D p;

      p = getInnerPoint(i);

      out.write("" + p.getZ());
    }

    out.write(")\n");

  }

  /**
   * Generate R code to plot the inners points
   * @param out Writer used to write data
   * @param sphere plot spheres
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @throws IOException if an error occurs while write data
   */
  public void innerPointstoRPlot(final Writer out, final boolean sphere,
      final String size, final String colorName) throws IOException {

    if (sphere)
      out.write("rgl.spheres(x, y, z, r=" + size);
    else
      out.write("points3d(x, y, z, size=" + size);

    if (colorName != null) {
      out.write(",color=\"");
      out.write(colorName);
      out.write("\"");
    }
    out.write(")\n");

  }

  /**
   * Generate R code to plot the inners points
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @return A String with the R code to plot the points
   */
  public String surfacePointLinestoR(final float size, final String colorName) {

    final int n = surfacePointsCount();

    if (n == 0)
      return "";

    StringBuffer sb = new StringBuffer();

    sb.append("x <- c(");

    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(",");

      final Point3D p;

      p = getSurfacePoint(i);

      sb.append(p.getX());
    }

    sb.append(")\ny <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(",");

      final Point3D p;

      p = getSurfacePoint(i);

      sb.append(p.getY());
    }

    sb.append(")\nz <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(",");

      final Point3D p;

      p = getSurfacePoint(i);

      sb.append(p.getZ());
    }

    sb.append(")\n");
    sb.append("points3d(x, y, z, size=");
    sb.append(size);
    if (colorName != null) {
      sb.append(",color=\"");
      sb.append(colorName);
      sb.append("\"");
    }
    sb.append(")\n");

    return sb.toString();
  }

  /**
   * Generate R code to plot the surface points
   * @param out Writer used to write data
   * @throws IOException if an error occurs while writing data
   */
  public void surfacePointstoRData(final Writer out) throws IOException {

    if (out == null)
      return;

    if (surfacePointsCount() == 0)
      return;

    Map slices = getSurfacePointSlices(this);

    Iterator it = slices.keySet().iterator();

    while (it.hasNext()) {

      String z = (String) it.next();

      Particle2D par = (Particle2D) slices.get(z);
      final int n = par.surfacePointsCount();

      if (par.surfacePointsCount() == 0)
        continue;

      out.write("x <- c(");

      for (int i = 0; i < n; i++) {
        final Point2D p;
        if (i > 0)
          out.write(",");
        p = par.getSurfacePoint(i);

        out.write("" + p.getX());
        // sb.append(",");
      }
      // sb.append("" + getSurfacePoint(0).getX());

      out.write(")\ny <- c(");
      for (int i = 0; i < n; i++) {
        if (i > 0)
          out.write(",");
        final Point2D p;

        p = par.getSurfacePoint(i);

        out.write("" + p.getY());
        // sb.append(",");
      }
      // sb.append("" + getSurfacePoint(0).getY());

      out.write(")\nz <- c(");
      for (int i = 0; i < n + 1; i++) {
        if (i > 0)
          out.write(",");

        out.write(z);
      }

      out.write(")\n");

    }

  }

  /**
   * Generate R code to plot the surface points
   * @param out Writer used to write data
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @throws IOException if an error occurs while writing data
   */
  public void surfacePointstoRPlot(final Writer out, final float size,
      final String colorName) throws IOException {

    if (out == null)
      return;

    out.write("lines3d(x, y, z, size=" + size);
    if (colorName != null) {
      out.write(",color=\"");
      out.write(colorName);
      out.write("\"");
    }
    out.write(")\n");

  }

  /**
   * Recalc the intensity of the particle from the inners points.
   */
  public void setIntensityFromInnerPoints() {

    int intensity = 0;

    final int n = innerPointsCount();

    for (int i = 0; i < n; i++)
      intensity += getInnerPoint(i).getI();

    this.intensity = intensity;
  }

  ListPoint3D getInnerPoints() {
    return this.innerPoints;
  }

  /**
   * Get the minimal distance between two inner points of the particle.
   * @return the minimal distance between two inner points of the particle
   */
  public float getMinDistance() {

    final int n = innerPointsCount();

    if (n < 2)
      return Float.MAX_VALUE;

    float min = Float.MAX_VALUE;

    for (int i = 0; i < n - 1; i++) {

      final Point3D p1 = getInnerPoint(i);

      for (int j = i + 1; j < n; j++) {
        final Point3D p2 = getInnerPoint(j);

        final float d = p1.distance(p2);

        if (d < min)
          min = d;
      }
    }

    return min;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public Particle3D() {
    this((int) 0L);
  }

  /**
   * Public constructor.
   */
  public Particle3D(final int initialCapatity) {
    count++;
    this.surfacePoints = new ListPoint3D(initialCapatity);
    this.innerPoints = new ListPoint3D(initialCapatity);
  }

  /**
   * Public constructor
   * @param pixelDepth The voxel Depth
   */
  public Particle3D(final float pixelDepth) {

    this();

    // this.pixelHeight = pixelHeight;
    // this.pixelWidth = pixelWidth;
    this.pixelDepth = pixelDepth;
  }

  /**
   * Public constructor
   * @param pixelDepth The voxel Depth
   * @param s String to parse
   */
  public Particle3D(final float pixelDepth, final String s) {

    this(pixelDepth);
    parse(s);
  }

  /**
   * Public constructor
   * @param s String to parse
   */
  public Particle3D(final String s) {

    this();
    parse(s);
  }

}
