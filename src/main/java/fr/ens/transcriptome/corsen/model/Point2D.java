package fr.ens.transcriptome.corsen.model;
import java.util.StringTokenizer;

/**
 * This abstract class define a point 2D
 * @author Laurent Jourdren
 */
public abstract class Point2D {

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
   * Set the intensity value of the point.
   * @param i The intensity value to set
   */
  public abstract void setI(final int i);

  //
  // Other methods
  //

  /**
   * Get the distance between 2 points.
   * @param p The other point
   * @return the distance between the 2 points
   */
  public final float distance(final Point2D p) {
    float dx, dy;

    dx = getX() - p.getX();
    dy = getY() - p.getY();

    return (float) Math.sqrt(dx * dx + dy * dy);
  }

  /**
   * Overide toString() method.
   * @return A string describing the point.
   */
  public String toString() {

    final StringBuffer sb = new StringBuffer();
    sb.append(getX());
    sb.append(',');
    sb.append(getY());
    sb.append(',');
    sb.append(getI());

    return sb.toString();
  }

  /**
   * Set a point from a string (each coordinate is separated by comma.
   * @param s String to parse
   * @return a new intance of a Point2D created from a string
   */
  public static Point2D parse(final String s) {

    if (s == null)
      return null;

    final Point2D p = new SimplePoint2DImpl();

    final StringTokenizer st = new StringTokenizer(s, ",");
    if (st.hasMoreElements())
      p.setX(Float.parseFloat(st.nextToken()));
    if (st.hasMoreElements())
      p.setY(Float.parseFloat(st.nextToken()));
    if (st.hasMoreElements())
      p.setI(Integer.parseInt(st.nextToken()));

    return p;
  }

  /**
   * Test if two point have the same position.
   * @param point Other point to test
   * @return true if the two have the same position
   */
  public boolean isSamePosition(final Point2D point) {
    
    if (point==null) return false;
    
    return this.getX()==point.getX() && this.getY()==point.getY();
  }
  
  //
  // Constructor
  //

}
