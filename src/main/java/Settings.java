import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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

public final class Settings {

  private static final String ZFACTOR_KEY = "zFactor";
  private static final String FACTOR_KEY = "factor";
  private static final String UNIT_KEY = "unit";
  private static final String AUTO_CUBOID_SIZE_KEY = "autoCuboidSize";
  private static final String CUBOID_SIZE_KEY = "cuboidSize";
  private static final String SAVE_DATA_FILE_KEY = "saveDataFile";
  private static final String SAVE_IV_FILE_SAVED = "saveIVFile";
  private static final String SAVE_3DFILES_KEY = "save3dFiles";
  private static final String SAVE_MESSENGERS_3DFILES_KEY = "saveMessengers3dFile";
  private static final String SAVE_MESSENGERS_CUBOIDS_3DFILES_KEY = "saveMessengersCuboidsFile";
  private static final String SAVE_MITOS_3DFILES_KEY = "saveMitos3DFile";
  private static final String SAVE_MITOS_CUBOIDS_3DFILES_KEY = "saveMitosCuboids3dFile";
  private static final String SAVE_DISTANCES_3DFILES_KEY = "saveDistances3dFile";
  private static final String SAVE_FULL_RESULTS_KEY = "saveFullResultsFile";

  private Properties properties = new Properties();

  //
  // Getters
  //

