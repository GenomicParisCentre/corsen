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

public final class SingletonListPoint3D extends AbstractListPoint3D {

  private Point3D point;

  /**
   * Get the number of points in the list.
   * @return The number of the points
   */
  public int size() {
    return 1;
  }

  /**
   * Get the point at the index in the list.
   * @param index The index of the point
   * @return The point at the index
   */
  public Point3D get(final int index) {

    return point;
  }

  /**
   * Apply a factor to all values of the x coordinates.
   * @param xFactor factor to apply
   */
  public void applyXFactor(final float xFactor) {

    point.setX(point.getX() * xFactor);
  }

  /**
   * Apply a factor to all values of the y coordinates.
   * @param yFactor factor to apply
   */
  public void applyYFactor(final float yFactor) {

    point.setY(point.getY() * yFactor);
  }

  /**
   * Apply a factor to all values of the z coordinates.
   * @param zFactor factor to apply
   */
  public void applyZFactor(final float zFactor) {

    point.setZ(point.getZ() * zFactor);
  }

  /**
   * Get an intensity value
   * @param index Of the value.
   * @return The intensity value at the index position
   */
  public int getIAt(final int index) {

    return get(index).getI();
  }

  /**
   * Get an X value
   * @param index Of the value.
   * @return The X value at the index position
   */
  public float getXAt(final int index) {

    return get(index).getX();
  }

  /**
   * Get an Y value
   * @param index Of the value.
   * @return The Y value at the index position
   */
  public float getYAt(final int index) {

    return get(index).getY();
  }

  /**
   * Get an Z value
   * @param index Of the value.
   * @return The Z value at the index position
   */
  public float getZAt(final int index) {

    return get(index).getZ();
  }

  public void ensureCapacity(int minCapacity) {
  }

  public void trimToSize() {
  }

  @Override
  public void add(float x, float y, float z, int i) {

    throw new UnsupportedOperationException();
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public SingletonListPoint3D(final Point3D point) {

    this.point = point;
  }

}
