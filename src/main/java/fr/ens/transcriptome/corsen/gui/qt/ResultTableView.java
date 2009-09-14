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

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.gui.QSortFilterProxyModel;
import com.trolltech.qt.gui.QTableView;
import com.trolltech.qt.gui.QWidget;

public class ResultTableView extends QTableView {

  private QSortFilterProxyModel sorter;

  /**
   * Set the model of the tableView
   * @param model Model to set
   */
  void setModel(final DataModelQt.HistoryDataModel model) {

    this.sorter = new QSortFilterProxyModel() {

      @Override
      protected boolean lessThan(final QModelIndex a, final QModelIndex b) {

        if (a == null || b == null)
          return false;

        final int size = model.rowCount();

        if (a.row() >= size || b.row() >= size)
          return false;

        final Object oa = a.data();

        if (oa instanceof Number) {

          final Number na = (Number) oa;
          final Number nb = (Number) b.data();

          return na.doubleValue() < nb.doubleValue();
        }

        return oa.toString().compareTo(b.data().toString()) < 0;
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