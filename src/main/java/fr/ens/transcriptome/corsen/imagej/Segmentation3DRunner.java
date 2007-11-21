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

package fr.ens.transcriptome.corsen.imagej;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ens.transcriptome.corsen.model.Particle2D;
import fr.ens.transcriptome.corsen.model.Particle2DBuilder;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particle3DBuilder;

/**
 * This class implements the segmentation 3D for imagej 2D data.
 * @author Laurent Jourdren
 */
public class Segmentation3DRunner {

  private Set<Particle2D> previousParticules2D = new HashSet<Particle2D>();
  private Set<Particle2D> currentParticles2D = new HashSet<Particle2D>();
  private Map<Particle2D, Particle3DBuilder> previousParticles3D =
      new HashMap<Particle2D, Particle3DBuilder>();
  private Map<Particle2D, Particle3DBuilder> currentParticles3D =
      new HashMap<Particle2D, Particle3DBuilder>();

  private Set<Particle3DBuilder> particles3D = new HashSet<Particle3DBuilder>();

  private int previousZ = -1;
  private static final boolean DEBUG = false;
  private int totalPixels2D = 0;

  /**
   * Clear internal data.
   */
  public void clear() {

    if (DEBUG)
      System.out.println("Cleaning data");

    this.previousParticules2D.clear();
    this.currentParticles2D.clear();
    this.previousParticles3D.clear();
    this.currentParticles3D.clear();
    this.particles3D.clear();

    this.previousZ = -1;
    this.totalPixels2D = 0;
  }

  /**
   * Process segmentation3D for current slice
   * @param imp Original Image
   * @param roi Region of interest from Segmentation 2D
   */
  public void savePolygonXY(final ImagePlus imp, final Roi roi) {

    // double zHeight= 2.0;

    Calibration cal = imp.getCalibration();
    final double pixelWidth = cal.pixelWidth;
    final double pixelHeight = cal.pixelHeight;
    final double pixelDepth = cal.pixelDepth;

    final int slice = imp.getCurrentSlice();

    if (slice - 1 != this.previousZ) {
      this.previousParticules2D = this.currentParticles2D;
      this.currentParticles2D = new HashSet<Particle2D>();
      this.previousParticles3D = this.currentParticles3D;
      this.currentParticles3D = new HashMap<Particle2D, Particle3DBuilder>();

      if (this.previousZ == -1)
        this.previousZ = slice - 1;
      else
        this.previousZ++;
    }

    Particle2D p2D =
        Particle2DBuilder.createParticle2D((float) pixelWidth,
            (float) pixelHeight, imp, (PolygonRoi) roi);

    this.totalPixels2D += p2D.innerPointsCount();

    boolean find = false;

    for (Particle2D p2DToTest : this.previousParticules2D) {

      if (p2DToTest.innerPointIntersect(p2D)) {

        Particle3DBuilder existingP3D = this.previousParticles3D.get(p2DToTest);

        existingP3D.add(p2D, slice);

        if (DEBUG)
          System.out.println("\tAdd p2D #"
              + p2D.getId() + " to 3D Object #" + existingP3D.getId() + " z="
              + slice + " (" + p2D.innerPointsCount() + " points, "
              + existingP3D.innerPointsCount() + " total points)");

        if (this.currentParticles3D.containsKey(p2D)) {
          Particle3DBuilder particle2 = this.currentParticles3D.get(p2D);

          if (particle2.getId() != existingP3D.getId()) {

            particle2.add(existingP3D.getParticle());
            this.particles3D.remove(existingP3D);

            if (DEBUG)
              System.out.println("Merge 3D Object #"
                  + existingP3D.getId() + " in #" + particle2.getId() + " z="
                  + slice);
          }

        } else
          this.currentParticles3D.put(p2D, existingP3D);

        find = true;
        break;
      }

    }

    if (!find) {

      Particle3DBuilder newP3D =
          new Particle3DBuilder((float) pixelWidth, (float) pixelHeight,
              (float) pixelDepth);
      newP3D.setName(imp.getTitle() + "-" + newP3D.getId());
      newP3D.add(p2D, slice);

      this.particles3D.add(newP3D);
      this.currentParticles3D.put(p2D, newP3D);

      if (DEBUG)
        System.out.println("New 3D Object #"
            + newP3D.getId() + " z=" + slice + " add p2D #" + p2D.getId()
            + " (" + p2D.innerPointsCount() + " points)");

    }

    this.currentParticles2D.add(p2D);
  }

  /**
   * Create the list of Particles 3D segmented.
   * @return a list of Particle3D
   */
  public List<Particle3D> getParticlesToSave() {

    final List<Particle3D> result =

    new ArrayList<Particle3D>(this.particles3D.size());

    int totalPixels3D = 0;

    for (Particle3DBuilder pb : this.particles3D) {
      final Particle3D p = pb.getParticle();
      result.add(p);
      totalPixels3D += p.innerPointsCount();
    }

    if (DEBUG)
      System.out.println("Seg2D: "
          + this.totalPixels2D + "\tSeg3D:" + totalPixels3D);

    if (this.totalPixels2D != totalPixels3D)
      throw new RuntimeException("Segmentation 3D error: "
          + "Segmented 2D and 3D points count are not the same.");

    return result;
  }
}
