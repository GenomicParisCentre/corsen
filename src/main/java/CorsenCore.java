import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorsenCore implements Runnable {

  private float maxX = -1;
  private float maxY = -1;
  private float maxZ = -1;
  private float zCoef;
  private float lenCube;
  private boolean updateZ;
  private float pixelSize = 1.0f;

  private File dirFiles;
  private File mitoFile;
  private File rnaFile;
  private File resultDir;
  private String resultFilename;
  private boolean multipleFiles;

  //
  // inner class
  //

  private class InputFiles {

    File inputrnaFile;
    File inputmitoFile;
  }

  //
  // Getters
  //

  /**
   * Get the Directory of the files to read
   * @return Returns the dirFiles
   */
  public File getDirFiles() {
    return dirFiles;
  }

  /**
   * Get the mito file.
   * @return Returns the mitoFile
   */
  public File getMitoFile() {
    return mitoFile;
  }

  /**
   * Get the result directory.
   * @return Returns the resultDir
   */
  public File getResultDir() {
    return resultDir;
  }

  /**
   * Get the result filename.
   * @return Returns the resultFilename
   */
  public String getResultFilename() {
    return resultFilename;
  }

  /**
   * Get the rna file.
   * @return Returns the rnaFile
   */
  public File getRnaFile() {
    return rnaFile;
  }

  /**
   * Test if there are multple files to read.
   * @return Returns the multipleFiles
   */
  public boolean isMultipleFiles() {
    return multipleFiles;
  }

  //
  // Setters
  //

  /**
   * Set the length of a cuboid.
   * @param lenCube The new length of a cuboid
   */
  public void setLenCuboid(final float lenCube) {
    this.lenCube = lenCube;
  }

  /**
   * Set the size of pixel.
   * @param pixelSize The size of a pixel
   */
  public void setPixelSize(final float pixelSize) {
    this.pixelSize = pixelSize;
  }

  /**
   * Set the X max Value of the space.
   * @param maxX The X max value of the space
   */
  public void setMaxX(final float maxX) {
    this.maxX = maxX;
  }

  /**
   * Set the Y max Value of the space.
   * @param maxY The Y max value of the space
   */
  public void setMaxY(final float maxY) {
    this.maxY = maxY;
  }

  /**
   * Set the Z max Value of the space.
   * @param maxZ The Z max value of the space
   */
  public void setMaxZ(final float maxZ) {
    this.maxZ = maxZ;
  }

  /**
   * Set if the Z coordinate must be updated
   * @param updateZ true if the Z coordinate must be updated
   */
  public void setUpdateZ(final boolean updateZ) {
    this.updateZ = updateZ;
  }

  /**
   * Set the coef of the update of the Z values.
   * @param coef the coef of the update of the Z values
   */
  public void setZCoef(final float coef) {
    zCoef = coef;
  }

  /**
   * Set the directory of the files to read.
   * @param dirFiles The dirFiles to set
   */
  public void setDirFiles(final File dirFiles) {
    this.dirFiles = dirFiles;
  }

  /**
   * Set the mito file to read.
   * @param mitoFile The mitoFile to set
   */
  public void setMitoFile(final File mitoFile) {
    this.mitoFile = mitoFile;
  }

  /**
   * Set the result directory.
   * @param resultDir The resultDir to set
   */
  public void setResultDir(final File resultDir) {
    this.resultDir = resultDir;
  }

  /**
   * Set the result filename.
   * @param resultFilename The resultFilename to set
   */
  public void setResultFilename(final String resultFilename) {
    this.resultFilename = resultFilename;
  }

  /**
   * Set the rna file.
   * @param rnaFile The rnaFile to set
   */
  public void setRnaFile(final File rnaFile) {
    this.rnaFile = rnaFile;
  }

  /**
   * Set if there are multiple files to read.
   * @param multipleFiles The multipleFiles to set
   */
  public void setMultipleFiles(final boolean multipleFiles) {
    this.multipleFiles = multipleFiles;
  }

  //
  // Other methods
  //

  /**
   * Process data for a cell
   * @param mitoFile ImageJ Plugin result file for mitochondria
   * @param rnaFile ImageJ Plugin result file for RNAm
   * @param resultDir Result file directory
   * @param resultFilename Result filename
   * @throws IOException if an error occurs while reading or writing data
   */
  private void processACell(final File mitoFile, final File rnaFile,
      final File resultDir, final String resultFilename) throws IOException {

    // Read messengers and write R plot for messengers
    sendEvent(ProgressEvent.START_READ_MESSENGERS_EVENT);
    final Particle3D[] messengers = readmageJPlugingOutputFile(rnaFile);
    final float minDistanceBetweenTwoPoints = getMinDistanceBetweenTwoPoints(messengers);
    sendEvent(ProgressEvent.START_WRITE_RPLOT_MESSENGERS_EVENT);
    File messengersFile = new File(resultDir, resultFilename + "_messengers.R");
    writeRPlots(messengersFile, messengers, "green", true,
        minDistanceBetweenTwoPoints);

    // Read mitos and write R plot for mitos
    sendEvent(ProgressEvent.START_READ_MITOS_EVENT);
    final Particle3D[] mitos = readmageJPlugingOutputFile(mitoFile);

    File mitosFile = new File(resultDir, resultFilename + "_mitos.R");
    sendEvent(ProgressEvent.START_WRITE_RPLOT_MITOS_EVENT);
    writeRPlots(mitosFile, mitos, "red", false, minDistanceBetweenTwoPoints);

    // Calc cuboids and write R plot for cuboids
    sendEvent(ProgressEvent.START_CALC_CUBOIDS_EVENT);
    final Particle3D[] cuboids = calcCuboid(messengers, this.lenCube);

    File cuboidsFile = new File(resultDir, resultFilename + "_cuboids.R");
    sendEvent(ProgressEvent.START_WRITE_RPLOT_CUBOIDS_EVENT);
    writeRPlots(cuboidsFile, cuboids, "green", true,
        minDistanceBetweenTwoPoints);

    // Write full result final
    FileOutputStream fos = new FileOutputStream(new File(resultDir,
        resultFilename + ".result"));
    Writer out = new OutputStreamWriter(fos);

    sendEvent(ProgressEvent.START_WRITE_FULLRESULT_MESSAGERS_EVENT);
    writeFullResultFile(messengers, mitos, out);

    sendEvent(ProgressEvent.START_WRITE_FULLRESULT_CUBOIDS_EVENT);
    writeFullResultFile(cuboids, mitos, out);

    out.close();

    // Write R result file
    fos = new FileOutputStream(new File(resultDir, resultFilename + ".data"));
    out = new OutputStreamWriter(fos);
    sendEvent(ProgressEvent.START_WRITE_RRESULT_CUBOIDS_EVENT);
    writeRResultFile(cuboids, mitos, minDistanceBetweenTwoPoints, out);
    // writeRResultFile(messengers, mitos, out);
    out.close();

  }

  /**
   * Count the number of points in an array of particle3D/
   * @param particles Array of particles3D
   * @return the number of points in an array of particle3D
   */
  private int countPoints(final Particle3D[] particles) {

    if (particles == null)
      return 0;

    int count = 0;

    for (int i = 0; i < particles.length; i++)
      count += particles[i].innerPointsCount();

    return count;
  }

  /**
   * Write data in full resut file
   * @param messengers Messengers particles
   * @param mitos Mitochondia particles
   * @param out Writer
   * @throws IOException if an error occurs while writing data
   */
  private void writeFullResultFile(final Particle3D[] messengers,
      final Particle3D[] mitos, final Writer out) throws IOException {

    long start = System.currentTimeMillis();

    out.write("messager");
    out.write("\t");
    out.write("volume");
    out.write("\t");
    out.write("intensity");
    out.write("\t");

    for (int j = 0; j < mitos.length; j++) {

      out.write("d(s,s)[");
      out.write(mitos[j].getName());
      out.write("]");
      out.write("\t");
      out.write("d(c,s)[");
      out.write(mitos[j].getName());
      out.write("]");
      out.write("\t");
      out.write("include[");
      out.write(mitos[j].getName());
      out.write("]");
      out.write("\t");
    }
    out.write("min(s,s)\tmin(c,s)\n");

    for (int i = 0; i < messengers.length; i++) {

      out.write(messengers[i].getName());
      out.write("\t");
      out.write("" + messengers[i].getVolume());
      out.write("\t");
      out.write("" + messengers[i].getIntensity());
      out.write("\t");

      double minss = java.lang.Double.MAX_VALUE;
      double mincs = java.lang.Double.MAX_VALUE;

      for (int j = 0; j < mitos.length; j++) {

        double dss = messengers[i].getSurfaceToSurfaceDistance(mitos[j]);
        double dcs = messengers[i].getBarycenterToSurfaceDistance(mitos[j]);

        if (dss < minss)
          minss = dss;
        if (dcs < mincs)
          mincs = dcs;

        out.write("" + dss);
        out.write("\t");
        out.write("" + dcs);
        out.write("\t");

        final boolean include = messengers[i].intersect(mitos[j]);
        if (include)
          out.write("1");
        else
          out.write("0");
        out.write("\t");
      }

      out.write("" + minss);
      out.write("\t");
      out.write("" + mincs);
      out.write("\n");
    }

    long end = System.currentTimeMillis();
    System.out.println("Write full result file : " + (end - start) + " ms");
  }

  /**
   * Read data from the output file of the ImageJ plugin to create a new
   * particle3D object.
   * @param file File to read
   * @return a new Particle3D created from data readed
   * @throws IOException IOException if an error occurs while readinf data
   */
  private Particle3D[] readmageJPlugingOutputFile(final File file)
      throws IOException {

    if (file == null)
      return null;

    ArrayList al = new ArrayList();

    BufferedReader in = new BufferedReader(new FileReader(file));

    String line;
    int count = 0;
    while ((line = in.readLine()) != null) {

      if (!line.startsWith("#")) {
        if (count == 0) {
          String[] ch = line.split("\t");
          if (ch.length != 4)
            throw new RuntimeException(
                "Invalid input file (Bad dimension field).");

          float maxX = Float.parseFloat(ch[1].trim());
          float maxY = Float.parseFloat(ch[2].trim());
          float maxZ = Float.parseFloat(ch[3].trim());

          if ((this.maxX != -1 && this.maxX != maxX)
              || (this.maxY != -1 && this.maxY != maxY)
              || (this.maxZ != -1 && this.maxZ != maxZ))
            throw new RuntimeException(
                "The dimensitions of the two files are not the same.");

          this.maxX = maxX;
          this.maxY = maxY;
          this.maxZ = maxZ;

        } else if (count > 1) {
          Particle3D p = new Particle3D(line);

          // Update z coords
          if (this.updateZ) {
            changeZCoord(p);
          }

          al.add(p);
        }
        count++;
      }
    }

    in.close();

    Particle3D[] array = new Particle3D[al.size()];

    al.toArray(array);

    return array;
  }

  /**
   * Write R plot for an array of particle3D
   * @param file Output file
   * @param particles particles to plot
   * @param color color of the plot
   * @param plotCenter true if center and barycenter must be plot
   * @throws IOException if an error occurs while writing file
   */
  private static void writeRPlots(final File file,
      final Particle3D[] particles, final String color,
      final boolean plotCenter, final float minDistanceBetweenTwoPoints)
      throws IOException {

    long start = System.currentTimeMillis();

    if (particles == null)
      return;

    FileOutputStream fos = new FileOutputStream(file);
    Writer out = new OutputStreamWriter(fos);

    out.write("# Generated by ");
    out.write(Corsen.APP_NAME);
    out.write(" version ");
    out.write(Corsen.APP_VERSION);
    out.write("\n\n# Load rgl\n");

    out
        .write("if (length( grep(\"package:rgl\",search()) )==0) { library(rgl) }\n\n");

    for (int i = 0; i < particles.length; i++) {

      out.write("#\n# Particle #");
      out.write("" + particles[i].getId());
      out.write(" (");
      out.write(particles[i].getName());
      out.write(")\n#\n\n");

      particles[i].innerPointstoRData(out);

      float size = (float) (minDistanceBetweenTwoPoints * 1.0);

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

      particles[i].innerPointstoRPlot(out, false, "corsen.sizepoints", color);
      out.write("z <- z - ");
      out.write("" + size);
      out.write("\n");
      particles[i].innerPointstoRPlot(out, false, "corsen.sizepoints", color);
      out.write("z <- z +");
      out.write("" + (size * 2));
      out.write("\n");
      particles[i].innerPointstoRPlot(out, false, "corsen.sizepoints", color);

      out.write("} else {\n");

      particles[i].innerPointstoRPlot(out, true, "corsen.sizepoints", color);
      out.write("}\n");

      out
          .write("\nif (!exists(\"corsen.surfaces3d\") "
              + "|| ( exists(\"corsen.surfaces3d\") && corsen.surfaces3d==T )) {\n");

      particles[i].surfacePointstoRData(out);

      out.write("\nif ( exists(\"corsen.shift\") && corsen.shift>0 ) {\n");

      out.write("print(x)\n");
      out.write("x <- x + corsen.shift\n");
      out.write("y <- y + corsen.shift\n");
      out.write("z <- z + corsen.shift\n");

      out.write("}\n");

      particles[i].surfacePointstoRPlot(out, 1.0f, "dark" + color);
      out.write("z <- z - ");
      out.write("" + size);
      out.write("\n");
      particles[i].surfacePointstoRPlot(out, 1.0f, "dark" + color);
      out.write("z <- z +");
      out.write("" + (size * 2));
      out.write("\n");
      particles[i].surfacePointstoRPlot(out, 1.0f, "dark" + color);

      out.write("}\n\n");

      // out.write(particles[i].surfacePointLinestoR( 5.0f, "blue"));
      if (plotCenter) {

        out
            .write("if (!exists(\"corsen.centers\") || (exists(\"corsen.centers\") && corsen.centers==T)) {\n");

        out.write(particles[i].getCenter().toR(5, "darkblue"));

        out.write("}\n\n");

        out
            .write("if (!exists(\"corsen.barycenters\") || (exists(\"corsen.barycenters\") && corsen.barycenters==T)) {\n");

        out.write(particles[i].getBarycenter().toR(5, "blue"));

        out.write("}\n");
      }

      out.write("\n");

    }
    out.close();

    long end = System.currentTimeMillis();
    System.out.println("Write R plot : " + (end - start) + " ms");
  }

  /**
   * Calc the best distance between a messenger and a mitocondria.
   * @param messenger Messenger to test
   * @param mitos Array of mitochondria
   * @param minDistanceBetweenTwoPoints minimal distance between two points
   * @return the best distance between a messenger and a mitocondria or 0 if the
   *         barycenter is in a mitochondria
   */
  private double bestDistance(final Particle3D messenger,
      final Particle3D[] mitos, final float minDistanceBetweenTwoPoints) {

    if (messenger == null || mitos == null || messenger.getIntensity() == 0)
      return -1;

    final Point3D bary = messenger.getBarycenter();

    double best = Float.MAX_VALUE;
    int count = 0;

    for (int i = 0; i < mitos.length; i++) {
      double val = messenger.getBarycenterToSurfaceDistance(mitos[i]);

      final int n2 = mitos[i].innerPointsCount();

      for (int j = 0; j < n2; j++) {
        final Point3D p = mitos[i].getInnerPoint(j);

        if (bary.isNear(p, minDistanceBetweenTwoPoints))
          count++;
        // return -val;
      }

      if (count > 0) {

        if (count > 2)
          return -val;

        return val;
      }

      if (val < best)
        best = val;
    }

    return best;
  }

  private float getMinDistanceBetweenTwoPoints(final Particle3D[] particles) {

    if (particles == null)
      return 1.0f;

    float minDistance = Float.MAX_VALUE;
    for (int i = 0; i < particles.length; i++) {

      float d = particles[i].getMinDistance();

      if (d < minDistance && d != 0.0)
        minDistance = d;
    }

    if (minDistance == Float.MAX_VALUE)
      minDistance = 1.0f;

    return minDistance;
  }

  /**
   * Write R result file.
   * @param messengers Messengers
   * @param mitos Mitochondria
   * @param out Writer
   * @throws IOException if an error occurs while writing
   */
  private void writeRResultFile(final Particle3D[] messengers,
      final Particle3D[] mitos, final float minDistanceBetweenTwoPoints,
      final Writer out) throws IOException {

    long start = System.currentTimeMillis();

    final float pixelLen = this.pixelSize;

    for (int i = 0; i < messengers.length; i++) {

      out.write("" + messengers[i].getIntensity());
      out.write("\t");

      final double d = bestDistance(messengers[i], mitos,
          minDistanceBetweenTwoPoints)
          * pixelLen;

      out.write("" + d);
      out.write("\n");

    }

    long end = System.currentTimeMillis();
    System.out.println("Write R result file: " + (end - start) + " ms");
  }

  private boolean processMultipleCells(final File directory) throws IOException {

    if (directory == null || !directory.exists())
      return false;

    // Obtient la liste des fichiers du répertoire
    File[] files = directory.listFiles();

    // Maintenant crée un table de hachage pour y stoquer les fichier en entrée
    final HashMap map = new HashMap();

    // compilation de la regex
    final Pattern p = Pattern.compile(".*\\_ch(\\d+)\\_ce(\\d+)\\.par$");
    // final Pattern p = Pattern.compile("txt");

    for (int i = 0; i < files.length; i++) {

      final String filename = files[i].getName();

      Matcher m = p.matcher(filename);

      if (m.matches()) {

        int field = Integer.parseInt(m.group(1));
        int cell = Integer.parseInt(m.group(2));

        InputFiles iFiles;
        final String key = "ch" + field + "_ce" + cell;

        if (map.containsKey(key))
          iFiles = (InputFiles) map.get(key);
        else {
          iFiles = new InputFiles();
          map.put(key, iFiles);
        }

        if (filename.indexOf("mito") != -1)
          iFiles.inputmitoFile = files[i];
        else
          iFiles.inputrnaFile = files[i];
      }
    }

    // Lance les traitement

    Iterator it = map.keySet().iterator();

    int n = 0;
    final int count = map.size();

    if (count == 0)
      return false;

    while (it.hasNext()) {

      n++;
      String key = (String) it.next();

      InputFiles iFiles = (InputFiles) map.get(key);

      if (iFiles.inputmitoFile != null && iFiles.inputrnaFile != null) {

        final File parent = iFiles.inputmitoFile.getParentFile();

        // Execute the process for the file
        sendEvent(ProgressEvent.START_CELL_EVENT, n, count);
        processACell(iFiles.inputmitoFile, iFiles.inputrnaFile, parent, "cell_"
            + key);
      }

    }
    return true;
  }

  /**
   * Update z Coords
   * @param particle Particle to update
   */
  private void changeZCoord(final Particle3D particle) {

    if (particle == null)
      return;

    final int ni = particle.innerPointsCount();

    for (int i = 0; i < ni; i++) {

      Point3D p = particle.getInnerPoint(i);
      p.setZ(p.getZ() * this.zCoef);
    }

    final int ns = particle.surfacePointsCount();

    for (int i = 0; i < ns; i++) {

      Point3D p = particle.getSurfacePoint(i);
      p.setZ(p.getZ() * this.zCoef);
    }

  }

  /**
   * Get if exists the particle object generated from the intersection of the
   * cuboid with a particle 3D. In the new object, no surface point are set.
   * @param particle Particle to test
   * @param x0 X coordinate of the first point
   * @param y0 Y coordinate of the first point
   * @param z0 Z coordinate of the first point
   * @param len length of the cuboid
   * @param prefix The name of the particle to create
   * @param initialCapacity Initial capacity of the result particle3D
   * @return a new particle with contains only the point of the original
   *         particle which are too in the cuboid
   */
  private static Particle3D createCuboid(final Particle3D particle,
      final float x0, final float y0, final float z0, final float len,
      final String prefix, final int initialCapacity) {

    final float x1, y1, z1;
    Particle3D result = null;

    x1 = x0 + len;
    y1 = y0 + len;
    z1 = z0 + len;

    final ListPoint3D points = particle.getInnerPoints();
    final int n = points.size();

    for (int i = 0; i < n; i++) {

      final float x = points.getXAt(i);
      final float y = points.getYAt(i);
      final float z = points.getZAt(i);

      if (x >= x0 && x < x1 && y >= y0 && y < y1 && z >= z0 && z < z1) {

        if (result == null) {
          result = new Particle3D(initialCapacity);
          result.setName(prefix + "-" + result.getId());
          result.addSurfacePoint(x0, y0, z0);
          result.addSurfacePoint(x1, y0, z0);
          result.addSurfacePoint(x1, y1, z0);
          result.addSurfacePoint(x0, y1, z0);
          result.addSurfacePoint(x0, y0, z1);
          result.addSurfacePoint(x1, y0, z1);
          result.addSurfacePoint(x1, y1, z1);
          result.addSurfacePoint(x0, y1, z1);
        }
        result.addInnerPoint(x, y, z, points.getIAt(i));

      }
    }

    if (result != null)
      result.setIntensityFromInnerPoints();

    return result;
  }

  private void sendEvent(final int id) {
    Corsen.getCorsen().updateStatus(new ProgressEvent(id));
  }

  private void sendEvent(final int id, final int value1) {

    Corsen.getCorsen().updateStatus(new ProgressEvent(id, value1));
  }

  private void sendEvent(final int id, final int value1, final int value2) {

    Corsen.getCorsen().updateStatus(new ProgressEvent(id, value1, value2));
  }

  /**
   * Create cuboids particles from messengers particles.
   * @param messagers Messagers
   * @param lenght of the cuboid
   * @return An array of Particle3D
   */
  private Particle3D[] calcCuboid(final Particle3D[] messagers,
      final float lenght) {

    if (messagers == null)
      return null;

    ArrayList al = null;

    final int nMessagers = messagers.length;
    final int countMax = countPoints(messagers);
    int count = 0;

    for (int m = 0; m < nMessagers; m++) {

      final Particle3D messenger = messagers[m];
      ListPoint3D lp = messenger.getInnerPoints();

      final float xMax = lp.getXMax() + 1.0f;
      final float yMax = lp.getYMax() + 1.0f;
      final float zMax = lp.getZMax() + 1.0f;
      final float xMin = (float) (Math.floor(lp.getXMin() / lenght) * lenght);
      final float yMin = (float) (Math.floor(lp.getYMin() / lenght) * lenght);
      final float zMin = (float) (Math.floor(lp.getZMin() / lenght) * lenght);

      for (float i = xMin; i < xMax; i += lenght)
        for (float j = yMin; j < yMax; j += lenght)
          for (float k = zMin; k < zMax; k += lenght) {

            Particle3D r = createCuboid(messenger, i, j, k, lenght, "cuboid",
                48);

            if (r != null) {
              if (al == null)
                al = new ArrayList();
              al.add(r);
            }

          }

      count += lp.size();
      double p = ((double) count) / ((double) countMax) * 1000.0;
      sendEvent(ProgressEvent.PROGRESS_CALC_CUBOIDS_EVENT, (int) p);
    }

    if (al == null)
      return null;
    final Particle3D[] result = new Particle3D[al.size()];
    al.toArray(result);

    return result;
  }

  //
  // Methods from Runnable
  //

  /**
   * Main method of the thread.
   */
  public void run() {

    try {

      if (isMultipleFiles()) {
        if (processMultipleCells(getDirFiles())) {
          sendEvent(ProgressEvent.END_SUCCESSFULL_EVENT, 1, 1);
          Corsen.getCorsen().showMessage("Outputs files creations successful.");
        } else {
          Corsen.getCorsen().showError(
              "Directory not exists or no files to process");
        }
      } else {
        sendEvent(ProgressEvent.START_CELL_EVENT, 1, 1);
        processACell(getMitoFile(), getRnaFile(), getResultDir(),
            getResultFilename());
        sendEvent(ProgressEvent.END_SUCCESSFULL_EVENT, 1, 1);
        Corsen.getCorsen().showMessage("Output file creation successful.");
      }

    } catch (IOException e) {
      Corsen.getCorsen().showError(e.getMessage());
    }

  }
}
