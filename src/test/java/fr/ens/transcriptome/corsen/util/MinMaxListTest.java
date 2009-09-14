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

import fr.ens.transcriptome.corsen.util.MinMaxList;
import junit.framework.TestCase;

public class MinMaxListTest extends TestCase {

  public void testSize() {

    MinMaxList<Integer> list = new MinMaxList<Integer>();

    assertEquals(0, list.size());
    list.add(3);
    assertEquals(1, list.size());
    list.add(3);
    assertEquals(2, list.size());
    list.add(3);
    assertEquals(2, list.size());
  }

  public void testClear() {

    MinMaxList<Integer> list = new MinMaxList<Integer>();

    list.add(3);
    list.add(2);
    list.clear();
    assertEquals(0, list.size());
  }

  public void testGetInt() {

    MinMaxList<Integer> list = new MinMaxList<Integer>();

    list.add(3);
    list.add(2);

    assertEquals(2, list.get(0).intValue());
    assertEquals(3, list.get(1).intValue());

    list.clear();

    list.add(-3);
    list.add(-2);

    assertEquals(-3, list.get(0).intValue());
    assertEquals(-2, list.get(1).intValue());

  }

  public void testRemoveInt() {

    MinMaxList<Integer> list = new MinMaxList<Integer>();

    list.add(3);
    list.add(2);

    assertEquals(2, list.get(0).intValue());
    assertEquals(3, list.get(1).intValue());

    list.remove(0);
    assertEquals(1, list.size());
    assertEquals(3, list.get(0).intValue());
    list.remove(0);
    assertEquals(0, list.size());
  }

  public void testSetIntE() {

    MinMaxList<Integer> list = new MinMaxList<Integer>();

    list.add(3);
    assertEquals(1, list.size());
    list.set(0, 4);
    assertEquals(1, list.size());
    assertEquals(3, list.get(0).intValue());

    list.set(0, 2);
    assertEquals(1, list.size());
    assertEquals(2, list.get(0).intValue());

    list.add(5);
    assertEquals(2, list.size());

    list.set(1, 1);
    assertEquals(2, list.size());
    assertEquals(5, list.get(1).intValue());
    assertEquals(2, list.get(0).intValue());

    list.set(1, 6);
    assertEquals(2, list.size());
    assertEquals(6, list.get(1).intValue());
    assertEquals(2, list.get(0).intValue());

  }

}
