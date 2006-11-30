package fr.ens.transcriptome.corsen.gui.qt;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QDesktopServices;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QAbstractItemView.SelectionBehavior;
import com.trolltech.qt.gui.QAbstractItemView.SelectionMode;
import com.trolltech.qt.gui.QFileDialog.FileMode;

import fr.ens.transcriptome.corsen.CorsenCore;
import fr.ens.transcriptome.corsen.CorsenResult;
import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;

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

// TODO add an in phase progress for writing/computing distances 
// TODO Remove the cancel button 
// TODO When alt-f4 -> System.exit(0)
public class CorsenQt extends QMainWindow {

  private Ui_CorsenMainWindow mainWindowUi = new Ui_CorsenMainWindow();

  private DataModelQt models = new DataModelQt();
  private Settings settings = new Settings();
  private String lastDir = Globals.DEBUG_HOME_DIR ? "/home/jourdren/Desktop/atp16"
      : "";

  private class UpdateStatusQt extends QObject implements UpdateStatus {

    public QObject.Signal1<ProgressEvent> statusSignal = new QObject.Signal1<ProgressEvent>();
    public QObject.Signal1<String> messageSignal = new QObject.Signal1<String>();
    public QObject.Signal1<String> errorSignal = new QObject.Signal1<String>();
    public QObject.Signal1<CorsenResult> resultSignal = new QObject.Signal1<CorsenResult>();

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
  }

  private class StatusInfo {

    int currentCellToProcess;
    int cellToProcessCount;
    int currentPhase;
    int nbPhaseDone;
    int maxPhase;
    int indexInPhase;
    long timeStartPhase;
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

  /**
   * Show About dialog box.
   */
  @SuppressWarnings("unused")
  private void about() {
    QMessageBox
        .about(
            this,
            tr("About " + Globals.APP_NAME),
            tr("<p><b>"
                + Globals.APP_NAME
                + " version "
                + Globals.APP_VERSION
                + "</b> is a software to calc the distances between "
                + "mitochondria and mRNA.</p><br/>"
                + "<b>Authors</b>:"
                + "<ul><li><a href=\"mailto:jourdren@biologie.ens.fr?subject=Corsen\">Laurent Jourdren</a><br/>"
                + "<a href=\"http://transcriptome.ens.fr\">Microarray platform, École Normale Supérieure</a>"
                + "<br/>Main developer, maintener.</li><br/>"
                + "<li><a href=\"mailto:garcia@biologie.ens.fr?subject=Corsen\">Mathilde Garcia</a><br/>"
                + "<a href=\"http://www.biologie.ens.fr/lgmgml\">Laboratoire de Génétique Moléculaire, École Normale Supérieure</a>"
                + "<br/>Project leader, R programming, ImageJ scripting, testing.</li></ul>"
                + "<p>Copyright 2006 École Normale Supérieure.<br/>"
                + "This program is developed under the GNU General Public Licence.</p>"));
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

    mainWindowUi.messengerPathLabel.setText(text);
  }

  private void setMitoPathLabelText(String text) {

    mainWindowUi.mitoPathLabel.setText(text);
  }

  private void setDirectoryPathLabelText(String text) {

    mainWindowUi.directoryPathLabel.setText(text);
  }

  /**
   * Set the messenger path.
   */
  @SuppressWarnings("unused")
  private void openMessengers() {

    String fileName = QFileDialog.getOpenFileName(this, "Set messengers file",
        this.lastDir, "Particles file (*.par)");
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

    String fileName = QFileDialog.getOpenFileName(this,
        "Set mitochondria file", this.lastDir, "Particles file (*.par)");
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

    String fileName = QFileDialog.getSaveFileName(this, "Save result",
        this.lastDir, "Result file (*" + ext + ")");
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
    this.models.setResult(result);
    this.resultViewChanged(new Integer(this.mainWindowUi.resultViewComboBox
        .currentIndex()));

  }

