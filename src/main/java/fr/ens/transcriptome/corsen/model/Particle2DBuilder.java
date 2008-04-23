/*
 *                  Corsen development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence version 2 or later. This
 * should be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Corsen project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen.model;

public final class Particle2DBuilder {

  private Particle2D particle;
  private boolean edgeParticle;
  private float maxX, maxY;

  /**
   * Add an inner point.
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i Intensity of the point to add
   */
  public void addInnerPoint(final float x, final float y, final int i) {

    this.particle.addInnerPoint(x, y, i);
  }

  /**
   * Add an inner point.
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i Intensity of the point to add
   */
  public void addSurfacePoint(final float x, final float y, final int i) {

    if (!this.edgeParticle
        && (x == 0.0 || y == 0.0 || x >= this.maxX || y >= this.maxY))
      this.edgeParticle = true;

    this.particle.addSurfacePoint(x, y, i);
  }

  public Particle2D getParticle() {

    this.particle.setEdgeParticle(this.edgeParticle);

    return this.particle;
  }

  //
  // Constructor
  //

  /**
   * Public constructor
   * @param pixelWidth The width of a pixel
   * @param pixelHeight The height of a pixel
   */
  public Particle2DBuilder(final float pixelWidth, final float pixelHeight,
      final int width, final int heigth) {

    this.particle = new Particle2D(pixelHeight, pixelHeight);
    this.maxX = (width - 1) * pixelWidth + 0.5f;
    this.maxY = (heigth - 1) * pixelHeight + 0.5f;
  }

}
