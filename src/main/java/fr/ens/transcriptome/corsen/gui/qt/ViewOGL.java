package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.Color;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QWheelEvent;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.opengl.QGLWidget;

import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.model.SimplePoint3DImpl;

public class ViewOGL extends QGLWidget {

  private GL gl;
  private GLU glu;
  private GLUT glut;

  private CorsenResult result;
  private Settings settings;
  private boolean drawNoMessengers;
  private boolean drawMessengersCuboids;
  private boolean drawNoMitos;
  private boolean drawMitosCuboids;
  private boolean drawBaryCenter;
  private boolean drawDistances;

  private boolean remakeObject = true;
  private int gllist = -1;

  private Color backgroundColor = Color.black;

  // private QColor trolltechPurple = QColor.fromCmykF(0.39, 0.39, 0.0, 0.0);

  private int xRot = 0;

  private int yRot = 0;

  private int zRot = 0;

  private double zoom = 100; //1000; // 0.5;

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
   * Test if the particles can be drawed
   * @return Returns the remakeObject
   */
  public boolean isRemakeObject() {
    return remakeObject;
  }

  /**
   * Get if messengers must be drawn.
   * @return Returns the drawNoMessengers
   */
  public boolean isDrawNoMessengers() {
    return drawNoMessengers;
  }

  /**
   * Get if mitos must be drawn.
   * @return Returns the drawNoMitos
   */
  public boolean isDrawNoMitos() {
    return drawNoMitos;
  }

  //
  // Setters
  //

  /**
   * Set the settings.
   * @param settings settings to set
   */
  public void setSettings(final Settings settings) {

    this.settings = settings;
  }

  /**
   * Set if the messenges must be drawn.
   * @param drawNoMessengers The drawNoMessengers to set
   */
  public void setDrawNoMessengers(boolean drawNoMessengers) {
    this.drawNoMessengers = drawNoMessengers;
  }

  /**
   * Set if the mitos must be drawn.
   * @param drawNoMitos The drawNoMitos to set
   */
  public void setDrawNoMitos(boolean drawNoMitos) {
    this.drawNoMitos = drawNoMitos;
  }

  /**
   * Set the corsen result
   * @param result The result to set
   */
  public void setResult(final CorsenResult result) {
    this.result = result;
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

    qglClearColor(new QColor(color.getRed(), color.getGreen(), color.getRed(),
        color.getAlpha()));
  }

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

    
    /*gl.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_CULL_FACE);
    gl.glShadeModel(GL.GL_FLAT);
    gl.glDisable(GL.GL_NORMALIZE);
    gl.glEnable(GL.GL_COLOR_MATERIAL);
    gl.glEnable(GL.GL_LIGHTING);
    gl. glEnable(GL.GL_LIGHT0);

    
    gl.glLightfv( GL.GL_LIGHT0, GL.GL_AMBIENT,  FloatBuffer.wrap(new float[] {.3f, .3f, .3f, .3f}) );
    gl.glLightfv( GL.GL_LIGHT0, GL.GL_DIFFUSE,  FloatBuffer.wrap(new float[] {.8f, .8f, .8f, .8f}) );
    gl.glLightfv( GL.GL_LIGHT0, GL.GL_SPECULAR, FloatBuffer.wrap(new float[] {1, 1, 1, 1}) );*/
    
    
    
    FloatBuffer mat_specular = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer mat_shininess = FloatBuffer.wrap(new float[] {50});
    

    FloatBuffer white_light = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer lmodel_ambient = FloatBuffer.wrap(new float[] {0.9f, 0.9f,
        0.9f, 1.0f});

    //FloatBuffer light_ambient = FloatBuffer.wrap(new float[] {.9f, .9f, .9f, 1});
    FloatBuffer light_ambient = FloatBuffer.wrap(new float[] {.3f, .3f, .3f, 3f});
    FloatBuffer light_diffuse = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer light_specular = FloatBuffer.wrap(new float[] {1, 1, 1, 1});
    FloatBuffer light_position0 = FloatBuffer.wrap(new float[] {1, 1, 1, 0});
    FloatBuffer light_position1 = FloatBuffer.wrap(new float[] {-1, -1, -1, 0});

