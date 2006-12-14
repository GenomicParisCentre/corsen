package fr.ens.transcriptome.corsen;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.calc.Particles3D;
import fr.ens.transcriptome.corsen.model.Particle2D;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Point2D;
import fr.ens.transcriptome.corsen.model.Point3D;
import fr.ens.transcriptome.corsen.model.SimplePoint3DImpl;

public class RGL {

  private Writer out;
  private String unit = "pixel";

  public static final String COLOR_CYAN = "cyan";
  public static final String COLOR_GREEN = "green";
  public static final String COLOR_RED = "red";
  public static final String COLOR_WHITE = "white";
  public static final String COLOR_BLACK = "black";

  //
  // Getters
  //

  /**
   * Get the writer.
   * @return Returns the writer object
   */
  public Writer getWriter() {
    return out;
  }

  /**
   * Get the unit
   * @return Returns the unit
   */
  public String getUnit() {
    return unit;
  }

  //
  // Setters
  //

  /**
   * Set the writer.
   * @param out The out to set
   */
  public void setWriter(final Writer writer) {
    this.out = writer;
  }

  /**
   * Set the file to write
   * @param file File to write
   * @throws FileNotFoundException if the file is not found
   */
  public void setFile(final File file) throws FileNotFoundException {

    final FileOutputStream fos = new FileOutputStream(file);
    setWriter(new OutputStreamWriter(fos));
  }

  /**
   * Set the file to write
   * @param file File to write
   * @throws FileNotFoundException if the file is not found
   */
  public void setFile(final String file) throws FileNotFoundException {

    setFile(new File(file));
  }

  /**
   * Set the unit
   * @param unit The unit to set
   */
  public void setUnit(String unit) {
    if (unit != null)
      this.unit = unit;
  }

  //
  // Other methods
  //

  /**
   * Write header of the file.
   * @throws IOException if an error occurs while closing the file
   */
  private void writeHeader() throws IOException {

    final Writer out = this.out;

    if (out == null)
      throw new IOException("The Writer is null");

    out.write("# Generated by ");
    out.write(Globals.APP_NAME);
    out.write(" version ");
    out.write(Globals.APP_VERSION);
    out.write("\n\n# Load rgl\n");

    out
        .write("if (length( grep(\"package:rgl\",search()) )==0) { library(rgl) }\n\n");

    out
        .write("rgl.bg(color=c(\"" + COLOR_BLACK
            + "\"), fogtype=\"linear\" )\n");

    writeLegend(getUnit());
  }

  /**
   * Draw the legend
   * @param unit Unit of the legend
   * @throws IOException
   */
  public void writeLegend(final String unit) throws IOException {

    // final Writer out = this.out;

    Point3D o = new SimplePoint3DImpl(0, 0, 0);
    Point3D x = new SimplePoint3DImpl(10, 0, 0);
    Point3D y = new SimplePoint3DImpl(0, 10, 0);
    Point3D z = new SimplePoint3DImpl(0, 0, 10);

    writeLine(o, x, COLOR_WHITE);
    writeLine(o, y, COLOR_WHITE);
    writeLine(o, z, COLOR_WHITE);

    String text = "10 " + unit;

    out.write("text3d(x=-1,y=-1,z=-1,text=\"" + text + "\",color=\""
        + COLOR_WHITE + "\")\n");

  }

  /**
   * Write the lines of distances.
   * @param distances Distances to draw
   * @param color Color of the lines
   * @throws IOException if an error occurs while writing the distances
   */
  public void writeDistances(final Map<Particle3D, Distance> distances,
      final String color) throws IOException {

    if (distances != null) {

      Iterator<Particle3D> it = distances.keySet().iterator();

      while (it.hasNext()) {

        Distance d = distances.get(it.next());

        writeLine(d.getPointA(), d.getPointB(), color);
      }
    }

    close();
  }

  /**
   * Close the file
   * @throws IOException if an error occurs while closing the file
   */
  public void close() throws IOException {

    this.out.close();
  }

