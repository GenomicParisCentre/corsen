/*
 *                      Corsen development code
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
 *      http://www.transcriptome.ens.fr/corsen
 *
 */

/**
 * This class define a vector class.
 */
public class Vector3D {

  private float x, y, z;

  //
  // Getters
  //

  /**
   * Get the x value.
   * @return Returns the x
   */
  public float getX() {
    return x;
  }

  /**
   * Get the y value.
   * @return Returns the y
   */
  public float getY() {
    return y;
  }

  /**
   * Get the z value.
   * @return Returns the z
   */
  public float getZ() {
    return z;
  }

  //
  // Setters
  // 

  /**
   * Set the x value.
   * @param x The x to set
   */
  public void setX(float x) {
    this.x = x;
  }

  /**
   * Set the y value.
   * @param y The y to set
   */
  public void setY(float b) {
    this.y = b;
  }

  /**
   * Set the z value.
   * @param z The z to set
   */
  public void setZ(float c) {
    this.z = c;
  }

  //
  // Other methods
  //

  /**
   * Get the length of the vector.
   * @return the norm of the vector
   */
  public double getlength() {

    return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
  }

  /**
   * Add two vectors.
   * @param v Vector to add
   * @return a new vector as the sum of the vectors
   */
  public Vector3D add(final Vector3D v) {

    if (v == null)
      new NullPointerException("The vector3D in argument is null");

    Vector3D r = new Vector3D();

    r.x = this.x + v.x;
    r.y = this.y + v.y;
    r.z = this.z + v.z;

    return r;
  }

  /**
   * Substraction two vectors.
   * @param v Vector to subtracte
   * @return a new vector as the subtraction of the vectors
   */
  public Vector3D sub(final Vector3D v) {

    if (v == null)
      new NullPointerException("The vector3D in argument is null");

    Vector3D r = new Vector3D();

    r.x = this.x - v.x;
    r.y = this.y - v.y;
    r.z = this.z - v.z;

    return r;
  }

  /**
   * Returns the product of the vector.
   * @param f The factor
   * @return a new vector as the result of the product
   */
  public Vector3D product(final float f) {

    Vector3D r = new Vector3D();

    r.x = this.x * f;
    r.y = this.y * f;
    r.z = this.z * f;

    return r;
  }

  /**
   * Returns the dot product of this vector and another vector
   * @param v the other vector
   * @return the dot product of this and v
   */
  public final double dot(final Vector3D v) {

    if (v == null)
      new NullPointerException("The vector3D in argument is null");

    return (this.x * v.x + this.y * v.y + this.z * v.z);
  }

  /**
   * Returns  the cross product of two vectors.
   * @param v The second vector
   * @return a new vector as the result of the cross product
   */
  public Vector3D cross(final Vector3D v) {

    if (v == null)
      new NullPointerException("The vector3D in argument is null");

    Vector3D r = new Vector3D();

    r.x = this.y * v.z - this.z * v.y;
    r.y = this.z * v.x - this.x * v.z;
    r.z = this.x * v.y - this.y * v.x;

    return r;
  }

  /**
   * Returns the length of this vector.
   * @return the length of this vector
   */
  public final double length() {

    return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
  }

  //
  // Constructors
  //

  /**
   * Create a vector from its coordinates.
   */
  public Vector3D() {
  }

  /**
   * Create a vector from its coordinates.
   * @param x X coordinate of the vector
   * @param y Y coordinate of the vector
   * @param z Z coordinate of the vector
   */
  public Vector3D(final float x, final float y, final float z) {

    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Create a vector from 2 points.
   * @param a First point
   * @param b Second point
   */
  public Vector3D(final Point3D a, final Point3D b) {

    if (a == null || b == null)
      throw new NullPointerException("One or more argument is null");

    this.x = b.getX() - a.getX();
    this.y = b.getY() - a.getY();
    this.z = b.getZ() - a.getZ();

  }

}
