/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen.model;

import java.util.ArrayList;

import fr.ens.transcriptome.corsen.util.Util;

class ArrayListPackedPoint2D extends AbstractListPoint2D {

  private final ArrayList<Long> values = new ArrayList<Long>();
  private float xPrecision = 100.0f;
  private float yPrecision = 100.0f;

  /**
   * This class implements a Point2D which data for Point2D are stored in
   * ArrayLists.
   * @author Laurent Jourdren
   */
  private class InnerPoint2DImpl extends Point2D {

    private ArrayListPackedPoint2D listPoints;
    private int index;

    //
    // Getters
    //

    /**
     * Get the X coordinate of the point.
     * @return the X coordinate of the point
     */
    public final float getX() {

      final long val = this.listPoints.values.get(this.index);

      return Util.getX(val, this.listPoints.xPrecision);
    }

    /**
     * Get the Y coordinate of the point.
     * @return the Y coordinate of the point
     */
    public final float getY() {

      final long val = this.listPoints.values.get(this.index);

      return Util.getY(val, this.listPoints.yPrecision);
    }

    /**
     * Get the intensity of the point.
     * @return the intensity of the point
     */
    public final int getI() {

      final long val = this.listPoints.values.get(this.index);

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

      long val = this.listPoints.values.get(this.index);

      val = Util.setX(val, x, this.listPoints.xPrecision);

      this.listPoints.values.set(this.index, val);
    }

    /**
     * Set the value for the Y coordinate.
     * @param y The value for the Y coordinate
     */
    public final void setY(final float y) {

      long val = this.listPoints.values.get(this.index);

      val = Util.setY(val, y, this.listPoints.yPrecision);

      this.listPoints.values.set(this.index, val);
    }

    /**
     * Set the value for the intensity of the point.
     * @param i The value of the intensity of the point
     */
    public final void setI(final int i) {

      long val = this.listPoints.values.get(this.index);

      val = Util.setI(val, i);

      this.listPoints.values.set(this.index, val);
    }

    //
    // Constructors
    //

    /**
     * Public constructor.
     * @param AbstractListPoint2D List of points that contains data
     * @param precision Precision of the data
     * @param index Index of the point in arraylists
     */
    public InnerPoint2DImpl(final ArrayListPackedPoint2D listPoints, final int index) {

      this.listPoints = listPoints;
      this.index = index;
    }

  }

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

    return new InnerPoint2DImpl(this, index);
  }

  /**
   * Get an intensity value
   * @param index Of the value.
   * @return The intensity value at the index position
   */
  public final int getIAt(final int index) {

    final long val = this.values.get(index);

    return Util.getI(val);
  }

  /**
   * Get an X value
   * @param index Of the value.
   * @return The X value at the index position
   */
  public final float getXAt(final int index) {

    final long val = this.values.get(index);

    return Util.getX(val, xPrecision);
  }

  /**
   * Get an Y value
   * @param index Of the value.
   * @return The Y value at the index position
   */
  public final float getYAt(final int index) {

    final long val = this.values.get(index);

    return Util.getY(val, yPrecision);
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public final void set(final int index, final float x, final float y,
      final int i) {

    this.values.set(index, convert(x, y, i));
  }

  /**
   * Add a point to the list.
   * @param p The point to add
   */
  public boolean add(final Point2D p) {

    if (p == null)
      return false;
    add(p.getX(), p.getY(), p.getI());
    return true;
  }

  /**
   * Add a point to the list
   * @param index where add the new point
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param z Z coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public final void add(final int index, final float x, final float y,
      final int i) {

    this.values.add(index, convert(x, y, i));
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public void add(final float x, final float y, final int i) {

    add(size(), x, y, i);
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
   * Add a point to the list.
   * @param p The point to add
   */
  public final Point2D set(final int index, final Point2D p) {

    if (p == null)
      return null;

    Point2D old = get(index);

    if (old == null)
      return null;

    set(index, p.getX(), p.getY(), p.getI());

    return old;
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

        this.values.remove(j);

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
  public boolean contains(final float x, final float y) {

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
  public boolean contains(final float x, final float y, final int i) {

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
   * Test if the list contains a point.
   * @param point Point to test
   * @return true if the list contains the point
   */
  public boolean contains(final Point2D point) {

    if (point == null)
      return false;

    return contains(point.getX(), point.getY(), point.getI());
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

  /**
   * Copy the object to a new object.
   * @return A new object with the values of the object
   */
  public AbstractListPoint2D copy() {

    final ArrayListPackedPoint2D result =
        new ArrayListPackedPoint2D(this.xPrecision, this.yPrecision);
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
  public boolean intersect(final AbstractListPoint2D listPoints) {

    if (listPoints == null)
      return false;

    if (!(listPoints instanceof ArrayListPackedPoint2D))
      return super.intersect(listPoints);

    final ArrayListPackedPoint2D list = (ArrayListPackedPoint2D) listPoints;

    final ArrayList<Long> l1 = this.values;
    final ArrayList<Long> l2 = list.values;

    final int n1 = l1.size();
    final int n2 = l2.size();

    for (int i = 0; i < n1; i++)
      for (int j = 0; j < n2; j++)
        if (Util.valueWithoutI(l1.get(i)) == Util.valueWithoutI(l2.get(j)))
          return true;
    return false;
  }

  private final long convert(final float x, final float y, final int i) {

    long val = 0;

    val = Util.setX(val, x, xPrecision);
    val = Util.setY(val, y, yPrecision);
    val = Util.setI(val, i);

    return val;
  }

  public void clear() {

    this.values.clear();
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  ArrayListPackedPoint2D() {
  }

  /**
   * Public constructor.
   * @param xPrecision Precision for x values
   * @param yPrecision Precision for y values
   */
  ArrayListPackedPoint2D(final float xPrecision, final float yPrecision) {

    this.xPrecision = xPrecision;
    this.yPrecision = yPrecision;
  }

}
