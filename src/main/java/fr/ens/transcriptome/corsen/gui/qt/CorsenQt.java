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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import com.trolltech.qt.QThread;
import com.trolltech.qt.QtInfo;
import com.trolltech.qt.core.QLocale;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt.ConnectionType;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QFileDialog.FileMode;

import fr.ens.transcriptome.corsen.Corsen;
import fr.ens.transcriptome.corsen.CorsenCore;
import fr.ens.transcriptome.corsen.CorsenResultWriter;
import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.DistancesCalculator;
import fr.ens.transcriptome.corsen.gui.qt.DataModelQt.HistoryDataModel;
import fr.ens.transcriptome.corsen.util.Util;

/**
 * This class implements the Qt GUI interface for Corsen.
 * @author Laurent Jourdren
 */
// TODO add an in phase progress for writing/computing distances
// TODO Remove the cancel button
// TODO When alt-f4 -> System.exit(0)
public class CorsenQt extends QMainWindow {

  private static Logger logger = Logger.getLogger(Globals.APP_NAME);

  private static final int PATH_STRING_MAX_LEN = 50;

  private static CorsenQt mainw;
  private Ui_CorsenMainWindow mainWindowUi = new Ui_CorsenMainWindow();

  private DataModelQt models = new DataModelQt();
  private final StatusInfo status = new StatusInfo();
  private Settings settings = new Settings();

  private String lastDir =
      Globals.DEBUG_HOME_DIR ? "/home/jourdren/Desktop/atp16" : "";

  private String particlesAPath = "";
  private String particlesBPath = "";
  private String directoryPath = "";

  private boolean refreshHistoryGraphics;

  /**
   * Class used for updating information about the running process.
   * @author Laurent Jourdren
   */
  private class UpdateStatusQt extends QObject implements UpdateStatus {

    public QObject.Signal1<ProgressEvent> statusSignal =
        new QObject.Signal1<ProgressEvent>();
    public QObject.Signal1<String> messageSignal =
        new QObject.Signal1<String>();
    public QObject.Signal1<String> errorSignal = new QObject.Signal1<String>();
    public QObject.Signal1<CorsenResult> resultSignal =
        new QObject.Signal1<CorsenResult>();

    public void showError(String msg) {
      this.errorSignal.emit(msg);
    }

    public void showMessage(String msg) {
      this.messageSignal.emit(msg);
    }

    public void updateStatus(ProgressEvent e) {
      this.statusSignal.emit(e);
    }

    public void endProcess(CorsenResult result) {
      this.resultSignal.emit(result);
    }

    /**
     * Create a new Thread.
     * @param runnable Runnable Object for the thread
     * @return a new Thread
     */
    public Thread newThread(final Runnable runnable) {

      return new QThread(runnable);
    }

    /**
     * Chain the update status for the differents threads. Needed by Qt.
     * @return an UpdateStatus instance
     */
    public UpdateStatus chain() {

      UpdateStatusQt result = new UpdateStatusQt();
      result.errorSignal.connect(this, "showError(String)",
          ConnectionType.QueuedConnection);
      result.messageSignal.connect(this, "showMessage(String)",
          ConnectionType.QueuedConnection);
      result.statusSignal.connect(this, "updateStatus(ProgressEvent)",
          ConnectionType.QueuedConnection);
      result.resultSignal.connect(this, "endProcess(CorsenResult)",
          ConnectionType.QueuedConnection);

      return result;
    }

  }

  /**
   * Define a class that contains informations about the running process.
   * @author Laurent Jourdren
   */
  private static final class StatusInfo {

    int currentCellToProcess;
    int cellToProcessCount;
    ProgressEventType type;
    int currentPhase;
    int nbPhaseDone;
    int maxPhase;
    int indexInPhase;
    long timeStartCells;
    long timeStartCell;
    String particlesAFilePath;
    String particlesBFilePath;
    String resultFilePath;
  }

  //
  // Qt triggered methods
  //

  /**
   * Open Corsen website.
   */
  @SuppressWarnings("unused")
  private void openWebsite() {

    QUrl url = new QUrl();
    url.setPath(Globals.WEBSITE_URL);
    QDesktopServices.openUrl(url);
  }

  /**
   * Open Corsen handbook.
   */
  @SuppressWarnings("unused")
  private void openHandbook() {

    QUrl url = new QUrl();
    url.setPath(Globals.HANDBOOK_URL);
    QDesktopServices.openUrl(url);
  }

  /**
   * Open bug report page.
   */
  @SuppressWarnings("unused")
  private void reportBug() {

    QUrl url = new QUrl();
    url.setPath(Globals.REPORT_BUG_URL);
    QDesktopServices.openUrl(url);
  }

  /**
   * Clear history results.
   */
  void clearHistoryResults() {

    CorsenHistoryResults.getCorsenHistoryResults().clear();
    resultsHistoryChanged();
  }

