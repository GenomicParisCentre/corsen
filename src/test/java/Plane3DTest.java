import java.util.Random;

import junit.framework.TestCase;

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
 * This 
 */
public class Plane3DTest extends TestCase {

  private Random generator = new Random(System.currentTimeMillis());
  
  private Point3D createPoint() {
    
    float x = generator.nextInt(200);
    float y = generator.nextInt(200);
    float z = generator.nextInt(200);
    
    
    return new SimplePoint3DImpl(x,y,z);
    
  }
  
  public void testPlane3D() {
    

     for (int i = 0; i < 100000; i++) {
      
       Point3D p1 = createPoint();
       Point3D p2 = createPoint();
       Point3D p3 = createPoint();
       
       Plane3D plane = new Plane3D(p1,p2,p3);
       
       assertTrue(plane.contains(p1));
       assertTrue(plane.contains(p2));
       assertTrue(plane.contains(p3));
    }
    

  }

}
