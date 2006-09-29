import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Particle3DUtil {

  private static final int INITIAL_CAPACITY_CUBOID = 48;

  private static Map initCuboids(final Particle3D particle,
      final float xlenght, final float ylenght, final float zlenght) {

    final ListPoint3D lp = particle.getInnerPoints();

    final Map mapX = new HashMap();

    final double xMax = lp.getXMax() + xlenght;
    final double yMax = lp.getYMax() + ylenght;
    final double zMax = lp.getZMax() + zlenght;

    final double xMin = Math.floor(lp.getXMin() / xlenght) * xlenght;

    final double yMin = (float) (Math.floor(lp.getYMin() / ylenght) * ylenght);
    final double zMin = (float) (Math.floor(lp.getZMin() / zlenght) * zlenght);

    for (double i = xMin; i < xMax; i += xlenght)
      for (double j = yMin; j < yMax; j += ylenght)
        for (double k = zMin; k < zMax; k += zlenght) {

          final Integer x = new Integer((int) (i / xlenght));
          final Integer y = new Integer((int) (j / ylenght));
          final Integer z = new Integer((int) (k / zlenght));

          Map mapY = (Map) mapX.get(x);

          if (mapY == null) {
            mapY = new HashMap();
            mapX.put(x, mapY);
          }

          Map mapZ = (Map) mapY.get(y);

          if (mapZ == null) {
            mapZ = new HashMap();
            mapY.put(y, mapZ);
          }

          final double x1, y1, z1;
          x1 = i + xlenght;
          y1 = j + ylenght;
          z1 = k + zlenght;

          final Particle3D p = new Particle3D(INITIAL_CAPACITY_CUBOID);
          // p.setName(prefix + "-" + result.getId());
          addSurfacePoint(p, i, j, k);
          addSurfacePoint(p, x1, j, k);
          addSurfacePoint(p, x1, y1, k);
          addSurfacePoint(p, i, y1, k);
          addSurfacePoint(p, i, j, z1);
          addSurfacePoint(p, x1, j, z1);
          addSurfacePoint(p, x1, y1, z1);
          addSurfacePoint(p, i, y1, z1);

          mapZ.put(z, p);
        }

    return mapX;
  }

  public static void addSurfacePoint(final Particle3D p, final double x,
      final double y, final double z) {

    p.addSurfacePoint((float) x, (float) y, (float) z);
  }

  private static void fillCuboids(final Particle3D particle,
      final Map mapCuboids, final float xlenght, final float ylenght,
      final float zlenght) {

    final ListPoint3D points = particle.getInnerPoints();
    final int n = points.size();

    for (int i = 0; i < n; i++) {

      final float x = points.getXAt(i);
      final float y = points.getYAt(i);
      final float z = points.getZAt(i);

      final Integer xMapIndex = new Integer((int) (x / xlenght));
      final Integer yMapIndex = new Integer((int) (y / ylenght));
      final Integer zMapIndex = new Integer((int) (z / zlenght));

      final Map mapY = (Map) mapCuboids.get(xMapIndex);
      final Map mapZ = (Map) mapY.get(yMapIndex);

      final Particle3D p = (Particle3D) mapZ.get(zMapIndex);
      p.addInnerPoint(x, y, z, points.getIAt(i));

    }

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

    System.out.println("CREATE CUBOID");

    Map mapCuboids = initCuboids(particle, xlenght, ylenght, zlenght);
    fillCuboids(particle, mapCuboids, xlenght, ylenght, zlenght);

    ArrayList cuboidArrayList = null;

    Iterator mapXIt = mapCuboids.keySet().iterator();

    while (mapXIt.hasNext()) {

      Object keyX = mapXIt.next();
      Map mapY = (Map) mapCuboids.get(keyX);
      Iterator mapYIt = mapY.keySet().iterator();

      while (mapYIt.hasNext()) {

        Object keyY = mapYIt.next();
        Map mapZ = (Map) mapY.get(keyY);
        Iterator mapZIt = mapZ.keySet().iterator();

        while (mapZIt.hasNext()) {

          Object keyZ = mapZIt.next();
          Particle3D p = (Particle3D) mapZ.get(keyZ);

          if (p.innerPointsCount() > 0) {
            if (cuboidArrayList == null)
              cuboidArrayList = new ArrayList();
            cuboidArrayList.add(p);
          }
        }
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
