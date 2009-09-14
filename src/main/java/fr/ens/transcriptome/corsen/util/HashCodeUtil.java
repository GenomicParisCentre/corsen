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

package fr.ens.transcriptome.corsen.util;

import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 * Example use case:
 * 
 * <pre>
 * public int hashCode() {
 *   int result = HashCodeUtil.SEED;
 *   //collect the contributions of various fields
 *   result = HashCodeUtil.hash(result, fPrimitive);
 *   result = HashCodeUtil.hash(result, fObject);
 *   result = HashCodeUtil.hash(result, fArray);
 *   return result;
 * }
 * </pre>
 */
public final class HashCodeUtil {

  /**
   * An initial value for a <code>hashCode</code>, to which is added
   * contributions from fields. Using a non-zero value decreases collisons of
   * <code>hashCode</code> values.
   */
  public static final int SEED = 23;

  //
  // Private methods
  //

  private static final int fODD_PRIME_NUMBER = 37;

  private static int firstTerm(int aSeed) {
    return fODD_PRIME_NUMBER * aSeed;
  }

  private static boolean isArray(Object aObject) {
    return aObject.getClass().isArray();
  }

  //
  // Public methods
  //

  /**
   * booleans.
   */
  public static int hash(int aSeed, boolean aBoolean) {

    return firstTerm(aSeed) + (aBoolean ? 1 : 0);
  }

  /**
   * chars.
   */
  public static int hash(int aSeed, char aChar) {

    return firstTerm(aSeed) + aChar;
  }

  /**
   * ints.
   */
  public static int hash(int aSeed, int aInt) {
    /*
     * Implementation Note Note that byte and short are handled by this method,
     * through implicit conversion.
     */

    return firstTerm(aSeed) + aInt;
  }

  /**
   * longs.
   */
  public static int hash(int aSeed, long aLong) {

    return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
  }

  /**
   * floats.
   */
  public static int hash(int aSeed, float aFloat) {
    return hash(aSeed, Float.floatToIntBits(aFloat));
  }

  /**
   * doubles.
   */
  public static int hash(int aSeed, double aDouble) {
    return hash(aSeed, Double.doubleToLongBits(aDouble));
  }

  /**
   * <code>aObject</code> is a possibly-null object field, and possibly an
   * array. If <code>aObject</code> is an array, then each element may be a
   * primitive or a possibly-null object.
   */
  public static int hash(int aSeed, Object aObject) {

    int result = aSeed;

    if (aObject == null)
      result = hash(result, 0);
    else if (!isArray(aObject))
      result = hash(result, aObject.hashCode());
    else {

      int length = Array.getLength(aObject);
      for (int idx = 0; idx < length; ++idx) {
        Object item = Array.get(aObject, idx);

        // recursive call!
        result = hash(result, item);
      }
    }
    return result;
  }

}
