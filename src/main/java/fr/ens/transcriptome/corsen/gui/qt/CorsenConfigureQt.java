package fr.ens.transcriptome.corsen.gui.qt;

import java.awt.Color;
import java.util.Properties;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QColorDialog;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;

import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults;
import fr.ens.transcriptome.corsen.calc.ParticleType;
import fr.ens.transcriptome.corsen.model.JavascriptParticles3DFilter;

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

public class CorsenConfigureQt {

  private CorsenQt mainWindow;
  private Settings settings;

  private QColor messengersColor;
  private QColor mitosColor;
  private QColor baryCentersColor;
  private QColor distancesColor;
  private QColor backgroundColor;
  private QColor legendColor;
  private Properties particlesAProporties;
  private Properties particlesBProporties;

  @SuppressWarnings("unused")
  public void configureDialog() {
    // Make the dialog.

    final Ui_ConfigureDialog dialogUi = new Ui_ConfigureDialog();
    final QDialog dialog = new QDialog(this.mainWindow);
    dialogUi.setupUi(dialog);

    final QObject o = new QObject() {

      public void enable3DVisalisation() {

        dialogUi.messengersCheckBox.setEnabled(true);
        dialogUi.messengersCuboidsCheckBox.setEnabled(true);
        dialogUi.distancesCheckBox.setEnabled(true);
        dialogUi.mitosCheckBox.setEnabled(true);
        dialogUi.mitosCuboidsCheckBox.setEnabled(true);
      }

      public void disable3DVisalisation() {

        dialogUi.messengersCheckBox.setEnabled(false);
        dialogUi.messengersCuboidsCheckBox.setEnabled(false);
        dialogUi.distancesCheckBox.setEnabled(false);
        dialogUi.mitosCheckBox.setEnabled(false);
        dialogUi.mitosCuboidsCheckBox.setEnabled(false);
      }

      public void stateChanged3DVisualisation(final int value) {

        if (value == 0)
          disable3DVisalisation();
        else
          enable3DVisalisation();
      }

      public void stateChangedVisualisationShowSurfaceLines(final int value) {

        if (value == 0)
          dialogUi.surfaceLineSizeLineEdit.setEnabled(false);
        else
          dialogUi.surfaceLineSizeLineEdit.setEnabled(true);
      }

      public void changeMessengersColor() {

        QColor c =
            QColorDialog.getColor(CorsenConfigureQt.this.messengersColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.messengersColor = c;
          dialogUi.messengersColorPushButton.setIcon(createIcon(c));
        }
      }

      public void changeMitosColor() {

        QColor c = QColorDialog.getColor(CorsenConfigureQt.this.mitosColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.mitosColor = c;
          dialogUi.mitosColorPushButton.setIcon(createIcon(c));
        }
      }

      public void changeBaryCentersColor() {

        QColor c =
            QColorDialog.getColor(CorsenConfigureQt.this.baryCentersColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.baryCentersColor = c;
          dialogUi.barycentersColorPushButton.setIcon(createIcon(c));
        }
      }

      public void changeDistancesColor() {

        QColor c = QColorDialog.getColor(CorsenConfigureQt.this.distancesColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.distancesColor = c;
          dialogUi.distancesColorPushButton.setIcon(createIcon(c));
        }
      }

      public void changeBackgroundColor() {

        QColor c =
            QColorDialog.getColor(CorsenConfigureQt.this.backgroundColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.backgroundColor = c;
          dialogUi.backgroundColorPushButton.setIcon(createIcon(c));
        }
      }

      public void changeLegendColor() {

        QColor c = QColorDialog.getColor(CorsenConfigureQt.this.legendColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.legendColor = c;
          dialogUi.legendColorPushButton.setIcon(createIcon(c));
        }
      }

      public void enableCustomThread() {

        dialogUi.customThreadSpinBox.setEnabled(true);
      }

      public void disableCustomThread() {

        dialogUi.customThreadSpinBox.setEnabled(false);
      }

      public void dialogParticleAProperties() {

        ParticleType type =
            ParticleType.getParticleType(dialogUi.particleATypeComboBox
                .currentText());

        ParticleTypeProperties ptp =
            new ParticleTypeProperties(dialog, type, particlesAProporties);
        ptp.configureDialog();
        particlesAProporties = ptp.getProperties();
      }

      public void dialogParticleBProperties() {

        ParticleType type =
            ParticleType.getParticleType(dialogUi.particleBTypeComboBox
                .currentText());

        ParticleTypeProperties ptp =
            new ParticleTypeProperties(dialog, type, particlesBProporties);
        ptp.configureDialog();
        particlesBProporties = ptp.getProperties();
      }

    };

    final Settings s = this.settings;

    ParticleType[] particleTypes = ParticleType.values();

    for (int i = 0; i < particleTypes.length; i++) {
      dialogUi.particleATypeComboBox.addItem(particleTypes[i].toString());
      dialogUi.particleBTypeComboBox.addItem(particleTypes[i].toString());
    }

    dialogUi.particleANameLineEdit.setText(s.getParticlesAName());
    dialogUi.particleATypeComboBox.setCurrentIndex(s.getParticlesAType()
        .ordinal());
    dialogUi.particleABatchPrefixLineEdit.setText(s.getParticlesABatchPrefix());
    dialogUi.particleAFilterLineEdit.setText(s.getParticlesAFilterExpression());
    dialogUi.particleAPropertiesPushButton.clicked.connect(o,
        "dialogParticleAProperties()");

    dialogUi.particleBNameLineEdit.setText(s.getParticlesBName());
    dialogUi.particleBTypeComboBox.setCurrentIndex(s.getParticlesBType()
        .ordinal());
    dialogUi.particleBBatchPrefixLineEdit.setText(s.getParticlesBBatchPrefix());
    dialogUi.particleBFilterLineEdit.setText(s.getParticlesBFilterExpression());
    dialogUi.particleBPropertiesPushButton.clicked.connect(o,
        "dialogParticleBProperties()");

    dialogUi.zFactorLineEdit.setText("" + s.getZFactor());
    dialogUi.factorLineEdit.setText("" + s.getFactor());

    if (s.getUnit() != null)
      dialogUi.unitLineEdit.setText(s.getUnit().trim());

    // Second tab

    dialogUi.customThreadRadioButton.toggled.connect(o, "enableCustomThread()");
    dialogUi.automaticThreadRadioButton.toggled.connect(o,
        "disableCustomThread()");
    dialogUi.noThreadRadioButton.toggled.connect(o, "disableCustomThread()");

    dialogUi.customThreadSpinBox.setEnabled(false);

    switch (s.getThreadNumber()) {
    case -1:
      dialogUi.noThreadRadioButton.setChecked(true);
      break;

    case 0:
      dialogUi.automaticThreadRadioButton.setChecked(true);
      break;

    default:
      int i = s.getThreadNumber();

      if (i > 0) {
        dialogUi.customThreadRadioButton.setChecked(true);
        dialogUi.customThreadSpinBox.setValue(i);
      } else
        dialogUi.automaticThreadRadioButton.setChecked(true);

      break;
    }

    dialogUi.visualizationCheckBox.stateChanged.connect(o,
        "stateChanged3DVisualisation(int)");

    dialogUi.visualizationCheckBox.setChecked(true);
    dialogUi.visualizationCheckBox.setChecked(false);

    dialogUi.dataFileCheckBox.setChecked(s.isSaveDataFile());
    dialogUi.ivFileCheckBox.setChecked(s.isSaveIVFile());
    dialogUi.resultCheckBox.setChecked(s.isSaveResultsFile());
    dialogUi.fullResultCheckBox.setChecked(s.isSaveFullResultsFile());
    dialogUi.visualizationCheckBox.setChecked(s.isSaveVisualizationFiles());
    dialogUi.messengersCheckBox.setChecked(s.isSaveParticleA3dFile());
    dialogUi.messengersCuboidsCheckBox.setChecked(s
        .isSaveParticlesACuboids3dFile());
    dialogUi.mitosCheckBox.setChecked(s.isSaveParticleB3dFile());
    dialogUi.mitosCuboidsCheckBox.setChecked(s.isSaveParticlesBCuboids3dFile());
    dialogUi.distancesCheckBox.setChecked(s.isSaveDistances3dFile());

    dialogUi.clearHistoryCheckBox.setChecked(s
        .isClearHistoryWhenLaunchingNewCalc());

    // Third tab

    dialogUi.messengersColorPushButton.clicked.connect(o,
        "changeMessengersColor()");
    dialogUi.mitosColorPushButton.clicked.connect(o, "changeMitosColor()");
    dialogUi.barycentersColorPushButton.clicked.connect(o,
        "changeBaryCentersColor()");
    dialogUi.distancesColorPushButton.clicked.connect(o,
        "changeDistancesColor()");
    dialogUi.backgroundColorPushButton.clicked.connect(o,
        "changeBackgroundColor()");
    dialogUi.legendColorPushButton.clicked.connect(o, "changeLegendColor()");
    dialogUi.showSurfaceLinesCheckBox.stateChanged.connect(o,
        "stateChangedVisualisationShowSurfaceLines(int)");

    dialogUi.showSurfaceLinesCheckBox.setChecked(false);
    dialogUi.showSurfaceLinesCheckBox.setChecked(true);

    dialogUi.pointSizeLineEdit.setText("" + s.getVisualizationPointsSize());
    dialogUi.showSurfaceLinesCheckBox.setChecked(s
        .isVisualizationShowSurfaceLines());
    dialogUi.surfaceLineSizeLineEdit.setText(""
        + s.getVisualizationSurfaceLinesSize());
    dialogUi.distanceLineSizelineEdit.setText(""
        + s.getVisualizationDistancesLinesSize());
    dialogUi.showDistancesNegativeCheckBox.setChecked(s
        .isVisualizationShowNegativeDistances());
    dialogUi.showParticlesADifferentColorCcheckBox.setChecked(s
        .isVisualisationParticlesAInDifferentsColor());
    dialogUi.showParticlesBDifferentColorCcheckBox.setChecked(s
        .isVisualisationParticlesBInDifferentsColor());

    this.messengersColor = colorToQColor(s.getColorParticlesA());
    this.mitosColor = colorToQColor(s.getColorParticlesB());
    this.distancesColor = colorToQColor(s.getColorDistances());
    this.baryCentersColor = colorToQColor(s.getColorBaryCenters());
    this.backgroundColor = colorToQColor(s.getColorBackground());
    this.legendColor = colorToQColor(s.getColorLegend());

    dialogUi.messengersColorPushButton
        .setIcon(createIcon(this.messengersColor));
    dialogUi.mitosColorPushButton.setIcon(createIcon(this.mitosColor));
    dialogUi.distancesColorPushButton.setIcon(createIcon(this.distancesColor));
    dialogUi.barycentersColorPushButton
        .setIcon(createIcon(this.baryCentersColor));
    dialogUi.backgroundColorPushButton
        .setIcon(createIcon(this.backgroundColor));
    dialogUi.legendColorPushButton.setIcon(createIcon(this.legendColor));

    // Fourth tab

    dialogUi.histoResultClassesNumberSpinBox.setValue(s
        .getHistogramResultsNumberClasses());
    dialogUi.histoHistoryClassesNumberSpinBox.setValue(s
        .getHistogramHistoryNumberClasses());
    dialogUi.customExpressionLineEdit.setText(s.getCustomHistoryExpression());

    // Validation

    if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {

      try {
        s.setZFactor(Float.parseFloat(dialogUi.zFactorLineEdit.text().trim()));
      } catch (NumberFormatException e) {
      }
      try {
        s.setFactor(Float.parseFloat(dialogUi.factorLineEdit.text().trim()));
      } catch (NumberFormatException e) {
      }

      String unitValue;

      if (dialogUi.unitLineEdit.text() != null
          && !((unitValue = dialogUi.unitLineEdit.text().trim()).equals("")))
        s.setUnit(unitValue);

      s.setParticlesAName(dialogUi.particleANameLineEdit.text());
      s.setParticlesAType(ParticleType
          .getParticleType(dialogUi.particleATypeComboBox.currentText()));
      s.setParticlesABatchPrefix(dialogUi.particleABatchPrefixLineEdit.text());
      s.setParticlesAProperties(this.particlesAProporties);

      s.setParticlesBName(dialogUi.particleBNameLineEdit.text());
      s.setParticlesBType(ParticleType
          .getParticleType(dialogUi.particleBTypeComboBox.currentText()));
      s.setParticlesBBatchPrefix(dialogUi.particleBBatchPrefixLineEdit.text());
      s.setParticlesBProperties(this.particlesBProporties);

      if (dialogUi.noThreadRadioButton.isChecked())
        s.setThreadNumber(-1);
      else if (dialogUi.automaticThreadRadioButton.isChecked())
        s.setThreadNumber(0);
      else
        s.setThreadNumber(dialogUi.customThreadSpinBox.value());

      s.setSaveDataFile(dialogUi.dataFileCheckBox.isChecked());
      s.setSaveIVFile(dialogUi.ivFileCheckBox.isChecked());
      s.setSaveFullResultFile(dialogUi.fullResultCheckBox.isChecked());
      s.setSaveResultFile(dialogUi.resultCheckBox.isChecked());
      s.setSaveVisualizationFiles(dialogUi.visualizationCheckBox.isChecked());
      s.setSaveParticlesA3dFile(dialogUi.messengersCheckBox.isChecked());
      s.setSaveParticlesACuboids3dFile(dialogUi.messengersCuboidsCheckBox
          .isChecked());
      s.setSaveParticlesB3dFile(dialogUi.mitosCheckBox.isChecked());
      s.setSaveParticlesBCuboids3dFile(dialogUi.mitosCuboidsCheckBox
          .isChecked());
      s.setSaveDistances3dFile(dialogUi.distancesCheckBox.isChecked());

      s.setClearHistoryWhenLaunchNewCalc(dialogUi.clearHistoryCheckBox
          .isChecked());

      try {
        s.setVisualizationPointsSize(Float
            .parseFloat(dialogUi.pointSizeLineEdit.text()));
      } catch (NumberFormatException e) {
      }

      s.setVisualizationShowSurfaceLines(dialogUi.showSurfaceLinesCheckBox
          .isChecked());

      try {
        s.setVisualizationSurfaceLinesSize(Float
            .parseFloat(dialogUi.surfaceLineSizeLineEdit.text()));
      } catch (NumberFormatException e) {
      }

      try {
        s.setVisualizationDistancesLinesSize(Float
            .parseFloat(dialogUi.distanceLineSizelineEdit.text()));
      } catch (NumberFormatException e) {
      }

      s
          .setVisualizationShowNegativeDistances(dialogUi.showDistancesNegativeCheckBox
              .isChecked());
      s
          .setVisualisationParticlesAInDifferentsColors(dialogUi.showParticlesADifferentColorCcheckBox
              .isChecked());
      s
          .setVisualisationParticlesBInDifferentsColors(dialogUi.showParticlesBDifferentColorCcheckBox
              .isChecked());

      s.setColorParticlesA(qColorToColor(this.messengersColor));
      s.setColorParticlesB(qColorToColor(this.mitosColor));
      s.setColorBaryCenters(qColorToColor(this.baryCentersColor));
      s.setColorDistances(qColorToColor(this.distancesColor));
      s.setColorBackground(qColorToColor(this.backgroundColor));
      s.setColorLegend(qColorToColor(this.legendColor));

      final int histoResultClassesNumber =
          dialogUi.histoResultClassesNumberSpinBox.value();

      final int histoHistoryClassesNumber =
          dialogUi.histoHistoryClassesNumberSpinBox.value();

      if (histoResultClassesNumber != s.getHistogramResultsNumberClasses()) {
        s.setHistogramResultsNumberClasses(histoResultClassesNumber);
        this.mainWindow.redrawResultGraph();
      }

      if (histoHistoryClassesNumber != s.getHistogramHistoryNumberClasses()) {
        s.setHistogramHistoryNumberClasses(histoHistoryClassesNumber);
        this.mainWindow.redrawHistoryGraph();
      }

      String customExpression = dialogUi.customExpressionLineEdit.text();
      if (!CorsenHistoryResults.getCorsenHistoryResults().setCustomExpression(
          customExpression))
        QMessageBox.warning(this.mainWindow, Globals.APP_NAME,
            "Warning: Your custom expression is not valid.");
      else
        s.setCustomHistoryExpression(customExpression);

      String particleAFilter = dialogUi.particleAFilterLineEdit.text();
      if (JavascriptParticles3DFilter.createFilter(particleAFilter) != null)
        s.setParticlesAFilterExpression(particleAFilter);
      else
        QMessageBox.warning(this.mainWindow, Globals.APP_NAME,
            "Warning: Your filter for particles A is not valid.");

      String particleBFilter = dialogUi.particleBFilterLineEdit.text();
      if (JavascriptParticles3DFilter.createFilter(particleBFilter) != null)
        s.setParticlesBFilterExpression(particleBFilter);
      else
        QMessageBox.warning(this.mainWindow, Globals.APP_NAME,
            "Warning: Your filter for particles B is not valid.");

    }

  }

  //
  // Utility methods
  //

  private QColor colorToQColor(final Color color) {

    if (color == null)
      return null;

    return QColor.fromRgb(color.getRed(), color.getGreen(), color.getBlue(),
        color.getAlpha());
  }

  private Color qColorToColor(final QColor color) {

    if (color == null)
      return null;

    return new Color(color.red(), color.green(), color.blue(), color.alpha());
  }

  @SuppressWarnings("unused")
  private QIcon createIcon(final Color color) {

    if (color == null)
      return null;

    return createIcon(colorToQColor(color));
  }

  private QIcon createIcon(final QColor color) {

    if (color == null)
      return null;

    QPixmap pixmap = new QPixmap(10, 10);
    pixmap.fill(color);

    return new QIcon(pixmap);
  }

  //
  // Constructor
  //

  /**
   * public constructor.
   */
  CorsenConfigureQt(final CorsenQt mainWindow, final Settings settings) {

    this.mainWindow = mainWindow;
    this.settings = settings;
    this.particlesAProporties = settings.getParticlesAProperties();
    this.particlesBProporties = settings.getParticlesBProperties();
  }

}
