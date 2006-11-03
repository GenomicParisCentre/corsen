/**
 * This class implements a Point2D which data for Point2D are stored in
 * ArrayLists.
 * @author Laurent Jourdren
 */
public class ArrayListPoint2DImpl extends Point2D {

  private ArrayLongList values;
  private float xPrecision;
  private float yPrecision;
  private int index;

  //
  // Getters
  //

  /**
   * Get the X coordinate of the point.
   * @return the X coordinate of the point
   */
  public final float getX() {

    final long val = this.values.get(this.index);

    return Util.getX(val, this.xPrecision);
  }

  /**
   * Get the Y coordinate of the point.
   * @return the Y coordinate of the point
   */
  public final float getY() {

    final long val = this.values.get(this.index);

    return Util.getY(val, this.yPrecision);
  }

  /**
   * Get the intensity of the point.
   * @return the intensity of the point
   */
  public final int getI() {

    final long val = this.values.get(this.index);

    return Util.getI(val);
  }

  //
  // Setters
  //

  /**
   * Set the value for the X coordinate.
   * @param x The value for the X coordinate
   */
  public final void setX(final float x) {

    long val = this.values.get(this.index);

    val = Util.setX(val, x, this.xPrecision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the Y coordinate.
   * @param y The value for the Y coordinate
   */
  public final void setY(final float y) {

    long val = this.values.get(this.index);

    val = Util.setY(val, y, this.yPrecision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the intensity of the point.
   * @param i The value of the intensity of the point
   */
  public final void setI(final int i) {

    long val = this.values.get(this.index);

    val = Util.setI(val, i);

    this.values.set(this.index, val);
  }

  //
  // Others methods
  //

  /**
   * Apply a factor to all values of the x coordinates.
   * @param xFactor factor to apply
   */
  public void applyXFactor(final float xFactor) {

    this.xPrecision = this.xPrecision / xFactor;
  }

  /**
   * Apply a factor to all values of the y coordinates.
   * @param yFactor factor to apply
   */
  public void applyYFactor(final float yFactor) {

    this.yPrecision = this.yPrecision / yFactor;
  }

  //
  // Constructors
  //

  /**
   * Private constructor.
   */
  private ArrayListPoint2DImpl() {
  }

  /**
   * Public constructor.
   * @param values The arraylist of the x coordinates
   * @param precision Precision of the data
   * @param index Index of the point in arraylists
   */
  public ArrayListPoint2DImpl(final ArrayLongList values,
      final float xPrecision, final float yPrecision, final int index) {
    this.values = values;
    this.xPrecision = xPrecision;
    this.yPrecision = yPrecision;
    this.index = index;
  }

}
