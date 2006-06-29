
import junit.framework.TestCase;


public class Point3DTest extends TestCase {

  /*
   * Test method for 'Point2D.getX()'
   */
  public void testGetX() {

    Point3D p = new SimplePoint3DImpl(10, 20,30);
    assertEquals(10.0, p.getX(), 0.0);
    p.setX(100);
    assertEquals(100.0, p.getX(), 0.0);
  }

  /*
   * Test method for 'Point2D.getY()'
   */
  public void testGetY() {

    Point3D p = new SimplePoint3DImpl(10, 20,30);
    assertEquals(20.0, p.getY(), 0.0);
    p.setY(200);
    assertEquals(200.0, p.getY(), 0.0);
  }

  /*
   * Test method for 'Point2D.getI()'
   */
  public void testGetI() {

    Point3D p = new SimplePoint3DImpl(10.0f, 20.0f, 30.0f,40);
    assertEquals(40.0, p.getI(), 0.0);
    p.setI(100);
    assertEquals(100.0, p.getI(), 0.0);
  }

  /*
   * Test method for 'Point2D.distance(Point2D)'
   */
  public void testDistance() {

    Point3D p1 = new SimplePoint3DImpl(10, 20,30);
    Point3D p2 = new SimplePoint3DImpl(20, 40,60);

    double d = p1.distance(p2);

    assertEquals(Math.sqrt((20 - 10) * (20 - 10) + (40 - 20) * (40 - 20) + (60-30) * (60-30)), d,
        0.0);
  }

  /*
   * Test method for 'Point2D.toString()'
   */
  public void testToString() {

    Point3D p = new SimplePoint3DImpl(10, 20, 30, 40);

    System.out.println(p.toString());

    assertTrue("10.0,20.0,30.0,40".equals(p.toString()));
  }

  /*
   * Test method for 'Point2D.parse(String)'
   */
  public void testParse() {

    String s = "10,20,30,40";
    
    Point3D p = SimplePoint3DImpl.parse(s);

    assertEquals(10.0, p.getX(), 0.0);
    assertEquals(20.0, p.getY(), 0.0);
    assertEquals(30.0, p.getZ(), 0.0);
    assertEquals(40.0, p.getI(), 0.0);

  }

  
  
}
