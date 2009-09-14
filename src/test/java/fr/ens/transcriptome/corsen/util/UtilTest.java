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
 * or to join the Corsen google group, visit the home page
 * at:
 *
 *      http://transcriptome.ens.fr/corsen
 *
 */

package fr.ens.transcriptome.corsen.util;

import fr.ens.transcriptome.corsen.model.Point3D;
import fr.ens.transcriptome.corsen.model.SimplePoint3DImpl;
import fr.ens.transcriptome.corsen.util.Util;
import junit.framework.TestCase;

public class UtilTest extends TestCase {

  /*
   * Test method for 'Util.getX(long, float)'
   */
  public void testGetSet() {

    long p = 0;
    p = Util.setX(p, 1111.1f, 10);
    p = Util.setY(p, 2222.2f, 10);
    p = Util.setZ(p, 3333.3f, 10);
    p = Util.setI(p, 4444);

    assertTrue(("" + 1111.1f).equals("" + Util.getX(p, 10)));
    assertTrue(("" + 2222.2f).equals("" + Util.getY(p, 10)));
    assertTrue(("" + 3333.3f).equals("" + Util.getZ(p, 10)));
    assertTrue(("" + 4444).equals("" + Util.getI(p)));

    p = Util.setI(p, 4444);
    p = Util.setZ(p, 3333.3f, 10);
    p = Util.setY(p, 2222.2f, 10);
    p = Util.setX(p, 1111.1f, 10);

    assertTrue(("" + 1111.1f).equals("" + Util.getX(p, 10)));
    assertTrue(("" + 2222.2f).equals("" + Util.getY(p, 10)));
    assertTrue(("" + 3333.3f).equals("" + Util.getZ(p, 10)));
    assertTrue(("" + 4444).equals("" + Util.getI(p)));

  }

  public void testEq() {

    Point3D p1 = new SimplePoint3DImpl(1, 2, 3);
    Point3D p2 = new SimplePoint3DImpl(6, 5, 4);
    Point3D p3 = new SimplePoint3DImpl(9, 7, 8);

    Util.eq(p1, p2, p3);

  }

}
