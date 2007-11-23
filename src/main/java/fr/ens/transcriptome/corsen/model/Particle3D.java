package fr.ens.transcriptome.corsen.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.calc.ParticleType;
import fr.ens.transcriptome.corsen.util.MathUtil;

/**
 * This class define a Particle 3D.
 * @author Laurent Jourdren
 */
public final class Particle3D {

  private static int count;

  private final int id = count;
  private String name = "" + this.id;

  private final AbstractListPoint3D surfacePoints;
  private final AbstractListPoint3D innerPoints;

  private final AbstractListPoint3D unmodifiableSurfacePoints;
  private final AbstractListPoint3D unmodifiableInnerPoints;

  private float pixelWidth = 1.0f;
  private float pixelHeight = 1.0f;
  private float pixelDepth = 1.0f;

  private double volume;
  private long intensity;
  private double area;
  private double sphericity;

  private ParticleType type = ParticleType.UNDEFINED;

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

  /**
   * Get the pixelDepth.
   * @return Returns the pixelDepth
   */
  public float getPixelDepth() {
    return pixelDepth;
  }

  /**
   * Get the pixelHeight.
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
   * Get the type of the particle
   * @return Returns the type
   */
  public ParticleType getType() {
    return type;
  }

  /**
   * Get the area of the particle.
   * @return The area of the particle
   */
  public double getArea() {

    return this.area;
  }

