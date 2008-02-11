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
 * of the �cole Normale Sup�rieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import fr.ens.transcriptome.corsen.calc.ParticleType;

/**
 * This class defines settings of the application.
 */
public final class Settings {

  private static final String ZFACTOR_KEY = "main.zFactor";
  private static final String FACTOR_KEY = "main.factor";
  private static final String UNIT_KEY = "main.unit";

  private static final String AUTO_CUBOID_SIZE_KEY = "main.autoCuboidSize";
  private static final String CUBOID_SIZE_KEY = "main.cuboidSize";

  private static final String PARTICLES_A_NAME = "particles.a.name";
  private static final String PARTICLES_A_TYPE = "particles.a.type";
  private static final String PARTICLES_A_BATCH_PREFIX =
      "particles.a.batch.prefix";
  private static final String PARTICLES_A_PROPERTIES =
      "particles.a.properties.";

  private static final String PARTICLES_B_NAME = "particles.b.name";
  private static final String PARTICLES_B_TYPE = "particles.b.type";
  private static final String PARTICLES_B_BATCH_PREFIX =
      "particles.b.batch.prefix";
  private static final String PARTICLES_B_PROPERTIES = "particles.b.properties";

  private static final String THREAD_NUMBER = "threads.number";

  private static final String SAVE_DATA_FILE_KEY = "save.dataFile";
  private static final String SAVE_IV_FILE_SAVED = "save.IVFile";
  private static final String SAVE_3DFILES_KEY = "save.3dFiles";
  private static final String SAVE_PARTICLES_A_3DFILES_KEY =
      "save.3dFiles.particles.a";
  private static final String SAVE_PARTICLES_A_CUBOIDS_3DFILES_KEY =
      "save.3dFiles.particles.a";
  private static final String SAVE_PARTICLES_B_3DFILES_KEY =
      "save.3dFiles.particles.b";
  private static final String SAVE_PARTICLES_B_CUBOIDS_3DFILES_KEY =
      "save.3dFiles.particles.b";
  private static final String SAVE_DISTANCES_3DFILES_KEY =
      "save.3dFiles.distances";
  private static final String SAVE_RESULTS_KEY = "save.results";
  private static final String SAVE_FULL_RESULTS_KEY = "save.fullResults";

  private static final String VISUALIZATION_POINTS_SIZE_KEY =
      "visualization.pointSize";
  private static final String VISUALIZATION_SHOW_SURFACE_LINES_KEY =
      "visualization.showSurfaceLines";
  private static final String VISUALIZATION_SURFACE_LINE_SIZE_KEY =
      "visualization.surfaceLinesSize";
  private static final String VISUALIZATION_DISTANCES_LINES_SIZE_KEY =
      "visualization.distanceLinesSize";
  private static final String VISUALIZATION_SHOW_NEGATIVE_DISTANCES_KEY =
      "visualization.showNegativeDistances";
  private static final String VISUALIZATION_SHOW_PARTICLES_A_DIFFERENT_COLORS_KEY =
      "visualization.showParticleADifferentColors";
  private static final String VISUALIZATION_SHOW_PARTICLES_B_DIFFERENT_COLORS_KEY =
      "visualization.showParticleBDifferentColors";

  private static final String VISUALIZATION_COLOR_PARTICLE_A_KEY =
      "visualization.color.particle.a";
  private static final String VISUALIZATION_COLOR_PARTICLE_B_KEY =
      "visualization.color.particle.b";
  private static final String VISUALIZATION_COLOR_BARYCENTERS_KEY =
      "visualization.color.barycenters";
  private static final String VISUALIZATION_COLOR_DISTANCES_KEY =
      "visualization.color.distances";
  private static final String VISUALIZATION_COLOR_BACKGROUND_KEY =
      "visualization.color.background";
  private static final String VISUALIZATION_COLOR_LEGEND_KEY =
      "visualization.color.legend";

  private static final String HISTOGRAM_RESULTS_NB_CLASSES_KEY =
      "histogram.results.classes";
  private static final String HISTOGRAM_HISTORY_NB_CLASSES_KEY =
      "histogram.history.classes";

