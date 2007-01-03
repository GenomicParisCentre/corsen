package fr.ens.transcriptome.corsen.model;

import java.util.AbstractList;

/**
 * This class store a list of Point2D as arraylists.
 * @author Laurent Jourdren
 */
public abstract class AbstractListPoint2D extends AbstractList<Point2D> {

  public abstract void ensureCapacity(int minCapacity);

  public abstract AbstractListPoint2D copy();

  /**
   * Test if two list of point contains one or more points in common.
   * @param listPoints List to test
   * @return true if the two list of point contains one or more points in common
   */
  public boolean intersect(final AbstractListPoint2D listPoints) {

    if (listPoints == null)
      return false;

    for (Point2D p1 : this)
      for (Point2D p2 : listPoints)
        if (p1.isSamePosition(p2))
          return true;

    return false;
  }

  /**
   * Apply a factor to all values of the x coordinates.
   * @param xFactor factor to apply
   */
  public abstract void applyXFactor(final float xFactor);

  /**
   * Apply a factor to all values of the y coordinates.
   * @param yFactor factor to apply
   */
  public abstract void applyYFactor(final float yFactor);

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public abstract void add(final float x, final float y, final int i);

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   */
  public abstract void add(final float x, final float y);

  /**
   * Test if the list contains a point.
   * @param x X coordinate of the point to test
   * @param y Y coordinate of the point to test
   * @return true if the list contains the point
   */
  public abstract boolean contains(final float x, final float y);

  /**
   * Test if the list contains a point.
   * @param x X coordinate of the point to test
   * @param y Y coordinate of the point to test
   * @param i Intensity of the point to test
   * @return true if the list contains the point
   */
  public abstract boolean contains(final float x, final float y, final int i);

  public abstract float getXAt(int index);

  public abstract float getYAt(int index);

}
