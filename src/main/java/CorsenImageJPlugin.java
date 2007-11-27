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

import fr.ens.transcriptome.corsen.Globals;
import fr.ens.transcriptome.corsen.imagej.CorsenImageJUtil;
import fr.ens.transcriptome.corsen.imagej.Segmentation2DRunner;
import fr.ens.transcriptome.corsen.imagej.Segmentation3DRunner;
import fr.ens.transcriptome.corsen.model.ListPoint2DFactory;
import fr.ens.transcriptome.corsen.model.ListPoint3DFactory;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;
import fr.ens.transcriptome.corsen.util.Util;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.io.FileInfo;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * This class implements the ImageJ Plugin main class.
 * @author Laurent Jourdren
 */
public class CorsenImageJPlugin implements PlugInFilter {

  /** Display results in the ImageJ console. */
  public static final int SHOW_RESULTS = 1;

  /** Do not measure particles touching edge of image. */
  public static final int EXCLUDE_EDGE_PARTICLES = 2;

  /** Change output file name. */
  public static final int CHANGE_OUTPUT_FILENAME = 4;

  /** No confirm save dialog. */
  public static final int NO_CONFIRM_SAVE_DIALOG = 8;

  /** Show Particles 3D. */
  public static final int SHOW_PARTICLES_3D = 16;

  /** Use packed list of points. */
  public static final int PACK_PARTICLES_DATA = 32;

  private static final String OPTIONS = Globals.APP_NAME + ".options";
  private static int staticOptions = Prefs.getInt(OPTIONS, 0);

  private ImagePlus imp;
  private int options;
  private boolean cancel;
  private int slice;
  private int nSlices;
  private int width, height;
  private ResultsTable rt = new ResultsTable();

  private Segmentation2DRunner seg2DRunner;
  private Segmentation3DRunner seg3DRunner = new Segmentation3DRunner();
  private List<Particle3D> particles3DToSave;

  /**
   * This method is called once when the filter is loaded. 'arg', which may be
   * blank, is the argument specified for this plugin in IJ_Props.txt. 'imp' is
   * the currently active image. This method should return a flag word that
   * specifies the filters capabilities.
   */
  public int setup(String arg, ImagePlus imp) {

    if (arg.equals("about")) {
      showAbout();
      return DONE;
    }

    IJ.register(CorsenImageJPlugin.class);
    if (imp == null) {
      IJ.noImage();
      return DONE;
    }

    this.imp = imp;
    this.nSlices = imp.getNSlices();

    if (!showDialog())
      return DONE;

    final int baseFlags =
        DOES_8G + DOES_16 + DOES_32 + NO_CHANGES + NO_UNDO + STACK_REQUIRED;
    final int flags = IJ.setupDialog(imp, baseFlags);

    slice = 0;
    this.seg3DRunner.clear();
    this.cancel = false;

    return flags;
  }

  /**
   * Filters use this method to process the image. If the SUPPORTS_STACKS flag
   * was set, it is called for each slice in a stack. ImageJ will lock the image
   * before calling this method and unlock it when the filter is finished.
   */
  public void run(final ImageProcessor ip) {

    if (cancel || !testThreshold(ip))
      return;

    this.slice++;
    this.width = ip.getWidth();
    this.height = ip.getHeight();

    if (slice == 1)
      initForANewStack();

    this.seg3DRunner.addParticles2DForSegmentation3D(this.seg2DRunner
        .getParticles2D(ip), slice, this.imp.getCalibration().pixelDepth,
        this.imp.getTitle());

    // Update Progress bar
    IJ.showProgress((double) this.slice / (double) this.nSlices);

    if (slice == this.nSlices)
      afterSegmentation();

  }

  private void initForANewStack() {

    IJ.showProgress(0.0);

    final boolean packedMode = (options & PACK_PARTICLES_DATA) != 0;

    ListPoint2DFactory.setPackedMode(packedMode);
    ListPoint3DFactory.setPackedMode(packedMode);

    // New Segmentation 2D Runner
    Calibration cal = this.imp.getCalibration();
    this.seg2DRunner =
        new Segmentation2DRunner(cal.pixelWidth, cal.pixelHeight);
  }

  private void afterSegmentation() {

    try {

      this.particles3DToSave =
          this.seg3DRunner
              .getParticlesToSave((options & EXCLUDE_EDGE_PARTICLES) != 0);

      // Show Summary
      if ((options & SHOW_RESULTS) != 0)
        showSummary();

      // Show Particle 3D
      if ((options & SHOW_PARTICLES_3D) != 0)
        showParticles3D(this.imp);

      particles3DResultsProcessor(this.imp);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (RuntimeException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this.imp.getWindow(), e.getMessage());
    }
  }

