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

package fr.ens.transcriptome.corsen.gui.qt;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QWheelEvent;
import com.trolltech.qt.opengl.QGLWidget;

public class TestLightGL extends QGLWidget {

  private GL gl;
  private GLU glu;
  private GLUT glut;

  private int xRot = 0;

  private int yRot = 0;

  private int zRot = 0;

  private double zoom = 1; // 0.5;

  private static final double ZOOM_FACTOR = 1.2;

  private static final double MIN_ZOOM = 0.1;

  private static final double MAX_ZOOM = 2;

  QPoint lastPos;

  private int gllist = -1;

  public void initializeGL() {

    GLContext context = GLDrawableFactory.getFactory()
        .createExternalGLContext();

    this.gl = context.getGL();

    final GL gl = this.gl;

    context.makeCurrent();
    this.glu = new GLU();
    this.glut = new GLUT();

    
    gl.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_CULL_FACE);
    gl.glShadeModel(GL.GL_FLAT);
    gl.glDisable(GL.GL_NORMALIZE);
    gl.glEnable(GL.GL_COLOR_MATERIAL);
    gl.glEnable(GL.GL_LIGHTING);
    gl. glEnable(GL.GL_LIGHT0);

    
    gl.glLightfv( GL.GL_LIGHT0, GL.GL_AMBIENT,  FloatBuffer.wrap(new float[] {.3f, .3f, .3f, .3f}) );
    gl.glLightfv( GL.GL_LIGHT0, GL.GL_DIFFUSE,  FloatBuffer.wrap(new float[] {.8f, .8f, .8f, .8f}) );
    gl.glLightfv( GL.GL_LIGHT0, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] {1, 1, 1, 1}) );
    
    
    if (false) {
    
    FloatBuffer mat_specular = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer mat_shininess = FloatBuffer.wrap(new float[] {50});
    FloatBuffer light_position0 = FloatBuffer.wrap(new float[] {1, 1, 1, 0});

    FloatBuffer white_light = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer lmodel_ambient = FloatBuffer.wrap(new float[] {0.9f, 0.9f,
        0.9f, 1.0f});

    FloatBuffer light_ambient = FloatBuffer.wrap(new float[] {0, 0, 0, 1});
    FloatBuffer light_diffuse = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer light_specular = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer light_position = FloatBuffer.wrap(new float[] {1, 1, 1, 0});

    gl.glClearColor(0, 0, 0, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position0);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, white_light);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, white_light);

    gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, light_specular);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position);

    gl.glLightf(GL.GL_LIGHT0, GL.GL_LINEAR_ATTENUATION, 1.0f);
    gl.glLightf(GL.GL_LIGHT0, GL.GL_CONSTANT_ATTENUATION, 2.0f);

    gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);

    gl.glEnable(GL.GL_LIGHTING);
    gl.glEnable(GL.GL_LIGHT0);
    gl.glEnable(GL.GL_DEPTH_TEST);

    }
    // qglClearColor(trolltechPurple.dark());
    makeObject();

    this.gl.glShadeModel(GL.GL_FLAT);
    // this.gl.glEnable(GL.GL_DEPTH_TEST);
    // this.gl.glEnable(GL.GL_CULL_FACE);

    // Lights parameters

    /*
     * this.gl.glShadeModel(GL.GL_SMOOTH);
     * this.gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
     * this.gl.glEnable(GL.GL_LIGHTING); this.gl.glEnable(GL.GL_LIGHT0);
     * FloatBuffer l0dif = FloatBuffer.wrap(new float[] {0.3f,0.3f,0.8f});
     * FloatBuffer l0pos = FloatBuffer.wrap(new float[] {0f,0f,0f}); FloatBuffer
     * l1dif = FloatBuffer.wrap(new float[] {0.5f,0.5f,0.5f}); FloatBuffer mSpec =
     * FloatBuffer.wrap(new float[] {0.5f,0.5f,0.5f});
     */

    // this.gl.glEnable(GL.GL_LIGHT1);
    // this.gl.glLightfv(GL.GL_LIGHT0,GL.GL_POSITION, l0dif);
    /*
     * this.gl.glLightfv(GL.GL_LIGHT0,GL.GL_DIFFUSE, l0dif);
     * this.gl.glLightfv(GL.GL_LIGHT0,GL.GL_SPECULAR, l0dif);
     * this.gl.glLightfv(GL.GL_LIGHT1,GL.GL_DIFFUSE, l1dif);
     * this.gl.glLightfv(GL.GL_LIGHT1,GL.GL_SPECULAR, l1dif);
     */

    // Materials
    // this.gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, mSpec);
    // this.gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, 50);
  }

  public void resizeGL(int width, int height) {

    int side = width <= height ? width : height; // qMin(width, height);
    this.gl.glViewport((width - side) / 2, (height - side) / 2, side, side);
    // this.gl.glViewport(0, 0, width, height);

    this.gl.glMatrixMode(GL.GL_PROJECTION);
    this.gl.glLoadIdentity();
    // this.gl.glOrtho(-zoom, +zoom, +zoom, -zoom, -1000, 1000);
    this.gl.glFrustum(-zoom, +zoom, +zoom, -zoom, 1.5, 20.0);
    this.gl.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void paintGL() {

    

    
    
    this.gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    this.gl.glLoadIdentity();
    this.gl.glTranslated(0.0, 0.0, -10.0);
    this.gl.glRotated(xRot / 16.0, 1.0, 0.0, 0.0);
    this.gl.glRotated(yRot / 16.0, 0.0, 1.0, 0.0);
    this.gl.glRotated(zRot / 16.0, 0.0, 0.0, 1.0);

    //eigen
    this.gl.glLightfv( GL.GL_LIGHT0, GL.GL_POSITION, FloatBuffer.wrap(new float[] {0, 1, 1, 0}) );
    
    
    makeObject();

    this.gl.glMatrixMode(GL.GL_PROJECTION);
    this.gl.glLoadIdentity();
    // this.gl.glOrtho(-zoom, +zoom, +zoom, -zoom, -1000, 1000);
    this.gl.glFrustum(-zoom, +zoom, +zoom, -zoom, 1.5, 20.0);
    this.gl.glMatrixMode(GL.GL_MODELVIEW);

  }

  public void mousePressEvent(QMouseEvent event) {

    this.lastPos = event.pos();
  }

  public void mouseMoveEvent(QMouseEvent event) {

    int dx = event.x() - lastPos.x();
    int dy = event.y() - lastPos.y();

    if (event.buttons().value() == Qt.MouseButton.LeftButton.value()) {
      setXRotation(xRot + 8 * dy);
      setYRotation(yRot + 8 * dx);
    } else if (event.buttons().value() == Qt.MouseButton.RightButton.value()) {
      setXRotation(xRot + 8 * dy);
      setZRotation(zRot + 8 * dx);
    }

    // event.buttons() == Qt.MouseButton.

    lastPos = event.pos();
  }

  public void wheelEvent(QWheelEvent event) {

    if (event.delta() > 0) {
      final double zoom = this.zoom * ZOOM_FACTOR;
      // if (zoom < MAX_ZOOM)
      this.zoom = zoom;
    } else {
      final double zoom = this.zoom / ZOOM_FACTOR;
      // if (zoom > MIN_ZOOM)
      this.zoom = zoom;
    }

    System.out.println(event.delta() + " zoom=" + this.zoom);

    QRect geo = this.geometry();

    resize(geo.width(), geo.height());
    updateGL();
  }

  public void setXRotation(int angle) {

    angle = normalizeAngle(angle);
    if (angle != xRot) {
      xRot = angle;
      // emit xRotationChanged(angle);
      updateGL();
    }
  }

  public void setYRotation(int angle) {

    angle = normalizeAngle(angle);
    if (angle != yRot) {
      yRot = angle;
      // emit yRotationChanged(angle);
      updateGL();
    }
  }

  public void setZRotation(int angle) {

    angle = normalizeAngle(angle);
    if (angle != zRot) {
      zRot = angle;
      // emit zRotationChanged(angle);
      updateGL();
    }
  }

  private int normalizeAngle(int angle) {
    while (angle < 0)
      return angle += 360 * 16;
    while (angle > 360 * 16)
      return angle -= 360 * 16;

    return angle;
  }

  public void clear() {

    repaint();
  }

  private void makeObject() {

    if (this.gl == null)
      return;

    GL gl = this.gl;

    FloatBuffer no_mat = FloatBuffer.wrap(new float[] {0, 0, 0, 1});
    FloatBuffer mat_ambient = FloatBuffer.wrap(new float[] {0.7f, 0.7f, 0.7f,
        1.0f});
    FloatBuffer mat_ambient_color = FloatBuffer.wrap(new float[] {0.8f, 0.8f,
        0.2f, 1});
    // FloatBuffer mat_diffuse = FloatBuffer.wrap(new float[] {.1f, .5f, .8f,
    // 1});
    FloatBuffer mat_diffuse = FloatBuffer.wrap(new float[] {1f, 1, 1, 1});
    FloatBuffer mat_specular = FloatBuffer.wrap(new float[] {1f, 1f, 1, 1});
    FloatBuffer no_shininess = FloatBuffer.wrap(new float[] {0});
    FloatBuffer low_shininess = FloatBuffer.wrap(new float[] {0.5f});
    FloatBuffer high_shininess = FloatBuffer.wrap(new float[] {100});
    FloatBuffer mat_emission = FloatBuffer.wrap(new float[] {.3f, .2f, .2f, 0});


    

    gl.glPushMatrix();
    gl.glColor3f(1,0,1);
    gl.glTranslatef(-3.75f, 3.0f, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    // this.glut.glutSolidSphere(1, 16, 16);
    this.glut.glutSolidCube(0.5f);
    gl.glPopMatrix();

    glutSolidCube(1, -2,3,3);

    gl.glPushMatrix();
    gl.glTranslatef(-1.25f, 3.0f, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, low_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    this.glut.glutSolidSphere(1, 16, 16);
    gl.glPopMatrix();

    gl.glPushMatrix();
    gl.glTranslatef(1.25f, 3.0f, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, high_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    this.glut.glutSolidSphere(1, 16, 16);
    gl.glPopMatrix();

    gl.glPushMatrix();
    gl.glTranslatef(3.75f, 3.0f, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission);
    this.glut.glutSolidSphere(1, 16, 16);
    gl.glPopMatrix();

    // this.glut.glutWireCube(1.0f);
    // gl.glColor3f(1.0f, 0.0f, 0.5f);
    // this.glut.glutSolidSphere(1.0f, 20, 16);
    // this.glu.gluLookAt(0, 0, 5, 0, 0, 0, 0, 1, 0);

    /*
     * gl.glTranslated(-.25, -.25, 0); gl.glColor3f(1.0f, 1.0f, 1.0f);
     * gl.glBegin(GL.GL_POLYGON); gl.glVertex3f(.25f, .25f, 0);
     * gl.glVertex3f(.75f, .25f, 0); gl.glVertex3f(.75f, .75f, 0);
     * gl.glVertex3f(.25f, .75f, 0); gl.glEnd();
     */

    gl.glFlush();

  }

  public void glutSolidCube(float size, float x, float y, float z) {
    drawBox(GLU.getCurrentGL(), size, GL.GL_QUADS, x, y, z);
  }

  private static float[][] boxVertices;
  private static final float[][] boxNormals = { {-1.0f, 0.0f, 0.0f},
      {0.0f, 1.0f, 0.0f}, {1.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f},
      {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};
  private static final int[][] boxFaces = { {0, 1, 2, 3}, {3, 2, 6, 7},
      {7, 6, 5, 4}, {4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3}};

  private void drawBox(GL gl, float size, int type, float x, float y, float z) {
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
    for (int i = 5; i >= 0; i--) {
      gl.glBegin(type);
      gl.glNormal3fv(n[i], 0);
      float[] vt = v[faces[i][0]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size +z);
      vt = v[faces[i][1]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size+z);
      vt = v[faces[i][2]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size+z);
      vt = v[faces[i][3]];
      gl.glVertex3f(vt[0] * size + x, vt[1] * size + y, vt[2] * size+z);
      gl.glEnd();
    }
  }

  public static void main(String[] args) {

    QApplication.initialize(args);
    TestLightGL testGL = new TestLightGL();
    testGL.show();
    QApplication.exec();

  }

}
