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

package fr.ens.transcriptome.corsen.util;

public class MathUtil {

  /**
   * Round up a double value
   * @param value to round up
   * @return a round up value
   */
  public static final double roundValue(final double value) {

    return roundValue(value, 0.00000000000001);
  }

  /**
   * Round up a double value
   * @param value to round up
   * @param threshold the threshold
   * @return a round up value
   */
  public static final double roundValue(final double value,
      final double threshold) {

    final double rInt = Math.rint(value);

    if (Math.abs(value - rInt) < threshold)
      return rInt;
    return value;
  }

  /**
   * Round up a float value
   * @param value to round up
   * @return a round up value
   */
  public static final float roundValue(final float value) {

    return roundValue(value, 0.00001f);
  }

  /**
   * Round up a float value
   * @param value to round up
   * @param threshold the threshold
   * @return a round up value
   */
  public static final float roundValue(final float value, final float threshold) {

    final float rInt = (float) Math.rint(value);

    if (Math.abs(value - rInt) < threshold)
      return rInt;
    return value;
  }

  /**
   * Calculate the sphericity of a particle (Laurent formula)
   * @param volume Volume of the particle
   * @param area area of the particle
   * @return the sphericity of the particle
   */
  public static final double sphericite1(final double volume, final double area) {

    return (Math.PI * 4.0 * Math.pow(3.0 * volume / Math.PI * 4.0, 2.0 / 3.0))
        / area;
  }

  /**
   * Calculate the sphericity of a particle (Mathilde formula)
   * @param volume Volume of the particle
   * @param area area of the particle
   * @return the sphericity of the particle
   */
  public static final double sphericite2(final double volume, final double area) {

    return (6.0 * Math.sqrt(Math.PI) * volume) / Math.pow(area, 3.0 / 2.0);
  }

  /**
   * Calculate the sphericity of a particle (Wikipedia formula)
   * @param volume Volume of the particle
   * @param area area of the particle
   * @return the sphericity of the particle
   */
  public static final double sphericite3(double volume, double area) {

    return (Math.pow(Math.PI, 1.0 / 3.0) * Math.pow(6.0 * volume, 2.0 / 3.0))
        / area;
  }

}