  /**
   * Write R plot for an array of particle3D
   * @param file Output file
   * @param particles particles to plot
   * @param color color of the plot
   * @param plotCenter true if center and barycenter must be plot
   * @throws IOException if an error occurs while writing file
   */
  public void writeRPlots(final Particles3D particles, final String color,
      final boolean plotCenter) throws IOException {

    writeRPlots(particles.getParticles(), color, plotCenter, particles
        .getPixelWidth());
    close();
  }

  /**
   * Write R plot for an array of particle3D
   * @param file Output file
   * @param particles particles to plot
   * @param color color of the plot
   * @param plotCenter true if center and barycenter must be plot
   * @throws IOException if an error occurs while writing file
   */
  public void writeRPlots(final List<Particle3D> particles, final String color,
      final boolean plotCenter, final float minDistanceBetweenTwoPoints)
      throws IOException {

    if (particles == null)
      return;

    final Writer out = this.out;

    Random random = new Random(System.currentTimeMillis());

    for (Particle3D par: particles) {
    //for (int i = 0; i < particles.length; i++) {

      out.write("#\n# Particle #");
      out.write("" + par.getId());
      out.write(" (");
      out.write(par.getName());
      out.write(")\n#\n\n");

      innerPointstoRData(par);

      final float size = (float) (minDistanceBetweenTwoPoints * 1.0);

      out.write("\nplotColor <- \"");
      out.write(color);
      out.write("\"\n");

      out
          .write("\nif (exists(\"corsen.unicolor\") && corsen.unicolor==F ) {\nplotColor <- \"#");
      Color c = new Color(random.nextInt(255), random.nextInt(255), random
          .nextInt(255));

      out.write(Integer.toHexString(c.getRGB()));

      out.write("\"\n}\n");

      out.write("\nif ( exists(\"corsen.shift\") && corsen.shift>0 ) {\n");

      out.write("print(x)\n");
      out.write("x <- x + corsen.shift\n");
      out.write("y <- y + corsen.shift\n");
      out.write("z <- z + corsen.shift\n");

      out.write("}\n");

      out
          .write("\nif (!exists(\"corsen.sizepoints\")) {\n corsen.sizepoints <- ");
      out.write("" + size);
      out.write("\n}\n");

      out.write("\nif (!exists(\"corsen.points3d\") "
          + "|| ( exists(\"corsen.points3d\") && corsen.points3d==T )) {\n");

      String plotColor = "plotColor";
      innerPointstoRPlot(false, "corsen.sizepoints", plotColor, true);
      /*
       * out.write("z <- z - "); out.write("" + size); out.write("\n");
       * innerPointstoRPlot(false, "corsen.sizepoints", color); out.write("z <-
       * z +"); out.write("" + size * 2); out.write("\n");
       * innerPointstoRPlot(false, "corsen.sizepoints", color);
       */

      out.write("} else {\n");

      innerPointstoRPlot(true, "corsen.sizepoints", plotColor, true);
      out.write("}\n");

      out
          .write("\nif (!exists(\"corsen.surfaces3d\") "
              + "|| ( exists(\"corsen.surfaces3d\") && corsen.surfaces3d==T )) {\n");

      // surfacePointstoR(particles[i], 1.0f, "dark" + plotColor);
      surfacePointstoR(par, 1.0f, plotColor, true);

      // RParticle3DWriter rpw = new RParticle3DWriter(particles[i],out,"red");
      // rpw.writeTriangleSurface();

      out.write("}\n\n");

      // out.write(particles[i].surfacePointLinestoR( 5.0f, "blue"));
      if (plotCenter) {

        out.write("if (exists(\"corsen.centers\") && corsen.centers==T) {\n");

        writePoint(par.getSurfacePoints().getCenter(), 5, "darkblue");

        out.write("}\n\n");

        out
            .write("if (!exists(\"corsen.barycenters\") || (exists(\"corsen.barycenters\") && corsen.barycenters==T)) {\n");

        writePoint(par.getInnerPoints().getBarycenter(), 5, "blue");

        out.write("}\n");
      }

      out.write("\n");

    }
  }