  /**
   * Test if iv file must be saved.
   * @return Returns the autoCuboidSize
   */
  public boolean isSaveIVFile() {

    String value = this.properties.getProperty(SAVE_IV_FILE_SAVED, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if full results must be saved.
   * @return Returns the autoCuboidSize
   */
  public boolean isSaveFullResultsFile() {

    String value = this.properties.getProperty(SAVE_FULL_RESULTS_KEY, "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if the cuboid size for messenger must be set automaticaly.
   * @return Returns the autoCuboidSize
   */
  public boolean isAutoCuboidSize() {

    String value = this.properties.getProperty(AUTO_CUBOID_SIZE_KEY, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if data file must be saved.
   * @return Returns the dataFile
   */
  public boolean isSaveDataFile() {

    String value = this.properties.getProperty(SAVE_DATA_FILE_KEY, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if 3d distances file must be saved.
   * @return Returns the distances3dFile
   */
  public boolean isSaveDistances3dFile() {

    String value = this.properties.getProperty(SAVE_DISTANCES_3DFILES_KEY, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Get the factor.
   * @return Returns the factor
   */
  public float getFactor() {

    String value = this.properties.getProperty(FACTOR_KEY, "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Test if 3d messengers file must be saved.
   * @return Returns the messengers3dFile
   */
  public boolean isSaveMessengers3dFile() {

    String value = this.properties.getProperty(SAVE_MESSENGERS_3DFILES_KEY, "true");

    return Boolean.valueOf(value);
  }

  /**
   * Test if 3d messengers ciboids file must be saved.
   * @return Returns the messengersCuboids3dFile
   */
  public boolean isSaveMessengersCuboids3dFile() {

    String value = this.properties.getProperty(SAVE_MESSENGERS_CUBOIDS_3DFILES_KEY,
        "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if 3d mito file must be saved.
   * @return Returns the mito3dFile
   */
  public boolean isSaveMito3dFile() {

    String value = this.properties.getProperty(SAVE_MITOS_3DFILES_KEY, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if 3d mito cuboids file must be saved.
   * @return Returns the mitoCuboids3dFile
   */
  public boolean isSaveMitoCuboids3dFile() {

    String value = this.properties.getProperty(SAVE_MITOS_CUBOIDS_3DFILES_KEY,
        "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Get the unit.
   * @return Returns the unit
   */
  public String getUnit() {

    return this.properties.getProperty(UNIT_KEY);
  }

  /**
   * Test if 3d files must be saved.
   * @return Returns the visualizationFiles
   */
  public boolean isSaveVisualizationFiles() {

    String value = this.properties.getProperty(SAVE_3DFILES_KEY, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Get the z factor.
   * @return Returns the zFactor
   */
  public float getZFactor() {

    String value = this.properties.getProperty(ZFACTOR_KEY, "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Get messenger cuboids size.
   * @return Returns the cuboidSize
   */
  public float getCuboidSize() {

    String value = this.properties.getProperty(CUBOID_SIZE_KEY, "1.0");

    return Float.parseFloat(value.trim());
  }

  //
  // Setters
  //

  /**
   * Set if the iv file must be saved.
   * @param saveIVFile true if the iv file must be saved
   */
  public void setSaveIVFile(final boolean saveIVFile) {

    this.properties.setProperty(SAVE_IV_FILE_SAVED, Boolean
        .toString(saveIVFile));
  }

  /**
   * Set if the cuboid size for messenger must be set automaticaly.
   * @param autoCuboidSize The autoCuboidSize to set
   */
  public void setAutoCuboidSize(final boolean autoCuboidSize) {

    this.properties.setProperty(AUTO_CUBOID_SIZE_KEY, Boolean
        .toString(autoCuboidSize));
  }

  /**
   * Set if data file must be saved.
   * @param dataFile The dataFile to set
   */
  public void setSaveDataFile(final boolean dataFile) {

    this.properties.setProperty(SAVE_DATA_FILE_KEY, Boolean.toString(dataFile));
  }

  /**
   * Set if 3d distances file must be saved.
   * @param distances3dFile The distances3dFile to set
   */
  public void setSaveDistances3dFile(final boolean distances3dFile) {

    this.properties.setProperty(SAVE_DISTANCES_3DFILES_KEY, Boolean
        .toString(distances3dFile));
  }

  /**
   * Set the factor.
   * @param factor The factor to set
   */
  public void setFactor(final float factor) {

    this.properties.setProperty(FACTOR_KEY, Float.toString(factor));
  }

  /**
   * Set if 3d messengers file must be saved.
   * @param messengers3dFile The messengers3dFile to set
   */
  public void setSaveMessengers3dFile(final boolean messengers3dFile) {

    this.properties.setProperty(SAVE_MESSENGERS_3DFILES_KEY, Boolean
        .toString(messengers3dFile));
  }

  /**
   * Set if 3d messengers cuboids file must be saved.
   * @param messengersCuboids3dFile The messengersCuboids3dFile to set
   */
  public void setSaveMessengersCuboids3dFile(final boolean messengersCuboids3dFile) {

    this.properties.setProperty(SAVE_MESSENGERS_CUBOIDS_3DFILES_KEY, Boolean
        .toString(messengersCuboids3dFile));
  }

  /**
   * Set if 3d mito file must be saved.
   * @param mito3dFile The mito3dFile to set
   */
  public void setSaveMito3dFile(final boolean mito3dFile) {

    this.properties.setProperty(SAVE_MITOS_3DFILES_KEY, Boolean.toString(mito3dFile));
  }

  /**
   * Set if 3d mito cuboids file must be saved.
   * @param mitoCuboids3dFile The mitoCuboids3dFile to set
   */
  public void setSaveMitoCuboids3dFile(final boolean mitoCuboids3dFile) {

    this.properties.setProperty(SAVE_MITOS_CUBOIDS_3DFILES_KEY, Boolean
        .toString(mitoCuboids3dFile));
  }

  /**
   * Set the unit.
   * @param unit The unit to set
   */
  public void setUnit(final String unit) {

    this.properties.setProperty(UNIT_KEY, unit);
  }

  /**
   * Set if 3d files must be saved.
   * @param visualizationFiles The visualizationFiles to set
   */
  public void setSaveVisualizationFiles(final boolean visualizationFiles) {

    this.properties.setProperty(SAVE_3DFILES_KEY, Boolean
        .toString(visualizationFiles));
  }

  /**
   * Set the z factor.
   * @param factor The zFactor to set
   */
  public void setZFactor(final float zFactor) {

    this.properties.setProperty(ZFACTOR_KEY, Float.toString(zFactor));
  }

  /**
   * Set mesengers cuboids sizes.
   * @param cuboidSize The cuboidSize to set
   */
  public void setCuboidSize(final float cuboidSize) {

    this.properties.setProperty(CUBOID_SIZE_KEY, Float.toString(cuboidSize));
  }

  /**
   * Set if full results file must be saved.
   * @param visualizationFiles The visualizationFiles to set
   */
  public void setSaveFullResultFile(final boolean saveFullResultFile) {

    this.properties.setProperty(SAVE_FULL_RESULTS_KEY, Boolean
        .toString(saveFullResultFile));
  }

  //
  // Other methods
  // 

  public static String getConfigurationFilePath() {

    final String os = System.getProperty("os.name");
    final String home = System.getProperty("user.home");

    if (os.toLowerCase().startsWith("windows"))
      return home + File.separator + "Application Data" + "corsen.conf";

    return home + File.separator + ".corsen";
  }

  /**
   * Save Corsen options
   */
  public void saveOptions() throws IOException {

    saveOptions(new File(getConfigurationFilePath()));
  }

  /**
   * Save Corsen options
   * @param file File to save.
   * @throws IOException if an error occurs while writing the file
   */
  public void saveOptions(final File file) throws IOException {

    FileOutputStream fos = new FileOutputStream(file);

    this.properties.store(fos, " " + Globals.APP_NAME + " version "
        + Globals.APP_VERSION + " configuration file");

  }

  /**
   * Load Corsen options
   */
  public void loadOptions() throws IOException {

    loadOptions(new File(getConfigurationFilePath()));
  }

  /**
   * Load Corsen options
   * @param file
   * @throws IOException if an error occurs while reading the file
   */
  public void loadOptions(final File file) throws IOException {

    FileInputStream fis = new FileInputStream(file);

    this.properties.load(fis);
  }

}