  /**
   * Save history results.
   */
  @SuppressWarnings("unused")
  private void saveHistoryResults() {

    String fileName =
        QFileDialog.getSaveFileName(this, "Save result", this.lastDir,
            new QFileDialog.Filter("Result file (*"
                + Globals.EXTENSION_POPULATION_FILE + ")"));
    if (fileName.length() != 0) {

      try {
        CorsenResultWriter.writeHistoryResults(new File(fileName));
      } catch (IOException e) {
        showError("An error occurs while writing result file.");
      }
    }

  }

  /**
   * Show About dialog box.
   */
  @SuppressWarnings("unused")
  private void about() {

    QMessageBox.about(this, "About " + Globals.APP_NAME, Globals.ABOUT_HTML);
  }

  /**
   * Write settings on the disk.
   */
  @SuppressWarnings("unused")
  private void saveSettings() {

    try {
      this.settings.saveSettings();
    } catch (IOException e) {
      QMessageBox.critical(this, Globals.APP_NAME,
          "An error occurs while writing the setting on the disk.");
    }

  }

  /**
   * Set the particles A path in the first tab of the GUI.
   * @param text Text to show in the GUI
   */
  private void setParticlesAPathLabelText(final String text) {

    this.particlesAPath = text;
    mainWindowUi.particlesAPathLabel.setText(Util.shortPath(text,
        PATH_STRING_MAX_LEN));
  }

  /**
   * Set the particles B path in the first tab of the GUI.
   * @param text Text to show in the GUI
   */
  private void setParticlesBPathLabelText(final String text) {

    this.particlesBPath = text;
    mainWindowUi.particlesBPathLabel.setText(Util
        .shortPath(text, PATH_STRING_MAX_LEN));
  }

  /**
   * Set the directory path in the first tab
   * @param text Text to show in the GUI
   */
  private void setDirectoryPathLabelText(final String text) {

    this.directoryPath = text;
    mainWindowUi.directoryPathLabel.setText(Util.shortPath(text,
        PATH_STRING_MAX_LEN));
  }

  /**
   * Open dialog box for particles A.
   */
  @SuppressWarnings("unused")
  private void openParticlesA() {

    String fileName =
        QFileDialog.getOpenFileName(this, "Set "
            + this.settings.getParticlesAName().toLowerCase() + " file",
            this.lastDir, new QFileDialog.Filter("Particles file (*"
                + Globals.EXTENSION_PARTICLES_FILE + ")"));
    if (fileName.length() != 0) {
      setParticlesAPathLabelText(fileName);
      setDirectoryPathLabelText("");
      setLastDir(fileName);
    }

  }

  /**
   * Open dialog box for particles B.
   */
  @SuppressWarnings("unused")
  private void openParticlesB() {

    String fileName =
        QFileDialog.getOpenFileName(this, "Set "
            + this.settings.getParticlesBName().toLowerCase() + " file",
            this.lastDir, new QFileDialog.Filter("Particles file (*"
                + Globals.EXTENSION_PARTICLES_FILE + ")"));
    if (fileName.length() != 0) {
      setParticlesBPathLabelText(fileName);
      setDirectoryPathLabelText("");
      setLastDir(fileName);
    }
  }

  /**
   * Set the directory path.
   */
  @SuppressWarnings("unused")
  private void openDirectory() {

    QFileDialog dialog = new QFileDialog(this);
    dialog.setFileMode(FileMode.DirectoryOnly);
    dialog.setDirectory(this.lastDir);
    dialog.setWindowTitle("Set particles directory");

    if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {

      List<String> fileNames = dialog.selectedFiles();

      if (fileNames.size() > 0) {
        String file = fileNames.get(0);
        setDirectoryPathLabelText(file);
        setParticlesAPathLabelText("");
        setParticlesBPathLabelText("");
        setLastDir(file);
      }
    }

  }

  /**
   * Set the prefix path.
   */
  @SuppressWarnings("unused")
  private void openOutputFiles() {

    String fileName = QFileDialog.getOpenFileName(this);
    if (fileName.length() != 0)
      mainWindowUi.outputFilesPathLabel.setText(fileName);
  }

  /**
   * Set the prefix path.
   * @throws IOException
   */
  @SuppressWarnings("unused")
  private void saveResultFile() {

    final int modelView = mainWindowUi.resultViewComboBox.currentIndex();

    String ext = this.models.getSaveFileExtension(modelView);

    String fileName =
        QFileDialog.getSaveFileName(this, "Save result", this.lastDir,
            new QFileDialog.Filter("Result file (*" + ext + ")"));
    if (fileName.length() != 0) {

      try {
        this.models.saveView(modelView, fileName);
      } catch (IOException e) {
        showError("An error occurs while writing result file.");
      }
    }

  }