  /**
   * Get the sphericty of the particle.
   * @return the sphericty of the particle
   */
  public double getSphericty() {

    return this.sphericity;
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
   * Set the type of the particle.
   * @param type The particle type to set
   */
  public void setType(ParticleType type) {
    this.type = type;
  }

  /**
   * Set the volume of the particle
   * @param volume of the particle
   */
  void setVolume(final double volume) {

    if (volume == -1)
      calcVolume();
    else
      this.volume = volume;
  }

  /**
   * Set the intensity of the particle
   * @param intensity of the particle
   */
  void setIntensity(final long intensity) {

    if (intensity == -1)
      calcIntensity();
    else
      this.intensity = intensity;
  }

  /**
   * Set the area of the particle
   * @param area Area of the particle
   */
  void setArea(final double area) {

    if (area == -1)
      calcArea();
    else
      this.area = area;
  }

  /**
   * Set the sphericity of the particle
   * @param sphericity Sphericity of the particle
   */
  void setSphericity(final double sphericity) {

    this.sphericity = sphericity;
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

    final Point3D p1 = getInnerPoints().getBarycenter();

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

    return p.getMinDistanceToInnerPoint(getInnerPoints().getBarycenter());
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

    final Point3D p1 = getInnerPoints().getCenter();
    final Point3D p2 = p.getInnerPoints().getCenter();

    return p1.distance(p2);
  }

  public static final Map getSurfacePointSlices(final Particle3D particle) {

    final Map<Float, Particle2D> slices = new HashMap<Float, Particle2D>();

    final int nPoints = particle.surfacePointsCount();

    for (int i = 0; i < nPoints; i++) {
      final Point3D p = particle.getSurfacePoint(i);
      final Float key = new Float(p.getZ());
      Particle2D par = slices.get(key);
      if (par == null) {
        par =
            new Particle2D(particle.getPixelWidth(), particle.getPixelHeight());
        slices.put(key, par);
      }
      par.addSurfacePoint(p.getX(), p.getY());
    }

    return slices;
  }

  private static Map<String, Particle2D> getSurfaceAndInnerPointSlices(
      final Particle3D particle) {

    final Map<String, Particle2D> slices = new HashMap<String, Particle2D>();

    final int nSurfacePoints = particle.surfacePointsCount();

    for (int i = 0; i < nSurfacePoints; i++) {
      final Point3D p = particle.getSurfacePoint(i);
      final String key = "" + p.getZ();
      Particle2D par = slices.get(key);
      if (par == null) {
        par =
            new Particle2D(particle.getPixelWidth(), particle.getPixelHeight());
        slices.put(key, par);
      }
      par.addSurfacePoint(p.getX(), p.getY());
    }

    final int nInnerPoints = particle.innerPointsCount();

    for (int i = 0; i < nInnerPoints; i++) {
      final Point3D p = particle.getInnerPoint(i);
      final String key = "" + p.getZ();
      Particle2D par = slices.get(key);
      if (par == null) {
        par =
            new Particle2D(particle.getPixelWidth(), particle.getPixelHeight());
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

  private float getMinDiffNotNull(final Set<Float> s) {

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

    Set<Float> setX = new HashSet<Float>();
    Set<Float> setY = new HashSet<Float>();
    Set<Float> setZ = new HashSet<Float>();

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

    final Map<String, Particle2D> slices = getSurfaceAndInnerPointSlices(this);

    if (slices == null || slices.size() == 0)
      return null;

    final int n = slices.size();

    final float[] keys = new float[n];

    int i = 0;
    for (String key : slices.keySet()) {

      keys[i] = Float.parseFloat(key);
      i++;
    }

    Arrays.sort(keys);

    final Particle2D[] result = new Particle2D[n];

    for (int j = 0; j < n; j++)
      result[j] = slices.get("" + keys[j]);

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

      if (p1 != null && p2 != null && p1.innerPointIntersect(p2))
        return true;

    }

    return false;
  }

  /**
   * Overide toString() method.
   * @return A string describing the point.
   */
  public String toString() {

    final StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append('\t');
    sb.append(getInnerPoints().getCenter());
    sb.append('\t');
    sb.append(getInnerPoints().getBarycenter());
    sb.append('\t');
    sb.append(getArea());
    sb.append('\t');
    sb.append(getVolume());
    sb.append('\t');
    sb.append(getSphericty());
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

  public AbstractListPoint3D getInnerPoints() {
    return this.unmodifiableInnerPoints;
  }

  public AbstractListPoint3D getSurfacePoints() {
    return this.unmodifiableSurfacePoints;
  }

  AbstractListPoint3D getModifiableInnerPoints() {
    return this.innerPoints;
  }

  AbstractListPoint3D getModifiableSurfacePoints() {
    return this.surfacePoints;
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

    if (p == null)
      return null;

    Distance d = getNearestInnerPointDistance(p);

    return d.getPointA();
  }

  /**
   * Find the nearst inner point of the particle from another point.
   * @param p Point to test.
   * @return the nearst point or null if there is no nearest point
   */
  public Distance getNearestInnerPointDistance(final Point3D p) {

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

    return new Distance(nearest, p, minDistance);
  }

  /**
   * Find the furthest inner point of the particle from another point.
   * @param p Point to test.
   * @return the furthest point or null if there is no nearest point
   */
  public Point3D getFurthestInnerPoint(final Point3D p) {

    if (p == null)
      return null;

    Distance d = getFurthestInnerPointDistance(p);

    return d.getPointA();
  }

  /**
   * Find the furthest inner point of the particle from another point.
   * @param p Point to test.
   * @return the furthest point or null if there is no nearest point
   */
  public Distance getFurthestInnerPointDistance(final Point3D p) {

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

    return new Distance(furthest, p, maxDistance);
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

  /**
   * Apply a factor to all values of the z coordinates.
   * @param zFactor factor to apply
   */
  public void applyZFactor(final float zFactor) {

    this.surfacePoints.applyZFactor(zFactor);
    this.innerPoints.applyZFactor(zFactor);
  }

  //
  // Calculate methods
  //

  /**
   * Calculate or recalculate the intensity of the particle.
   */
  public void calcIntensity() {

    long intensity = 0;
    for (Point3D p : this.innerPoints)
      intensity += p.getI();

    this.intensity = intensity;
  }

  /**
   * Calculate or recalculate the volume of the particle.
   */
  public void calcVolume() {

    this.volume =
        innerPointsCount()
            * getPixelWidth() * getPixelHeight() * getPixelDepth();
  }

  /**
   * Calculate or recalculate the area of the particle.
   */
  public void calcArea() {

    this.area = new PixelizedParticle3D(this).calcSurface();
  }

  /**
   * Calculate or recalculate the sphericty of the particle.
   */
  public void calcSphericity() {

    this.sphericity = MathUtil.sphericite2(getVolume(), getArea());
  }

  //
  // Constructor
  //

  /**
   * Public constructor
   * @param pixelDepth The voxel Depth
   */
  public Particle3D(final float pixelWidth, final float pixelHeight,
      final float pixelDepth) {

    count++;

    this.pixelWidth = pixelWidth;
    this.pixelHeight = pixelHeight;
    this.pixelDepth = pixelDepth;

    this.surfacePoints =
        new ArrayListPackedPoint3D(this.pixelWidth, this.pixelHeight, this.pixelDepth);
    this.innerPoints =
        new ArrayListPackedPoint3D(this.pixelWidth, this.pixelHeight, this.pixelDepth);

    this.unmodifiableSurfacePoints =
        new UnmodifiableListPoint3D(this.surfacePoints);
    this.unmodifiableInnerPoints =
        new UnmodifiableListPoint3D(this.innerPoints);
  }

}
