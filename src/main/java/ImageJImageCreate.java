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

public class ImageJImageCreate {

  public static ImagePlus createCubeImage(boolean third) {

    ImageStack is = new ImageStack(41, 41);

    for (int k = 0; k < 41; k++) {

      ImageProcessor ip = new ShortProcessor(41, 41);

      if (k >= 10 && k <= 31)
        for (int i = 10; i < 31; i++)
          for (int j = 10; j < 31; j++)
            ip.set(i, j, 100);

      is.addSlice("" + k, ip);
    }

    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  public static ImagePlus createHoriPoints() {

    ImageStack is = new ImageStack(41, 41);

    for (int k = 0; k < 41; k++) {

      ImageProcessor ip = new ShortProcessor(41, 41);

      if (k == 20)
        for (int i = 0; i < 41; i = i + 2)
          ip.set(i, 20, 100);

      is.addSlice("" + k, ip);
    }

    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  public static ImagePlus createHoriPoints2(final int step) {

    ImageStack is = new ImageStack(50, 50);

    for (int k = 0; k < (50 / step) + 1; k++) {

      ImageProcessor ip = new ShortProcessor(50, 50);

      boolean draw = true;

      if (k == 25 / step)
        for (int i = 1; i < 50; i = i + step)
          if ((i - 1) % (6 / step) == 0) {
            ip.set(i, 25, 100);
            draw = false;
          } else
            draw = true;

      is.addSlice("" + k / step, ip);
    }

    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  public static ImagePlus createVertPoints2(final int step) {

    ImageStack is = new ImageStack(50, 50);

    int count = 0;

    for (int k = 0; k < ((50 + 1) / step) + 1; k++) {

      ImageProcessor ip = new ShortProcessor(50, 50);

      if ((k - 1) % (6 / step) == 0) {
        ip.set(25, 25, 100);
        System.out.println((++count) + " k=" + k + "\t" + (k * step));
      }

      is.addSlice("" + k, ip);
    }

    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  public static ImagePlus createCubeImage2(final int step) {

    ImageStack is = new ImageStack(50, 50);

    int count = 0;

    for (int k = 0; k < 50 / step; k++) {

      ImageProcessor ip = new ShortProcessor(50, 50);

      if (k > 12 / step && k <= 36 / step + 1)
        for (int i = 13; i < 38; i++)
          for (int j = 13; j < 38; j++) {
            ip.set(i, j, 100);
            count++;
          }

      is.addSlice("" + k, ip);
    }

    System.out.println("create a cube with " + count + " blocks.");
    ImagePlus ipl = new ImagePlus("cube", is);

    return ipl;
  }

  public static Particles3D convertToParticles(final ImagePlus ip,
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

      ipr.setThreshold(1, 2000, ImageProcessor.NO_LUT_UPDATE);

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

    cube.getProcessor().setThreshold(1, 2000, ImageProcessor.NO_LUT_UPDATE);
    hori.getProcessor().setThreshold(1, 2000, ImageProcessor.NO_LUT_UPDATE);
    vert.getProcessor().setThreshold(1, 2000, ImageProcessor.NO_LUT_UPDATE);

    Particles3D cubeParticles = convertToParticles(cube, 1, 1, 1);
    Particles3D horiParticles = convertToParticles(hori, 1, 1, 1);
    Particles3D vertParticles = convertToParticles(vert, 1, 1, 1);

    cubeParticles.saveParticles(new FileOutputStream(new File(
        "/home/jourdren/Desktop/bug/cube" + step + ".par")));
    horiParticles.saveParticles(new FileOutputStream(new File(
        "/home/jourdren/Desktop/bug/hori" + step + ".par")));
    vertParticles.saveParticles(new FileOutputStream(new File(
        "/home/jourdren/Desktop/bug/vert" + step + ".par")));

  }

  public static final void main(final String[] args)
      throws FileNotFoundException, IOException {

    createFiles(1);
    createFiles(2);
  }

}
