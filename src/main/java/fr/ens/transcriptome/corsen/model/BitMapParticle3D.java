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

import org.apache.commons.math.stat.descriptive.rank.Median;

import fr.ens.transcriptome.corsen.util.MathUtil;

/**
 * This class define a Particle3D without using vector coordinates.
 * @author Laurent Jourdren
 */
public final class BitMapParticle3D {

  private static final byte NO_POINT = 0;
  private static final byte INNER_POINT = 1;
  private static final byte SURFACE_POINT = 2;
  private static final byte DISCOVERED_SURFACE_POINT = 3;

  private float x0, y0, z0;
  private float pixelWidth, pixelHeight, pixelDepth;
  private int xLen, yLen, zLen;
  private byte[][][] array;
  private int[][][] intensities;

  private Particle3D particle;

  /**
   * Get x0.
   * @return x0
   */
  public float getX0() {
    return x0;
  }

  /**
   * Get y0.
   * @return y0
   */
  public float getY0() {
    return y0;
  }

  /**
   * Get z0.
   * @return z0
   */
  public float getZ0() {
    return z0;
  }

  /**
   * Get pixel witdh
   * @return pixelWidth
   */
  public float getPixelWidth() {
    return pixelWidth;
  }

  /**
   * Get pixel height
   * @return pixelHeight
   */
  public float getPixelHeight() {
    return pixelHeight;
  }

  /**
   * Get pixel depth
   * @return pixelDepth
   */
  public float getPixelDepth() {
    return pixelDepth;
  }

  /**
   * Get xLen
   * @return
   */
  public int getXLen() {
    return xLen;
  }

  /**
   * Get yLen
   * @return
   */
  public int getYLen() {
    return yLen;
  }

  /**
   * Get zLen
   * @return
   */
  public int getZLen() {
    return zLen;
  }

  public int getXBitmapCoordinate(final float x) {

    return (int) Math.floor(MathUtil
        .roundValue((x / this.pixelWidth) - this.x0));
  }

  public int getYBitmapCoordinate(final float y) {

    return (int) Math.floor(MathUtil.roundValue((y / this.pixelHeight)
        - this.y0));
  }

  public int getZBitmapCoordinate(final float z) {

    return (int) Math.floor(MathUtil
        .roundValue((z / this.pixelDepth) - this.z0));
  }

  private void setBitMapIntensity(final float x, final float y, final float z,
      final int intensity) {

    final int dx = getXBitmapCoordinate(x);
    final int dy = getYBitmapCoordinate(y);
    final int dz = getZBitmapCoordinate(z);

    if (dx < 0
        || dx >= this.xLen || dy < 0 || dy >= this.yLen || dz < 0
        || dz >= this.zLen)
      return;

    this.intensities[dz][dy][dx] += intensity;
  }

  private void setBitMapValue(final float x, final float y, final float z,
      final byte value) {

    final int dx = getXBitmapCoordinate(x);
    final int dy = getYBitmapCoordinate(y);
    final int dz = getZBitmapCoordinate(z);

    if (dx < 0
        || dx >= this.xLen || dy < 0 || dy >= this.yLen || dz < 0
        || dz >= this.zLen)
      return;

    this.array[dz][dy][dx] = value;
  }

  private byte getBitMapValue(final float x, final float y, final float z) {

    final int dx = getXBitmapCoordinate(x);
    final int dy = getYBitmapCoordinate(y);
    final int dz = getZBitmapCoordinate(z);

    return getBitMapValue(dx, dy, dz);
  }

  private final byte getBitMapValue(final int x, final int y, final int z) {

    if (x < 0
        || x >= this.xLen || y < 0 || y >= this.yLen || z < 0 || z >= this.zLen)
      return NO_POINT;

    return this.array[z][y][x];
  }

  public int getBitMapIntensity(final float x, final float y, final float z) {

    final int dx = getXBitmapCoordinate(x);
    final int dy = getYBitmapCoordinate(y);
    final int dz = getZBitmapCoordinate(z);

    return getBitMapIntensity(dx, dy, dz);
  }

  public final int getBitMapIntensity(final int x, final int y, final int z) {

    if (x < 0
        || x >= this.xLen || y < 0 || y >= this.yLen || z < 0 || z >= this.zLen)
      return NO_POINT;

    return this.intensities[z][y][x];
  }

