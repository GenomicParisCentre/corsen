

/**
 * This class implements a basic implementation of Point2D. In this objects data
 * are stored inside the object.
 * @author Laurent Jourdren
 */
public class SimplePoint2DImpl extends Point2D {

  private float x, y;
  private int i;

  //
  // Getters
  //

  /**
   * Get the X coordinate of the point.
   * @return the X coordinate of the point
   */
  final public float getX() {
    return x;
  }

  /**
   * Get the Y coordinate of the point.
   * @return the Y coordinate of the point
   */
  final public float getY() {
    return y;
  }

  /**
   * Get the intensity of the point.
   * @return the intensity of the point
   */
  final public int getI() {
    return i;
  }

  //
  // Setters
  //

  /**
   * Set the value for the X coordinate.
   * @param x The value for the X coordinate
   */
  final public void setX(final float x) {
    this.x = x;
  }

  /**
   * Set the value for the Y coordinate.
   * @param y The value for the Y coordinate
   */
  final public void setY(final float y) {
    this.y = y;
  }

  /**
   * Set the value for the intensity of the point.
   * @param i The value of the intensity of the point
   */
  final public void setI(final int i) {
    this.i = i;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param x X Coordinate
   * @param y Y Coordinate
   */
  public SimplePoint2DImpl(final float x, final float y) {

    setX(x);
    setY(y);
  }

  /**
   * Public constructor.
   * @param x X Coordinate
   * @param y Y Coordinate
   * @param i Intensity of the point
   */
  public SimplePoint2DImpl(final float x, final float y, final int i) {

    this(x, y);
    setI(i);
  }

  /**
   * Public constructor.
   * @param s String to parse
   */
  public SimplePoint2DImpl(final String s) {
    parse(s);
  }

  /**
   * Protected constructor.
   */
  protected SimplePoint2DImpl() {
  }
  
}
