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

package fr.ens.transcriptome.corsen.model;

/**
 * This class define a factory for AbstractListPoint2D.
 * @author Laurent Jourdren
 */
public final class ListPoint2DFactory {

  /**
   * Create a AbstractListPoint3D object.
   * @return a new AbstractListPoint3D object
   */
/*  public static AbstractListPoint2D createListPoint2D() {

    return new ArrayListPackedPoint2D();
  }*/

  /**
   * Create a AbstractListPoint2D object.
   * @param xPrecision Precision for x values.
   * @param yPrecision Precision for y values.
   * @return a new AbstractListPoint2D object
   */
  public static AbstractListPoint2D createListPoint2D(final float xPrecision,
      final float yPrecision) {

    return new ArrayListPackedPoint2D(xPrecision, yPrecision);
  }

}
