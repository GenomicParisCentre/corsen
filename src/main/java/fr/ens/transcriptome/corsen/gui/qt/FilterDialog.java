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

package fr.ens.transcriptome.corsen.gui.qt;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QMessageBox;

import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.calc.JavascriptDistancesFilter;
import fr.ens.transcriptome.corsen.model.JavascriptParticles3DFilter;

/**
 * This class define a dialog box to configure viewer filters
 * @author Laurent Jourdren
 */
public class FilterDialog {

  private CorsenQt mainWindow;
  private Settings settings;

  public void show() {

    final Ui_FilterDialog dialogUi = new Ui_FilterDialog();
    final QDialog dialog = new QDialog(this.mainWindow);
    dialogUi.setupUi(dialog);

    final QObject o = new QObject() {

      public void enableParticlesAFilter() {

        dialogUi.particlesAFilterlabel.setEnabled(true);
        dialogUi.particlesAFilterlineEdit.setEnabled(true);
      }

      public void disableParticlesAFilter() {

        dialogUi.particlesAFilterlabel.setEnabled(false);
        dialogUi.particlesAFilterlineEdit.setEnabled(false);
      }

      @SuppressWarnings("unused")
      public void stateChangedParticlesAFilter(final int value) {

        if (value == 0)
          disableParticlesAFilter();
        else
          enableParticlesAFilter();
      }

      public void enableParticlesBFilter() {

        dialogUi.particlesBFilterlabel.setEnabled(true);
        dialogUi.particlesBFilterlineEdit.setEnabled(true);
      }

      public void disableParticlesBFilter() {

        dialogUi.particlesBFilterlabel.setEnabled(false);
        dialogUi.particlesBFilterlineEdit.setEnabled(false);
      }

      @SuppressWarnings("unused")
      public void stateChangedParticlesBFilter(final int value) {

        if (value == 0)
          disableParticlesBFilter();
        else
          enableParticlesBFilter();
      }

      public void enableDistancesFilter() {

        dialogUi.distancesFilterlabel.setEnabled(true);
        dialogUi.distancesFilterlineEdit.setEnabled(true);
      }

      public void disableDistancesFilter() {

        dialogUi.distancesFilterlabel.setEnabled(false);
        dialogUi.distancesFilterlineEdit.setEnabled(false);
      }

      @SuppressWarnings("unused")
      public void stateChangedDistancesFilter(final int value) {

        if (value == 0)
          disableDistancesFilter();
        else
          enableDistancesFilter();
      }

    };

    dialogUi.particlesAFilterCheckBox.stateChanged.connect(o,
        "stateChangedParticlesAFilter(int)");
    dialogUi.particlesBFilterCheckBox.stateChanged.connect(o,
        "stateChangedParticlesBFilter(int)");
    dialogUi.distancesFilterCheckBox.stateChanged.connect(o,
        "stateChangedDistancesFilter(int)");

    final Settings s = this.settings;

    dialogUi.particlesAFilterCheckBox.setChecked(true);
    // dialogUi.particlesAFilterCheckBox.setChecked(false);
    dialogUi.particlesAFilterCheckBox.setChecked(s
        .isParticlesAViewFilterEnabled());
    dialogUi.particlesAFilterlineEdit.setText(s
        .getParticlesAViewFilterExpression());

    dialogUi.particlesBFilterCheckBox.setChecked(true);
    // dialogUi.particlesAFilterCheckBox.setChecked(false);
    dialogUi.particlesBFilterCheckBox.setChecked(s
        .isParticlesBViewFilterEnabled());
    dialogUi.particlesBFilterlineEdit.setText(s
        .getParticlesBViewFilterExpression());

    dialogUi.distancesFilterCheckBox.setChecked(true);
    // dialogUi.particlesAFilterCheckBox.setChecked(false);
    dialogUi.distancesFilterCheckBox.setChecked(s
        .isDistancesViewFilterEnabled());
    dialogUi.distancesFilterlineEdit.setText(s
        .getDistancesViewFilterExpression());

    dialogUi.onlyDistanceOfShowedParticlesCheckBox.setChecked(s
        .isFilterShowDistanceShowedParticles());

    if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {

      // Particles A filter
      s.setEnableParticlesAViewerFilter(dialogUi.particlesAFilterCheckBox
          .isChecked());
      final String expA = dialogUi.particlesAFilterlineEdit.text();
      if (JavascriptParticles3DFilter.createFilter(expA) != null)
        s.setExpressionParticlesAViewerFilter(expA);
      else
        QMessageBox.warning(this.mainWindow, Globals.APP_NAME,
            "Warning: Your filter for particles A is not valid.");

      // Particles B filter
      s.setEnableParticlesBViewerFilter(dialogUi.particlesBFilterCheckBox
          .isChecked());
      final String expB = dialogUi.particlesBFilterlineEdit.text();
      if (JavascriptParticles3DFilter.createFilter(expB) != null)
        s.setExpressionParticlesBViewerFilter(expB);
      else
        QMessageBox.warning(this.mainWindow, Globals.APP_NAME,
            "Warning: Your filter for particles B is not valid.");

      // Distances filter
      s.setEnableDistancesViewerFilter(dialogUi.distancesFilterCheckBox
          .isChecked());
      final String expD = dialogUi.distancesFilterlineEdit.text();
      if (JavascriptDistancesFilter.createFilter(expD) != null)
        s.setExpressionDistancesViewerFilter(expD);
      else
        QMessageBox.warning(this.mainWindow, Globals.APP_NAME,
            "Warning: Your filter for distances is not valid.");

      s
          .setFilterShowDistanceShowedParticles(dialogUi.onlyDistanceOfShowedParticlesCheckBox
              .isChecked());

    }

  }

  //
  // Constructor
  //

  /**
   * public constructor.
   */
  FilterDialog(final CorsenQt mainWindow, final Settings settings) {

    this.mainWindow = mainWindow;
    this.settings = settings;

  }

}
