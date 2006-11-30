package fr.ens.transcriptome.corsen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;

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

public class CorsenResultWriter {

  private CorsenResult result;
  private UpdateStatus updateStatus;

  //
  // Getters
  //
  /**
   * Get the corsen result.
   * @return Returns the result
   */
  public CorsenResult getResult() {
    return result;
  }

  /**
   * Get the update status
   * @return Returns the updateStatus
   */
  public UpdateStatus getUpdateStatus() {
    return updateStatus;
  }

  //
  // Setters
  //

  /**
   * Set the update status
   * @param updateStatus The updateStatus to set
   */
  public void setUpdateStatus(UpdateStatus updateStatus) {
    this.updateStatus = updateStatus;
  }

  //
  // Write methods
  //

  /**
   * Write data file.
   * @param file file to write
   * @param suffix Suffix of file to append
   * @throws IOException if an error occurs while write the file
   */
  public void writeDataFile(final File file, String suffix) throws IOException {

    writeDataFile(createFileWithSuffix(file, suffix));
  }

  /**
   * Write data file.
   * @param file file to write
   * @throws IOException if an error occurs while write the file
   */
  public void writeDataFile(final File file) throws IOException {

    writeDataFile(new FileOutputStream(file));
  }

  /**
   * Write data file.
   * @param os OutputStream
   * @throws IOException if an error occurs while write the file
   */
  public void writeDataFile(final OutputStream os) throws IOException {

    final CorsenResult r = getResult();

    Writer out = new OutputStreamWriter(os);

    out.write("#Intensity\tmin distance\tmax distance\n");

    Map<Particle3D, Distance> mins = r.getMinDistances();
    Map<Particle3D, Distance> maxs = r.getMaxDistances();

    Iterator<Particle3D> it = mins.keySet().iterator();

    while (it.hasNext()) {

      Particle3D p = it.next();
      out.write("" + p.getIntensity());
      out.write("\t");
      out.write(Float.toString(mins.get(p).getDistance()));
      out.write("\t");
      out.write(Float.toString( maxs.get(p).getDistance()));
      out.write("\n");
    }

    out.close();
  }

