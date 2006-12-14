package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QWheelEvent;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.opengl.QGLWidget;

import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.calc.CorsenResult;

public class ViewOGL extends QGLWidget {

  private GL gl;
  private GLU glu;

  private CorsenResult result;
  private Settings settings;
  private boolean drawMessengersCuboids;
  private boolean drawMitosCuboids;
  private boolean drawBaryCenter;
  private boolean drawDistances;
  private Color messengersColor = Color.green;
  private Color mitosColor = Color.red;
  private Color barycenterColor = Color.blue;
  private Color distanceColor = Color.yellow;
  private Color legendColor = Color.white;
  private boolean remakeObject = true;
  private int gllist = -1;

  private Color backgroundColor = Color.black;

  // private QColor trolltechPurple = QColor.fromCmykF(0.39, 0.39, 0.0, 0.0);

  private int xRot = 0;

  private int yRot = 0;

  private int zRot = 0;

  private double zoom = 1000; // 0.5;

  private static final double ZOOM_FACTOR = 1.2;

  private static final double MIN_ZOOM = 0.1;

  private static final double MAX_ZOOM = 2;

  QPoint lastPos;

  // private int object;

  //
  // Getters
  //

  /**
   * Get the corsen result.
   * @return Returns the result
   */
  public CorsenResult getResult() {
    return result;
  }

  /**
   * Get barycenter color
   * @return Returns the barycenterColor
   */
  public Color getBarycenterColor() {
    return barycenterColor;
  }

  /**
   * Get the distance color
   * @return Returns the distanceColor
   */
  public Color getDistanceColor() {
    return distanceColor;
  }

  /**
   * Test if the barycenter must be draw
   * @return Returns the drawBaryCenter
   */
  public boolean isDrawBaryCenter() {
    return drawBaryCenter;
  }

  /**
   * Test if the distance must be draw
   * @return Returns the drawDistances
   */
  public boolean isDrawDistances() {
    return drawDistances;
  }

  /**
   * Test if messengers cuboids must be draw
   * @return Returns the drawMessengersCuboids
   */
  public boolean isDrawMessengersCuboids() {
    return drawMessengersCuboids;
  }

  /**
   * Test if mitos cuboid must be draw
   * @return Returns the drawMitosCuboids
   */
  public boolean isDrawMitosCuboids() {
    return drawMitosCuboids;
  }

  /**
   * Get the messengers color
   * @return Returns the messengersColor
   */
  public Color getMessengersColor() {
    return messengersColor;
  }

  /**
   * Get the mitos Color
   * @return Returns the mitosColor
   */
  public Color getMitosColor() {
    return mitosColor;
  }

  /**
   * Test if the particles can be drawed
   * @return Returns the remakeObject
   */
  public boolean isRemakeObject() {
    return remakeObject;
  }

  //
  // Setters
  //

  /**
   * Set the corsen result
   * @param result The result to set
   */
  public void setResult(final CorsenResult result) {
    this.result = result;
  }

  /**
   * Set if the barycenter must be draw
   * @param barycenterColor The barycenterColor to set
   */
  public void setBarycenterColor(Color barycenterColor) {
    this.barycenterColor = barycenterColor;
  }

  /**
   * Set the color of the distances
   * @param distanceColor The distanceColor to set
   */
  public void setDistanceColor(Color distanceColor) {
    this.distanceColor = distanceColor;
  }

  /**
   * Set if the barycenter must be draw
   * @param drawBaryCenter The drawBaryCenter to set
   */
  public void setDrawBaryCenter(boolean drawBaryCenter) {
    this.drawBaryCenter = drawBaryCenter;
  }

  /**
   * Set if the distance must be draw.
   * @param drawDistances The drawDistances to set
   */
  public void setDrawDistances(boolean drawDistances) {
    this.drawDistances = drawDistances;
  }