  /**
   * Open dialog box for the output prefix.
   */
  @SuppressWarnings("unused")
  private void saveParticlesFile() {

    final int modelView = mainWindowUi.resultViewComboBox.currentIndex();

    String fileName =
        QFileDialog.getSaveFileName(this, "Save result", this.lastDir,
            new QFileDialog.Filter("Result file (*"
                + Globals.EXTENSION_PARTICLES_FILE + ")"));
    if (fileName.length() != 0) {

      try {
        this.models.saveParticlesView(modelView, fileName);
      } catch (IOException e) {
        showError("An error occurs while writing result file.");
      }
    }

  }

  /**
   * Set the result to the visualisation tabs
   */
  private void endProcess(final CorsenResult result) {

    mainWindowUi.viewOGL.setResult(result);
    mainWindowUi.viewOGL.setSettings(this.settings);

    this.models.setResult(result);
    this.resultViewChanged(Integer.valueOf(this.mainWindowUi.resultViewComboBox
        .currentIndex()));

    if (result == null || result.getCuboidsParticlesA() == null) {
      mainWindowUi.particlesACuboidsRadioButton.setEnabled(false);
    } else
      mainWindowUi.particlesACuboidsRadioButton.setEnabled(true);

    if (result == null || result.getParticlesA() == null) {
      mainWindowUi.particlesARadioButton.setEnabled(false);
    } else
      mainWindowUi.particlesARadioButton.setEnabled(true);

    if (result == null || result.getCuboidsParticlesB() == null) {
      mainWindowUi.particlesBCuboidsRadioButton.setEnabled(false);
    } else
      mainWindowUi.particlesBCuboidsRadioButton.setEnabled(true);

    if (result == null || result.getParticlesB() == null) {
      mainWindowUi.particlesARadioButton.setEnabled(false);
    } else
      mainWindowUi.particlesARadioButton.setEnabled(true);

    if (result == null || result.getMinDistances() == null)
      mainWindowUi.showDistancesCheckBox.setEnabled(false);
    else
      mainWindowUi.showDistancesCheckBox.setEnabled(true);
  }

  /**
   * Update visalisation.
   */
  private void updateVisualisation() {

    final ViewOGL v = mainWindowUi.viewOGL;

    v
        .setDrawNoParticlesA(mainWindowUi.particlesANothingRadioButton
            .isChecked());
    v
        .setDrawNoParticlesB(mainWindowUi.particlesBNothingRadioButton
            .isChecked());
    v.setDrawBaryCenter(mainWindowUi.showBarycentersCheckBox.isChecked());
    v.setDrawDistances(mainWindowUi.showDistancesCheckBox.isChecked());
    v.setDrawParticlesACuboids(mainWindowUi.particlesACuboidsRadioButton
        .isChecked());
    v.setDrawParticlesBCuboids(mainWindowUi.particlesBCuboidsRadioButton
        .isChecked());
    v.setDrawDistances(mainWindowUi.showDistancesCheckBox.isChecked());

    v.setRemakeObject(true);
    v.repaint();
  }

  /**
   * Launch 3D view.
   * @throws IOException
   */
  @SuppressWarnings("unused")
  private void launch3DView() {

    setStartEnable(false);

    mainWindowUi.viewOGL.clear();
    this.models.setResult(null);
    this.resultViewChanged(Integer.valueOf(this.mainWindowUi.resultViewComboBox
        .currentIndex()));

    final String particlesAFile = particlesAPath;
    final String particlesBFile = particlesBPath;

    if (particlesAFile.length() == 0) {
      showError("No particles particles A ("
          + this.settings.getParticlesAName() + ") to load.");
      setStartEnable(true);
      return;
    }

    if (particlesBFile.length() == 0) {
      showError("No particles particles B ("
          + this.settings.getParticlesBName() + ") to load.");
      setStartEnable(true);
      return;
    }

    try {

      mainWindowUi.logTextEdit.setText("");
      CorsenResult cr =
          new CorsenResult(new File(particlesAFile), new File(particlesBFile),
              null, this.settings, null);
      DistancesCalculator dc = new DistancesCalculator(cr);
      dc.setCoordinatesFactor(settings.getFactor());
      dc.setZCoordinatesFactor(settings.getZFactor());

      mainWindowUi.progressLabel.setText("Process 0 of 1 cell");
      mainWindowUi.progressBar.setValue(0);

      mainWindowUi.logTextEdit.append("Lannch 3D visualisation only.");
      mainWindowUi.logTextEdit.append("Particles A ("
          + this.settings.getParticlesAName() + ") file: " + particlesAFile);
      mainWindowUi.logTextEdit.append("Particles B ("
          + this.settings.getParticlesBName() + ") file: " + particlesBFile);

      dc.loadParticles();
      mainWindowUi.logTextEdit.append("Show 3D visualisation successfully.");
      mainWindowUi.progressLabel.setText("Process 1 of 1 cell");
      mainWindowUi.progressBar.setValue(100);
      endProcess(cr);
    } catch (IOException e) {
      showError("Error while loading particles files.");
    }

    setStartEnable(true);
  }

