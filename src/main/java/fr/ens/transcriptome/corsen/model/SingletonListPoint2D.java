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

public class SingletonListPoint2D extends AbstractListPoint2D {

  private Point2D point;

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
  public Point2D get(final int index) {

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

  public void ensureCapacity(int minCapacity) {
  }

  public void trimToSize() {
  }

  @Override
  public void add(float x, float y, int i) {

    throw new UnsupportedOperationException();
  }

  @Override
  public void add(float x, float y) {
    throw new UnsupportedOperationException();

  }

  @Override
  public boolean contains(float x, float y) {

    return this.point.getX() == x && this.point.getY() == y;
  }

  @Override
  public boolean contains(float x, float y, int i) {

    return this.point.getX() == x && this.point.getY() == y
        && this.point.getI() == i;
  }

  @Override
  public AbstractListPoint2D copy() {

    return new SingletonListPoint2D(this.point);
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public SingletonListPoint2D(final Point2D point) {

    this.point = point;
  }

}
