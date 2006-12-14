package fr.ens.transcriptome.corsen.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Particle3DUtil {

  private static final int INITIAL_CAPACITY_CUBOID = 48;

  private static final class MapCuboid extends
      HashMap<Integer, Map<Integer, Map<Integer, Particle3DBuilder>>> {

    /**
     * Put a builder in the map.
     * @param xMapIndex the x index of the map
     * @param yMapIndex the y index of the map
     * @param zMapIndex the z index of the map
     * @param particleBuilder builder to add
     */
    public void put(int xMapIndex, int yMapIndex, int zMapIndex,
        Particle3DBuilder particleBuilder) {

      Map<Integer, Map<Integer, Particle3DBuilder>> mapY = get(xMapIndex);

      if (mapY == null) {
        mapY = new HashMap<Integer, Map<Integer, Particle3DBuilder>>();
        put(xMapIndex, mapY);
      }

      Map<Integer, Particle3DBuilder> mapZ = mapY.get(yMapIndex);

      if (mapZ == null) {
        mapZ = new HashMap<Integer, Particle3DBuilder>();
        mapY.put(yMapIndex, mapZ);
      }

      mapZ.put(zMapIndex, particleBuilder);
    }

    /**
     * Get a builder from the map.
     * @param xMapIndex the x index of the map
     * @param yMapIndex the y index of the map
     * @param zMapIndex the z index of the map
     * @return a particle builder
     */
    public Particle3DBuilder get(int xMapIndex, int yMapIndex, int zMapIndex) {

      final Map<Integer, Map<Integer, Particle3DBuilder>> mapY = this
          .get(xMapIndex);
      final Map<Integer, Particle3DBuilder> mapZ = mapY.get(yMapIndex);

      return mapZ.get(zMapIndex);
    }

  }

  private static MapCuboid initCuboids(final Particle3D particle,
      final float xlenght, final float ylenght, final float zlenght) {

    final ListPoint3D lp = particle.getInnerPoints();

    final MapCuboid mapCuboid = new Particle3DUtil.MapCuboid();

    final double xMax = lp.getXMax() + xlenght;
    final double yMax = lp.getYMax() + ylenght;
    final double zMax = lp.getZMax() + zlenght;

    final double xMin = Math.floor(lp.getXMin() / xlenght) * xlenght;

    final double yMin = (float) (Math.floor(lp.getYMin() / ylenght) * ylenght);
    final double zMin = (float) (Math.floor(lp.getZMin() / zlenght) * zlenght);

    for (double i = xMin; i < xMax; i += xlenght)
      for (double j = yMin; j < yMax; j += ylenght)
        for (double k = zMin; k < zMax; k += zlenght) {

          final int x = (int) (i / xlenght);
          final int y = (int) (j / ylenght);
          final int z = (int) (k / zlenght);

          final double x1, y1, z1;
          x1 = i + xlenght;
          y1 = j + ylenght;
          z1 = k + zlenght;

          final Particle3DBuilder builder = new Particle3DBuilder(particle
              .getPixelWidth(), particle.getPixelHeight(), particle
              .getPixelDepth());

          builder.setName(particle.getName() + "-" + builder.getId());
          addSurfacePoint(builder, i, j, k);
          addSurfacePoint(builder, x1, j, k);
          addSurfacePoint(builder, x1, y1, k);
          addSurfacePoint(builder, i, y1, k);
          addSurfacePoint(builder, i, j, z1);
          addSurfacePoint(builder, x1, j, z1);
          addSurfacePoint(builder, x1, y1, z1);
          addSurfacePoint(builder, i, y1, z1);

          mapCuboid.put(x, y, z, builder);
        }

    return mapCuboid;
  }

  public static void addSurfacePoint(final Particle3DBuilder builder,
      final double x, final double y, final double z) {

    builder.addSurfacePoint((float) x, (float) y, (float) z);
  }

  private static void fillCuboids(final Particle3D particle,
      final MapCuboid mapCuboids, final float xlenght, final float ylenght,
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

      // final Particle3D p = (Particle3D) mapZ.get(zMapIndex);

      Particle3DBuilder builder = mapCuboids.get(xMapIndex, yMapIndex,
          zMapIndex);

      builder.addInnerPoint(x, y, z, points.getIAt(i));
      // p.addInnerPoint(x, y, z, points.getIAt(i));
    }

  }

  /**
   * Create an arraylist of cuboids from a particle.
   * @param particle Input Particle
   * @param xlenght x length of cuboids
   * @param ylenght y length of cuboids
   * @param zlenght z length of cuboids
   */
  public static List<Particle3D> createCuboidToArrayList(
      final Particle3D particle, final float xlenght, final float ylenght,
      final float zlenght) {

    if (particle == null)
      throw new NullPointerException("Particle is null");

    final MapCuboid mapCuboids = initCuboids(particle, xlenght, ylenght,
        zlenght);

    fillCuboids(particle, mapCuboids, xlenght, ylenght, zlenght);

    List<Particle3D> cuboidArrayList = null;

    Iterator<Integer> mapXIt = mapCuboids.keySet().iterator();

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
          Particle3DBuilder builder = (Particle3DBuilder) mapZ.get(keyZ);

          if (builder.getInnerPointCount() > 0) {
            if (cuboidArrayList == null)
              cuboidArrayList = new ArrayList<Particle3D>();
            cuboidArrayList.add(builder.getParticle());
          }
        }
      }
    }

    return cuboidArrayList;
  }

  /**
   * Create an arraylist of cuboids from a particle.
   * @param particle Input Particle
   * @param xlenght x length of cuboids
   * @param ylenght y length of cuboids
   * @param zlenght z length of cuboids
   */
  public static Map<String, Particle3D> createCuboidToMap(
      final Particle3D particle, final float xlenght, final float ylenght,
      final float zlenght) {

    if (particle == null)
      throw new NullPointerException("Particle is null");

    final MapCuboid mapCuboids = initCuboids(particle, xlenght, ylenght,
        zlenght);
    fillCuboids(particle, mapCuboids, xlenght, ylenght, zlenght);

    Map<String, Particle3D> result = null;

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
          Particle3D p = ((Particle3DBuilder) mapZ.get(keyZ)).getParticle();

          if (p.innerPointsCount() > 0) {
            if (result == null)
              result = new HashMap<String, Particle3D>();
            result.put("" + keyX + "," + keyY + "," + keyZ, p);
          }
        }
      }
    }

    return result;
  }

  /**
   * Count the number of points in an array of particle3D/
   * @param particles Array of particles3D
   * @return the number of points in an array of particle3D
   */
  public static int countInnerPointsInParticles(final List<Particle3D> particles) {

    if (particles == null)
      return 0;

    int count = 0;

    for (Particle3D par : particles)
      count += par.innerPointsCount();

    return count;
  }

}
