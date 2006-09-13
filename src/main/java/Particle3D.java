import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class defin a Particle 3D.
 * @author Laurent Jourdren
 */
public final class Particle3D {

  private static int count;

  private final int id = count;
  private String name = "" + this.id;

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
  public double getVolume() {
    return this.volume;
  }

  /**
   * Get the id of the particle.
   * @return The id of the particle
   */
  public int getId() {
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
  public boolean containsInnerPoint(final Point3D p) {

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
  public boolean containsSurfacePoint(final Point3D p) {

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
  public void addSurfacePoint(final Point3D p) {

    addSurfacePoint(p, false);
  }

  /**
   * Add a surface point to the particle.
   * @param p Point to add
   */
  public void addSurfacePoint(final Point3D p, final boolean testIfExists) {

    if (p == null)
      return;

    if (testIfExists && containsSurfacePoint(p))
      return;

    this.surfacePoints.add(p);
  }

  public void addSurfacePoint(final float x, final float y, final float z) {

    this.surfacePoints.add(x, y, z);
  }

  /**
   * Add an inner point to the particle.
   * @param p Point to add
   */
  public void addInnerPoint(final Point3D p) {

    addInnerPoint(p, false);
  }

  /**
   * Add an inner point to the particle.
   * @param p Point to add
   */
  public void addInnerPoint(final Point3D p, final boolean testIfExists) {

    if (p == null)
      return;

    if (testIfExists && containsInnerPoint(p))
      return;

    this.innerPoints.add(p);
  }

  public void addInnerPoint(final float x, final float y, final float z,
      final int i) {

    this.innerPoints.add(x, y, z, i);
  }

  /**
   * Add the points of a particle to this particle.
   * @param particle Particle to add
   */
  public void add(final Particle3D particle) {

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
  public int surfacePointsCount() {

    return this.surfacePoints.size();
  }

  /**
   * Get the number of surfacePoints in the particle.
   * @return The number of point in the particle
   */
  public int innerPointsCount() {

    return this.innerPoints.size();
  }

  /**
   * Get the surface point at index.
   * @param index Index of the point
   * @return The point at the index if exists.
   */
  public Point3D getSurfacePoint(final int index) {

    return this.surfacePoints.get(index);
  }

  /**
   * Get the inner point at index.
   * @param index Index of the point
   * @return The point at the index if exists.
   */
  public Point3D getInnerPoint(final int index) {

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
      final Point2D pt = particle.getSurfacePoint(i);
      addSurfacePoint(pt.getX(), pt.getY(), slice * this.pixelDepth);
    }

    n = particle.innerPointsCount();
    this.innerPoints.ensureCapacity(n);

    for (int i = 0; i < n; i++) {
      final Point2D pt = particle.getInnerPoint(i);
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

        final double d = p1.distance(p2);

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

      final double d = p1.distance(p2);
      if (d < min)
        min = d;
    }

    return min;
  }

  /**
   * Calc the minimal distance between a point and an inner point of the
   * particle.
   * @param p Point to test
   * @return the minimal distance between a point and an inner point of the
   *         particle
   */
  public double getMinDistanceToInnerPoint(final Point3D p) {

    double min = Double.MAX_VALUE;

    if (p == null)
      throw new NullPointerException("Point is null");

    final int n = innerPointsCount();

    for (int i = 0; i < n; i++) {

      final Point3D p2 = getInnerPoint(i);

      final double d = p.distance(p2);
      if (d < min)
        min = d;
    }

    return min;
  }

  /**
   * Calc the maximal distance between a point and an inner point of the
   * particle.
   * @param p Point to test
   * @return the maximal distance between a point and an inner point of the
   *         particle
   */
  public double getMaxDistanceToInnerPoint(final Point3D p) {

    double max = Double.MIN_VALUE;

    if (p == null)
      throw new NullPointerException("Point is null");

    final int n = innerPointsCount();

    for (int i = 0; i < n; i++) {

      final Point3D p2 = getInnerPoint(i);

      final double d = p.distance(p2);
      if (d > max)
        max = d;
    }

    return max;
  }

  /**
   * Calc the distance between 2 objects from the center to the surface of the
   * second object.
   * @param p Particle to get the distance
   * @return The distance between the 2 objects
   */
  public double getBarycenterToInnerDistance(final Particle3D p) {

    if (p == null)
      throw new NullPointerException("Particle is null");

    return p.getMinDistanceToInnerPoint(getBarycenter());
  }

  /**
   * Calc the distance between 2 objects from the center of the objects.
   * @param p Particle to get the distance
   * @return The distance between the 2 objects
   */
  public double getCenterToCenterDistance(final Particle3D p) {

    final double min = Double.MAX_VALUE;

    if (p == null)
      return min;

    final Point3D p1 = getCenter();
    final Point3D p2 = p.getCenter();

    return p1.distance(p2);
  }

  public static final Map getSurfacePointSlices(final Particle3D particle) {

    final Map slices = new HashMap();

    final int nPoints = particle.surfacePointsCount();

    for (int i = 0; i < nPoints; i++) {
      final Point3D p = particle.getSurfacePoint(i);
      final Float key = new Float(p.getZ());
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

    final Map slices = new HashMap();

    final int nSurfacePoints = particle.surfacePointsCount();

    for (int i = 0; i < nSurfacePoints; i++) {
      final Point3D p = particle.getSurfacePoint(i);
      final String key = "" + p.getZ();
      Particle2D par = (Particle2D) slices.get(key);
      if (par == null) {
        par = new Particle2D();
        slices.put(key, par);
      }
      par.addSurfacePoint(p.getX(), p.getY());
    }

    final int nInnerPoints = particle.innerPointsCount();

    for (int i = 0; i < nInnerPoints; i++) {
      final Point3D p = particle.getInnerPoint(i);
      final String key = "" + p.getZ();
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
   * Get the minimal distances in the 3 axes between the point in the particle.
   * @return An array of float with the 3 minimal distances (minX,minY and minZ)
   */
  public float[] getMinInnerDistancesNotNull0() {

    final int nInnerPoints = innerPointsCount();

    float minX = Float.MAX_VALUE;
    float minY = Float.MAX_VALUE;
    float minZ = Float.MAX_VALUE;

    for (int i = 0; i < nInnerPoints; i++) {

      final Point3D pt1 = getInnerPoint(i);

      for (int j = i + 1; j < nInnerPoints; j++) {

        final Point3D pt2 = getInnerPoint(j);

        float x = Math.abs(pt1.getX() - pt2.getX());
        float y = Math.abs(pt1.getY() - pt2.getY());
        float z = Math.abs(pt1.getZ() - pt2.getZ());

        if (x > 0.0f && x < minX)
          minX = x;
        if (x > 0.0f && y < minY)
          minY = y;
        if (x > 0.0f && z < minZ)
          minZ = z;

      }
    }

    return new float[] {minX, minY, minZ};
  }

  private float getMinDiffNotNull(final Set s) {

    if (s == null)
      throw new NullPointerException("The set is null ");

    if (s.size() == 1)
      return 1.0f;

    Float[] data = new Float[s.size()];
    s.toArray(data);

    float min = Float.MAX_VALUE;

    for (int i = 0; i < data.length; i++) {

      final float f1 = data[i].floatValue();

      for (int j = i + 1; j < data.length; j++) {

        float d = Math.abs(data[j].floatValue() - f1);

        if (d != 0.0f && d < min)
          min = d;
      }
    }

    return min;
  }

  /**
   * Get the minimal distances in the 3 axes between the point in the particle.
   * @return An array of float with the 3 minimal distances (minX,minY and minZ)
   */
  public float[] getMinInnerDistancesNotNull() {

    final int nInnerPoints = innerPointsCount();

    Set setX = new HashSet();
    Set setY = new HashSet();
    Set setZ = new HashSet();

    for (int i = 0; i < nInnerPoints; i++) {

      final Point3D pt1 = getInnerPoint(i);
      setX.add(new Float(pt1.getX()));
      setY.add(new Float(pt1.getY()));
      setZ.add(new Float(pt1.getZ()));
    }

    return new float[] {getMinDiffNotNull(setX), getMinDiffNotNull(setY),
        getMinDiffNotNull(setZ)};
  }

  /**
   * Transfort the particle in an array of particles2D.
   * @return An array of Particles 2D
   */
  public Particle2D[] toParticles2D() {

    final Map slices = getSurfaceAndInnerPointSlices(this);

    if (slices == null || slices.size() == 0)
      return null;

    final int n = slices.size();

    final float[] keys = new float[n];

    final Iterator it = slices.keySet().iterator();

    int i = 0;
    while (it.hasNext()) {

      keys[i] = Float.parseFloat((String) it.next());
      i++;
    }

    Arrays.sort(keys);

    final Particle2D[] result = new Particle2D[n];

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
   * @return true if there is an intersection
   */
  public boolean intersect(final Particle3D particle) {

    if (particle == null)
      return false;

    final Map slices = getSurfacePointSlices(this);
    final Map particlesSlices = getSurfacePointSlices(particle);

    final Iterator it = slices.keySet().iterator();
    while (it.hasNext()) {

      final Float z = (Float) it.next();

      final Particle2D p1 = (Particle2D) slices.get(z);
      final Particle2D p2 = (Particle2D) particlesSlices.get(z);

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

    final StringBuffer sb = new StringBuffer();
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

    final StringTokenizer st = new StringTokenizer(s, "\t");

    if (st.hasMoreElements())
      setName(st.nextToken());

    if (st.hasMoreElements())
      st.nextToken(); // center

    if (st.hasMoreElements())
      st.nextToken(); // barycenter

    if (st.hasMoreElements())
      this.volume = Double.parseDouble(st.nextToken());
    if (st.hasMoreElements())
      this.intensity = Long.parseLong(st.nextToken());

    final Set existingPoints = new HashSet();

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

  /**
   * Find the nearst inner point of the particle from another point.
   * @param p Point to test.
   * @return the nearst point or null if there is no nearest point
   */
  public Point3D getNearestInnerPoint(final Point3D p) {

    Point3D nearest = null;
    float minDistance = Float.MAX_VALUE;

    final int n = innerPointsCount();

    for (int j = 0; j < n; j++) {
      final Point3D p2 = getInnerPoint(j);

      if (nearest == null) {
        nearest = p2;
        minDistance = p2.distance(p);
      } else {

        float d = p2.distance(p);
        if (d < minDistance) {
          minDistance = d;
          nearest = p2;
        }

      }

    }

    return nearest;
  }

  /**
   * Find the furthest inner point of the particle from another point.
   * @param p Point to test.
   * @return the furthest point or null if there is no nearest point
   */
  public Point3D getFurthestInnerPoint(final Point3D p) {

    Point3D furthest = null;
    float maxDistance = Float.MIN_VALUE;

    final int n = innerPointsCount();

    for (int j = 0; j < n; j++) {
      final Point3D p2 = getInnerPoint(j);

      if (furthest == null) {
        furthest = p2;
        maxDistance = p2.distance(p);
      } else {

        float d = p2.distance(p);
        if (d > maxDistance) {
          maxDistance = d;
          furthest = p2;
        }

      }

    }

    return furthest;
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
   * @param initialCapatity Initial capacity of the list of points
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
