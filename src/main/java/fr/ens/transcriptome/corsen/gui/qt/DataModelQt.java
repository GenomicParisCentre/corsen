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

package fr.ens.transcriptome.corsen.gui.qt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.trolltech.qt.core.QAbstractItemModel;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QAbstractTableModel;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QSortFilterProxyModel;

import fr.ens.transcriptome.corsen.CorsenResultWriter;
import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.calc.DistanceAnalyser;
import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults.Entry;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.util.Util;

public class DataModelQt {

  private static final int VIEWS_COUNT = 6;
  private static final String DATA_SUMMARY_DESCRIPTION = "Data result summary";
  private static final String DATA_VIEW_DESCRIPTION = "Full data results";
  private static final String IV_MESSENGERS_DESCRIPTION =
      "Intensities and volumes of messengers";
  private static final String IV_MESSENGERS_CUBOIDS_DESCRIPTION =
      "Intensities and volumes of messengers cuboids";
  private static final String IV_MITO_DESCRIPTION =
      "Intensities and volumes of mitochondria ";
  private static final String IV_MITO_CUBOIDS_DESCRIPTION =
      "Intensities and volumes of mitochondria cuboids";

  private static HistoryDataModel historyModelSingleton =
      new HistoryDataModel();
  private CorsenResult result;
  private Map<Integer, QImage> cacheImage = new HashMap<Integer, QImage>();
  private Map<Integer, QPixmap> cachePixmap = new HashMap<Integer, QPixmap>();

  private static final class FullDataModel extends QAbstractTableModel {

    private Map<Particle3D, Distance> mins = null;
    private Map<Particle3D, Distance> maxs = null;
    private List<Particle3D> listParticles;

    private int nRows;

    @Override
    public int columnCount(QModelIndex arg0) {

      return 4;
    }

    @Override
    public int rowCount(QModelIndex arg0) {

      return nRows;
    }

    @Override
    public Object data(QModelIndex mIndex, int role) {

      if (mIndex == null || role != Qt.ItemDataRole.DisplayRole)
        return null;

      final int row = mIndex.row();
      final int col = mIndex.column();

      Particle3D p = this.listParticles.get(row);

      switch (col) {

      case 0:
        return p.getName();

      case 1:
        return p.getIntensity();

      case 2:
        return (double) mins.get(p).getDistance();

      case 3:
        return (double) maxs.get(p).getDistance();

      default:
        return null;
      }

    }

    @Override
    public Object headerData(int section, Orientation orientation, int role) {

      if (orientation != Orientation.Horizontal
          || role != Qt.ItemDataRole.DisplayRole)
        return null;

      switch (section) {
      case 0:
        return "Particle Id";

      case 1:
        return "Intensity";

      case 2:
        return "Min";

      case 3:
        return "Max";

      default:
        return null;
      }

    }

    //
    // Constructor
    //

    public FullDataModel(final CorsenResult r) {

      if (r == null)
        return;
      mins = r.getMinDistances();
      maxs = r.getMaxDistances();
      listParticles = new ArrayList<Particle3D>(mins.keySet());

      nRows = mins.size();
    }

  }

  private static class IVModel extends QAbstractTableModel {

    private List<Particle3D> pars;

    @Override
    public int columnCount(QModelIndex arg0) {

      return 3;
    }

    @Override
    public Object data(final QModelIndex mIndex, final int role) {

      if (mIndex == null || role != Qt.ItemDataRole.DisplayRole)
        return null;

      Particle3D p = this.pars.get(mIndex.row());
      final int col = mIndex.column();

      switch (col) {
      case 0:
        return p.getName();

      case 1:
        return p.getIntensity();

      case 2:
        return p.getVolume();

      default:
        break;
      }

      return null;
    }

    @Override
    public int rowCount(QModelIndex arg0) {

      return this.pars == null ? 0 : pars.size();
    }

    @Override
    public Object headerData(int section, Orientation orientation, int role) {

      if (orientation != Orientation.Horizontal
          || role != Qt.ItemDataRole.DisplayRole)
        return null;

      switch (section) {
      case 0:
        return "Particle Id";

      case 1:
        return "Intensity";

      case 2:
        return "Volume";

      default:
        return null;
      }

    }

    //
    // Constructor
    //

    public IVModel(final Particles3D particles) {

      if (particles != null)
        this.pars = particles.getParticles();
    }

  }

  private static class SummaryDataModel extends QAbstractTableModel {

    private DistanceAnalyser daMins = null;
    private DistanceAnalyser daMaxs = null;

    @Override
    public int columnCount(QModelIndex arg0) {

      return 7;
    }

