/*
 *                  Corsen development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence version 2 or later. This
 * should be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Corsen project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.DistancesCalculator;
import fr.ens.transcriptome.corsen.model.JavascriptParticles3DFilter;

/**
 * This class is called by ui when process cells.
 * @author Laurent Jourdren
 */
public class CorsenCore implements Runnable {

  private static Logger logger = Logger.getLogger(CorsenCore.class.getName());
  private UpdateStatus updateStatus;

  private Settings settings = new Settings();

  private File dirFiles;
  private File particlesBFile;
  private File particlesAFile;
  private File resultFile;
  private boolean multipleFiles;

  private static class InputFiles {

    File inputParticlesAFile;
    File inputParticlesBFile;
    String prefix;
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
   * @return Returns the particlesBFile
   */
  public File getParticlesBFile() {
    return this.particlesBFile;
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
   * @return Returns the particlesAFile
   */
  public File getParticlesAFile() {
    return this.particlesAFile;
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
   * @param settings The setting to set
   */
  public void setSettings(final Settings settings) {

    this.settings = settings;
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
   * @param particlesBFile The particlesBFile to set
   */
  public void setParticlesBFile(final File particlesBFile) {
    this.particlesBFile = particlesBFile;
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
  public void setResultFile(final File resultFile) {
    this.resultFile = resultFile;
  }

  /**
   * Set the rna file.
   * @param particlesAFile The particlesAFile to set
   */
  public void setParticlesAFile(final File particlesAFile) {
    this.particlesAFile = particlesAFile;
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

  private void processACell(final File rnaFile, final File mitoFile,
      final File resultFile) throws IOException {

    sendEvent(ProgressEventType.START_CELLS_EVENT, ProgressEvent
        .countPhase(this.settings));
    doACell(rnaFile, mitoFile, resultFile, 1, 1);
    sendEvent(ProgressEventType.END_CELLS_SUCCESSFULL_EVENT, 1, 1);

  }

  /**
   * Process data for a cell
   * @param particlesBFile ImageJ Plugin result file for mitochondria
   * @param particlesAFile ImageJ Plugin result file for RNAm
   * @param resultFile Result filename
   * @param currentCell index of the current cell
   * @param cellCount number of cell to process
   * @throws IOException if an error occurs while reading or writing data
   */
  private void doACell(final File particlesAFile, final File particlesBFile,
      final File resultFile, final int currentCell, final int cellCount)
      throws IOException {

    final Settings s = this.getSettings();

    logger.info("File A: " + particlesAFile);
    logger.info("File B: " + particlesBFile);

    // Send Start cell event
    sendEvent(ProgressEventType.START_CELL_EVENT, currentCell, cellCount,
        particlesAFile.getAbsolutePath(), particlesBFile.getAbsolutePath(),
        resultFile.getAbsolutePath());

    final CorsenResult result =
        new CorsenResult(particlesAFile, particlesBFile, resultFile, s,
            getUpdateStatus());

    // Set Filters
    result.setParticlesAFilter(JavascriptParticles3DFilter
        .createFilter(settings.getParticlesAFilterExpression()));
    result.setParticlesBFilter(JavascriptParticles3DFilter
        .createFilter(settings.getParticlesBFilterExpression()));

    // Create writer object
    final CorsenResultWriter writer = new CorsenResultWriter(result);

    DistancesCalculator dc = new DistancesCalculator(result);
    dc.setCoordinatesFactor(settings.getFactor());
    dc.setZCoordinatesFactor(settings.getZFactor());
    dc.loadParticles();

    result.getMessengersParticles().setType(settings.getParticlesAType());
    result.getMitosParticles().setType(settings.getParticlesBType());

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
      writer.writeDataFile(resultFile, Globals.EXTENSION_DATA_FILE);
    }

    if (s.isSaveIVFile()) {
      sendEvent(ProgressEventType.START_WRITE_IV_MESSENGERS_EVENT);
      writer.writeMessengersIntensityVolume(resultFile, "_"
          + this.settings.getParticlesAName().toLowerCase()
          + Globals.EXTENSION_IV_FILE);
      sendEvent(ProgressEventType.START_WRITE_IV_MESSENGERS_CUBOIDS_EVENT);
      writer.writeCuboidsMessengersIntensityVolume(resultFile, "_"
          + this.settings.getParticlesAName().toLowerCase() + "_cuboid"
          + Globals.EXTENSION_IV_FILE);
      sendEvent(ProgressEventType.START_WRITE_IV_MITOS_EVENT);
      writer.writeMitosIntensityVolume(resultFile, "_"
          + this.settings.getParticlesBName().toLowerCase()
          + Globals.EXTENSION_IV_FILE);
    }

    if (s.isSaveResultsFile()) {
      sendEvent(ProgressEventType.START_WRITE_RESULT_EVENT);
      writer.writeResult(resultFile, Globals.EXTENSION_RESULT_FILE);
    }

    if (s.isSaveFullResultsFile()) {
      sendEvent(ProgressEventType.START_WRITE_FULLRESULT_EVENT);
      writer.writeFullResult(resultFile, Globals.EXTENSION_FULL_RESULT_FILE);
    }

    //
    // Write RGL files
    //

    if (s.isSaveVisualizationFiles()) {

      if (s.isSaveParticleA3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MESSENGERS_EVENT);
        new RGL(resultFile, "_"
            + this.settings.getParticlesAName().toLowerCase()
            + Globals.EXTENSION_RGL_FILE).writeRPlots(result
            .getMessengersParticles(), "green", true);
      }

      if (s.isSaveParticleB3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MITOS_EVENT);
        new RGL(resultFile, "_"
            + this.settings.getParticlesBName().toLowerCase()
            + Globals.EXTENSION_RGL_FILE).writeRPlots(result
            .getMitosParticles(), "red", false);
      }

      if (s.isSaveParticlesACuboids3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT);
        new RGL(null, resultFile, "_"
            + this.settings.getParticlesAName().toLowerCase() + "_cuboid"
            + Globals.EXTENSION_RGL_FILE).writeRPlots(result
            .getCuboidsMessengersParticles(), "green", true);
      }

      if (s.isSaveParticlesBCuboids3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_MITOS_CUBOIDS_EVENT);
        new RGL(null, resultFile, "_"
            + this.settings.getParticlesBName().toLowerCase() + "_cuboid"
            + Globals.EXTENSION_RGL_FILE).writeRPlots(result
            .getCuboidsMitosParticles(), "red", false);
      }

      if (s.isSaveDistances3dFile()) {
        sendEvent(ProgressEventType.START_WRITE_RPLOT_DISTANCES_EVENT);
        new RGL(null, resultFile, Globals.EXTENSION_DISTANCES_FILE)
            .writeDistances(result.getMinDistances(), "cyan");
      }

    }

    // Send result to visualisation
    endProcess(result);

    // Send End cell event
    sendEvent(ProgressEventType.END_CELL_EVENT);
  }

