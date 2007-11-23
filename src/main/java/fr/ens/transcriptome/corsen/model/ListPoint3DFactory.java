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
 * This class define a factory for AbstractListPoint3D.
 * @author Laurent Jourdren
 */
public final class ListPoint3DFactory {

  /**
   * Create a AbstractListPoint3D object.
   * @return a new AbstractListPoint3D object
   */
  public static AbstractListPoint3D createListPoint3D() {

    return new ArrayListPackedPoint3D();
  }

  /**
   * Create a AbstractListPoint3D object.
   * @param xPrecision Precision for x values.
   * @param yPrecision Precision for y values.
   * @param zPrecisionPrecision for z values.
   * @return a new AbstractListPoint3D object
   */
  public static AbstractListPoint3D createListPoint3D(final float xPrecision,
      final float yPrecision, final float zPrecision) {

    return new ArrayListPackedPoint3D(xPrecision, yPrecision, zPrecision);
  }

}