    @Override
    public Object data(QModelIndex mIndex, int role) {

      if (mIndex == null || role != Qt.ItemDataRole.DisplayRole)
        return null;

      if (daMins == null || daMaxs == null)
        return null;

      final int row = mIndex.row();
      final int col = mIndex.column();

      if (col == 0) {

        if (row == 0)
          return "Min distances";

        return "Max distances";
      }

      DistanceAnalyser da = row == 0 ? this.daMins : this.daMaxs;

      switch (col) {

      case 1:
        return da.getMin();

      case 2:
        return da.getFirstQuartile();

      case 3:
        return da.getMedian();

      case 4:
        return da.getMean();

      case 5:
        return da.getThirdQuartile();

      case 6:
        return da.getMax();

      default:
        return null;

      }

    }

    @Override
    public int rowCount(QModelIndex arg0) {

      int count = 0;

      if (daMins != null)
        count++;
      if (daMaxs != null)
        count++;

      return count;
    }

    @Override
    public Object headerData(int section, Orientation orientation, int role) {

      if (orientation != Orientation.Horizontal
          || role != Qt.ItemDataRole.DisplayRole)
        return null;

      switch (section) {

      case 0:
        return "";

      case 1:
        return "Mininum";

      case 2:
        return "1st Quartile";

      case 3:
        return "Median";

      case 4:
        return "Mean";

      case 5:
        return "3rd Quartile";

      case 6:
        return "Maximum";

      default:
        return null;
      }

    }

    //
    // Constructor
    //

    public SummaryDataModel(final CorsenResult r) {

      if (r == null)
        return;

      this.daMins = r.getMinAnalyser();
      this.daMaxs = r.getMaxAnalyser();
    }

  }

  static class HistoryDataModel extends QAbstractTableModel {

    private CorsenHistoryResults results =
        CorsenHistoryResults.getCorsenHistoryResults();

    private static final DecimalFormat percentFormat =
        new DecimalFormat("00.000%", new DecimalFormatSymbols(Locale.UK));

    @Override
    public int columnCount(QModelIndex arg0) {

      return 4;
    }

    @Override
    public Object data(QModelIndex mIndex, int role) {

      if (mIndex == null || role != Qt.ItemDataRole.DisplayRole)
        return null;

      Entry e = this.results.get(mIndex.row());

      final int col = mIndex.column();

      switch (col) {

      case 0:
        return e.getId();

      case 1:
        return e.getFileA().getName();

      case 2:
        return e.getFileB().getName();

      case 3:
        switch (results.getStatType()) {

        case CUSTOM:

          double val = e.getCustomMinDistance();
          if (Double.isInfinite(val) || Double.isNaN(val))
            return val;

          return percentFormat.format(val);

        case MIN:
          return e.getMinMinDistance();

        case MAX:
          return e.getMaxMinDistance();

        case MEAN:
          return e.getMeanMinDistance();

        case MEDIAN:
        default:
          return e.getMedianMinDistance();

        }

      default:
        break;
      }

      return null;
    }

    @Override
    public int rowCount(QModelIndex arg0) {

      return results.size();
    }

    @Override
    public Object headerData(int section, Orientation orientation, int role) {

      if (orientation != Orientation.Horizontal
          || role != Qt.ItemDataRole.DisplayRole)
        return null;

      switch (section) {

      case 0:
        return "Id";

      case 1:
        return "File A";

      case 2:
        return "File B";

      case 3:

        switch (results.getStatType()) {

        case CUSTOM:
          return "Percent of particles";
        default:
          return "Minimal distance";
        }

      default:
        return null;
      }

    }

    public QPixmap getBoxplot(final Settings settings) {

      final QImage img =
          new ResultGraphs().createBoxPlot(Util.removeNaN(this.results
              .getDistances()), settings.getUnit());

      if (img == null)
        return null;

      return QPixmap.fromImage(img);
    }

    public QPixmap getHisto(final int classes, final Settings settings) {

      final QImage img =
          new ResultGraphs().createDistanceDistributionImage(Util
              .removeNaN(this.results.getDistances()), classes, settings
              .getUnit());

      if (img == null)
        return null;

      return QPixmap.fromImage(img);
    }

    public String getResultMessage() {

      final double median = this.results.getMedianOfMedianMinDistances();
      final double mean = this.results.getMeanOfMedianMinDistances();

      StringBuilder sb = new StringBuilder();
      sb.append("The median of minimal distances is: ");

      if (Double.isNaN(median))
        sb.append("undefined");
      else
        sb.append(String.format("%.3f", median));

      sb.append(" and the mean of minimal distances is: ");

      if (Double.isNaN(mean))
        sb.append("undefined");
      else
        sb.append(String.format("%.3f", mean));

      sb.append(" (");
      sb.append(this.results.size());
      if (this.results.size() > 1)
        sb.append(" cells).");
      else
        sb.append(" cell).");

      return sb.toString();
    }

