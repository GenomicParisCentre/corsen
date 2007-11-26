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

public class ArrayListPoint3D extends AbstractListPoint3D {

  private ArrayList<Float> xValues = new ArrayList<Float>();
  private ArrayList<Float> yValues = new ArrayList<Float>();
  private ArrayList<Float> zValues = new ArrayList<Float>();
  private ArrayList<Integer> iValues = new ArrayList<Integer>();

  /**
   * This class implements a Point3D which data for Point3D are stored in
   * ArrayLists. No data about the point are stored inside this object.
   * @author Laurent Jourdren
   */
  private final class InnerPoint3DImpl extends Point3D {

    private ArrayListPoint3D listPoints;
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
     * Get the Z coordinate of the point.
     * @return the Z coordinate of the point
     */
    public final float getZ() {

      return this.listPoints.zValues.get(this.index);
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
     * Set the value for the Z coordinate.
     * @param z The value for the Y coordinate
     */
    public final void setZ(final float z) {

      this.listPoints.zValues.set(this.index, z);
    }

    /**
     * Set the value for the intensity of the point.
     * @param i The value of the intensity of the point
     */
    public final void setI(final int i) {

      this.listPoints.iValues.set(this.index, i);
    }

    //
    // Constructor
    //

    /**
     * Public constructor.
     * @param listPoints List of ponts which contains the data
     * @param index Index of the point in arraylists
     */
    public InnerPoint3DImpl(final ArrayListPoint3D listPoints, final int index) {

      this.listPoints = listPoints;
      this.index = index;
    }

  }

  /**
   * Get the number of points in the list.
   * @return The number of the points
   */
  public final int size() {
    return this.xValues.size();
  }

  /**
   * Get the point at the index in the list.
   * @param index The index of the point
   * @return The point at the index
   */
  public final Point3D get(final int index) {

    return new InnerPoint3DImpl(this, index);
  }

  /**
   * Add a point to the list.
   * @param p The point to add
   */
  public final void add(final int index, final Point3D p) {

    if (p == null)
      return;
    add(index, p.getX(), p.getY(), p.getZ(), p.getI());
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param z Z coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public final void add(final float x, final float y, final float z, final int i) {

    add(size(), x, y, z, i);
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
      final float z, final int i) {

    this.xValues.add(index, x);
    this.yValues.add(index, y);
    this.zValues.add(index, z);
    this.iValues.add(index, i);
  }

  /**
   * Add a point to the list.
   * @param p The point to add
   */
  public final Point3D set(final int index, final Point3D p) {

    if (p == null)
      return null;

    Point3D old = get(index);

    if (old == null)
      return null;

    set(index, p.getX(), p.getY(), p.getZ(), p.getI());

    return old;
  }

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param z Z coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public final void set(final int index, final float x, final float y,
      final float z, final int i) {

    this.xValues.set(index, x);
    this.yValues.set(index, y);
    this.zValues.set(index, y);
    this.iValues.set(index, i);
  }

  /**
   * Add the capacity of the arraylists.
   * @param newElementsCount Number of elements to add
   */
  public final void ensureCapacity(final int newElementsCount) {

    final int newSize = size() + newElementsCount;

    this.xValues.ensureCapacity(newSize);
    this.yValues.ensureCapacity(newSize);
    this.zValues.ensureCapacity(newSize);
    this.iValues.ensureCapacity(newSize);
  }

  /**
   * Add a list of points to this list of points.
   * @param listPoints List of points to add
   */
  public final void add(final AbstractListPoint3D listPoints) {

    if (listPoints == null)
      return;

    for (Point3D p : listPoints)
      add(p);
  }

  /**
   * Apply a factor to all values of the x coordinates.
   * @param xFactor factor to apply
   */
  public final void applyXFactor(final float xFactor) {

  }

  /**
   * Apply a factor to all values of the y coordinates.
   * @param yFactor factor to apply
   */
  public final void applyYFactor(final float yFactor) {

  }

  /**
   * Apply a factor to all values of the z coordinates.
   * @param zFactor factor to apply
   */
  public final void applyZFactor(final float zFactor) {

  }

  /**
   * Remove an element.
   * @param index Index of the element to remove
   */
  public final Point3D remove(final int index) {

    final Float x = this.xValues.remove(index);
    final Float y = this.yValues.remove(index);
    final Float z = this.zValues.remove(index);
    final int i = this.iValues.remove(index);

    if (x == null)
      return null;

    return new SimplePoint3DImpl(x, y, z, i);
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
   * Get an Z value
   * @param index Of the value.
   * @return The Z value at the index position
   */
  public final float getZAt(final int index) {

    return this.zValues.get(index);
  }

  public void trimToSize() {

    this.xValues.trimToSize();
    this.yValues.trimToSize();
    this.zValues.trimToSize();
    this.iValues.trimToSize();
  }

  public final boolean contains(final Point3D p) {

    if (p == null)
      return false;

    final float x = p.getX();
    final float y = p.getY();
    final float z = p.getZ();
    final int i = p.getI();

    final int n = size();

    for (int j = 0; j < n; j++)
      if (x == this.xValues.get(j)
          && y == this.yValues.get(j) && z == this.zValues.get(j)
          && i == this.iValues.get(j))
        return true;

    return false;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  ArrayListPoint3D() {
  }

}