  private static final String CLEAR_HISTORY_WHEN_NEW_CALC =
      "main.clear.history.new.calc";

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
  public boolean isSaveResultsFile() {

    String value = this.properties.getProperty(SAVE_RESULTS_KEY, "false");

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

    String value =
        this.properties.getProperty(SAVE_DISTANCES_3DFILES_KEY, "true");

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
  public boolean isSaveParticleA3dFile() {

    String value =
        this.properties.getProperty(SAVE_PARTICLES_A_3DFILES_KEY, "true");

    return Boolean.valueOf(value);
  }

  /**
   * Test if 3d messengers ciboids file must be saved.
   * @return Returns the messengersCuboids3dFile
   */
  public boolean isSaveParticlesACuboids3dFile() {

    String value =
        this.properties.getProperty(SAVE_PARTICLES_A_CUBOIDS_3DFILES_KEY,
            "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if 3d mito file must be saved.
   * @return Returns the mito3dFile
   */
  public boolean isSaveParticleB3dFile() {

    String value =
        this.properties.getProperty(SAVE_PARTICLES_B_3DFILES_KEY, "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if 3d mito cuboids file must be saved.
   * @return Returns the mitoCuboids3dFile
   */
  public boolean isSaveParticlesBCuboids3dFile() {

    String value =
        this.properties.getProperty(SAVE_PARTICLES_B_CUBOIDS_3DFILES_KEY,
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

    String value = this.properties.getProperty(SAVE_3DFILES_KEY, "false");

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

    String value =
        this.properties.getProperty(VISUALIZATION_POINTS_SIZE_KEY, "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Get the size of the lines in vizualisations.
   * @return The size of the points
   */
  public float getVisualizationSurfaceLinesSize() {

    String value =
        this.properties.getProperty(VISUALIZATION_SURFACE_LINE_SIZE_KEY, "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Get the size of the distances lines in vizualisations.
   * @return The size of the points
   */
  public float getVisualizationDistancesLinesSize() {

    String value =
        this.properties.getProperty(VISUALIZATION_DISTANCES_LINES_SIZE_KEY,
            "1.0");

    return Float.parseFloat(value.trim());
  }

  /**
   * Test if surface lines must be show in visualizations.
   * @return true if surface lines must be show in visualizations
   */
  public boolean isVisualizationShowSurfaceLines() {

    String value =
        this.properties.getProperty(VISUALIZATION_SHOW_SURFACE_LINES_KEY,
            "true");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Test if the distances lines must be shown in a different color if negative.
   * @return trueif the distances lines must be shown in a different color if
   *         negative
   */
  public boolean isVisualizationShowNegativeDistances() {

    String value =
        this.properties.getProperty(VISUALIZATION_SHOW_NEGATIVE_DISTANCES_KEY,
            "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Get the color of messengers
   * @return The color of the messengers
   */
  public Color getColorParticlesA() {

    String value =
        this.properties.getProperty(VISUALIZATION_COLOR_PARTICLE_A_KEY,
            colorToString(Color.GREEN));

    return colorFromString(value);
  }

  /**
   * Get the color of mitos
   * @return The color of the mitos
   */
  public Color getColorParticlesB() {

    String value =
        this.properties.getProperty(VISUALIZATION_COLOR_PARTICLE_B_KEY,
            colorToString(Color.RED));

    return colorFromString(value);
  }

  /**
   * Get the color of barycenters
   * @return The color of the barycenter
   */
  public Color getColorBaryCenters() {

    String value =
        this.properties.getProperty(VISUALIZATION_COLOR_BARYCENTERS_KEY,
            colorToString(Color.BLUE));

    return colorFromString(value);
  }

  /**
   * Get the color of distances
   * @return The color of the distances
   */
  public Color getColorDistances() {

    String value =
        this.properties.getProperty(VISUALIZATION_COLOR_DISTANCES_KEY,
            colorToString(Color.YELLOW));

    return colorFromString(value);
  }

  /**
   * Get the color of the background
   * @return The color of background
   */
  public Color getColorBackground() {

    String value =
        this.properties.getProperty(VISUALIZATION_COLOR_BACKGROUND_KEY,
            colorToString(Color.BLACK));

    return colorFromString(value);
  }

  /**
   * Get the color of the legend
   * @return The color of the legend
   */
  public Color getColorLegend() {

    String value =
        this.properties.getProperty(VISUALIZATION_COLOR_LEGEND_KEY,
            colorToString(Color.WHITE));

    return colorFromString(value);
  }

  /**
   * Get the name of the particle A.
   * @return The name of the particle A
   */
  public String getParticlesAName() {

    return this.properties.getProperty(PARTICLES_A_NAME, "Messengers");
  }

  /**
   * Get the type of the particle A.
   * @return The type of the particle A
   */
  public ParticleType getParticlesAType() {

    String val =
        this.properties.getProperty(PARTICLES_A_TYPE, ParticleType.TINY
            .toString());

    return ParticleType.getParticleType(val);
  }

  /**
   * Get the batch prefix of the particle A.
   * @return The batch prefix of the particle A
   */
  public String getParticlesABatchPrefix() {

    return this.properties.getProperty(PARTICLES_A_BATCH_PREFIX, "messengers_");
  }

  /**
   * Get the properties of the particle A.
   * @return The properties of the particle A
   */
  public Properties getParticlesAProperties() {

    Properties result = new Properties();

    Iterator it = this.properties.keySet().iterator();

    while (it.hasNext()) {

      String key = (String) it.next();

      if (key.startsWith(PARTICLES_A_PROPERTIES))
        result.setProperty(key.substring(PARTICLES_A_PROPERTIES.length(), key
            .length()), this.properties.getProperty(key));
    }

    return result;
  }

  /**
   * Get the name of the particle B.
   * @return The name of the particle B
   */
  public String getParticlesBName() {

    return this.properties.getProperty(PARTICLES_B_NAME, "Mitochondria");
  }

  /**
   * Get the type of the particle B.
   * @return The type of the particle B
   */
  public ParticleType getParticlesBType() {

    return ParticleType.getParticleType(this.properties.getProperty(
        PARTICLES_B_TYPE, ParticleType.HUGE.toString()));
  }

  /**
   * Get the batch prefix of the particle B.
   * @return The batch prefix of the particle B
   */
  public String getParticlesBBatchPrefix() {

    return this.properties.getProperty(PARTICLES_B_BATCH_PREFIX, "mitos_");
  }

  /**
   * Get the properties of the particle B.
   * @return The properties of the particle B
   */
  public Properties getParticlesBProperties() {

    Properties result = new Properties();

    // PARTICLE_A_PROPERTIES;

    Iterator it = this.properties.keySet().iterator();

    while (it.hasNext()) {

      String key = (String) it.next();

      if (key.startsWith(PARTICLES_B_PROPERTIES))
        result.setProperty(key.substring(PARTICLES_B_PROPERTIES.length(), key
            .length()), this.properties.getProperty(key));
    }

    return result;
  }

  /**
   * Get the number of thread to use
   * @return The number of thread to use
   */
  public int getThreadNumber() {

    String value =
        this.properties.getProperty(THREAD_NUMBER, ""
            + Globals.THREAD_NUMBER_DEFAULT);

    return Integer.parseInt(value.trim());
  }

  /**
   * Get if Particles A must be shown in differents colors.
   * @return true if Particles A must be shown in differents colors
   */
  public boolean isVisualisationParticlesAInDifferentsColor() {

    String value =
        this.properties.getProperty(
            VISUALIZATION_SHOW_PARTICLES_A_DIFFERENT_COLORS_KEY, "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Get if Particles A must be shown in differents colors.
   * @return true if Particles A must be shown in differents colors
   */
  public boolean isVisualisationParticlesBInDifferentsColor() {

    String value =
        this.properties.getProperty(
            VISUALIZATION_SHOW_PARTICLES_B_DIFFERENT_COLORS_KEY, "false");

    return Boolean.valueOf(value.trim());
  }

  /**
   * Get the number of classes for the result histogram.
   * @return the number of classes for the histogram results
   */
  public int getHistogramResultsNumberClasses() {

    String value =
        this.properties.getProperty(HISTOGRAM_RESULTS_NB_CLASSES_KEY, "50");

    return Integer.valueOf(value.trim());
  }

  /**
   * Get the number of classes for the history histogram.
   * @return the number of classes for the histogram results
   */
  public int getHistogramHistoryNumberClasses() {

    String value =
        this.properties.getProperty(HISTOGRAM_HISTORY_NB_CLASSES_KEY, "50");

    return Integer.valueOf(value.trim());
  }

  /**
   * Test if the history must be cleared when launching a new batch.
   * @return true if the history must be cleared when launching a new batch
   */
  public boolean isClearHistoryWhenLaunchingNewCalc() {

    String value =
        this.properties.getProperty(CLEAR_HISTORY_WHEN_NEW_CALC, "false");

    return Boolean.valueOf(value);
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
  public void setFactor(final String factor) {

    if (factor == null)
      return;

    try {
      setFactor(Float.parseFloat(factor.trim()));
    } catch (NumberFormatException e) {
    }
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
   * @param particlesA3dFile The messengers3dFile to set
   */
  public void setSaveParticlesA3dFile(final boolean particlesA3dFile) {

    this.properties.setProperty(SAVE_PARTICLES_A_3DFILES_KEY, Boolean
        .toString(particlesA3dFile));
  }

  /**
   * Set if 3d messengers cuboids file must be saved.
   * @param particlesACuboids3dFile The messengersCuboids3dFile to set
   */
  public void setSaveParticlesACuboids3dFile(
      final boolean particlesACuboids3dFile) {

    this.properties.setProperty(SAVE_PARTICLES_A_CUBOIDS_3DFILES_KEY, Boolean
        .toString(particlesACuboids3dFile));
  }

  /**
   * Set if 3d mito file must be saved.
   * @param partcilesB3dFile The mito3dFile to set
   */
  public void setSaveParticlesB3dFile(final boolean partcilesB3dFile) {

    this.properties.setProperty(SAVE_PARTICLES_B_3DFILES_KEY, Boolean
        .toString(partcilesB3dFile));
  }

  /**
   * Set if 3d mito cuboids file must be saved.
   * @param partcilesBCuboids3dFile The mitoCuboids3dFile to set
   */
  public void setSaveParticlesBCuboids3dFile(
      final boolean partcilesBCuboids3dFile) {

    this.properties.setProperty(SAVE_PARTICLES_B_CUBOIDS_3DFILES_KEY, Boolean
        .toString(partcilesBCuboids3dFile));
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
   * Set the Z factor.
   * @param factor The factor to set
   */
  public void setZFactor(final String zFactor) {

    if (zFactor == null)
      return;

    try {
      setZFactor(Float.parseFloat(zFactor.trim()));
    } catch (NumberFormatException e) {
    }
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
   * Set if results file must be saved.
   * @param visualizationFiles The visualizationFiles to set
   */
  public void setSaveResultFile(final boolean saveResultFile) {

    this.properties.setProperty(SAVE_RESULTS_KEY, Boolean
        .toString(saveResultFile));
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

    this.properties.setProperty(VISUALIZATION_DISTANCES_LINES_SIZE_KEY, Float
        .toString(size));
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
   *            negative
   */
  public void setVisualizationShowNegativeDistances(final boolean value) {

    this.properties.setProperty(VISUALIZATION_SHOW_NEGATIVE_DISTANCES_KEY,
        Boolean.toString(value));
  }

  /**
   * Set the color of messengers
   * @param color The color of messengers
   */
  public void setColorParticlesA(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_PARTICLE_A_KEY,
        colorToString(color));

  }

  /**
   * Set the color of mitos
   * @param color The color of mitos
   */
  public void setColorParticlesB(final Color color) {

    this.properties.setProperty(VISUALIZATION_COLOR_PARTICLE_B_KEY,
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

  /**
   * Set the name of the particle A.
   * @param name The name of the particle A
   */
  public void setParticlesAName(final String name) {

    this.properties.setProperty(PARTICLES_A_NAME, name);
  }

  /**
   * Set the type of the particle A.
   * @param type Type to set
   */
  public void setParticlesAType(final ParticleType type) {

    this.properties.setProperty(PARTICLES_A_TYPE, type.toString());
  }

  /**
   * Set the batch prefix of the particle A.
   * @param prefix Prefix to set
   */
  public void setParticlesABatchPrefix(final String prefix) {

    this.properties.setProperty(PARTICLES_A_BATCH_PREFIX, prefix);
  }

  /**
   * Set the properties of the particle A
   * @param properties Properties to set
   */
  public void setParticlesAProperties(final Properties properties) {

    if (properties == null)
      return;

    Iterator it = properties.keySet().iterator();

    while (it.hasNext()) {

      final String key = (String) it.next();

      this.properties.setProperty(PARTICLES_A_PROPERTIES + key, properties
          .getProperty(key));
    }
  }

  /**
   * Set the name of the particle B.
   * @param name The name of the particle B
   */
  public void setParticlesBName(final String name) {

    this.properties.setProperty(PARTICLES_B_NAME, name);
  }

  /**
   * Set the type of the particle B.
   * @param type Type to set
   */
  public void setParticlesBType(final ParticleType type) {

    this.properties.setProperty(PARTICLES_B_TYPE, type.toString());
  }

  /**
   * Set the batch prefix of the particle B.
   * @param prefix Prefix to set
   */
  public void setParticlesBBatchPrefix(final String prefix) {

    this.properties.setProperty(PARTICLES_B_BATCH_PREFIX, prefix);
  }

  /**
   * Set the properties of the particle B
   * @param properties Properties to set
   */
  public void setParticlesBProperties(final Properties properties) {

    if (properties == null)
      return;

    Iterator it = properties.keySet().iterator();

    while (it.hasNext()) {

      final String key = (String) it.next();

      this.properties.setProperty(PARTICLES_B_PROPERTIES + key, properties
          .getProperty(key));
    }
  }

  /**
   * Set the number of thread to use
   * @param threadNumber The number of thread to use
   */
  public void setThreadNumber(final int threadNumber) {

    this.properties.setProperty(THREAD_NUMBER, "" + threadNumber);
  }

  /**
   * Set if Particles A must be shown in different colors.
   * @param differentColors true if Particles A must be shown in different
   *            colors
   */
  public void setVisualisationParticlesAInDifferentsColors(
      final boolean differentColors) {

    this.properties.setProperty(
        VISUALIZATION_SHOW_PARTICLES_A_DIFFERENT_COLORS_KEY, Boolean
            .toString(differentColors));
  }

  /**
   * Set if Particles B must be shown in different colors.
   * @param differentColors true if Particles A must be shown in different
   *            colors
   */
  public void setVisualisationParticlesBInDifferentsColors(
      final boolean differentColors) {

    this.properties.setProperty(
        VISUALIZATION_SHOW_PARTICLES_B_DIFFERENT_COLORS_KEY, Boolean
            .toString(differentColors));
  }

  /**
   * Set the number of classes for the result histogram.
   * @param classes the number of classes for the histogram
   */
  public void setHistogramResultsNumberClasses(final int classes) {

    this.properties.setProperty(HISTOGRAM_RESULTS_NB_CLASSES_KEY, Integer
        .toString(classes));
  }

  /**
   * Set the number of classes for the history histogram.
   * @param classes the number of classes for the histogram
   */
  public void setHistogramHistoryNumberClasses(final int classes) {

    this.properties.setProperty(HISTOGRAM_HISTORY_NB_CLASSES_KEY, Integer
        .toString(classes));
  }

  public void setClearHistoryWhenLaunchNewCalc(final boolean value) {

    this.properties.setProperty(CLEAR_HISTORY_WHEN_NEW_CALC, Boolean
        .toString(value));
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

    return c.getRed()
        + "," + c.getGreen() + "," + c.getBlue() + "," + c.getAlpha();

  }

  public static String getConfigurationFilePath() {

    final String os = System.getProperty("os.name");
    final String home = System.getProperty("user.home");

    if (os.toLowerCase().startsWith("windows"))
      return home
          + File.separator + "Application DataDouble"
          + Globals.APP_NAME_LOWER_CASE + ".conf";

    return home + File.separator + "." + Globals.APP_NAME_LOWER_CASE;
  }

  /**
   * Save CorsenSwing options
   */
  public void saveSettings() throws IOException {

    saveSettings(new File(getConfigurationFilePath()));
  }

  /**
   * Save CorsenSwing options
   * @param file File to save.
   * @throws IOException if an error occurs while writing the file
   */
  public void saveSettings(final File file) throws IOException {

    FileOutputStream fos = new FileOutputStream(file);

    this.properties.store(fos, " "
        + Globals.APP_NAME + " version " + Globals.APP_VERSION
        + " configuration file");
    fos.close();
  }

  /**
   * Load CorsenSwing options
   */
  public void loadSettings() throws IOException {

    loadSettings(new File(getConfigurationFilePath()));
  }

  /**
   * Load CorsenSwing options
   * @param file
   * @throws IOException if an error occurs while reading the file
   */
  public void loadSettings(final String filename) throws IOException {

    if (filename == null)
      loadSettings();
    else
      loadSettings(new File(filename));
  }

  /**
   * Load CorsenSwing options
   * @param file
   * @throws IOException if an error occurs while reading the file
   */
  public void loadSettings(final File file) throws IOException {

    FileInputStream fis = new FileInputStream(file);

    this.properties.load(fis);
    fis.close();
  }

  /**
   * Add settings to current settings.
   * @param s Settings to add
   */
  public void addSettings(final Settings s) {

    if (s == null)
      return;

    Properties p = s.properties;

    Enumeration keys = p.keys();

    while (keys.hasMoreElements()) {

      String key = (String) keys.nextElement();

      this.properties.setProperty(key, p.getProperty(key));
    }

  }

}