  // private byte getPixel(final int x, final int y, final int z) {
  //
  // if (x < 0 || y < 0 || z < 0 || x >= xLen || y >= yLen || z >= zLen)
  // return 0;
  //
  // if (this.array[z][y][x]!=0) System.out.println("val="+this.array[z][y][x]);
  //    
  // return this.array[z][y][x];
  // }

  private void initArray(final AbstractListPoint3D lp) {

    // new Exception().printStackTrace();

    final float xMin = lp.getXMin();
    final float yMin = lp.getYMin();
    final float zMin = lp.getZMin();

    final float xMax = lp.getXMax();
    final float yMax = lp.getYMax();
    final float zMax = lp.getZMax();

    this.x0 =
        (float) Math.floor(MathUtil.roundValue((xMin - 2 * this.pixelWidth)
            / this.pixelWidth));
    this.y0 =
        (float) Math.floor(MathUtil.roundValue((yMin - 2 * this.pixelHeight)
            / this.pixelHeight));
    this.z0 =
        (float) Math.floor(MathUtil.roundValue((zMin - 2 * this.pixelDepth)
            / this.pixelDepth));

    final int xLen =
        (int) MathUtil.roundValue((xMax - xMin) / this.pixelWidth) + 5;
    final int yLen =
        (int) MathUtil.roundValue((yMax - yMin) / this.pixelHeight) + 5;
    final int zLen =
        (int) MathUtil.roundValue((zMax - zMin) / this.pixelDepth) + 5;

    this.xLen = xLen;
    this.yLen = yLen;
    this.zLen = zLen;

    this.array = new byte[zLen][][];

    for (int i = 0; i < array.length; i++) {

      array[i] = new byte[yLen][];
      for (int j = 0; j < yLen; j++)
        array[i][j] = new byte[xLen];
    }

    this.intensities = new int[zLen][][];
    for (int i = 0; i < intensities.length; i++) {

      intensities[i] = new int[yLen][];
      for (int j = 0; j < yLen; j++)
        intensities[i][j] = new int[xLen];
    }

  }

  private void fillArray(final AbstractListPoint3D lp, final byte pointType) {

    for (Point3D p : lp)
      setBitMapValue(p.getX(), p.getY(), p.getZ(), pointType);
  }

  private byte getPixelPresent(final int x, final int y, final int z) {

    if (x < 0 || y < 0 || z < 0 || x >= xLen || y >= yLen || z >= zLen)
      return 0;

    final byte value = this.array[z][y][x];

    return value > 0 ? (byte) 1 : (byte) 0;
  }

  private int[] countAxesSurfaces(final int x, final int y, final int z) {

    final int xCount =
        2 - getPixelPresent(x - 1, y, z) - getPixelPresent(x + 1, y, z);
    final int yCount =
        2 - getPixelPresent(x, y - 1, z) - getPixelPresent(x, y + 1, z);
    final int zCount =
        2 - getPixelPresent(x, y, z - 1) - getPixelPresent(x, y, z + 1);

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

          if (getBitMapValue(i, j, k) == 0)
            continue;

          final int[] faces = countAxesSurfaces(i, j, k);

          if (faces[0] == 0 && faces[1] == 0 && faces[2] == 0)
            continue;

          surface +=
              faces[0] * xSurface + faces[1] * ySurface + faces[2] * zSurface;

        }

