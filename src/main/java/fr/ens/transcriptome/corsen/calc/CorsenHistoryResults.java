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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.rank.Median;

/**
 * This class handle last corsen results.
 * @author Laurent Jourdren
 */
public class CorsenHistoryResults {

  private static CorsenHistoryResults singleton = new CorsenHistoryResults();

  private LinkedHashMap<String, Entry> entries =
      new LinkedHashMap<String, Entry>();
  private List<String> keys;

  private double[] data;

  /**
   * Define an entry of the last corsen results.
   * @author Laurent Jourdren
   */
  public class Entry {

    private String fileA;
    private String fileB;
    private double medianMinDistance;

    /**
     * Get File A.
     * @return the file A
     */
    public String getFileA() {

      return this.fileA;
    }

    /**
     * Get File B.
     * @return the file B
     */
    public String getFileB() {

      return this.fileB;
    }

    /**
     * Get median of the median distances.
     * @return the median of the median distances.
     */
    public double getMedianMinDistance() {

      return this.medianMinDistance;
    }

    private Entry(final String fileA, final String fileB,
        final double medianMinDistance) {

      this.fileA = fileA;
      this.fileB = fileB;
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

    final String fileA = cr.getMessengersFilename();
    final String fileB = cr.getMitosFilename();

    final String key = fileA + "-" + fileB;

    final double dist = cr.getMinAnalyser().getMedian();

    this.entries.put(key, new Entry(fileA, fileB, dist));

    this.data = null;
  }

  /**
   * Clear the entries.
   */
  public void clear() {

    this.keys = null;
    this.entries.clear();
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
   * Get an entry
   * @param index index of the element to get
   * @return an antry
   */
  public Entry getEntry(int index) {

    if (this.keys == null)
      fillKeys();

    return this.entries.get(this.keys.get(index));
  }

  /**
   * Remove an entry
   * @param index index of the element to get
   */
  public void removeEntry(int index) {

    if (this.entries.remove(this.keys.get(index)) != null) {
      this.keys = null;
      this.data = null;
    }

  }

  private void fillKeys() {

    final List<String> result = new ArrayList<String>();

    Set<String> set = this.entries.keySet();

    for (String s : set)
      result.add(s);

    this.keys = result;
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
    for (String key : this.entries.keySet()) {

      Entry e = this.entries.get(key);
      data[count++] = e.getMedianMinDistance();
    }

    return data;
  }

  /**
   * Get the median of the median of Min Distances.
   * @return thee median of the median of Min Distances
   */
  public double getMedianOfMedianMinDistances() {

    return new Median().evaluate(getDistances());
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
