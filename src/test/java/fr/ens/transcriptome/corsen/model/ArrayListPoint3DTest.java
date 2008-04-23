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

import java.util.Random;

import fr.ens.transcriptome.corsen.model.ArrayListPackedPoint3D;
import fr.ens.transcriptome.corsen.model.Point3D;
import fr.ens.transcriptome.corsen.model.SimplePoint3DImpl;
import fr.ens.transcriptome.corsen.util.Util;
import junit.framework.TestCase;

public class ArrayListPoint3DTest extends TestCase {

  private float[] xs = {1.1f, 2.2f, 3.3f, 4.4f, 5.5f};
  private float[] ys = {11.1f, 22.2f, 33.3f, 44.4f, 55.5f};
  private float[] zs = {111.1f, 222.2f, 333.3f, 444.4f, 555.5f};
  private int[] is = {1111, 2222, 3333, 4444, 5555};

  public void testSize() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < 5; i++) {

      assertEquals(i, list.size());
      list.add(xs[i], ys[i], zs[i], is[i]);
      assertEquals(i + 1, list.size());
    }

    list.clear();
    assertEquals(0, list.size());
  }

  public void testAddFloatFloatFloatInt() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(xs[i], ys[i], zs[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point3D p = list.get(i);
      assertEquals(xs[i], p.getX());
      assertEquals(ys[i], p.getY());
      assertEquals(zs[i], p.getZ());
      assertEquals(is[i], p.getI());

      assertEquals(xs[i], list.getXAt(i));
      assertEquals(ys[i], list.getYAt(i));
      assertEquals(zs[i], list.getZAt(i));
      assertEquals(is[i], list.getIAt(i));
    }

  }

  public void testAddIntPoint3D() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++) {
      Point3D p = new SimplePoint3DImpl(xs[i], ys[i], zs[i], is[i]);
      list.add(p);
    }

    for (int i = 0; i < xs.length; i++) {

      Point3D p = list.get(i);
      assertEquals(xs[i], p.getX());
      assertEquals(ys[i], p.getY());
      assertEquals(zs[i], p.getZ());
      assertEquals(is[i], p.getI());

      assertEquals(xs[i], list.getXAt(i));
      assertEquals(ys[i], list.getYAt(i));
      assertEquals(zs[i], list.getZAt(i));
      assertEquals(is[i], list.getIAt(i));
    }
  }

  public void testAddIntFloatFloatFloatInt() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point3D p = list.get(i);
      assertEquals(xs[xs.length - i - 1], p.getX());
      assertEquals(ys[xs.length - i - 1], p.getY());
      assertEquals(zs[xs.length - i - 1], p.getZ());
      assertEquals(is[xs.length - i - 1], p.getI());

      assertEquals(xs[xs.length - i - 1], list.getXAt(i));
      assertEquals(ys[xs.length - i - 1], list.getYAt(i));
      assertEquals(zs[xs.length - i - 1], list.getZAt(i));
      assertEquals(is[xs.length - i - 1], list.getIAt(i));
    }
  }

  public void testSetIntPoint3D() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    for (int i = 0; i < xs.length; i++) {
      Point3D p = new SimplePoint3DImpl(xs[i], ys[i], zs[i], is[i]);
      list.set(xs.length - i - 1, p);
    }

    for (int i = 0; i < xs.length; i++) {

      Point3D p = list.get(i);
      assertEquals(xs[xs.length - i - 1], p.getX());
      assertEquals(ys[xs.length - i - 1], p.getY());
      assertEquals(zs[xs.length - i - 1], p.getZ());
      assertEquals(is[xs.length - i - 1], p.getI());

      assertEquals(xs[xs.length - i - 1], list.getXAt(i));
      assertEquals(ys[xs.length - i - 1], list.getYAt(i));
      assertEquals(zs[xs.length - i - 1], list.getZAt(i));
      assertEquals(is[xs.length - i - 1], list.getIAt(i));
    }

  }

  public void testSetIntFloatFloatFloatInt() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    for (int i = 0; i < xs.length; i++) {
      list.set(xs.length - i - 1, xs[i], ys[i], zs[i], is[i]);
    }

    for (int i = 0; i < xs.length; i++) {

      Point3D p = list.get(i);
      assertEquals(xs[xs.length - i - 1], p.getX());
      assertEquals(ys[xs.length - i - 1], p.getY());
      assertEquals(zs[xs.length - i - 1], p.getZ());
      assertEquals(is[xs.length - i - 1], p.getI());

      assertEquals(xs[xs.length - i - 1], list.getXAt(i));
      assertEquals(ys[xs.length - i - 1], list.getYAt(i));
      assertEquals(zs[xs.length - i - 1], list.getZAt(i));
      assertEquals(is[xs.length - i - 1], list.getIAt(i));
    }
  }

  public void testContainsPoint3D() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point3D p = new SimplePoint3DImpl(xs[i], ys[i], zs[i], is[i]);
      assertTrue(list.contains(p));
    }
  }

  public void testAddAbstractListPoint3D() {

    ArrayListPackedPoint3D list1 = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length / 2; i++)
      list1.add(0, xs[i], ys[i], zs[i], is[i]);

    ArrayListPackedPoint3D list2 = new ArrayListPackedPoint3D();

    for (int i = xs.length / 2; i < xs.length; i++)
      list2.add(0, xs[i], ys[i], zs[i], is[i]);

    list1.add(list2);

    assertEquals(xs.length, list1.size());

    for (int i = 0; i < xs.length; i++) {
      Point3D p = new SimplePoint3DImpl(xs[i], ys[i], zs[i], is[i]);
      assertTrue(list1.contains(p));
    }

  }

  public void testContainsObject() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length - 1; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    for (int i = 0; i < xs.length - 1; i++) {
      Point3D p = new SimplePoint3DImpl(xs[i], ys[i], zs[i], is[i]);
      assertTrue(list.contains(p));
    }

    Point3D p =
        new SimplePoint3DImpl(xs[xs.length - 1], ys[xs.length - 1],
            zs[xs.length - 1], is[xs.length - 1]);
    assertFalse(list.contains(p));

  }

  public void testRemoveInt() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point3D p = new SimplePoint3DImpl(list.get(0));

      assertEquals(xs.length - i, list.size());
      assertTrue(list.contains(p));
      list.remove(0);
      assertEquals(xs.length - i - 1, list.size());

      assertFalse(list.contains(p));

    }

  }

  public void testApplyXFactor() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    final float factor = 15.5f;

    list.applyXFactor(factor);

    int count = 0;
    for (Point3D p : list)
      assertEquals(xs[xs.length - (++count)] * factor, p.getX(), 1 / factor);

  }

  public void testApplyYFactor() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    final float factor = 15.5f;

    list.applyYFactor(factor);

    int count = 0;
    for (Point3D p : list)
      assertEquals(ys[ys.length - (++count)] * factor, p.getY(), 1 / factor);
  }

  public void testApplyZFactor() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    final float factor = 15.5f;

    list.applyZFactor(factor);

    int count = 0;
    for (Point3D p : list)
      assertEquals(zs[zs.length - (++count)] * factor, p.getZ(), 1 / factor);
  }

  public void testGetBarycenter() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    Point3D p = list.getBarycenter();

    assertEquals(4.03, p.getX(), 0.01);
    assertEquals(40.7, p.getY(), 0.01);
    assertEquals(407.36, p.getZ(), 0.01);
    assertEquals(3333, p.getI());
  }

  public void testGetCenter() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    Point3D p = list.getCenter();

    assertEquals(3, 3, p.getX());
    assertEquals(33, 3, p.getY());
    assertEquals(333, 3, p.getZ());
    assertEquals(0, p.getI());
  }

  public void testGetXMax() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    assertEquals(xs[4], list.getXMax());

  }

  public void testGetXMin() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    assertEquals(xs[0], list.getXMin());
  }

  public void testGetYMax() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    assertEquals(ys[4], list.getYMax());
  }

  public void testGetYMin() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    assertEquals(xs[0], list.getXMin());
  }

  public void testGetZMax() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    assertEquals(zs[4], list.getZMax());
  }

  public void testGetZMin() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], zs[i], is[i]);

    assertEquals(zs[0], list.getZMin());
  }

  private final static void test(final float x, final float y, final float z,
      final int i, final ArrayListPackedPoint3D list, final float precision) {

    list.set(0, x, y, z, i);
    Point3D p = list.get(0);

    assertEquals(x, p.getX(), 1 / precision * 2);
    assertEquals(y, p.getY(), 1 / precision * 2);
    assertEquals(z, p.getZ(), 1 / precision * 2);

    assertEquals(i, p.getI());
  }

  public void testStoredData() {

    ArrayListPackedPoint3D list = new ArrayListPackedPoint3D();

    Random random = new Random(System.currentTimeMillis());

    list.add(0, 0, 0, 0);
    final float precision = 100.0f;

    for (int j = 0; j < 1000000; j++) {

      float x =
          (float) (Math.ceil(random.nextFloat()
              * Util.getStoredMaxValue(precision) * precision) / precision);
      float y =
          (float) (Math.ceil(random.nextFloat()
              * Util.getStoredMaxValue(precision) * precision) / precision);
      float z =
          (float) (Math.ceil(random.nextFloat()
              * Util.getStoredMaxValue(precision) * precision) / precision);
      int i = random.nextInt(Util.getStoredMaxValue());

      test(x, y, z, i, list, precision);

    }

    float x = Util.getStoredMaxValue(precision);
    float y = Util.getStoredMaxValue(precision);
    float z = Util.getStoredMaxValue(precision);
    int i = Util.getStoredMaxValue();

    test(x, y, z, i, list, precision);

    x = 0;
    y = 0;
    z = 0;
    i = 0;

    test(x, y, z, i, list, precision);

    x = -1;
    y = -1;
    z = -1;
    i = -1;

    try {
      test(x, y, z, i, list, precision);
      assertTrue(false);
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }

}
