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
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QSortFilterProxyModel;
import com.trolltech.qt.gui.QTableView;
import com.trolltech.qt.gui.QWidget;

import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults;

/**
 * This class extends a tableview to add contextual menu
 * @author Laurent Jourdren
 */
public class CorsenHistoryTableView extends QTableView {

  private int lastRowIndex;
  private QSortFilterProxyModel sorter;

  public void contextMenuEvent(QContextMenuEvent e) {

    QModelIndex index = indexAt(e.pos());

    if (index != null) {
      this.lastRowIndex = index.row();

      QMenu menu = new QMenu(this);
      menu.addAction("Redo calcultion for this result", this, "recalcResult()");
      menu.addAction("Delete this result", this, "deleteResult()");
      menu.addAction("Delete all results", this, "deleteAllResults()");
      menu.setEnabled(!CorsenQt.isCalculation());
      menu.exec(QCursor.pos());

    }
  }

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

  /**
   * Recalc a result.
   */
  @SuppressWarnings("unused")
  private void recalcResult() {

    int id = (Integer) this.sorter.data(this.lastRowIndex, 0);

    CorsenHistoryResults.Entry e =
        CorsenHistoryResults.getCorsenHistoryResults().getEntry(id);

    CorsenQt.launchAnalysis(e.getFileA().getAbsolutePath(), e.getFileB()
        .getAbsolutePath(), e.getResultsPath().getAbsolutePath());

  }

  /**
   * Remove a result.
   */
  @SuppressWarnings("unused")
  private void deleteResult() {

    int id = (Integer) this.sorter.data(this.lastRowIndex, 0);

    CorsenHistoryResults.getCorsenHistoryResults().removeEntry(id);
    CorsenQt.updateHistoryResults();
  }

  /**
   * Remove all results.
   */
  @SuppressWarnings("unused")
  private void deleteAllResults() {

    QMessageBox.StandardButton result =
        QMessageBox.warning(this, "Delete all results",
            "Are you sure to remove all results ?",
            new QMessageBox.StandardButtons(QMessageBox.StandardButton.Yes,
                QMessageBox.StandardButton.No), QMessageBox.StandardButton.No);

    if (result == QMessageBox.StandardButton.Yes) {
      System.out.println("clear all results");
      CorsenHistoryResults.getCorsenHistoryResults().clear();
      CorsenQt.updateHistoryResults();
    }
  }

  //
  // Constructors
  //

  public CorsenHistoryTableView() {

    this(null);
  }

  public CorsenHistoryTableView(final QWidget widget) {

    super(widget);
  }

}