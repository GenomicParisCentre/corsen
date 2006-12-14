package fr.ens.transcriptome.corsen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.DistancesCalculator;
import fr.ens.transcriptome.corsen.calc.ParticleType;

public class CorsenCore implements Runnable {

  private UpdateStatus updateStatus;

  private Settings settings = new Settings();

  private File dirFiles;
  private File mitoFile;
  private File rnaFile;
  private File resultFile;
  private boolean multipleFiles;

  public static final String EXTENSION_MESSENGERS_IV_FILE = "_messengers.iv";
  public static final String EXTENSION_MESSENGERS_RGL_FILE = "_messengers.R";
  public static final String EXTENSION_CUBOIDS_IV_FILE = "_cuboids.iv";
  public static final String EXTENSION_MITOS_CUBOIDS_RGL_FILE = "_mitos_cuboids.R";
  public static final String EXTENSION_MITOS_RGL_FILE = "_mitos.R";
  public static final String EXTENSION_MITOS_IV_FILE = "_mitos.iv";
  public static final String EXTENSION_CUBOIDS_RGL_FILE = "_cuboids.R";
  public static final String EXTENSION_DISTANCES_FILE = "_distances.R";
  public static final String EXTENSION_DATA_FILE = ".data";
  public static final String EXTENSION_FULL_RESULT_FILE = ".result";

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
   * Get the update status class.
   * @return the updateStatus class
   */
  public final UpdateStatus getUpdateStatus() {

    return this.updateStatus;
  }

  /**
   * Get the settings.
   * @return an settings object
   */
  public Settings getSettings() {

    return this.settings;
  }

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
  /*
   * public File getResultDir() { return this.resultDir; }
   */

  /**
   * Get the result filename.
   * @return Returns the resultFile
   */
  public File getResultFile() {
    return this.resultFile;
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
   * Set the update status class.
   * @param updateStatus the updateStatus class
   */
  public final void setUpdateStatus(final UpdateStatus updateStatus) {

    this.updateStatus = updateStatus;
  }

  /**
   * Set the settings.
   * @param setting The setting to set
   */
  public void setSettings(final Settings settings) {

    this.settings = settings;
  }

  /**
   * Set the size of pixel.
   * @param pixelSize The size of a pixel
   */
  /*
   * public void setPixelSize(final float pixelSize) { this.pixelSize =
   * pixelSize; }
   */

  /**
   * Set if the Z coordinate must be updated
   * @param updateZ true if the Z coordinate must be updated
   */
  /*
   * public void setUpdateZ(final boolean updateZ) { this.updateZ = updateZ; }
   */

  /**
   * Set the coef of the update of the Z values.
   * @param coef the coef of the update of the Z values
   */
  /*
   * public void setZCoef(final float coef) { this.zCoef = coef; }
   */

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
  /*
   * public void setResultDir(final File resultDir) { this.resultDir =
   * resultDir; }
   */

  /**
   * Set the result filename.
   * @param resultFile The resultFile to set
   */
  public void setResultFile(final File resultFilename) {
    this.resultFile = resultFilename;
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

  private void processACell(final File mitoFile, final File rnaFile,
      final File resultFile) throws IOException {

    sendEvent(ProgressEventType.START_CELLS_EVENT, ProgressEvent
        .countPhase(this.settings));
    doACell(mitoFile, rnaFile, resultFile, 1, 1);
    sendEvent(ProgressEventType.END_CELLS_SUCCESSFULL_EVENT, 1, 1);

  }

  /**
   * Process data for a cell
   * @param mitoFile ImageJ Plugin result file for mitochondria
   * @param rnaFile ImageJ Plugin result file for RNAm
   * @param resultDir Result file directory
   * @param resultFile Result filename
   * @throws IOException if an error occurs while reading or writing data
   */
  private void doACell(final File mitoFile, final File rnaFile,
      final File resultFile, int currentCell, int cellCount) throws IOException {

    final Settings s = this.getSettings();

    // Send Start cell event
    sendEvent(ProgressEventType.START_CELL_EVENT, currentCell, cellCount,
        mitoFile.getAbsolutePath(), rnaFile.getAbsolutePath(), resultFile
            .getAbsolutePath());

    final CorsenResult result = new CorsenResult(rnaFile, mitoFile,s,
        getUpdateStatus());

    // Create writer object
    final CorsenResultWriter writer = new CorsenResultWriter(result);

    DistancesCalculator dc = new DistancesCalculator(result);
    dc.loadParticles();

    result.getMessengersParticles().setType(ParticleType.TINY);
    result.getMitosParticles().setType(ParticleType.HUGE);

    dc.calc();

    // Calc the cuboids
    // sendEvent(ProgressEventType.START_CALC_MESSENGERS_CUBOIDS_EVENT);
    // result.calcMessengerCuboids();
    // sendEvent(ProgressEventType.START_CALC_MITOS_CUBOIDS_EVENT);
    // result.calcMitosCuboids();

    // Calc the distances
    // sendEvent(ProgressEventType.START_CALC_MIN_DISTANCES_EVENT);
    // result.calcMinimalDistances();
    // sendEvent(ProgressEventType.START_CALC_MAX_DISTANCES_EVENT);
    // result.calcMaximalDistances();

    //
    // Write results
    //

    if (s.isSaveDataFile()) {
      sendEvent(ProgressEventType.START_WRITE_DATA_EVENT);
      writer.writeDataFile(resultFile, EXTENSION_DATA_FILE);
    }

    if (s.isSaveIVFile()) {
      sendEvent(ProgressEventType.START_WRITE_IV_MESSENGERS_EVENT);
      writer.writeMessengersIntensityVolume(resultFile,
          EXTENSION_MESSENGERS_IV_FILE);
      sendEvent(ProgressEventType.START_WRITE_IV_MESSENGERS_CUBOIDS_EVENT);
      writer.writeCuboidsMessengersIntensityVolume(resultFile,
          EXTENSION_CUBOIDS_IV_FILE);
      sendEvent(ProgressEventType.START_WRITE_IV_MITOS_EVENT);
      writer.writeMitosIntensityVolume(resultFile, EXTENSION_MITOS_IV_FILE);
    }

    if (s.isSaveFullResultsFile()) {
      sendEvent(ProgressEventType.START_WRITE_FULLRESULT_EVENT);
      writer.writeFullResult(resultFile, EXTENSION_FULL_RESULT_FILE);
    }

    //
    // Write RGL files
    //

    if (s.isSaveVisualizationFiles()) {

      if (s.isSaveMessengers3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MESSENGERS_EVENT);
        new RGL(resultFile, EXTENSION_MESSENGERS_RGL_FILE).writeRPlots(result
            .getMessengersParticles(), "green", true);
      }

      if (s.isSaveMito3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MITOS_EVENT);
        new RGL(resultFile, EXTENSION_MITOS_RGL_FILE).writeRPlots(result
            .getMitosParticles(), "red", false);
      }

      if (s.isSaveMessengersCuboids3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT);
        new RGL(null, resultFile, EXTENSION_CUBOIDS_RGL_FILE).writeRPlots(
            result.getCuboidsMessengersParticles(), "green", true);
      }

      if (s.isSaveMitoCuboids3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MITOS_CUBOIDS_EVENT);
        new RGL(null, resultFile, EXTENSION_MITOS_CUBOIDS_RGL_FILE)
            .writeRPlots(result.getCuboidsMitosParticles(), "red", false);
      }