    /**
     * Change the statistical type view
     * @param name the name of the type
     */
    public void setType(final String name) {

      results.setStatType(CorsenHistoryResults.StatType
          .getTypeFromDescription(name));
    }

    public void update() {

      super.reset();
    }

  }

  //
  // Getters
  //

  /**
   * Get the HistoryDataModel model.
   * @return the HistoryDataModel model
   */
  public static HistoryDataModel getHistoryModel() {

    return historyModelSingleton;
  }

  /**
   * Get the corsen result.
   * @return Returns the result
   */
  public CorsenResult getResult() {
    return result;
  }

  //
  // Setters
  //

  /**
   * Set the CorsenSwing result.
   * @param result The result to set
   */
  public void setResult(final CorsenResult result) {

    this.result = result;
    this.cacheImage.clear();
    this.cachePixmap.clear();
  }

  //
  // Other methods
  //

  /**
   * Get the number of views.
   * @return the number of views
   */
  public int getViewCount() {

    return VIEWS_COUNT;
  }

  /**
   * Get the description of a view.
   * @param index The index of the view
   * @return the description of a view
   */
  public String getViewDescription(final int index) {

    switch (index) {

    case 0:
      return DATA_SUMMARY_DESCRIPTION;
    case 1:
      return DATA_VIEW_DESCRIPTION;
    case 2:
      return IV_MESSENGERS_DESCRIPTION;
    case 3:
      return IV_MESSENGERS_CUBOIDS_DESCRIPTION;
    case 4:
      return IV_MITO_DESCRIPTION;
    case 5:
      return IV_MITO_CUBOIDS_DESCRIPTION;

    default:
      return null;
    }

  }

  /**
   * Get the description of a view.
   * @param index The index of the view
   * @return the description of a view
   */
  public String getViewDescription(final int index, final Settings settings) {

    String desc = getViewDescription(index);

    if (desc == null)
      return null;

    desc = desc.replaceAll("messengers", settings.getParticlesAName());
    desc = desc.replaceAll("mitochondria", settings.getParticlesBName());

    return desc;
  }

  /**
   * Get the model of a view
   * @param index The index of the view
   * @return The model of the view
   */
  public QAbstractItemModel getModel(final int index) {

    final CorsenResult r = getResult();
    if (r == null || r.getMinDistances() == null || r.getMaxDistances() == null)
      return null;

    final QAbstractItemModel model;

    switch (index) {

    case 0:
      model = new SummaryDataModel(r);
      break;

    case 1:
      model = new FullDataModel(r);
      break;

    case 2:
      model = new IVModel(r.getParticlesA());
      break;

    case 3:
      model = new IVModel(r.getCuboidsParticlesA());
      break;

    case 4:
      model = new IVModel(r.getParticlesB());
      break;

    case 5:
      model = new IVModel(r.getCuboidsParticlesB());
      break;

    default:
      model = null;
    }

    QSortFilterProxyModel sortModel = new QSortFilterProxyModel();
    sortModel.setSourceModel(model);

    return sortModel;
  }

  /**
   * Save DataDouble of a view.
   * @param index The index of the view
   * @param filename File to save
   * @throws IOException IOException if an error occurs while writing data
   */
  public void saveView(final int index, final String filename)
      throws IOException {

    saveView(index, new File(filename));
  }

  /**
   * Save DataDouble of a view.
   * @param index The index of the view
   * @param file File to save
   * @throws IOException IOException if an error occurs while writing data
   */
  public void saveView(final int index, final File file) throws IOException {

    saveView(index, new FileOutputStream(file));
  }

  /**
   * Save DataDouble of a view.
   * @param index The index of the view
   * @param os OutputStream to use
   * @throws IOException if an error occurs while writing data
   */
  public void saveView(final int index, final OutputStream os)
      throws IOException {

    final CorsenResult r = getResult();

    if (r == null)
      return;

    final CorsenResultWriter crw = new CorsenResultWriter(r);

    switch (index) {

    case 0:
      crw.writeSummaryResultFile(os);
      break;

    case 1:
      crw.writeDataFile(os);
      break;

    case 2:
      crw.writeMessengersIntensityVolume(os);
      break;

    case 3:
      crw.writeCuboidsMessengersIntensityVolume(os);
      break;

    case 4:
      crw.writeMitosIntensityVolume(os);
      break;

    case 5:
      crw.writeCuboidsMitosIntensityVolume(os);
      break;

    default:
      return;
    }

  }

  /**
   * Save particles in a file
   * @param index The index of the view
   * @param filename output filename
   * @throws IOException if an error occurs while writing data
   */
  public void saveParticlesView(final int index, final String filename)
      throws IOException {

    saveParticlesView(index, new File(filename));
  }

