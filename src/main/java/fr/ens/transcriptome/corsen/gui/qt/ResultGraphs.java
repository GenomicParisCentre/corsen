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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;

import com.trolltech.qt.gui.QImage;

import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.util.SystemUtil;

/**
 * This class create graph for corsen results
 * @author Laurent Jourdren
 */
public class ResultGraphs {

  private static final int IMAGE_WIDTH_DEFAULT = 500;
  private static final int IMAGE_HEIGHT_DEFAULT = 500;

  private int width = IMAGE_WIDTH_DEFAULT;
  private int height = IMAGE_HEIGHT_DEFAULT;

  private static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);

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

    final int size = buffer.getSize();

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

  private void createHistoDataSet(final Map<Particle3D, Distance> dists,
      final String name, final HistogramDataset histogramdataset,
      final int classes) {

    ArrayList<Float> list = new ArrayList<Float>();

    for (Map.Entry<Particle3D, Distance> e : dists.entrySet()) {

      final long intensity = e.getKey().getIntensity();
      final float distance = e.getValue().getDistance();

      for (int i = 0; i < intensity; i++)
        list.add(distance);
    }

    final double[] data = new double[list.size()];
    for (int i = 0; i < data.length; i++)
      data[i] = list.get(i);

    final double max = getMax(data);
    final double min = getMin(data);

    histogramdataset.addSeries(name, data, classes, min, max);
  }

  public QImage createDistanceDistributionImage(final double[] data,
      final int classes) {

    if (data == null || data.length < 2)
      return null;

    HistogramDataset histogramdataset = new HistogramDataset();

    histogramdataset.addSeries("Min distances", data, classes, getMin(data),
        getMax(data));

    // createHistoDataSet(results.getMaxDistances(), "Max distances",
    // histogramdataset);

    JFreeChart chart =
        ChartFactory.createHistogram("Distribution of minimal distances",
        // title
            "Distance", // domain axis label
            "Intensity", // range axis label
            histogramdataset, // data

            PlotOrientation.VERTICAL, // orientation
            false, // include legend
            true, // tooltips?
            false // URLs?
            );

    if (SystemUtil.isWindowsSystem() || SystemUtil.isMacOsX())
      chart.setBackgroundPaint(TRANSPARENT_COLOR);

    final BufferedImage image =
        chart.createBufferedImage(this.width, this.height,
            BufferedImage.TYPE_INT_ARGB, null);

    return new QImage(toByte(image.getData().getDataBuffer()), this.width,
        this.height, QImage.Format.Format_ARGB32);
  }

  /**
   * Create a histogram of distance distribution.
   * @param results Result to use
   * @return a QImage of the graph
   */
  public QImage createDistanceDistributionImage(final CorsenResult results,
      final int classes) {

    HistogramDataset histogramdataset = new HistogramDataset();

    createHistoDataSet(results.getMinDistances(), "Min distances",
        histogramdataset, classes);
    // createHistoDataSet(results.getMaxDistances(), "Max distances",
    // histogramdataset);

    JFreeChart chart =
        ChartFactory.createHistogram("Distribution of minimal distances",
        // title
            "Distance", // domain axis label
            "Intensity", // range axis label
            histogramdataset, // data

            PlotOrientation.VERTICAL, // orientation
            false, // include legend
            true, // tooltips?
            false // URLs?
            );

    if (SystemUtil.isWindowsSystem() || SystemUtil.isMacOsX())
      chart.setBackgroundPaint(TRANSPARENT_COLOR);

    final BufferedImage image =
        chart.createBufferedImage(this.width, this.height,
            BufferedImage.TYPE_INT_ARGB, null);

    return new QImage(toByte(image.getData().getDataBuffer()), this.width,
        this.height, QImage.Format.Format_ARGB32);
  }

  /**
   * Create a box plot qimage.
   * @param data Data to use
   * @return a QImage
   */
  public QImage createBoxPlot(final double[] data) {

    if (data == null || data.length < 2)
      return null;

    this.width = this.width / 2;

    List<Float> listData = new ArrayList<Float>(data.length);
    for (int i = 0; i < data.length; i++)
      listData.add((float) data[i]);

    DefaultBoxAndWhiskerCategoryDataset defaultboxandwhiskercategorydataset =
        new DefaultBoxAndWhiskerCategoryDataset();
    defaultboxandwhiskercategorydataset.add(listData, "Distances", "Min");

    JFreeChart chart =
        ChartFactory.createBoxAndWhiskerChart("Intensities Boxplot", "",
            "Distance", defaultboxandwhiskercategorydataset, false);

    if (SystemUtil.isWindowsSystem() || SystemUtil.isMacOsX())
      chart.setBackgroundPaint(TRANSPARENT_COLOR);

    final BufferedImage image =
        chart.createBufferedImage(this.width, this.height,
            BufferedImage.TYPE_INT_ARGB, null);

    return new QImage(toByte(image.getData().getDataBuffer()), this.width,
        this.height, QImage.Format.Format_ARGB32);

  }

  /**
   * Create a boxplot.
   * @param results Results to use
   * @return a QImage
   */
  public QImage createBoxPlot(final CorsenResult results) {

    this.width = this.width / 2;

    Map<Particle3D, Distance> distsMin = results.getMinDistances();
    Map<Particle3D, Distance> distsMax = results.getMaxDistances();

    if (distsMin == null || distsMax == null)
      return null;

    List<Float> listMin = new ArrayList<Float>();
    List<Float> listMax = new ArrayList<Float>();

    for (Map.Entry<Particle3D, Distance> e : distsMin.entrySet()) {

      final Particle3D p = e.getKey();

      final long intensity = p.getIntensity();
      final float distanceMin = e.getValue().getDistance();
      final float distanceMax = distsMax.get(p).getDistance();

      for (int i = 0; i < intensity; i++) {
        listMin.add(distanceMin);
        listMax.add(distanceMax);
      }
    }

    DefaultBoxAndWhiskerCategoryDataset defaultboxandwhiskercategorydataset =
        new DefaultBoxAndWhiskerCategoryDataset();
    defaultboxandwhiskercategorydataset.add(listMin, "Distances", "Min");
    // defaultboxandwhiskercategorydataset.add(listMax, "Distances", "Max");

    JFreeChart chart =
        ChartFactory.createBoxAndWhiskerChart("Intensities Boxplot", "",
            "Distance", defaultboxandwhiskercategorydataset, false);

    if (SystemUtil.isWindowsSystem() || SystemUtil.isMacOsX())
      chart.setBackgroundPaint(TRANSPARENT_COLOR);

    // CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
    // // chart.setBackgroundPaint(Color.white);
    // categoryplot.setBackgroundPaint(Color.lightGray);
    // categoryplot.setDomainGridlinePaint(Color.white);
    // categoryplot.setDomainGridlinesVisible(true);
    // categoryplot.setRangeGridlinePaint(Color.white);
    //
    // NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
    // numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    final BufferedImage image =
        chart.createBufferedImage(this.width, this.height,
            BufferedImage.TYPE_INT_ARGB, null);

    return new QImage(toByte(image.getData().getDataBuffer()), this.width,
        this.height, QImage.Format.Format_ARGB32);
  }

  /**
   * Create a scatter plot
   * @param particles Particle data to use
   * @param title Title of the graph
   * @return a QImage
   */
  public QImage createScatterPlot(final Particles3D particles,
      final String title) {

    if (particles == null)
      return null;

    List<Particle3D> pars = particles.getParticles();

    final int count = pars.size();

    final double[][] data = new double[2][];
    data[0] = new double[count];
    data[1] = new double[count];

    int i = 0;
    for (Particle3D p : pars) {

      data[0][i] = p.getIntensity();
      data[1][i] = p.getVolume();
      i++;
    }

    DefaultXYDataset xydataset = new DefaultXYDataset();
    xydataset.addSeries("data", data);

    JFreeChart chart =
        ChartFactory.createScatterPlot(title, "Intensity", "Volume", xydataset,
            PlotOrientation.VERTICAL, false, true, false);

    if (SystemUtil.isWindowsSystem() || SystemUtil.isMacOsX())
      chart.setBackgroundPaint(new Color(255, 255, 255, 0));

    XYPlot xyplot = (XYPlot) chart.getPlot();
    XYDotRenderer xydotrenderer = new XYDotRenderer();
    xydotrenderer.setDotWidth(2);
    xydotrenderer.setDotHeight(2);
    xyplot.setRenderer(xydotrenderer);
    NumberAxis numberaxis = (NumberAxis) xyplot.getDomainAxis();
    numberaxis.setAutoRangeIncludesZero(false);

    final BufferedImage image =
        chart.createBufferedImage(this.width, this.height,
            BufferedImage.TYPE_INT_ARGB, null);

    return new QImage(toByte(image.getData().getDataBuffer()), this.width,
        this.height, QImage.Format.Format_ARGB32);
  }

}
