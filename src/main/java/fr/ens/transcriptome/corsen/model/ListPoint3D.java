package fr.ens.transcriptome.corsen.model;

import java.util.AbstractList;

/**
 * This class store a list of Point2D as arraylists.
 * @author Laurent Jourdren
 */
public abstract class ListPoint3D extends AbstractList<Point3D> {

  //
  // Abstract methods
  //

  /**
   * Add a point to the list
   * @param x X coordinate of the point to add
   * @param y Y coordinate of the point to add
   * @param z Z coordinate of the point to add
   * @param i The intensity of the point to add
   */
  public abstract void add(final float x, final float y, final float z, final int i);
  
  public abstract float getXAt(int index);

  public abstract float getYAt(int index);

  public abstract float getZAt(int index);

  public abstract int getIAt(int index);
  
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
   * Apply a factor to all values of the z coordinates.
   * @param zFactor factor to apply
   */
  public abstract void applyZFactor(final float zFactor);

  public abstract void ensureCapacity(int minCapacity);
  
  public abstract void trimToSize();
  
  //
  // Concrete methods
  //

  /**
   * Get the barycenter of the partcle.
   * @return A point with the coordinates of the center
   */
  public Point3D getBarycenter() {

    final int n = size();

    if (n == 0)
      return null;

    double x = 0;
    double y = 0;
    double z = 0;
    int sum = 0;

    for (Point3D p : this) {

      final int val = p.getI();
      sum += val;
      x += p.getX() * val;
      y += p.getY() * val;
      z += p.getZ() * val;
    }

    return new SimplePoint3DImpl((float) x / sum, (float) y / sum, (float) z
        / sum, sum / n);
  }

  /**
   * Get the center of the partcle.
   * @return A point with the coordinates of the center
   */
  public Point3D getCenter() {

    final int n = size();

    if (n == 0)
      return null;

    double x = 0;
    double y = 0;
    double z = 0;

    for (Point3D p : this) {

      x += p.getX();
      y += p.getY();
      z += p.getZ();
    }

    return new SimplePoint3DImpl((float) x / n, (float) y / n, (float) z / n);
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
}
