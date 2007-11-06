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

package fr.ens.transcriptome.corsen.gui.qt;

import java.io.File;

import com.trolltech.qt.core.QEventLoop;
import com.trolltech.qt.core.QObject;

import fr.ens.transcriptome.corsen.CorsenCore;
import fr.ens.transcriptome.corsen.Settings;
import fr.ens.transcriptome.corsen.UpdateStatus;

public class CorsenCoreQt extends QObject implements Runnable {

  private CorsenCore cc= new CorsenCore();
  
  /**
   * Get the update status class.
   * @return the updateStatus class
   */
  public final UpdateStatus getUpdateStatus() {

   return cc.getUpdateStatus();
  }

  /**
   * Get the settings.
   * @return an settings object
   */
  public Settings getSettings() {

    return cc.getSettings();
  }

  /**
   * Get the Directory of the files to read
   * @return Returns the dirFiles
   */
  public File getDirFiles() {
    return cc.getDirFiles();
  }

  /**
   * Get the mito file.
   * @return Returns the particlesBFile
   */
  public File getParticlesBFile() {
    return cc.getParticlesBFile();
  }

  /**
   * Get the result directory.
   * @return Returns the resultDir
   */
  /*
   * public File getResultDir() { return this.resultDir; }
   */

  /**
   * Get the result filename.
   * @return Returns the resultFile
   */
  public File getResultFile() {
    return cc.getResultFile();
  }

  /**
   * Get the rna file.
   * @return Returns the particlesAFile
   */
  public File getParticlesAFile() {
    return cc.getParticlesAFile();
  }

  /**
   * Test if there are multple files to read.
   * @return Returns the multipleFiles
   */
  public boolean isMultipleFiles() {
    return cc.isMultipleFiles();
  }

  //
  // Setters
  //

  /**
   * Set the update status class.
   * @param updateStatus the updateStatus class
   */
  public final void setUpdateStatus(final UpdateStatus updateStatus) {

    cc.setUpdateStatus(updateStatus);
  }

  /**
   * Set the settings.
   * @param setting The setting to set
   */
  public void setSettings(final Settings settings) {

   cc.setSettings(settings);
  }

  /**
   * Set the size of pixel.
   * @param pixelSize The size of a pixel
   */
  /*
   * public void setPixelSize(final float pixelSize) { this.pixelSize =
   * pixelSize; }
   */

  /**
   * Set if the Z coordinate must be updated
   * @param updateZ true if the Z coordinate must be updated
   */
  /*
   * public void setUpdateZ(final boolean updateZ) { this.updateZ = updateZ; }
   */

  /**
   * Set the coef of the update of the Z values.
   * @param coef the coef of the update of the Z values
   */
  /*
   * public void setZCoef(final float coef) { this.zCoef = coef; }
   */

  /**
   * Set the directory of the files to read.
   * @param dirFiles The dirFiles to set
   */
  public void setDirFiles(final File dirFiles) {
    cc.setDirFiles(dirFiles);
  }

  /**
   * Set the mito file to read.
   * @param particlesBFile The particlesBFile to set
   */
  public void setParticlesBFile(final File particlesBFile) {
    cc.setParticlesBFile(particlesBFile);
  }

  /**
   * Set the result directory.
   * @param resultDir The resultDir to set
   */
  /*
   * public void setResultDir(final File resultDir) { this.resultDir =
   * resultDir; }
   */

  /**
   * Set the result filename.
   * @param resultFile The resultFile to set
   */
  public void setResultFile(final File resultFilename) {
    cc.setResultFile(resultFilename);
  }

  /**
   * Set the rna file.
   * @param particlesAFile The particlesAFile to set
   */
  public void setParticlesAFile(final File particlesAFile) {
   cc.setParticlesAFile(particlesAFile);
  }

  /**
   * Set if there are multiple files to read.
   * @param multipleFiles The multipleFiles to set
   */
  public void setMultipleFiles(final boolean multipleFiles) {
   cc.setMultipleFiles(multipleFiles);
  }
  
  public void run() {
    
    System.out.println("run thread...");
    
    getUpdateStatus().showMessage("hello world!");
    System.out.println("run thread2...");
    
    QEventLoop eventLoop = new QEventLoop();
    eventLoop.exec();
    
    cc.run();
  }
  
  
  
}
