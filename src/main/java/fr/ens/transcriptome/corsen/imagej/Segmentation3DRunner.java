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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ens.transcriptome.corsen.model.Particle2D;
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
   * Add particles 2D to the segmentation 3D process.
   * @param particles2D Particles 2D to add
   * @param slice slice in the stack of the particles
   * @param pixelDepth the pixel Depth
   * @param imageTitle the image title
   */
  public void addParticles2DForSegmentation3D(
      final List<Particle2D> particles2D, final int slice,
      final double pixelDepth, final String imageTitle) {

    if (particles2D == null)
      return;

    for (Particle2D par : particles2D)
      addParticle2DForSegmentation3D(par, slice, (float) pixelDepth, imageTitle);

  }

  /**
   * Add a particle 2D to the segmentation 3D process.
   * @param particle2D Particle 2D to add
   * @param slice slice in the stack of the particles
   * @param pixelDepth the pixel Depth
   * @param imageTitle the image title
   */
  private void addParticle2DForSegmentation3D(final Particle2D particle2D,
      final int slice, final float pixelDepth, final String imageTitle) {

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

    this.totalPixels2D += particle2D.innerPointsCount();

    boolean find = false;

    for (Particle2D p2DToTest : this.previousParticules2D) {

      if (p2DToTest.innerPointIntersect(particle2D)) {

        Particle3DBuilder existingP3D = this.previousParticles3D.get(p2DToTest);

        if (!find) {
          existingP3D.add(particle2D, slice);
          find = true;

          if (DEBUG)
            System.out.println("\tAdd p2D #"
                + particle2D.getId() + " to 3D Object #" + existingP3D.getId()
                + " z=" + slice + " (" + particle2D.innerPointsCount()
                + " points, " + existingP3D.innerPointsCount()
                + " total points)");
        }

        if (this.currentParticles3D.containsKey(particle2D)) {
          Particle3DBuilder particle3D2 =
              this.currentParticles3D.get(particle2D);

          if (particle3D2.getId() != existingP3D.getId()) {

            particle3D2.add(existingP3D);

            replaceOccurances(previousParticles3D, existingP3D, particle3D2);
            replaceOccurances(currentParticles3D, existingP3D, particle3D2);

            this.particles3D.remove(existingP3D);

            if (DEBUG)
              System.out.println("Merge 3D Object #"
                  + existingP3D.getId() + " in #" + particle3D2.getId() + " z="
                  + slice);
          }

        } else
          this.currentParticles3D.put(particle2D, existingP3D);
      }

    }

    if (!find) {

      Particle3DBuilder newP3D =
          new Particle3DBuilder(particle2D.getPixelWidth(), particle2D
              .getPixelHeight(), pixelDepth);
      newP3D.setName(imageTitle + "-" + newP3D.getId());
      newP3D.add(particle2D, slice);

      this.particles3D.add(newP3D);

      this.currentParticles3D.put(particle2D, newP3D);

      if (DEBUG)
        System.out.println("New 3D Object #"
            + newP3D.getId() + " z=" + slice + " add p2D #"
            + particle2D.getId() + " (" + particle2D.innerPointsCount()
            + " points)");

    }

    this.currentParticles2D.add(particle2D);
  }

  private void replaceOccurances(Map<Particle2D, Particle3DBuilder> map,
      Particle3DBuilder oldParticle3D, Particle3DBuilder newParticle3D) {

    if (map == null || oldParticle3D == null || newParticle3D == null)
      return;

    for (Particle2D key : map.keySet()) {

      Particle3DBuilder value = map.get(key);
      if (value == oldParticle3D)
        map.put(key, newParticle3D);
    }
  }

  /**
   * Create the list of Particles 3D segmented.
   * @param removeEdgeParticles true if edge particles must be removed
   * @return a list of Particle3D
   */
  public List<Particle3D> getParticlesToSave() {

    return getParticlesToSave(false);
  }

  /**
   * Create the list of Particles 3D segmented.
   * @param removeEdgeParticles true if edge particles must be removed
   * @return a list of Particle3D
   */
  public List<Particle3D> getParticlesToSave(final boolean removeEdgeParticles) {

    final List<Particle3D> result =

    new ArrayList<Particle3D>(this.particles3D.size());

    int totalPixels3D = 0;

    for (Particle3DBuilder pb : this.particles3D) {
      final Particle3D p = pb.getParticle();

      if (!removeEdgeParticles || !p.isEdgeParticle())
        result.add(p);

      totalPixels3D += p.innerPointsCount();

      if (DEBUG)
        System.out.println("Particle 3D #"
            + p.getId() + " " + p.innerPointsCount() + " points\tOn edge: "
            + p.isEdgeParticle());

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
