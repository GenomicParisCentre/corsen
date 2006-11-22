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
  void endProcess(CorsenResult result) ;
  
}