  /**
   * Set if messengers cuboids must be draw.
   * @param drawMessengersCuboids The drawMessengersCuboids to set
   */
  public void setDrawMessengersCuboids(boolean drawMessengersCuboids) {
    this.drawMessengersCuboids = drawMessengersCuboids;
  }

  /**
   * Set if mitos cuboids must be draw.
   * @param drawMitosCuboids The drawMitosCuboids to set
   */
  public void setDrawMitosCuboids(boolean drawMitosCuboids) {
    this.drawMitosCuboids = drawMitosCuboids;
  }

  /**
   * Set the color of the messengers.
   * @param messengersColor The messengersColor to set
   */
  public void setMessengersColor(Color messengersColor) {
    this.messengersColor = messengersColor;
  }

  /**
   * Set the color of the mitos
   * @param mitosColor The mitosColor to set
   */
  public void setMitosColor(Color mitosColor) {
    this.mitosColor = mitosColor;
  }

  /**
   * Set if particles can be drawed
   * @param remakeObject The remakeObject to set
   */
  public void setRemakeObject(boolean okToDraw) {

    this.remakeObject = okToDraw;
  }

  //
  //
  //

  public void clear() {

    setResult(null);
    this.remakeObject = true;
    repaint();
  }

  //
  // Open GL methods
  //

  private void clearGL() {

    Color color = this.backgroundColor;
    
    qglClearColor(new QColor(color.getRed(), color.getGreen(), color
        .getRed(), color.getAlpha()));
  }

  public void initializeGL() {

    GLContext context = GLDrawableFactory.getFactory()
        .createExternalGLContext();

    this.gl = context.getGL();

    context.makeCurrent();
    this.glu = new GLU();

    // glu.gluPerspective(40.0,1.5,50.0,100.0);
    // glu.gluLookAt(-100, 100, 100, 100, 100, 25, 0, 1, 0);

    // qglClearColor(trolltechPurple.dark());
    makeObject();
    setRemakeObject(true);
    this.gl.glShadeModel(GL.GL_FLAT);
    this.gl.glEnable(GL.GL_DEPTH_TEST);
    // this.gl.glEnable(GL.GL_CULL_FACE);
    
    // Lights parameters
    
    /*this.gl.glShadeModel(GL.GL_SMOOTH);
    this.gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
    this.gl.glEnable(GL.GL_LIGHTING);
    this.gl.glEnable(GL.GL_LIGHT0);
    
    FloatBuffer l0dif = FloatBuffer.wrap(new float[] {0.3f,0.3f,0.8f});
    FloatBuffer l0pos = FloatBuffer.wrap(new float[] {0f,0f,0f});
    FloatBuffer l1dif = FloatBuffer.wrap(new float[] {0.5f,0.5f,0.5f});
    FloatBuffer mSpec = FloatBuffer.wrap(new float[] {0.5f,0.5f,0.5f});*/
    

    //this.gl.glEnable(GL.GL_LIGHT1);
    //this.gl.glLightfv(GL.GL_LIGHT0,GL.GL_POSITION, l0dif);
    /*this.gl.glLightfv(GL.GL_LIGHT0,GL.GL_DIFFUSE, l0dif);
    this.gl.glLightfv(GL.GL_LIGHT0,GL.GL_SPECULAR, l0dif);
    this.gl.glLightfv(GL.GL_LIGHT1,GL.GL_DIFFUSE, l1dif);
    this.gl.glLightfv(GL.GL_LIGHT1,GL.GL_SPECULAR, l1dif);*/

    // Materials
    
    //this.gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, mSpec);
    //this.gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, 50);
    
  }

