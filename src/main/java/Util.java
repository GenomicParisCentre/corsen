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
  public static float getX(final long point, final float precision) {

    return ((point & ~MASK_X) >> SHIFT_X & MASK_16BITS) / precision;
  }

  /**
   * Extract the X value from a coded point.
   * @param point The coded location
   * @param precision of the value
   * @return The meta row from the coded location
   */
  public static float getY(final long point, final float precision) {

    return ((point & ~MASK_Y) >> SHIFT_Y & MASK_16BITS) / precision;
  }

  /**
   * Extract the X value from a coded point.
   * @param point The coded location
   * @param precision of the value
   * @return The meta row from the coded location
   */
  public static float getZ(final long point, final float precision) {

    return ((point & ~MASK_Z) >> SHIFT_Z & MASK_16BITS) / precision;
  }

  /**
   * Extract the X value from a coded point.
   * @param point The coded location
   * @return The meta row from the coded location
   */
  public static int getI(final long point) {

    return (int) ((point & ~MASK_I) >> SHIFT_I & MASK_16BITS);
  }

  /**
   * Set the X in coded point.
   * @param point The coded location
   * @param value The value of X to set
   * @param precision of the value
   * @return The new coded point
   */
  public static long setX(final long point, final float value,
      final float precision) {

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
  public static long setY(final long point, final float value,
      final float precision) {

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
  public static long setZ(final long point, final float value,
      final float precision) {

    final long v = (long) (value * precision);

    return point & MASK_Z | (v & MASK_16BITS) << SHIFT_Z;
  }

  /**
   * Set the intensity in coded point.
   * @param point The coded location
   * @param value The value of intensity to set
   * @return The new coded point
   */
  public static long setI(final long point, final long value) {

    return point & MASK_I | (value & MASK_16BITS) << SHIFT_I;
  }

  /**
   * Get the encode value of a point without the intensity data (which set to
   * 0). This method is useful to compare two points.
   * @param point The coded location
   * @return a encoded point with intensity set to 0 
   */
  public static long valueWithoutI(final long point) {

    return point & MASK_I;
  }

  /**
   * Get the equation of a plan
   * @param p1
   * @param p2
   * @param p3
   * @return the equation of a plan
   */
  public static double eq(final Point3D p1, final Point3D p2, final Point3D p3) {

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

  public static double eqTest(final double a, final double b, final double c,
      final double d, final Point3D p) {

    if (p == null)
      return -1;

    return a * p.getX() + b * p.getY() + c * p.getZ() + d;

  }

}
