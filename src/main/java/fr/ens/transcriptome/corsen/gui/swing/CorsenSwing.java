package fr.ens.transcriptome.corsen.gui.swing;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import fr.ens.transcriptome.corsen.CorsenCore;
import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.ProgressEvent;
import fr.ens.transcriptome.corsen.UpdateStatus;
import fr.ens.transcriptome.corsen.ProgressEvent.ProgressEventType;
import fr.ens.transcriptome.corsen.calc.CorsenResult;

public class CorsenSwing implements UpdateStatus {

  // Windows Look and Feel
  private static final String WINDOWS_PLAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

  private static final CorsenSwing corsen = new CorsenSwing();
  private CorsenUI gui;

  private class StatusInfo {

    int currentFile;
    int maxFiles;
    int currentPhase;
    int phaseIndex;
    int phaseEnd;
    long phaseStart;
    long cellStart;
    long initStart;

  }

  private final StatusInfo status = new StatusInfo();

  //
  // Getter
  //

  /**
   * Get the singleton of the The class.
   * @return the unique instance of CorsenSwing
   */
  /*
   * public static final CorsenSwing getCorsen() { return corsen; }
   */

  //
  //
  //
  /**
   * Show the application.
   */
  public void show() {

    this.gui.show();
    if (isWindowsSystem())
      setLookAndFeel(WINDOWS_PLAF);

  }

  /**
   * Show an error message.
   * @param msg Message to display
   */
  public void showError(final String msg) {

    if (this.gui != null && msg != null)
      this.gui.showError(msg);
  }

