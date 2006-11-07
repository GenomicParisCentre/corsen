import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DistanceCalculator {

  // private Particles3D mitoParticles;

  private Map<Particle3D, Particle3D[]> cuboidsMitos = new HashMap<Particle3D, Particle3D[]>();
  private Point3D pointToTest;
  private Point3D nearestPoint;
  private Point3D furthestPoint;
  private UpdateStatus updateStatus;
  private CorsenResult result;

  //
  // Getters
  //

  /**
   * Get the corsen result.
   * @return Returns the result
   */
  public CorsenResult getResult() {
    return result;
  }

  /**
   * Get the update status
   * @return Returns the updateStatus
   */
  public UpdateStatus getUpdateStatus() {
    return updateStatus;
  }

  //
  // Setters
  //

  /**
   * Set the update status
   * @param updateStatus The updateStatus to set
   */
  public void setUpdateStatus(UpdateStatus updateStatus) {
    this.updateStatus = updateStatus;
  }

  //
  // Other methods
  //

  private final static int[] keyParser(final String s) {

    String[] values = s.split("\\,");

    final int[] result = new int[3];
    result[0] = Integer.parseInt(values[0]);
    result[1] = Integer.parseInt(values[1]);
    result[2] = Integer.parseInt(values[2]);

    return result;
  }

  private static final String createKey(final int x, final int y, final int z) {

    return "" + x + "," + y + "," + z;
  }

  private static final boolean isFullCuboid(final Map cuboids, final int x,
      final int y, final int z) {

    final String key = createKey(x, y, z);

    if (!cuboids.containsKey(key))
      return false;

    Particle3D p = (Particle3D) cuboids.get(key);

    return p.innerPointsCount() == 8;
  }

  /**
   * Calc the cuboids from a particle. The cuboids can't contains more than 8
   * points in a cuboid.
   * @param particle Input partcile
   */
  private static Particle3D[] defineCuboids(final Particle3D particle,
      final float xlen, final float ylen, final float zlen) {

    if (particle == null)
      throw new NullPointerException("Particle is null");

    Map cuboids = Particle3DUtil.createCuboidToMap(particle, xlen * 2,
        ylen * 2, zlen * 2);

    Iterator it = cuboids.keySet().iterator();

    Set<Particle3D> toRemove = new HashSet<Particle3D>();

    while (it.hasNext()) {

      String key = (String) it.next();

      Particle3D p = (Particle3D) cuboids.get(key);

      final int count = p.innerPointsCount();

      if (count == 0)
        toRemove.add(p);
      else if (count == 8) {

        // test if all the nearst cuboids are full to remove this cuboid

        final int[] coords = keyParser(key);

        final int x = coords[0];
        final int y = coords[1];
        final int z = coords[2];

        if (isFullCuboid(cuboids, x - 1, y, z)
            && isFullCuboid(cuboids, x, y + 1, z)
            && isFullCuboid(cuboids, x, y - 1, z)
            && isFullCuboid(cuboids, x + 1, y, z)
            && isFullCuboid(cuboids, x, y, z + 1)
            && isFullCuboid(cuboids, x, y, z - 1))
          toRemove.add(p);

      }
    }

    it = toRemove.iterator();
    while (it.hasNext())
      cuboids.remove(it.next());

    final Particle3D[] result = new Particle3D[cuboids.size()];
    cuboids.values().toArray(result);

    return result;
  }

  /**
   * Calc the cuboids for all the mito particles.
   * @throws IOException
   */
  private void calcCuboids() {

    final Particles3D mitoParticles = getResult().getMitosParticles();

    if (mitoParticles == null)
      return;

    final float xlen = mitoParticles.getPixelWidth();
    final float ylen = mitoParticles.getPixelHeight();
    final float zlen = mitoParticles.getPixelDepth();

    ArrayList<Particle3D> al = new ArrayList<Particle3D>();

    final Particle3D[] mp = mitoParticles.getParticles();
    final int n = mp.length;

    for (int i = 0; i < n; i++) {

      final double p = (double) i / (double) n * 1000.0;
      sendEvent(ProgressEvent.PROGRESS_CALC_MITOS_CUBOIDS_EVENT, (int) p);

      final Particle3D mito = mp[i];

      final Particle3D[] cuboids = defineCuboids(mito, xlen, ylen, zlen);

      for (int j = 0; j < cuboids.length; j++) {
        al.add(cuboids[j]);
      }

      this.cuboidsMitos.put(mito, cuboids);
    }

    Particle3D[] mitoCuboids = new Particle3D[al.size()];

    al.toArray(mitoCuboids);

    getResult().setCuboidsMitosParticles(
        new Particles3D(mitoParticles, mitoCuboids));

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

    Point3D p = mito.getNearestInnerPoint(this.pointToTest);

    this.nearestPoint = p;

    return p.distance(this.pointToTest);
  }

  /**
   * Calc the maximal distance between the point and a mito (inner point), in
   * the case where the point is outside the mito.
   * @param mito The mito particle
   * @return the distance between the point and the mito
   */
  private double calcMaximalDistanceToPointNotIncludeInMito(
      final Particle3D mito) {

    Point3D p = mito.getFurthestInnerPoint(this.pointToTest);

    this.furthestPoint = p;

    return p.distance(this.pointToTest);
  }

  /**
   * Calc the minimal distance between the point and a mito (inner point), in
   * the case where the point is inside the mito.
   * @param mito The mito particle
   * @return the distance between the point and the mito
   */
  private double calcMinimalDistanceToPointIncludeInMito(final Particle3D mito) {

    Particle3D[] cuboids = this.cuboidsMitos.get(mito);

    double min = Double.MAX_VALUE;
    final Point3D p = this.pointToTest;

    for (int i = 0; i < cuboids.length; i++) {

      Point3D np = cuboids[i].getNearestInnerPoint(p);
      
      final double d = np.distance(p);

      if (d < min) {
        min = d;
        this.nearestPoint=np;
      }
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
   */
  public Distance minimalDistance(final Particle3D messenger) {

    if (messenger == null)
      throw new NullPointerException("Messenger is null");

    final Point3D barycenter = messenger.getBarycenter();

    if (barycenter == null)
      return null;

    this.pointToTest = barycenter;

    double min = Double.MAX_VALUE;
    Point3D p = null;

    // final Particles3D mitoParticles = getResult().getCuboidsMitosParticles();
    final Particles3D mitoParticles = getResult().getMitosParticles();

    final float xlen = mitoParticles.getPixelWidth();
    final float ylen = mitoParticles.getPixelHeight();
    final float zlen = mitoParticles.getPixelDepth();

    final int n = mitoParticles.getParticlesNumber();

    Particle3D nearestMito = null;

    for (int i = 0; i < n; i++) {

      final Particle3D mito = mitoParticles.getParticle(i);

      if (isPointInMito(mito, xlen, ylen, zlen)) {
        min = -calcMinimalDistanceToPointIncludeInMito(mito);
        nearestMito = mito;
        p = this.nearestPoint;
        break;
      }

      double d = calcMinimalDistanceToPointNotIncludeInMito(mito);

      if (d < min) {
        min = d;
        p = this.nearestPoint;
        nearestMito = mito;
      }

    }

    return new Distance(messenger, nearestMito, barycenter, p, (float) min);
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
  public Distance maximalDistance(final Particle3D messenger) {

    if (messenger == null)
      throw new NullPointerException("Messenger is null");

    final Point3D barycenter = messenger.getBarycenter();

    if (barycenter == null)
      return null;

    this.pointToTest = barycenter;

    final Particles3D mitoParticles = getResult().getMitosParticles();

    double max = Double.MIN_VALUE;
    final int n = mitoParticles.getParticlesNumber();

    Particle3D furthestMito = null;
    Point3D p = null;

    for (int i = 0; i < n; i++) {

      Particle3D mito = mitoParticles.getParticle(i);

      double d = calcMaximalDistanceToPointNotIncludeInMito(mito);

      if (d > max) {
        max = d;
        furthestMito = mito;
        p = this.furthestPoint;
      }

    }

    return new Distance(messenger, furthestMito, barycenter, p, (float) max);
  }

  private void sendEvent(final int id, final int value1) {

    if (this.updateStatus == null)
      return;

    this.updateStatus.updateStatus(new ProgressEvent(id, value1));
  }

  //
  // Constructor
  //

  /**
   * Public constructor
   * @param mitos mito Particles
   * @throws IOException
   */
  public DistanceCalculator(final CorsenResult result) {

    this.result = result;

    calcCuboids();
  }

}
