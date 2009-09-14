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

package fr.ens.transcriptome.corsen.calc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.util.Stats;
import fr.ens.transcriptome.corsen.util.Stats.DataDouble;

public class DistanceAnalyser {

  private double min = Double.NaN;
  private double firstQuartile = Double.NaN;
  private double median = Double.NaN;
  private double mean = Double.NaN;
  private double thirdQuartile = Double.NaN;
  private double max = Double.NaN;

  private double minRegularValue = Double.NaN;
  private double maxRegularValue = Double.NaN;
  private double minOutlier = Double.NaN;
  private double maxOutlier = Double.NaN;
  private List<Double> listOutliers;

  private boolean minCalcDone;
  private boolean firstQuartileCalcDone;
  private boolean medianCalcDone;
  private boolean meanCalcDone;
  private boolean thirdQuartileCalcDone;
  private boolean maxCalcDone;
  private boolean outliersCalcDone;

  private List<DataDouble> data;

  private void setDistances(Map<Particle3D, Distance> mapDistances) {

    this.data = new ArrayList<DataDouble>();

    for (Map.Entry<Particle3D, Distance> e : mapDistances.entrySet()) {

      final Distance d = e.getValue();
      final Particle3D par = e.getKey();

      data.add(new DataDouble(d.getDistance(), par.getIntensity()));
    }

  }

  //
  // Other methods
  //

  public int count() {

    return this.data.size();
  }

  public double getMin() {

    if (!minCalcDone) {
      this.min = Stats.min(this.data);
      this.minCalcDone = true;
    }

    return this.min;
  }

  public double getFirstQuartile() {

    if (!firstQuartileCalcDone) {
      this.firstQuartile = Stats.firstQuartile(this.data);
      this.firstQuartileCalcDone = true;
    }

    return this.firstQuartile;
  }

  public double getMedian() {

    if (!medianCalcDone) {
      this.median = Stats.median(this.data);
      this.medianCalcDone = true;
    }

    return this.median;
  }

  public double getMean() {

    if (!this.meanCalcDone) {
      this.mean = Stats.mean(this.data);
      this.meanCalcDone = true;
    }

    return this.mean;
  }

  public double getThirdQuartile() {

    if (!this.thirdQuartileCalcDone) {
      this.thirdQuartile = Stats.thirdQuartile(this.data);
      this.thirdQuartileCalcDone = true;
    }

    return this.thirdQuartile;
  }

  public double getMax() {

    if (!this.maxCalcDone) {
      this.max = Stats.max(this.data);
      this.maxCalcDone = true;
    }

    return this.max;
  }

  public double getMinRegularValue() {

    if (!this.outliersCalcDone)
      calcOutliers();

    return this.minRegularValue;
  }

  public double getMaxRegularValue() {

    if (!this.outliersCalcDone)
      calcOutliers();

    return this.maxRegularValue;
  }

  public double getMinOutlier() {

    if (!this.outliersCalcDone)
      calcOutliers();

    return this.minOutlier;
  }

  public double getMaxOutlier() {

    if (!this.outliersCalcDone)
      calcOutliers();

    return this.maxOutlier;
  }

  public List<Double> getOutliers() {

    if (!this.outliersCalcDone)
      calcOutliers();

    return this.listOutliers;
  }

  private void load(final File file) throws IOException {

    InputStream is = new FileInputStream(file);

    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    this.data = new ArrayList<DataDouble>();

    String line;

    while ((line = reader.readLine()) != null) {

      if (line.startsWith("#"))
        continue;

      String[] values = line.split("\t");

      this.data.add(new DataDouble(Float.parseFloat(values[1]), Long
          .parseLong(values[0])));

    }
    reader.close();
  }

  private void calcOutliers() {

    final double q1 = getFirstQuartile();
    final double q3 = getThirdQuartile();

    double interQuartileRange = q3 - q1;

    double upperOutlierThreshold = q3 + (interQuartileRange * 1.5);
    double lowerOutlierThreshold = q1 - (interQuartileRange * 1.5);

    double upperFaroutThreshold = q3 + (interQuartileRange * 2.0);
    double lowerFaroutThreshold = q1 - (interQuartileRange * 2.0);

    double minRegularValue = Double.POSITIVE_INFINITY;
    double maxRegularValue = Double.NEGATIVE_INFINITY;
    double minOutlier = Double.POSITIVE_INFINITY;
    double maxOutlier = Double.NEGATIVE_INFINITY;

    final List<Double> outliers = new ArrayList<Double>();

    for (DataDouble d : data) {

      double value = d.value;
      if (value > upperOutlierThreshold) {
        outliers.add(value);
        if (value > maxOutlier && value <= upperFaroutThreshold) {
          maxOutlier = value;
        }
      } else if (value < lowerOutlierThreshold) {
        outliers.add(value);
        if (value < minOutlier && value >= lowerFaroutThreshold) {
          minOutlier = value;
        }
      } else {
        minRegularValue = Math.min(minRegularValue, value);
        maxRegularValue = Math.max(maxRegularValue, value);
      }
      minOutlier = Math.min(minOutlier, minRegularValue);
      maxOutlier = Math.max(maxOutlier, maxRegularValue);
    }

    this.minRegularValue = minRegularValue;
    this.maxRegularValue = maxRegularValue;
    this.minOutlier = minOutlier;
    this.maxOutlier = maxOutlier;
    this.listOutliers = outliers;

    this.outliersCalcDone = true;
  }

  public void calcAll() {

    this.min = Stats.min(this.data);
    this.minCalcDone = true;

    this.firstQuartile = Stats.firstQuartile(this.data);
    this.firstQuartileCalcDone = true;

    this.median = Stats.median(this.data);
    medianCalcDone = true;

    this.mean = Stats.mean(this.data);
    this.meanCalcDone = true;

    this.thirdQuartile = Stats.thirdQuartile(this.data);
    this.thirdQuartileCalcDone = true;

    this.max = Stats.max(this.data);
    this.maxCalcDone = true;
  }

  public String toString() {

    StringBuffer sb = new StringBuffer();

    sb.append("Min.\t1st Qu.\tMedian\tMean\t3rd Qu.\tMax.\n");

    sb.append(String.format("%2.3f\t%2.3f\t%2.3f\t%2.3f\t%2.3f\t%2.3f", this
        .getMin(), this.getFirstQuartile(), this.getMedian(), this.getMean(),
        this.getThirdQuartile(), this.getMax()));

    return sb.toString();
  }

  //
  // Constructor
  //

  public DistanceAnalyser(Map<Particle3D, Distance> mapDistances) {

    setDistances(mapDistances);
  }

  public DistanceAnalyser(final File file) throws IOException {

    load(file);
  }

}
