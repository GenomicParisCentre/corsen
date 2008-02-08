package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.opengl.QGLWidget;

import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.model.AbstractListPoint3D;
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

public class CorsenJOGL extends CorsenGL {

  private GL gl;
  private GLUT glut;
  private GLU glu;
  private QGLWidget widgetGL;

  public static Color inverseColor(final Color c) {

    if (c == null)
      return null;

    return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue(), c
        .getAlpha());

  }

  @Override
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

  @Override
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

  @Override
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

  @Override
  public void drawLine3D(Point3D a, Point3D b, Color color) {

    if (a == null || b == null)
      return;

    final FloatBuffer mat_diffuse =
        FloatBuffer.wrap(new float[] {color.getRed() / 255.0f,
            color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1});

    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission);

    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
    // setGLColor(color);

    this.gl.glBegin(GL.GL_LINES);

    this.gl.glVertex3f(a.getX(), a.getY(), a.getZ());
    this.gl.glVertex3f(b.getX(), b.getY(), b.getZ());

    this.gl.glEnd();
  }

  @Override
  public void text3D(Point3D p, String text, Color color) {
    // TODO Auto-generated method stub

  }

  public void drawDistances(Map<Particle3D, Distance> distances, Color color,
      final boolean showNegativesDistances) {

    if (distances == null)
      return;

    for (Map.Entry<Particle3D, Distance> e : distances.entrySet()) {

      final Distance d = e.getValue();

      if (showNegativesDistances) {

        if (d.getDistance() < 0)
          drawLine3D(d.getPointA(), d.getPointB(), CorsenJOGL
              .inverseColor(color));
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

  private static final FloatBuffer mat_emission =
      FloatBuffer.wrap(new float[] {1f, 1f, 1f, 0});

  public void solidCube(float size, Point3D p, Color c) {

    final FloatBuffer mat_diffuse =
        FloatBuffer.wrap(new float[] {c.getRed() / 255.0f,
            c.getGreen() / 255.0f, c.getBlue() / 255.0f, .5f});
    this.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

    gl.glPushMatrix();

    this.gl.glEnable(GL.GL_BLEND);
    this.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

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

    this.glu.gluSphere(glu.gluNewQuadric(), 1, 4, 4);
    // this.glut.glutSolidCube(1f);
    // this.glut.glutSolidSphere(1.0, 4, 4);

    this.gl.glDisable(GL.GL_BLEND);

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

  //
  // Constructor
  //

  public CorsenJOGL(final GL gl, final QGLWidget widgetGL) {

    this.gl = gl;
    this.widgetGL = widgetGL;
    this.glut = new GLUT();
    this.glu = new GLU();

  }

}