  private static InputFiles getInputFile(final Map<String, InputFiles> map,
      final String filename, final String prefix, final String suffix) {

    if (filename.startsWith(prefix) && filename.endsWith(suffix)) {

      String between =
          filename.substring(prefix.length(), filename.length()
              - suffix.length());

      InputFiles iFile;

      if (map.containsKey(between))
        iFile = map.get(between);
      else {
        iFile = new InputFiles();
        iFile.prefix = between;
        map.put(between, iFile);
      }

      return iFile;
    }
    return null;
  }

  private boolean processMultipleCells(final File directory) throws IOException {

    if (directory == null || !directory.exists())
      return false;

    final long startTime = System.currentTimeMillis();

    final String prefixA = this.settings.getParticlesABatchPrefix();
    final String prefixB = this.settings.getParticlesBBatchPrefix();

    // Store in an HashMap the input files
    final Map<String, InputFiles> map = new HashMap<String, InputFiles>();

    // Get the list of directoty files
    final File[] files = directory.listFiles();

    for (int i = 0; i < files.length; i++) {

      final String filename = files[i].getName();

      InputFiles iFile =
          getInputFile(map, filename, prefixA, Globals.EXTENSION_PARTICLES_FILE);
      if (iFile != null)
        iFile.inputParticlesAFile = files[i];

      iFile =
          getInputFile(map, filename, prefixB, Globals.EXTENSION_PARTICLES_FILE);
      if (iFile != null)
        iFile.inputParticlesBFile = files[i];
    }

    // Remove bad entries
    for (String key : new HashSet<String>(map.keySet())) {

      final InputFiles iFiles = map.get(key);

      if (iFiles.inputParticlesBFile == null
          || iFiles.inputParticlesAFile == null)
        map.remove(key);
    }

    // Start the process

    int n = 0;
    final int count = map.size();

    if (count == 0)
      return false;

    sendEvent(ProgressEventType.START_CELLS_EVENT, ProgressEvent
        .countPhase(this.settings));

    List<String> sortedKeys = new ArrayList<String>(map.keySet());
    Collections.sort(sortedKeys);

    for (String key : sortedKeys) {

      n++;
      final InputFiles iFiles = map.get(key);

      if (iFiles.inputParticlesBFile != null
          && iFiles.inputParticlesAFile != null) {

        final File parent = iFiles.inputParticlesBFile.getParentFile();
        doACell(iFiles.inputParticlesAFile, iFiles.inputParticlesBFile,
            new File(parent, iFiles.prefix), n, count);
      }

    }

    final long endTime = System.currentTimeMillis();

    logger.info("Calc " + n + " cells in " + (endTime - startTime) + " ms.");

    sendEvent(ProgressEventType.END_CELLS_SUCCESSFULL_EVENT, 1, 1);
    return true;
  }

  private void sendEvent(final ProgressEventType type) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.updateStatus(new ProgressEvent(type));
  }

  private void sendEvent(final ProgressEventType type, final int value1) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.updateStatus(new ProgressEvent(type, value1));
  }

  private void sendEvent(final ProgressEventType type, final int value1,
      final int value2) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.updateStatus(new ProgressEvent(type, value1, value2));
  }

  private void sendEvent(final ProgressEventType type, final int value1,
      final int value2, final String value3, final String value4,
      final String value5) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.updateStatus(new ProgressEvent(type, value1, value2, value3, value4,
        value5));
  }

  private void endProcess(final CorsenResult result) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.endProcess(result);
  }

  private void showMessage(final String message) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.showMessage(message);
  }

  private void showError(final String message) {

    final UpdateStatus us = getUpdateStatus();

    if (us == null)
      return;

    us.showError(message);
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
          showMessage("Particles distances computations successful.");
        } else
          showError("Directory not exists or no files to process");
      } else {

        processACell(getParticlesAFile(), getParticlesBFile(), getResultFile());

        showMessage("Particles distances computations successful.");
      }

    } catch (final IOException e) {
      showError(e.getMessage());
    }

    logger.info("End of the thread");
  }
}
