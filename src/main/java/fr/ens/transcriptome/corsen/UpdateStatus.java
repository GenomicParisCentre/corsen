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

package fr.ens.transcriptome.corsen;

import fr.ens.transcriptome.corsen.calc.CorsenResult;

public interface UpdateStatus {

  /**
   * Update the status bar.
   * @param e event to show
   */
  void updateStatus(final ProgressEvent e);

  /**
   * Show an error message.
   * @param msg Message to display
   */
  void showError(final String msg);

  /**
   * Show a message.
   * @param msg Message to display
   */
  void showMessage(final String msg);

  /**
   * Send corsen result at the end of the process.
   * @param result The corsen result
   */
  void endProcess(CorsenResult result);

  /**
   * Move to a thread. Needed by Qt.
   * @param thread Thread to move
   */
  void moveToThread(Thread thread);

  /**
   * Chain the update status for the differents threads. Needed by Qt.
   * @return an UpdateStatus instance
   */
  UpdateStatus chain();

  /**
   * Create a new Thread.
   * @param runnable Runnable Object for the thread
   * @return a new Thread
   */
  Thread newThread(final Runnable runnable);

}
