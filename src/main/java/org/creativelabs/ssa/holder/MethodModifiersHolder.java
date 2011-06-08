package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;

import java.util.List;

/**
 * @author azotcsit
 *         Date: 21.05.11
 *         Time: 11:54
 */
public interface MethodModifiersHolder extends Copyable {
    int getModifier(String methodName, List<String> argsTypes);
}
