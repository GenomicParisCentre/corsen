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

  private boolean minCalcDone;
  private boolean firstQuartileCalcDone;
  private boolean medianCalcDone;
  private boolean meanCalcDone;
  private boolean thirdQuartileCalcDone;
  private boolean maxCalcDone;

  private List<DataDouble> data;

  private void setDistances(Map<Particle3D, Distance> mapDistances) {

    this.data = new ArrayList<DataDouble>();

    for (Particle3D par : mapDistances.keySet()) {

      Distance d = mapDistances.get(par);

      data.add(new DataDouble(d.getDistance(), par.getIntensity()));
    }

  }

  //
  // Other methods
  //

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
    is.close();
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

  public static void main(String[] args) throws IOException {

    DistanceAnalyser da =
        new DistanceAnalyser(new File(
            "/home/jourdren/Desktop/atp16/result.result.data"));

    System.out.println("Min.\t1st Qu.\tMedian\tMean\t3rd Qu.\tMax.");

    System.out.printf("%2.3f  %2.3f  %2.3f  %2.3f  %2.3f  %2.3f\n\n", da
        .getMin(), da.getFirstQuartile(), da.getMedian(), da.getMean(), da
        .getThirdQuartile(), da.getMax());

    System.out.println("  Min. 1st Qu.  Median    Mean 3rd Qu.    Max.\n"
        + "-1.729  14.300  22.170  21.080  29.440  39.510");

  }
}
