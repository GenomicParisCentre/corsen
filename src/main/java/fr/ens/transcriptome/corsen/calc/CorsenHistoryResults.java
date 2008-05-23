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

package fr.ens.transcriptome.corsen.calc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Median;

import fr.ens.transcriptome.corsen.model.Particle3D;

/**
 * This class handle last corsen results.
 * @author Laurent Jourdren
 */
public final class CorsenHistoryResults {

  private static CorsenHistoryResults singleton = new CorsenHistoryResults();

  private Map<String, Entry> entries = new LinkedHashMap<String, Entry>();
  private List<String> keys = new ArrayList<String>();

  private static int count = 0;

  private Map<StatType, double[]> data = new HashMap<StatType, double[]>();
  private StatType statType = CorsenHistoryResults.StatType.values()[0];

  private CompiledScript script;

  public enum StatType {

    MEDIAN("median"), MEAN("mean"), MIN("min"), MAX("max"), CUSTOM("custom");

    private String description;

    /**
     * Get the description of the type of stat.
     * @return the description of the stat
     */
    public String toString() {

      return this.description;
    }

    /**
     * Get a Stat type from this description.
     * @param description description of the type
     * @return a StatType
     */
    public static StatType getTypeFromDescription(String description) {

      if (description == null)
        return null;

      StatType[] types = StatType.values();

      for (int i = 0; i < types.length; i++) {

        if (description.equals(types[i].toString()))
          return types[i];
      }

      return null;
    }

    //
    // Constructor
    //

    /**
     * Private constructor.
     * @param description The description of the stat
     */
    private StatType(final String description) {

      this.description = description;

    }

  };

  /**
   * Define an entry of the last corsen results.
   * @author Laurent Jourdren
   */
  public static final class Entry {

    private int id = count++;
    private File fileA;
    private File fileB;
    private File resultsPath;
    private double medianMinDistance;
    private double meanMinDistance;
    private double minMinDistance;
    private double maxMinDistance;
    private double customMinDistance;

    /**
     * Get the id of the entry.
     * @return the id of the entry
     */
    public int getId() {

      return this.id;
    }

    /**
     * Get File A.
     * @return the file A
     */
    public File getFileA() {

      return this.fileA;
    }

    /**
     * Get File B.
     * @return the file B
     */
    public File getFileB() {

      return this.fileB;
    }

    /**
     * Get the result path.
     * @return the result path
     */
    public File getResultsPath() {

      return this.resultsPath;
    }

    /**
     * Get median of the min distances.
     * @return the median of the median distances.
     */
    public double getMedianMinDistance() {

      return this.medianMinDistance;
    }

    /**
     * Get mean of the min distances.
     * @return the mean of the min distances.
     */
    public double getMeanMinDistance() {

      return this.meanMinDistance;
    }

    /**
     * Get the min of the min distances.
     * @return the min of the min distances.
     */
    public double getMinMinDistance() {

      return this.minMinDistance;
    }

    /**
     * Get the max of the min distances.
     * @return the max of the min distances.
     */
    public double getMaxMinDistance() {

      return this.maxMinDistance;
    }

    /**
     * Get custom min distances.
     * @return the custom min distances.
     */
    public double getCustomMinDistance() {

      return this.customMinDistance;
    }

    /**
     * Set the custom value
     * @param customValue Value to set
     */
    private void setCustom(double customValue) {

      this.customMinDistance = customValue;
    }

    private Entry(final File fileA, final File fileB, final CorsenResult cr) {

      // final double dist = cr.getMinAnalyser().getMedian();
      final DistanceAnalyser da = cr.getMinAnalyser();

      this.fileA = fileA;
      this.fileB = fileB;
      this.resultsPath = cr.getResultsPath();
      this.medianMinDistance = da.getMedian();
      this.meanMinDistance = da.getMean();
      this.minMinDistance = da.getMin();
      this.maxMinDistance = da.getMax();
    }

  }

  /**
   * Get the stat type.
   * @return The stat type
   */
  public StatType getStatType() {

    return statType;
  }

  /**
   * Set the statType
   * @param statType StatType to set
   */
  public void setStatType(final StatType statType) {

    this.statType = statType;
  }

