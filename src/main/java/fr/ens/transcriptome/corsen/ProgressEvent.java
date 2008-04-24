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

/**
 * This class define an event that is send to UpdateStatus interfaces.
 * @author Laurent Jourdren
 */
public class ProgressEvent {

  /**
   * Define a enum for particles process phases.
   * @author Laurent Jourdren
   */
  public enum ProgressEventType {

    START_READ_MESSENGERS_EVENT("Read particles A PAR file"),
    START_READ_MITOS_EVENT("Read particles B PAR file"),
    START_CHANGE_MESSENGERS_COORDINATES_EVENT(
        "Transform particles A coordinates"),
    START_CHANGE_MITOS_COORDINATES_EVENT("Transform particles B coordinates"),
    START_FILTER_MESSENGERS_EVENT("Filter particles A"),
    START_FILTER_MITOS_EVENT("Filter particles B"),
    START_CALC_MESSENGERS_CUBOIDS_EVENT("Calc particles A cuboids"),
    START_CALC_MITOS_CUBOIDS_EVENT("Calc particles B cuboids"),
    START_CALC_MIN_DISTANCES_EVENT("Calc distances"), START_DISTANCES_ANALYSIS(
        "Distances analysis"), START_WRITE_DATA_EVENT(
        "Write results data file for R"), START_WRITE_IV_MESSENGERS_EVENT(
        "Write particles A intensities and volumes"),
    START_WRITE_IV_MITOS_EVENT("Write particles B intensities and volumes"),
    START_WRITE_IV_MESSENGERS_CUBOIDS_EVENT(
        "Write particles A cuboids intensities and volumes"),
    START_WRITE_RESULT_EVENT("Write results"), START_WRITE_FULLRESULT_EVENT(
        "Write full results"), START_WRITE_RPLOT_MESSENGERS_EVENT(
        "Write particles B R plot file"), START_WRITE_RPLOT_MITOS_EVENT(
        "Write particles A R plot file"),
    START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT(
        "Write particles A cuboids R plot file"),
    START_WRITE_RPLOT_MITOS_CUBOIDS_EVENT(
        "Write particles B cuboids R plot file"),
    START_WRITE_RPLOT_DISTANCES_EVENT("Write distances R plot file"),
    PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT(""),
    PROGRESS_CALC_MITOS_CUBOIDS_EVENT(""), PROGRESS_CALC_DISTANCES_EVENT(""),
    START_CELLS_EVENT(""), START_CELL_EVENT(""),
    END_CELLS_SUCCESSFULL_EVENT(""), END_CELL_EVENT(""), END_ERROR_EVENT("");

    private String description;

    /**
     * Get the description of the type of the particle.
     * @return the description of the particle
     */
    public String toString() {

      return this.description;
    }

    //
    // Constructor
    //

    /**
     * Private constructor.
     * @param description The description of the particle
     */
    private ProgressEventType(final String description) {

      this.description = description;
    }
  }

  /** The number of phase in a process. */
  public static final int PHASE_COUNT =
      ProgressEventType.START_WRITE_RPLOT_DISTANCES_EVENT.ordinal();
  /** The number of indexes in a phase. */
  public static final int INDEX_IN_PHASE_MAX = 1000;

  private ProgressEventType type;
  private int intValue1;
  private int intValue2;
  private String stringValue1;
  private String stringValue2;
  private String stringValue3;

  /**
   * Get the event type.
   * @return Returns the type
   */
  public ProgressEventType getType() {
    return this.type;
  }

  /**
   * Get the int value1.
   * @return Returns the intValue1
   */
  public int getIntValue1() {
    return this.intValue1;
  }

  /**
   * Get the int value2.
   * @return Returns the intValue2
   */
  public int getIntValue2() {
    return this.intValue2;
  }

  /**
   * Get the String value1
   * @return Returns the stringValue1
   */
  public String getStringValue1() {
    return stringValue1;
  }

  /**
   * Get the String value2
   * @return Returns the stringValue2
   */
  public String getStringValue2() {
    return stringValue2;
  }

  /**
   * Get the String value3
   * @return Returns the stringValue3
   */
  public String getStringValue3() {
    return stringValue3;
  }

  //
  // Other methods
  //

  /**
   * Count the number of phases for a process.
   * @return the number of phases for a process
   */
  public static int countPhase(final Settings settings) {

    if (settings == null)
      return 0;

    int count = PHASE_COUNT;

    if (!settings.isSaveVisualizationFiles())
      count -= 5;
    else {

      if (!settings.isSaveParticleB3dFile())
        count--;
      if (!settings.isSaveParticleA3dFile())
        count--;
      if (!settings.isSaveParticlesACuboids3dFile())
        count--;
      if (!settings.isSaveParticlesBCuboids3dFile())
        count--;
      if (!settings.isSaveDistances3dFile())
        count--;
    }

    if (!settings.isSaveDataFile())
      count--;
    if (!settings.isSaveIVFile())
      count -= 3;
    if (!settings.isSaveFullResultsFile())
      count--;

    return count;
  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   * @param type the phase of the event
   */
  public ProgressEvent(final ProgressEventType type) {
    this.type = type;
  }

  /**
   * Public constructor.
   * @param type the phase of the event
   * @param intValue1 first integer argument of the event
   */
  public ProgressEvent(final ProgressEventType type, final int intValue1) {

    this(type);
    this.intValue1 = intValue1;
  }

  /**
   * Public constructor.
   * @param type the phase of the event
   * @param intValue1 first integer argument of the event
   * @param intValue2 second argument value of the event
   */
  public ProgressEvent(final ProgressEventType type, final int intValue1,
      final int intValue2) {

    this(type, intValue1);
    this.intValue2 = intValue2;
  }

  /**
   * Public constructor.
   * @param type the phase of the event
   * @param intValue1 first integer argument of the event
   * @param intValue2 second argument value of the event
   * @param stringValue1 first string argument of the event
   * @param stringValue2 second string argument of the event
   * @param stringValue3 third string argument of the event
   */
  public ProgressEvent(final ProgressEventType type, final int intValue1,
      final int intValue2, final String stringValue1,
      final String stringValue2, final String stringValue3) {

    this(type, intValue1, intValue2);
    this.stringValue1 = stringValue1;
    this.stringValue2 = stringValue2;
    this.stringValue3 = stringValue3;
  }

}
