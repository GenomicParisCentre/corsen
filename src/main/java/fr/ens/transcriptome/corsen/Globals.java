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

public final class Globals {

  
  
  /** The name of the application. */
  public static final String APP_NAME = "CorsenSwing";

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
  
  public static enum GUI { SWING, QT, FAKE}; 
  
  public static final GUI DEFAULT_GUI = GUI.FAKE;

}