  private boolean testThreshold(final ImageProcessor ip) {

    final double t1 = ip.getMinThreshold();

    if (t1 == ImageProcessor.NO_THRESHOLD) {

      this.cancel = true;

      IJ.error(Globals.APP_NAME + " plugin",
          "A thresholded image is required.\n"
              + "Threshold levels can be set using\n"
              + "the Image->Adjust->Threshold tool.");

      return false;
    }

    return true;
  }

  /** Displays a modal options dialog. */
  public boolean showDialog() {

    GenericDialog gd = new GenericDialog(Globals.getWindowsTitle());

    options = staticOptions;

    String[] labels = new String[6];
    boolean[] states = new boolean[6];
    labels[0] = "Display Results";
    states[0] = (options & SHOW_RESULTS) != 0;
    labels[1] = "Exclude on Edges";
    states[1] = (options & EXCLUDE_EDGE_PARTICLES) != 0;
    labels[2] = "Change output file name";
    states[2] = (options & CHANGE_OUTPUT_FILENAME) != 0;
    labels[3] = "No confirm save dialog";
    states[3] = (options & NO_CONFIRM_SAVE_DIALOG) != 0;
    labels[4] = "Show Particles 3D";
    states[4] = (options & SHOW_PARTICLES_3D) != 0;
    labels[5] = "Pack Particle data";
    states[5] = (options & PACK_PARTICLES_DATA) != 0;

    gd.addCheckboxGroup(3, 2, labels, states);

    gd.showDialog();
    if (gd.wasCanceled())
      return false;

    if (gd.getNextBoolean())
      options |= SHOW_RESULTS;
    else
      options &= ~SHOW_RESULTS;

    if (gd.getNextBoolean())
      options |= EXCLUDE_EDGE_PARTICLES;
    else
      options &= ~EXCLUDE_EDGE_PARTICLES;

    if (gd.getNextBoolean())
      options |= CHANGE_OUTPUT_FILENAME;
    else
      options &= ~CHANGE_OUTPUT_FILENAME;

    if (gd.getNextBoolean())
      options |= NO_CONFIRM_SAVE_DIALOG;
    else
      options &= ~NO_CONFIRM_SAVE_DIALOG;

    if (gd.getNextBoolean())
      options |= SHOW_PARTICLES_3D;
    else
      options &= ~SHOW_PARTICLES_3D;

    if (gd.getNextBoolean())
      options |= PACK_PARTICLES_DATA;
    else
      options &= ~PACK_PARTICLES_DATA;

    staticOptions = options;

    return true;
  }

  /**
   * Show a stack with particles3D in colors
   * @param imp Original Image
   */
  private void showParticles3D(final ImagePlus imp) {

    final ImageStack stack = new ImageStack(imp.getWidth(), imp.getHeight());

    final int nSlices = imp.getNSlices();

    for (int i = 0; i < nSlices; i++) {

      final ImageProcessor drawIP = new ColorProcessor(width, height);
      drawIP.setColor(Color.white);
      drawIP.fill();
      stack.addSlice(null, drawIP);
    }

    int r = 0;
    int g = 125;
    int b = 255;

    for (Particle3D p : this.particles3DToSave) {

      CorsenImageJUtil.addParticle3DtoStack(stack, p, new Color(r, g, b));

      r += 5;
      g += 10;
      b += 15;

      if (r > 255)
        r -= 255;
      if (g > 255)
        g -= 255;
      if (b > 255)
        b -= 255;
    }

    new ImagePlus("Particles 3D found by " + Globals.APP_NAME, stack).show();

  }

