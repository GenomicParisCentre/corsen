public class ProgressEvent {

  public static final int START_READ_MESSENGERS_EVENT = 1;
  public static final int START_WRITE_RPLOT_MESSENGERS_EVENT = 2;
  public static final int START_READ_MITOS_EVENT = 3;
  public static final int START_WRITE_RPLOT_MITOS_EVENT = 4;
  public static final int START_CALC_MESSENGERS_CUBOIDS_EVENT = 5;
  public static final int START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT = 6;
  public static final int START_CALC_MITOS_CUBOIDS_EVENT = 7;
  public static final int START_WRITE_RESULT_CUBOIDS_EVENT = 8;
  public static final int START_WRITE_INTENSITIES_VOLUMES_EVENT = 9;
  public static final int START_WRITE_FULLRESULT_MESSAGERS_EVENT = 10;
  public static final int START_WRITE_FULLRESULT_CUBOIDS_EVENT = 11;
  public static final int PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT = 100;
  public static final int PROGRESS_CALC_MITOS_CUBOIDS_EVENT = 101;

  public static final int START_CELLS_EVENT = 1000;
  public static final int START_CELL_EVENT = 1001;

  public static final int END_CELLS_SUCCESSFULL_EVENT = 2000;
  public static final int END_CELL_EVENT = 2001;
  public static final int END_ERROR_EVENT = 3000;

  public static final int PHASE_COUNT = START_WRITE_FULLRESULT_CUBOIDS_EVENT;
  public static final int INDEX_IN_PHASE_MAX = 1000;

  private int id;
  private int intValue1;
  private int intValue2;
  private String stringValue1;
  private String stringValue2;
  private String stringValue3;

  /**
   * Get the event id.
   * @return Returns the id
   */
  public int getId() {
    return this.id;
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

    if (!settings.isSaveMito3dFile())
      count--;
    if (!settings.isSaveMessengers3dFile())
      count--;
    if (!settings.isSaveMessengers3dFile())
      count--;
    if (!settings.isSaveDataFile())
      count--;
    if (!settings.isSaveIVFile())
      count--;
    if (!settings.isSaveFullResultsFile())
      count = count - 2;

    return count;
  }

  //
  // Constructors
  //

  public ProgressEvent(final int id) {
    this.id = id;
  }

  public ProgressEvent(final int id, final int value1) {

    this(id);
    this.intValue1 = value1;
  }

  public ProgressEvent(final int id, final int value1, final int value2) {

    this(id, value1);
    this.intValue2 = value2;
  }

  public ProgressEvent(final int id, final int intValue1, final int intValue2,
      final String stringValue1, final String stringValue2, final String stringValue3) {

    this(id, intValue1, intValue2);
    this.stringValue1 = stringValue1;
    this.stringValue2 = stringValue2;
    this.stringValue3 = stringValue3;
  }

}
