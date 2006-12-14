/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen.calc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particle3DUtil;

final class CuboidUtil {

  public final static int[] keyParser(final String s) {

    String[] values = s.split("\\,");

    final int[] result = new int[3];
    result[0] = Integer.parseInt(values[0]);
    result[1] = Integer.parseInt(values[1]);
    result[2] = Integer.parseInt(values[2]);

    return result;
  }

  public static final String createKey(final int x, final int y, final int z) {

    return "" + x + "," + y + "," + z;
  }

  public static final boolean isFullCuboid(final Map cuboids, final int x,
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
  public static final List<Particle3D> defineCuboids(final Particle3D particle,
      final float xlen, final float ylen, final float zlen) {

    if (particle == null)
      throw new NullPointerException("Particle is null");

    Map<String, Particle3D> cuboids = Particle3DUtil.createCuboidToMap(
        particle, xlen * 2, ylen * 2, zlen * 2);

    Iterator it = cuboids.keySet().iterator();

    Set<Particle3D> toRemove = new HashSet<Particle3D>();

    while (it.hasNext()) {

      String key = (String) it.next();

      Particle3D p = cuboids.get(key);

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

    return new ArrayList<Particle3D>(cuboids.values());
    // final Particle3D[] result = new Particle3D[cuboids.size()];
    // cuboids.values().toArray(result);

    // return result;
  }

}
