import java.awt.Color;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QColorDialog;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QPixmap;

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

public class CorsenConfigureQt {

  private QMainWindow mainWindow;
  private Settings settings;

  private QColor messengersColor;
  private QColor mitosColor;
  private QColor baryCentersColor;
  private QColor distancesColor;
  private QColor backgroundColor;
  private QColor legendColor;

  @SuppressWarnings("unused")
  public void configureDialog() {
    // Make the dialog.

    final Ui_ConfigureDialog dialogUi = new Ui_ConfigureDialog();
    QDialog dialog = new QDialog(this.mainWindow);
    dialogUi.setupUi(dialog);

    final QObject o = new QObject() {
      public void enableCustomCuboidSize() {
        dialogUi.cuboidSizeValueLabel.setEnabled(true);
        dialogUi.cuboidSizeLineEdit.setEnabled(true);
      }

      public void disableCustomCuboidSize() {
        dialogUi.cuboidSizeValueLabel.setEnabled(false);
        dialogUi.cuboidSizeLineEdit.setEnabled(false);
      }

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

        QColor c = QColorDialog
            .getColor(CorsenConfigureQt.this.messengersColor);

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

        QColor c = QColorDialog
            .getColor(CorsenConfigureQt.this.baryCentersColor);

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

        QColor c = QColorDialog
            .getColor(CorsenConfigureQt.this.backgroundColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.backgroundColor = c;
          dialogUi.backgroundColorPushButton.setIcon(createIcon(c));
        }
      }
      
      public void changeLegendColor() {

        QColor c = QColorDialog
            .getColor(CorsenConfigureQt.this.legendColor);

        if (c.isValid()) {
          CorsenConfigureQt.this.legendColor = c;
          dialogUi.legendColorPushButton.setIcon(createIcon(c));
        }
      }

    };

    dialogUi.customRadioButton.toggled.connect(o, "enableCustomCuboidSize()");
    dialogUi.automaticRadioButton.toggled.connect(o,
        "disableCustomCuboidSize()");

    dialogUi.visualizationCheckBox.stateChanged.connect(o,
        "stateChanged3DVisualisation(int)");

    dialogUi.visualizationCheckBox.setChecked(true);
    dialogUi.visualizationCheckBox.setChecked(false);

    final Settings s = this.settings;

    dialogUi.zFactorLineEdit.setText("" + s.getZFactor());
    dialogUi.factorLineEdit.setText("" + s.getFactor());

    if (s.getUnit() != null)
      dialogUi.unitLineEdit.setText(s.getUnit().trim());

    if (s.isAutoCuboidSize())
      dialogUi.automaticRadioButton.setChecked(true);
    else
      dialogUi.customRadioButton.setChecked(true);

    dialogUi.cuboidSizeLineEdit.setText("" + s.getCuboidSize());

    dialogUi.dataFileCheckBox.setChecked(s.isSaveDataFile());
    dialogUi.ivFileCheckBox.setChecked(s.isSaveIVFile());
    dialogUi.fullResultCheckBox.setChecked(s.isSaveFullResultsFile());
    dialogUi.visualizationCheckBox.setChecked(s.isSaveVisualizationFiles());
    dialogUi.messengersCheckBox.setChecked(s.isSaveMessengers3dFile());
    dialogUi.messengersCuboidsCheckBox.setChecked(s
        .isSaveMessengersCuboids3dFile());
    dialogUi.mitosCheckBox.setChecked(s.isSaveMito3dFile());
    dialogUi.mitosCuboidsCheckBox.setChecked(s.isSaveMitoCuboids3dFile());
    dialogUi.distancesCheckBox.setChecked(s.isSaveDistances3dFile());

    // Second tab

    dialogUi.messengersColorPushButton.clicked.connect(o,
        "changeMessengersColor()");
    dialogUi.mitosColorPushButton.clicked.connect(o, "changeMitosColor()");
    dialogUi.barycentersColorPushButton.clicked.connect(o,
        "changeBaryCentersColor()");
    dialogUi.distancesColorPushButton.clicked.connect(o,
        "changeDistancesColor()");
    dialogUi.backgroundColorPushButton.clicked.connect(o,
        "changeBackgroundColor()");
    dialogUi.legendColorPushButton.clicked.connect(o,
    "changeLegendColor()");
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

    this.messengersColor = colorToQColor(s.getColorMessengers());
    this.mitosColor = colorToQColor(s.getColorMitos());
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
    dialogUi.legendColorPushButton
    .setIcon(createIcon(this.legendColor));

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

      s.setAutoCuboidSize(dialogUi.automaticRadioButton.isChecked());

      try {
        s.setCuboidSize(Float.parseFloat(dialogUi.cuboidSizeLineEdit.text()
            .trim()));
      } catch (NumberFormatException e) {
      }

      s.setSaveDataFile(dialogUi.dataFileCheckBox.isChecked());
      s.setSaveIVFile(dialogUi.ivFileCheckBox.isChecked());
      s.setSaveFullResultFile(dialogUi.fullResultCheckBox.isChecked());
      s.setSaveVisualizationFiles(dialogUi.visualizationCheckBox.isChecked());
      s.setSaveMessengers3dFile(dialogUi.messengersCheckBox.isChecked());
      s.setSaveMessengersCuboids3dFile(dialogUi.messengersCuboidsCheckBox
          .isChecked());
      s.setSaveMito3dFile(dialogUi.mitosCheckBox.isChecked());
      s.setSaveMitoCuboids3dFile(dialogUi.mitosCuboidsCheckBox.isChecked());
      s.setSaveDistances3dFile(dialogUi.distancesCheckBox.isChecked());

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

      s.setColorMessengers(qColorToColor(this.messengersColor));
      s.setColorMitos(qColorToColor(this.mitosColor));
      s.setColorBaryCenters(qColorToColor(this.baryCentersColor));
      s.setColorDistances(qColorToColor(this.distancesColor));
      s.setColorBackground(qColorToColor(this.backgroundColor));
      s.setColorLegend(qColorToColor(this.legendColor));
      
     

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
  CorsenConfigureQt(final QMainWindow mainWindow, final Settings settings) {

    this.mainWindow = mainWindow;
    this.settings = settings;
  }

}