  /**
   * Update visalisation.
   */
  @SuppressWarnings("unused")
  private void updateVisualisation() {

    System.out.println("update visualisation");

    final ViewOGL v = mainWindowUi.viewOGL;

    // v.setMitosColor(null);
    // v.setMessengersColor(null);
    // v.setBarycenterColor(null);
    // v.setDistanceColor(null);

    v.setDrawBaryCenter(mainWindowUi.showBarycentersCheckBox.isChecked());
    v.setDrawDistances(mainWindowUi.showDistancesCheckBox.isChecked());
    v.setDrawMessengersCuboids(mainWindowUi.messengersCuboidsRadioButton
        .isChecked());
    v.setDrawMitosCuboids(mainWindowUi.mitosCuboidsRadioButton.isChecked());
    v.setDrawDistances(mainWindowUi.showDistancesCheckBox.isChecked());

    v.setRemakeObject(true);
    v.repaint();
  }

  /**
   * Launch.
   */
  @SuppressWarnings("unused")
  private void launch() {

    mainWindowUi.viewOGL.clear();
    this.models.setResult(null);
    this.resultViewChanged(new Integer(this.mainWindowUi.resultViewComboBox
        .currentIndex()));

    final CorsenCore cc = new CorsenCore();

    cc.setSettings(this.settings);
    UpdateStatusQt updateStatus = new UpdateStatusQt();

    cc.setUpdateStatus(updateStatus);
    updateStatus.errorSignal.connect(this, "showError(String)");
    updateStatus.messageSignal.connect(this, "showMessage(String)");
    updateStatus.statusSignal.connect(this, "updateStatus(ProgressEvent)");
    updateStatus.resultSignal.connect(this, "endProcess(CorsenResult)");

    String dirFile = mainWindowUi.directoryPathLabel.text();
    String arnFile = mainWindowUi.messengerPathLabel.text();
    String mitoFile = mainWindowUi.mitoPathLabel.text();

    if (dirFile.length() > 0) {

      cc.setDirFiles(new File(dirFile));
      cc.setMultipleFiles(true);

      Thread t = new Thread(cc);
      t.start();

    } else if (arnFile.length() == 0 || mitoFile.length() == 0) {

      if (arnFile.length() == 0)
        showError("No messager file specified.");
      else
        showError("No mito file specified.");

    } else {

      // final JFileChooser jfc = new JFileChooser();
      QFileDialog dialog = new QFileDialog(this);
      dialog.setWindowTitle("Set output files prefix");
      // if (dirFile.length() > 0)
      // jfc.setCurrentDirectory(Corsen.this.gui.getCurrentDirectory());
      dialog.setDirectory(this.lastDir);

      if (dialog.exec() == QDialog.DialogCode.Accepted.value()) {

        List<String> fileNames = dialog.selectedFiles();

        File f = new File(fileNames.get(0));
        String prefixFilename = f.getAbsolutePath();
        // String prefixFilename = f.getName();
        mainWindowUi.outputFilesPathLabel.setText(prefixFilename);

        cc.setMitoFile(new File(mitoFile));
        cc.setRnaFile(new File(arnFile));
        cc.setResultFile(new File(prefixFilename));
        cc.setMultipleFiles(false);

        new Thread(cc).start();
        // SwingUtilities.invokeLater(cc);

      }

    }

  }

  /**
   * Enable the start button
   * @param value The state of activation of the start button
   */
  private void setStartEnable(final boolean value) {

    this.mainWindowUi.launchButton.setEnabled(value);
    this.mainWindowUi.updateViewPushButton.setEnabled(value);
    this.mainWindowUi.viewOGL.setRemakeObject(value);
    if (value == true && this.models.getResult() != null)
      this.mainWindowUi.saveResultPushButton.setEnabled(true);
    else
      this.mainWindowUi.saveResultPushButton.setEnabled(false);

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

    final int value = (currentCell - 1) * maxPhase
        * ProgressEvent.INDEX_IN_PHASE_MAX + (phase - 1)
        * ProgressEvent.INDEX_IN_PHASE_MAX + indexInPhase;

    if (false)
      showStatusMessage("Phase=" + phase + "/" + maxPhase + " index="
          + indexInPhase + " val=" + value + "/" + max);

    mainWindowUi.progressBar.setMinimum(0);
    mainWindowUi.progressBar.setMaximum(max);
    mainWindowUi.progressBar.setValue(value);
  }