  /**
   * Process Particles 3D results.
   * @param imp Original Image
   * @throws IOException if an error occurs while saving data
   */
  private void particles3DResultsProcessor(final ImagePlus imp)
      throws IOException {

    File file = null;

    if ((options & CHANGE_OUTPUT_FILENAME) != 0) {

      file = chooseResultFile(imp);

      if (file == null)
        return;

    } else {

      boolean writeFile = false;

      if ((options & NO_CONFIRM_SAVE_DIALOG) == 0) {

        final int response =
            JOptionPane.showConfirmDialog(imp.getWindow(),
                new String[] {"Save results ?"}, "Save results ?",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {

          writeFile = true;
        } else
          return;

      } else
        writeFile = true;

      if (writeFile) {
        FileInfo fi = imp.getOriginalFileInfo();

        if (fi == null) {

          final int response =
              JOptionPane.showConfirmDialog(imp.getWindow(), new String[] {
                  "No location is associated to the current image.",
                  "You select the result filename"}, "Warning",
                  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

          if (response == JOptionPane.NO_OPTION)
            return;

          file = chooseResultFile(imp);

        } else {

          String newName;

          int index = fi.fileName.indexOf(".");
          if (index == -1)
            newName = fi.fileName;
          else
            newName = fi.fileName.substring(0, index);

          file =
              new File(fi.directory, newName + Globals.EXTENSION_PARTICLES_FILE);
        }
      }

    }

    if (file != null)
      saveParticles3DFile(file, imp.getOriginalFileInfo());

    /*
     * final Particles3D mitosParticles = new Particles3D(file); RGL rgl = new
     * RGL(file.getParentFile(), file.getName() + ".R");
     * rgl.writeRPlots(mitosParticles, "red", false); rgl.close();
     */

  }

  /**
   * Choose the name of result file
   * @param imp Original Image
   * @return A file object
   */
  private File chooseResultFile(final ImagePlus imp) {

    JFileChooser chooser = new JFileChooser();

    final FileFilter ff = new FileFilter() {

      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }

        String extension = Util.getExtension(f);

        if (extension != null) {
          if (extension.equals(Globals.EXTENSION_PARTICLES_FILE))
            return true;

        }

        return false;
      }

      // The description of this filter
      public String getDescription() {
        return "Particle file (*.par)";
      }
    };

    chooser.setFileFilter(ff);

    int result = chooser.showSaveDialog(imp.getWindow());

    if (result == JFileChooser.APPROVE_OPTION)
      return chooser.getSelectedFile();

    return null;
  }

  /**
   * Save Particles 3D.
   * @param outputFile output file
   * @param fi File information about the image
   * @throws IOException if an error occurs while saving the output file
   */
  private void saveParticles3DFile(final File outputFile, final FileInfo fi)
      throws IOException {

    FileOutputStream fos = new FileOutputStream(outputFile);

    Particles3D particles = new Particles3D();

    File f = fi == null ? null : new File(fi.directory, fi.fileName);

    particles.setImageFilename(f != null ? f.getAbsolutePath() : null);
    particles.setImageFilenameDate(f != null
        ? new Date(f.lastModified()) : null);

    ImageProcessor ip = this.imp.getProcessor();
    Calibration cal = imp.getCalibration();

    particles.setWidth(this.imp.getWidth());
    particles.setHeight(this.imp.getHeight());
    particles.setZSlices(this.imp.getNSlices());

    particles.setPixelWidth((float) cal.pixelWidth);
    particles.setPixelHeight((float) cal.pixelHeight);
    particles.setPixelDepth((float) cal.pixelDepth);

    particles.setUnitOfLength(cal.getUnit());
    particles.setMinThreshold(ip.getMinThreshold());
    particles.setMaxThreshold(ip.getMaxThreshold());

    particles.setParticles(this.particles3DToSave);

    particles.saveParticles(fos);
  }

  private void showSummary() {

    this.rt.setHeading(0, "Internal id");
    this.rt.setHeading(1, "Area");
    this.rt.setHeading(2, "Volume");
    this.rt.setHeading(3, "Sphericity");
    this.rt.setHeading(4, "Intensity");
    this.rt.setHeading(5, "Density");

    int count = 0;
    for (Particle3D p : this.particles3DToSave) {

      this.rt.incrementCounter();
      this.rt.setValue(0, count, p.getId());
      this.rt.setValue(1, count, p.getArea());
      this.rt.setValue(2, count, p.getVolume());
      this.rt.setValue(3, count, p.getSphericty());
      this.rt.setValue(4, count, p.getIntensity());
      this.rt.setValue(5, count, p.getDesnity());

      count++;
    }

    rt.show("Summary of Particles 3D found by " + Globals.APP_NAME);

  }

  /** Called once when ImageJ quits. */
  public static void savePreferences(Properties prefs) {
    prefs.put(OPTIONS, Integer.toString(staticOptions));
  }

  public void savePreferences() {
    Prefs.set(OPTIONS, Integer.toString(staticOptions));
  }

  /**
   * Show information about the plugin.
   */
  private void showAbout() {

    IJ.showMessage(Globals.APP_NAME, Globals.ABOUT_TXT);
  }

}
