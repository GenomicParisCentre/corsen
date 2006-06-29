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

public class Corsen {

  public static final String APP_NAME = "Corsen";
  public static final String APP_VERSION = "0.14";

  // Windows Look and Feel
  private static final String WINDOWS_PLAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

  private static final float Z_COEF_DEFAULT = 2.96f;
  private static final float LEN_CUBE_DEFAULT = 2.5f;
  private static final float PIXEL_SIZE_DEFAULT = 1.0f;

  private static Corsen corsen = new Corsen();
  private CorsenUI gui;

  private class StatusInfo {

    public int currentFile;
    public int maxFiles;
    public int currentPhase;
    public int phaseIndex;
    public int phaseEnd;
    public long phaseStart;
    public long cellStart;
    public long initStart;

  }

  private StatusInfo status = new StatusInfo();

  //
  // Getter
  //

  public static final Corsen getCorsen() {

    return corsen;
  }

  //
  //
  //

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
  private Corsen() {

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
        return APP_NAME + " ImageJ plugin result file";
      }
    };

    this.gui = new CorsenUI(ffPar);
    this.gui.setZCoef(Z_COEF_DEFAULT);
    this.gui.setCubeLen(LEN_CUBE_DEFAULT);
    this.gui.setPixelSize(PIXEL_SIZE_DEFAULT);
    

    this.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    gui.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        CorsenCore cc = new CorsenCore();

        cc.setLenCuboid(gui.getCubeLen());
        cc.setZCoef(gui.getZCoef());
        cc.setUpdateZ(gui.isUpdateZ());
        cc.setPixelSize(gui.getPixelSize());

        if (gui.getDirFile() != null) {

          cc.setDirFiles(gui.getDirFile());
          cc.setMultipleFiles(true);

          new Thread(cc).start();
          // SwingUtilities.invokeLater(cc);

        } else if (gui.getARNFile() == null || gui.getMitoFile() == null) {

          if (gui.getARNFile() == null)
            gui.showError("No messager file specified.");
          else
            gui.showError("No mito file specified.");

        } else {
          JFileChooser jfc = new JFileChooser();
          if (gui.getCurrentDirectory() != null)
            jfc.setCurrentDirectory(gui.getCurrentDirectory());

          int result = jfc.showSaveDialog(gui);
          if (result == JFileChooser.APPROVE_OPTION) {

            cc.setMitoFile(gui.getMitoFile());
            cc.setRnaFile(gui.getARNFile());
            cc.setResultDir(jfc.getSelectedFile().getParentFile());
            cc.setResultFilename(jfc.getSelectedFile().getName());
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

  public void updateStatus(final ProgressEvent e) {

    if (e == null)
      return;

    switch (e.getId()) {

    case ProgressEvent.START_CELL_EVENT:
      long last = this.status.cellStart;
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

    case ProgressEvent.START_READ_MESSENGERS_EVENT:
    case ProgressEvent.START_READ_MITOS_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_MESSENGERS_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_MITOS_EVENT:
    case ProgressEvent.START_WRITE_RPLOT_CUBOIDS_EVENT:
    case ProgressEvent.START_CALC_CUBOIDS_EVENT:
    case ProgressEvent.START_WRITE_FULLRESULT_MESSAGERS_EVENT:
    case ProgressEvent.START_WRITE_FULLRESULT_CUBOIDS_EVENT:
    case ProgressEvent.START_WRITE_RRESULT_CUBOIDS_EVENT:

      // long lastPhase = this.status.phaseStart;
      this.status.phaseStart = System.currentTimeMillis();
      this.status.currentPhase = e.getId();
      this.status.phaseIndex = 0;
      this.status.phaseEnd = 0;
      /*
       * if (lastPhase != 0) { System.out.println("Last phase (#" +
       * this.status.currentPhase + ") in " + (this.status.phaseStart -
       * lastPhase) + " ms\n"); top(); }
       */

      break;

    case ProgressEvent.PROGRESS_CALC_CUBOIDS_EVENT:
      this.status.phaseIndex = e.getIntValue1();
      break;

    case ProgressEvent.END_SUCCESSFULL_EVENT:
      long end = System.currentTimeMillis();
      System.out.println("Finish in" + (this.status.initStart - end) + " ms");
      this.gui.setStartEnable(true);
      this.gui.setStatusMessage("Ready");
      return;

    case ProgressEvent.END_ERROR_EVENT:
      this.gui.setStartEnable(true);
      this.gui.setStatusMessage("Ready");
      return;

    default:
      return;
    }

    StringBuffer sb = new StringBuffer();
    sb.append("Cell: ");
    sb.append(this.status.currentFile);
    sb.append("/");
    sb.append(this.status.maxFiles);
    sb.append(" Phase: ");
    sb.append(this.status.currentPhase);
    sb.append("/9 (");
    sb.append(getPhaseName(this.status.currentPhase));

    if (this.status.currentPhase == ProgressEvent.START_CALC_CUBOIDS_EVENT) {
      sb.append(" ");
      sb.append((double) this.status.phaseIndex / 10);
      sb.append("%");
    }

    sb.append(")");

    this.gui.setStatusMessage(sb.toString());
    // System.out.println(sb.toString());

    if (false)
      top();

  }

  private static String getPhaseName(int phase) {

    switch (phase) {
    case ProgressEvent.START_READ_MESSENGERS_EVENT:
      return "Read messengers PAR file";
    case ProgressEvent.START_READ_MITOS_EVENT:
      return "Read mitos PAR file";
    case ProgressEvent.START_WRITE_RPLOT_MESSENGERS_EVENT:
      return "Write messengers R plot file";
    case ProgressEvent.START_WRITE_RPLOT_MITOS_EVENT:
      return "Write mitos R plot file";
    case ProgressEvent.START_WRITE_RPLOT_CUBOIDS_EVENT:
      return "Write cuboids R plot file";
    case ProgressEvent.START_CALC_CUBOIDS_EVENT:
      return "Calc cuboids";
    case ProgressEvent.START_WRITE_FULLRESULT_MESSAGERS_EVENT:
      return "Write full results for messengers";
    case ProgressEvent.START_WRITE_FULLRESULT_CUBOIDS_EVENT:
      return "Write full results for cuboids";
    case ProgressEvent.START_WRITE_RRESULT_CUBOIDS_EVENT:
      return "Write results data file for R";

    default:
      return "";
    }

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

    if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
      try {
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            setLookAndFeel(uiClassName);
          }
        });
      } catch (InterruptedException e) {
        System.err
            .println("Error when trying to use the Event dispatch thread.");
      } catch (InvocationTargetException e) {
        System.err
            .println("Error when trying to use the Event dispatch thread.");
      }

    } else {

      try {

        // UIManager.setLookAndFeel(uiClassName);

        Class c = Corsen.class.getClassLoader().loadClass(uiClassName);
        LookAndFeel laf = (LookAndFeel) c.newInstance();

        UIManager.setLookAndFeel(laf);

      } catch (ClassNotFoundException e) {
        System.err.println("PLAF error, class not found: " + uiClassName);
        return;
      } catch (InstantiationException e) {
        System.err.println("PLAF error, instantiation exception: "
            + uiClassName);
        return;
      } catch (IllegalAccessException e) {
        System.err.println("PLAF error, illegal access: " + uiClassName);
        return;
      } catch (UnsupportedLookAndFeelException e) {
        System.err.println("PLAF error, unssopported look and feel: "
            + uiClassName);
        return;
      }

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

    

    getCorsen().show();
  }

}
