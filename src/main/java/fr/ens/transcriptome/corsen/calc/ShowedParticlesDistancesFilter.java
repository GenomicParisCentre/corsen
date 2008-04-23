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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;

/**
 * This class allow to filter distance which particles aren't displayed.
 * @author Laurent Jourdren
 */
public class ShowedParticlesDistancesFilter implements DistancesFilter {

  private Set<Particle3D> particlesA;
  private Set<Particle3D> particlesB;

  /**
   * Test if a distance must be filtered.
   * @param distance Particle3D to test
   * @return true if the particle is accepted
   */
  public boolean accept(final Distance distance) {

    if (distance == null || this.particlesA == null || this.particlesB == null)
      return false;

    final Particle3D pA = distance.getParticleA();
    final Particle3D pB = distance.getParticleB();

    if (pA == null || pB == null)
      return false;

    return particlesA.contains(pA) && particlesB.contains(pB);
  }

  private static final Set<Particle3D> convertParticles(
      final Particles3D particles) {

    if (particles == null)
      return null;

    final List<Particle3D> pars = particles.getParticles();
    final Set<Particle3D> result = new HashSet<Particle3D>(pars.size());
    result.addAll(pars);

    return result;
  }

  //
  // Public constructor
  //

  /**
   * Public constructor.
   * @param particlesA Set of Particles3D
   * @param particlesA Set of Particles3D
   */
  public ShowedParticlesDistancesFilter(final Set<Particle3D> particlesA,
      final Set<Particle3D> particlesB) {

    System.out.println("particlesA = "
        + (particlesA == null ? "null" : particlesA.size()));
    System.out.println("particlesB = "
        + (particlesB == null ? "null" : particlesB.size()));
    this.particlesA = particlesA;
    this.particlesB = particlesB;
  }

  /**
   * Public constructor.
   * @param particlesA Particles3D
   * @param particlesA Particles3D
   */
  public ShowedParticlesDistancesFilter(final Particles3D particlesA,
      final Particles3D particlesB) {

    this(convertParticles(particlesA), convertParticles(particlesB));
  }

}
