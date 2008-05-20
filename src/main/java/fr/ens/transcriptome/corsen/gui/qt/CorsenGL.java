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

package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.opengl.QGLWidget;

import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.gui.marchingcubes.DrawMarchingCubes;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
import fr.ens.transcriptome.corsen.model.BitMapParticle3D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.Point3D;
import fr.ens.transcriptome.corsen.model.SimplePoint3DImpl;

public class CorsenGL {

  public static final Color COLOR_CYAN = Color.cyan;
  public static final Color COLOR_GREEN = Color.green;
  public static final Color COLOR_RED = Color.red;
  public static final Color COLOR_WHITE = Color.white;
  public static final Color COLOR_BLACK = Color.black;

  protected float xScale = 1;
  protected float yScale = 1;
  protected float zScale = 1;

  private static final float LEN = 0.5f;

  private GL gl;
  private GLUT glut;
  private GLU glu;
  private QGLWidget widgetGL;

  /**
   * Draw the legend
   * @param unit Unit of the legend
   * @throws IOException
   */
  public void drawAxis() {

    Point3D o = new SimplePoint3DImpl(0, 0, 0);
    Point3D x = new SimplePoint3DImpl(10, 0, 0);
    Point3D y = new SimplePoint3DImpl(0, 10, 0);
    Point3D z = new SimplePoint3DImpl(0, 0, 10);

    drawLine3D(o, x, COLOR_WHITE);
    drawLine3D(o, y, COLOR_WHITE);
    drawLine3D(o, z, COLOR_WHITE);
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
      final boolean barycentre, final Color colorBaryCentre, final float alpha) {

    if (particle == null)
      return;

    this.xScale = particle.getPixelWidth();
    this.yScale = particle.getPixelHeight();
    this.zScale = particle.getPixelDepth();

    // drawPoints3D(particle.getInnerPoints(), color,
    // particle.getPixelDepth()/2);

    // drawPoints3D(particle.getInnerPoints(), color, particle.getPixelDepth() /
    // 10);
    drawSurfaceParticle3D(particle, color, alpha);

    // drawPolygon3D(particle.getSurfacePoints(), getLowColor(color));

    // if (barycentre)
    // drawPoint3D(particle.getInnerPoints().getBarycenter(), colorBaryCentre,
    // LEN);

  }

  public void drawBarycenter(final Particles3D particles, final Color color) {

    if (particles == null)
      return;

    for (Particle3D p : particles.getParticles())
      drawPoint3D(p.getInnerPoints().getBarycenter(), color, LEN / 2);
  }

  public void drawParticles(final Particles3D particles, final Color color,
      final boolean barycentre, final Color colorBaryCentre,
      final boolean randomColor, final float alpha) {

    if (particles == null)
      return;

    Color c = color;

    int r = 0;
    int g = 125;
    int b = 255;

    for (Particle3D par : particles.getParticles()) {

      if (randomColor) {

        int id = par.getId();
        c = new Color((id % 19) * 10, (id % 53) * 4, (id % 97) * 2);
        // c = new Color(r, g, b);
        drawParticle(par, c, barycentre, colorBaryCentre, alpha);

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
        drawParticle(par, c, barycentre, colorBaryCentre, alpha);
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

  public static Color inverseColor(final Color c) {

    if (c == null)
      return null;

    return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue(), c
        .getAlpha());

  }

  public void drawPoint3D(final Point3D point, final Color color,
      final float size) {

    if (point == null)
      return;

    // drawDiamondPoint(point.getX(), point.getY(), point.getZ(), 0.5f, color);
    solidCube(size, point, color);

    /*
     * this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_POINT);
     * this.gl.glBegin(GL.GL_POINT); setGLColor(color);
     * this.gl.glVertex3f(point.getX(), point.getY(), point.getZ());
     * this.gl.glEnd();
     */
  }

  public void drawPoints3D(AbstractListPoint3D points, Color color,
      final float size) {

    if (points == null)
      return;

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_POINT);
    setGLColor(color);

    this.gl.glBegin(GL.GL_POINTS);

    int n = points.size();

    for (int i = 0; i < n; i++) {

      final Point3D p = points.get(i);
      // this.gl.glVertex3f(p.getX(), p.getY(), p.getZ());
      // drawDiamondPoint(p.getX(), p.getY(), p.getZ(), .5f, color);
      solidCube(size, p, color);
    }

    this.gl.glEnd();
  }

  public void drawPolygon3D(AbstractListPoint3D points, Color color) {

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

  public void drawLine3D(Point3D a, Point3D b, Color c) {

    if (a == null || b == null)
      return;

    final FloatBuffer mat_diffuse =
        FloatBuffer.wrap(new float[] {c.getRed() / 255.0f,
            c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f});

    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_diffuse);

    this.gl.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f,
        c.getBlue() / 255.0f, 1);

    this.gl.glBegin(GL.GL_LINES);

    this.gl.glVertex3f(a.getX(), a.getY(), a.getZ());
    this.gl.glVertex3f(b.getX(), b.getY(), b.getZ());

    this.gl.glEnd();
  }

