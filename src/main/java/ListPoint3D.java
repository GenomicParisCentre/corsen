/**
 * This class store a list of Point2D as arraylists.
 * @author Laurent Jourdren
 */
public final class ListPoint3D {

  private ArrayLongList values = new ArrayLongList();
  private static final float PRECISION = 10.0f;

  /**
   * Get the number of points in the list.
   * @return The number of the points
   */
  public int size() {
    return this.values.size();
  }

  /**
   * Get the point at the index in the list.
   * @param index The index of the point
   * @return The point at the index
   */
  public Point3D get(final int index) {

    return new ArrayListPoint3DImpl(this.values, PRECISION, index);
  }

  /**
   * Add a point to the list.
   * @param p The point to add
   */
  public void add(final Point3D p) {

    if (p == null)
      return;
    add(p.getX(), p.getY(), p.getZ(), p.getI());
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param z Z coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public void add(final float x, final float y, final float z, final int i) {

    long val = 0;

    val = Util.setX(val, x, PRECISION);
    val = Util.setY(val, y, PRECISION);
    val = Util.setZ(val, z, PRECISION);
    val = Util.setI(val, i);

    this.values.add(val);

  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param z Z coordinate of the point to add
   */
  public void add(final float x, final float y, final float z) {

    add(x, y, z, 0);
  }

  /**
   * Add the capacity of the arraylists.
   * @param newElementsCount Number of elements to add
   */
  public void ensureCapacity(final int newElementsCount) {

    final int newSize = size() + newElementsCount;

    this.values.ensureCapacity(newSize);
  }

  /**
   * Get an X value
   * @param index Of the value.
   * @return The X value at the index position
   */
  public float getXAt(final int index) {

    final long val = this.values.get(index);

    return Util.getX(val, PRECISION);
  }

  /**
   * Get an Y value
   * @param index Of the value.
   * @return The Y value at the index position
   */
  public float getYAt(final int index) {

    final long val = this.values.get(index);

    return Util.getY(val, PRECISION);
  }

  /**
   * Get an Z value
   * @param index Of the value.
   * @return The Z value at the index position
   */
  public float getZAt(final int index) {

    final long val = this.values.get(index);

    return Util.getZ(val, PRECISION);
  }

  /**
   * Get an intensity value
   * @param index Of the value.
   * @return The intensity value at the index position
   */
  public int getIAt(final int index) {

    final long val = this.values.get(index);

    return Util.getI(val);
  }

  /**
   * Get the minimal value of X.
   * @return the minimal value of X
   */
  public float getXMin() {

    final int n = size();

    if (n == 0)
      return Float.NaN;

    float result = Float.MAX_VALUE;

    for (int i = 0; i < n; i++) {
      final float val = getXAt(i);
      if (val < result)
        result = val;
    }

    return result;
  }

  /**
   * Get the minimal value of Y.
   * @return the minimal value of Y
   */
  public float getYMin() {

    final int n = size();

    if (n == 0)
      return Float.NaN;

    float result = Float.MAX_VALUE;

    for (int i = 0; i < n; i++) {
      final float val = getYAt(i);
      if (val < result)
        result = val;
    }

    return result;
  }

  /**
   * Get the minimal value of Z.
   * @return the minimal value of Z
   */
  public float getZMin() {

    final int n = size();

    if (n == 0)
      return Float.NaN;

    float result = Float.MAX_VALUE;

    for (int i = 0; i < n; i++) {
      final float val = getZAt(i);
      if (val < result)
        result = val;
    }

    return result;
  }

  /**
   * Get the maximal value of X.
   * @return the maximal value of X
   */
  public float getXMax() {

    final int n = size();

    if (n == 0)
      return Float.NaN;

    float result = Float.MIN_VALUE;

    for (int i = 0; i < n; i++) {
      final float val = getXAt(i);
      if (val > result)
        result = val;
    }

    return result;
  }

  /**
   * Get the maximal value of Y.
   * @return the maximal value of Y
   */
  public float getYMax() {

    final int n = size();

    if (n == 0)
      return Float.NaN;

    float result = Float.MIN_VALUE;

    for (int i = 0; i < n; i++) {
      final float val = getYAt(i);
      if (val > result)
        result = val;
    }

    return result;
  }

  /**
   * Get the maximal value of Z.
   * @return the maximal value of Z
   */
  public float getZMax() {

    final int n = size();

    if (n == 0)
      return Float.NaN;

    float result = Float.MIN_VALUE;

    for (int i = 0; i < n; i++) {
      final float val = getZAt(i);
      if (val > result)
        result = val;
    }

    return result;
  }

  /**
   * Add a list of points to this list of points.
   * @param listPoints List of points to add
   */
  public void add(final ListPoint3D listPoints) {

    if (listPoints == null)
      return;

    final int n = listPoints.size();

    for (int i = 0; i < n; i++)
      this.values.add(listPoints.values.get(i));

  }

  /**
   * Add a list of points to this list of points.
   * @param listPoints List of points to add
   */
  public void merge(final ListPoint3D listPoints) {

    if (listPoints == null)
      return;

    this.values.merge(listPoints.values);

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param initialCapacity Initial capacity of the list
   */
  public ListPoint3D(final int initialCapacity) {

    this.values = new ArrayLongList(initialCapacity);
  }

  /**
   * Public constructor.
   */
  public ListPoint3D() {

    this.values = new ArrayLongList();
  }

}
