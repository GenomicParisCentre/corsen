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
 * This class define a plane.
 */
public class Plane3D {

  private float a, b, c, d;

  //
  // Getters
  //

  /**
   * Get the a determinant of the equation of plan.
   * @return Returns the a
   */
  public float getA() {
    return a;
  }

  /**
   * Get the b determinant of the equation of plan.
   * @return Returns the b
   */
  public float getB() {
    return b;
  }

  /**
   * Get the c determinant of the equation of plan.
   * @return Returns the c
   */
  public float getC() {
    return c;
  }

  /**
   * Get the d determinant of the equation of plan.
   * @return Returns the d
   */
  public float getD() {
    return d;
  }

  //
  // Setters
  //  

  /**
   * Set the a determinant of the equation of plan.
   * @param a The a to set
   */
  public void setA(float a) {
    this.a = a;
  }

  /**
   * Set the b determinant of the equation of plan.
   * @param b The b to set
   */
  public void setB(float b) {
    this.b = b;
  }

  /**
   * Set the c determinant of the equation of plan.
   * @param c The c to set
   */
  public void setC(float c) {
    this.c = c;
  }

  /**
   * Set the d determinant of the equation of plan.
   * @param d The d to set
   */
  public void setD(float d) {
    this.d = d;
  }

  //
  // Other methods
  //

  /**
   * Get the distance of the plane to a point.
   * @param p Point.
   */
  public double distance(final Point3D p) {

    if (p == null)
      throw new NullPointerException("The point in argument is null");

    return Math.abs(this.a * p.getX() + this.b * p.getY() + this.c * p.getZ()
        + this.d)
        / Math.sqrt(this.a * this.a + this.b * this.b + this.c * this.c);

  }

  /**
   * Test if the plane contains a point.
   * @param p Point to test
   * @return true if the plane contains the point
   */
  public boolean contains(final Point3D p) {

    return 0.0 == p.getX() * this.a + p.getY() * this.b + p.getZ() * this.c
        + this.d;
  }

  //
  // Constructor
  //

  /**
   * Create a new plane from 3 points.
   * @param p1 First point
   * @param p2 Second point
   * @param p3 Third point
   */
  public Plane3D(final Point3D p1, final Point3D p2, final Point3D p3) {

    final float x1 = p1.getX();
    final float y1 = p1.getY();
    final float z1 = p1.getZ();

    final float x2 = p2.getX();
    final float y2 = p2.getY();
    final float z2 = p2.getZ();

    final float x3 = p3.getX();
    final float y3 = p3.getY();
    final float z3 = p3.getZ();

    this.a = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
    this.b = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
    this.c = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
    this.d = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3
        * (y1 * z2 - y2 * z1));

  }

}
