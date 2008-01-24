package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.Point3D;
import fr.ens.transcriptome.corsen.model.SimplePoint3DImpl;

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

public abstract class CorsenGL {

  public static final Color COLOR_CYAN = Color.cyan;
  public static final Color COLOR_GREEN = Color.green;
  public static final Color COLOR_RED = Color.red;
  public static final Color COLOR_WHITE = Color.white;
  public static final Color COLOR_BLACK = Color.black;

  protected float xScale = 1;
  protected float yScale = 1;
  protected float zScale = 1;

  public abstract void drawPoint3D(Point3D point, final Color color,
      final float size);

  public abstract void drawLine3D(final Point3D a, final Point3D b,
      final Color color);

  public abstract void text3D(final Point3D p, final String text,
      final Color color);

  public abstract void drawPoints3D(AbstractListPoint3D points,
      final Color color, final float size);

  public abstract void drawPolygon3D(AbstractListPoint3D points,
      final Color color);

  /**
   * Draw the legend
   * @param unit Unit of the legend
   * @throws IOException
   */
  public void drawLegend(final String unit) {

    // final Writer out = this.out;

    Point3D o = new SimplePoint3DImpl(0, 0, 0);
    Point3D x = new SimplePoint3DImpl(10, 0, 0);
    Point3D y = new SimplePoint3DImpl(0, 10, 0);
    Point3D z = new SimplePoint3DImpl(0, 0, 10);

    drawLine3D(o, x, COLOR_WHITE);
    drawLine3D(o, y, COLOR_WHITE);
    drawLine3D(o, z, COLOR_WHITE);

    String text = "10 " + unit;

    text3D(new SimplePoint3DImpl(-1, -1, -1), text, COLOR_WHITE);

    /*
     * out.write("text3d(x=-1,y=-1,z=-1,text=\"" + text + "\",color=\"" +
     * COLOR_WHITE + "\")\n");
     */

  }

  /**
   * Write the lines of distances.
   * @param distances Distances to draw
   * @param color Color of the lines
   * @throws IOException if an error occurs while writing the distances
   */
  public void writeDistances(final Map<Particle3D, Distance> distances,
      final Color color) throws IOException {

    if (distances != null) {

      Iterator<Particle3D> it = distances.keySet().iterator();

      while (it.hasNext()) {

        Distance d = distances.get(it.next());

        drawLine3D(d.getPointA(), d.getPointB(), color);
      }
    }

  }

  /**
   * Draw a particle.
   * @param particle Particle to draw
   * @param color Color of the particle
   * @param barycentre Show the barycentre
   * @param colorBaryCentre Color of the barycenter
   */
  public void drawParticle(final Particle3D particle, final Color color,
      final boolean barycentre, final Color colorBaryCentre) {

    if (particle == null)
      return;

    this.xScale = particle.getPixelWidth();
    this.yScale = particle.getPixelHeight();
    this.zScale = particle.getPixelDepth();

    // drawPoints3D(particle.getInnerPoints(), color,
    // particle.getPixelDepth()/2);
    drawPoints3D(particle.getInnerPoints(), color,
        particle.getPixelDepth() / 2);
    // drawPolygon3D(particle.getSurfacePoints(), getLowColor(color));

    /*
     * if (barycentre) drawPoint3D(particle.getBarycenter(), colorBaryCentre);
     */
  }

  public void drawParticles(final Particles3D particles, final Color color,
      final boolean barycentre, final Color colorBaryCentre,
      final boolean randomColor) {

    if (particles == null)
      return;

    Color c = color;

    int r = 0;
    int g = 125;
    int b = 255;

    for (Particle3D par : particles.getParticles()) {

      if (randomColor) {
        c = new Color(r, g, b);
        drawParticle(par, c, barycentre, colorBaryCentre);

        r += 5;
        g += 10;
        b += 15;

        if (r > 255)
          r -= 255;
        if (g > 255)
          g -= 255;
        if (b > 255)
          b -= 255;

      } else
        drawParticle(par, c, barycentre, colorBaryCentre);
    }

  }

  public static Color getLowColor(final Color c) {

    if (c == null)
      return null;

    final float factor = 0.999f;

    int r = (int) (c.getRed() * factor);
    int g = (int) (c.getGreen() * factor);
    int b = (int) (c.getBlue() * factor);

    return new Color(r, g, b, c.getAlpha());
  }

}