  /**
   * Show a message.
   * @param msg Message to display
   */
  public void showMessage(final String msg) {

    if (this.gui != null && msg != null)
      JOptionPane.showMessageDialog(this.gui, msg);
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private CorsenSwing() {

    final FileFilter ffPar = new FileFilter() {
      public boolean accept(final File f) {

        if (f.isDirectory())
          return true;
        if (f.getName().length() < 4)
          return false;
        String end = f.getName().substring(f.getName().length() - 4);

        return end.toLowerCase().endsWith(".par");
      }

      public String getDescription() {
        return Globals.APP_NAME + " ImageJ plugin result file";
      }
    };

    this.gui = new CorsenUI(ffPar);
    this.gui.setZCoef(Globals.Z_COEF_DEFAULT);
    this.gui.setPixelSize(Globals.PIXEL_SIZE_DEFAULT);

    this.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.gui.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent e) {

        final CorsenCore cc = new CorsenCore();

        cc.getSettings().setZFactor(CorsenSwing.this.gui.getZCoef());
        cc.getSettings().setFactor(CorsenSwing.this.gui.getPixelSize());
        cc.setUpdateStatus(CorsenSwing.this);

        if (CorsenSwing.this.gui.getDirFile() != null) {

          cc.setDirFiles(CorsenSwing.this.gui.getDirFile());
          cc.setMultipleFiles(true);

          new Thread(cc).start();
          // SwingUtilities.invokeLater(cc);

        } else if (CorsenSwing.this.gui.getARNFile() == null
            || CorsenSwing.this.gui.getMitoFile() == null) {

          if (CorsenSwing.this.gui.getARNFile() == null)
            CorsenSwing.this.gui.showError("No messager file specified.");
          else
            CorsenSwing.this.gui.showError("No mito file specified.");

        } else {
          final JFileChooser jfc = new JFileChooser();
          if (CorsenSwing.this.gui.getCurrentDirectory() != null)
            jfc.setCurrentDirectory(CorsenSwing.this.gui.getCurrentDirectory());

          final int result = jfc.showSaveDialog(CorsenSwing.this.gui);
          if (result == JFileChooser.APPROVE_OPTION) {

            cc.setParticlesBFile(CorsenSwing.this.gui.getMitoFile());
            cc.setParticlesAFile(CorsenSwing.this.gui.getARNFile());
            cc.setResultFile(jfc.getSelectedFile());
            cc.setMultipleFiles(false);

            new Thread(cc).start();
            // SwingUtilities.invokeLater(cc);

          }
        }

      }

    });

  }

  //
  // Progress status
  //

  /**
   * Send corsen result at the end of the process.
   * @param result The corsen result
   */
  public void endProcess(CorsenResult result)  {
    
  }
  
  /**
   * Update the status bar.
   * @param e event to show
   */
  public void updateStatus(final ProgressEvent e) {

    if (e == null)
      return;

    switch (e.getType()) {

    case START_CELL_EVENT:
      final long last = this.status.cellStart;
      this.status.cellStart = System.currentTimeMillis();
      this.status.currentFile = e.getIntValue1();
      this.status.maxFiles = e.getIntValue2();
      if (this.status.currentFile == 1) {
        this.gui.setStartEnable(false);
        this.status.initStart = this.status.cellStart;
        this.status.phaseStart = 0;
      } else
        System.out.println("\n---\n\nProcess cell in "
            + (this.status.currentFile - last));
      break;

    case START_READ_MESSENGERS_EVENT:
    case START_READ_MITOS_EVENT:
    case START_CHANGE_MESSENGERS_COORDINATES_EVENT:
    case START_CHANGE_MITOS_COORDINATES_EVENT:
    case START_CALC_MESSENGERS_CUBOIDS_EVENT:
    case START_CALC_MITOS_CUBOIDS_EVENT:
    case START_CALC_MIN_DISTANCES_EVENT:
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

      this.status.phaseStart = System.currentTimeMillis();
      this.status.currentPhase = e.getType().ordinal();
      this.status.phaseIndex = 0;
      this.status.phaseEnd = 0;

      break;

    case PROGRESS_CALC_MESSENGERS_CUBOIDS_EVENT:
    case PROGRESS_CALC_MITOS_CUBOIDS_EVENT:

      this.status.phaseIndex = e.getIntValue1();
      break;

    case END_CELLS_SUCCESSFULL_EVENT:
      final long end = System.currentTimeMillis();
      System.out.println("Finish in " + (end - this.status.initStart) + " ms");
      this.gui.setStartEnable(true);
      this.gui.setStatusMessage("Ready");
      return;

    case END_ERROR_EVENT:
      this.gui.setStartEnable(true);
      this.gui.setStatusMessage("Ready");
      return;

    default:
      return;
    }

    final StringBuffer sb = new StringBuffer();
    sb.append("Cell: ");
    sb.append(this.status.currentFile);
    sb.append("/");
    sb.append(this.status.maxFiles);
    sb.append(" Phase: ");

    sb.append(this.status.currentPhase);
    sb.append("/11 (");
    sb.append(e.getType().toString());

    if (this.status.currentPhase == ProgressEventType.START_CALC_MESSENGERS_CUBOIDS_EVENT.ordinal()
        || this.status.currentPhase == ProgressEventType.START_CALC_MITOS_CUBOIDS_EVENT.ordinal()) {
      sb.append(" ");
      sb.append((double) this.status.phaseIndex / 10);
      sb.append("%");
    }

    sb.append(")");

    this.gui.setStatusMessage(sb.toString());
    // System.out.println("[+" + e.getId() + "]\t" + sb.toString());

    if (false)
      top();

  }

  

  /**
   * Test if the system is Windows.
   * @return true if the operating systeme is Windows.
   */
  public static boolean isWindowsSystem() {
    return System.getProperty("os.name").toLowerCase().startsWith("windows");
  }

  /**
   * Set a look and feel for the application.
   * @param uiClassName Class name of the look and feel
   */
  private static void setLookAndFeel(final String uiClassName) {

    if (uiClassName == null)
      return;

    if (!javax.swing.SwingUtilities.isEventDispatchThread())
      try {
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            setLookAndFeel(uiClassName);
          }
        });
      } catch (final InterruptedException e) {
        System.err
            .println("Error when trying to use the Event dispatch thread.");
      } catch (final InvocationTargetException e) {
        System.err
            .println("Error when trying to use the Event dispatch thread.");
      }
    else
      try {

        // UIManager.setLookAndFeel(uiClassName);

        final Class c = CorsenSwing.class.getClassLoader().loadClass(uiClassName);
        final LookAndFeel laf = (LookAndFeel) c.newInstance();

        UIManager.setLookAndFeel(laf);

      } catch (final ClassNotFoundException e) {
        System.err.println("PLAF error, class not found: " + uiClassName);
        return;
      } catch (final InstantiationException e) {
        System.err.println("PLAF error, instantiation exception: "
            + uiClassName);
        return;
      } catch (final IllegalAccessException e) {
        System.err.println("PLAF error, illegal access: " + uiClassName);
        return;
      } catch (final UnsupportedLookAndFeelException e) {
        System.err.println("PLAF error, unssopported look and feel: "
            + uiClassName);
        return;
      }

  }

  /**
   * Show information about the memory and the running threads.
   */
  private static void top() {

    System.out.print("Free mem: "
        + String.valueOf(Runtime.getRuntime().freeMemory() / 1024));
    System.out.print(" Kb\tTot  mem: "
        + String.valueOf(Runtime.getRuntime().totalMemory() / 1024));
    System.out.println(" Kb\t Thread n: "
        + String.valueOf(Thread.activeCount()));
  }

  //
  // Main Method
  //

  /**
   * Main method.
   * @param args Command line arguments
   * @throws IOException if an error occurs while reading or writing a file
   */
  public static void main(final String[] args) throws IOException {

    JFrame.setDefaultLookAndFeelDecorated(true);
    Toolkit.getDefaultToolkit().setDynamicLayout(true);

    new CorsenSwing().show();
  }

}
