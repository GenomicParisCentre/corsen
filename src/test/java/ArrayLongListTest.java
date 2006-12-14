import java.util.Arrays;

import fr.ens.transcriptome.corsen.util.ArrayLongList;

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
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

public class ArrayLongListTest extends TestCase {

  private static long[] tab = {1L, 2L, 3L, 4L, 5L, 6L};

  /*
   * Test method for 'ArrayLongList.get(int)'
   */
  public void testGet() {

  }

  /*
   * Test method for 'ArrayLongList.set(int, long)'
   */
  public void testSet() {

  }

  /*
   * Test method for 'ArrayLongList.size()'
   */
  public void testSize() {

  }

  /*
   * Test method for 'ArrayLongList.add(long)'
   */
  public void testAdd() {

  }

  /*
   * Test method for 'ArrayLongList.removeFirstElement()'
   */
  public void testRemoveFirstElement() {

  }

  /*
   * Test method for 'ArrayLongList.removeElementAt(int)'
   */
  public void testRemoveElementAt() {

  }

  /*
   * Test method for 'ArrayLongList.ensureCapacity(int)'
   */
  public void testEnsureCapacity() {

    ArrayLongList all = new ArrayLongList(tab);
    all.removeFirstElement();
    all.removeFirstElement();
    all.removeFirstElement();
    all.removeFirstElement();
    all.removeFirstElement();
    all.removeFirstElement();
    all.ensureCapacity(10);

    for (int i = 0; i < all.size(); i++) {
      System.out.println(all.get(i));
    }

  }

  /*
   * Test method for 'ArrayLongList.trimToSize()'
   */
  public void testTrimToSize() {

    ArrayLongList all = new ArrayLongList(tab);
    all.removeFirstElement();
    all.removeFirstElement();
    all.removeFirstElement();
    all.trimToSize();

  }

  /*
   * Test method for 'ArrayLongList.isEmpty()'
   */
  public void testIsEmpty() {

  }

  /*
   * Test method for 'ArrayLongList.ArrayLongList(int)'
   */
  public void testArrayLongListInt() {

  }

  /*
   * Test method for 'ArrayLongList.ArrayLongList(long[])'
   */
  public void testArrayLongListLongArray() {

  }

  /*
   * Test method for 'ArrayLongList.ArrayLongList()'
   */
  public void testArrayLongList() {

  }

  public void testMerge() {

    ArrayLongList all1 = new ArrayLongList(tab);
    ArrayLongList all2 = new ArrayLongList(tab);

    all1.merge(all2);

  }

  public void testMergeSpeed() {

    final int size = 999754;

    long a1[] = new long[size];
    Arrays.fill(a1, 1000);
    ArrayLongList all1 = new ArrayLongList(a1);

    long a2[] = new long[size];
    Arrays.fill(a2, 2000);
    ArrayLongList all2 = new ArrayLongList(a2);

    all1.merge(all2);

    assertEquals(all1.size(), size * 2);
    assertEquals(all2.size(), 0);

    for (int i = 0; i < size; i++)
      assertEquals(1000, all1.get(i));

    for (int i = size; i < size * 2; i++)
      assertEquals(2000, all1.get(i), 2000);

  }

}