  /**
   * Save particles in a file
   * @param index The index of the view
   * @param file the output file
   * @throws IOException if an error occurs while writing data
   */
  public void saveParticlesView(final int index, final File file)
      throws IOException {

    saveParticlesView(index, new FileOutputStream(file));
  }

  /**
   * Save particles in a file
   * @param index The index of the view
   * @param outputStream the output stream
   * @throws IOException if an error occurs while writing data
   */
  public void saveParticlesView(final int index, final OutputStream outputStream)
      throws IOException {

    final CorsenResult r = getResult();

    Particles3D particlesToSave;

    switch (index) {

    case 2:
      particlesToSave = r.getParticlesA();
      break;

    case 3:
      particlesToSave = r.getCuboidsParticlesA();
      break;

    case 4:
      particlesToSave = r.getParticlesB();
      break;

    case 5:
      particlesToSave = r.getCuboidsParticlesB();
      break;

    default:
      particlesToSave = null;
    }

    if (particlesToSave != null)
      particlesToSave.saveParticles(outputStream);
  }

  public String getSaveFileExtension(final int index) {

    switch (index) {
    case 1:
      return Globals.EXTENSION_DATA_FILE;

    case 2:
    case 3:
    case 4:
    case 5:
      return Globals.EXTENSION_IV_FILE;

      // case 2:
      // return Globals.EXTENSION_PARTICLES_A_IV_FILE;
      //
      // case 3:
      // return Globals.EXTENSION_PARTICLES_A_CUBOIDS_IV_FILE;
      //
      // case 4:
      // return Globals.EXTENSION_PARTICLES_B_IV_FILE;

    default:
      return "";
    }

  }

  public QPixmap getResultPixmap(final int index, final Settings settings) {

    final QPixmap result;

    if (!this.cachePixmap.containsKey(index)) {

      QtUtil.CreateQImageThread cqpt = new QtUtil.CreateQImageThread() {

        @Override
        protected QImage createQImage() {

          return getImage(index, settings);
        }
      };

      Thread t = new Thread(cqpt);
      t.start();

      while (!cqpt.isEnd())
        QApplication.processEvents();

      result = QPixmap.fromImage(cqpt.getQImage());
      this.cachePixmap.put(index, result);
    } else
      result = this.cachePixmap.get(index);

    return result;
  }

  public QImage getImage(final int index, final Settings settings) {

    final CorsenResult r = getResult();

    if (r == null)
      return null;

    switch (index) {

    case 0:

      if (!this.cacheImage.containsKey(0)) {

        final QImage img =
            new ResultGraphs().createBoxPlot(r, settings.getUnit());

        if (img == null)
          return null;
        this.cacheImage.put(0, img);
      }

      return this.cacheImage.get(0);

    case 1:

      if (!this.cacheImage.containsKey(1)) {

        final QImage img =
            new ResultGraphs().createDistanceDistributionImage(r, settings
                .getHistogramResultsNumberClasses(), settings.getUnit());

        if (img == null)
          return null;
        QApplication.processEvents();
        cacheImage.put(1, img);
      }

      return this.cacheImage.get(1);

    case 2:

      if (!this.cacheImage.containsKey(2)) {

        final QImage img =
            new ResultGraphs().createScatterPlot(r.getParticlesA(), r
                .getParticlesA().getName()
                + " intensity/volume", settings.getUnit());

        if (img == null)
          return null;
        QApplication.processEvents();
        cacheImage.put(2, img);
      }

      return this.cacheImage.get(2);

    case 3:

      if (!this.cacheImage.containsKey(3)) {

        final QImage img =
            new ResultGraphs().createScatterPlot(r
                .getCuboidsParticlesA(), r.getParticlesA()
                .getName()
                + " cuboids intensity/volume", settings.getUnit());

        if (img == null)
          return null;
        QApplication.processEvents();
        cacheImage.put(3, img);
      }

      return this.cacheImage.get(3);

    case 4:

      if (!this.cacheImage.containsKey(4)) {

        final QImage img =
            new ResultGraphs().createScatterPlot(r.getParticlesB(), r
                .getParticlesB().getName()
                + " intensity/volume", settings.getUnit());

        if (img == null)
          return null;
        QApplication.processEvents();
        cacheImage.put(4, img);
      }

      return this.cacheImage.get(4);

    case 5:

      if (!this.cacheImage.containsKey(5)) {

        final QImage img =
            new ResultGraphs().createScatterPlot(r.getCuboidsParticlesB(),
                r.getParticlesB().getName() + " cuboids intensity/volume",
                settings.getUnit());

        if (img == null)
          return null;
        QApplication.processEvents();
        cacheImage.put(4, img);
      }

      return this.cacheImage.get(4);

    default:
      return null;

    }
  }

}
