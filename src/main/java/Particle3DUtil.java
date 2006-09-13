import java.util.ArrayList;

public final class Particle3DUtil {

  private static final int INITIAL_CAPACITY_CUBOID = 48;
  
  /**
   * Get if exists the particle object generated from the intersection of the
   * cuboid with a particle 3D. In the new object, no surface point are set.
   * @param particle Particle to test
   * @param x0 X coordinate of the first point
   * @param y0 Y coordinate of the first point
   * @param z0 Z coordinate of the first point
   * @param xlen x length of the cuboid
   * @param ylen y length of the cuboid
   * @param zlen z length of the cuboid
   * @param prefix The name of the particle to create
   * @param initialCapacity Initial capacity of the result particle3D
   * @return a new particle with contains only the point of the original
   *         particle which are too in the cuboid
   */
  private static Particle3D createCuboid(final Particle3D particle,
      final float x0, final float y0, final float z0, final float xlen,
      final float ylen, final float zlen, final String prefix,
      final int initialCapacity) {

    final float x1, y1, z1;
    Particle3D result = null;

    x1 = x0 + xlen;
    y1 = y0 + ylen;
    z1 = z0 + zlen;

    final ListPoint3D points = particle.getInnerPoints();
    final int n = points.size();

    for (int i = 0; i < n; i++) {

      final float x = points.getXAt(i);
      final float y = points.getYAt(i);
      final float z = points.getZAt(i);

      if (x >= x0 && x < x1 && y >= y0 && y < y1 && z >= z0 && z < z1) {

        if (result == null) {
          result = new Particle3D(initialCapacity);
          result.setName(prefix + "-" + result.getId());
          result.addSurfacePoint(x0, y0, z0);
          result.addSurfacePoint(x1, y0, z0);
          result.addSurfacePoint(x1, y1, z0);
          result.addSurfacePoint(x0, y1, z0);
          result.addSurfacePoint(x0, y0, z1);
          result.addSurfacePoint(x1, y0, z1);
          result.addSurfacePoint(x1, y1, z1);
          result.addSurfacePoint(x0, y1, z1);
        }
        result.addInnerPoint(x, y, z, points.getIAt(i));

      }
    }

    if (result != null)
      result.setIntensityFromInnerPoints();

    return result;
  }

  /**
   * Create an arraylist of cuboids from a particle.
   * @param particle Input Particle
   * @param xlenght x length of cuboids 
   * @param ylenght y length of cuboids 
   * @param zlenght z length of cuboids 
   */
  public static ArrayList createCuboid(final Particle3D particle,
      final float xlenght, final float ylenght, final float zlenght) {

    if (particle == null)
      throw new NullPointerException("Particle is null");

    ArrayList cuboidArrayList = null;

    final ListPoint3D lp = particle.getInnerPoints();

    final float xMax = lp.getXMax() + 1.0f;
    final float yMax = lp.getYMax() + 1.0f;
    final float zMax = lp.getZMax() + 1.0f;
    final float xMin = (float) (Math.floor(lp.getXMin() / xlenght) * xlenght);
    final float yMin = (float) (Math.floor(lp.getYMin() / ylenght) * ylenght);
    final float zMin = (float) (Math.floor(lp.getZMin() / zlenght) * zlenght);

    for (float i = xMin; i < xMax; i += xlenght)
      for (float j = yMin; j < yMax; j += ylenght)
        for (float k = zMin; k < zMax; k += zlenght) {

          final Particle3D r = Particle3DUtil.createCuboid(particle, i, j, k,
              xlenght, ylenght, zlenght, "cuboid", INITIAL_CAPACITY_CUBOID);

          if (r != null) {
            if (cuboidArrayList == null)
              cuboidArrayList = new ArrayList();
            cuboidArrayList.add(r);
          }

        }

    return cuboidArrayList;
  }

  /**
   * Count the number of points in an array of particle3D/
   * @param particles Array of particles3D
   * @return the number of points in an array of particle3D
   */
  public static int countInnerPointsInParticles(final Particle3D[] particles) {

    if (particles == null)
      return 0;

    int count = 0;

    for (int i = 0; i < particles.length; i++)
      count += particles[i].innerPointsCount();

    return count;
  }

}
