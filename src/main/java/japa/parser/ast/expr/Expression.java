package japa.parser.ast.expr;

import japa.parser.ast.Node;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * It's hacked class.
 * Has been added information about using, modifying variables into expressions.
 *
 * @author azotcsit
 *         Date: 24.04.11
 *         Time: 18:27
 */
public abstract class Expression extends Node implements Serializable {

    public Expression() {
    }

    public Expression(int beginLine, int beginColumn, int endLine, int endColumn) {
        super(beginLine, beginColumn, endLine, endColumn);
    }

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
