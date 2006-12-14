package fr.ens.transcriptome.corsen;

public class ProgressEvent {

  public enum ProgressEventType {

    START_READ_MESSENGERS_EVENT("Read messengers PAR file"),

    START_READ_MITOS_EVENT("Read mitos PAR file"),

    START_CHANGE_MESSENGERS_COORDINATES_EVENT("Transform messengers coordinates"), START_CHANGE_MITOS_COORDINATES_EVENT(
        "Transform mitos coordinates"), START_CALC_MESSENGERS_CUBOIDS_EVENT(
        "Calc messengers cuboids"), START_CALC_MITOS_CUBOIDS_EVENT(
        "Calc mitochondrions cuboids"), START_CALC_MIN_DISTANCES_EVENT(
        "Calc distances"),  START_WRITE_DATA_EVENT(
        "Write results data file for R"), START_WRITE_IV_MESSENGERS_EVENT(
        "Write messengers intensities and volumes"), START_WRITE_IV_MITOS_EVENT(
        "Write mitocondria intensities and volumes"), START_WRITE_IV_MESSENGERS_CUBOIDS_EVENT(
        "Write messengers cuboids intensities and volumes"), START_WRITE_FULLRESULT_EVENT(
        "Write full results"), START_WRITE_RPLOT_MESSENGERS_EVENT(
        "Write messengers R plot file"), START_WRITE_RPLOT_MITOS_EVENT(
        "Write mitos R plot file"), START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT(
        "Write messengers cuboids R plot file"), START_WRITE_RPLOT_MITOS_CUBOIDS_EVENT(
        "Write cuboids cuboids R plot file"), START_WRITE_RPLOT_DISTANCES_EVENT(
        "Write distances R plot file"), PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT(
        ""), PROGRESS_CALC_MITOS_CUBOIDS_EVENT(""), PROGRESS_CALC_DISTANCES_EVENT(
        ""),  START_CELLS_EVENT(""), START_CELL_EVENT(
        ""), END_CELLS_SUCCESSFULL_EVENT(""), END_CELL_EVENT(""), END_ERROR_EVENT(
        "");

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

  public static final int PHASE_COUNT = ProgressEventType.START_WRITE_RPLOT_DISTANCES_EVENT
      .ordinal();
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

  public static int countPhase(final Settings settings) {

    if (settings == null)
      return 0;

    int count = PHASE_COUNT;

    if (!settings.isSaveVisualizationFiles())
      count -= 5;
    else {

      if (!settings.isSaveMito3dFile())
        count--;
      if (!settings.isSaveMessengers3dFile())
        count--;
      if (!settings.isSaveMessengersCuboids3dFile())
        count--;
      if (!settings.isSaveMitoCuboids3dFile())
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

  public ProgressEvent(final ProgressEventType type) {
    this.type = type;
  }

  public ProgressEvent(final ProgressEventType type, final int value1) {

    this(type);
    this.intValue1 = value1;
  }

  public ProgressEvent(final ProgressEventType type, final int value1,
      final int value2) {

    this(type, value1);
    this.intValue2 = value2;
  }

  public ProgressEvent(final ProgressEventType type, final int intValue1,
      final int intValue2, final String stringValue1,
      final String stringValue2, final String stringValue3) {

    this(type, intValue1, intValue2);
    this.stringValue1 = stringValue1;
    this.stringValue2 = stringValue2;
    this.stringValue3 = stringValue3;
  }

}
