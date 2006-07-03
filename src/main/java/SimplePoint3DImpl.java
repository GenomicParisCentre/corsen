/**
 * This class implements a basic implementation of Point3D. In this objects data
 * are stored inside the object.
 * @author Laurent Jourdren
 */
public class SimplePoint3DImpl extends Point3D {

  private float x, y, z;
  private int i;

  //
  // Getters
  //

  /**
   * Get the X coordinate of the point.
   * @return the X coordinate of the point
   */
  public final float getX() {
    return this.x;
  }

  /**
   * Get the Y coordinate of the point.
   * @return the Y coordinate of the point
   */
  public final float getY() {
    return this.y;
  }

  /**
   * Get the Z coordinate of the point.
   * @return the Z coordinate of the point
   */
  public float getZ() {
    return this.z;
  }

  /**
   * Get the intensity of the point.
   * @return the intensity of the point
   */
  public final int getI() {
    return this.i;
  }

  //
  // Setters
  //

  /**
   * Set the value for the X coordinate.
   * @param x The value for the X coordinate
   */
  public final void setX(final float x) {
    this.x = x;
  }

  /**
   * Set the value for the Y coordinate.
   * @param y The value for the Y coordinate
   */
  public final void setY(final float y) {
    this.y = y;
  }

  /**
   * Set the value for the Z coordinate.
   * @param z The value for the Z coordinate
   */
  public final void setZ(final float z) {
    this.z = z;
  }

  /**
   * Set the value for the intensity of the point.
   * @param i The value of the intensity of the point
   */
  public final void setI(final int i) {
    this.i = i;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param x X Coordinate
   * @param y Y Coordinate
   * @param z Z Coordinate
   */
  public SimplePoint3DImpl(final float x, final float y, final float z) {

    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Public constructor.
   * @param x X Coordinate
   * @param y Y Coordinate
   * @param z Z Coordinate
   * @param i Intensity of the point
   */
  public SimplePoint3DImpl(final float x, final float y, final float z,
      final int i) {

    this(x, y, z);
    this.i = i;
  }

  /**
   * Public constructor.
   */
  public SimplePoint3DImpl() {
  }

}
