package org.creativelabs.ssa.holder.variable;

import org.creativelabs.copy.Copyable;

/**
 * @author azotcsit
 *         Date: 11.06.11
 *         Time: 21:34
 */
public interface Variable extends Comparable<Variable>, Copyable {
    String getString();
    String getName();
    String getScope();
}
