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

public class TestGL extends QGLWidget {

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

    context.makeCurrent();
    this.glu = new GLU();
    this.glut = new GLUT();

    // glu.gluPerspective(40.0,1.5,50.0,100.0);
    // glu.gluLookAt(-100, 100, 100, 100, 100, 25, 0, 1, 0);

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
    //this.gl.glViewport(0, 0, width, height);

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

    //this.glut.glutWireCube(1.0f);

    //this.glu.gluLookAt(0, 0, 5, 0, 0, 0, 0, 1, 0);

    gl.glTranslated(-.25,- .25, 0);
    
    gl.glColor3f(1.0f, 1.0f, 1.0f);
    gl.glBegin(GL.GL_POLYGON);
    gl.glVertex3f(.25f, .25f, 0);
    gl.glVertex3f(.75f, .25f, 0);
    gl.glVertex3f(.75f, .75f, 0);
    gl.glVertex3f(.25f, .75f, 0);
    gl.glEnd();

    gl.glFlush();

  }

  public static void main(String[] args) {

    QApplication.initialize(args);
    TestGL testGL = new TestGL();
    testGL.show();
    QApplication.exec();

  }

}
