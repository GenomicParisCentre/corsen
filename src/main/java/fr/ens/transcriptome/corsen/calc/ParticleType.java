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

package fr.ens.transcriptome.corsen.calc;

public enum ParticleType {

  HUGE("Huge particle"), TINY("Tiny particle"), UNDEFINED("Undefined particle");

  private String description;

  /**
   * Get the description of the type of the particle.
   * @return the description of the particle
   */
  public String toString() {

    return this.description;
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
  private ParticleType(String description) {

    this.description = description;
  }

}
