
public class Triangle3D {

  private Point3D p1, p2, p3;

  private boolean sameSide(final Point3D p1, final Point3D p2, final Point3D a,
      final Point3D b) {

    final Vector3D ba = new Vector3D(b, a);
    final Vector3D p1a = new Vector3D(p1, a);
    final Vector3D p2a = new Vector3D(p2, a);

    final Vector3D cp1 = ba.cross(p1a);
    final Vector3D cp2 = ba.cross(p2a);

    if (cp1.dot(cp2) >= 0)
      return true;

    return false;
  }

  public final boolean pointInTriangle(final Point3D p) {

    if (sameSide(p, this.p1, this.p2, this.p3)
        && sameSide(p, this.p2, this.p1, this.p3)
        && sameSide(p, this.p3, this.p1, this.p2))
      return true;

    return false;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param p1 First point of the triangle
   * @param p2 Second point of the triangle
   * @param p3 Third point of the triangle
   */
  public Triangle3D(final Point3D p1, final Point3D p2, final Point3D p3) {

    if (p1 == null || p2 == null || p3 == null)
      throw new NullPointerException("One or more argument is null");

    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
  }

}
