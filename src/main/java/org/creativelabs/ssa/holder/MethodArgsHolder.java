package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;

import java.util.Collection;
import java.util.Set;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:07
 */
public interface MethodArgsHolder extends Copyable {
    void addArgName(String argName);
    void addArgNames(Collection<String> argNames);

    Set<String> getArgsNames();
    void setArgsNames(Set<String> argsNames);

    boolean containsArgName(String argName);

    int getCountOfArgNames();
}
