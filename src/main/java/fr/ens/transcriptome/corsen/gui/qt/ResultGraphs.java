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

package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;

import com.trolltech.qt.gui.QImage;

import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.model.Particle3D;

/**
 * This class create graph for corsen results
 * @author Laurent Jourdren
 */
public class ResultGraphs {

  private static final int IMAGE_WIDTH_DEFAULT = 500;
  private static final int IMAGE_HEIGHT_DEFAULT = 500;

  private int width = IMAGE_WIDTH_DEFAULT;
  private int height = IMAGE_HEIGHT_DEFAULT;

  //
  // Getters
  //

  /**
   * Get the width of the image.
   * @return the width of the image
   */
  public int getWidth() {
    return width;
  }

  /**
   * Get the height of the image
   * @return the height of the image
   */
  public int getHeight() {
    return height;
  }

  //
  // Setters
  //

  /**
   * Set the width of the image
   * @param width the width of the image
   */
  public void setWidth(final int width) {
    this.width = width;
  }

  /**
   * Set the height of the image
   * @param height the height of the image
   */
  public void setHeight(final int height) {
    this.height = height;
  }

  //
  // Utility methods
  //

  private static final byte[] toByte(DataBuffer buffer) {

    if (buffer == null)
      return null;

    System.out.println(buffer.getDataType());

    final int size = buffer.getSize();
    System.out.println(size);

    byte[] result = new byte[size * 4];

    for (int i = 0; i < size; i++) {

      final int val = buffer.getElem(i);
      final int j = i * 4;

      result[j + 3] = (byte) (((val & 0xFF000000) >> 24) & 0xFF);
      result[j + 2] = (byte) (((val & 0x00FF0000) >> 16) & 0xFF);
      result[j + 1] = (byte) (((val & 0x0000FF00) >> 8) & 0xFF);
      result[j] = (byte) (val & 0xFF);
    }

    return result;
  }

  private static final double getMax(final double[] array) {

    double max = Double.MIN_VALUE;

    if (array == null)
      return max;

    for (int i = 0; i < array.length; i++)
      if (array[i] > max)
        max = array[i];

    return max;
  }

  private static final double getMin(final double[] array) {

    double min = Double.MAX_VALUE;

    if (array == null)
      return min;

    for (int i = 0; i < array.length; i++)
      if (array[i] < min)
        min = array[i];

    return min;
  }

  //
  // Image generating methods
  //

  /**
   * Create a histogram of distance distribution.
   * @param results Result to use
   * @return a QImage of the graph
   */
  public QImage createDistanceDistributionImage(final CorsenResult results) {

    Map<Particle3D, Distance> dists = results.getMinDistances();

    ArrayList<Float> list = new ArrayList<Float>();

    for (Particle3D p : dists.keySet()) {

      final long intensity = p.getIntensity();
      final float distance = dists.get(p).getDistance();
      // System.out.println("d=" + distance);

      for (int i = 0; i < intensity; i++)
        list.add(distance);
    }

    final double[] data = new double[list.size()];
    for (int i = 0; i < data.length; i++)
      data[i] = list.get(i);

    HistogramDataset histogramdataset = new HistogramDataset();

    final double max = getMax(data);
    final double min = getMin(data);

    histogramdataset.addSeries("Particles", data, 50, min, max);
    // ((int) (max / 10)) * 10 + 1);

    JFreeChart chart =
        ChartFactory.createHistogram("Distribution of minimal distances",
        // title
            "Distance", // domain axis label
            "Intensity", // range axis label
            histogramdataset, // data

            PlotOrientation.VERTICAL, // orientation
            true, // include legend
            true, // tooltips?
            false // URLs?
            );

    final BufferedImage image =
        chart.createBufferedImage(this.width, this.height,
            BufferedImage.TYPE_INT_ARGB, null);

    return new QImage(toByte(image.getData().getDataBuffer()), this.width,
        this.height, QImage.Format.Format_ARGB32);
  }

}
