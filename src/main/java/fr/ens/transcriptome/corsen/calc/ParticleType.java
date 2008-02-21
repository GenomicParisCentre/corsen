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
 * of the �cole Normale Sup�rieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.corsen.calc;

import java.util.Properties;

public enum ParticleType {

  HUGE("Huge particle", "full description for HUGE particle"), TINY(
      "Tiny particle", "full description for TINY particle"), UNDEFINED(
      "Undefined particle", "full description for UNDEFINED particle"),
  BARYCENTER("Barycenter particle", "full description for BARYCENTER particle");

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

    case HUGE:
      return new HugeParticles3D();

    case TINY:
      return new TinyParticles3D();

    case UNDEFINED:
      return new UndefinedParticles3D();

    case BARYCENTER:
      return new BarycenterParticles3D();

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

    case TINY:
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