      if (s.isSaveDistances3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_DISTANCES_EVENT);
        new RGL(null, resultFile, EXTENSION_DISTANCES_FILE).writeDistances(
            result.getMinDistances(), "cyan");
      }

    }

    // Send result to visualisation
    getUpdateStatus().endProcess(result);

    // Send End cell event
    sendEvent(ProgressEventType.END_CELL_EVENT);
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

    sendEvent(ProgressEventType.START_CELLS_EVENT, ProgressEvent
        .countPhase(this.settings));

    while (it.hasNext()) {

      n++;
      final String key = (String) it.next();

      final InputFiles iFiles = (InputFiles) map.get(key);

      if (iFiles.inputmitoFile != null && iFiles.inputrnaFile != null) {

        final File parent = iFiles.inputmitoFile.getParentFile();

        doACell(iFiles.inputmitoFile, iFiles.inputrnaFile, new File(parent
            .getAbsoluteFile()
            + "cell_" + key), n, count);

        // System.out.println("mito: " + iFiles.inputmitoFile + " arn: "
        // + iFiles.inputrnaFile);

      }

    }

    sendEvent(ProgressEventType.END_CELLS_SUCCESSFULL_EVENT, 1, 1);
    return true;
  }

  private void sendEvent(final ProgressEventType type) {
    getUpdateStatus().updateStatus(new ProgressEvent(type));
  }

  private void sendEvent(final ProgressEventType type, final int value1) {

    getUpdateStatus().updateStatus(new ProgressEvent(type, value1));
  }

  private void sendEvent(final ProgressEventType type, final int value1,
      final int value2) {

    getUpdateStatus().updateStatus(new ProgressEvent(type, value1, value2));
  }

  private void sendEvent(final ProgressEventType type, final int value1,
      final int value2, final String value3, final String value4,
      final String value5) {

    getUpdateStatus().updateStatus(
        new ProgressEvent(type, value1, value2, value3, value4, value5));
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
          sendEvent(ProgressEventType.END_CELLS_SUCCESSFULL_EVENT, 1, 1);
          getUpdateStatus().showMessage("Outputs files creations successful.");
        } else
          getUpdateStatus().showError(
              "Directory not exists or no files to process");
      } else {

        processACell(getMitoFile(), getRnaFile(), getResultFile());

        getUpdateStatus().showMessage("Output file creation successful.");
      }

    } catch (final IOException e) {
      getUpdateStatus().showError(e.getMessage());
    }

  }
}
