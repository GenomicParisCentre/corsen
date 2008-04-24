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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Globals constants of the application.
 * @author Laurent Jourdren
 */
public final class Globals {

  private static Properties manifestProperties;
  private static final String MANIFEST_PROPERTIES_FILE = "/manifest.txt";

  /** The name of the application. */
  public static final String APP_NAME = "Corsen";

  /** The name of the application. */
  public static final String APP_NAME_LOWER_CASE = APP_NAME.toLowerCase();

  /** The version of the application. */
  public static final String APP_VERSION = getVersion();

  /** The built number of the application. */
  public static final String APP_BUILD_NUMBER = getBuiltNumber();

  /** The build date of the application. */
  public static final String APP_BUILD_DATE = getBuiltDate();

  /** The number of threads to use for computation. */
  public static final int THREAD_NUMBER_DEFAULT = -1;

  private static final boolean IS_JAR = isJar();

  /** Use debug home dir. */
  public static final boolean DEBUG_HOME_DIR = IS_JAR ? false : true;
  /** Show built version. */
  public static final boolean SHOW_BUILT = IS_JAR ? true : false;

  /** Default Z coef value. */
  public static final float Z_COEF_DEFAULT = 2.96f;
  /** Default cuboid size. */
  public static final float CUBOID_SIZE_FACTOR = 2.1f;
  /** Default pixel size. */
  public static final float PIXEL_SIZE_DEFAULT = 1.0f;

  /** Type of list of points to use. */
  public static final boolean LIST_POINT_PACKED_MODE = false;

  private static final String WEBSITE_URL_DEFAULT =
      "http://transcriptome.ens.fr/corsen";

  /** Corsen Website url. */
  public static final String WEBSITE_URL = getWebSiteURL();
  /** Corsen handbook url. */
  public static final String HANDBOOK_URL = WEBSITE_URL + "/handbook.html";
  /** Corsen report bug url. */
  public static final String REPORT_BUG_URL =
      "mailto:jourdren@biologie.ens.fr?subject=Corsen Report Bug&body=Report here the Corsen bug";

  /** Enum of available ui. */
  public static enum GUI {
    /** Command line UI. */
    CLI,
    /** Swing GUI. */
    SWING,
    /** Qt GUI. */
    QT,
    /** Fake UI. */
    FAKE
  };

  /** Default ui to use. */
  public static final GUI DEFAULT_GUI = GUI.QT;

  /** Par files extension. */
  public static final String EXTENSION_PARTICLES_FILE = ".par";

  /** Iv files extension. */
  public static final String EXTENSION_IV_FILE = ".iv";

  /** R files extension. */
  public static final String EXTENSION_RGL_FILE = ".R";

  /** Distances files extension. */
  public static final String EXTENSION_DISTANCES_FILE = "_distances.R";

  /** Data file extension. */
  public static final String EXTENSION_DATA_FILE = ".data";

  /** Result file extension. */
  public static final String EXTENSION_RESULT_FILE = ".result";

  /** Full result extension. */
  public static final String EXTENSION_FULL_RESULT_FILE = ".fullresult";

  /** Licence text. */
  public static final String LICENCE_TXT =
      "This program is developed under the GNU General Public Licence version 2 or later.";

  private static final String COPYRIGHT_DATE = "2006-2008";

  /** About string, plain text version. */
  public static final String ABOUT_TXT =
      Globals.APP_NAME
          + " version "
          + Globals.APP_VERSION
          + " ("
          + Globals.APP_BUILD_NUMBER
          + ")"
          + " is a software to calc the distances between "
          + "particles.\n"
          + "\nThis version has been built on "
          + APP_BUILD_DATE
          + ".\n\n"
          + "Authors:\n"
          + "  Laurent Jourdren(*) <jourdren@biologie.ens.fr> Main developer, maintener.\n"
          + "  Mathilde Garcia(**) <garcia@biologie.ens.fr> Project leader,"
          + " R programming, ImageJ scripting, testing.\n\n"
          + "(*)  Plate-forme transcriptome, École Normale Supérieure\n"
          + "(**) Laboratoire de Génétique Moléculaire, École Normale Supérieure\n\n"
          + "Copyright " + COPYRIGHT_DATE + " École Normale Supérieure.\n"
          + LICENCE_TXT + "\n";

  /** About string, html version. */
  public static final String ABOUT_HTML =
      "<p><b>"
          + Globals.APP_NAME
          + " version "
          + Globals.APP_VERSION
          + " ("
          + Globals.APP_BUILD_NUMBER
          + ")"
          + "</b> is a software to calc the distances between "
          + "mitochondria and mRNA.</p><br/>"
          + "<br/>This version has been built on "
          + APP_BUILD_DATE
          + ".<br/><br/>"
          + "<b>Authors</b>:"
          + "<ul><li><a href=\"mailto:jourdren@biologie.ens.fr?"
          + "subject=CorsenSwing\">Laurent Jourdren</a><br/>"
          + "<a href=\"http://transcriptome.ens.fr\">"
          + "Microarray platform, École Normale Supérieure</a>"
          + "<br/>Main developer, maintener.</li><br/>"
          + "<li><a href=\"mailto:garcia@biologie.ens.fr?"
          + "subject=CorsenSwing\">Mathilde Garcia</a><br/>"
          + "<a href=\"http://www.biologie.ens.fr/lgmgml\">"
          + "Laboratoire de Génétique Moléculaire, "
          + "École Normale Supérieure</a>"
          + "<br/>Project leader, R programming, ImageJ scripting, testing.</li></ul>"
          + "<p>Copyright " + COPYRIGHT_DATE
          + " École Normale Supérieure.<br/>" + LICENCE_TXT + "</p>";

  private static String getVersion() {

    String s = getManifestProperty("Specification-Version");

    return s != null ? s : "UNKNOWN VERSION";
  }

  private static String getBuiltNumber() {

    String s = getManifestProperty("Implementation-Version");

    return s != null ? s : "UNKNOWN BUILT";
  }

  private static String getBuiltDate() {

    final String unknown = "UNKNOWN DATE";

    String s = getManifestProperty("Built-Date");

    return s != null ? s : unknown;
  }

  private static String getWebSiteURL() {

    String s = getManifestProperty("url");

    return s != null ? s : WEBSITE_URL_DEFAULT;
  }

  private static String getManifestProperty(final String propertyKey) {

    if (propertyKey == null)
      return null;

    readManifest();

    return manifestProperties.getProperty(propertyKey);
  }

  private static boolean isJar() {

    return manifestProperties.size() != 0;
  }

  /**
   * Define the title of the the windows of the gui
   * @return the title of the windows
   */
  public static String getWindowsTitle() {

    StringBuffer sb = new StringBuffer();
    sb.append(APP_NAME);
    sb.append(" ");
    sb.append(APP_VERSION);

    if (Globals.SHOW_BUILT) {
      sb.append(" ");
      sb.append(Globals.APP_BUILD_NUMBER);
      sb.append(" (");
      sb.append(Globals.APP_BUILD_DATE);
      sb.append(")");
    }

    return sb.toString();
  }

  private static void readManifest() {

    if (manifestProperties != null)
      return;

    try {
      manifestProperties = new Properties();

      InputStream is =
          Globals.class.getResourceAsStream(MANIFEST_PROPERTIES_FILE);

      if (is == null)
        return;

      manifestProperties.load(is);
    } catch (IOException e) {
    }
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private Globals() {
  }

}
