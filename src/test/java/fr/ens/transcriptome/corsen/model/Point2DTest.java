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

import fr.ens.transcriptome.corsen.model.Point2D;
import fr.ens.transcriptome.corsen.model.SimplePoint2DImpl;
import junit.framework.TestCase;

public class Point2DTest extends TestCase {

  /*
   * Test method for 'Point2D.getX()'
   */
  public void testGetX() {

    Point2D p = new SimplePoint2DImpl(10, 20);
    assertEquals(10.0, p.getX(), 0.0);
    p.setX(100);
    assertEquals(100.0, p.getX(), 0.0);
  }

  /*
   * Test method for 'Point2D.getY()'
   */
  public void testGetY() {

    Point2D p = new SimplePoint2DImpl(10, 20);
    assertEquals(20.0, p.getY(), 0.0);
    p.setY(200);
    assertEquals(200.0, p.getY(), 0.0);
  }

  /*
   * Test method for 'Point2D.getI()'
   */
  public void testGetI() {

    Point2D p = new SimplePoint2DImpl(10, 20, 30);
    assertEquals(30.0, p.getI(), 0.0);
    p.setI(100);
    assertEquals(100.0, p.getI(), 0.0);
  }

  /*
   * Test method for 'Point2D.distance(Point2D)'
   */
  public void testDistance() {

    Point2D p1 = new SimplePoint2DImpl(10, 20);
    Point2D p2 = new SimplePoint2DImpl(20, 40);

    double d = p1.distance(p2);

    assertEquals(Math.sqrt((20 - 10) * (20 - 10) + (40 - 20) * (40 - 20)), d,
        0.01);
  }

  /*
   * Test method for 'Point2D.toString()'
   */
  public void testToString() {

    Point2D p = new SimplePoint2DImpl(10, 20, 30);

    System.out.println(p.toString());

    assertTrue("10.0,20.0,30".equals(p.toString()));
  }

  /*
   * Test method for 'Point2D.parse(String)'
   */
  public void testParse() {

    String s = "10,20,30";
    Point2D p = SimplePoint2DImpl.parse(s);

    assertEquals(10.0, p.getX(), 0.0);
    assertEquals(20.0, p.getY(), 0.0);
    assertEquals(30.0, p.getI(), 0.0);

  }

}
