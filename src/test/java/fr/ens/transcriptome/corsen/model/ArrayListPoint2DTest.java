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

import fr.ens.transcriptome.corsen.model.ArrayListPackedPoint2D;
import fr.ens.transcriptome.corsen.model.Point2D;
import fr.ens.transcriptome.corsen.model.SimplePoint2DImpl;
import fr.ens.transcriptome.corsen.util.Util;
import junit.framework.TestCase;

public class ArrayListPoint2DTest extends TestCase {

  private float[] xs = {1.1f, 2.2f, 3.3f, 4.4f, 5.5f};
  private float[] ys = {11.1f, 22.2f, 33.3f, 44.4f, 55.5f};
  private int[] is = {1111, 2222, 3333, 4444, 5555};

  public void testSize() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < 5; i++) {

      assertEquals(i, list.size());
      list.add(xs[i], ys[i], is[i]);
      assertEquals(i + 1, list.size());
    }

    list.clear();
    assertEquals(0, list.size());
  }

  public void testAddFloatFloatFloatInt() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(xs[i], ys[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point2D p = list.get(i);
      assertEquals(xs[i], p.getX());
      assertEquals(ys[i], p.getY());

      assertEquals(is[i], p.getI());

      assertEquals(xs[i], list.getXAt(i));
      assertEquals(ys[i], list.getYAt(i));
      assertEquals(is[i], list.getIAt(i));
    }

  }

  public void testAddIntPoint2D() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++) {
      Point2D p = new SimplePoint2DImpl(xs[i], ys[i], is[i]);
      list.add(p);
    }

    for (int i = 0; i < xs.length; i++) {

      Point2D p = list.get(i);
      assertEquals(xs[i], p.getX());
      assertEquals(ys[i], p.getY());
      assertEquals(is[i], p.getI());

      assertEquals(xs[i], list.getXAt(i));
      assertEquals(ys[i], list.getYAt(i));
      assertEquals(is[i], list.getIAt(i));
    }
  }

  public void testAddIntFloatFloatFloatInt() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point2D p = list.get(i);
      assertEquals(xs[xs.length - i - 1], p.getX());
      assertEquals(ys[xs.length - i - 1], p.getY());
      assertEquals(is[xs.length - i - 1], p.getI());

      assertEquals(xs[xs.length - i - 1], list.getXAt(i));
      assertEquals(ys[xs.length - i - 1], list.getYAt(i));
      assertEquals(is[xs.length - i - 1], list.getIAt(i));
    }
  }

  public void testSetIntPoint2D() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], is[i]);

    for (int i = 0; i < xs.length; i++) {
      Point2D p = new SimplePoint2DImpl(xs[i], ys[i], is[i]);
      list.set(xs.length - i - 1, p);
    }

    for (int i = 0; i < xs.length; i++) {

      Point2D p = list.get(i);
      assertEquals(xs[xs.length - i - 1], p.getX());
      assertEquals(ys[xs.length - i - 1], p.getY());
      assertEquals(is[xs.length - i - 1], p.getI());

      assertEquals(xs[xs.length - i - 1], list.getXAt(i));
      assertEquals(ys[xs.length - i - 1], list.getYAt(i));
      assertEquals(is[xs.length - i - 1], list.getIAt(i));
    }

  }

  public void testSetIntFloatFloatFloatInt() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], is[i]);

    for (int i = 0; i < xs.length; i++) {
      list.set(xs.length - i - 1, xs[i], ys[i], is[i]);
    }

    for (int i = 0; i < xs.length; i++) {

      Point2D p = list.get(i);
      assertEquals(xs[xs.length - i - 1], p.getX());
      assertEquals(ys[xs.length - i - 1], p.getY());
      assertEquals(is[xs.length - i - 1], p.getI());

      assertEquals(xs[xs.length - i - 1], list.getXAt(i));
      assertEquals(ys[xs.length - i - 1], list.getYAt(i));
      assertEquals(is[xs.length - i - 1], list.getIAt(i));
    }
  }

  public void testContainsPoint2D() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point2D p = new SimplePoint2DImpl(xs[i], ys[i], is[i]);
      assertTrue(list.contains(p));
    }
  }

  public void testContainsObject() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length - 1; i++)
      list.add(0, xs[i], ys[i], is[i]);

    for (int i = 0; i < xs.length - 1; i++) {
      Point2D p = new SimplePoint2DImpl(xs[i], ys[i], is[i]);
      assertTrue(list.contains(p));
    }

    Point2D p =
        new SimplePoint2DImpl(xs[xs.length - 1], ys[xs.length - 1],
            is[xs.length - 1]);
    assertFalse(list.contains(p));

  }

  public void testRemoveInt() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], is[i]);

    for (int i = 0; i < xs.length; i++) {

      Point2D p = new SimplePoint2DImpl(list.get(0));

      assertEquals(xs.length - i, list.size());
      assertTrue(list.contains(p));
      list.remove(p);
      assertEquals(xs.length - i - 1, list.size());

      assertFalse(list.contains(p));

    }

  }

  public void testApplyXFactor() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], is[i]);

    final float factor = 15.5f;

    list.applyXFactor(factor);

    int count = 0;
    for (Point2D p : list)
      assertEquals(xs[xs.length - (++count)] * factor, p.getX(), 1 / factor);

  }

  public void testApplyYFactor() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

    for (int i = 0; i < xs.length; i++)
      list.add(0, xs[i], ys[i], is[i]);

    final float factor = 15.5f;

    list.applyYFactor(factor);

    int count = 0;
    for (Point2D p : list)
      assertEquals(ys[ys.length - (++count)] * factor, p.getY(), 1 / factor);
  }

  private final static void test(final float x, final float y, final int i,
      final ArrayListPackedPoint2D list, final float precision) {

    list.set(0, x, y, i);
    Point2D p = list.get(0);

    assertEquals(x, p.getX(), 1 / precision * 2);
    assertEquals(y, p.getY(), 1 / precision * 2);

    assertEquals(i, p.getI());
  }

  public void testStoredData() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D();

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

      test(x, y, i, list, precision);

    }

    float x = Util.getStoredMaxValue(precision);
    float y = Util.getStoredMaxValue(precision);
    float z = Util.getStoredMaxValue(precision);
    int i = Util.getStoredMaxValue();

    test(x, y, i, list, precision);

    x = 0;
    y = 0;
    z = 0;
    i = 0;

    test(x, y, i, list, precision);

    x = -1;
    y = -1;
    z = -1;
    i = -1;

    try {
      test(x, y, i, list, precision);
      assertTrue(false);
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }

  public void testStoredData2() {

    ArrayListPackedPoint2D list = new ArrayListPackedPoint2D(2.0f, 0.3f);

    Random random = new Random(System.currentTimeMillis());

    list.add(1.0f, 1.0f);
    Point2D p = list.get(0);

    assertEquals(1.0f, p.getX());
    assertEquals(1.0f, p.getY());

    list.add(5.0f, 33.0f);
    p = list.get(1);

    assertEquals(5.0f, p.getX());
    assertEquals(33.0f, p.getY());

  }

}
