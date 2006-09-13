import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RParticle3DWriter {

  private Particle3D particle;
  private Writer writer;
  private String colorName;
  private float size;

  private class Triangle {

    private Point3D[] points = new Point3D[3];
    private String colorName;

    //
    // Setters
    // 

    public void setPoint1(Point3D p) {
      this.points[0] = p;
    }

    public void setPoint2(Point3D p) {
      this.points[1] = p;
    }

    public void setPoint3(Point3D p) {
      this.points[2] = p;
    }

    public void setColorName(String colorName) {
      this.colorName = colorName;
    }

    //
    // Other methods
    //

    public int hashCode() {

      int iConstant = 37;
      int iTotal = 17;

      Point3D[] sortedPoints = new Point3D[3];
      sortedPoints[0] = points[0];
      sortedPoints[1] = points[1];
      sortedPoints[2] = points[2];
      Arrays.sort(sortedPoints);

      for (int i = 0; i < sortedPoints.length; i++) {

        iTotal = iTotal * iConstant
            + Float.floatToIntBits(sortedPoints[i].getX());
        iTotal = iTotal * iConstant
            + Float.floatToIntBits(sortedPoints[i].getY());
        iTotal = iTotal * iConstant
            + Float.floatToIntBits(sortedPoints[i].getZ());
        iTotal = iTotal * iConstant + sortedPoints[i].getI();

      }

      return iTotal;
    }

    public String toRCommandString() {

      if (points[0] == null || points[1] == null || points[2] == null)
        return "";

      StringBuffer sb = new StringBuffer();

      sb.append("x <- c(");
      sb.append(points[0].getX());
      sb.append(",");
      sb.append(points[1].getX());
      sb.append(",");
      sb.append(points[2].getX());
      sb.append(")\n");

      sb.append("y <- c(");
      sb.append(points[0].getY());
      sb.append(",");
      sb.append(points[1].getY());
      sb.append(",");
      sb.append(points[2].getY());
      sb.append(")\n");

      sb.append("z <- c(");
      sb.append(points[0].getZ());
      sb.append(",");
      sb.append(points[1].getZ());
      sb.append(",");
      sb.append(points[2].getZ());
      sb.append(")\n");

      sb.append("rgl.triangles(x, y, z");
      if (this.colorName != null) {
        sb.append(",color=\"");
        sb.append(this.colorName);
        sb.append("\"");
      }
      sb.append(")\n");

      return sb.toString();
    }

    
    
    //
    // Constructor
    //

    public Triangle(final Point3D p1, final Point3D p2, final Point3D p3,
        final String colorName) {

      setPoint1(p1);
      setPoint2(p2);
      setPoint3(p3);
      setColorName(colorName);
    }

  }

  //
  // Getters
  //

  /**
   * Get the particle3D.
   * @return Returns the particle
   */
  public Particle3D getParticle() {
    return this.particle;
  }

  /**
   * Get the name of the color.
   * @return Returns the colorName
   */
  public String getColorName() {
    return this.colorName;
  }

  /**
   * Get the writer
   * @return Returns the writer
   */
  public Writer getWriter() {
    return this.writer;
  }

  /**
   * Get the draw size
   * @return Returns the size
   */
  public float getSize() {
    return this.size;
  }

  //
  // Setters
  //

  /**
   * Set the particle3D
   * @param particle The particle to set
   */
  public void setParticle(final Particle3D particle) {
    this.particle = particle;
  }

  /**
   * Set the color name.
   * @param colorName The colorName to set
   */
  public void setColorName(final String colorName) {
    this.colorName = colorName;
  }

  /**
   * Set the writer.
   * @param writer The writer to set
   */
  public void setWriter(final Writer writer) {
    this.writer = writer;
  }

  /**
   * Set the draw size
   * @param size The size to set
   */
  public void setSize(final float size) {
    this.size = size;
  }

  //
  // Other methods
  //

  /**
   * Generate R code to plot the surface points
   * @param out Writer used to write data
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @throws IOException if an error occurs while writing data
   */
  public void surfacePointstoR() throws IOException {

    final Writer out = this.writer;

    if (out == null)
      return;

    if (this.particle.surfacePointsCount() == 0)
      return;

    final Map slices = Particle3D.getSurfacePointSlices(this.particle);

    final Object[] keys = slices.keySet().toArray();
    Arrays.sort(keys);

    Particle2D previousPar = null;
    float previousZ = 0;

    for (int i = 0; i < keys.length; i++) {

      final Float zF = (Float) keys[i];
      final float z = zF.floatValue();

      final Particle2D par = (Particle2D) slices.get(zF);
      final int n = par.surfacePointsCount();

      if (par.surfacePointsCount() == 0)
        continue;

      out.write("x <- c(");

      for (int j = 0; j < n; j++) {
        final Point2D p;
        if (j > 0)
          out.write(",");
        p = par.getSurfacePoint(j);
        out.write("" + p.getX());
      }

      out.write(")\ny <- c(");
      for (int j = 0; j < n; j++) {
        if (j > 0)
          out.write(",");
        final Point2D p;

        p = par.getSurfacePoint(j);
        out.write("" + p.getY());
      }

      out.write(")\nz <- c(");
      for (int j = 0; j < n + 1; j++) {
        if (j > 0)
          out.write(",");
        out.write(zF.toString());
      }

      out.write(")\n");

      out.write("lines3d(x, y, z, size=" + this.size);
      if (this.colorName != null) {
        out.write(",color=\"");
        out.write(this.colorName);
        out.write("\"");
      }
      out.write(")\n");

      previousPar = par;
      previousZ = z;

    }
  }

  public void writeTriangleSurface() throws IOException {

    final Writer out = this.writer;

    if (out == null)
      return;

    final Map slices = Particle3D.getSurfacePointSlices(this.particle);

    final Object[] keys = slices.keySet().toArray();
    Arrays.sort(keys);

    Set triangles = new HashSet();

    Particle2D previousPar = null;
    float previousZ = 0;

    for (int i = 0; i < keys.length; i++) {

      final Float zF = (Float) keys[i];
      final float z = zF.floatValue();

      final Particle2D par = (Particle2D) slices.get(zF);

      if (previousPar != null) {

        final int nPar = par.surfacePointsCount();
        final int nPreviousPar = previousPar.surfacePointsCount();

        for (int j = 0; j < nPar; j++) {

          Point2D pPar = par.getSurfacePoint(j);

          Point2D[] points2D = previousPar
              .findNearestsSurfacePoints(pPar, 5.0f);
          for (int k = 1; k < points2D.length; k++) {

            Point3D p1 = new SimplePoint3DImpl(pPar, z);
            Point3D p2 = new SimplePoint3DImpl(points2D[k - 1], previousZ);
            Point3D p3 = new SimplePoint3DImpl(points2D[k], previousZ);
            
            Triangle t = new Triangle(p1, p2, p3, this.colorName);

            triangles.add(t);
          }
        }

        for (int j = 0; j < nPreviousPar; j++) {

          Point2D pPreviousPar = previousPar.getSurfacePoint(j);

          Point2D[] points2D = par
              .findNearestsSurfacePoints(pPreviousPar, 5.0f);
          for (int k = 1; k < points2D.length; k++) {
            Triangle t = new Triangle(new SimplePoint3DImpl(pPreviousPar,
                previousZ), new SimplePoint3DImpl(points2D[k - 1], z),
                new SimplePoint3DImpl(points2D[k], z), this.colorName);

            triangles.add(t);
          }
        }

      }

      previousPar = par;
      previousZ = z;
    }

    Iterator it = triangles.iterator();
    while (it.hasNext()) {

      Triangle t = (Triangle) it.next();
      out.write(t.toRCommandString());
    }

  }

  //
  // Constructor
  //

  /**
   * Constructor.
   * @param particle Particle to handle
   * @param out the writer
   * @param colorName the colro name
   */
  public RParticle3DWriter(final Particle3D particle, final Writer out,
      final String colorName) {

    setParticle(particle);
    setWriter(out);
    setColorName(colorName);
  }

}
