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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Median;

/**
 * This class handle last corsen results.
 * @author Laurent Jourdren
 */
public class CorsenHistoryResults {

  private static CorsenHistoryResults singleton = new CorsenHistoryResults();

  private LinkedHashMap<Integer, Entry> entries =
      new LinkedHashMap<Integer, Entry>();
  private Set<String> filesKeys = new HashSet<String>();
  private List<Integer> index = new ArrayList<Integer>();

  // private List<String> keys = new ArrayList<String>();

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

    if (!this.filesKeys.contains(key)) {

      final double dist = cr.getMinAnalyser().getMedian();

      final Entry e = new Entry(fileA, fileB, cr.getResultsPath(), dist);
      final int id = e.getId();

      this.entries.put(id, e);
      this.filesKeys.add(key);
      this.index.add(id);

      this.data = null;
    }
  }

  /**
   * Clear the entries.
   */
  public void clear() {

    this.entries.clear();
    this.filesKeys.clear();
    this.index.clear();
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
   * @param id index of the element to get
   * @return an antry
   */
  public Entry getEntry(int id) {

    return this.entries.get(id);
  }

  /**
   * Get an entry by this index.
   * @param index Index of the entry to get
   * @return an Entry object
   */
  public Entry get(final int index) {

    return getEntry(this.index.get(index));
  }

  /**
   * Remove an entry by this index
   * @param index
   */
  public void remove(final int index) {

    removeEntry(this.index.get(index));
  }

  /**
   * Remove an entry
   * @param id index of the element to get
   */
  public void removeEntry(int id) {

    Entry e = getEntry(id);

    if (e != null) {

      String key =
          e.getFileA().getAbsolutePath() + "-" + e.getFileB().getAbsolutePath();
      this.filesKeys.remove(key);
      this.entries.remove(id);

      // Recreate the index
      this.index.clear();
      for (int id2 : this.entries.keySet())
        this.index.add(id2);

      this.data = null;
    }

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
    for (int id : this.entries.keySet()) {

      Entry e = this.entries.get(id);
      data[count++] = e.getMedianMinDistance();
    }

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
