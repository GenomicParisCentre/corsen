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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import fr.ens.transcriptome.corsen.calc.ParticleType;
import fr.ens.transcriptome.corsen.gui.CLIGui;
import fr.ens.transcriptome.corsen.gui.CorsenFakeGui;
import fr.ens.transcriptome.corsen.gui.qt.CorsenQt;
import fr.ens.transcriptome.corsen.gui.swing.CorsenSwing;
import fr.ens.transcriptome.corsen.util.Util;

/**
 * This class is the main class of Corsen. It launch the bootstrap is needed and
 * then call the ui.
 * @author Laurent Jourdren
 */
public final class Corsen {

  private static Logger logger = Logger.getLogger(Globals.APP_NAME);

  private static Settings settings;
  private static boolean batchMode;
  private static String batchFile;
  private static String[] mainArgs;
  private static boolean confFile;

  /**
   * Get the instance of the settings of the application.
   * @return the setting of the application
   */
  public static Settings getSettings() {

    return settings;
  }

  /**
   * Test if the application is currently in batch mode.
   * @return true if the application is in batch mode
   */
  public static boolean isBatchMode() {

    return batchMode;
  }

  private static void bootstrap() {

    if (!System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
      return;

    try {
      Corsen.class.getClassLoader().loadClass("com.sun.jnlp.JNLPClassLoader");
      BootStrap.bootstrap();
    } catch (ClassNotFoundException e) {
    }
  }

  /**
   * Main method.
   * @param args command line arguments
   * @throws IOException if an error occurs while executing Corsen
   */
  public static void main(final String[] args) throws IOException {

    bootstrap();

    logger.setLevel(Globals.LOG_LEVEL);

    parseCommandLine(args);

    if (batchFile != null) {
      if (!confFile)
        loadSettings(null);
      batchMode = true;
      executeBatchFile();
      return;
    }

    if (mainArgs != null && mainArgs.length == 3) {

      CLIGui.main(mainArgs);
      return;
    }

    switch (Globals.DEFAULT_GUI) {

    case FAKE:
      CorsenFakeGui.main(args);
      return;

    case SWING:
      if (!confFile)
        loadSettings(null);
      CorsenSwing.main(args);
      return;

    case QT:
      if (!confFile)
        loadSettings(null);
      CorsenQt.main(args);
      return;

    default:
      System.err.println("No GUI defined.");
      System.exit(1);
      break;
    }

  }

  private static void loadSettings(final String confFilename) {

    Settings s = new Settings();
    try {
      s.loadSettings(confFilename);
    } catch (IOException e) {
    }

    s.addSettings(settings);
    settings = s;
  }

  /**
   * Show licence information about this application.
   */
  private static void about() {

    System.out.println(Globals.ABOUT_TXT);
    System.exit(0);
  }

  /**
   * Show information about this application.
   */
  private static void licence() {

    System.out.println(Globals.LICENCE_TXT);
    System.exit(0);
  }

  private static void help(final Options options) {

    // Show help message
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(Globals.APP_NAME
        + " particleA_file particleB_file output_prefix", options);

    System.exit(0);
  }

  /**
   * Create the Options object for the command line.
   * @return The Option object
   */
  private static Options makeOptions() {

    Option help = new Option("help", "show this message");
    Option about = new Option("about", "show information this software");
    Option licence =
        new Option("licence",
            "show information about the licence of this software");

    Option batchFile =
        OptionBuilder.withArgName("batchfile").hasArg().withDescription(
            "batch file").create("batchFile");

    Option batch = new Option("batch", "batch mode");

    Option conf =
        OptionBuilder.withArgName("file").hasArg().withDescription(
            "configuration file").create("conf");
    Option typea =
        OptionBuilder.withArgName("typea").hasArg().withDescription(
            "particle A type").create("typea");
    Option typeb =
        OptionBuilder.withArgName("typeb").hasArg().withDescription(
            "particle B type").create("typeb");
    Option unit =
        OptionBuilder.withArgName("unit").hasArg().withDescription("unit")
            .create("unit");
    Option zfactor =
        OptionBuilder.withArgName("zfactor").hasArg().withDescription(
            "z factor").create("zfactor");
    Option factor =
        OptionBuilder.withArgName("factor").hasArg().withDescription(
            "coordinates factor").create("factor");
    Option threads =
        OptionBuilder.withArgName("number").hasArg().withDescription(
            "number of threads").create("threads");

    Option od = new Option("od", "create output data files");
    Option oiv = new Option("oiv", "create output iv files");
    Option or = new Option("or", "create output results files");

    Option ova =
        new Option("ova", "create output particle A R visualisation files");
    Option ovac =
        new Option("ovac",
            "create output particle A cuboids R visualisation files");
    Option ovb =
        new Option("ovb", "create output particle B R visualisation files");
    Option ovbc =
        new Option("ovbc",
            "create output particle B cuboids R visualisation files");
    Option ovd =
        new Option("ovd", "create output distances R visualisation files");

    Options options = new Options();

    options.addOption(help);
    options.addOption(about);
    options.addOption(licence);
    options.addOption(conf);
    options.addOption(batchFile);
    options.addOption(batch);
    options.addOption(threads);
    options.addOption(typea);
    options.addOption(typeb);
    options.addOption(unit);
    options.addOption(zfactor);
    options.addOption(factor);
    options.addOption(od);
    options.addOption(oiv);
    options.addOption(or);
    options.addOption(ova);
    options.addOption(ovac);
    options.addOption(ovb);
    options.addOption(ovbc);
    options.addOption(ovd);

    return options;
  }

  private static void parseCommandLine(final String[] args) {

    Options options = makeOptions();

    Settings s = new Settings();

    try {

      CommandLineParser parser = new GnuParser();

      int argsOptions = 0;

      // parse the command line arguments
      CommandLine line = parser.parse(options, args);

      if (line.hasOption("help"))
        help(options);

      if (line.hasOption("about"))
        about();

      if (line.hasOption("licence"))
        licence();

      if (line.hasOption("conf")) {
        loadSettings(line.getOptionValue("file"));
        s = settings;
        argsOptions++;
      }

      if (line.hasOption("typea")) {
        s.setParticlesAType(ParticleType.getParticleType(line
            .getOptionValue("type")));
        argsOptions++;
      }

      if (line.hasOption("typeb")) {
        s.setParticlesBType(ParticleType.getParticleType(line
            .getOptionValue("type")));
        argsOptions++;
      }

      if (line.hasOption("factor")) {
        s.setFactor(line.getOptionValue("factor"));
        argsOptions++;
      }

      if (line.hasOption("zfactor")) {
        s.setZFactor(line.getOptionValue("zfactor"));
        argsOptions++;
      }

      if (line.hasOption("od"))
        s.setSaveDataFile(true);

      if (line.hasOption("oiv"))
        s.setSaveIVFile(true);

      if (line.hasOption("or"))
        s.setSaveResultFile(true);

      if (line.hasOption("ova")) {
        s.setSaveVisualizationFiles(true);
        s.setSaveParticlesA3dFile(true);
      }

      if (line.hasOption("ovac")) {
        s.setSaveVisualizationFiles(true);
        s.setSaveParticlesACuboids3dFile(true);
      }

      if (line.hasOption("ovb")) {
        s.setSaveVisualizationFiles(true);
        s.setSaveParticlesB3dFile(true);
      }

      if (line.hasOption("ovbc")) {
        s.setSaveVisualizationFiles(true);
        s.setSaveParticlesBCuboids3dFile(true);
      }

      if (line.hasOption("ovd")) {
        s.setSaveVisualizationFiles(true);
        s.setSaveDistances3dFile(true);
      }

      if (line.hasOption("threads")) {

        String val = line.getOptionValue("threads");
        if (val != null) {

          try {
            s.setThreadNumber(Integer.parseInt(val.trim()));

          } catch (NumberFormatException e) {
          }
          argsOptions++;
        }
      }

      if (line.hasOption("batch"))
        batchMode = true;

      if (line.hasOption("batchFile"))
        batchFile = line.getOptionValue("batchFile");

      mainArgs = line.getArgs();

      if (mainArgs.length > 0 && mainArgs.length != 3)
        help(options);

    } catch (UnrecognizedOptionException exp) {
      System.err.println(exp.getMessage());
      System.exit(1);
    } catch (MissingArgumentException exp) {
      System.err.println(exp.getMessage());
      System.exit(1);
    }

    catch (ParseException exp) {
      System.err.println("Error analysing command line. ");
      System.exit(1);
    }

    settings = s;
  }

  /**
   * Execute corsen CLI in batch mode with batch file
   */
  public static void executeBatchFile() {

    final long startTime = System.currentTimeMillis();

    final List<String[]> dirs = new ArrayList<String[]>();

    boolean error = false;

    try {
      final FileReader fr = new FileReader(batchFile);

      BufferedReader br = new BufferedReader(fr);
      String line = null;

      boolean first = true;

      while ((line = br.readLine()) != null) {

        if (first) {

          first = false;
          continue;
        }

        String[] fields = line.split("\t");
        File dir = new File(fields[0]);
        final String prefixA = fields[1];
        final String prefixB = fields[2];

        if (!dir.exists()) {

          error = true;
          System.err.println("Directory not found : " + dir.getAbsolutePath());
        } else {

          File[] filesA = dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {

              return name.startsWith(prefixA);
            }
          });

          File[] filesB = dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {

              return name.startsWith(prefixB);
            }
          });

          if (filesA == null || filesA.length == 0) {
            System.err.println("Prefix a: "
                + prefixA + " in directory " + dir.getAbsolutePath());
            error = true;
          }

          if (filesB == null || filesB.length == 0) {
            System.err.println("Prefix b: "
                + prefixB + " in directory " + dir.getAbsolutePath());
            error = true;
          }

        }

        dirs.add(new String[] {fields[0], fields[1], fields[2]});

      }
    } catch (IOException e) {
      System.err.println("Error while reading batch file: " + batchFile);
    }

    if (!error)
      for (String[] fields : dirs)
        CLIGui.main(new String[] {fields[1], fields[2], fields[0]});

    final long endTime = System.currentTimeMillis();
    final long totalTime = endTime - startTime;

    System.out.println("Process all data in "
        + Util.toTimeHumanReadable(totalTime) + " (" + totalTime + "ms).");

  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private Corsen() {
  }

}