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
 * or to join the Corsen google group, visit the home page
 * at:
 *
 *      http://transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen.model;

import fr.ens.transcriptome.corsen.Globals;

/**
 * This class define a factory for AbstractListPoint2D.
 * @author Laurent Jourdren
 */
public final class ListPoint2DFactory {

  private static boolean packed = Globals.LIST_POINT_PACKED_MODE;

  /**
   * Set the backend to use to store list of Points
   * @param packedMode true enable packed mode
   */
  public static void setPackedMode(final boolean packedMode) {

    packed = packedMode;
  }

  /**
   * Test if the packed mode is enable
   * @return true if the packed mode is enabled
   */
  public static boolean isPackedMode() {

    return packed;
  }

  /**
   * Create a AbstractListPoint3D object.
   * @return a new AbstractListPoint3D object
   */
  public static AbstractListPoint2D createListPoint2D() {

    if (packed)
      return new ArrayListPackedPoint2D();

    return new ArrayListPoint2D();
  }

  /**
   * Create a AbstractListPoint2D object.
   * @param xPrecision Precision for x values.
   * @param yPrecision Precision for y values.
   * @return a new AbstractListPoint2D object
   */
  public static AbstractListPoint2D createListPoint2D(final float xPrecision,
      final float yPrecision) {

    if (packed)
      return new ArrayListPackedPoint2D(xPrecision, yPrecision);

    return new ArrayListPoint2D();
  }

}
