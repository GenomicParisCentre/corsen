package fr.ens.transcriptome.corsen.gui.qt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QAbstractItemModel;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.DropActions;
import com.trolltech.qt.core.Qt.ItemFlags;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.core.Qt.SortOrder;
import com.trolltech.qt.gui.QAbstractProxyModel;
import com.trolltech.qt.gui.QAbstractTableModel;
import com.trolltech.qt.gui.QSortFilterProxyModel;
import com.trolltech.qt.gui.QStandardItem;
import com.trolltech.qt.gui.QStandardItemModel;
import com.trolltech.qt.gui.QStringListModel;

import fr.ens.transcriptome.corsen.CorsenCore;
import fr.ens.transcriptome.corsen.CorsenResult;
import fr.ens.transcriptome.corsen.CorsenResultWriter;
import fr.ens.transcriptome.corsen.Distance;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;

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

public class DataModelQt {

  private static final int VIEWS_COUNT = 4;
  private static final String DATA_VIEW_DESCRIPTION = "Data results";
  private static final String IV_MESSENGERS_DESCRIPTION = "Intensities and volumes of messengers";
  private static final String IV_MESSENGERS_CUBOIDS_DESCRIPTION = "Intensities and volumes of messengers cuboids";
  private static final String IV_CUBOIDS_DESCRIPTION = "Intensities and volumes of mitochondria ";

  private CorsenResult result;

  private class DataModel extends QAbstractTableModel {

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

    public DataModel(final CorsenResult r) {

      if (r == null)
        return;
      mins = r.getMinDistances();
      maxs = r.getMaxDistances();
      listParticles = new ArrayList<Particle3D>(mins.keySet());

      nRows = mins.size();
    }

  }

  private class IVModel extends QAbstractTableModel {

    private Particle3D[] pars;

    @Override
    public int columnCount(QModelIndex arg0) {

      return 3;
    }

    @Override
    public Object data(final QModelIndex mIndex, final int role) {

      if (mIndex == null || role != Qt.ItemDataRole.DisplayRole)
        return null;

      Particle3D p = this.pars[mIndex.row()];
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

      return this.pars == null ? 0 : pars.length;
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
   * Set the Corsen result.
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
      return DATA_VIEW_DESCRIPTION;
    case 1:
      return IV_MESSENGERS_DESCRIPTION;
    case 2:
      return IV_MESSENGERS_CUBOIDS_DESCRIPTION;
    case 3:
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

    final QAbstractItemModel model;

    switch (index) {
    case 0:
      model = new DataModel(r);
      break;

    case 1:
      model = new IVModel(r.getMessengersParticles());
      break;

    case 2:
      model = new IVModel(r.getCuboidsMessengersParticles());
      break;

    case 3:
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
   * Save Data of a view.
   * @param index The index of the view
   * @param filename File to save
   * @throws IOException
   */
  public void saveViewl(final int index, final String filename)
      throws IOException {

    saveViewl(index, new File(filename));
  }

  /**
   * Save Data of a view.
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
    case 0:

      crw.writeDataFile(file);
      break;

    case 1:

      crw.writeMessengersIntensityVolume(file);
      break;

    case 2:
      crw.writeCuboidsMessengersIntensityVolume(file);
      break;

    case 3:
      crw.writeMitosIntensityVolume(file);
      break;

    default:
      return;
    }

  }

  public String getSaveFileExtension(final int index) {

    switch (index) {
    case 0:
      return CorsenCore.EXTENSION_DATA_FILE;

    case 1:

      return CorsenCore.EXTENSION_MESSENGERS_IV_FILE;

    case 2:
      return CorsenCore.EXTENSION_CUBOIDS_IV_FILE;

    case 3:
      return CorsenCore.EXTENSION_MITOS_IV_FILE;

    default:
      return "";
    }

  }

}
