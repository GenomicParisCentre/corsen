package fr.ens.transcriptome.corsen;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Point3D;

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

public class Distance {

  private Particle3D messenger;
  private Particle3D mito;
  private Point3D pointMessenger;
  private Point3D pointMito;
  private float distance;

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
   * Get the messenger particle
   * @return Returns the messenger
   */
  public Particle3D getMessenger() {
    return messenger;
  }

  /**
   * Get the mito particle
   * @return Returns the mito
   */
  public Particle3D getMito() {
    return mito;
  }

  /**
   * Get messenger point
   * @return Returns the pointMessenger
   */
  public Point3D getPointMessenger() {
    return pointMessenger;
  }

  /**
   * Get mito point
   * @return Returns the pointMito
   */
  public Point3D getPointMito() {
    return pointMito;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param messenger Messenger Particle
   * @param mito Mito particle
   * @param pointMessenger point of the messenger
   * @param pointMito point of the mito
   * @param distance Distance between the two points
   */
  public Distance(final Particle3D messenger, final Particle3D mito,
      final Point3D pointMessenger, final Point3D pointMito,
      final float distance) {

    this.messenger = messenger;
    this.mito = mito;
    this.pointMessenger = pointMessenger;
    this.pointMito = pointMito;
    this.distance = distance;
  }

}