  /**
   * Generate R code to plot the inners points
   * @param out Writer used to write data
   * @param sphere plot spheres
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @param colorNameVariable if the color name is a R variable
   * @throws IOException if an error occurs while write data
   */
  public void innerPointstoRPlot(final boolean sphere, final String size,
      final String colorName, final boolean colorNameVariable)
      throws IOException {

    final Writer out = this.out;

    if (sphere)
      out.write("rgl.spheres(x, y, z, r=" + size);
    else
      out.write("points3d(x, y, z, size=" + size);

    if (colorName != null) {

      out.write(",color=");
      if (!colorNameVariable)
        out.write("\"");
      out.write(colorName);
      if (!colorNameVariable)
        out.write("\"");
    }
    out.write(")\n");

  }

  /**
   * Generate R code to plot the inners points
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @return A String with the R code to plot the points
   */
  /*
   * public String innerPointstoR(final float size, final String colorName) {
   * final int n = innerPointsCount(); if (n == 0) return ""; StringBuffer sb =
   * new StringBuffer(); sb.append("x <- c("); for (int i = 0; i < n; i++) { if
   * (i > 0) sb.append(","); final Point3D p; p = getInnerPoint(i);
   * sb.append(p.getX()); } sb.append(")\ny <- c("); for (int i = 0; i < n; i++) {
   * if (i > 0) sb.append(","); final Point3D p; p = getInnerPoint(i);
   * sb.append(p.getY()); } sb.append(")\nz <- c("); for (int i = 0; i < n; i++) {
   * if (i > 0) sb.append(","); final Point3D p; p = getInnerPoint(i);
   * sb.append(p.getZ()); } sb.append(")\n"); sb.append("points3d(x, y, z,
   * size="); sb.append(size); if (colorName != null) { sb.append(",color=\"");
   * sb.append(colorName); sb.append("\""); } sb.append(")\n"); return
   * sb.toString(); }
   */

  /**
   * Generate R code to plot the inners points
   * @param par Particle
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @return A String with the R code to plot the points
   */
  public String surfacePointLinestoR(final Particle3D par, final float size,
      final String colorName) {

    final int n = par.surfacePointsCount();

    if (n == 0)
      return "";

    final StringBuffer sb = new StringBuffer();

    sb.append("x <- c(");

    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(",");

      final Point3D p;

      p = par.getSurfacePoint(i);

      sb.append(p.getX());
    }

    sb.append(")\ny <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(",");

      final Point3D p;

      p = par.getSurfacePoint(i);

      sb.append(p.getY());
    }

    sb.append(")\nz <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        sb.append(",");

      final Point3D p;

      p = par.getSurfacePoint(i);

      sb.append(p.getZ());
    }

    sb.append(")\n");
    sb.append("points3d(x, y, z, size=");
    sb.append(size);
    if (colorName != null) {
      sb.append(",color=\"");
      sb.append(colorName);
      sb.append("\"");
    }
    sb.append(")\n");

