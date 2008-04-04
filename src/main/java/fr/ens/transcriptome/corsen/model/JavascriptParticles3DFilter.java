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

package fr.ens.transcriptome.corsen.model;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavascriptParticles3DFilter implements Particles3DFilter {

  private CompiledScript script;
  private Bindings bindings;

  /**
   * Test if a particle must be filtered.
   * @param particle Particle3D to test
   * @return true if the particle is accepted
   */
  public boolean accept(final Particle3D particle) {

    if (this.script == null)
      return true;

    if (particle == null)
      return false;

    final Bindings b = this.bindings;

    b.put("area", particle.getArea());
    b.put("density", particle.getDensity());
    b.put("intensity", particle.getIntensity());
    b.put("sphericity", particle.getSphericity());
    b.put("volume", particle.getVolume());

    try {

      Object o = this.script.eval();

      if (o instanceof Boolean && ((Boolean) o) == true)
        return true;

    } catch (ScriptException e1) {
    }

    return false;
  }

  private boolean setExpression(final String expression) {

    this.script = null;

    if (expression == null || "".equals(expression.trim()))
      return true;

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

    Compilable compilable = (Compilable) engine;

    try {

      final CompiledScript script = compilable.compile(expression);
      this.script = script;
      this.bindings =
          script.getEngine().getBindings(ScriptContext.ENGINE_SCOPE);

    } catch (ScriptException e) {

      return false;
    }

    return true;
  }

  /**
   * Create a filter.
   * @param expression Javascript exception to set
   * @return the filter of null if the expression is mal formed
   */
  public static JavascriptParticles3DFilter createFilter(final String expression) {

    JavascriptParticles3DFilter result = new JavascriptParticles3DFilter();
    if (result.setExpression(expression))
      return result;

    return null;
  }

  //
  // Constructor
  //

  private JavascriptParticles3DFilter() {
  }
}
