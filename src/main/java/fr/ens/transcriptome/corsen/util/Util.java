package fr.ens.transcriptome.corsen.util;

import fr.ens.transcriptome.corsen.model.Point3D;

public final class Util {

  /** 16 bits mask. */
  private static final int MASK_16BITS = 0xffff;

  // private static final int MAX_INTENSITY = 0xffff;

  /** X bits mask. */
  private static final long MASK_X = 0xffffffffffff0000L;

  /** Y bits mask. */
  private static final long MASK_Y = 0xffffffff0000ffffL;

  /** Z bits mask. */
  private static final long MASK_Z = 0xffff0000ffffffffL;

  /** I bits mask. */
  private static final long MASK_I = 0xffffffffffffL;

  /** Shift X. */
  private static final int SHIFT_X = 0;

  /** Shift Y. */
  private static final int SHIFT_Y = 16;

  /** Shift Z. */
  private static final int SHIFT_Z = 32;

  /** Shift Y. */
  private static final int SHIFT_I = 48;

  /**
   * Extract the X value from a coded point.
   * @param point The coded location
   * @param precision of the value
   * @return The meta row from the coded location
   */
  public static final float getX(final long point, final float precision) {

    return ((point & ~MASK_X) >> SHIFT_X & MASK_16BITS) / precision;
  }

  /**
   * Extract the X value from a coded point.
   * @param point The coded location
   * @param precision of the value
   * @return The meta row from the coded location
   */
  public static final float getY(final long point, final float precision) {

    return ((point & ~MASK_Y) >> SHIFT_Y & MASK_16BITS) / precision;
  }

  /**
   * Extract the X value from a coded point.
   * @param point The coded location
   * @param precision of the value
   * @return The meta row from the coded location
   */
  public static final float getZ(final long point, final float precision) {

    return ((point & ~MASK_Z) >> SHIFT_Z & MASK_16BITS) / precision;
  }

  /**
   * Extract the X value from a coded point.
   * @param point The coded location
   * @return The meta row from the coded location
   */
  public static final int getI(final long point) {

    return (int) ((point & ~MASK_I) >> SHIFT_I & MASK_16BITS);
  }

  /**
   * Set the X in coded point.
   * @param point The coded location
   * @param value The value of X to set
   * @param precision of the value
   * @return The new coded point
   */
  public static final long setX(final long point, final float value,
      final float precision) {

    if (!isValueCorrect(value, precision))
      throw new RuntimeException(
          "Invalid point value: This x value is too great or negative to be stored ("
              + value + ")");

    final long v = (long) (value * precision);

    return point & MASK_X | (v & MASK_16BITS) << SHIFT_X;
  }

  /**
   * Set the Y in coded point.
   * @param point The coded location
   * @param value The value of Y to set
   * @param precision of the value
   * @return The new coded point
   */
  public static final long setY(final long point, final float value,
      final float precision) {

    if (!isValueCorrect(value, precision))
      throw new RuntimeException(
          "Invalid point value: This y value is too great or negative to be stored ("
              + value + ")");

    final long v = (long) (value * precision);

    return point & MASK_Y | (v & MASK_16BITS) << SHIFT_Y;
  }

  /**
   * Set the Z in coded point.
   * @param point The coded location
   * @param value The value of Z to set
   * @param precision of the value
   * @return The new coded point
   */
  public static final long setZ(final long point, final float value,
      final float precision) {

    if (!isValueCorrect(value, precision))
      throw new RuntimeException(
          "Invalid point value: This z value is too great or negative to be stored ("
              + value + ")");

    final long v = (long) (value * precision);

    return point & MASK_Z | (v & MASK_16BITS) << SHIFT_Z;
  }

  /**
   * Set the intensity in coded point.
   * @param point The coded location
   * @param value The value of intensity to set
   * @return The new coded point
   */
  public static final long setI(final long point, final int value) {

    if (!isValueCorrect(value))
      throw new RuntimeException(
          "Invalid point value: This i value is too great to be stored ("
              + value + ")");

    return point & MASK_I | (((long) value) & MASK_16BITS) << SHIFT_I;
  }

  /**
   * Get the encode value of a point without the intensity data (which set to
   * 0). This method is useful to compare two points.
   * @param point The coded location
   * @return a encoded point with intensity set to 0
   */
  public static final long valueWithoutI(final long point) {

    return point & MASK_I;
  }

  /**
   * Test if a value is correct to be stored in a long.
   * @param value Value to test
   * @param precision precision of the stored data
   * @return true if the value can be stored in a long
   */
  private static final boolean isValueCorrect(final float value,
      final float precision) {

    return value >= 0 && value < (Math.pow(2, MASK_16BITS) / precision);
  }

  /**
   * Test if a value is correct to be stored in a long.
   * @param value Value to test
   * @return true if the value can be stored in a long
   */
  private static final boolean isValueCorrect(final int value) {

    return value >= 0 && value < (Math.pow(2, MASK_16BITS));
  }

  /**
   * Get the max float value which can be stored.
   * @param precision precision of the value
   * @return the max value which can be stored
   */
  public static final float getStoredMaxValue(final float precision) {

    return MASK_16BITS / precision;
  }

  /**
   * Get the max int value which can be stored.
   * @return the max value which can be stored
   */
  public static final int getStoredMaxValue() {

    return MASK_16BITS;
  }

  /**
   * Get the equation of a plan
   * @param p1
   * @param p2
   * @param p3
   * @return the equation of a plan
   */
  public static final double eq(final Point3D p1, final Point3D p2,
      final Point3D p3) {

    if (p1 == null || p2 == null || p3 == null)
      return -1;

    final double diff12X = p1.getX() - p2.getX();
    final double diff21Z = p2.getZ() - p1.getZ();
    final double diff12Y = p1.getY() - p2.getY();

    double a = 1;
    final double b = -a
        * (p3.getX() * diff21Z + p3.getZ() * diff12X - p1.getX() - p1.getZ()
            * diff12X)
        / (p3.getY() * diff21Z + 9 * diff12Y - p1.getY() - p1.getZ() * diff12Y);

    final double c = (a * diff12X + b * diff12Y) / diff21Z;
    final double d = -a * p1.getX() - b * p1.getY() - c * p1.getZ();

    System.out.println("a=" + a);
    System.out.println("b=" + b);
    System.out.println("c=" + c);
    System.out.println("d=" + d);

    System.out.println("eq test p1 =" + eqTest(a, b, c, d, p1));
    System.out.println("eq test p2 =" + eqTest(a, b, c, d, p2));
    System.out.println("eq test p3 =" + eqTest(a, b, c, d, p3));

    return -1;
  }

  public static final double eqTest(final double a, final double b,
      final double c, final double d, final Point3D p) {

    if (p == null)
      return -1;

    return a * p.getX() + b * p.getY() + c * p.getZ() + d;

  }

  /**
   * Get the number of the occurence of a char in a string.
   * @param s String to parse
   * @param c Char to find
   * @return the number of the char occurence
   */
  public static final int charCount(final String s, final char c) {

    if (s == null)
      return 0;

    final int len = s.length();
    int count = 0;

    for (int i = 0; i < len; i++)
      if (s.charAt(i) == c)
        count++;

    return count;
  }

  public static final String toTimeHumanReadable(final long time) {

    long min = time / (60 * 1000);
    long minRest = time % (60 * 1000);
    long sec = minRest / 1000;

    long mili = minRest % 1000;

    return String.format("%02d:%02d.%03d", min, sec, mili);
  }

}