  public void resizeGL(int width, int height) {

    int side = width <= height ? width : height; // qMin(width, height);
    this.gl.glViewport((width - side) / 2, (height - side) / 2, side, side);

    this.gl.glMatrixMode(GL.GL_PROJECTION);
    this.gl.glLoadIdentity();
    // this.gl.glOrtho(-zoom, +zoom, +zoom, -zoom, 4.0, 10.0);
    this.gl.glOrtho(-zoom, +zoom, +zoom, -zoom, -1000, 1000);
    // this.glu.gluPerspective(45.0f, 1.0f, 0.2f, 255.0f);
    this.gl.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void paintGL() {

    this.gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    this.gl.glLoadIdentity();
    this.gl.glTranslated(0.0, 0.0, -10.0);
    this.gl.glRotated(xRot / 16.0, 1.0, 0.0, 0.0);
    this.gl.glRotated(yRot / 16.0, 0.0, 1.0, 0.0);
    this.gl.glRotated(zRot / 16.0, 0.0, 0.0, 1.0);

    if (isRemakeObject()) {
      makeObject();
      this.remakeObject = false;
      // setOkToDraw(false);
    }

    this.gl.glCallList(this.gllist);

    this.gl.glMatrixMode(GL.GL_PROJECTION);
    this.gl.glLoadIdentity();
    // this.gl.glOrtho(-zoom, +zoom, +zoom, -zoom, 4.0, 15.0);
    this.gl.glOrtho(-zoom, +zoom, +zoom, -zoom, -1000, 1000);
    // this.glu.gluPerspective(45.0f, 1.0f, 0.2f, 255.0f);
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

  /*
   * public QSize minimumSizeHint() { return new QSize(50, 50); } public QSize
   * sizeHint() { return new QSize(400, 400); }
   */

  //
  // Other methods
  //
 
  
  private void makeObject() {

    if (this.gl == null)
      return;

    setRemakeObject(false);

    if (gllist > 0)
      this.gl.glDeleteLists(gllist, 1);
    int list = this.gl.glGenLists(++gllist);

    this.gl.glNewList(list, GL.GL_COMPILE);

    // point(0f, 0f, 0f, 0.01f);

    System.out.println("makeObject(" + System.currentTimeMillis() + ")");

   /* this.messengersColor = this.settings.getColorMessengers();
    this.mitosColor = this.settings.getColorMitos();
    this.barycenterColor= this.settings.getColorBaryCenters();
    this.distanceColor = this.settings.getColorDistances();
    this.backgroundColor = this.settings.getColorBackground();*/
    
    CorsenJOGL cjogl = new CorsenJOGL(this.gl, this);
    clearGL();
    
    
    

    final CorsenResult r = getResult();

    System.out.println("draw...");

    if (r != null) {

      if (r.getMessengersParticles() != null) {

        cjogl.drawLegend(r.getMessengersParticles().getUnitOfLength());

        if (isDrawMessengersCuboids())
          cjogl.drawParticles(r.getMessengersParticles(), getMessengersColor(),
              isDrawBaryCenter(), getBarycenterColor());
        else
          cjogl.drawParticles(r.getCuboidsMessengersParticles(),
              getMessengersColor(), isDrawBaryCenter(), getBarycenterColor());
      }

      if (r.getMitosParticles() != null) {

        if (isDrawMitosCuboids())
          cjogl.drawParticles(r.getMitosParticles(), getMitosColor(), false,
              null);
        else
          cjogl.drawParticles(r.getCuboidsMitosParticles(), getMitosColor(),
              false, null);
      }

      if (isDrawDistances() && r.getMinDistances() != null)
        cjogl.drawDistances(r.getMinDistances(), getDistanceColor());

      // 
      // cjogl.drawParticles(this.mitoParticles, Color.RED, true, Color.BLUE);
    } else
      cjogl.drawLegend("");

    this.gl.glEndList();

    this.gllist = list;

  }

  private int normalizeAngle(int angle) {
    while (angle < 0)
      return angle += 360 * 16;
    while (angle > 360 * 16)
      return angle -= 360 * 16;

    return angle;
  }

  //
  // Constructor
  //

  /**
   * Fake constructor. With this constructor, there is no problems with
   * QtDesigner.
   */
  public ViewOGL(final QWidget widget) {

  }

}