  private UpdateStatusQt createNewUpdateStatus() {

    final UpdateStatusQt updateStatus = new UpdateStatusQt();

    updateStatus.errorSignal.connect(this, "showError(String)",
        ConnectionType.QueuedConnection);
    updateStatus.messageSignal.connect(this, "showMessage(String)",
        ConnectionType.QueuedConnection);
    updateStatus.statusSignal.connect(this, "updateStatus(ProgressEvent)",
        ConnectionType.QueuedConnection);
    updateStatus.resultSignal.connect(this, "endProcess(CorsenResult)",
        ConnectionType.QueuedConnection);

    return updateStatus;
  }

  /**
   * Launch analysis.
   */
  void launchAnalysis() {

    launchAnalysis(this.directoryPath, this.particlesAPath,
        this.particlesBPath, null);
  }

  void launchAnalysis(final String dirFile, final String particlesAFile,
      final String particlesBFile, final String resultDir) {

    mainWindowUi.viewOGL.clear();
    this.models.setResult(null);
    this.resultViewChanged(Integer.valueOf(this.mainWindowUi.resultViewComboBox
        .currentIndex()));

    final CorsenCore cc = new CorsenCore();

    cc.setSettings(this.settings);
    cc.setUpdateStatus(createNewUpdateStatus());

    if (dirFile != null && dirFile.length() > 0) {

      cc.setDirFiles(new File(dirFile));
      cc.setMultipleFiles(true);

      if (this.settings.isClearHistoryWhenLaunchingNewCalc()) {
        CorsenHistoryResults.getCorsenHistoryResults().clear();
        CorsenQt.updateHistoryResults();
      }

      QThread t = new QThread(cc);
      cc.getUpdateStatus().moveToThread(t);
      t.start();

    } else if (particlesAFile.length() == 0 || particlesBFile.length() == 0) {

      if (particlesAFile.length() == 0)
        showError("No particles A ("
            + this.settings.getParticlesAName() + ")  file specified.");
      else
        showError("No particles B ("
            + this.settings.getParticlesBName() + ")  file specified.");

    } else {

      String outputDir = resultDir;

      if (outputDir == null) {

        QFileDialog dialog = new QFileDialog(this);
        dialog.setFileMode(QFileDialog.FileMode.AnyFile);
        dialog.setWindowTitle("Set output files prefix");

        dialog.setDirectory(this.lastDir);

        if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {

          List<String> fileNames = dialog.selectedFiles();
          File f = new File(fileNames.get(0));
          outputDir = f.getAbsolutePath();
        }
      }

      if (outputDir != null) {

        mainWindowUi.particlesAPathLabel.setText(particlesAFile);
        mainWindowUi.particlesBPathLabel.setText(particlesBFile);
        mainWindowUi.outputFilesPathLabel.setText(outputDir);

        cc.setParticlesBFile(new File(particlesBFile));
        cc.setParticlesAFile(new File(particlesAFile));
        cc.setResultFile(new File(outputDir));
        cc.setMultipleFiles(false);

        Thread t = new Thread(cc);
        cc.getUpdateStatus().moveToThread(t);
        t.start();
      }
    }
  }

  /**
   * Enable the start button
   * @param value The state of activation of the start button
   */
  private void setStartEnable(final boolean value) {

    this.mainWindowUi.launchAnalysisPushButton.setEnabled(value);
    this.mainWindowUi.launch3DViewPushButton.setEnabled(value);
    this.mainWindowUi.updateViewPushButton.setEnabled(value);
    this.mainWindowUi.viewOGL.setRemakeObject(value);

    if (value == true && this.models.getResult() != null) {
      this.mainWindowUi.saveResultPushButton.setEnabled(true);

      if (this.mainWindowUi.resultViewComboBox.currentIndex() > 1)
        this.mainWindowUi.saveParticlesPushButton.setEnabled(true);
    } else {
      this.mainWindowUi.saveResultPushButton.setEnabled(false);
      this.mainWindowUi.saveParticlesPushButton.setEnabled(false);
    }

    this.resultViewChanged(Integer.valueOf(this.mainWindowUi.resultViewComboBox
        .currentIndex()));
    this.resultsHistoryChanged();
  }

  /**
   * Show status message.
   * @param message Message to show
   */
  private void showStatusMessage(final String message) {

    if (message == null)
      return;

    this.mainWindowUi.logTextEdit.insertPlainText(message + "\n");
  }

  /**
   * Clear status message.
   */
  private void clearStatusMessage() {

    this.mainWindowUi.logTextEdit.setPlainText("");
  }

  /**
   * Set the last used directory using the last used filename.
   * @param filename The last used filename
   */
  private void setLastDir(final String filename) {

    if (filename == null)
      return;

    File f = new File(filename);

    this.lastDir = f.getParentFile().getAbsolutePath();
  }

  /**
   * Set the progress message
   * @param message Message to show
   */
  private void showProgressMessage(final String message) {

    mainWindowUi.progressLabel.setText(message);
  }

