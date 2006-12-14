package fr.ens.transcriptome.corsen.model;

import java.util.ArrayList;

import fr.ens.transcriptome.corsen.util.Util;

/**
 * This class implements a Point3D which data for Point3D are stored in
 * ArrayLists. No data about the point are stored inside this object.
 * @author Laurent Jourdren
 */
public class ArrayListPoint3DImpl extends Point3D {

  private ArrayList<Long> values;
  private float xPrecision;
  private float yPrecision;
  private float zPrecision;
  private int index;

  //
  // Getters
  //

  /**
   * Get the X coordinate of the point.
   * @return the X coordinate of the point
   */
  public final float getX() {

    final long val = this.values.get(this.index);

    return Util.getX(val, this.xPrecision);
  }

  /**
   * Get the Y coordinate of the point.
   * @return the Y coordinate of the point
   */
  public final float getY() {

    final long val = this.values.get(this.index);

    return Util.getY(val, this.yPrecision);
  }

  /**
   * Get the Z coordinate of the point.
   * @return the Z coordinate of the point
   */
  public final float getZ() {

    final long val = this.values.get(this.index);

    return Util.getZ(val, this.zPrecision);
  }

  /**
   * Get the intensity of the point.
   * @return the intensity of the point
   */
  public final int getI() {

    final long val = this.values.get(this.index);

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

    long val = this.values.get(this.index);

    val = Util.setX(val, x, this.xPrecision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the Y coordinate.
   * @param y The value for the Y coordinate
   */
  public final void setY(final float y) {

    long val = this.values.get(this.index);

    val = Util.setY(val, y, this.yPrecision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the Z coordinate.
   * @param z The value for the Y coordinate
   */
  public final void setZ(final float z) {

    long val = this.values.get(this.index);

    val = Util.setZ(val, z, this.zPrecision);

    this.values.set(this.index, val);
  }

  /**
   * Set the value for the intensity of the point.
   * @param i The value of the intensity of the point
   */
  public final void setI(final int i) {

    long val = this.values.get(this.index);

    val = Util.setI(val, i);

    this.values.set(this.index, val);
  }

  //
  // Others methods
  //

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
   * Apply a factor to all values of the z coordinates.
   * @param zFactor factor to apply
   */
  public void applyZFactor(final float zFactor) {

    this.zPrecision = this.zPrecision / zFactor;
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private ArrayListPoint3DImpl() {
  }

  /**
   * Public constructor.
   * @param values The arraylist of the x coordinates
   * @param xPrecision Precision of x values
   * @param yPrecision Precision of y values
   * @param zPrecision Precision of z values
   * @param index Index of the point in arraylists
   */
  public ArrayListPoint3DImpl(final ArrayList<Long> values,
      final float xPrecision, final float yPrecision, final float zPrecision,
      final int index) {
    this.values = values;
    this.xPrecision = xPrecision;
    this.yPrecision = yPrecision;
    this.zPrecision = zPrecision;
    this.index = index;
  }

}
