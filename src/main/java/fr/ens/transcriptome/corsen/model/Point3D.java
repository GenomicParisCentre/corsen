package fr.ens.transcriptome.corsen.model;
import java.util.StringTokenizer;

/*
 * By Laurent Jourdren
 */

abstract public class Point3D implements Comparable {

  //
  // Getters
  //

  /**
   * Get the x value of the point.
   * @return the x value of the point
   */
  public abstract float getX();

  /**
   * Get the y value of the point.
   * @return the y value of the point
   */
  public abstract float getY();

  /**
   * Get the z value of the point.
   * @return the z value of the point
   */
  public abstract float getZ();

  /**
   * Get the intensity value of the point.
   * @return the intensity value of the point
   */
  public abstract int getI();

  //
  // Setters
  //

  /**
   * Set the x value of the point.
   * @param x The x value to set
   */
  public abstract void setX(final float x);

  /**
   * Set the y value of the point.
   * @param y The x value to set
   */
  public abstract void setY(final float y);

  /**
   * Set the z value of the point.
   * @param z The x value to set
   */
  public abstract void setZ(final float z);

  /**
   * Set the intensity value of the point.
   * @param i The intensity value to set
   */
  public abstract void setI(final int i);

  //
  // Other methods
  //

  /**
   * Get the distance of this point to another point.
   * @param p Point to test
   * @return the distance of this point to another point
   */
  public final float distance(final Point3D p) {
    final double dx, dy, dz;

    if (p == null)
      return -1;

    dx = getX() - p.getX();
    dy = getY() - p.getY();
    dz = getZ() - p.getZ();

    return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
  }

  /**
   * Test if the point is near another point.
   * @param p Point to test
   * @param maxDistance Maximal distance
   * @return true if this point is near the point
   */
  public final boolean isNear(final Point3D p, final float maxDistance) {

    if (p == null || maxDistance < 0.0f)
      return false;

    final float d = distance(p);

    return d < maxDistance;
  }

  /**
   * Test if the point is near another point.
   * @param p Point to test
   * @param xlen Maximal distance in x
   * @param ylen Maximal distance in y
   * @param zlen Maximal distance in z
   * @return true if this point is near the point
   */
  public final boolean isNear(final Point3D p, final float xlen,
      final float ylen, final float zlen) {

    if (p == null || xlen < 0.0f || ylen < 0.0f || zlen < 0.0f)
      return false;

    return (Math.abs(getX() - p.getX()) < xlen)
        && (Math.abs(getY() - p.getY()) < ylen)
        && (Math.abs(getZ() - p.getZ()) < zlen);
  }

  /**
   * Overide toString() method.
   * @return A string describing the point.
   */
  public String toString() {

    final StringBuffer sb = new StringBuffer();
    sb.append(this.getX());
    sb.append(',');
    sb.append(this.getY());
    sb.append(',');
    sb.append(this.getZ());
    sb.append(',');
    sb.append(this.getI());

    return sb.toString();
  }

  /**
   * Overide toString() method.
   * @return A string describing the point.
   */
  public String toStringWithoutIntensity() {

    final StringBuffer sb = new StringBuffer();
    sb.append(this.getX());
    sb.append(',');
    sb.append(this.getY());
    sb.append(',');
    sb.append(this.getZ());

    return sb.toString();
  }

  /**
   * Set a point from a string (each coordinate is separated by comma.
   * @param s String to parse
   * @return a new intance of a Point2D created from a string
   */
  public static Point3D parse(final String s) {

    if (s == null)
      return null;

    final Point3D p = new SimplePoint3DImpl();

    final StringTokenizer st = new StringTokenizer(s, ",");
    if (st.hasMoreElements())
      p.setX(Float.parseFloat(st.nextToken()));
    if (st.hasMoreElements())
      p.setY(Float.parseFloat(st.nextToken()));
    if (st.hasMoreElements())
      p.setZ(Float.parseFloat(st.nextToken()));
    if (st.hasMoreElements())
      p.setI(Short.parseShort(st.nextToken()));

    return p;
  }

  /**
   * Test if two points are equals.
   * @param point Point to test
   * @return true if the two points are equals
   */
  public boolean equals(final Object point) {

    if (!(point instanceof Point3D))
      return false;

    final Point3D p = (Point3D) point;

    if (p.getX() == getX() && p.getY() == getY() && p.getZ() == getZ())
      return true;

    return false;
  }

  /**
   * Implements Comparable interface
   * @param point Point to test
   * @return the result of the comparaison
   */
  public int compareTo(final Object point) {

    return compareTo((Point3D) point);
  }

  /**
   * Implements Comparable interface
   * @param point Point to test
   * @return the result of the comparaison
   */
  public int compareTo(final Point3D point) {

    if (point == null)
      throw new NullPointerException();

    float diffX = this.getX() - point.getX();
    if (diffX != 0)
      return diffX < 0 ? -1 : 1;

    float diffY = this.getY() - point.getY();
    if (diffY != 0)
      return diffY < 0 ? -1 : 1;

    float diffZ = this.getZ() - point.getZ();
    if (diffZ != 0)
      return diffZ < 0 ? -1 : 1;

    float diffI = this.getI() - point.getI();
    if (diffI < 0)
      return -1;
    if (diffI == 0)
      return 0;

    return 1;
  }

  public int hashCode() {
    
    int iTotal =17;
    final int iConstant = 37;
    
    iTotal = iTotal * iConstant + Float.floatToIntBits(getX());
    iTotal = iTotal * iConstant + Float.floatToIntBits(getY());
    iTotal = iTotal * iConstant + Float.floatToIntBits(getZ());
    iTotal = iTotal * iConstant + getI();
    
    return 0;
  }
  
}
