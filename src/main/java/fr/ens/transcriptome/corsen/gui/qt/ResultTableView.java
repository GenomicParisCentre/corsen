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

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.gui.QSortFilterProxyModel;
import com.trolltech.qt.gui.QTableView;
import com.trolltech.qt.gui.QWidget;

import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults;

public class ResultTableView extends QTableView {


  private QSortFilterProxyModel sorter;

  

  /**
   * Set the model of the tableView
   * @param model Model to set
   */
  void setModel(final DataModelQt.HistoryDataModel model) {

    this.sorter = new QSortFilterProxyModel() {

      @Override
      protected boolean lessThan(QModelIndex a, QModelIndex b) {

        if (a == null || b == null)
          return false;

        final int size = CorsenHistoryResults.getCorsenHistoryResults().size();

        if (a.row() >= size || b.row() >= size)
          return false;

        Object oa = a.data();

        if (oa instanceof Number) {

          Number na = (Number) oa;
          Number nb = (Number) b.data();

          return na.doubleValue() > nb.doubleValue();
        }

        return oa.toString().compareTo(b.data().toString()) > 0;
      }

    };

    this.sorter.setSourceModel(model);
    super.setModel(this.sorter);
  }

 

  //
  // Constructors
  //

  public ResultTableView() {

    this(null);
  }

  public ResultTableView(final QWidget widget) {

    super(widget);
  }

}