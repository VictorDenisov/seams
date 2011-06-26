package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;
import org.creativelabs.ssa.holder.variable.Variable;

import java.util.Set;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:53
 */
public interface UsingModifyingVariablesHolder extends Copyable {

    void addUsingVariable(Variable variableName);
    void addUsingVariables(Set<Variable> variableNames);
    public Set<Variable> getUsingVariables();

    void addModifyingVariable(Variable variableName);
    void addModifyingVariables(Set<Variable> variableNames);
    Set<Variable> getModifyingVariables();

    void addCreatingVariable(Variable variableName);
    void addCreatingVariables(Set<Variable> variableNames);
    Set<Variable> getCreatingVariables();

    void add(UsingModifyingVariablesHolder holder);
}
