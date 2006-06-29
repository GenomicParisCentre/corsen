import java.util.StringTokenizer;

/*
 * By Laurent Jourdren
 */

abstract public class Point3D {

  //
  // Getters
  //

  public abstract float getX();

  public abstract float getY();

  public abstract float getZ();

  public abstract int getI();

  //
  // Setters
  //

  public abstract void setX(final float x);

  public abstract void setY(final float y);

  public abstract void setZ(final float z);

  public abstract void setI(final int i);

  //
  // Other methods
  //

  /**
   * Get the distance of this point to another point.
   * @param p Point to test
   * @return the distance of this point to another point
   */
  public final float distance(Point3D p) {
    double dx, dy, dz;

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
  public final boolean isNear(Point3D p, float maxDistance) {

    if (p == null || maxDistance < 0.0f)
      return false;

    final float d = distance(p);

    return d < maxDistance;
  }

  /**
   * Overide toString() method.
   * @return A string describing the point.
   */
  public String toString() {

    StringBuffer sb = new StringBuffer();
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

    StringBuffer sb = new StringBuffer();
    sb.append(this.getX());
    sb.append(',');
    sb.append(this.getY());
    sb.append(',');
    sb.append(this.getZ());

    return sb.toString();
  }

  /**
   * Generate R code to plot the point
   * @param size Size of the points
   * @param colorName Name of the color of the point
   * @return A String with the R code to plot the point
   */
  public String toR(final float size, final String colorName) {

    StringBuffer sb = new StringBuffer();

    sb.append("x <- c(");
    sb.append(getX());
    sb.append(")\ny <- c(");
    sb.append(getY());
    sb.append(")\nz <- c(");
    sb.append(getZ());

    sb.append(")\n");
    sb.append("points3d(x, y, z, size=");
    sb.append(size);
    if (colorName != null) {
      sb.append(",color=\"");
      sb.append(colorName);
      sb.append("\"");
    }
    sb.append(")\n");

    return sb.toString();
  }

  /**
   * Set a point from a string (each coordinate is separated by comma.
   * @param s String to parse
   */
  public static Point3D parse(String s) {

    if (s == null)
      return null;

    Point3D p = new SimplePoint3DImpl();

    StringTokenizer st = new StringTokenizer(s, ",");
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

  public boolean equals(final Object point) {

    if (!(point instanceof Point3D))
      return false;

    final Point3D p = (Point3D) point;

    if (p.getX() == getX() && p.getY() == getY() && p.getZ() == getZ())
      return true;

    return false;
  }

}
