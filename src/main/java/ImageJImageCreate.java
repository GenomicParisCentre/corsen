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

  private static ImagePlus createHoriPoints2(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    for (int k = 0; k < (LEN / step) + 1; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);

      if (k == 25 / step)
        for (int i = 1; i < 50; i = i + step)
          if ((i - 1) % (6 / step) == 0)
            ip.set(i, 25, INTENSITY);

      is.addSlice("" + k / step, ip);
    }

    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  private static ImagePlus createVertPoints2(final int step) {

    ImageStack is = new ImageStack(LEN, LEN);

    for (int k = 0; k < ((LEN + 1) / step) + 1; k++) {

      ImageProcessor ip = new ShortProcessor(LEN, LEN);

      if ((k - 1) % (6 / step) == 0)
        ip.set(25, 25, INTENSITY);

      is.addSlice("" + k, ip);
    }

    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  private static ImagePlus createCubeImage2(final int step) {

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

  private static void createFiles(final int step) throws FileNotFoundException,
      IOException {

    ImagePlus cube = createCubeImage2(step);
    ImagePlus hori = createHoriPoints2(step);
    ImagePlus vert = createVertPoints2(step);

    cube.getProcessor().setThreshold(MIN_THRESHOLD, MAX_THRESHOLD,
        ImageProcessor.NO_LUT_UPDATE);
    hori.getProcessor().setThreshold(MIN_THRESHOLD, MAX_THRESHOLD,
        ImageProcessor.NO_LUT_UPDATE);
    vert.getProcessor().setThreshold(MIN_THRESHOLD, MAX_THRESHOLD,
        ImageProcessor.NO_LUT_UPDATE);

    Particles3D cubeParticles = convertToParticles(cube, 1, 1, 1);
    Particles3D horiParticles = convertToParticles(hori, 1, 1, 1);
    Particles3D vertParticles = convertToParticles(vert, 1, 1, 1);

    cubeParticles.saveParticles(new FileOutputStream(new File("cube"
        + step + ".par")));
    horiParticles.saveParticles(new FileOutputStream(new File("hori"
        + step + ".par")));
    vertParticles.saveParticles(new FileOutputStream(new File("vert"
        + step + ".par")));

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
