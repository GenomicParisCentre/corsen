import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DistanceCalculator {

  private Particles3D mitoParticles;

  private Map cuboidsMitos = new HashMap();
  private Point3D pointToTest;
  private File fileRGLMitoCuboids;
  private RGL rglDistances;
  private Point3D nearestPoint;
  private String colorDistanceInside = "deeppink";
  private String colorDistanceOutside = "cyan";

  //
  // Getters
  //

  /**
   * Get the mito particles
   * @return Returns the mitoParticles
   */
  public Particles3D getMitoParticles() {
    return mitoParticles;
  }

  //
  // Other methods
  //

  /**
   * Calc the cuboids from a particle. The cuboids can't contains more than 4
   * points in a cuboid.
   * @param particle Input partcile
   */
  private static Particle3D[] defineCuboids(final Particle3D particle,
      final float xlen, final float ylen, final float zlen) {

    if (particle == null)
      throw new NullPointerException("Particle is null");

    ArrayList cuboids = Particle3DUtil.createCuboid(particle, xlen * 2,
        ylen * 2, zlen * 2);

    Iterator it = cuboids.iterator();

    Set toRemove = new HashSet();

    while (it.hasNext()) {

      Particle3D p = (Particle3D) it.next();

      final int count = p.innerPointsCount();

      if (count == 0 || count == 8)
        toRemove.add(p);
    }

    it = toRemove.iterator();
    while (it.hasNext())
      cuboids.remove(it.next());

    final Particle3D[] result = new Particle3D[cuboids.size()];
    cuboids.toArray(result);

    return result;
  }

  /**
   * Calc the cuboids for all the mito particles.
   * @throws IOException
   */
  private void calcCuboids() throws IOException {

    if (this.mitoParticles == null)
      return;

    final float xlen = this.mitoParticles.getPixelWidth();
    final float ylen = this.mitoParticles.getPixelHeight();
    final float zlen = this.mitoParticles.getPixelDepth();

    ArrayList al = new ArrayList();

    final Particle3D[] mp = this.mitoParticles.getParticles();

    for (int i = 0; i < mp.length; i++) {

      final Particle3D mito = mp[i];

      final Particle3D[] cuboids = defineCuboids(mito, xlen, ylen, zlen);

      for (int j = 0; j < cuboids.length; j++) {
        al.add(cuboids[j]);
      }

      this.cuboidsMitos.put(mito, cuboids);
    }

    Particle3D[] mitoCuboids = new Particle3D[al.size()];

    al.toArray(mitoCuboids);

    // Write the mito cuboids

    if (this.fileRGLMitoCuboids != null) {
      RGL rgl = new RGL(this.fileRGLMitoCuboids);
      rgl.writeRPlots(mitoCuboids, "red", false, 1.0f);
      rgl.close();
    }

  }

  /**
   * Test if a point is include in the space of a mito particle.
   * @param mito The mito particle
   * @param xlen x min length
   * @param ylen y min length
   * @param zlen z min length
   * @return true if the point is include in the mito
   */
  private boolean isPointInMito(final Particle3D mito, final float xlen,
      final float ylen, final float zlen) {

    if (mito == null)
      throw new NullPointerException("The particle is null");
    if (this.pointToTest == null)
      throw new NullPointerException("The point to test is null is null");

    Point3D nearest = mito.getNearestInnerPoint(this.pointToTest);

    final float radius = (float) Math.sqrt(xlen * xlen + ylen * ylen + zlen
        * zlen);

    if (nearest == null || nearest.distance(this.pointToTest) > radius)
      return false;

    int count = 0;
    final int n = mito.innerPointsCount();

    for (int i = 0; i < n; i++) {

      Point3D p = mito.getInnerPoint(i);

      if (this.pointToTest.isNear(p, xlen, ylen, zlen))
        count++;
    }

    // if (count > 3)
    if (count > 1)
      return true;

    return false;
  }

  /**
   * Calc the minimal distance between the point and a mito (inner point), in
   * the case where the point is outside the mito.
   * @param mito The mito particle
   * @return the distance between the point and the mito
   */
  private double calcMinimalDistanceToPointNotIncludeInMito(
      final Particle3D mito) {

    if (this.rglDistances != null) {

      Point3D p = mito.getNearestInnerPoint(this.pointToTest);

      this.nearestPoint = p;

      return p.distance(this.pointToTest);
    }

    return mito.getMinDistanceToInnerPoint(this.pointToTest);
  }

  /**
   * Calc the maximal distance between the point and a mito (inner point), in
   * the case where the point is outside the mito.
   * @param mito The mito particle
   * @return the distance between the point and the mito
   */
  private double calcMaximalDistanceToPointNotIncludeInMito(
      final Particle3D mito) {

    return mito.getMaxDistanceToInnerPoint(this.pointToTest);
  }

  /**
   * Calc the minimal distance between the point and a mito (inner point), in
   * the case where the point is inside the mito.
   * @param mito The mito particle
   * @return the distance between the point and the mito
   * @throws IOException
   */
  private double calcMinimalDistanceToPointIncludeInMito(final Particle3D mito)
      throws IOException {

    Particle3D[] cuboids = (Particle3D[]) this.cuboidsMitos.get(mito);

    double min = Double.MAX_VALUE;
    final Point3D p = this.pointToTest;
    Point3D p2 = null;

    final boolean output = this.rglDistances != null;

    for (int i = 0; i < cuboids.length; i++) {

      if (output) {

        Point3D p3 = cuboids[i].getNearestInnerPoint(p);
        final double d = p.distance(p3);

        if (d < min) {
          min = d;
          p2 = p3;
        }

      } else {
        final double d = cuboids[i].getMinDistanceToInnerPoint(p);

        if (d < min) {
          min = d;
        }
      }

    }

    if (output) {
      this.rglDistances.writeLine(p, p2, this.colorDistanceInside);
    }

    return min;
  }

  /**
   * Calc the minimal distance between a messenger and the surface of
   * mitocondria.
   * @param messenger Messenger to test
   * @param messenger
   * @return the best distance between a messenger and a mitocondria. This
   *         distance can be negative
   * @throws IOException
   */
  public double minimalDistance(final Particle3D messenger) throws IOException {

    if (messenger == null)
      throw new NullPointerException("Messenger is null");

    final Point3D barycenter = messenger.getBarycenter();

    if (barycenter == null)
      return Double.NaN;

    this.pointToTest = barycenter;

    double min = Double.MAX_VALUE;
    Point3D p = null;

    final float xlen = this.mitoParticles.getPixelWidth();
    final float ylen = this.mitoParticles.getPixelHeight();
    final float zlen = this.mitoParticles.getPixelDepth();

    final int n = this.mitoParticles.getParticlesNumber();

    for (int i = 0; i < n; i++) {

      Particle3D mito = this.mitoParticles.getParticle(i);

      if (isPointInMito(mito, xlen, ylen, zlen))
        return -calcMinimalDistanceToPointIncludeInMito(mito);

      double d = calcMinimalDistanceToPointNotIncludeInMito(mito);

      if (d < min) {
        min = d;
        p = this.nearestPoint;
      }

    }

    if (this.rglDistances != null)
      this.rglDistances.writeLine(this.pointToTest, p,
          this.colorDistanceOutside);

    return min;
  }

  /**
   * Calc the maximal distance between a messenger and the surface of
   * mitocondria.
   * @param messenger Messenger to test
   * @param messenger
   * @return the best distance between a messenger and a mitocondria. This
   *         distance can be negative
   * @throws IOException
   */
  public double maximalDistance(final Particle3D messenger) throws IOException {

    if (messenger == null)
      throw new NullPointerException("Messenger is null");

    final Point3D barycenter = messenger.getBarycenter();

    if (barycenter == null)
      return Double.NaN;

    this.pointToTest = barycenter;

    double max = Double.MIN_VALUE;
    final int n = this.mitoParticles.getParticlesNumber();

    for (int i = 0; i < n; i++) {

      Particle3D mito = this.mitoParticles.getParticle(i);

      double d = calcMaximalDistanceToPointNotIncludeInMito(mito);

      if (d > max)
        max = d;

    }

    return max;

  }

  public void closeRGLDistances() throws IOException {

    this.rglDistances.close();
  }

  //
  // Constructor
  //

  /**
   * Public constructor
   * @param mitos mito Particles
   * @throws IOException
   */
  public DistanceCalculator(final Particles3D mitos,
      final File fileRGLDistances, final File fileRGLMitoCuboids)
      throws IOException {

    this.mitoParticles = mitos;
    this.fileRGLMitoCuboids = fileRGLMitoCuboids;

    if (fileRGLDistances != null) {

      this.rglDistances = new RGL(fileRGLDistances);

    }

    // calcMitoMinDistances();
    calcCuboids();
  }

}