  @SuppressWarnings("unused")
  private void quit() {

    QApplication.exit();

  }

  @SuppressWarnings("unused")
  public void configureDialog() {

    CorsenConfigureQt cc = new CorsenConfigureQt(this, this.settings);
    cc.configureDialog();

  }

  @SuppressWarnings("unused")
  private void resultViewChanged(Object o) {

    int i = ((Integer) o).intValue();
    mainWindowUi.resultTableView.setModel(this.models.getModel(i));

    mainWindowUi.resultTableView.setAlternatingRowColors(true);
    mainWindowUi.resultTableView.setSortingEnabled(true);
    mainWindowUi.resultTableView.sortByColumn(1, Qt.SortOrder.AscendingOrder);

  }

  public void closeEvent(QCloseEvent event) {

    quit();
    System.exit(0);
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

    switch (e.getId()) {

    case ProgressEvent.START_CELLS_EVENT:

      setStartEnable(false);
      this.status.timeStartCells = System.currentTimeMillis();

      this.status.maxPhase = e.getIntValue1();
      setMessengerPathLabelText("");
      setMitoPathLabelText("");
      clearStatusMessage();
      this.status.currentPhase = 0;

      break;

    case ProgressEvent.START_CELL_EVENT:

      this.status.timeStartCell = System.currentTimeMillis();
      this.status.currentCellToProcess = e.getIntValue1();
      this.status.cellToProcessCount = e.getIntValue2();
      this.status.mitoFilePath = e.getStringValue1();
      this.status.rnaFilePath = e.getStringValue2();
      this.status.resultFilePath = e.getStringValue3();
      setMessengerPathLabelText(this.status.rnaFilePath);
      setMitoPathLabelText(this.status.mitoFilePath);
      this.status.currentPhase = 0;
      this.status.nbPhaseDone = 0;

      showProgressMessage("Progress: " + this.status.currentCellToProcess
          + " of " + this.status.cellToProcessCount + " cells");

      showStatusMessage("Process cell " + this.status.currentCellToProcess
          + " of " + this.status.cellToProcessCount + " cells");
      showStatusMessage("Mitochondria particles file: "
          + this.status.mitoFilePath);
      showStatusMessage("Messengers particles file: " + this.status.rnaFilePath);
      showStatusMessage("Output files prefix: " + this.status.resultFilePath);

      break;

    case ProgressEvent.START_READ_MESSENGERS_EVENT:
    case ProgressEvent.START_READ_MITOS_EVENT:
    case ProgressEvent.START_CHANGE_Z_COORDINATES_EVENT:
    case ProgressEvent.START_CHANGE_ALL_COORDINATES_EVENT:
    case ProgressEvent.START_CALC_MESSENGERS_CUBOIDS_EVENT:
    case ProgressEvent.START_CALC_MITOS_CUBOIDS_EVENT:
    case ProgressEvent.START_CALC_MIN_DISTANCES_EVENT:
    case ProgressEvent.START_CALC_MAX_DISTANCES_EVENT:
    case ProgressEvent.START_WRITE_DATA_EVENT:
    case ProgressEvent.START_WRITE_IV_MESSENGERS_EVENT:
    case ProgressEvent.START_WRITE_IV_MITOS_EVENT:
    case ProgressEvent.START_WRITE_IV_MESSENGERS_CUBOIDS_EVENT:
    case ProgressEvent.START_WRITE_FULLRESULT_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_MESSENGERS_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_MITOS_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_MESSENGERS_CUBOIDS_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_MITOS_CUBOIDS_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_DISTANCES_EVENT:

      this.status.timeStartPhase = System.currentTimeMillis();
      this.status.currentPhase = e.getId();
      this.status.indexInPhase = 0;
      this.status.nbPhaseDone++;
      showCurrentPhase();

      break;

    case ProgressEvent.PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT:
    case ProgressEvent.PROGRESS_CALC_MITOS_CUBOIDS_EVENT:
    case ProgressEvent.PROGRESS_CALC_MIN_DISTANCES_EVENT:
    case ProgressEvent.PROGRESS_CALC_MAX_DISTANCES_EVENT:

      this.status.indexInPhase = e.getIntValue1();

      break;

    case ProgressEvent.END_CELL_EVENT:
      final long timeEndCell = System.currentTimeMillis();
      showStatusMessage("Process current cell in "
          + toTimeHumanReadable(timeEndCell - this.status.timeStartCell)
          + ".\n");

      endEvent = true;

      break;

    case ProgressEvent.END_CELLS_SUCCESSFULL_EVENT:
      final long timeEndCells = System.currentTimeMillis();
      showStatusMessage("Process all cells in "
          + toTimeHumanReadable(timeEndCells - this.status.timeStartCells)
          + ".");
      CorsenQt.this.setStartEnable(true);

      endEvent = true;

      break;

    case ProgressEvent.END_ERROR_EVENT:
      setStartEnable(true);
      showStatusMessage("Error !!!");
      return;

    default:
      return;
    }

    if (this.status.currentPhase != 0)
      showProgressBarProgress(this.status.currentCellToProcess,
          this.status.cellToProcessCount, this.status.nbPhaseDone,
          this.status.maxPhase, endEvent ? ProgressEvent.INDEX_IN_PHASE_MAX
              : this.status.indexInPhase);

  }

