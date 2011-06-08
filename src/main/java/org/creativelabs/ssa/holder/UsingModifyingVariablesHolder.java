package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;

import java.util.Set;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:53
 */
public interface UsingModifyingVariablesHolder extends Copyable {

    void addUsingVariable(String variableName);
    void addUsingVariables(Set<String> variableNames);
    public Set<String> getUsingVariables();

    void addModifyingVariable(String variableName);
    void addModifyingVariables(Set<String> variableNames);
    Set<String> getModifyingVariables();

    void add(UsingModifyingVariablesHolder holder);
}
