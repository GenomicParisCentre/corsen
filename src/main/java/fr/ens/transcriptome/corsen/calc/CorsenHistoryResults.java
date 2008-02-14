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

package fr.ens.transcriptome.corsen.calc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Median;

/**
 * This class handle last corsen results.
 * @author Laurent Jourdren
 */
public class CorsenHistoryResults {

  private static CorsenHistoryResults singleton = new CorsenHistoryResults();

  private Map<String, Entry> entries = new LinkedHashMap<String, Entry>();
  private List<String> keys = new ArrayList<String>();

  private static int count = 0;

  private double[] data;

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
     * Get median of the median distances.
     * @return the median of the median distances.
     */
    public double getMedianMinDistance() {

      return this.medianMinDistance;
    }

    private Entry(final File fileA, final File fileB, final File resultsPath,
        final double medianMinDistance) {

      this.fileA = fileA;
      this.fileB = fileB;
      this.resultsPath = resultsPath;
      this.medianMinDistance = medianMinDistance;
    }

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

    final double dist = cr.getMinAnalyser().getMedian();

    final Entry e = new Entry(fileA, fileB, cr.getResultsPath(), dist);

    this.entries.put(key, e);
    this.keys.add(key);

    this.data = null;
  }

  /**
   * Clear the entries.
   */
  public void clear() {

    this.entries.clear();
    this.keys.clear();
    this.data = null;
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

    this.data = null;
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

    if (data != null)
      return this.data;

    final double[] data = new double[size()];

    int count = 0;
    for (Map.Entry<String, Entry> e : this.entries.entrySet())
      data[count++] = e.getValue().getMedianMinDistance();

    this.data = data;

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
