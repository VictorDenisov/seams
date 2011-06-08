package org.creativelabs.ssa.holder;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:11
 */
public class SimpleMethodArgsHolder implements MethodArgsHolder {

    private Set<String> argsNames;

    public SimpleMethodArgsHolder() {
        argsNames = new TreeSet<String>();
    }

    public SimpleMethodArgsHolder(Set<String> argsNames) {
        this.argsNames = argsNames;
    }

    @Override
    public void addArgName(String argName) {
        argsNames.add(argName);
    }

    @Override
    public void addArgNames(Collection<String> argNames) {
        argsNames.addAll(argNames);
    }

    @Override
    public Set<String> getArgsNames() {
        return argsNames;
    }

    @Override
    public void setArgsNames(Set<String> argsNames) {
        this.argsNames = argsNames;
    }

    @Override
    public boolean containsArgName(String argName) {
        return argsNames.contains(argName);
    }

    @Override
    public int getCountOfArgNames() {
        return argsNames.size();
    }

    @Override
    public SimpleMethodArgsHolder copy() {
        SimpleMethodArgsHolder holder = new SimpleMethodArgsHolder();
        holder.addArgNames(argsNames);
        return holder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleMethodArgsHolder holder = (SimpleMethodArgsHolder) o;

        if (argsNames != null ? !argsNames.equals(holder.argsNames) : holder.argsNames != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return argsNames != null ? argsNames.hashCode() : 0;
    }
}
