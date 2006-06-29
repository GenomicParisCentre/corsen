import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;

/**
 * This contains some utils methods for Corsen.
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

    ImageStack stack = new ImageStack(width, height);

    for (int i = 0; i < particles.length; i++) {

      ImageProcessor drawIP = new ByteProcessor(width, height);
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

    if (particle == null)
      return;

    final int nSurface = particle.surfacePointsCount();
    final int nInner = particle.innerPointsCount();

    // Draw inner points

    ip.setColor(Color.GRAY);

    for (int i = 0; i < nInner; i++) {

      Point2D pt = particle.getInnerPoint(i);
      final int x = (int) pt.getX();
      final int y = (int) pt.getY();
      ip.drawPixel(x, y);
    }

    // Draw outline

    ip.setColor(Color.BLACK);
    if (nSurface > 0) {

      Point2D pt0 = particle.getSurfacePoint(0);
      ip.moveTo((int) pt0.getX(), (int) pt0.getY());

      for (int i = 1; i < nSurface; i++) {

        Point2D pt = particle.getSurfacePoint(i);
        final int x = (int) pt.getX();
        final int y = (int) pt.getY();
        ip.lineTo(x, y);
        ip.moveTo(x, y);
      }
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
