
import java.util.StringTokenizer;

/*
 * By Laurent Jourdren
 */

public abstract class Point2D {

  //
  // Getters
  //

  public abstract float getX();

  public abstract float getY();

  public abstract int getI();

  //
  // Setters
  //

  public abstract void setX(final float x);

  public abstract void setY(final float y);

  public abstract void setI(final int i);

  //
  // Other methods
  //

  public final float distance(Point2D p) {
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

    StringBuffer sb = new StringBuffer();
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
   */
  public static Point2D parse(String s) {

    if (s == null)
      return null;
    
    Point2D p = new SimplePoint2DImpl();

    StringTokenizer st = new StringTokenizer(s, ",");
    if (st.hasMoreElements())
      p.setX(Float.parseFloat(st.nextToken()));
    if (st.hasMoreElements())
      p.setY(Float.parseFloat(st.nextToken()));
    if (st.hasMoreElements())
      p.setI(Integer.parseInt(st.nextToken()));
    
    return p;
  }

  //
  // Constructor
  //

  

}