    return sb.toString();
  }

  /**
   * Generate R code to plot the surface points
   * @param par Particle to write
   * @param size Size of the points
   * @param colorName Name of the color of the points
   * @param colorNameVariable if the color name is a R variable
   * @throws IOException if an error occurs while writing data
   */
  public void surfacePointstoR(final Particle3D par, final float size,
      final String colorName, final boolean colorNameVariable)
      throws IOException {

    final Writer out = this.out;

    if (par.surfacePointsCount() == 0)
      return;

    final Map slices = Particle3D.getSurfacePointSlices(par);

    final Object[] keys = slices.keySet().toArray();
    Arrays.sort(keys);

    for (int i = 0; i < keys.length; i++) {

      final Float zF = (Float) keys[i];

      final Particle2D par2d = (Particle2D) slices.get(zF);
      final int n = par2d.surfacePointsCount();

      if (par2d.surfacePointsCount() == 0)
        continue;

      out.write("x <- c(");

      for (int j = 0; j < n; j++) {
        final Point2D p;
        if (j > 0)
          out.write(",");
        p = par2d.getSurfacePoint(j);
        out.write("" + p.getX());
      }

      out.write(")\ny <- c(");
      for (int j = 0; j < n; j++) {
        if (j > 0)
          out.write(",");
        final Point2D p;

        p = par2d.getSurfacePoint(j);
        out.write("" + p.getY());
      }

      out.write(")\nz <- c(");
      for (int j = 0; j < n + 1; j++) {
        if (j > 0)
          out.write(",");
        out.write(zF.toString());
      }

      out.write(")\n");

      out.write("lines3d(x, y, z, size=" + size);
      if (colorName != null) {
        out.write(",color=");
        if (!colorNameVariable)
          out.write(",color=");
        out.write(colorName);
        if (!colorNameVariable)
          out.write("\"");
      }
      out.write(")\n");

    }

  }

  /**
   * Generate R code to plot the inners points
   * @param par Particle to write
   * @throws IOException if an error occurs while write data
   */
  public void innerPointstoRData(final Particle3D par) throws IOException {

    if (out == null)
      return;

    final int n = par.innerPointsCount();

    if (n == 0)
      return;

    out.write("x <- c(");

    for (int i = 0; i < n; i++) {
      if (i > 0)
        out.write(",");

      final Point3D p;

      p = par.getInnerPoint(i);

      out.write("" + p.getX());
    }

    out.write(")\ny <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        out.write(",");

      final Point3D p;

      p = par.getInnerPoint(i);

      out.write("" + p.getY());
    }

    out.write(")\nz <- c(");
    for (int i = 0; i < n; i++) {
      if (i > 0)
        out.write(",");

      final Point3D p;

      p = par.getInnerPoint(i);

      out.write("" + p.getZ());
    }

    out.write(")\n");

  }

  /**
   * Write the RGL plot distance
   * @param p1 first point
   * @param p2 second point
   * @param color color
   * @throws IOException
   */
  public void writeLine(final Point3D p1, final Point3D p2, final String color)
      throws IOException {

    StringBuffer sb = new StringBuffer();

    sb.append("x <- c(");
    sb.append(p1.getX());
    sb.append(",");
    sb.append(p2.getX());
    sb.append(")\n");

    sb.append("y <- c(");
    sb.append(p1.getY());
    sb.append(",");
    sb.append(p2.getY());
    sb.append(")\n");

    sb.append("z <- c(");
    sb.append(p1.getZ());
    sb.append(",");
    sb.append(p2.getZ());
    sb.append(")\n");

    sb.append("lines3d(x, y, z, size=3.0,color=\"");
    sb.append(color);
    sb.append("\")\n");

    this.out.write(sb.toString());
  }

  /**
   * Generate R code to plot the point
   * @param p Point to draw
   * @param size Size of the points
   * @param colorName Name of the color of the point
   * @throws IOException if an error occurs while writing the point
   */
  public void writePoint(final Point3D p, final float size,
      final String colorName) throws IOException {

    final StringBuffer sb = new StringBuffer();

    sb.append("x <- c(");
    sb.append(p.getX());
    sb.append(")\ny <- c(");
    sb.append(p.getY());
    sb.append(")\nz <- c(");
    sb.append(p.getZ());

    sb.append(")\n");
    sb.append("points3d(x, y, z, size=");
    sb.append(size);
    if (colorName != null) {
      sb.append(",color=\"");
      sb.append(colorName);
      sb.append("\"");
    }
    sb.append(")\n");

    out.write(sb.toString());
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param unit of the 3d view
   * @param file File to write
   * @throws IOException if an error occurs while writing the header
   */
  public RGL(String unit, final File file) throws IOException {

    setFile(file);
    setUnit(unit);
    writeHeader();
  }

  /**
   * Public constructor.
   * @param file File to write
   * @param suffix Suffix of the file to write
   * @throws IOException if an error occurs while writing the header
   */
  public RGL(final File file, final String suffix) throws IOException {

    this(null, CorsenResultWriter.createFileWithSuffix(file, suffix));
  }

  /**
   * Public constructor.
   * @param file File to write
   * @param unit of the 3d view
   * @param suffix Suffix of the file to write
   * @throws IOException if an error occurs while writing the header
   */
  public RGL(final String unit, final File file, final String suffix)
      throws IOException {

    this(unit, CorsenResultWriter.createFileWithSuffix(file, suffix));
  }

}