    return surface;
  }

  /**
   * Calc the median circularity of the particle.
   * @return the median circularity of the particle
   */
  public double calcMedianCircularity() {

    final int xLen = this.xLen;
    final int yLen = this.yLen;
    final int zLen = this.zLen;

    final double pixelWidth = this.pixelWidth;
    final double pixelHeight = this.pixelHeight;
    final double squareArea = pixelWidth * pixelHeight;

    final double[] circularities = new double[zLen];

    for (int k = 0; k < zLen; k++) {

      int count = 0;
      double perimeter = 0;

      for (int i = 0; i < xLen; i++)
        for (int j = 0; j < yLen; j++) {

          if (isParticleInnerPoint(i, j, k)) {

            if (!isParticleInnerPoint(i - 1, j, k))
              perimeter += pixelHeight;
            if (!isParticleInnerPoint(i + 1, j, k))
              perimeter += pixelHeight;
            if (!isParticleInnerPoint(i, j - 1, k))
              perimeter += pixelWidth;
            if (!isParticleInnerPoint(i, j + 1, k))
              perimeter += pixelWidth;

            count++;
          }
        }

      final double area = count * squareArea;
      circularities[k] =
          perimeter == 0.0 ? 0.0 : 4.0
              * Math.PI * (area / (perimeter * perimeter));
    }

    return new Median().evaluate(circularities);
  }

  /**
   * Test if a point is in the BitMap.
   * @param p Point to test
   * @return true if the point to test is in the bitmap
   */
  public boolean isParticlePoint(final Point3D p) {

    if (p == null)
      return false;

    return getBitMapValue(p.getX(), p.getY(), p.getZ()) != NO_POINT;
  }

  /**
   * Test if a point is in the BitMap.
   * @param x x bitmap coordinate
   * @param y z bitmap coordinate
   * @param z z bitmap coordinate
   * @return true if the point to test is in the bitmap
   */
  public boolean isParticlePoint(final int x, final int y, final int z) {

    // System.out.println("x="
    // + x + "\ty=" + y + "\tz=" + z + "\tval=" + getBitMapValue(x, y, z));

    // final byte val = getBitMapValue(x, y, z);

    // if (val > 0)
    // System.out.println(val + "\t" + NO_POINT + "\t" + (val != NO_POINT));

    return getBitMapValue(x, y, z) != NO_POINT;
  }

  /**
   * Test if a point is in a Surface in the BitMap.
   * @param p Point to test
   * @return true if the point to test is in the surface of the bitmap
   */
  public boolean isParticleSurfacePoint(final Point3D p) {

    if (p == null)
      return false;

    return getBitMapValue(p.getX(), p.getY(), p.getZ()) == SURFACE_POINT;
  }

  /**
   * Test if a point is in a Surface in the BitMap.
   * @param x x bitmap coordinate
   * @param y z bitmap coordinate
   * @param z z bitmap coordinate
   * @return true if the point to test is in the surface of the bitmap
   */
  public boolean isParticleSurfacePoint(final int x, final int y, final int z) {

    return getBitMapValue(x, y, z) == SURFACE_POINT;
  }

  /**
   * Test if a point is under the surface in the BitMap.
   * @param p Point to test
   * @return true if the point to test is under the surface in the bitmap
   */
  public boolean isParticleInnerPoint(final Point3D p) {

    if (p == null)
      return false;

    return getBitMapValue(p.getX(), p.getY(), p.getZ()) == INNER_POINT;
  }

  /**
   * Test if a point is under the surface in the BitMap.
   * @param x x bitmap coordinate
   * @param y z bitmap coordinate
   * @param z z bitmap coordinate
   * @return true if the point to test is under the surface in the bitmap
   */
  public boolean isParticleInnerPoint(final int x, final int y, final int z) {

    return getBitMapValue(x, y, z) == INNER_POINT;
  }

  /**
   * Test if a point is in the particle.
   * @param point Point to test
   * @return true if the point is in the particle
   */
  public boolean isPointInParticle(final Point3D point) {

    final boolean in = isParticlePoint(point);

    final boolean result;

    if (!in)
      return false;
    else if (isParticleInnerPoint(point))
      return true;

    final Point3D nearest = findNearestInnerPointNotSurfacePoint(point);

    final float x = nearest.getX();
    final float y = nearest.getY();
    final float z = nearest.getZ();

    boolean found = false;
    for (int i = -1; i <= 1; i++)
      for (int j = -1; j <= 1; j++)
        for (int k = -1; k <= 1; k++) {

          if (i == 0 && j == 00 && k == 0)
            continue;

          final Point3D p =
              new SimplePoint3DImpl(x + i * this.pixelWidth, y
                  + j * this.pixelHeight, z + k * this.pixelDepth);

          if (isParticlePoint(p)) {

            final Point3D middle = getMiddle(nearest, p);
            if (isPointInSphere(middle, point)) {

              found = true;
              break;
            }
          }
        }

    if (found)
      result = true;
    else
      result = false;

    return result;
  }

  private Point3D findNearestInnerPointNotSurfacePoint(final Point3D point) {

    Point3D nearest = null;
    float minDistance = Float.MAX_VALUE;

    final int n = this.particle.innerPointsCount();

    for (int j = 0; j < n; j++) {
      final Point3D p2 = this.particle.getInnerPoint(j);

      if (nearest == null) {
        nearest = p2;
        minDistance = p2.distance(point);
      } else {

        float d = p2.distance(point);
        if (d < minDistance && isParticleInnerPoint(p2)) {
          minDistance = d;
          nearest = p2;
        }

      }

    }

    return nearest;
  }

  private Point3D getMiddle(final Point3D point1, final Point3D point2) {

    final ArrayListPoint3D lineSegment = new ArrayListPoint3D();
    lineSegment.add(point1);
    lineSegment.add(point2);

    return lineSegment.getCenter();
  }

  private boolean isPointInSphere(final Point3D ref, final Point3D point) {

    if (Math.abs(ref.getX() - point.getX()) > this.pixelWidth / 2)
      return false;
    if (Math.abs(ref.getY() - point.getY()) > this.pixelHeight / 2)
      return false;
    if (Math.abs(ref.getZ() - point.getZ()) > this.pixelDepth / 2)
      return false;

    return true;
  }

  private void testAdditionalSurfacePoint(final int x, final int y, final int z) {

    final byte value = getBitMapValue(x, y, z); // this.array[z][y][x];

    if (value != INNER_POINT)
      return;

    if (getBitMapValue(x - 1, y, z) == NO_POINT
        || getBitMapValue(x + 1, y, z) == NO_POINT
        || getBitMapValue(x, y - 1, z) == NO_POINT
        || getBitMapValue(x, y + 1, z) == NO_POINT
        || getBitMapValue(x, y, z - 1) == NO_POINT
        || getBitMapValue(x, y, z + 1) == NO_POINT) {

      this.array[z][y][x] = DISCOVERED_SURFACE_POINT;

      testAdditionalSurfacePoint(x - 1, y, z);
      testAdditionalSurfacePoint(x + 1, y, z);
      testAdditionalSurfacePoint(x, y + 1, z);
      testAdditionalSurfacePoint(x, y + 1, z);
      testAdditionalSurfacePoint(x, y, z + 1);
      testAdditionalSurfacePoint(x, y, z + 1);
    }

  }

  private void findSurfacePoints() {

    for (int i = 0; i < this.xLen; i++)
      for (int j = 0; j < this.yLen; j++)
        for (int k = 0; k < this.zLen; k++)
          if (this.array[k][j][i] == SURFACE_POINT) {

            testAdditionalSurfacePoint(i - 1, j, k);
            testAdditionalSurfacePoint(i + 1, j, k);
            testAdditionalSurfacePoint(i, j + 1, k);
            testAdditionalSurfacePoint(i, j + 1, k);
            testAdditionalSurfacePoint(i, j, k + 1);
            testAdditionalSurfacePoint(i, j, k + 1);
          }

    for (int i = 0; i < this.xLen; i++)
      for (int j = 0; j < this.yLen; j++)
        for (int k = 0; k < this.zLen; k++)
          if (this.array[k][j][i] == DISCOVERED_SURFACE_POINT)
            this.array[k][j][i] = SURFACE_POINT;

  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   * @param particle Particle3D to "pixelised"
   */
  BitMapParticle3D(final Particle3D particle) {

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
  BitMapParticle3D(final Particle3D particle, final float pixelWidth,
      final float pixelHeight, final float pixelDepth) {

    this.pixelWidth = pixelWidth;
    this.pixelHeight = pixelHeight;
    this.pixelDepth = pixelDepth;

    initArray(particle.getInnerPoints());
    fillArray(particle.getInnerPoints(), INNER_POINT);
    fillArray(particle.getSurfacePoints(), SURFACE_POINT);
    findSurfacePoints();

    for (Point3D p : particle.getInnerPoints())
      setBitMapIntensity(p.getX(), p.getY(), p.getZ(), p.getI());

    this.particle = particle;
  }

}
