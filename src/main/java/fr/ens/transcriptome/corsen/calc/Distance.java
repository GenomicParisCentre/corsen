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

package fr.ens.transcriptome.corsen.calc;

import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Point3D;
import fr.ens.transcriptome.corsen.util.HashCodeUtil;

/**
 * This class define a distance. Note: this class has a natural ordering that is
 * inconsistent with equals.
 * @author Laurent Jourdren
 */
public final class Distance implements Comparable<Distance> {

  private Particle3D particleA;
  private Particle3D particleB;
  private Point3D pointA;
  private Point3D pointB;
  private float distance;
  private boolean comparatorAbsoluteMode = true;

  //
  // Getters
  //

  /**
   * Get the length of distance
   * @return Returns the distance
   */
  public float getDistance() {
    return distance;
  }

  /**
   * Get the particleA particle
   * @return Returns the particleA
   */
  public Particle3D getParticleA() {
    return particleA;
  }

  /**
   * Get the particleB particle
   * @return Returns the particleB
   */
  public Particle3D getParticleB() {
    return particleB;
  }

  /**
   * Get particleA point
   * @return Returns the pointA
   */
  public Point3D getPointA() {
    return pointA;
  }

  /**
   * Get particleB point
   * @return Returns the pointB
   */
  public Point3D getPointB() {
    return pointB;
  }

  /**
   * Test if the comparator is in absolute mode.
   * @return Returns the comparatorAbsoluteMode
   */
  public boolean isComparatorAbsoluteMode() {
    return comparatorAbsoluteMode;
  }

  //
  // Setters
  //

  /**
   * Set the particleA.
   * @param particleA The particleA to set
   */
  void setParticleA(final Particle3D particleA) {
    this.particleA = particleA;
  }

  /**
   * Set the particleB
   * @param particleB The particleB to set
   */
  void setParticleB(final Particle3D particleB) {
    this.particleB = particleB;
  }

  /**
   * Set the comparator absolute mode.
   * @param comparatorAbsoluteMode The comparatorAbsoluteMode to set
   */
  public void setComparatorAbsoluteMode(final boolean comparatorAbsoluteMode) {
    this.comparatorAbsoluteMode = comparatorAbsoluteMode;
  }

  //
  // Other methods
  //

  /**
   * Test if two distance are equals.
   * @param o Object to test
   * @return true if the distances are equals
   */
  public boolean equals(final Object o) {

    if (o == null || !(o instanceof Distance))
      return false;

    return ((Distance) o).distance == this.distance;
  }

  /**
   * Compare two distances objects.
   * @param distance distance to test
   * @return an int
   */
  public final int compareTo(final Distance distance) {

    final float f;

    if (!this.comparatorAbsoluteMode)
      f = this.distance - distance.distance;
    else
      f = Math.abs(this.distance) - Math.abs(distance.distance);

    if (f < 0)
      return -1;
    if (f > 0)
      return 1;

    return 0;
  }

  public final int hashCode() {

    int result = HashCodeUtil.SEED;

    result = HashCodeUtil.hash(result, this.particleA);
    result = HashCodeUtil.hash(result, this.particleB);
    result = HashCodeUtil.hash(result, this.pointA);
    result = HashCodeUtil.hash(result, this.pointB);
    result = HashCodeUtil.hash(result, this.distance);
    return result;
  }
  
  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param pointA point of the particleA
   * @param pointB point of the particleB
   * @param distance Distance between the two points
   */
  public Distance(final Point3D pointA, final Point3D pointB,
      final float distance) {

    this.pointA = pointA;
    this.pointB = pointB;
    this.distance = distance;
  }

  /**
   * Public constructor.
   * @param particleA Messenger Particle
   * @param particleB Mito particle
   * @param pointA point of the particleA
   * @param pointB point of the particleB
   * @param distance Distance between the two points
   */
  public Distance(final Point3D pointA, final Point3D pointB,
      final Particle3D particleA, final Particle3D particleB,
      final float distance) {

    this(pointA, pointB, distance);
    setParticleA(particleA);
    setParticleB(particleB);
  }

}
