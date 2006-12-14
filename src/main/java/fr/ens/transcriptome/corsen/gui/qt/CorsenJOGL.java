package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import javax.media.opengl.GL;

import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.opengl.QGLWidget;

import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.model.ListPoint3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Point3D;

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

public class CorsenJOGL extends CorsenGL {

  private GL gl;
  private QGLWidget widgetGL;

  @Override
  public void drawPoint3D(final Point3D point, final Color color) {

    if (point == null)
      return;

    drawDiamondPoint(point.getX(), point.getY(), point.getZ(), 0.1f, color);

    /*
     * this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_POINT);
     * this.gl.glBegin(GL.GL_POINT); setGLColor(color);
     * this.gl.glVertex3f(point.getX(), point.getY(), point.getZ());
     * this.gl.glEnd();
     */
  }

  @Override
  public void drawPoints3D(ListPoint3D points, Color color) {

    if (points == null)
      return;

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_POINT);
    setGLColor(color);

    this.gl.glBegin(GL.GL_POINTS);

    int n = points.size();

    for (int i = 0; i < n; i++) {

      final Point3D p = points.get(i);
      // this.gl.glVertex3f(p.getX(), p.getY(), p.getZ());
      drawDiamondPoint(p,1);
    }

    this.gl.glEnd();
  }

  @Override
  public void drawPolygon3D(ListPoint3D points, Color color) {

    if (points == null)
      return;

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
    setGLColor(color);

    this.gl.glBegin(GL.GL_POLYGON);

    int n = points.size();

    for (int i = 0; i < n; i++) {

      final Point3D p = points.get(i);
      this.gl.glVertex3f(p.getX(), p.getY(), p.getZ());
    }

    this.gl.glEnd();
  }

  @Override
  public void drawLine3D(Point3D a, Point3D b, Color color) {

    if (a == null || b == null)
      return;

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
    setGLColor(color);

    this.gl.glBegin(GL.GL_LINES);

    this.gl.glVertex3f(a.getX(), a.getY(), a.getZ());
    this.gl.glVertex3f(b.getX(), b.getY(), b.getZ());

    this.gl.glEnd();
  }

  @Override
  public void text3D(Point3D p, String text, Color color) {
    // TODO Auto-generated method stub

  }

  public void drawDistances(Map<Particle3D, Distance> distances, Color color) {

    if (distances == null)
      return;

    Iterator<Particle3D> it = distances.keySet().iterator();

    while (it.hasNext()) {

      Particle3D p = it.next();
      Distance d = distances.get(p);

      drawLine3D(d.getPointA(), d.getPointB(), color);
    }
  }

  private final void setGLColor(final Color color) {

    if (color == null)
      return;

    this.widgetGL.qglColor(new QColor(color.getRed(), color.getGreen(), color
        .getRed(), color.getAlpha()));

    /*
     * float r = color.getRed() / 255; float g = color.getGreen() / 255; float b =
     * color.getBlue() / 255; float a = color.getAlpha() / 255;
     * this.gl.glColor4f(r, g, b, a);
     */
  }

  private void drawDiamondPoint(float x, float y, float z, float d, Color color) {

    setGLColor(color);
    drawDiamondPoint(x, y, z, d);
  }

  private void drawDiamondPoint(final Point3D p, float d) {

    drawDiamondPoint(p.getX(), p.getY(), p.getZ(),d);
  }

  private void drawDiamondPoint(float x, float y, float z, float d) {

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
    this.gl.glBegin(GL.GL_TRIANGLES);
    // this.gl.glBegin(GL.GL_QUADS);

    this.gl.glVertex3d(x - d, y, z);
    this.gl.glVertex3d(x, y + d, z);
    this.gl.glVertex3d(x, y, z + d);

    this.gl.glVertex3d(x, y + d, z);
    this.gl.glVertex3d(x + d, y, z);
    this.gl.glVertex3d(x, y, z + d);

    this.gl.glVertex3d(x + d, y, z);
    this.gl.glVertex3d(x, y - d, z);
    this.gl.glVertex3d(x, y, z + d);

    this.gl.glVertex3d(x, y - d, z);
    this.gl.glVertex3d(x - d, y, z);
    this.gl.glVertex3d(x, y, z + d);

    this.gl.glVertex3d(x - d, y, z);
    this.gl.glVertex3d(x, y + d, z);
    this.gl.glVertex3d(x, y, z - d);

    this.gl.glVertex3d(x, y + d, z);
    this.gl.glVertex3d(x + d, y, z);
    this.gl.glVertex3d(x, y, z - d);

    this.gl.glVertex3d(x + d, y, z);
    this.gl.glVertex3d(x, y - d, z);
    this.gl.glVertex3d(x, y, z - d);

    this.gl.glVertex3d(x, y - d, z);
    this.gl.glVertex3d(x - d, y, z);
    this.gl.glVertex3d(x, y, z - d);

    this.gl.glEnd();

  }

  //
  // Constructor
  //

  public CorsenJOGL(final GL gl, final QGLWidget widgetGL) {
    this.gl = gl;
    this.widgetGL = widgetGL;
  }

}
