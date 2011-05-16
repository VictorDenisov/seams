package japa.parser.ast.stmt;


import japa.parser.ast.Node;
import org.creativelabs.ssa.UMVariablesHolder;
import org.creativelabs.ssa.PhiNode;

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

    private Set<PhiNode> phiNodes = new TreeSet<PhiNode>();
    private UMVariablesHolder variablesHolder = new UMVariablesHolder();

    public Set<PhiNode> getPhiNodes() {
        return phiNodes;
    }

    public void addPhi(PhiNode phiNode){
        phiNodes.add(phiNode);
    }

    public void removePhiNodes(){
        phiNodes.clear();
    }

    public UMVariablesHolder getVariablesHolder() {
        return variablesHolder;
    }

}
