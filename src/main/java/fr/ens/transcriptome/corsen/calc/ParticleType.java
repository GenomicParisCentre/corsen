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

package fr.ens.transcriptome.corsen.calc;

import java.util.Properties;

public enum ParticleType {

  SURFACE("Surface particle", "Use the surface points of particles to compute distances."),
  DECOMPOSITION("Decomposition particle", "Decompose the particles in cuboids before compute distances."),
  ALLPOINTS("All points particle", "Use all the points of the particles to compute distances."),
  BARYCENTER("Barycenter particle", "Use the barycenter of particles to compute distances.");

  private String description;
  private String fullDescription;

  /**
   * Get the description of the type of the particle.
   * @return the description of the particle
   */
  public String toString() {

    return this.description;
  }

  /**
   * Get the full description of the particle type
   * @return the full description of the particle type
   */
  public String getFullDescription() {

    return this.fullDescription;
  }

  /**
   * Create the distance processor for the particle.
   * @return a distance processor for the particle
   */
  public DistanceProcessor getDistanceProcessor() {

    switch (this) {

    case SURFACE:
      return new SurfaceParticles3DType();

    case DECOMPOSITION:
      return new DecompostionParticles3DType();

    case ALLPOINTS:
      return new AllPointsParticles3DType();

    case BARYCENTER:
      return new BarycenterParticles3DType();

    default:
      return null;
    }

  }

  public static ParticleType getParticleType(String type) {

    if (type == null)
      return null;

    ParticleType[] types = ParticleType.values();

    for (int i = 0; i < types.length; i++) {

      if (type.equals(types[i].toString()))
        return types[i];
    }

    return null;
  }

  public Properties getDefaultProperties() {

    switch (this) {

    case DECOMPOSITION:
      Properties properties = new Properties();
      properties.setProperty("cuboid.automatic.size", "true");
      properties.setProperty("cuboid.custom.size", "" + 1.0);
      return properties;

    default:
      return null;
    }

  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   * @param description The description of the particle
   */
  private ParticleType(final String description, final String fullDescription) {

    this.description = description;
    this.fullDescription = fullDescription;
  }

}
