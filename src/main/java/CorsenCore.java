import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorsenCore implements Runnable {

  private float pixelSize = 1.0f;
  private float zCoef;
  private boolean updateZ;
  private boolean writeFullResults;

  private File dirFiles;
  private File mitoFile;
  private File rnaFile;
  private File resultDir;
  private String resultFilename;
  private boolean multipleFiles;

  private static final String EXTENSION_MESSENGERS_IV_FILE = "_messengers.iv";
  private static final String EXTENSION_MESSENGERS_RGL_FILE = "_messengers.R";
  private static final String EXTENSION_CUBOIDS_IV_FILE = "_cuboids.iv";
  private static final String EXTENSION_MITOS_CUBOIDS_RGL_FILE = "_mitos_cuboids.R";
  private static final String EXTENSION_MITOS_RGL_FILE = "_mitos.R";
  private static final String EXTENSION_MITOS_IV_FILE = "_mitos.iv";
  private static final String EXTENSION_CUBOIDS_RGL_FILE = "_cuboids.R";
  private static final String EXTENSION_DISTANCES_FILE = "_distances.R";
  private static final String EXTENSION_DATA_FILE = ".data";

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
    return this.dirFiles;
  }

  /**
   * Get the mito file.
   * @return Returns the mitoFile
   */
  public File getMitoFile() {
    return this.mitoFile;
  }

  /**
   * Get the result directory.
   * @return Returns the resultDir
   */
  public File getResultDir() {
    return this.resultDir;
  }

  /**
   * Get the result filename.
   * @return Returns the resultFilename
   */
  public String getResultFilename() {
    return this.resultFilename;
  }

  /**
   * Get the rna file.
   * @return Returns the rnaFile
   */
  public File getRnaFile() {
    return this.rnaFile;
  }

  /**
   * Test if there are multple files to read.
   * @return Returns the multipleFiles
   */
  public boolean isMultipleFiles() {
    return this.multipleFiles;
  }

  //
  // Setters
  //

  /**
   * Set the size of pixel.
   * @param pixelSize The size of a pixel
   */
  public void setPixelSize(final float pixelSize) {
    this.pixelSize = pixelSize;
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
    this.zCoef = coef;
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

    final Particles3D messengersParticles = new Particles3D(rnaFile);
    if (this.updateZ)
      messengersParticles.changeZCoord(this.zCoef);

    messengersParticles.changeCoord(this.pixelSize);

    // final Particle3D[] messengers = readmageJPlugingOutputFile(rnaFile);
    sendEvent(ProgressEvent.START_WRITE_RPLOT_MESSENGERS_EVENT);

    RGL rgl = new RGL(resultDir, resultFilename + EXTENSION_MESSENGERS_RGL_FILE);
    rgl.writeRPlots(messengersParticles, "green", true);
    rgl.close();

    final File messengersIVFile = new File(resultDir, resultFilename
        + EXTENSION_MESSENGERS_IV_FILE);
    writeIntensityVolume(messengersIVFile, messengersParticles);

    // Read mitos and write R plot for mitos
    sendEvent(ProgressEvent.START_READ_MITOS_EVENT);

    final Particles3D mitosParticles = new Particles3D(mitoFile);
    if (this.updateZ) {
      mitosParticles.changeZCoord(this.zCoef);
    }
    mitosParticles.changeCoord(this.pixelSize);

    sendEvent(ProgressEvent.START_WRITE_RPLOT_MITOS_EVENT);

    rgl = new RGL(resultDir, resultFilename + EXTENSION_MITOS_RGL_FILE);
    rgl.writeRPlots(mitosParticles, "red", false);
    rgl.close();

    final File mitosIVFile = new File(resultDir, resultFilename
        + EXTENSION_MITOS_IV_FILE);
    writeIntensityVolume(mitosIVFile, mitosParticles);

    // Calc cuboids and write R plot for cuboids
    sendEvent(ProgressEvent.START_CALC_MESSENGERS_CUBOIDS_EVENT);
    final Particles3D cuboids = calcCuboid(messengersParticles);

    sendEvent(ProgressEvent.START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT);

    rgl = new RGL(resultDir, resultFilename + EXTENSION_CUBOIDS_RGL_FILE);
    rgl.writeRPlots(cuboids, "green", true);
    rgl.close();

    // Write R result file
    File resultDataFile = new File(resultDir, resultFilename
        + EXTENSION_DATA_FILE);

    File fileRGLDistances = new File(resultDir, resultFilename
        + EXTENSION_DISTANCES_FILE);

    File fileRGLMitoCuboids = new File(resultDir, resultFilename
        + EXTENSION_MITOS_CUBOIDS_RGL_FILE);

    writeRResultFile(cuboids, mitosParticles, resultDataFile, fileRGLDistances,
        fileRGLMitoCuboids);

    sendEvent(ProgressEvent.START_WRITE_INTENSITIES_VOLUMES_EVENT);
    final File cuboidsIVFile = new File(resultDir, resultFilename
        + EXTENSION_CUBOIDS_IV_FILE);
    writeIntensityVolume(cuboidsIVFile, cuboids);

    // Write full result final

    if (this.writeFullResults) {

      FileOutputStream fos = new FileOutputStream(new File(resultDir,
          resultFilename + ".result"));
      Writer out = new OutputStreamWriter(fos);

      sendEvent(ProgressEvent.START_WRITE_FULLRESULT_MESSAGERS_EVENT);
      writeFullResultFile(messengersParticles, mitosParticles, out);

      sendEvent(ProgressEvent.START_WRITE_FULLRESULT_CUBOIDS_EVENT);
      writeFullResultFile(cuboids, mitosParticles, out);

      out.close();
    }

  }

  /**
   * Write data in full resut file
   * @param messengers Messengers particles
   * @param mitos Mitochondia particles
   * @param out Writer
   * @throws IOException if an error occurs while writing data
   */
  private void writeFullResultFile(final Particles3D messengersParticles,
      final Particles3D mitosParticles, final Writer out) throws IOException {

    final long start = System.currentTimeMillis();

    out.write("messager");
    out.write("\t");
    out.write("volume");
    out.write("\t");
    out.write("intensity");
    out.write("\t");

    final Particle3D[] mitos = mitosParticles.getParticles();
    final Particle3D[] messengers = messengersParticles.getParticles();

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

        final double dss = messengers[i].getSurfaceToSurfaceDistance(mitos[j]);
        final double dcs = messengers[i]
            .getBarycenterToSurfaceDistance(mitos[j]);

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

    final long end = System.currentTimeMillis();
    System.out.println("Write full result file : " + (end - start) + " ms");
  }

  /**
   * Write R result file.
   * @param messengers Messengers
   * @param mitos Mitochondria
   * @param fileDistances Writer for result
   * @throws IOException if an error occurs while writing
   */
  private void writeRResultFile(final Particles3D messengers,
      final Particles3D mitos, final File outFile, final File fileDistances,
      final File fileMitoCuboids) throws IOException {

    sendEvent(ProgressEvent.START_CALC_MITOS_CUBOIDS_EVENT);
    final DistanceCalculator dc = new DistanceCalculator(mitos, fileDistances,
        fileMitoCuboids);

    sendEvent(ProgressEvent.START_WRITE_RESULT_CUBOIDS_EVENT);
    FileOutputStream fos = new FileOutputStream(outFile);
    Writer out = new OutputStreamWriter(fos);

    out.write("#Intensity\tmin distance\tmax distance\n");

    final Particle3D[] mes = messengers.getParticles();

    for (int i = 0; i < mes.length; i++) {

      out.write("" + mes[i].getIntensity());
      out.write("\t");
      final double min = dc.minimalDistance(mes[i]);
      out.write("" + min);
      out.write("\t");
      final double max = dc.maximalDistance(mes[i]);
      out.write("" + max);
      out.write("\n");
    }

    dc.closeRGLDistances();
    out.close();
  }

  private boolean processMultipleCells(final File directory) throws IOException {

    if (directory == null || !directory.exists())
      return false;

    // Get the list of directoty files
    final File[] files = directory.listFiles();

    // Store in an HashMap the input files
    final HashMap map = new HashMap();

    // Create regex
    final Pattern p = Pattern.compile(".*\\_ch(\\d+)\\_ce(\\d+)\\.par$");
    // final Pattern p = Pattern.compile("txt");

    for (int i = 0; i < files.length; i++) {

      final String filename = files[i].getName();

      final Matcher m = p.matcher(filename);

      if (m.matches()) {

        final int field = Integer.parseInt(m.group(1));
        final int cell = Integer.parseInt(m.group(2));

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

        // System.out.println(filename+
        // "\t"+field+"\t"+cell+"\t"+key+"\t"+map.size());

      }
    }

    // Start the process

    final Iterator it = map.keySet().iterator();

    int n = 0;
    final int count = map.size();

    if (count == 0)
      return false;

    while (it.hasNext()) {

      n++;
      final String key = (String) it.next();

      final InputFiles iFiles = (InputFiles) map.get(key);

      if (iFiles.inputmitoFile != null && iFiles.inputrnaFile != null) {

        final File parent = iFiles.inputmitoFile.getParentFile();

        // Execute the process for the file
        sendEvent(ProgressEvent.START_CELL_EVENT, n, count);

        processACell(iFiles.inputmitoFile, iFiles.inputrnaFile, parent, "cell_"
            + key);

        // System.out.println("mito: " + iFiles.inputmitoFile + " arn: "
        // + iFiles.inputrnaFile);

      }

    }
    return true;
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
   * @param messengers Messengers
   * @param lenght of the cuboid
   * @return An array of Particle3D
   */
  public Particles3D calcCuboid(final Particles3D particles) {

    if (particles == null)
      return null;

    ArrayList al = null;

    final int nParticles = particles.getParticlesNumber();
    final int countMax = Particle3DUtil.countInnerPointsInParticles(particles
        .getParticles());
    int count = 0;

    for (int m = 0; m < nParticles; m++) {

      final Particle3D messenger = particles.getParticle(m);

      float len = particles.getPixelDepth();
      if (particles.getPixelWidth() > len)
        len = particles.getPixelWidth();
      if (particles.getPixelHeight() > len)
        len = particles.getPixelHeight();

      len = len * Corsen.CUBOID_SIZE_FACTOR;

      ArrayList cuboids = Particle3DUtil.createCuboid(messenger, len, len, len);

      if (cuboids != null) {
        if (al == null)
          al = new ArrayList();
        al.addAll(cuboids);
      }

      count += messenger.innerPointsCount();
      final double p = (double) count / (double) countMax * 1000.0;
      sendEvent(ProgressEvent.PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT, (int) p);
    }

    if (al == null)
      return null;
    final Particle3D[] result = new Particle3D[al.size()];
    al.toArray(result);

    Particles3D pars = new Particles3D(particles);

    pars.setParticles(result);

    return pars;
  }

  private void writeIntensityVolume(final File file, final Particles3D particles)
      throws IOException {

    if (particles == null || file == null)
      return;

    Particle3D[] pars = particles.getParticles();

    if (pars == null)
      return;

    final FileOutputStream fos = new FileOutputStream(file);
    final Writer out = new OutputStreamWriter(fos);

    out.write("# Intensity\tVolume\n");

    for (int i = 0; i < pars.length; i++) {
      if (i != 0)
        out.write("\n");
      out.write(Long.toString(pars[i].getIntensity()));
      out.write("\t");
      out.write(Double.toString(pars[i].getVolume()));
    }

    out.close();
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
        } else
          Corsen.getCorsen().showError(
              "Directory not exists or no files to process");
      } else {
        sendEvent(ProgressEvent.START_CELL_EVENT, 1, 1);
        processACell(getMitoFile(), getRnaFile(), getResultDir(),
            getResultFilename());
        sendEvent(ProgressEvent.END_SUCCESSFULL_EVENT, 1, 1);
        Corsen.getCorsen().showMessage("Output file creation successful.");
      }

    } catch (final IOException e) {
      Corsen.getCorsen().showError(e.getMessage());
    }

  }
}
