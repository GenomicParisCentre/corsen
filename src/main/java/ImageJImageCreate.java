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

import fr.ens.transcriptome.corsen.imagej.Segmentation2DRunner;
import fr.ens.transcriptome.corsen.imagej.Segmentation3DRunner;
import fr.ens.transcriptome.corsen.model.Particles3D;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class create test particles.
 * @author Laurent Jourdren
 */
public final class ImageJImageCreate {

  private static final int LEN = 50;
  private static final int INTENSITY = 100;
  private static final int MIN_THRESHOLD = 1;
  private static final int MAX_THRESHOLD = 2000;

  private static ImagePlus createHoriPoints(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    for (int k = 0; k < (LEN / step) + 1; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);

      if (k == 25 / step)
        for (int i = 1; i < 50; i = i + step)
          if ((i - 1) % (6 / step) == 0)
            ip.set(i, 25, INTENSITY);

      is.addSlice("" + k / step, ip);
    }

    ImagePlus ipl = new ImagePlus("hori", is);

    return ipl;
  }

  private static ImagePlus createVertPoints(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    for (int k = 0; k < ((LEN + 1) / step) + 1; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);

      if ((k - 1) % (6 / step) == 0)
        ip.set(25, 25, INTENSITY);

      is.addSlice("" + k, ip);
    }

    ImagePlus ipl = new ImagePlus("vert", is);

    return ipl;
  }

  private static ImagePlus create0ptImage(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    for (int k = 0; k < LEN / step; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);

      is.addSlice("" + k, ip);
    }

    ImagePlus ipl = new ImagePlus("0pt", is);

    return ipl;
  }

  private static ImagePlus create1ptImage(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    for (int k = 0; k < LEN / step; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);

      if (k == 24)
        ip.set(12, 12, INTENSITY);

      is.addSlice("" + k, ip);
    }

    ImagePlus ipl = new ImagePlus("1pt", is);

    return ipl;
  }

  private static ImagePlus createCubeImage(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    int count = 0;

    for (int k = 0; k < LEN / step; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);

      if (k > 12 / step && k <= 36 / step + 1)
        for (int i = 13; i < 38; i++)
          for (int j = 13; j < 38; j++) {
            ip.set(i, j, INTENSITY);
            count++;
          }

      is.addSlice("" + k, ip);
    }

    System.out.println("create a cube with " + count + " blocks.");
    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  private static ImagePlus createPyramidImage(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    int count = 0;

    for (int k = 0; k < LEN / step; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);
      for (int i = 0; i < k; i++)
        for (int j = 0; j < k; j++) {
          ip.set(i, j, INTENSITY);
          count++;
        }

      is.addSlice("" + k, ip);
    }

    System.out.println("create a pyramid with " + count + " blocks.");
    ImagePlus ipl = new ImagePlus("pyramid", is);

    return ipl;
  }

  private static ImagePlus createSolidImage(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    int count = 0;

    final int max = LEN / step / 2;

    for (int k = 0; k < LEN / step; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);
      for (int i = 0; i < max - k; i++)
        for (int j = 0; j < max + k; j++) {
          ip.set(i, j, INTENSITY);
          count++;
        }

      is.addSlice("" + k, ip);
    }

    System.out.println("create a solid with " + count + " blocks.");
    ImagePlus ipl = new ImagePlus("solid", is);

    return ipl;
  }

  private static Particles3D convertToParticles(final ImagePlus ip,
      float pixelWidth, final float pixelHeight, final float pixelDepth) {

    if (ip == null)
      return null;

    Segmentation2DRunner s2Dr =
        new Segmentation2DRunner(pixelWidth, pixelHeight);
    Segmentation3DRunner seg3DRunner = new Segmentation3DRunner();

    final int nSlices = ip.getNSlices();
    final ImageStack is = ip.getStack();

    for (int i = 0; i < nSlices; i++) {

      final ImageProcessor ipr = is.getProcessor(i + 1);

      ipr.setThreshold(MIN_THRESHOLD, MAX_THRESHOLD,
          ImageProcessor.NO_LUT_UPDATE);

      seg3DRunner.addParticles2DForSegmentation3D(s2Dr.getParticles2D(ipr), i,
          pixelDepth, ip.getTitle());
    }

    Particles3D pars = new Particles3D();
    pars.setParticles(seg3DRunner.getParticlesToSave());

    return pars;
  }

  private static void createFile(final ImagePlus ip, final int step,
      final String prefix) throws FileNotFoundException, IOException {

    ip.getProcessor().setThreshold(MIN_THRESHOLD, MAX_THRESHOLD,
        ImageProcessor.NO_LUT_UPDATE);
    Particles3D particles = convertToParticles(ip, 1, 1, 1);
    particles.saveParticles(new FileOutputStream(new File(prefix
        + step + ".par")));
  }

  private static void createFiles(final int step) throws FileNotFoundException,
      IOException {

    createFile(createCubeImage(step), step, "cube");
    createFile(createHoriPoints(step), step, "hori");
    createFile(createVertPoints(step), step, "vert");
    createFile(createPyramidImage(step), step, "pyramid");
    createFile(createSolidImage(step), step, "solid");
    createFile(create0ptImage(step), step, "0pt");
    createFile(create1ptImage(step), step, "1pt");

  }

  //
  // Constructor
  //

  private ImageJImageCreate() {
  }

  //
  // Main method
  //

  /**
   * Main method.
   * @param args command line arguments
   * @exception FileNotFoundException if can create output files
   * @exception IOException if can create output files
   */
  public static void main(final String[] args) throws FileNotFoundException,
      IOException {

    createFiles(1);
    createFiles(2);
  }

}
