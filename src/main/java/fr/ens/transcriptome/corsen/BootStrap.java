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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sun.javaws.JnlpxArgs;
import com.sun.javaws.cache.DiskCacheEntry;
import com.sun.javaws.cache.DownloadProtocol;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.jnl.ApplicationDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.JREDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.jnlp.JNLPClassLoader;

/**
 * This class is used to launch an application build with QtJambi via Java Web
 * Start on Mac OS X.
 * @author Laurent Jourdren
 */
public final class BootStrap {

  private static final String JAVA_PATH_MACOS =
      "/System/Library/Frameworks/"
          + "JavaVM.framework/Versions/CurrentJDK/Commands/java";

  private List<String> classpath = new ArrayList<String>();
  private String mainClass;
  private String[] args;
  private Map<String, String> properties = new HashMap<String, String>();
  private String vmArgs;
  private long maxHeap;
  private long minHeap;

  private static void save(final File f, final String s) {

    try {
      Writer writer = new OutputStreamWriter(new FileOutputStream(f));
      writer.write(s);
      writer.close();
    } catch (IOException e) {
    }

  }

  private void collectInfos(final JNLPClassLoader cl) {

    final LaunchDesc ld = cl.getLaunchDesc();

    collectClasspath(ld);
    collectMainClass(ld);
    collectProperties(ld);
    collectJVM(ld.getResources().getSelectedJRE());
  }

  private void collectClasspath(final LaunchDesc ld) {

    final List<JARDesc> jarDescs = new ArrayList<JARDesc>();

    ResourcesDesc rd = ld.getResources();

    if (rd != null) {

      JARDesc[] jars = rd.getEagerOrAllJarDescs(true);

      for (int i = 0; i < jars.length; i++)
        if (jars[i].isJavaFile() || jars[i].isNativeLib())
          jarDescs.add(jars[i]);
    }

    for (JARDesc jd : jarDescs) {

      URL location = jd.getLocation();
      String version = jd.getVersion();

      try {
        DiskCacheEntry dce =
            DownloadProtocol.getResource(location, version,
                DownloadProtocol.JAR_DOWNLOAD, true, null);
        this.classpath.add(dce.getFile().getAbsolutePath());

      } catch (JNLPException e) {
      }
    }

  }

  private void collectMainClass(final LaunchDesc ld) {

    // If applet exit.
    if (ld.isApplet())
      System.exit(0);

    ApplicationDesc ad = ld.getApplicationDescriptor();
    this.mainClass = ad.getMainClass();
    this.args = ad.getArguments();
  }

  private void collectJVM(final JREDesc jreDesc) {

    this.maxHeap = JnlpxArgs.getMaxHeapSize();
    this.minHeap = JnlpxArgs.getInitialHeapSize();
    this.vmArgs = jreDesc.getVmArgs();
  }

  private void collectProperties(final LaunchDesc ld) {

    ResourcesDesc rd = ld.getResources();
    Properties ps = rd.getResourceProperties();
    Iterator it = ps.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      this.properties.put(key, ps.getProperty(key));
    }
  }

  /**
   * Launch the bootstrap.
   */
  public static void bootstrap() {

    final ClassLoader cl = BootStrap.class.getClassLoader();

    if (cl == null)
      return;

    if (cl instanceof JNLPClassLoader) {

      BootStrap b = new BootStrap();
      b.collectInfos((JNLPClassLoader) cl);
      String cmd = b.createCommandLine();
      save(new File(System.getProperty("user.home")
          + File.separator + "javacmd.txt"), cmd);
      exec(cmd);
      System.exit(0);
    }
  }

  private String createCommandLine() {

    StringBuffer sb = new StringBuffer();

    sb.append(JAVA_PATH_MACOS);

    // Set classpath
    sb.append(" -cp ");
    boolean first = true;
    for (String path : this.classpath) {
      if (first)
        first = false;
      else
        sb.append(":");
      sb.append(path);
    }

    // Set Java properties
    for (String key : this.properties.keySet()) {

      String value = this.properties.get(key);

      sb.append(" -D");
      sb.append(key);
      sb.append("=");
      sb.append(value);
    }

    // Set vm args
    if (this.vmArgs != null) {
      sb.append(" ");
      sb.append(this.vmArgs);
    }

    // Set initial heap
    if (this.minHeap != -1) {
      sb.append(" -Xms");
      sb.append(this.minHeap);
    }

    // Set max heap
    if (this.maxHeap != -1) {
      sb.append(" -Xmx");
      sb.append(this.maxHeap);
    }

    // Main class
    sb.append(" ");
    sb.append(this.mainClass);

    // Args

    for (int i = 0; i < this.args.length; i++) {
      sb.append(" ");
      sb.append(this.args[i]);
    }

    return sb.toString();
  }

  private static void exec(final String cmd) {

    Runtime rt = Runtime.getRuntime();

    try {
      rt.exec(cmd);
    } catch (IOException e) {
    }
  }

  //
  // Constructor
  //

  private BootStrap() {
  }

}
