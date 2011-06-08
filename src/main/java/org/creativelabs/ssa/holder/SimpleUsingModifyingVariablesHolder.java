package org.creativelabs.ssa.holder;

import java.util.Set;
import java.util.TreeSet;

/**
 * Stores using and modifying in expressions and statements variables.
 *
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 14:07
 */
public class SimpleUsingModifyingVariablesHolder implements UsingModifyingVariablesHolder {

    private Set<String> usingVariables;
    private Set<String> modifyingVariables;

    public SimpleUsingModifyingVariablesHolder() {
        usingVariables = new TreeSet<String>();
        modifyingVariables = new TreeSet<String>();
    }

    public SimpleUsingModifyingVariablesHolder(Set<String> usingVariables, Set<String> modifyingVariables) {
        this.usingVariables = usingVariables;
        this.modifyingVariables = modifyingVariables;
    }

    @Override
    public Set<String> getUsingVariables() {
        return usingVariables;
    }

    @Override
    public void addUsingVariable(String variableName) {
        usingVariables.add(variableName);
    }

    @Override
    public void addUsingVariables(Set<String> variableNames) {
        usingVariables.addAll(variableNames);
    }

    @Override
    public Set<String> getModifyingVariables() {
        return modifyingVariables;
    }

    @Override
    public void addModifyingVariable(String variableName) {
        modifyingVariables.add(variableName);
    }

    @Override
    public void addModifyingVariables(Set<String> variableNames) {
        modifyingVariables.addAll(variableNames);
    }

    @Override
    public void add(UsingModifyingVariablesHolder holder) {
        addUsingVariables(holder.getUsingVariables());
        addModifyingVariables(holder.getModifyingVariables());
    }

    @Override
    public SimpleUsingModifyingVariablesHolder copy() {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        holder.add(this);
        return holder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleUsingModifyingVariablesHolder holder = (SimpleUsingModifyingVariablesHolder) o;

        if (modifyingVariables != null ? !modifyingVariables.equals(holder.modifyingVariables) : holder.modifyingVariables != null)
            return false;
        if (usingVariables != null ? !usingVariables.equals(holder.usingVariables) : holder.usingVariables != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = usingVariables != null ? usingVariables.hashCode() : 0;
        result = 31 * result + (modifyingVariables != null ? modifyingVariables.hashCode() : 0);
        return result;
    }
}
