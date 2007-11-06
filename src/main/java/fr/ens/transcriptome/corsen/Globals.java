package fr.ens.transcriptome.corsen;

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

public final class Globals {

  /** The name of the application. */
  public static final String APP_NAME = "Corsen";

  /** The version of the application. */
  public static final String APP_VERSION = getVersion();

  /** The built number of the application. */
  public static final String APP_BUILD_NUMBER = getBuiltNumber();

  /** The build date of the application. */
  public static final String APP_BUILD_DATE = getBuiltDate();

  public static final boolean IS_JAR = isJar();

  public static final int THREAD_NUMBER_DEFAULT = 0;
  
  public static final boolean DEBUG = true;
  public static final boolean DEBUG_HOME_DIR = IS_JAR ? false : true;
  public static final boolean SHOW_BUILT = IS_JAR ? true : false;

  public static final float Z_COEF_DEFAULT = 2.96f;
  public static final float CUBOID_SIZE_FACTOR = 2.1f;
  public static final float PIXEL_SIZE_DEFAULT = 1.0f;

  private static final String WEBSITE_URL_DEFAULT = "http://transcriptome.ens.fr/corsen";
  public static final String WEBSITE_URL = getWebSiteURL();
  public static final String HANDBOOK_URL = WEBSITE_URL + "/handbook.html";
  public static final String REPORT_BUG_URL = "mailto:jourdren@biologie.ens.fr?subject=Corsen Report Bug&body=Report here the CorsenSwing bug";

  public static enum GUI {
    CLI, SWING, QT, FAKE
  };

  public static final GUI DEFAULT_GUI = GUI.QT;

  public static final String EXTENSION_PARTICLES_FILE = ".par";

  public static final String EXTENSION_PARTICLES_A_IV_FILE = "_messengers.iv";

  public static final String EXTENSION_PARTICLES_A_RGL_FILE = "_messengers.R";

  public static final String EXTENSION_PARTICLES_A_CUBOIDS_IV_FILE = "_cuboids.iv";

  public static final String EXTENSION_PARTICLES_B_CUBOIDS_RGL_FILE = "_mitos_cuboids.R";

  public static final String EXTENSION_PARTICLES_B_RGL_FILE = "_mitos.R";

  public static final String EXTENSION_PARTICLES_B_IV_FILE = "_mitos.iv";

  public static final String EXTENSION_PARTICLES_A_CUBOIDS_RGL_FILE = "_cuboids.R";

  public static final String EXTENSION_DISTANCES_FILE = "_distances.R";

  public static final String EXTENSION_DATA_FILE = ".data";

  public static final String EXTENSION_RESULT_FILE = ".result";

  public static final String EXTENSION_FULL_RESULT_FILE = ".fullresult";

  public static final String LICENCE_TXT = "This program is developed under the GNU General Public Licence.";

  public static final String ABOUT_TXT = Globals.APP_NAME
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
      + "  Mathilde Garcia(**) <garcia@biologie.ens.fr> Project leader, R programming, ImageJ scripting, testing.\n\n"
      + "(*)  Plate-forme transcriptome, �cole Normale Sup�rieure\n"
      + "(**) Laboratoire de G�n�tique Mol�culaire, �cole Normale Sup�rieure\n\n"
      + "Copyright 2006 �cole Normale Sup�rieure.\n" + LICENCE_TXT + "\n";

  public static final String ABOUT_HTML = "<p><b>"
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
      + "<ul><li><a href=\"mailto:jourdren@biologie.ens.fr?subject=CorsenSwing\">Laurent Jourdren</a><br/>"
      + "<a href=\"http://transcriptome.ens.fr\">Microarray platform, �cole Normale Sup�rieure</a>"
      + "<br/>Main developer, maintener.</li><br/>"
      + "<li><a href=\"mailto:garcia@biologie.ens.fr?subject=CorsenSwing\">Mathilde Garcia</a><br/>"
      + "<a href=\"http://www.biologie.ens.fr/lgmgml\">Laboratoire de G�n�tique Mol�culaire, �cole Normale Sup�rieure</a>"
      + "<br/>Project leader, R programming, ImageJ scripting, testing.</li></ul>"
      + "<p>Copyright 2006 �cole Normale Sup�rieure.<br/>" + LICENCE_TXT
      + "</p>";

  private static String getVersion() {

    String s = Globals.class.getPackage().getSpecificationVersion();

    return s != null ? s : "UNKNOWN VERSION";
  }

  private static String getBuiltNumber() {

    String s = Globals.class.getPackage().getImplementationVersion();

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

  private static final String getManifestProperty(final String propertyKey) {

    if (propertyKey == null)
      return null;

    try {
      java.net.URL codeBase = Globals.class.getProtectionDomain()
          .getCodeSource().getLocation();

      if (codeBase.getPath().endsWith(".jar")) {
        java.util.jar.JarInputStream jin = new java.util.jar.JarInputStream(
            codeBase.openStream());
        java.util.jar.Manifest mf = jin.getManifest();

        java.util.Map entries = mf.getMainAttributes();

        for (java.util.Iterator it = entries.keySet().iterator(); it.hasNext();) {

          java.util.jar.Attributes.Name key = (java.util.jar.Attributes.Name) it
              .next();

          if (propertyKey.equals(key.toString())) {
            return (String) entries.get(key);

          }
        }

        jin.close();
      }
    } catch (Exception e) {
      return null;
    }

    return null;
  }

  private static final boolean isJar() {

    java.net.URL codeBase = Globals.class.getProtectionDomain().getCodeSource()
        .getLocation();

    return codeBase.getPath().endsWith(".jar");
  }

  /**
   * Define the title of the the windows of the gui
   * @return the title of the windows
   */
  public static final String getWindowsTitle() {

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
}
