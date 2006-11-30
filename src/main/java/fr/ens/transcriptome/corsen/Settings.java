package fr.ens.transcriptome.corsen;

import java.awt.Color;
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

  private static final String ZFACTOR_KEY = "main.zFactor";
  private static final String FACTOR_KEY = "main.factor";
  private static final String UNIT_KEY = "main.unit";
  private static final String AUTO_CUBOID_SIZE_KEY = "main.autoCuboidSize";
  private static final String CUBOID_SIZE_KEY = "main.cuboidSize";
  private static final String SAVE_DATA_FILE_KEY = "save.dataFile";
  private static final String SAVE_IV_FILE_SAVED = "save.IVFile";
  private static final String SAVE_3DFILES_KEY = "save.3dFiles";
  private static final String SAVE_MESSENGERS_3DFILES_KEY = "save.3dFiles.messengers";
  private static final String SAVE_MESSENGERS_CUBOIDS_3DFILES_KEY = "save.3dFiles.messengersCuboids";
  private static final String SAVE_MITOS_3DFILES_KEY = "save.3dFiles.mitos";
  private static final String SAVE_MITOS_CUBOIDS_3DFILES_KEY = "save.3dFiles.mitosCuboids";
  private static final String SAVE_DISTANCES_3DFILES_KEY = "save.3dFiles.distances";
  private static final String SAVE_FULL_RESULTS_KEY = "save.fullResults";

  private static final String VISUALIZATION_POINTS_SIZE_KEY = "visualization.pointSize";
  private static final String VISUALIZATION_SHOW_SURFACE_LINES_KEY = "visualization.showSurfaceLines";
  private static final String VISUALIZATION_SURFACE_LINE_SIZE_KEY = "visualization.surfaceLinesSize";
  private static final String VISUALIZATION_DISTANCES_LINES_SIZE_KEY = "visualization.distanceLinesSize";
  private static final String VISUALIZATION_SHOW_NEGATIVE_DISTANCES_KEY = "visualization.showNegativeDistances";

  private static final String VISUALIZATION_COLOR_MESSENGERS_KEY = "visualization.color.messengers";
  private static final String VISUALIZATION_COLOR_MITOS_KEY = "visualization.color.mitos";
  private static final String VISUALIZATION_COLOR_BARYCENTERS_KEY = "visualization.color.barycenters";
  private static final String VISUALIZATION_COLOR_DISTANCES_KEY = "visualization.color.distances";
  private static final String VISUALIZATION_COLOR_BACKGROUND_KEY = "visualization.color.background";
  private static final String VISUALIZATION_COLOR_LEGEND_KEY = "visualization.color.legend";

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

    String value = this.properties.getProperty(SAVE_DISTANCES_3DFILES_KEY,
        "true");

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

    String value = this.properties.getProperty(SAVE_MESSENGERS_3DFILES_KEY,
        "true");

    return Boolean.valueOf(value);
  }

  /**
   * Test if 3d messengers ciboids file must be saved.
   * @return Returns the messengersCuboids3dFile
   */
  public boolean isSaveMessengersCuboids3dFile() {

    String value = this.properties.getProperty(
        SAVE_MESSENGERS_CUBOIDS_3DFILES_KEY, "false");

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

  /**
   * Get the size of the points in vizualisations.
   * @return The size of the points
   */
  public float getVisualizationPointsSize() {

    String value = this.properties.getProperty(VISUALIZATION_POINTS_SIZE_KEY,
        "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Get the size of the lines in vizualisations.
   * @return The size of the points
   */
  public float getVisualizationSurfaceLinesSize() {

    String value = this.properties.getProperty(
        VISUALIZATION_SURFACE_LINE_SIZE_KEY, "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Get the size of the distances lines in vizualisations.
   * @return The size of the points
   */
  public float getVisualizationDistancesLinesSize() {

    String value = this.properties.getProperty(
        VISUALIZATION_DISTANCES_LINES_SIZE_KEY, "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Test if surface lines must be show in visualizations.
   * @return true if surface lines must be show in visualizations
   */
  public boolean isVisualizationShowSurfaceLines() {

    String value = this.properties.getProperty(
        VISUALIZATION_SHOW_SURFACE_LINES_KEY, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if the distances lines must be shown in a different color if negative.
   * @return trueif the distances lines must be shown in a different color if
   *         negative
   */
  public boolean isVisualizationShowNegativeDistances() {

    String value = this.properties.getProperty(
        VISUALIZATION_SHOW_NEGATIVE_DISTANCES_KEY, "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Get the color of messengers
   * @return The color of the messengers
   */
  public Color getColorMessengers() {

    String value = this.properties.getProperty(
        VISUALIZATION_COLOR_MESSENGERS_KEY, colorToString(Color.GREEN));

    return colorFromString(value);
  }

  /**
   * Get the color of mitos
   * @return The color of the mitos
   */
  public Color getColorMitos() {

    String value = this.properties.getProperty(VISUALIZATION_COLOR_MITOS_KEY,
        colorToString(Color.RED));

    return colorFromString(value);
  }

  /**
   * Get the color of barycenters
   * @return The color of the barycenter
   */
  public Color getColorBaryCenters() {

    String value = this.properties.getProperty(
        VISUALIZATION_COLOR_BARYCENTERS_KEY, colorToString(Color.BLUE));

    return colorFromString(value);
  }

  /**
   * Get the color of distances
   * @return The color of the distances
   */
  public Color getColorDistances() {

    String value = this.properties.getProperty(
        VISUALIZATION_COLOR_DISTANCES_KEY, colorToString(Color.YELLOW));

    return colorFromString(value);
  }

  /**
   * Get the color of the background
   * @return The color of background
   */
  public Color getColorBackground() {

    String value = this.properties.getProperty(
        VISUALIZATION_COLOR_BACKGROUND_KEY, colorToString(Color.BLACK));

    return colorFromString(value);
  }
  
  /**
   * Get the color of the legend
   * @return The color of the legend
   */
  public Color getColorLegend() {

    String value = this.properties.getProperty(
        VISUALIZATION_COLOR_LEGEND_KEY, colorToString(Color.WHITE));

    return colorFromString(value);
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
  public void setSaveMessengersCuboids3dFile(
      final boolean messengersCuboids3dFile) {

    this.properties.setProperty(SAVE_MESSENGERS_CUBOIDS_3DFILES_KEY, Boolean
        .toString(messengersCuboids3dFile));
  }

  /**
   * Set if 3d mito file must be saved.
   * @param mito3dFile The mito3dFile to set
   */
  public void setSaveMito3dFile(final boolean mito3dFile) {

    this.properties.setProperty(SAVE_MITOS_3DFILES_KEY, Boolean
        .toString(mito3dFile));
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

  /**
   * Set the size of the points in vizualisations.
   * @param size The size of the points
   */
  public void setVisualizationPointsSize(final float size) {

    this.properties.setProperty(VISUALIZATION_POINTS_SIZE_KEY, Float
        .toString(size));
  }

  /**
   * Set the size of the lines in vizualisations.
   * @param size The size of the points
   */
  public void setVisualizationSurfaceLinesSize(final float size) {

    this.properties.setProperty(VISUALIZATION_SURFACE_LINE_SIZE_KEY, Float
        .toString(size));
  }

  /**
   * Set the size of the distances lines in vizualisations.
   * @param size The size of the points
   */
  public void setVisualizationDistancesLinesSize(final float size) {

    this.properties.setProperty(
        VISUALIZATION_DISTANCES_LINES_SIZE_KEY, Float.toString(size));
  }

  /**
   * Set if surface lines must be show in visualizations.
   * @param value if surface lines must be show in visualizations
   */
  public void setVisualizationShowSurfaceLines(final boolean value) {

    this.properties.setProperty(VISUALIZATION_SHOW_SURFACE_LINES_KEY, Boolean
        .toString(value));

  }

  /**
   * Set if the distances lines must be shown in a different color if negative.
   * @param value if the distances lines must be shown in a different color if
   *          negative
   */
  public void setVisualizationShowNegativeDistances(final boolean value) {

    this.properties.setProperty(VISUALIZATION_SHOW_NEGATIVE_DISTANCES_KEY,
        Boolean.toString(value));
  }

  /**
   * Set the color of messengers
   * @param color The color of messengers
   */
  public void setColorMessengers(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_MESSENGERS_KEY,
        colorToString(color));

  }

  /**
   * Set the color of mitos
   * @param color The color of mitos
   */
  public void setColorMitos(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_MITOS_KEY,
        colorToString(color));
  }

  /**
   * Set the color of barycenters
   * @param color The color of barycenters
   */
  public void setColorBaryCenters(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_BARYCENTERS_KEY,
        colorToString(color));

  }

  /**
   * Set the color of distances
   * @param color The color of the distances
   */
  public void setColorDistances(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_DISTANCES_KEY,
        colorToString(color));

  }

  /**
   * Set the color of the background
   * @param color The color of the background
   */
  public void setColorBackground(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_BACKGROUND_KEY,
        colorToString(color));
  }
  
  /**
   * Set the color of the legend.
   * @param color The color of the legend
   */
  public void setColorLegend(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_LEGEND_KEY,
        colorToString(color));
  }

  //
  // Other methods
  // 

  public Color colorFromString(final String s) {

    if (s == null)
      return null;

    String[] words = s.split("\\,");

    if (words.length < 3)
      return null;

    int r = Integer.parseInt(words[0].trim());
    int g = Integer.parseInt(words[1].trim());
    int b = Integer.parseInt(words[2].trim());

    if (words.length > 3) {

      int a = Integer.parseInt(words[3].trim());
      return new Color(r, g, b, a);
    }

    return new Color(r, g, b);
  }

  public String colorToString(final Color c) {

    if (c == null)
      return null;

    return c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ","
        + c.getAlpha();

  }

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
  public void saveSettings() throws IOException {

    saveSettings(new File(getConfigurationFilePath()));
  }

  /**
   * Save Corsen options
   * @param file File to save.
   * @throws IOException if an error occurs while writing the file
   */
  public void saveSettings(final File file) throws IOException {

    FileOutputStream fos = new FileOutputStream(file);

    this.properties.store(fos, " " + Globals.APP_NAME + " version "
        + Globals.APP_VERSION + " configuration file");

  }

  /**
   * Load Corsen options
   */
  public void loadSettings() throws IOException {

    loadSettings(new File(getConfigurationFilePath()));
  }

  /**
   * Load Corsen options
   * @param file
   * @throws IOException if an error occurs while reading the file
   */
  public void loadSettings(final File file) throws IOException {

    FileInputStream fis = new FileInputStream(file);

    this.properties.load(fis);
  }

}
