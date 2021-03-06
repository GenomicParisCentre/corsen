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

package fr.ens.transcriptome.corsen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults;
import fr.ens.transcriptome.corsen.calc.CorsenResult;
import fr.ens.transcriptome.corsen.calc.Distance;
import fr.ens.transcriptome.corsen.calc.DistanceAnalyser;
import fr.ens.transcriptome.corsen.calc.CorsenHistoryResults.Entry;
import fr.ens.transcriptome.corsen.model.Particle3D;
import fr.ens.transcriptome.corsen.model.Particles3D;

/**
 * This save results files.
 * @author Laurent Jourdren
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
  public void setUpdateStatus(final UpdateStatus updateStatus) {
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
  public void writeDataFile(final File file, final String suffix)
      throws IOException {

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

    for (Map.Entry<Particle3D, Distance> e : mins.entrySet()) {

      final Particle3D p = e.getKey();
      final Distance d = e.getValue();

      out.write(Integer.toString(p.getId()));
      out.write("\t");
      out.write(Long.toString(p.getIntensity()));
      out.write("\t");
      out.write(Float.toString(d.getDistance()));
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

    writeIntensityVolume(os, getResult().getCuboidsParticlesA());
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
   * Write the intensities and volumes of mitos particles
   * @param os OutputStream
   * @throws IOException if an error occurs while write the stream
   */
  public void writeMitosIntensityVolume(final OutputStream os)
      throws IOException {

    writeIntensityVolume(os, getResult().getParticlesB());
  }

  /**
   * Write the intensities and volumes of mitos cuboids particles
   * @param file File to write
   * @param suffix Suffix to append
   * @throws IOException if an error occurs while write the file
   */
  public void writeCuboidsMitosIntensityVolume(final File file,
      final String suffix) throws IOException {

    writeCuboidsMitosIntensityVolume(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the intensities and volumes of mitos cuboids particles
   * @param file File to write
   * @throws IOException if an error occurs while write the file
   */
  public void writeCuboidsMitosIntensityVolume(final File file)
      throws IOException {

    writeCuboidsMitosIntensityVolume(new FileOutputStream(file));
  }

  /**
   * Write the intensities and volumes of mitos cuboids particles
   * @param os OutputStream
   * @throws IOException if an error occurs while write the stream
   */
  public void writeCuboidsMitosIntensityVolume(final OutputStream os)
      throws IOException {

    writeIntensityVolume(os, getResult().getCuboidsParticlesB());
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

    writeIntensityVolume(os, getResult().getParticlesA());
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

    final Writer out = new OutputStreamWriter(os);

    out
        .write("#Internal id\tArea\tVolume\tSphericity\tIntensity\tDensity\tMedian Circularity\n");

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
      out.write("\t");
      out.write(Double.toString(par.getMedianCircularity()));
      out.write("\n");

    }

    out.close();
  }

  /**
   * Write the full result file
   * @param file File to save
   * @param suffix Suffix of the file
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeFullResult(final File file, final String suffix)
      throws IOException {

    writeFullResult(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the full result file
   * @param file File to save
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

    writeFullResultFile(getResult().getParticlesA(), getResult()
        .getParticlesA(), os);
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
   * Write data in full resut file
   * @param os outpstream to use
   * @throws IOException if an error occurs while writing data
   */
  public void writeSummaryResultFile(final OutputStream os) throws IOException {

    final Writer out = new OutputStreamWriter(os);

    out.write("\tMinimum\t1st Quartile\tMedian\tMean\t3rd Quartile\tMaximum\n");

    DistanceAnalyser da = result.getMinAnalyser();

    out.write("Min distances");
    out.write(Double.toString(da.getMin()));
    out.write("\t");
    out.write(Double.toString(da.getFirstQuartile()));
    out.write("\t");
    out.write(Double.toString(da.getMedian()));
    out.write("\t");
    out.write(Double.toString(da.getMean()));
    out.write("\t");
    out.write(Double.toString(da.getThirdQuartile()));
    out.write("\t");
    out.write(Double.toString(da.getMax()));
    out.write("\n");

    da = result.getMaxAnalyser();

    out.write("Max distances");
    out.write(Double.toString(da.getMin()));
    out.write("\t");
    out.write(Double.toString(da.getFirstQuartile()));
    out.write("\t");
    out.write(Double.toString(da.getMedian()));
    out.write("\t");
    out.write(Double.toString(da.getMean()));
    out.write("\t");
    out.write(Double.toString(da.getThirdQuartile()));
    out.write("\t");
    out.write(Double.toString(da.getMax()));
    out.write("\n");

    out.close();
  }

  /**
   * Write the result file
   * @param file File to write
   * @param suffix Suffix
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeResult(final File file, final String suffix)
      throws IOException {

    writeResult(createFileWithSuffix(file, suffix));
  }

  /**
   * Write the result file
   * @param file File to write
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeResult(final File file) throws IOException {

    writeResult(new FileOutputStream(file));
  }

  /**
   * Write the result file
   * @param os OutputStream
   * @throws IOException if an error occurs while writing the stream
   */
  public void writeResult(final FileOutputStream os) throws IOException {

    final Writer out = new OutputStreamWriter(os);

    final CorsenResult cr = getResult();
    if (cr == null)
      return;

    StringBuilder sb = new StringBuilder();
    sb.append("#Particle A\tParticle B\t");
    sb.append("Mins min\tMins first quartile\tMins median\tMins mean\t"
        + "Mins median\tMins third quartile\tMins max\t");
    sb.append("Mins min\tMaxs first quartile\tMaxs median\tMaxs mean\t"
        + "Maxs median\tMaxs third quartile\tMaxs max\n");

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
    
    out.write(sb.toString());
    out.close();
  }

  /**
   * Write the history results file
   * @param file file output file
   * @throws IOException if an error occurs while writing the stream
   */
  public static void writeHistoryResults(final File file) throws IOException {

    writeHistoryResults(new FileOutputStream(file));
  }

  /**
   * Write the history results file
   * @param os the OutputStream
   * @throws IOException if an error occurs while writing the stream
   */
  public static void writeHistoryResults(final OutputStream os)
      throws IOException {

    final Writer out = new OutputStreamWriter(os);

    out.write("#File A\tFile B\tMin distance\n");

    CorsenHistoryResults history =
        CorsenHistoryResults.getCorsenHistoryResults();

    final int size = history.size();

    for (int i = 0; i < size; i++) {

      Entry e = history.get(i);

      out.write(e.getFileA().getAbsolutePath());
      out.write("\t");
      out.write(e.getFileB().getAbsolutePath());
      out.write("\t");
      out.write(Double.toString(e.getMedianMinDistance()));
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
