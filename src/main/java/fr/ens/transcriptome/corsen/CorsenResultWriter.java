package fr.ens.transcriptome.corsen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.calc.DistanceAnalyser;
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

    out.write("#Internal id\tIntensity\tMin distance\tMax distance\tComment\n");

    Map<Particle3D, Distance> mins = r.getMinDistances();
    Map<Particle3D, Distance> maxs = r.getMaxDistances();

    Iterator<Particle3D> it = mins.keySet().iterator();

    while (it.hasNext()) {

      Particle3D p = it.next();
      out.write(Integer.toString(p.getId()));
      out.write("\t");
      out.write(Long.toString(p.getIntensity()));
      out.write("\t");
      out.write(Float.toString(mins.get(p).getDistance()));
      out.write("\t");
      out.write(Float.toString(maxs.get(p).getDistance()));
      out.write("\t");
      out.write(p.getComment());
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

    // Particle3D[] pars = particles.getParticles();

    if (particles == null)
      return;

    final Writer out = new OutputStreamWriter(os);

    out.write("#Internal id\tArea\tVolume\tSphericity\tIntensity\tDensity\n");

    for (Particle3D par : particles.getParticles()) {

      out.write(Integer.toString(par.getId()));
      out.write("\t");
      out.write(Double.toString(par.getArea()));
      out.write("\t");
      out.write(Double.toString(par.getVolume()));
      out.write("\t");
      out.write(Double.toString(par.getSphericity()));
      out.write("\t");
      out.write(Long.toString(par.getIntensity()));
      out.write("\t");
      out.write(Double.toString(par.getDensity()));
      out.write("\n");
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

    // final Particle3D[] mitos = mitosParticles.getParticles();
    // final Particle3D[] messengers = messengersParticles.getParticles();

    for (Particle3D mito : mitosParticles.getParticles()) {
      // for (int j = 0; j < mitos.length; j++) {

      out.write("d(s,s)[");
      out.write(mito.getName());
      out.write("]");
      out.write("\t");
      out.write("d(c,s)[");
      out.write(mito.getName());
      out.write("]");
      out.write("\t");
      out.write("include[");
      out.write(mito.getName());
      out.write("]");
      out.write("\t");
    }
    out.write("min(s,s)\tmin(c,s)\n");

    for (Particle3D messenger : messengersParticles.getParticles()) {

      out.write(messenger.getName());
      out.write("\t");
      out.write("" + messenger.getVolume());
      out.write("\t");
      out.write("" + messenger.getIntensity());
      out.write("\t");

      double minss = java.lang.Double.MAX_VALUE;
      double mincs = java.lang.Double.MAX_VALUE;

      for (Particle3D mito : mitosParticles.getParticles()) {

        final double dss = messenger.getSurfaceToSurfaceDistance(mito);
        final double dcs = messenger.getBarycenterToSurfaceDistance(mito);

        if (dss < minss)
          minss = dss;
        if (dcs < mincs)
          mincs = dcs;

        out.write("" + dss);
        out.write("\t");
        out.write("" + dcs);
        out.write("\t");

        final boolean include = messenger.intersect(mito);
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

  /**
   * Write the result file
   * @param os OutputStream
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeResult(final File file, final String suffix)
      throws IOException {

    writeResult(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the result file
   * @param os OutputStream
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeResult(final File file) throws IOException {

    writeResult(new FileOutputStream(file));
  }

  public void writeResult(final FileOutputStream os) throws IOException {

    final Writer out = new OutputStreamWriter(os);

    final CorsenResult cr = getResult();
    if (cr == null)
      return;

    StringBuffer sb = new StringBuffer();
    sb.append("#Particle A\tParticle B\t");
    sb
        .append("Mins min\tMins first quartile\tMins median\tMins mean\tMins median\tMins third quartile\tMins max\t");
    sb
        .append("Mins min\tMaxs first quartile\tMaxs median\tMaxs mean\tMaxs median\tMaxs third quartile\tMaxs max\n");

    out.write(cr.getMessengersFile().getAbsolutePath());
    sb.append("\t");
    out.write(cr.getMitosFile().getAbsolutePath());
    sb.append("\t");

    DistanceAnalyser min = cr.getMinAnalyser();

    sb.append(min.getMin());
    sb.append("\t");
    sb.append(min.getFirstQuartile());
    sb.append("\t");
    sb.append(min.getMedian());
    sb.append("\t");
    sb.append(min.getMean());
    sb.append("\t");
    sb.append(min.getThirdQuartile());
    sb.append("\t");
    sb.append(min.getMax());
    sb.append("\t");

    DistanceAnalyser max = cr.getMinAnalyser();

    sb.append(max.getMin());
    sb.append("\t");
    sb.append(max.getFirstQuartile());
    sb.append("\t");
    sb.append(max.getMedian());
    sb.append("\t");
    sb.append(max.getMean());
    sb.append("\t");
    sb.append(max.getThirdQuartile());
    sb.append("\t");
    sb.append(max.getMax());
    sb.append("\n");

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
