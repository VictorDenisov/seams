package japa.parser.ast.helper;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Stores using and modifying in expressions and statements variables.
 *
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 14:07
 */
public class UMVariablesHolder implements Serializable {

    private Set<String> usingVariables = new TreeSet<String>();
    private Set<String> modifyingVariables = new TreeSet<String>();

    public Set<String> getUsingVariables() {
        return usingVariables;
    }

    public void addUsingVariable(String variableName) {
        usingVariables.add(variableName);
    }

    public void addUsingVariables(Set<String> variableNames) {
        usingVariables.addAll(variableNames);
    }

    public Set<String> getModifyingVariables() {
        return modifyingVariables;
    }

    public void addModifyingVariable(String variableName) {
        modifyingVariables.add(variableName);
    }

    public void addModifyingVariables(Set<String> variableNames) {
        modifyingVariables.addAll(variableNames);
    }

}
