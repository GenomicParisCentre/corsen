/*
 *                  Corsen development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence version 2 or later. This
 * should be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Corsen project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen.model;

import java.util.ArrayList;

public class ArrayListPoint2D extends AbstractListPoint2D {

  private final ArrayList<Float> xValues = new ArrayList<Float>();
  private final ArrayList<Float> yValues = new ArrayList<Float>();
  private final ArrayList<Integer> iValues = new ArrayList<Integer>();

  /**
   * This class implements a Point2D which data for Point2D are stored in
   * ArrayLists.
   * @author Laurent Jourdren
   */
  private static final class InnerPoint2DImpl extends Point2D {

    private ArrayListPoint2D listPoints;
    private int index;

    //
    // Getters
    //

    /**
     * Get the X coordinate of the point.
     * @return the X coordinate of the point
     */
    public final float getX() {

      return this.listPoints.xValues.get(this.index);
    }

    /**
     * Get the Y coordinate of the point.
     * @return the Y coordinate of the point
     */
    public final float getY() {

      return this.listPoints.yValues.get(this.index);
    }

    /**
     * Get the intensity of the point.
     * @return the intensity of the point
     */
    public final int getI() {

      return this.listPoints.iValues.get(this.index);
    }

    //
    // Setters
    //

    /**
     * Set the value for the X coordinate.
     * @param x The value for the X coordinate
     */
    public final void setX(final float x) {

      this.listPoints.xValues.set(this.index, x);
    }

    /**
     * Set the value for the Y coordinate.
     * @param y The value for the Y coordinate
     */
    public final void setY(final float y) {

      this.listPoints.yValues.set(this.index, y);
    }

    /**
     * Set the value for the intensity of the point.
     * @param i The value of the intensity of the point
     */
    public final void setI(final int i) {

      this.listPoints.iValues.set(this.index, i);
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
    InnerPoint2DImpl(final ArrayListPoint2D listPoints, final int index) {

      this.listPoints = listPoints;
      this.index = index;
    }

  }

  /**
   * Get the number of points in the list.
   * @return The number of the points
   */
  public int size() {
    return this.xValues.size();
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

    return this.iValues.get(index);
  }

  /**
   * Get an X value
   * @param index Of the value.
   * @return The X value at the index position
   */
  public final float getXAt(final int index) {

    return this.xValues.get(index);
  }

  /**
   * Get an Y value
   * @param index Of the value.
   * @return The Y value at the index position
   */
  public final float getYAt(final int index) {

    return this.yValues.get(index);
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public final void set(final int index, final float x, final float y,
      final int i) {

    this.xValues.set(index, x);
    this.yValues.set(index, y);
    this.iValues.set(index, i);
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

    this.xValues.add(index, x);
    this.yValues.add(index, y);
    this.iValues.add(index, i);
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

        this.xValues.remove(j);
        this.yValues.remove(j);
        this.iValues.remove(j);

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

    final int n = this.xValues.size();

    for (int i = 0; i < n; i++) {

      final float xVal = this.xValues.get(i);
      final float yVal = this.yValues.get(i);

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

    final int n = this.xValues.size();

    for (int j = 0; j < n; j++) {

      final float xVal = this.xValues.get(j);
      final float yVal = this.yValues.get(j);
      final float iVal = this.iValues.get(j);

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

    this.xValues.ensureCapacity(newSize);
    this.xValues.ensureCapacity(newSize);
    this.iValues.ensureCapacity(newSize);
  }

  /**
   * Apply a factor to all values of the x coordinates.
   * @param xFactor factor to apply
   */
  public void applyXFactor(final float xFactor) {

  }

  /**
   * Apply a factor to all values of the y coordinates.
   * @param yFactor factor to apply
   */
  public void applyYFactor(final float yFactor) {

  }

  /**
   * Copy the object to a new object.
   * @return A new object with the values of the object
   */
  public AbstractListPoint2D copy() {

    final ArrayListPoint2D result = new ArrayListPoint2D();
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

    if (!(listPoints instanceof ArrayListPoint2D))
      return super.intersect(listPoints);

    final ArrayListPoint2D list = (ArrayListPoint2D) listPoints;

    final ArrayList<Float> lx1 = this.xValues;
    final ArrayList<Float> ly1 = this.yValues;

    final ArrayList<Float> lx2 = list.xValues;
    final ArrayList<Float> ly2 = list.yValues;

    final int n1 = lx1.size();
    final int n2 = lx2.size();

    for (int i = 0; i < n1; i++) {

      final float x = lx1.get(i);
      final float y = ly1.get(i);

      for (int j = 0; j < n2; j++)
        if ((lx2.get(j) == x) && (ly2.get(j) == y))
          return true;
    }

    return false;
  }

  public void clear() {

    this.xValues.clear();
    this.yValues.clear();
    this.iValues.clear();
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  ArrayListPoint2D() {
  }

}