  private void showCurrentPhase() {

    final StringBuffer sb = new StringBuffer();
    sb.append(" Phase: ");

    sb.append(this.status.nbPhaseDone);
    sb.append("/");
    sb.append(this.status.maxPhase);
    sb.append(" (");
    sb.append(ProgressEvent.getPhaseName(this.status.currentPhase));
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

  //
  // Init tabs methods
  //

  private void initResultTab() {

    int n = this.models.getViewCount();

    for (int i = 0; i < n; i++)
      mainWindowUi.resultViewComboBox.addItem(
          this.models.getViewDescription(i), new Integer(i));

    mainWindowUi.resultViewComboBox.currentIndexChanged.connect(this,
        "resultViewChanged(Object)");

    resultViewChanged(new Integer(0));

    mainWindowUi.saveResultPushButton.clicked.connect(this, "saveResultFile()");

  }

  //
  // Utility methods
  //

  private String toTimeHumanReadable(final long time) {

    DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.UK);

    return df.format(new Date(time));
  }

  //
  // Main method
  //

  /**
   * Main method.
   * @param application args
   */
  public static void main(final String[] args) {
    QApplication.initialize(args);
    CorsenQt mainw = new CorsenQt();
    mainw.show();
    QApplication.exec();
  }

  //
  // Constructor
  //

  public CorsenQt() {

    // load settings
    try {
      this.settings.loadSettings();
    } catch (IOException e) {
    }

    // Place what you made in Designer onto the main window.
    mainWindowUi.setupUi(this);
    setWindowTitle(Globals.APP_NAME + " " + Globals.APP_VERSION);

    mainWindowUi.actionAbout_Corsen.triggered.connect(this, "about()");
    mainWindowUi.actionCorsen_Website.triggered.connect(this, "openWebsite()");
    mainWindowUi.actionCorsen_Handbook.triggered
        .connect(this, "openHandbook()");
    mainWindowUi.action_Report_Bug.triggered.connect(this, "reportBug()");

    mainWindowUi.action_Configure_Corsen.triggered.connect(this,
        "configureDialog()");
    mainWindowUi.actionSave_settings.triggered.connect(this, "saveSettings()");
    mainWindowUi.actionOpen_messenger_particle.triggered.connect(this,
        "openMessengers()");
    mainWindowUi.actionOpen_mi_tochondrial_particle.triggered.connect(this,
        "openMitos()");
    mainWindowUi.actionOpen_a_directory.triggered.connect(this,
        "openDirectory()");
    mainWindowUi.action_Start_analysis.triggered.connect(this, "launch()");

    mainWindowUi.launchButton.clicked.connect(this, "launch()");
    mainWindowUi.action_Quit.triggered.connect(this, "quit()");
    mainWindowUi.updateViewPushButton.clicked.connect(this,
        "updateVisualisation()");

    initResultTab();

    setWindowIcon(new QIcon("classpath:files/corsen-logo.png"));

    setStartEnable(true);

  }

}
