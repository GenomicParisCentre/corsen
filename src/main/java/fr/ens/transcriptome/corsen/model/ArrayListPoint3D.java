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

public final class ArrayListPoint3D extends ListPoint3D {

  private ArrayList<Long> values = new ArrayList<Long>();

  private float xPrecision = 100.0f;
  private float yPrecision = 100.0f;
  private float zPrecision = 100.0f;

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

    return new ArrayListPoint3DImpl(this.values, xPrecision, yPrecision,
        zPrecision, index);
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
  public final void add(final int index, final float x, final float y, final float z,
      final int i) {

    long val = 0;

    val = Util.setX(val, x, xPrecision);
    val = Util.setY(val, y, yPrecision);
    val = Util.setZ(val, z, zPrecision);
    val = Util.setI(val, i);

    this.values.add(index, val);
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
  public final void set(final int index, final float x, final float y, final float z,
      final int i) {

    long val = 0;

    val = Util.setX(val, x, xPrecision);
    val = Util.setY(val, y, yPrecision);
    val = Util.setZ(val, z, zPrecision);
    val = Util.setI(val, i);

    this.values.set(index, val);
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
  public final void add(final ListPoint3D listPoints) {

    if (listPoints == null)
      return;

    final int n = listPoints.size();

    for (int i = 0; i < n; i++)
      add(listPoints.get(i));
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

  public final boolean contains(final Object o) {

    if (o == null || !(o instanceof Point3D))
      return false;

    Point3D p = (Point3D) o;

    long val = 0;

    val = Util.setX(val, p.getX(), xPrecision);
    val = Util.setY(val, p.getY(), yPrecision);
    val = Util.setZ(val, p.getI(), zPrecision);
    val = Util.setI(val, p.getI());

    return this.values.contains(val);
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public ArrayListPoint3D(final float xPrecision, final float yPrecision,
      final float zPrecision) {

    this.xPrecision = xPrecision;
    this.yPrecision = yPrecision;
    this.zPrecision = zPrecision;
    this.values = new ArrayList<Long>();
  }

}
