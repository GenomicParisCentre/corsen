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

package fr.ens.transcriptome.corsen.imagej;

import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import fr.ens.transcriptome.corsen.model.Particle2D;
import fr.ens.transcriptome.corsen.model.Particle2DBuilder;

/**
 * This class implements a 2D segmentation for ImageJ images.
 * @author Laurent Jourdren
 */
public class Segmentation2DRunner {

  private int xLen, yLen;
  private int width, height;
  private int[][] arrayImage;
  private boolean[][] arrayDone;
  private List<Particle2D> particles = new ArrayList<Particle2D>();

  private double minThreshold;
  private double maxThreshold;
  private float pixelWidth, pixelHeight;

  /**
   * Process the 2D segmentation and return a list of Particle2D
   * @param ip ImageProcessor to use
   * @return a list of Particle2D
   */
  public List<Particle2D> getParticles2D(final ImageProcessor ip) {

    this.particles.clear();

    this.xLen = ip.getWidth();
    this.yLen = ip.getHeight();
    this.minThreshold = ip.getMinThreshold();
    this.maxThreshold = ip.getMaxThreshold();
    this.width = ip.getWidth();
    this.height = ip.getHeight();

    this.arrayImage = ip.getIntArray();

    intArrayDone();

    for (int i = 0; i < this.xLen; i++)
      for (int j = 0; j < this.yLen; j++)
        if (getPixelMask(i, j) > 0 && !isPixelDone(i, j))
          this.particles.add(buildParticle(i, j));

    return this.particles;
  }

  /**
   * With the first point of a new particle, search all the point of this
   * particle.
   * @param x X coordinate of the first point
   * @param y Y coordinate of the second point
   * @return a new Particle2D object
   */
  private final Particle2D buildParticle(final int x, final int y) {

    final Particle2DBuilder result =
        new Particle2DBuilder(pixelWidth, pixelHeight, this.width, this.height);

    final Queue<Integer> queueX = new LinkedList<Integer>();
    final Queue<Integer> queueY = new LinkedList<Integer>();

    queueX.add(x);
    queueY.add(y);

    while (queueX.size() != 0)
      addPoint(result, queueX.poll(), queueY.poll(), queueX, queueY);

    return result.getParticle();
  }

  /**
   * Add a point to the current building particle and search of particle point
   * near this point.
   * @param particle Current building particle
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param queueX X coordinate Queue of found points of the particle to add
   * @param queueY Y coordinate Queue of found points of the particle to add
   */
  private final void addPoint(final Particle2DBuilder particle, final int x,
      final int y, Queue<Integer> queueX, Queue<Integer> queueY) {

    if (isPixelDone(x, y))
      return;

    byte count = 0;

    count = testPoint(x - 1, y - 1, count, queueX, queueY);
    count = testPoint(x, y - 1, count, queueX, queueY);
    count = testPoint(x + 1, y - 1, count, queueX, queueY);

    count = testPoint(x - 1, y, count, queueX, queueY);
    count = testPoint(x, y, count, queueX, queueY);
    count = testPoint(x + 1, y, count, queueX, queueY);

    count = testPoint(x - 1, y + 1, count, queueX, queueY);
    count = testPoint(x, y + 1, count, queueX, queueY);
    count = testPoint(x + 1, y + 1, count, queueX, queueY);

    final int value = getPixelImage(x, y);

    final float xReal = (x + 0.5f) * this.pixelWidth;
    final float yReal = (y + 0.5f) * this.pixelHeight;

    particle.addInnerPoint(xReal, yReal, value);

    if (count < 9)
      particle.addSurfacePoint(xReal, yReal, value);

    setPixelDone(x, y);
  }

  //
  // Init method
  //

  private final void intArrayDone() {

    this.arrayDone = new boolean[this.yLen][];

    for (int i = 0; i < this.yLen; i++)
      this.arrayDone[i] = new boolean[this.xLen];
  }

  //
  // Method to access to the pixels states
  //

  private final int getPixelImage(final int x, final int y) {

    if (x < 0 || y < 0 || x >= xLen || y >= yLen)
      return 0;

    return this.arrayImage[x][y];
  }

  private final int getPixelMask(final int x, final int y) {

    if (x < 0 || y < 0 || x >= xLen || y >= yLen)
      return 0;

    final int value = this.arrayImage[x][y];

    if (value < this.minThreshold || value > this.maxThreshold)
      return 0;

    return value;
  }

  private final boolean isPixelDone(final int x, final int y) {

    if (x < 0 || y < 0 || x >= xLen || y >= yLen)
      return true;

    return this.arrayDone[y][x];
  }

  private final void setPixelDone(final int x, final int y) {

    if (x < 0 || y < 0 || x >= xLen || y >= yLen)
      return;

    this.arrayDone[y][x] = true;
  }

  private final byte testPoint(final int x, final int y, final byte count,
      Queue<Integer> queueX, Queue<Integer> queueY) {

    if (getPixelMask(x, y) > 0) {

      if (!isPixelDone(x, y)) {
        queueX.add(x);
        queueY.add(y);
      }

      byte c = count;
      return ++c;
    }

    return count;
  }

  //
  // Constructor
  //

  public Segmentation2DRunner(final double pixelWidth, final double pixelHeight) {

    this.pixelWidth = (float) pixelWidth;
    this.pixelHeight = (float) pixelHeight;
  }

}
