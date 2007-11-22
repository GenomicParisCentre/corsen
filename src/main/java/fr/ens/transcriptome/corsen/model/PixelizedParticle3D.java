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
 * of the �cole Normale Sup�rieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen.model;

import fr.ens.transcriptome.corsen.util.MathUtil;

/**
 * This class define a Particle3D without using vector coordinates.
 * @author Laurent Jourdren
 */
public class PixelizedParticle3D {

  private float x0, y0, z0;
  private float pixelWidth, pixelHeight, pixelDepth;
  private int xLen, yLen, zLen;
  private byte[][][] array;

  private void initArray(final AbstractListPoint3D lp) {

    final float xMin = lp.getXMin();
    final float yMin = lp.getYMin();
    final float zMin = lp.getZMin();

    final float xMax = lp.getXMax();
    final float yMax = lp.getYMax();
    final float zMax = lp.getZMax();

    this.x0 = (float) Math.floor(MathUtil.roundValue(xMin / this.pixelWidth));
    this.y0 = (float) Math.floor(MathUtil.roundValue(yMin / this.pixelHeight));
    this.z0 = (float) Math.floor(MathUtil.roundValue(zMin / this.pixelDepth));

    final int xLen =
        (int) MathUtil.roundValue((xMax - xMin) / this.pixelWidth) + 1;
    final int yLen =
        (int) MathUtil.roundValue((yMax - yMin) / this.pixelHeight) + 1;
    final int zLen =
        (int) MathUtil.roundValue((zMax - zMin) / this.pixelDepth) + 1;

    this.xLen = xLen;
    this.yLen = yLen;
    this.zLen = zLen;

    this.array = new byte[zLen][][];

    for (int i = 0; i < array.length; i++) {

      array[i] = new byte[yLen][];
      for (int j = 0; j < yLen; j++)
        array[i][j] = new byte[xLen];
    }

  }

  private void fillArray(final AbstractListPoint3D lp) {

    final float x0 = this.x0;
    final float y0 = this.y0;
    final float z0 = this.z0;

    for (Point3D p : lp) {

      final int dx =
          (int) Math.floor(MathUtil.roundValue((p.getX() / this.pixelWidth)
              - x0));
      final int dy =
          (int) Math.floor(MathUtil.roundValue((p.getY() / this.pixelHeight)
              - y0));
      final int dz =
          (int) Math.floor(MathUtil.roundValue((p.getZ() / this.pixelDepth)
              - z0));

      this.array[dz][dy][dx] = 1;
    }
  }

  public byte getPixel(final int x, final int y, final int z) {

    if (x < 0 || y < 0 || z < 0 || x >= xLen || y >= yLen || z >= zLen)
      return 0;

    return this.array[z][y][x];
  }

  private int[] countAxesSurfaces(final int x, final int y, final int z) {

    final int xCount = 2 - getPixel(x - 1, y, z) - getPixel(x + 1, y, z);
    final int yCount = 2 - getPixel(x, y - 1, z) - getPixel(x, y + 1, z);
    final int zCount = 2 - getPixel(x, y, z - 1) - getPixel(x, y, z + 1);

    return new int[] {xCount, yCount, zCount};
  }

  /**
   * Calc the surface of the particle.
   * @return the surface of the particle
   */
  public double calcSurface() {

    final int xLen = this.xLen;
    final int yLen = this.yLen;
    final int zLen = this.zLen;

    final double xSurface = pixelHeight * this.pixelDepth;
    final double ySurface = pixelWidth * this.pixelDepth;
    final double zSurface = pixelWidth * this.pixelHeight;

    double surface = 0;

    for (int i = 0; i < xLen; i++)
      for (int j = 0; j < yLen; j++)
        for (int k = 0; k < zLen; k++) {

          if (getPixel(i, j, k) == 0)
            continue;

          final int[] faces = countAxesSurfaces(i, j, k);

          if (faces[0] == 0 && faces[1] == 0 && faces[2] == 0)
            continue;

          surface +=
              faces[0] * xSurface + faces[1] * ySurface + faces[2] * zSurface;

        }

    return surface;
  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   * @param particle Particle3D to "pixelised"
   */
  public PixelizedParticle3D(final Particle3D particle) {

    this(particle, particle.getPixelWidth(), particle.getPixelHeight(),
        particle.getPixelDepth());
  }

  /**
   * Public constructor.
   * @param particle Particle3D to "pixelised"
   * @param pixelWidth pixel width
   * @param pixelWidth pixel height
   * @param pixelDepth pixel depth
   */
  public PixelizedParticle3D(final Particle3D particle, final float pixelWidth,
      final float pixelHeight, final float pixelDepth) {

    this.pixelWidth = pixelWidth;
    this.pixelHeight = pixelHeight;
    this.pixelDepth = pixelDepth;

    initArray(particle.getInnerPoints());
    fillArray(particle.getInnerPoints());
  }

}
