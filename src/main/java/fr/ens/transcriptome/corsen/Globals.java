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
  public static final String APP_VERSION = "0.21-dev";

  public static final boolean DEBUG = true;
  public static final boolean DEBUG_HOME_DIR = true;

  public static final float Z_COEF_DEFAULT = 2.96f;
  public static final float CUBOID_SIZE_FACTOR = 2.1f;
  public static final float PIXEL_SIZE_DEFAULT = 1.0f;

  public static final String WEBSITE_URL = "http://transcriptome.ens.fr/corsen";
  public static final String HANDBOOK_URL = "http://transcriptome.ens.fr/corsen/handbook.html";
  public static final String REPORT_BUG_URL = "mailto:jourdren@biologie.ens.fr?subject=CorsenSwing Report Bug&body=Report here the CorsenSwing bug";

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
  
  
  public static final String ABOUT_TXT = 
     Globals.APP_NAME
    + " version "
    + Globals.APP_VERSION
    + " is a software to calc the distances between "
    + "particles.\n"
    + "Authors:\n"
    + "  Laurent Jourdren(*) <jourdren@biologie.ens.fr> Main developer, maintener.\n"
    + "  Mathilde Garcia(**) <garcia@biologie.ens.fr> Project leader, R programming, ImageJ scripting, testing.\n\n"
    + "(*)  Plate-forme transcriptome, École Normale Supérieure\n"
    + "(**) Laboratoire de Génétique Moléculaire, École Normale Supérieure\n\n"
    + "Copyright 2006 École Normale Supérieure.\n"
    + LICENCE_TXT + "\n";

  
  public static final String ABOUT_HTML = "<p><b>" 
                + Globals.APP_NAME
                + " version "
                + Globals.APP_VERSION
                + "</b> is a software to calc the distances between "
                + "mitochondria and mRNA.</p><br/>"
                + "<b>Authors</b>:"
                + "<ul><li><a href=\"mailto:jourdren@biologie.ens.fr?subject=CorsenSwing\">Laurent Jourdren</a><br/>"
                + "<a href=\"http://transcriptome.ens.fr\">Microarray platform, École Normale Supérieure</a>"
                + "<br/>Main developer, maintener.</li><br/>"
                + "<li><a href=\"mailto:garcia@biologie.ens.fr?subject=CorsenSwing\">Mathilde Garcia</a><br/>"
                + "<a href=\"http://www.biologie.ens.fr/lgmgml\">Laboratoire de Génétique Moléculaire, École Normale Sup�rieure</a>"
                + "<br/>Project leader, R programming, ImageJ scripting, testing.</li></ul>"
                + "<p>Copyright 2006 École Normale Supérieure.<br/>"
                + LICENCE_TXT + "</p>";

  
 
}