  /**
   * Write the intensities and volumes of cuboids messenger particles
   * @param file File to write
   * @param suffix Suffix to append
   * @throws IOException if an error occurs while write the file
   */
  public void writeCuboidsMessengersIntensityVolume(final File file,
      final String suffix) throws IOException {

    writeCuboidsMessengersIntensityVolume(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the intensities and volumes of cuboids messenger particles
   * @param file File to write
   * @throws IOException if an error occurs while write the file
   */
  public void writeCuboidsMessengersIntensityVolume(final File file)
      throws IOException {

    writeCuboidsMessengersIntensityVolume(new FileOutputStream(file));
  }

  /**
   * Write the intensities and volumes of cuboids messenger particles
   * @param os OutputStream
   * @throws IOException if an error occurs while write the stream
   */
  public void writeCuboidsMessengersIntensityVolume(final OutputStream os)
      throws IOException {

    writeIntensityVolume(os, getResult().getCuboidsMessengersParticles());
  }

  /**
   * Write the intensities and volumes of mitos particles
   * @param file File to write
   * @param suffix Suffix to append
   * @throws IOException if an error occurs while write the file
   */
  public void writeMitosIntensityVolume(final File file, final String suffix)
      throws IOException {

    writeMitosIntensityVolume(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the intensities and volumes of mitos particles
   * @param file File to write
   * @throws IOException if an error occurs while write the file
   */
  public void writeMitosIntensityVolume(final File file) throws IOException {

    writeMitosIntensityVolume(new FileOutputStream(file));
  }

  /**
   * Write the intensities and volumes of messenger particles
   * @param os OutputStream
   * @throws IOException if an error occurs while write the stream
   */
  public void writeMitosIntensityVolume(final OutputStream os)
      throws IOException {

    writeIntensityVolume(os, getResult().getMitosParticles());
  }

  /**
   * Write the intensities and volumes of messenger particles
   * @param file File to write
   * @param suffix Suffix to append
   * @throws IOException if an error occurs while write the file
   */
  public void writeMessengersIntensityVolume(final File file,
      final String suffix) throws IOException {

    writeMessengersIntensityVolume(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the intensities and volumes of messenger particles
   * @param file File to write
   * @throws IOException if an error occurs while write the file
   */
  public void writeMessengersIntensityVolume(final File file)
      throws IOException {

    writeMessengersIntensityVolume(new FileOutputStream(file));
  }

  /**
   * Write the intensities and volumes of messenger particles
   * @param os OutputStream
   * @throws IOException if an error occurs while write the stream
   */
  public void writeMessengersIntensityVolume(final OutputStream os)
      throws IOException {

    writeIntensityVolume(os, getResult().getMessengersParticles());
  }

  /**
   * Write the intensities and volumes of particles
   * @param os OutputStream
   * @param particles Particles to write
   * @throws IOException if an error occurs while write the stream
   */
  public static void writeIntensityVolume(final OutputStream os,
      final Particles3D particles) throws IOException {

    if (particles == null || os == null)
      return;

    Particle3D[] pars = particles.getParticles();

    if (pars == null)
      return;

    final Writer out = new OutputStreamWriter(os);

    out.write("# Intensity\tVolume\n");

    for (int i = 0; i < pars.length; i++) {
      if (i != 0)
        out.write("\n");
      out.write(Long.toString(pars[i].getIntensity()));
      out.write("\t");
      out.write(Double.toString(pars[i].getVolume()));
    }

    out.close();
  }

  /**
   * Write the full result file
   * @param os OutputStream
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeFullResult(final File file, final String suffix)
      throws IOException {

    writeFullResult(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the full result file
   * @param os OutputStream
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeFullResult(final File file) throws IOException {

    writeFullResult(new FileOutputStream(file));
  }

  /**
   * Write the full result file
   * @param os OutputStream
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeFullResult(final OutputStream os) throws IOException {

    writeFullResultFile(getResult().getMessengersParticles(), getResult()
        .getMessengersParticles(), os);
  }

  /**
   * Write data in full resut file
   * @param messengers Messengers particles
   * @param mitos Mitochondia particles
   * @param out Writer
   * @throws IOException if an error occurs while writing data
   */
  private void writeFullResultFile(final Particles3D messengersParticles,
      final Particles3D mitosParticles, final OutputStream os)
      throws IOException {

    final Writer out = new OutputStreamWriter(os);

    out.write("messager");
    out.write("\t");
    out.write("volume");
    out.write("\t");
    out.write("intensity");
    out.write("\t");

    final Particle3D[] mitos = mitosParticles.getParticles();
    final Particle3D[] messengers = messengersParticles.getParticles();

    for (int j = 0; j < mitos.length; j++) {

      out.write("d(s,s)[");
      out.write(mitos[j].getName());
      out.write("]");
      out.write("\t");
      out.write("d(c,s)[");
      out.write(mitos[j].getName());
      out.write("]");
      out.write("\t");
      out.write("include[");
      out.write(mitos[j].getName());
      out.write("]");
      out.write("\t");
    }
    out.write("min(s,s)\tmin(c,s)\n");

    for (int i = 0; i < messengers.length; i++) {

      out.write(messengers[i].getName());
      out.write("\t");
      out.write("" + messengers[i].getVolume());
      out.write("\t");
      out.write("" + messengers[i].getIntensity());
      out.write("\t");

      double minss = java.lang.Double.MAX_VALUE;
      double mincs = java.lang.Double.MAX_VALUE;

      for (int j = 0; j < mitos.length; j++) {

        final double dss = messengers[i].getSurfaceToSurfaceDistance(mitos[j]);
        final double dcs = messengers[i]
            .getBarycenterToSurfaceDistance(mitos[j]);

        if (dss < minss)
          minss = dss;
        if (dcs < mincs)
          mincs = dcs;

        out.write("" + dss);
        out.write("\t");
        out.write("" + dcs);
        out.write("\t");

        final boolean include = messengers[i].intersect(mitos[j]);
        if (include)
          out.write("1");
        else
          out.write("0");
        out.write("\t");
      }

      out.write("" + minss);
      out.write("\t");
      out.write("" + mincs);
      out.write("\n");
    }
    out.close();
  }

  //
  // Other methods
  //

  /**
   * Create a file object with a file and a suffix
   * @param file the file to extends
   * @param suffix Suffix to append
   * @return new file with the suffix
   */
  public static File createFileWithSuffix(final File file, final String suffix) {

    if (file == null)
      return null;
    if (suffix == null)
      return file;

    return new File(file.getAbsolutePath() + suffix);
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param result Result to set.
   */
  public CorsenResultWriter(final CorsenResult result) {

    this.result = result;
  }

}
