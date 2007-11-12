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

package fr.ens.transcriptome.corsen.model;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.process.ImageProcessor;

import java.awt.Rectangle;

public class Particle2DBuilder {

  /**
   * Add a particle 2D to the particle 3D.
   * @param imp Image of the particle to add
   * @param roi particle 2D to add
   */
  public static final Particle2D createParticle2D(final float pixelWidth,
      final float pixelHeight, final ImagePlus imp, final PolygonRoi roi) {

    final Particle2D result = new Particle2D(pixelWidth, pixelHeight);

    imp.setRoi(roi);

    // final ImageStatistics stats = imp.getStatistics(); // mesurement

    // Get the x0 and y0 of the Roi
    final Rectangle r = roi.getBounds();
    final int nPoints = roi.getNCoordinates();
    final int[] xp = roi.getXCoordinates();
    final int[] yp = roi.getYCoordinates();

    final int x0 = r.x;
    final int y0 = r.y;

    // Get the inner points
    final ImageProcessor ipMask = imp.getMask();
    final ImageProcessor ip = imp.getProcessor();

    final int height = ipMask.getHeight();
    final int width = ipMask.getWidth();

    for (int i = 0; i < height; i++)
      for (int j = 0; j < width; j++)
        if (ipMask.getPixel(j, i) > 0) {

          final int val = ip.getPixel(j + x0, i + y0);

          result.addInnerPoint((j + x0 + 0.5f) * pixelWidth, (i + y0 + 0.5f)
              * pixelWidth, val);
        }

    // double[][] polygon = new double[nPoints][];

    // result.surfacePoints.ensureCapacity(nPoints +
    // result.surfacePoints.size());

    for (int i = 0; i < nPoints; i++) {

      // Add the point to the particle 2D

      final float x = (x0 + xp[i]) * pixelWidth;
      final float y = (y0 + yp[i]) * pixelHeight;

      result.addSurfacePoint(x, y);
    }

    return result;
  }

}
