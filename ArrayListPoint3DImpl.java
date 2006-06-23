
/**
 * This class implements a Point3D which data for Point3D are stored in
 * ArrayLists. No data about the point are stored inside this object.
 * @author Laurent Jourdren
 */
public class ArrayListPoint3DImpl extends Point3D {

  private ArrayLongList values;
  private float precision;
  private int index;

  //
  // Getters
  //

  /**
   * Get the X coordinate of the point.
   * @return the X coordinate of the point
   */
  final public float getX() {

    final long val = this.values.get(this.index);

    return Util.getX(val, this.precision);
  }

  /**
   * Get the Y coordinate of the point.
   * @return the Y coordinate of the point
   */
  final public float getY() {

    final long val = this.values.get(this.index);

    return Util.getY(val, this.precision);
  }

  /**
   * Get the Z coordinate of the point.
   * @return the Z coordinate of the point
   */
  final public float getZ() {

    final long val = this.values.get(this.index);

    return Util.getZ(val, this.precision);
  }

  /**
   * Get the intensity of the point.
   * @return the intensity of the point
   */
  final public int getI() {

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
  final public void setX(final float x) {

    long val = this.values.get(this.index);

    val = Util.setX(val, x, this.precision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the Y coordinate.
   * @param y The value for the Y coordinate
   */
  final public void setY(final float y) {

    long val = this.values.get(this.index);

    val = Util.setY(val, y, this.precision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the Z coordinate.
   * @param z The value for the Y coordinate
   */
  final public void setZ(final float z) {

    long val = this.values.get(this.index);

    val = Util.setZ(val, z, this.precision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the intensity of the point.
   * @param i The value of the intensity of the point
   */
  final public void setI(final int i) {

    long val = this.values.get(this.index);

    val = Util.setI(val, i);

    this.values.set(this.index, val);
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private ArrayListPoint3DImpl() {
  }

  /**
   * Public constructor.
   * @param values The arraylist of the x coordinates
   * @param precision Precision of the data
   * @param index Index of the point in arraylists
   */
  public ArrayListPoint3DImpl(final ArrayLongList values,
      final float precision, final int index) {
    this.values = values;
    this.precision = precision;
    this.index = index;
  }

}