  private void showProgressBarProgress(final int currentCell,
      final int cellCount, final int phase, final int maxPhase,
      final int indexInPhase) {

    final int max = cellCount * maxPhase * ProgressEvent.INDEX_IN_PHASE_MAX;

    final int value =
        (currentCell - 1)
            * maxPhase * ProgressEvent.INDEX_IN_PHASE_MAX + (phase - 1)
            * ProgressEvent.INDEX_IN_PHASE_MAX + indexInPhase;

    if (false)
      showStatusMessage("Phase="
          + phase + "/" + maxPhase + " index=" + indexInPhase + " val=" + value
          + "/" + max);

    mainWindowUi.progressBar.setMinimum(0);
    mainWindowUi.progressBar.setMaximum(max);
    mainWindowUi.progressBar.setValue(value);
  }

  @SuppressWarnings("unused")
  private void quit() {

    QApplication.exit();
  }

  /**
   * Implements the copy function for the GUI
   */
  @SuppressWarnings("unused")
  private void copy() {

    if (this.mainWindowUi.tabWidget.currentIndex() == 1)
      copyCorsenResult();

    if (this.mainWindowUi.tabWidget.currentIndex() == 3)
      copyHistoryResult();

  }

  /**
   * Copy the content of the table in the result tab.
   */
  private void copyCorsenResult() {

    int index = this.mainWindowUi.resultViewComboBox.currentIndex();

    if (this.models.getResult() == null)
      return;

    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      this.models.saveView(index, os);

      QApplication.clipboard().setText(new String(os.toByteArray()));

    } catch (IOException e) {

      showError(e.getMessage());
      this.mainWindowUi.statusbar.showMessage(
          "Error while copying result to clipboard", 2000);

    }

  }

  /**
   * Copy the content of the table in the history tab.
   */
  private void copyHistoryResult() {

    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      CorsenResultWriter.writeHistoryResults(os);

      QApplication.clipboard().setText(new String(os.toByteArray()));

    } catch (IOException e) {

      showError(e.getMessage());
      this.mainWindowUi.statusbar.showMessage(
          "Error while copying result to clipboard", 2000);

    }

  }

  /**
   * Launch the configure dialog box.
   */
  public void configureDialog() {

    CorsenConfigureQt cc = new CorsenConfigureQt(this, this.settings);
    cc.configureDialog();
    setWidgetTexts();
    updateVisualisation();
  }

  /**
   * Launch the configure dialog box for viewer filter.
   */
  public void configureViewerFilters() {

    FilterDialog fd = new FilterDialog(this, this.settings);
    fd.show();
    updateVisualisation();
  }

  //
  // Changed methods
  //
  private void resultsHistoryChanged() {

    final HistoryDataModel historyModel = DataModelQt.getHistoryModel();

    if (mainWindowUi.historyTableView.model() == null) {
      mainWindowUi.historyTableView.setModel(historyModel);
      mainWindowUi.historyTableView.setSortingEnabled(true);
    } else
      historyModel.update();
    // mainWindowUi.historyTableView.setModel(null);
    // mainWindowUi.historyTableView.setModel(historyModel);

    if (mainWindowUi.launchAnalysisPushButton.isEnabled()) {

      mainWindowUi.historyBoxplotLabel.setPixmap(historyModel
          .getBoxplot(this.settings));
      mainWindowUi.historyHistogramLabel.setPixmap(historyModel.getHisto(
          this.settings.getHistogramHistoryNumberClasses(), this.settings));

      if (this.models.getResult() == null)
        this.mainWindowUi.saveParticlesPushButton.setEnabled(false);
      else
        this.mainWindowUi.saveParticlesPushButton
            .setEnabled(this.mainWindowUi.resultViewComboBox.currentIndex() > 1);

      this.refreshHistoryGraphics = true;

    } else {

      if (this.refreshHistoryGraphics) {

        this.refreshHistoryGraphics = false;

        QImage img = mainWindowUi.historyBoxplotLabel.pixmap().toImage();
        if (img != null && !img.isNull())
          mainWindowUi.historyBoxplotLabel.setPixmap(QPixmap.fromImage(QtUtil
              .toGrayscale(img)));

        img = mainWindowUi.historyHistogramLabel.pixmap().toImage();
        if (img != null && !img.isNull())
          mainWindowUi.historyHistogramLabel.setPixmap(QPixmap.fromImage(QtUtil
              .toGrayscale(img)));
      }
      this.mainWindowUi.saveParticlesPushButton.setEnabled(false);
    }

    mainWindowUi.HistoryResultlabel.setText(historyModel.getResultMessage());
  }

  private void resultViewChanged(final Object o) {

    resultsHistoryChanged();

    final int i = ((Integer) o).intValue();
    mainWindowUi.resultTableView.setModel(this.models.getModel(i));
    mainWindowUi.resultTableView.setSortingEnabled(true);

    if (mainWindowUi.launchAnalysisPushButton.isEnabled()) {

      if (this.models.getResult() == null
          || this.models.getResult().getCuboidsParticlesA() == null)
        return;

      mainWindowUi.imageLabel.setPixmap(this.models
          .getResultPixmap(i, settings));

    } else
      mainWindowUi.imageLabel
          .setText("Waiting the end of the process to show graphics");

    mainWindowUi.resultTableView.setAlternatingRowColors(true);
    mainWindowUi.historyTableView.setAlternatingRowColors(true);
  }

  @SuppressWarnings("unused")
  private void resultsHistoryViewChanged(Object o) {

    int i = (Integer) o;
    String val = CorsenHistoryResults.StatType.values()[i].toString();

    final HistoryDataModel historyModel = DataModelQt.getHistoryModel();

    if (!val.equals(CorsenHistoryResults.getCorsenHistoryResults()
        .getStatType().toString())) {

      historyModel.setType(val);

      historyModel.update();
      mainWindowUi.HistoryResultlabel.setText(historyModel.getResultMessage());

      if (mainWindowUi.launchAnalysisPushButton.isEnabled()) {
        mainWindowUi.historyBoxplotLabel.setPixmap(historyModel
            .getBoxplot(this.settings));
        mainWindowUi.historyHistogramLabel.setPixmap(historyModel.getHisto(
            this.settings.getHistogramHistoryNumberClasses(), this.settings));
      }
    }
  }

  //
  // Update status methods
  //

  /**
   * Update the status bar.
   * @param e event to show
   */
  public void updateStatus(final ProgressEvent e) {

    // if (true) return;

    if (e == null)
      return;

    boolean endEvent = false;

    this.status.type = e.getType();

    switch (e.getType()) {

    case START_CELLS_EVENT:

      setStartEnable(false);
      this.status.timeStartCells = System.currentTimeMillis();

      this.status.maxPhase = e.getIntValue1();
      setParticlesAPathLabelText("");
      setParticlesBPathLabelText("");
      clearStatusMessage();
      this.status.currentPhase = 0;

      break;

    case START_CELL_EVENT:

      this.status.timeStartCell = System.currentTimeMillis();
      this.status.currentCellToProcess = e.getIntValue1();
      this.status.cellToProcessCount = e.getIntValue2();
      this.status.particlesAFilePath = e.getStringValue1();
      this.status.particlesBFilePath = e.getStringValue2();
      this.status.resultFilePath = e.getStringValue3();
      setParticlesAPathLabelText(this.status.particlesAFilePath);
      setParticlesBPathLabelText(this.status.particlesBFilePath);
      this.status.currentPhase = 0;
      this.status.nbPhaseDone = 0;

      showProgressMessage("Progress: "
          + this.status.currentCellToProcess + " of "
          + this.status.cellToProcessCount + " cells");

      showStatusMessage("Process cell "
          + this.status.currentCellToProcess + " of "
          + this.status.cellToProcessCount + " cells");
      showStatusMessage("Particles A ("
          + this.settings.getParticlesAName() + ") file: "
          + this.status.particlesAFilePath);
      showStatusMessage("Particles B ("
          + this.settings.getParticlesBName() + ") file: "
          + this.status.particlesBFilePath);
      showStatusMessage("Output files prefix: " + this.status.resultFilePath);

      break;

    case START_READ_PARTICLES_A_FILE_EVENT:
    case START_READ_PARTICLES_B_FILE_EVENT:
    case START_CHANGE_PARTICLES_A_COORDINATES_EVENT:
    case START_CHANGE_PARTICLES_B_COORDINATES_EVENT:
    case START_FILTER_PARTICLES_A_EVENT:
    case START_FILTER_PARTICLES_B_EVENT:
    case START_CALC_PARTICLES_A_CUBOIDS_EVENT:
    case START_CALC_PARTICLES_B_CUBOIDS_EVENT:
    case START_CALC_MIN_DISTANCES_EVENT:
    case START_DISTANCES_ANALYSIS:
    case START_WRITE_DATA_EVENT:
    case START_WRITE_IV_PARTICLES_A_EVENT:
    case START_WRITE_IV_PARTICLES_B_EVENT:
    case START_WRITE_IV_PARTICLES_A_CUBOIDS_EVENT:
    case START_WRITE_FULLRESULT_EVENT:
    case START_WRITE_RPLOT_PARTICLES_A_EVENT:
    case START_WRITE_RPLOT_PARTICLES_B_EVENT:
    case START_WRITE_RPLOT_PARTICLES_A_CUBOIDS_EVENT:
    case START_WRITE_RPLOT_PARTICLES_B_CUBOIDS_EVENT:
    case START_WRITE_RPLOT_DISTANCES_EVENT:

      this.status.currentPhase = e.getType().ordinal();
      this.status.indexInPhase = 0;
      this.status.nbPhaseDone++;
      showCurrentPhase();

      break;

    case PROGRESS_CALC_PARTICLES_A_CUBOIDS_EVENT:
    case PROGRESS_CALC_PARTICLES_B_CUBOIDS_EVENT:
    case PROGRESS_CALC_DISTANCES_EVENT:

      this.status.indexInPhase = e.getIntValue1();

      break;

    case END_CELL_EVENT:
      final long timeEndCell = System.currentTimeMillis();
      showStatusMessage("Process current cell in "
          + Util.toTimeHumanReadable(timeEndCell - this.status.timeStartCell)
          + ".\n");

      endEvent = true;

      break;

    case END_CELLS_SUCCESSFULL_EVENT:
      final long timeEndCells = System.currentTimeMillis();
      showStatusMessage("Process all cells in "
          + Util.toTimeHumanReadable(timeEndCells - this.status.timeStartCells)
          + ".");
      CorsenQt.this.setStartEnable(true);

      endEvent = true;

      break;

    case END_ERROR_EVENT:
      setStartEnable(true);
      showStatusMessage("Error !!!");
      return;

    default:
      return;
    }

    if (this.status.currentPhase != 0)
      showProgressBarProgress(this.status.currentCellToProcess,
          this.status.cellToProcessCount, this.status.nbPhaseDone,
          this.status.maxPhase, endEvent
              ? ProgressEvent.INDEX_IN_PHASE_MAX : this.status.indexInPhase);

  }

  private void showCurrentPhase() {

    final StringBuffer sb = new StringBuffer();
    sb.append(" Phase: ");

    sb.append(this.status.nbPhaseDone);
    sb.append("/");
    sb.append(this.status.maxPhase);
    sb.append(" (");
    sb.append(this.status.type.toString());
    sb.append(")");

    showStatusMessage(sb.toString());
  }

  /**
   * Show an error message.
   * @param msg Message to display
   */
  public void showError(final String msg) {

    QMessageBox.critical(this, Globals.APP_NAME, msg);
  }

  /**
   * Show a message.
   * @param msg Message to display
   */
  public void showMessage(final String msg) {

    QMessageBox.information(this, Globals.APP_NAME, msg);
  }

  void redrawResultGraph() {

    this.models.setResult(this.models.getResult());
    this.resultViewChanged(Integer.valueOf(this.mainWindowUi.resultViewComboBox
        .currentIndex()));
  }

  void redrawHistoryGraph() {

    this.refreshHistoryGraphics = true;
    resultsHistoryChanged();
  }

  //
  // Init tabs methods
  //

  private void initResultTab() {

    int n = this.models.getViewCount();

    for (int i = 0; i < n; i++)
      mainWindowUi.resultViewComboBox.addItem(this.models.getViewDescription(i,
          this.settings), Integer.valueOf(i));

    mainWindowUi.resultViewComboBox.currentIndexChanged.connect(this,
        "resultViewChanged(Object)");

    resultViewChanged(Integer.valueOf(0));

    mainWindowUi.saveResultPushButton.clicked.connect(this, "saveResultFile()");
    mainWindowUi.saveParticlesPushButton.clicked.connect(this,
        "saveParticlesFile()");

  }

  private void initView3DTab() {

    mainWindowUi.launch3DViewPushButton.clicked.connect(this, "launch3DView()");

    mainWindowUi.updateViewPushButton.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.updateViewPushButton.setHidden(true);
    mainWindowUi.particlesANothingRadioButton.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.particlesARadioButton.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.particlesACuboidsRadioButton.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.particlesBNothingRadioButton.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.particlesBRadioButton.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.particlesBCuboidsRadioButton.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.showBarycentersCheckBox.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.showDistancesCheckBox.clicked.connect(this,
        "updateVisualisation()");
    mainWindowUi.filterViewpushButton.clicked.connect(this,
        "configureViewerFilters()");
  }

  private void initHistoryTab() {

    mainWindowUi.historyClearPushButton.clicked.connect(this,
        "clearHistoryResults()");
    mainWindowUi.historySaveResultsPushButton.clicked.connect(this,
        "saveHistoryResults()");

    for (CorsenHistoryResults.StatType t : CorsenHistoryResults.StatType
        .values())
      mainWindowUi.historyStatComboBox.addItem(t.toString());

    mainWindowUi.historyStatComboBox.currentIndexChanged.connect(this,
        "resultsHistoryViewChanged(Object)");
  }

  private void setWidgetTexts() {

    final String particlesAName = this.settings.getParticlesAName();
    final String particlesBName = this.settings.getParticlesBName();

    final Ui_CorsenMainWindow mui = this.mainWindowUi;

    mui.particleAFileLabel.setText("<b>Particles A ("
        + particlesAName + ") file:</b>");
    mui.particleBFileLabel.setText("<b>Particles B ("
        + particlesBName + ") file:</b>");

    mui.actionOpen_particlesA.setText("Open particles A ("
        + particlesAName + ")...");
    mui.actionOpen_particlesB.setText("Open particles B ("
        + particlesBName + ")...");

    mui.particlesAGroupBox.setTitle(particlesAName);
    mui.particlesBGroupBox.setTitle(particlesBName);

    mui.particlesARadioButton.setText(particlesAName);
    mui.particlesBRadioButton.setText(particlesBName);

    mui.particlesACuboidsRadioButton.setText(particlesAName + " preprocessed");
    mui.particlesBCuboidsRadioButton.setText(particlesBName + " preprocessed");

    int max = mui.particlesBCuboidsRadioButton.geometry().width();

    QRect gA = mui.particlesAGroupBox.geometry();
    mui.particlesAGroupBox.setGeometry(gA.x(), gA.y(), max, gA.height());

    QRect gB = mui.particlesBGroupBox.geometry();
    mui.particlesBGroupBox.setGeometry(gB.x(), gB.y(), max, gB.height());

    QRect gBt = mui.updateViewPushButton.geometry();
    mui.updateViewPushButton.setGeometry(gBt.x(), gBt.y(), max, gBt.height());

    mui.historyTableView.setToolTip("Results history table");
    mui.historyTableView.setStatusTip("Results history table");

    int n = this.models.getViewCount();
    for (int i = 0; i < n; i++)
      mainWindowUi.resultViewComboBox.setItemText(i, this.models
          .getViewDescription(i, this.settings));

    mui.viewOGL.setToolTip("3D Visualisation");
    mui.viewOGL.setStatusTip("3D Visualisation");
  }

  /**
   * Launch an analysis for a recalculation of a result
   * @param particlesAFile path of particles A file
   * @param particlesBFile path of particles B file
   * @param dirFile path of results files
   */
  static void launchAnalysis(final String particlesAFile,
      final String particlesBFile, final String dirFile) {

    mainw.launchAnalysis(null, particlesAFile, particlesBFile, dirFile);
  }

  /**
   * Update the history results.
   */
  static void updateHistoryResults() {

    mainw.resultsHistoryChanged();
  }

  static boolean isCalculation() {

    return !mainw.mainWindowUi.launchAnalysisPushButton.isEnabled();

  }

  //
  // End of application
  //

  /**
   * Close the application.
   */
  public void closeEvent(QCloseEvent event) {

    QApplication.exit();

    /*
     * super.closeEvent(event); quit(); System.exit(0);
     */
  }

  //
  // Utility methods
  //

  /**
   * Main method.
   * @param args application arguments
   */
  public static void main(final String[] args) {

    QApplication.initialize(args);
    logger.info("Java version: " + System.getProperty("java.version"));
    logger.info("Qt version: " + QtInfo.versionString());

    // Set locales
    Locale.setDefault(Locale.UK);
    QLocale.setDefault(new QLocale(QLocale.Language.English,
        QLocale.Country.UnitedKingdom));

    mainw = new CorsenQt();

    mainw.show();
    QApplication.exec();
  }

  //
  // Constructor
  //

  public CorsenQt() {

    this.settings = Corsen.getSettings();

    // Place what you made in Designer onto the main window.
    mainWindowUi.setupUi(this);

    setWindowTitle(Globals.getWindowsTitle());

    mainWindowUi.actionAbout_Corsen.triggered.connect(this, "about()");
    // mainWindowUi.actionAbout_QtJambi.triggered.connect(QApplication.instance(),
    // "aboutQtJambi()");

    mainWindowUi.actionCorsen_Website.triggered.connect(this, "openWebsite()");
    mainWindowUi.actionCorsen_Handbook.triggered
        .connect(this, "openHandbook()");
    mainWindowUi.action_Report_Bug.triggered.connect(this, "reportBug()");

    mainWindowUi.action_Configure_Corsen.triggered.connect(this,
        "configureDialog()");
    mainWindowUi.actionSave_settings.triggered.connect(this, "saveSettings()");
    mainWindowUi.actionOpen_particlesA.triggered.connect(this,
        "openParticlesA()");
    mainWindowUi.actionOpen_particlesB.triggered.connect(this,
        "openParticlesB()");
    mainWindowUi.actionOpen_a_directory.triggered.connect(this,
        "openDirectory()");
    mainWindowUi.action_Start_analysis.triggered.connect(this,
        "launchAnalysis()");
    mainWindowUi.launchAnalysisPushButton.clicked.connect(this,
        "launchAnalysis()");

    mainWindowUi.action_Quit.triggered.connect(this, "quit()");

    mainWindowUi.action_Copy.triggered.connect(this, "copy()");

    setWidgetTexts();

    initResultTab();
    initView3DTab();
    initHistoryTab();

    final String iconPath =
        "classpath:"
            + (Globals.IS_JAR ? "" : "/files") + "/images/corsen-logo.png";

    logger.info("iconPath: " + iconPath);

    setWindowIcon(new QIcon(iconPath));

    endProcess(null);
    setStartEnable(true);

  }
}
