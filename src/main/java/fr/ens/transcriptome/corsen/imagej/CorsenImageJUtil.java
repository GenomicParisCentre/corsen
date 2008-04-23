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

package fr.ens.transcriptome.corsen.imagej;

import fr.ens.transcriptome.corsen.model.Particle2D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Point2D;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;

/**
 * This contains some utils methods for CorsenSwing.
 * @author Laurent Jourdren
 */
public final class CorsenImageJUtil {

  /**
   * Create an ImagePlus from a Particle3D object
   * @param width Width of the image
   * @param height Height of the image
   * @param particle Particle3D to draw
   * @return an ImagePlus object
   */
  public static ImagePlus createImageParticles3D(final int width,
      final int height, final Particle3D particle) {

    return createImageParticles3D(null, width, height, particle);
  }

  /**
   * Create an ImagePlus from a Particle3D object
   * @param width Width of the image
   * @param height Height of the image
   * @param particle Particle3D to draw
   * @return an ImagePlus object
   */
  public static ImagePlus createImageParticles3D(final String title,
      final int width, final int height, final Particle3D particle) {

    if (particle == null)
      return null;

    return createImageParticles2D(
        title == null ? particle.getName() : "noname", width, height, particle
            .toParticles2D());
  }

  /**
   * Create an ImagePlus from Particle2D data
   * @param title Title of the image
   * @param width Width of the image
   * @param height Height of the image
   * @param particles Particles to draw
   * @return an ImagePlus object
   */
  public static ImagePlus createImageParticles2D(final String title,
      final int width, final int height, final Particle2D[] particles) {

    if (particles == null || particles.length == 0 || width < 0 || height < 0)
      return null;

    final ImageStack stack = new ImageStack(width, height);

    for (int i = 0; i < particles.length; i++) {

      final ImageProcessor drawIP = new ByteProcessor(width, height);
      stack.addSlice(null, drawIP);

      drawIP.setColor(Color.white);
      drawIP.fill();
      drawIP.setColor(Color.black);

      drawParticle(drawIP, particles[i]);
    }

    return new ImagePlus(title, stack);
  }

  private static void drawParticle(final ImageProcessor ip,
      final Particle2D particle) {

    drawParticle(ip, particle, Color.GRAY);
  }

  private static void drawParticle(final ImageProcessor ip,
      final Particle2D particle, final Color color) {

    if (particle == null)
      return;

    final int nSurface = particle.surfacePointsCount();
    final int nInner = particle.innerPointsCount();

    // Draw inner points

    ip.setColor(color);

    final float pixelWidth = particle.getPixelWidth();
    final float pixelHeight = particle.getPixelHeight();

    for (int i = 0; i < nInner; i++) {

      final Point2D pt = particle.getInnerPoint(i);
      final int x = (int) (pt.getX() / pixelWidth);
      final int y = (int) (pt.getY() / pixelHeight);
      ip.drawPixel(x, y);
    }

    // Draw outline

    ip.setColor(Color.BLACK);

    for (int i = 0; i < nSurface; i++) {

      final Point2D pt = particle.getSurfacePoint(i);
      final int x = (int) (pt.getX() / pixelWidth);
      final int y = (int) (pt.getY() / pixelHeight);
      ip.drawPixel(x, y);
    }

  }

  /**
   * Draw a particle3D on an ImageStack
   * @param stack Stack where to draw
   * @param particle Particle to draw
   * @param color Color to use
   */
  public static void addParticle3DtoStack(final ImageStack stack,
      final Particle3D particle, final Color color) {

    if (stack == null || particle == null)
      return;

    final Color colorToDraw;

    if (color == null)
      colorToDraw = Color.white;
    else
      colorToDraw = color;

    final int minStack =
        (int) (particle.getInnerPoints().getZMin() / particle.getPixelDepth());

    Particle2D[] pars2d = particle.toParticles2D();

    int surfaceCountPixel = 0;

    for (int i = 0; i < pars2d.length; i++) {

      surfaceCountPixel += pars2d[i].surfacePointsCount();
      ImageProcessor drawIP = stack.getProcessor(minStack + i);
      drawParticle(drawIP, pars2d[i], colorToDraw);
    }

  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private CorsenImageJUtil() {
  }

}
