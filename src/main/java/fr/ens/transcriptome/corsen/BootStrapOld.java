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

package fr.ens.transcriptome.corsen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sun.javaws.cache.Cache;
import com.sun.javaws.cache.DiskCacheEntry;
import com.sun.javaws.jnl.ExtensionDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.jnlp.JNLPClassLoader;

public class BootStrapOld {

  private static final String PS_LINUX_CMD = "ps -ocmd -p ";
  private static final String PS_OSX_CMD = "ps -ww -o command -p ";
  
  public static final String getCmdLine() {

    int pid = getPid();

    String s = "" + pid + "\n";

    String c = isMacOsX() ? PS_OSX_CMD : PS_LINUX_CMD;

    String cmd = execAndGetLastLine(c + pid);

    s = s + c + "\n" + cmd + "\n";

    /*
     * save(new File(System.getProperty("user.home") + File.separator +
     * "javacmd.txt"), s);
     */

    return s;
  }

  public static String execAndGetLastLine(String cmd) {

    String result = null;

    try {

      Process p = Runtime.getRuntime().exec(cmd);

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p
          .getInputStream()));

      // read the output from the command
      String s = null;
      while ((s = stdInput.readLine()) != null) {
        result = s;
      }

    } catch (IOException e) {
      return null;
    }

    return result;
  }

  public static int getPid() {

    // throws IOException, InterruptedException

    ProcessBuilder pb = new ProcessBuilder(new String[] {"/bin/bash", "-c",
        "echo $PPID"});

    try {
      Process pr = pb.start();
      pr.waitFor();

      if (pr.exitValue() == 0) {
        BufferedReader outReader = new BufferedReader(new InputStreamReader(pr
            .getInputStream()));
        return Integer.parseInt(outReader.readLine().trim());
      }
    } catch (IOException e) {
      return -1;
    } catch (InterruptedException e) {
      return -1;
    } catch (NumberFormatException e) {
      return -1;
    }

    return -1;
  }
  
  /**
   * Test if the system is Mac OS X.
   * @return true if the system is Mac OS X
   */
  public static boolean isMacOsX() {
    return System.getProperty("os.name").toLowerCase().startsWith("mac os x");
  }
  
  public static void oldCode(JNLPClassLoader cl) {

    LaunchDesc ld = cl.getLaunchDesc();

    StringBuffer sb = new StringBuffer();
    sb.append("#");
    sb.append(new Date(System.currentTimeMillis()));
    sb.append("\n");

    sb.append("location=");
    sb.append(ld.getLocation());
    sb.append("\nversion=");
    sb.append(ld.getSpecVersion());
    sb.append("\n");

    // sb.append("ld=" + ld);

    List<JARDesc> listJars = new ArrayList<JARDesc>();

    ResourcesDesc rd = ld.getResources();

    addJar(rd, listJars);

    // sb.append("getLocalJarDescs=" + Arrays.toString(rd.getLocalJarDescs()));

    for (JARDesc jd : listJars) {

      sb.append(jd.getLocation());
      sb.append("\t");
      try {
        sb.append(Cache.getCachedLaunchedFile(jd.getLocation()));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
      }

      sb.append("\t");
      sb.append(Cache.getCachedFile(jd.getLocation()));
      sb.append("\n");
    }

    sb.append(getCmdLine());
    sb.append("\n");

    sb.append("---1 ---\n");

    Iterator it = Cache.getJnlpCacheEntries(true);
    while (it.hasNext()) {

      Object o = it.next();
      sb.append(o.getClass().getName());
      sb.append("\t");
      sb.append(o);
      sb.append("\n");
    }

    sb.append("---2 ---\n");

    it = Cache.getJnlpCacheEntries(false);
    while (it.hasNext()) {

      Object o = it.next();
      sb.append(o.getClass().getName());
      sb.append("\t");
      sb.append(o);
      sb.append("\n");
    }

    sb.append("--- 3 ---\n");

    try {
      DiskCacheEntry[] entries = Cache.getCacheEntries(Cache.APPLICATION_TYPE,
          ld.getLocation(), null, true);

      for (int i = 0; i < entries.length; i++) {
        sb.append(entries[i].getLocation());
        sb.append("\t");
        sb.append(entries[i].getFile());
        sb.append("\n");
      }

      entries = Cache.getCacheEntries(Cache.RESOURCE_TYPE, ld.getCodebase(),
          null, false);

      for (int i = 0; i < entries.length; i++) {
        sb.append(entries[i].getLocation());
        sb.append("\t");
        sb.append(entries[i].getFile());
        sb.append("\n");
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

   
  }
  
  private static void addJar(final ResourcesDesc rd,
      final List<JARDesc> listJars) {

    ExtensionDesc[] exts = rd.getExtensionDescs();

    for (int i = 0; i < exts.length; i++)
      addJar(exts[i].getExtensionDesc().getResources(), listJars);

    JARDesc[] array = rd.getLocalJarDescs();
    listJars.addAll(Arrays.asList(array));
  }
  
}