    gl.glClearColor(0, 0, 0, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position0);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, white_light);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, white_light);

    gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, light_specular);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position0);

    gl.glLightf(GL.GL_LIGHT0, GL.GL_LINEAR_ATTENUATION, 1.0f);
    gl.glLightf(GL.GL_LIGHT0, GL.GL_CONSTANT_ATTENUATION, 2.0f);
    
    


    
    
    
    gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, light_position1);
    gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, white_light);
    gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, white_light);

    gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, light_ambient);
    gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, light_diffuse);
    gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, light_specular);
    gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, light_position1);

    gl.glLightf(GL.GL_LIGHT1, GL.GL_LINEAR_ATTENUATION, 1.0f);
    gl.glLightf(GL.GL_LIGHT1, GL.GL_CONSTANT_ATTENUATION, 2.0f);
    
    

    gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);

    gl.glEnable(GL.GL_LIGHTING);
    gl.glEnable(GL.GL_LIGHT0);
    gl.glEnable(GL.GL_LIGHT1);
    gl.glEnable(GL.GL_DEPTH_TEST);

    
    
    makeObject();
    setRemakeObject(true);
    //this.gl.glShadeModel(GL.GL_FLAT);
    //this.gl.glEnable(GL.GL_DEPTH_TEST);
    
    
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

    // int side = width <= height ? width : height; // qMin(width, height);
    int side = width <= height ? height : width; // qMin(width, height);
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

    this.gl.glMatrixMode(GL.GL_MODELVIEW);
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
    CorsenJOGL cjogl = new CorsenJOGL(this.gl, this);

    
    
    // point(0f, 0f, 0f, 0.01f);

    System.out.println("makeObject(" + System.currentTimeMillis() + ")");

    /*
     * this.messengersColor = this.settings.getColorMessengers();
     * this.mitosColor = this.settings.getColorMitos(); this.barycenterColor=
     * this.settings.getColorBaryCenters(); this.distanceColor =
     * this.settings.getColorDistances(); this.backgroundColor =
     * this.settings.getColorBackground();
     */

    
    clearGL();

    final CorsenResult r = getResult();
    final Settings s = this.settings;

    if (r != null) {

      this.gl.glColor3d(100, 0, 100);
     

      if (r.getMessengersParticles() != null) {

        final Particles3D pars = r.getMessengersParticles();
        gl.glTranslated(-1 * pars.getWidth() * pars.getPixelWidth() / 2, -1
            * pars.getHeight() * pars.getPixelHeight() / 2, -1
            * pars.getZSlices() * pars.getPixelDepth() / 2);
        //this.gl.glLoadIdentity();

        cjogl.drawLegend(r.getMessengersParticles().getUnitOfLength());

      }

      if (r.getMessengersParticles() != null && !isDrawNoMessengers()) {

        if (!isDrawMessengersCuboids())
          cjogl.drawParticles(r.getMessengersParticles(), s
              .getColorParticlesA(), isDrawBaryCenter(), s
              .getColorBaryCenters(), s
              .isVisualisationParticlesAInDifferentsColor());
        else {
          cjogl.drawParticles(r.getCuboidsMessengersParticles(), s
              .getColorParticlesA(), isDrawBaryCenter(), s
              .getColorBaryCenters(), s
              .isVisualisationParticlesAInDifferentsColor());
        }
      }

      if (r.getMitosParticles() != null && !isDrawNoMitos()) {

        if (!isDrawMitosCuboids())
          cjogl.drawParticles(r.getMitosParticles(), s.getColorParticlesB(),
              false, null, s.isVisualisationParticlesBInDifferentsColor());
        else
          cjogl.drawParticles(r.getCuboidsMitosParticles(), s
              .getColorParticlesB(), false, null, s
              .isVisualisationParticlesBInDifferentsColor());
      }

      if (isDrawDistances() && r.getMinDistances() != null)
        cjogl.drawDistances(r.getMinDistances(), s.getColorDistances(), s
            .isVisualizationShowNegativeDistances());

      // 
      // cjogl.drawParticles(this.mitoParticles, Color.RED, true, Color.BLUE);
    } else {
      cjogl.drawLegend("");

    }

    /*this.gl.glLoadIdentity();
    Particles3D pars = r.getMessengersParticles();
    if (pars == null)
      pars = r.getMitosParticles();

    if (pars != null)
      gl.glTranslated(-1 * pars.getWidth() * pars.getPixelWidth() / 2, -1
          * pars.getHeight() * pars.getPixelHeight() / 2, -1
          * pars.getZSlices() * pars.getPixelDepth() / 2); */

    
    

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

  public QSize sizeHint() {

    int width = size().width();
    int height = size().height();

    int side = width <= height ? width : height;

    return new QSize(side, side);

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