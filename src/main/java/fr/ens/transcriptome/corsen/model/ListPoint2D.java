package fr.ens.transcriptome.corsen.model;
import fr.ens.transcriptome.corsen.util.ArrayLongList;
import fr.ens.transcriptome.corsen.util.Util;

/**
 * This class store a list of Point2D as arraylists.
 * @author Laurent Jourdren
 */
public class ListPoint2D {

  private final ArrayLongList values = new ArrayLongList();
  private float xPrecision = 100.0f;
  private float yPrecision = 100.0f;

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
  public Point2D get(final int index) {

    return new ArrayListPoint2DImpl(this.values, this.xPrecision,
        this.yPrecision, index);
  }

  /**
   * Add a point to the list.
   * @param p The point to add
   */
  public void add(final Point2D p) {

    if (p == null)
      return;
    add(p.getX(), p.getY(), p.getI());
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public void add(final float x, final float y, final int i) {

    long val = 0;

    val = Util.setX(val, x, this.xPrecision);
    val = Util.setY(val, y, this.yPrecision);
    val = Util.setI(val, i);

    this.values.add(val);
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   */
  public void add(final float x, final float y) {

    add(x, y, 0);
  }

  /**
   * Remove a point from the list.
   * @param p Point to remove
   */
  public void remove(final Point2D p) {

    remove(p.getX(), p.getY(), p.getI());
  }

  /**
   * Remove a point from the list.
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public void remove(final float x, final float y, final int i) {

    for (int j = 0; j < size(); j++) {

      final Point2D p = get(j);

      if (p.getX() == x && p.getY() == y && p.getI() == i) {

        this.values.removeElementAt(j);

        return;
      }
    }

  }

  /**
   * Test if the list contains a point.
   * @param x X coordinate of the point to test
   * @param y Y coordinate of the point to test
   * @return true if the list contains the point
   */
  public boolean isPoint(final float x, final float y) {

    final int n = this.values.size();

    for (int i = 0; i < n; i++) {

      final long val = this.values.get(i);

      final float xVal = Util.getX(val, this.xPrecision);
      final float yVal = Util.getY(val, this.yPrecision);

      if (xVal == x && yVal == y)
        return true;
    }

    return false;
  }

  /**
   * Test if the list contains a point.
   * @param x X coordinate of the point to test
   * @param y Y coordinate of the point to test
   * @param i Intensity of the point to test
   * @return true if the list contains the point
   */
  public boolean isPoint(final float x, final float y, final int i) {

    final int n = this.values.size();

    for (int j = 0; j < n; j++) {

      final long val = this.values.get(j);

      final float xVal = Util.getX(val, this.xPrecision);
      final float yVal = Util.getY(val, this.yPrecision);
      final float iVal = Util.getI(val);

      if (xVal == x && yVal == y && iVal == i)
        return true;
    }

    return false;
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
   * Copy the object to a new object.
   * @return A new object with the values of the object
   */
  public ListPoint2D copy() {

    final ListPoint2D result = new ListPoint2D(this.xPrecision, this.yPrecision);
    final int n = size();

    for (int i = 0; i < n; i++) {
      final Point2D p = get(i);
      result.add(p);
    }

    return result;
  }

  /**
   * Test if two list of point contains one or more points in common.
   * @param listPoints List to test
   * @return true if the two list of point contains one or more points in common
   */
  public boolean intersect(final ListPoint2D listPoints) {

    if (listPoints == null)
      return false;

    final ArrayLongList l1 = this.values;
    final ArrayLongList l2 = listPoints.values;

    final int n1 = l1.size();
    final int n2 = l2.size();

    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)

        if (Util.valueWithoutI(l1.get(i)) == Util.valueWithoutI(l2.get(j)))
          return true;

    return false;
  }
  
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
  // Constructor
  //

  /**
   * Public constructor.
   * @param xPrecision Precision for x values
   * @param yPrecision Precision for y values
   */
  public ListPoint2D(final float xPrecision, final float yPrecision) {

    this.xPrecision = xPrecision;
    this.yPrecision = yPrecision;
  }

}
