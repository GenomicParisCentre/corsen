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
 * of the �cole Normale Sup�rieure and the individual authors.
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

final class ArrayListPackedPoint3D extends AbstractListPoint3D {

  private ArrayList<Long> values = new ArrayList<Long>();

  private static final float X_PRECISION_DEFAULT = 100f;
  private static final float Y_PRECISION_DEFAULT = 100f;
  private static final float Z_PRECISION_DEFAULT = 100f;

  private float xPrecision;
  private float yPrecision;
  private float zPrecision;

  /**
   * This class implements a Point3D which data for Point3D are stored in
   * ArrayLists. No data about the point are stored inside this object.
   * @author Laurent Jourdren
   */
  private final class InnerPoint3DImpl extends Point3D {

    private ArrayListPackedPoint3D listPoints;
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

      return Util.getX(val, listPoints.xPrecision);
    }

    /**
     * Get the Y coordinate of the point.
     * @return the Y coordinate of the point
     */
    public final float getY() {

      final long val = this.listPoints.values.get(this.index);

      return Util.getY(val, listPoints.yPrecision);
    }

    /**
     * Get the Z coordinate of the point.
     * @return the Z coordinate of the point
     */
    public final float getZ() {

      final long val = this.listPoints.values.get(this.index);

      return Util.getZ(val, listPoints.zPrecision);
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

      val = Util.setX(val, x, listPoints.xPrecision);

      this.listPoints.values.set(this.index, val);
    }

    /**
     * Set the value for the Y coordinate.
     * @param y The value for the Y coordinate
     */
    public final void setY(final float y) {

      long val = this.listPoints.values.get(this.index);

      val = Util.setY(val, y, listPoints.yPrecision);

      this.listPoints.values.set(this.index, val);
    }

    /**
     * Set the value for the Z coordinate.
     * @param z The value for the Y coordinate
     */
    public final void setZ(final float z) {

      long val = this.listPoints.values.get(this.index);

      val = Util.setZ(val, z, listPoints.zPrecision);

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
    // Constructor
    //

    /**
     * Public constructor.
     * @param listPoints List of ponts which contains the data
     * @param index Index of the point in arraylists
     */
    public InnerPoint3DImpl(final ArrayListPackedPoint3D listPoints, final int index) {

      this.listPoints = listPoints;
      this.index = index;
    }

  }

  /**
   * Get the number of points in the list.
   * @return The number of the points
   */
  public final int size() {
    return this.values.size();
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

    this.values.add(index, convert(x, y, z, i));
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

    this.values.set(index, convert(x, y, z, i));
  }

  /**
   * Add the capacity of the arraylists.
   * @param newElementsCount Number of elements to add
   */
  public final void ensureCapacity(final int newElementsCount) {

    final int newSize = size() + newElementsCount;

    this.values.ensureCapacity(newSize);
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

    this.xPrecision = this.xPrecision / xFactor;
  }

  /**
   * Apply a factor to all values of the y coordinates.
   * @param yFactor factor to apply
   */
  public final void applyYFactor(final float yFactor) {

    this.yPrecision = this.yPrecision / yFactor;
  }

  /**
   * Apply a factor to all values of the z coordinates.
   * @param zFactor factor to apply
   */
  public final void applyZFactor(final float zFactor) {

    this.zPrecision = this.zPrecision / zFactor;
  }

  /**
   * Remove an element.
   * @param index Index of the element to remove
   */
  public final Point3D remove(final int index) {

    Long value = this.values.remove(index);

    if (value == null)
      return null;

    return new SimplePoint3DImpl(Util.getX(value, this.xPrecision), Util.getY(
        value, this.yPrecision), Util.getZ(value, this.zPrecision), Util
        .getI(value));
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
   * Get an Z value
   * @param index Of the value.
   * @return The Z value at the index position
   */
  public final float getZAt(final int index) {

    final long val = this.values.get(index);

    return Util.getZ(val, zPrecision);
  }

  public void trimToSize() {

    this.values.trimToSize();
  }

  public final boolean contains(final Point3D p) {

    if (p == null)
      return false;

    return this.values
        .contains(convert(p.getX(), p.getY(), p.getZ(), p.getI()));
  }

  private final long convert(final float x, final float y, final float z,
      final int i) {

    long val = 0;

    val = Util.setX(val, x, xPrecision);
    val = Util.setY(val, y, yPrecision);
    val = Util.setZ(val, z, zPrecision);
    val = Util.setI(val, i);

    return val;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  ArrayListPackedPoint3D() {

    this(X_PRECISION_DEFAULT, Y_PRECISION_DEFAULT, Z_PRECISION_DEFAULT);
  }

  /**
   * Public constructor.
   */
  ArrayListPackedPoint3D(final float xPrecision, final float yPrecision,
      final float zPrecision) {

    this.xPrecision = xPrecision;
    this.yPrecision = yPrecision;
    this.zPrecision = zPrecision;
    this.values = new ArrayList<Long>();
  }

}
