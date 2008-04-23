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

package fr.ens.transcriptome.corsen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Median;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.rank.Percentile;

import fr.ens.transcriptome.corsen.util.Stats;
import fr.ens.transcriptome.corsen.util.Stats.DataDouble;

public class StatTest extends TestCase {

  private static final Random random = new Random(System.currentTimeMillis());

  public void testToDoube() {

    List<DataDouble> list = new ArrayList<DataDouble>();
    DataDouble dd1 = new DataDouble(2f, 5);
    DataDouble dd2 = new DataDouble(-3f, 3);
    DataDouble dd3 = new DataDouble(6f, 2);

    list.add(dd1);
    list.add(dd2);
    list.add(dd3);

    double[] result = Stats.toDouble(list);
    double sum = 0;

    for (double d : result)
      sum += d;

    assertEquals((double) (2 * 5 + (-3 * 3) + 6 * 2), sum);
  }

  private static final List<DataDouble> generate() {

    List<DataDouble> list = new ArrayList<DataDouble>();

    for (int i = 0; i < 100; i++) {

      DataDouble dd =
          new DataDouble(random.nextDouble() * 1000, random.nextInt(20));
      list.add(dd);

    }

    return list;
  }

  public void testPercentile() {

    Percentile percentile = new Percentile();

    for (int j = 0; j < 10; j++) {
      for (int i = 0; i < 98; i++) {

        List<DataDouble> list = generate();
        double p = random.nextInt(99) + 1;

        assertEquals(percentile.evaluate(Stats.toDouble(list), p), Stats
            .percentile(list, p));

      }
    }

  }

  public void testMin() {

    Min min = new Min();

    for (int i = 0; i < 10000; i++) {

      List<DataDouble> list = generate();
      assertEquals(min.evaluate(Stats.toDouble(list)), Stats.min(list));
    }
  }

  public void testFirstQuartile() {

    Percentile percentile = new Percentile();

    for (int i = 0; i < 1000; i++) {

      List<DataDouble> list = generate();
      assertEquals(percentile.evaluate(Stats.toDouble(list), 25.0), Stats
          .firstQuartile(list));
    }
  }

  public void testMedian() {

    Median median = new Median();

    for (int i = 0; i < 1000; i++) {

      List<DataDouble> list = generate();
      assertEquals(median.evaluate(Stats.toDouble(list)), Stats.median(list));
    }
  }

  public void testMean() {

    Mean mean = new Mean();

    for (int i = 0; i < 1000; i++) {

      List<DataDouble> list = generate();
      assertEquals(mean.evaluate(Stats.toDouble(list)), Stats.mean(list));
    }
  }

  public void testThirdQuartile() {

    Percentile percentile = new Percentile();

    for (int i = 0; i < 1000; i++) {

      List<DataDouble> list = generate();
      assertEquals(percentile.evaluate(Stats.toDouble(list), 75.0), Stats
          .thirdQuartile(list));
    }
  }

  public void testMax() {

    Max max = new Max();

    for (int i = 0; i < 1000; i++) {

      List<DataDouble> list = generate();
      assertEquals(max.evaluate(Stats.toDouble(list)), Stats.max(list));
    }
  }

}