  public void text3D(Point3D p, String text, Color c) {

    this.gl.glPushMatrix();
    this.gl.glColor4f(c.getRed() / 255.0f, c.getGreen() / 255.0f,
        c.getBlue() / 255.0f, 1.0f);
    // this.widgetGL.renderText(p.getX(), p.getY(), p.getZ(), text);
    this.widgetGL.renderText(5, 15, text);
    this.gl.glPopMatrix();
  }

  public void drawDistances(Map<Particle3D, Distance> distances, Color color,
      final boolean showNegativesDistances) {

    if (distances == null)
      return;

    for (Map.Entry<Particle3D, Distance> e : distances.entrySet()) {

      final Distance d = e.getValue();

      if (showNegativesDistances) {

        if (d.getDistance() < 0)
          drawLine3D(d.getPointA(), d.getPointB(), CorsenGL.inverseColor(color));
        else
          drawLine3D(d.getPointA(), d.getPointB(), color);
      } else
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

    // setGLColor(color);
    // drawDiamondPoint(x, y, z, d);

    FloatBuffer no_mat = FloatBuffer.wrap(new float[] {0, 0, 0, 1});
    FloatBuffer mat_diffuse =
        FloatBuffer.wrap(new float[] {color.getRed(), color.getGreen(),
            color.getBlue(), 1});
    // FloatBuffer mat_diffuse = FloatBuffer.wrap(new float[] {1f, 0f, 0f, 1});

    FloatBuffer no_shininess = FloatBuffer.wrap(new float[] {0});

    // gl.glPushMatrix();

    // gl.glTranslatef(x, y, z);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    // this.glut.glutSolidCube(d*100);
    // gl.glPopMatrix();

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

  /*
   * private void drawDiamondPoint(final Point3D p, float d) {
   * drawDiamondPoint(p.getX(), p.getY(), p.getZ(), d); }
   */

  private void drawDiamondPoint(float x, float y, float z, float d) {

    if (true)
      return;

    // this.glut.glutSolidCube(d);

    // this.gl.glLoadIdentity();
    // this.gl.glTranslatef(x,y,z);
    // this.glut.glutSolidCube(d);
    // this.gl.glTranslatef(-x,-y,-z);

    FloatBuffer no_mat = FloatBuffer.wrap(new float[] {0, 0, 0, 1});
    FloatBuffer mat_diffuse = FloatBuffer.wrap(new float[] {1f, 0, 0, 1});
    // FloatBuffer mat_specular = FloatBuffer.wrap(new float[] {1f, 1f, 1, 1});
    FloatBuffer no_shininess = FloatBuffer.wrap(new float[] {0});
    // FloatBuffer low_shininess = FloatBuffer.wrap(new float[] {0.5f});
    // FloatBuffer high_shininess = FloatBuffer.wrap(new float[] {100});
    // FloatBuffer mat_emission = FloatBuffer.wrap(new float[] {.3f, .2f, .2f,
    // 0});

    gl.glPushMatrix();
    // gl.glTranslatef(-3.75f, 3.0f, 0);
    gl.glTranslatef(x, y, z);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    // this.glut.glutSolidSphere(d, 16, 16);
    this.glut.glutSolidCube(d);
    gl.glPopMatrix();

    // if (true) return;

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

  private static final FloatBuffer no_mat =
      FloatBuffer.wrap(new float[] {0, 0, 0, 1});

  private static final FloatBuffer no_shininess =
      FloatBuffer.wrap(new float[] {0});

  private static final FloatBuffer shininess =
      FloatBuffer.wrap(new float[] {1});

  private static final FloatBuffer mat_emission =
      FloatBuffer.wrap(new float[] {1f, 1f, 1f, 0});

  public void solidCube(float size, Point3D p, Color c) {

    final FloatBuffer mat_diffuse =
        FloatBuffer.wrap(new float[] {c.getRed() / 255.0f,
            c.getGreen() / 255.0f, c.getBlue() / 255.0f, .5f});
    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

    gl.glPushMatrix();

    // this.gl.glEnable(GL.GL_BLEND);
    // this.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    gl.glTranslatef(p.getX(), p.getY(), p.getZ());
    gl.glScalef(this.xScale, this.yScale, this.zScale);
    // gl.glScalef(.25f, .25f, .25f);

    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);

    // this.glut.glutSolidSphere(1, 16, 16);
    // this.glut.glutSolidCube(size);
    // this.glut.glutSolidOctahedron();
    // this.glut.glutSolidDodecahedron();

    this.glu.gluSphere(glu.gluNewQuadric(), size, 4, 4);
    // this.glut.glutSolidCube(1f);
    // this.glut.glutSolidSphere(1.0, 4, 4);

    // this.gl.glDisable(GL.GL_BLEND);

    gl.glPopMatrix();

    // solidCube(size, p.getX(), p.getY(), p.getZ(), c);
  }

  private void solidCube(float size, float x, float y, float z, Color c) {

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
    drawBox(GLU.getCurrentGL(), size, GL.GL_QUADS, x, y, z, c);
  }

  private static float[][] boxVertices;
  private static final float[][] boxNormals =
      { {-1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {1.0f, 0.0f, 0.0f},
          {0.0f, -1.0f, 0.0f}, {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};
  private static final int[][] boxFaces =
      { {0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4}, {4, 5, 1, 0}, {5, 6, 2, 1},
          {7, 4, 0, 3}};

  private void drawBox(GL gl, float size, int type, float x, float y, float z,
      Color c) {

    if (boxVertices == null) {
      float[][] v = new float[8][];
      for (int i = 0; i < 8; i++) {
        v[i] = new float[3];
      }
      v[0][0] = v[1][0] = v[2][0] = v[3][0] = -0.5f;
      v[4][0] = v[5][0] = v[6][0] = v[7][0] = 0.5f;
      v[0][1] = v[1][1] = v[4][1] = v[5][1] = -0.5f;
      v[2][1] = v[3][1] = v[6][1] = v[7][1] = 0.5f;
      v[0][2] = v[3][2] = v[4][2] = v[7][2] = -0.5f;
      v[1][2] = v[2][2] = v[5][2] = v[6][2] = 0.5f;
      boxVertices = v;
    }
    float[][] v = boxVertices;
    float[][] n = boxNormals;
    int[][] faces = boxFaces;

    FloatBuffer mat_diffuse =
        FloatBuffer
            .wrap(new float[] {c.getRed(), c.getGreen(), c.getBlue(), 1});

    for (int i = 5; i >= 0; i--) {
      gl.glBegin(type);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
      gl.glNormal3fv(n[i], 0);
      float[] vt = v[faces[i][0]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size + z);
      vt = v[faces[i][1]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size + z);
      vt = v[faces[i][2]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size + z);
      vt = v[faces[i][3]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size + z);
      gl.glEnd();
    }
  }

  private void drawSurfaceParticle3DWithMarchingCubes(
      final Particle3D particle, final Color c, final float alpha) {

    if (particle == null || c == null)
      return;

    if (false) {

      float red[] = {0.8f, 0.1f, 0.0f, 1.0f};
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, red, 0);
      this.glut.glutSolidTeapot(10f);

      return;
    }

    gl.glPushMatrix();

    // final float alpha = .7f;

    this.gl.glEnable(GL.GL_BLEND);
    this.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    final FloatBuffer mat_diffuse =
        FloatBuffer.wrap(new float[] {c.getRed() / 255.0f,
            c.getGreen() / 255.0f, c.getBlue() / 255.0f, alpha});

    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);

    BitMapParticle3D bitmap = particle.getBitMapParticle();

    gl.glTranslatef(bitmap.getX0(), bitmap.getY0(), bitmap.getZ0()
        * particle.getPixelDepth());

    DrawMarchingCubes.showParticle3D(this.gl, particle);

    this.gl.glDisable(GL.GL_BLEND);

    gl.glPopMatrix();

    gl.glFlush();

  }

  public void drawSurfaceParticle3D(final Particle3D particle,
      final Color color, final float alpha) {

    if (true)
      drawSurfaceParticle3DWithMarchingCubes(particle, color, alpha);
    else
      drawSurfaceParticle3DWithCubes(particle, color);
  }

  public void drawSurfaceParticle3DWithCubes(final Particle3D particle,
      final Color color) {

    if (particle == null || color == null)
      return;

    final BitMapParticle3D bpar = particle.getBitMapParticle();
    AbstractListPoint3D points = particle.getInnerPoints();

    final float alpha = 1f;

    for (Point3D p : points) {

      if (bpar.isParticleSurfacePoint(p)) {

        final int x = bpar.getXBitmapCoordinate(p.getX());
        final int y = bpar.getYBitmapCoordinate(p.getY());
        final int z = bpar.getZBitmapCoordinate(p.getZ());

        // final boolean face1 = bpar.isParticlePoint(x, y, z - 1);
        // final boolean face2 = bpar.isParticlePoint(x, y, z + 1);
        // final boolean face3 = bpar.isParticlePoint(x + 1, y, z);
        // final boolean face4 = bpar.isParticlePoint(x - 1, y, z);
        // final boolean face5 = bpar.isParticlePoint(x, y + 1, z);
        // final boolean face6 = bpar.isParticlePoint(x, y - 1, z);

        if (!bpar.isParticlePoint(x, y, z - 1))
          makeObjectSquare(x, y, z, 0, 0, 1, 0, color, alpha);

        if (!bpar.isParticlePoint(x, y, z + 1))
          makeObjectSquare(x, y, z, 180, 0, 1, 0, color, alpha);

        if (!bpar.isParticlePoint(x + 1, y, z))
          makeObjectSquare(x, y, z, 270, 0, 1, 0, color, alpha);

        if (!bpar.isParticlePoint(x - 1, y, z))
          makeObjectSquare(x, y, z, 90, 0, 1, 0, color, alpha);

        if (!bpar.isParticlePoint(x, y + 1, z))
          makeObjectSquare(x, y, z, 90, 1, 0, 0, color, alpha);

        if (!bpar.isParticlePoint(x, y - 1, z))
          makeObjectSquare(x, y, z, 270, 1, 0, 0, color, alpha);

      }

    }

  }

  private void makeObjectSquare(final float x, float y, float z,
      final float angle, final float xr, final float yr, final float zr,
      final Color c, final float alpha) {

    if (this.gl == null)
      return;

    final GL gl = this.gl;

    gl.glPushMatrix();

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
    this.gl.glEnable(GL.GL_BLEND);
    this.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    final FloatBuffer mat_diffuse =
        FloatBuffer.wrap(new float[] {c.getRed() / 255.0f,
            c.getGreen() / 255.0f, c.getBlue() / 255.0f, alpha});

    // final FloatBuffer mat_diffuse =
    // FloatBuffer.wrap(new float[] {255 / 255.0f, 255 / 255.0f, 255 / 255.0f,
    // this.alpha});

    final FloatBuffer no_mat = FloatBuffer.wrap(new float[] {0, 0, 0, 1});

    final FloatBuffer no_shininess = FloatBuffer.wrap(new float[] {0});

    final FloatBuffer mat_emission =
        FloatBuffer.wrap(new float[] {1f, 1f, 1f, 0});

    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);

    gl.glScalef(this.xScale, this.yScale, this.zScale);
    gl.glTranslatef(x, y, z);
    gl.glRotatef(angle, xr, yr, zr);

    gl.glBegin(GL.GL_POLYGON);

    gl.glVertex3f(-LEN, -LEN, -LEN);
    gl.glVertex3f(-LEN, LEN, -LEN);
    gl.glVertex3f(LEN, LEN, -LEN);
    gl.glVertex3f(LEN, -LEN, -LEN);

    gl.glEnd();
    this.gl.glDisable(GL.GL_BLEND);
    gl.glPopMatrix();

    gl.glFlush();
  }

  //
  // Constructor
  //

  public CorsenGL(final GL gl, final QGLWidget widgetGL) {

    this.gl = gl;
    this.widgetGL = widgetGL;
    this.glut = new GLUT();
    this.glu = new GLU();

  }

}