  /**
   * Add a result to the results.
   * @param cr Corsen result to add
   */
  public void addResult(final CorsenResult cr) {

    if (cr == null)
      return;

    final File fileA = cr.getMessengersFile();
    final File fileB = cr.getMitosFile();

    final String key = fileA.getAbsolutePath() + "-" + fileB.getAbsolutePath();

    if (this.entries.containsKey(key))
      this.keys.remove(key);

    final Entry e = new Entry(fileA, fileB, cr);
    e.setCustom(calcCustomValue(cr));

    this.entries.put(key, e);
    this.keys.add(key);

    this.data.clear();
  }

  /**
   * Clear the entries.
   */
  public void clear() {

    this.entries.clear();
    this.keys.clear();
    this.data.clear();
  }

  /**
   * Get the number of entries.
   * @return The number of entries
   */
  public int size() {

    return this.entries.size();
  }

  /**
   * Remove an entry
   * @param id index of the element to get
   */
  public void remove(final int index) {

    String key = this.keys.get(index);

    this.entries.remove(key);
    this.keys.remove(index);

    this.data.clear();
  }

  /**
   * Get an entry
   * @param index Index of the entry to get
   * @return an entry
   */
  public Entry get(final int index) {

    final String key = this.keys.get(index);

    return this.entries.get(key);
  }

  /**
   * Get an array of the minimal distances
   * @return an array of the minimal distances
   */
  public double[] getDistances() {

    if (data.containsKey(this.statType))
      return this.data.get(this.statType);

    final double[] data = new double[size()];

    int count = 0;
    for (Map.Entry<String, Entry> e : this.entries.entrySet()) {

      final double value;

      switch (this.statType) {

      case MEAN:
        value = e.getValue().getMeanMinDistance();
        break;

      case MIN:
        value = e.getValue().getMinMinDistance();
        break;

      case MAX:
        value = e.getValue().getMaxMinDistance();
        break;

      case CUSTOM:
        value = e.getValue().getCustomMinDistance();
        break;

      case MEDIAN:
      default:
        value = e.getValue().getMedianMinDistance();
        break;

      }

      data[count++] = value;
    }

    this.data.put(this.statType, data);

    return data;
  }

  /**
   * Get the median of the median of Min Distances.
   * @return thee median of the median of Min Distances
   */
  public double getMedianOfMedianMinDistances() {

    return new Median().evaluate(getDistances());
  }

  /**
   * Get the mean of the median of Min Distances.
   * @return thee mean of the median of Min Distances
   */
  public double getMeanOfMedianMinDistances() {

    return new Mean().evaluate(getDistances());
  }

  /**
   * Set the custom expression
   * @param expression The expression to set
   * @return true if the expression is correct
   */
  public boolean setCustomExpression(final String expression) {

    this.script = null;

    if (expression == null || "".equals(expression.trim()))
      return true;

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

    Compilable compilable = (Compilable) engine;

    try {

      final CompiledScript script = compilable.compile(expression);
      this.script = script;

    } catch (ScriptException e) {

      return false;
    }

    return true;
  }

  /**
   * Calc the custom value.
   * @param cr CorsenResults
   * @return the custom value
   */
  private double calcCustomValue(final CorsenResult cr) {

    if (this.script == null)
      return Double.NaN;

    Map<Particle3D, Distance> in = cr.getMinDistances();

    long inCount = 0;
    long outCount = 0;

    for (Map.Entry<Particle3D, Distance> e : in.entrySet()) {

      final Particle3D particle = e.getKey();
      final long intensity = particle.getIntensity();
      final Distance distance = e.getValue();

      inCount += intensity;

      final Bindings b =
          this.script.getEngine().getBindings(ScriptContext.ENGINE_SCOPE);

      b.put("i", intensity);
      b.put("d", distance.getDistance());

      try {

        Object o = this.script.eval();

        if (o instanceof Boolean && ((Boolean) o) == true)
          outCount += intensity;

      } catch (ScriptException e1) {
      }
    }

    return (double) outCount / (double) inCount;
  }

  //
  // Singleton
  //

  /**
   * Get the singleton.
   * @return the CorsenHistoryResults object
   */
  public static CorsenHistoryResults getCorsenHistoryResults() {

    return singleton;
  }

  //
  // Constructor
  //

  private CorsenHistoryResults() {
  }

}
