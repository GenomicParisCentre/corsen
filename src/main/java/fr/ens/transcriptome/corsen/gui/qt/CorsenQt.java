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

// TODO add an in phase progress for writing/computing distances
// TODO Remove the cancel button
// TODO When alt-f4 -> System.exit(0)
public class CorsenQt extends QMainWindow {

  private static Logger logger = Logger.getLogger(CorsenQt.class.getName());

  private static final int PATH_STRING_MAX_LEN = 50;

  private static CorsenQt mainw;

  private Ui_CorsenMainWindow mainWindowUi = new Ui_CorsenMainWindow();

  private DataModelQt models = new DataModelQt();
  private Settings settings = new Settings();
  private String lastDir =
      Globals.DEBUG_HOME_DIR ? "/home/jourdren/Desktop/atp16" : "";

  private String mitoPath = "";
  private String messengerPath = "";
  private String directoryPath = "";

  private boolean refreshHistoryGraphics;

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
    String mitoFilePath;
    String rnaFilePath;
    String resultFilePath;
  }

  private final StatusInfo status = new StatusInfo();

  @SuppressWarnings("unused")
  private void openWebsite() {

    QUrl url = new QUrl();
    url.setPath(Globals.WEBSITE_URL);
    QDesktopServices.openUrl(url);
  }

  @SuppressWarnings("unused")
  private void openHandbook() {

    QUrl url = new QUrl();
    url.setPath(Globals.HANDBOOK_URL);
    QDesktopServices.openUrl(url);
  }

  @SuppressWarnings("unused")
  private void reportBug() {

    QUrl url = new QUrl();
    url.setPath(Globals.REPORT_BUG_URL);
    QDesktopServices.openUrl(url);
  }

  //
  // Qt triggered methods
  //

  @SuppressWarnings("unused")
  void clearHistoryResults() {

    CorsenHistoryResults.getCorsenHistoryResults().clear();
    resultsHistoryChanged();
  }

  @SuppressWarnings("unused")
  private void saveHistoryResults() {

    String fileName =
        QFileDialog.getSaveFileName(this, "Save result", this.lastDir,
            new QFileDialog.Filter("Result file (*.txt)"));
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

  private void setMessengerPathLabelText(String text) {

    this.messengerPath = text;
    mainWindowUi.messengerPathLabel.setText(Util.shortPath(text,
        PATH_STRING_MAX_LEN));
  }

  private void setMitoPathLabelText(String text) {

    this.mitoPath = text;
    mainWindowUi.mitoPathLabel.setText(Util
        .shortPath(text, PATH_STRING_MAX_LEN));
  }

  private void setDirectoryPathLabelText(String text) {

    this.directoryPath = text;
    mainWindowUi.directoryPathLabel.setText(Util.shortPath(text,
        PATH_STRING_MAX_LEN));
  }

  /**
   * Set the messenger path.
   */
  @SuppressWarnings("unused")
  private void openMessengers() {

    String fileName =
        QFileDialog.getOpenFileName(this, "Set messengers file", this.lastDir,
            new QFileDialog.Filter("Particles file (*"
                + Globals.EXTENSION_PARTICLES_FILE + ")"));
    if (fileName.length() != 0) {
      setMessengerPathLabelText(fileName);
      setDirectoryPathLabelText("");
      setLastDir(fileName);
    }

  }

  /**
   * Set the mitos path.
   */
  @SuppressWarnings("unused")
  private void openMitos() {

    String fileName =
        QFileDialog.getOpenFileName(this, "Set mitochondria file",
            this.lastDir, new QFileDialog.Filter("Particles file (*.par)"));
    if (fileName.length() != 0) {
      setMitoPathLabelText(fileName);
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
    dialog.setWindowTitle("Set particle directory");

    if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {

      List<String> fileNames = dialog.selectedFiles();

      if (fileNames.size() > 0) {
        String file = fileNames.get(0);
        setDirectoryPathLabelText(file);
        setMessengerPathLabelText("");
        setMitoPathLabelText("");
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
        this.models.saveViewl(modelView, fileName);
      } catch (IOException e) {
        showError("An error occurs while writing result file.");
      }
    }

  }

  /**
   * Set the result to the visualisation tabs
   */
  @SuppressWarnings("unused")
  private void endProcess(final CorsenResult result) {

    mainWindowUi.viewOGL.setResult(result);
    mainWindowUi.viewOGL.setSettings(this.settings);

    this.models.setResult(result);
    this.resultViewChanged(Integer.valueOf(this.mainWindowUi.resultViewComboBox
        .currentIndex()));

    if (result == null || result.getCuboidsMessengersParticles() == null) {
      mainWindowUi.particlesACuboidsRadioButton.setEnabled(false);
    } else
      mainWindowUi.particlesACuboidsRadioButton.setEnabled(true);

    if (result == null || result.getMessengersParticles() == null) {
      mainWindowUi.particlesARadioButton.setEnabled(false);
    } else
      mainWindowUi.particlesARadioButton.setEnabled(true);

    if (result == null || result.getCuboidsMitosParticles() == null) {
      mainWindowUi.particlesBCuboidsRadioButton.setEnabled(false);
    } else
      mainWindowUi.particlesBCuboidsRadioButton.setEnabled(true);

    if (result == null || result.getMitosParticles() == null) {
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
  @SuppressWarnings("unused")
  private void updateVisualisation() {

    final ViewOGL v = mainWindowUi.viewOGL;

    v
        .setDrawNoMessengers(mainWindowUi.particlesANothingRadioButton
            .isChecked());
    v.setDrawNoMitos(mainWindowUi.particlesBNothingRadioButton.isChecked());
    v.setDrawBaryCenter(mainWindowUi.showBarycentersCheckBox.isChecked());
    v.setDrawDistances(mainWindowUi.showDistancesCheckBox.isChecked());
    v.setDrawMessengersCuboids(mainWindowUi.particlesACuboidsRadioButton
        .isChecked());
    v
        .setDrawMitosCuboids(mainWindowUi.particlesBCuboidsRadioButton
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

    String arnFile = messengerPath;
    String mitoFile = mitoPath;

    if (arnFile.length() == 0) {
      showError("No particles particles A ("
          + this.settings.getParticlesAName() + ") to load.");
      setStartEnable(true);
      return;
    }

    if (mitoFile.length() == 0) {
      showError("No particles particles B ("
          + this.settings.getParticlesBName() + ") to load.");
      setStartEnable(true);
      return;
    }

    try {

      mainWindowUi.logTextEdit.setText("");
      CorsenResult cr =
          new CorsenResult(new File(arnFile), new File(mitoFile), null,
              this.settings, null);
      DistancesCalculator dc = new DistancesCalculator(cr);
      dc.setCoordinatesFactor(settings.getFactor());
      dc.setZCoordinatesFactor(settings.getZFactor());

      mainWindowUi.progressLabel.setText("Process 0 of 1 cell");
      mainWindowUi.progressBar.setValue(0);

      mainWindowUi.logTextEdit.append("Lannch 3D visualisation only.");
      mainWindowUi.logTextEdit.append("Particles A ("
          + this.settings.getParticlesAName() + ") file: " + arnFile);
      mainWindowUi.logTextEdit.append("Particles B ("
          + this.settings.getParticlesBName() + ") file: " + mitoFile);

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
  @SuppressWarnings("unused")
  void launchAnalysis() {

    launchAnalysis(this.directoryPath, this.messengerPath, this.mitoPath, null);
  }

  void launchAnalysis(final String dirFile, String arnFile, String mitoFile,
      final String resultDir) {

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

    } else if (arnFile.length() == 0 || mitoFile.length() == 0) {

      if (arnFile.length() == 0)
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

        mainWindowUi.messengerPathLabel.setText(arnFile);
        mainWindowUi.mitoPathLabel.setText(mitoFile);
        mainWindowUi.outputFilesPathLabel.setText(outputDir);

        cc.setParticlesBFile(new File(mitoFile));
        cc.setParticlesAFile(new File(arnFile));
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
    if (value == true && this.models.getResult() != null)
      this.mainWindowUi.saveResultPushButton.setEnabled(true);
    else
      this.mainWindowUi.saveResultPushButton.setEnabled(false);

    this.resultViewChanged(Integer.valueOf(this.mainWindowUi.resultViewComboBox
        .currentIndex()));
    this.resultsHistoryChanged();
  }

  private void showStatusMessage(final String message) {

    if (message == null)
      return;

    this.mainWindowUi.logTextEdit.insertPlainText(message + "\n");
  }

  private void clearStatusMessage() {

    this.mainWindowUi.logTextEdit.setPlainText("");
  }

  private void setLastDir(final String filename) {

    if (filename == null)
      return;

    File f = new File(filename);

    this.lastDir = f.getParentFile().getAbsolutePath();
  }

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

  @SuppressWarnings("unused")
  private void copy() {

    if (this.mainWindowUi.tabWidget.currentIndex() == 1)
      copyCorsenResult();

    if (this.mainWindowUi.tabWidget.currentIndex() == 3)
      copyHistoryResult();

  }

  private void copyCorsenResult() {

    int index = this.mainWindowUi.resultViewComboBox.currentIndex();

    if (this.models.getResult() == null)
      return;

    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      this.models.saveViewl(index, os);

      QApplication.clipboard().setText(new String(os.toByteArray()));

    } catch (IOException e) {

      showError(e.getMessage());
      this.mainWindowUi.statusbar.showMessage(
          "Error while copying result to clipboard", 2000);

    }

  }

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

  @SuppressWarnings("unused")
  public void configureDialog() {

    CorsenConfigureQt cc = new CorsenConfigureQt(this, this.settings);
    cc.configureDialog();
    setWidgetTexts();
    updateVisualisation();
  }

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
    }

    mainWindowUi.HistoryResultlabel.setText(historyModel.getResultMessage());
  }

  @SuppressWarnings("unused")
  private void resultViewChanged(Object o) {

    resultsHistoryChanged();

    final int i = ((Integer) o).intValue();
    mainWindowUi.resultTableView.setModel(this.models.getModel(i));

    if (mainWindowUi.launchAnalysisPushButton.isEnabled()) {

      if (this.models.getResult() == null)
        return;

      mainWindowUi.imageLabel.setPixmap(this.models.getPixmap(i, settings));

    } else
      mainWindowUi.imageLabel
          .setText("Waiting the end of the process to show graphics");

    mainWindowUi.resultTableView.setAlternatingRowColors(true);
    mainWindowUi.historyTableView.setAlternatingRowColors(true);
  }

  public void closeEvent(QCloseEvent event) {

    QApplication.exit();

    /*
     * super.closeEvent(event); quit(); System.exit(0);
     */
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
      setMessengerPathLabelText("");
      setMitoPathLabelText("");
      clearStatusMessage();
      this.status.currentPhase = 0;

      break;

    case START_CELL_EVENT:

      this.status.timeStartCell = System.currentTimeMillis();
      this.status.currentCellToProcess = e.getIntValue1();
      this.status.cellToProcessCount = e.getIntValue2();
      this.status.rnaFilePath = e.getStringValue1();
      this.status.mitoFilePath = e.getStringValue2();
      this.status.resultFilePath = e.getStringValue3();
      setMessengerPathLabelText(this.status.rnaFilePath);
      setMitoPathLabelText(this.status.mitoFilePath);
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
          + this.status.rnaFilePath);
      showStatusMessage("Particles B ("
          + this.settings.getParticlesBName() + ") file: "
          + this.status.mitoFilePath);
      showStatusMessage("Output files prefix: " + this.status.resultFilePath);

      break;

    case START_READ_MESSENGERS_EVENT:
    case START_READ_MITOS_EVENT:
    case START_CHANGE_MESSENGERS_COORDINATES_EVENT:
    case START_CHANGE_MITOS_COORDINATES_EVENT:
    case START_CALC_MESSENGERS_CUBOIDS_EVENT:
    case START_CALC_MITOS_CUBOIDS_EVENT:
    case START_CALC_MIN_DISTANCES_EVENT:
    case START_DISTANCES_ANALYSIS:
    case START_WRITE_DATA_EVENT:
    case START_WRITE_IV_MESSENGERS_EVENT:
    case START_WRITE_IV_MITOS_EVENT:
    case START_WRITE_IV_MESSENGERS_CUBOIDS_EVENT:
    case START_WRITE_FULLRESULT_EVENT:
    case START_WRITE_RPLOT_MESSENGERS_EVENT:
    case START_WRITE_RPLOT_MITOS_EVENT:
    case START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT:
    case START_WRITE_RPLOT_MITOS_CUBOIDS_EVENT:
    case START_WRITE_RPLOT_DISTANCES_EVENT:

      this.status.currentPhase = e.getType().ordinal();
      this.status.indexInPhase = 0;
      this.status.nbPhaseDone++;
      showCurrentPhase();

      break;

    case PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT:
    case PROGRESS_CALC_MITOS_CUBOIDS_EVENT:
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
      mainWindowUi.resultViewComboBox.addItem(
          this.models.getViewDescription(i), Integer.valueOf(i));

    mainWindowUi.resultViewComboBox.currentIndexChanged.connect(this,
        "resultViewChanged(Object)");

    resultViewChanged(Integer.valueOf(0));

    mainWindowUi.saveResultPushButton.clicked.connect(this, "saveResultFile()");

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

    mui.viewOGL.setToolTip("3D Visualisation");
    mui.viewOGL.setStatusTip("3D Visualisation");
  }

  /**
   * Launch an analysis for a recalculation of a result
   * @param arnFile path of arn file
   * @param mitoFile path of mito file
   * @param dirFile path of results files
   */
  static void launchAnalysis(final String arnFile, final String mitoFile,
      final String dirFile) {

    mainw.launchAnalysis(null, arnFile, mitoFile, dirFile);
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
  // Utility methods
  //

  /**
   * Main method.
   * @param application args
   */
  public static void main(final String[] args) {

    QApplication.initialize(args);
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

    // mainWindowUi.actionAbout_Corsen.triggered.connect(this, "about()");
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
        "openMessengers()");
    mainWindowUi.actionOpen_particlesB.triggered.connect(this, "openMitos()");
    mainWindowUi.actionOpen_a_directory.triggered.connect(this,
        "openDirectory()");
    mainWindowUi.action_Start_analysis.triggered.connect(this,
        "launchAnalysis()");

    mainWindowUi.launchAnalysisPushButton.clicked.connect(this,
        "launchAnalysis()");
    mainWindowUi.launch3DViewPushButton.clicked.connect(this, "launch3DView()");
    mainWindowUi.action_Quit.triggered.connect(this, "quit()");

    mainWindowUi.updateViewPushButton.clicked.connect(this,
        "updateVisualisation()");
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
    mainWindowUi.historyClearPushButton.clicked.connect(this,
        "clearHistoryResults()");
    mainWindowUi.historySaveResultsPushButton.clicked.connect(this,
        "saveHistoryResults()");
    mainWindowUi.action_Copy.triggered.connect(this, "copy()");

    setWidgetTexts();

    initResultTab();

    setWindowIcon(new QIcon("classpath:corsen-logo.png"));

    endProcess(null);
    setStartEnable(true);

  }
}
