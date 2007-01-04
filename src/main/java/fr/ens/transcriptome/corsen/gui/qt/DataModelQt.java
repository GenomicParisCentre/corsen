package fr.ens.transcriptome.corsen.gui.qt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QAbstractItemModel;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QAbstractTableModel;
import com.trolltech.qt.gui.QSortFilterProxyModel;

import fr.ens.transcriptome.corsen.CorsenResultWriter;
import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.calc.DistanceAnalyser;
import fr.ens.transcriptome.corsen.calc.Particles3D;
import fr.ens.transcriptome.corsen.model.Particle3D;

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

public class DataModelQt {

  private static final int VIEWS_COUNT = 5;
  private static final String DATA_SUMMARY_DESCRIPTION = "Data result summary";
  private static final String DATA_VIEW_DESCRIPTION = "Full data results";
  private static final String IV_MESSENGERS_DESCRIPTION = "Intensities and volumes of messengers";
  private static final String IV_MESSENGERS_CUBOIDS_DESCRIPTION = "Intensities and volumes of messengers cuboids";
  private static final String IV_CUBOIDS_DESCRIPTION = "Intensities and volumes of mitochondria ";

  private CorsenResult result;

  private class FullDataModel extends QAbstractTableModel {

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

      QVariant v = new QVariant();

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

  private class IVModel extends QAbstractTableModel {

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

  private class SummaryDataModel extends QAbstractTableModel {

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

  //
  // Getters
  //

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
      return IV_CUBOIDS_DESCRIPTION;

    default:
      return null;
    }

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
      model = new IVModel(r.getMessengersParticles());
      break;

    case 3:
      model = new IVModel(r.getCuboidsMessengersParticles());
      break;

    case 4:
      model = new IVModel(r.getMitosParticles());
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
   * @throws IOException
   */
  public void saveViewl(final int index, final String filename)
      throws IOException {

    saveViewl(index, new File(filename));
  }

  /**
   * Save DataDouble of a view.
   * @param index The index of the view
   * @param File File to save
   * @throws IOException
   */
  public void saveViewl(final int index, final File file) throws IOException {

    final CorsenResult r = getResult();

    if (r == null)
      return;

    final CorsenResultWriter crw = new CorsenResultWriter(r);

    switch (index) {
    case 1:
      crw.writeDataFile(file);
      break;

    case 2:
      crw.writeMessengersIntensityVolume(file);
      break;

    case 3:
      crw.writeCuboidsMessengersIntensityVolume(file);
      break;

    case 4:
      crw.writeMitosIntensityVolume(file);
      break;

    default:
      return;
    }

  }

  public String getSaveFileExtension(final int index) {

    switch (index) {
    case 1:
      return Globals.EXTENSION_DATA_FILE;

    case 2:
      return Globals.EXTENSION_PARTICLES_A_IV_FILE;

    case 3:
      return Globals.EXTENSION_PARTICLES_A_CUBOIDS_IV_FILE;

    case 4:
      return Globals.EXTENSION_PARTICLES_B_IV_FILE;

    default:
      return "";
    }

  }

}
