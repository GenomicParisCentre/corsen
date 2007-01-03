
import fr.ens.transcriptome.corsen.model.Particle2D;
import fr.ens.transcriptome.corsen.model.Point2D;
import fr.ens.transcriptome.corsen.model.SimplePoint2DImpl;
import junit.framework.TestCase;
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
 * of the �cole Normale Sup�rieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

public class Particle2DTest extends TestCase {

  private static Particle2D defineParticle() {
    
    Point2D p1 = new SimplePoint2DImpl(10,10,10);
    Point2D p2 = new SimplePoint2DImpl(17,12,20);
    Point2D p3 = new SimplePoint2DImpl(16,19,30);
    Point2D p4 = new SimplePoint2DImpl(9,17,40);
    
    //PolygonRoi roi = new PolygonRoi();
    
    
    
    Particle2D par = new Particle2D(1.0f,1.0f);
    par.addSurfacePoint(p1);
    par.addSurfacePoint(p2);
    par.addSurfacePoint(p3);
    par.addSurfacePoint(p4);
    
    return par;
  }
  
  /*
   * Test method for 'Particle2D.getId()'
   */
  public void testGetId() {

  }

  /*
   * Test method for 'Particle2D.getName()'
   */
  public void testGetName() {

    String s = "testName";
    
    Particle2D p = new Particle2D(1.0f,1.0f);
    p.setName(s);
    
    assertTrue(s.equals(s));
  }

  /*
   * Test method for 'Particle2D.getIntensity()'
   */
  public void testGetIntensity() {

  }

  /*
   * Test method for 'Particle2D.getMean()'
   */
  public void testGetMean() {

  }

  /*
   * Test method for 'Particle2D.getArea()'
   */
  public void testGetArea() {

     Particle2D par = defineParticle();
     
     double area = par.getArea();
     
     //System.out.println(area);
     
     //assertEquals(59.12,par.getArea(),0.1);
    
  }

  /*
   * Test method for 'Particle2D.setName(String)'
   */
  public void testSetName() {

  }

  /*
   * Test method for 'Particle2D.addSurfacePoint(Point2D)'
   */
  public void testAddSurfacePoint() {

  }

  /*
   * Test method for 'Particle2D.addInnerPoint(Point2D)'
   */
  public void testAddInnerPoint() {

  }

  /*
   * Test method for 'Particle2D.surfacePointsCount()'
   */
  public void testSurfacePointsCount() {

  }

  /*
   * Test method for 'Particle2D.innerPointsCount()'
   */
  public void testInnerPointsCount() {

  }

  /*
   * Test method for 'Particle2D.getSurfacePoint(int)'
   */
  public void testGetSurfacePoint() {

  }

  /*
   * Test method for 'Particle2D.getInnerPoint(int)'
   */
  public void testGetInnerPoint() {

  }

  /*
   * Test method for 'Particle2D.add(ImagePlus, PolygonRoi)'
   */
  public void testAdd() {

  }

  /*
   * Test method for 'Particle2D.getCenter()'
   */
  public void testGetCenter() {

  }

  /*
   * Test method for 'Particle2D.intersect(Particle2D)'
   */
  public void testIntersect() {

  }

  /*
   * Test method for 'Particle2D.toString()'
   */
  public void testToString() {

  }

  /*
   * Test method for 'Particle2D.Particle2D()'
   */
  public void testParticle2D() {

  }

  /*
   * Test method for 'Particle2D.Particle2D(double, double)'
   */
  public void testParticle2DDoubleDouble() {

  }

  /*
   * Test method for 'Particle2D.Particle2D(double, double, String)'
   */
  public void testParticle2DDoubleDoubleString() {

  }

  /*
   * Test method for 'Particle2D.Particle2D(double, double, ImagePlus, PolygonRoi)'
   */
  public void testParticle2DDoubleDoubleImagePlusPolygonRoi() {

  }

  /*
   * Test method for 'Particle2D.Particle2D(String)'
   */
  public void testParticle2DString() {

  }

}
