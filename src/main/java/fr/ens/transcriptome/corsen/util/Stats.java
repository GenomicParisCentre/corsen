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

package fr.ens.transcriptome.corsen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stats {

  public final static class DataDouble implements Comparable<DataDouble> {

    public double value;
    public long intensity;

    public int virtualIndexMin;
    public int virtualIndexMax;

    public final int compareTo(DataDouble d) {

      return Double.compare(value, d.value);
    }

    public final boolean isVirtualIndex(int index) {

      // TODO Optimize this !!!

      return index >= this.virtualIndexMin && index <= this.virtualIndexMax;
    }

    public DataDouble(final double value, final long intensity) {

      this.value = value;
      this.intensity = intensity;
    }

  }

  private static final int getRealSize(List<DataDouble> values) {

    if (values == null)
      return 0;

    int count = 0;

    for (DataDouble d : values) {

      d.virtualIndexMin = count;
      count += d.intensity;
      d.virtualIndexMax = count - 1;
      ;
    }

    return count;
  }

  public static double[] toDouble(List<DataDouble> values) {

    if (values == null)
      return null;

    int count = 0;

    for (DataDouble data : values)
      count += data.intensity;

    final double[] result = new double[count];

    int i = 0;
    for (DataDouble data : values) {
      final double val = data.value;
      for (int j = 0; j < data.intensity; j++)
        result[i++] = val;
    }

    return result;
  }

  private static double getVirtualValue(List<DataDouble> values, int index) {

    for (DataDouble d : values)

      if (d.isVirtualIndex(index))
        return d.value;

    return Double.NaN;
  }

  public static double percentile(final List<DataDouble> values, final double p) {

    if (values == null)
      throw new NullPointerException("values is null");

    if ((p > 100) || (p <= 0))
      throw new IllegalArgumentException("invalid quantile value: " + p);

    if (values.size() == 0)
      return Double.NaN;

    final List<DataDouble> valuesCopy = new ArrayList<DataDouble>(values);

    Collections.sort(valuesCopy);
    final int length = getRealSize(valuesCopy);

    if (length == 1)
      return valuesCopy.get(0).value; // always return single value for n = 1

    double n = length;
    double pos = p * (n + 1) / 100;
    double fpos = Math.floor(pos);
    int intPos = (int) fpos;
    double dif = pos - fpos;

    // double[] sorted = new double[length];
    // System.arraycopy(values, begin, sorted, 0, length);
    // Arrays.sort(sorted);

    if (pos < 1)
      return valuesCopy.get(0).value;

    if (pos >= n)
      return valuesCopy.get(length - 1).value;

    double lower = getVirtualValue(valuesCopy, intPos - 1);
    double upper = getVirtualValue(valuesCopy, intPos);

    return lower + dif * (upper - lower);
  }

  public static double min(final List<DataDouble> values) {

    if (values == null)
      throw new NullPointerException("values is null");

    if (values.size() == 0)
      return Double.NaN;

    DataDouble min = Collections.min(values);

    double minVal = Double.MAX_VALUE;
    for (DataDouble d : values)
      if (d.value < minVal)
        minVal = d.value;

    if (min.value != minVal)
      System.err.println("not same!!");

    return min.value;
  }

  public static double firstQuartile(final List<DataDouble> values) {

    return percentile(values, 25.0);
  }

  public static double median(final List<DataDouble> values) {

    return percentile(values, 50.0);
  }

  public static double mean(final List<DataDouble> values) {

    if (values == null)
      throw new NullPointerException("values is null");

    int count = 0;
    float sum = 0;

    for (DataDouble d : values) {

      count += d.intensity;
      sum += d.intensity * d.value;
    }

    return sum / count;
  }

  public static double thirdQuartile(final List<DataDouble> values) {

    return percentile(values, 75.0);
  }

  public static double max(final List<DataDouble> values) {

    if (values == null)
      throw new NullPointerException("values is null");

    if (values.size() == 0)
      return Double.NaN;

    DataDouble max = Collections.max(values);

    return max.value;
  }

}
