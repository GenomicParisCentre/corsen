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

package fr.ens.transcriptome.corsen.calc;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class implements a javascript filter for distances.
 * @author Laurent Jourdren
 */
public class JavascriptDistancesFilter implements DistancesFilter {

  private CompiledScript script;
  private Bindings bindings;

  /**
   * Test if a distance must be filtered.
   * @param distance Particle3D to test
   * @return true if the particle is accepted
   */
  public boolean accept(final Distance distance) {

    if (this.script == null)
      return true;

    if (distance == null)
      return false;

    final Bindings b = this.bindings;

    b.put("distance", distance.getDistance());

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
  public static JavascriptDistancesFilter createFilter(final String expression) {

    JavascriptDistancesFilter result = new JavascriptDistancesFilter();
    if (result.setExpression(expression))
      return result;

    return null;
  }

  //
  // Constructor
  //

  private JavascriptDistancesFilter() {
  }
}
