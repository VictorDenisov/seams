package japa.parser.ast.stmt;


import japa.parser.ast.Node;
import org.creativelabs.ssa.PhiNode;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * It's hacked class.
 * Has been added information about using, modifying variables into statement and phi nodes.
 *
 * @author azotov
 */
public abstract class Statement extends Node {

    public Statement() {
    }

    public Statement(int beginLine, int beginColumn, int endLine, int endColumn) {
        super(beginLine, beginColumn, endLine, endColumn);
    }

    private Set<PhiNode> phiNodes = new HashSet<PhiNode>();

    private Set<String> usingVariables = new TreeSet<String>();
    private Set<String> modifyingVariables = new TreeSet<String>();

    public Set<PhiNode> getPhiNodes() {
        return phiNodes;
    }

    public void addPhi(PhiNode phiNode){
        phiNodes.add(phiNode);
    }

    public void removePhiNodes(){
        phiNodes.clear();
    }

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
