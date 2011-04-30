package japa.parser.ast.stmt;


import japa.parser.ast.Node;
import japa.parser.ast.helper.UMVariablesHolder;
import org.creativelabs.ssa.PhiNode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * It's hacked class.
 * Has been added information about using, modifying variables into statement and phi nodes.
 *
 * @author azotov
 */
public abstract class Statement extends Node implements Serializable {

    public Statement() {
    }

    public Statement(int beginLine, int beginColumn, int endLine, int endColumn) {
        super(beginLine, beginColumn, endLine, endColumn);
    }

    private Set<PhiNode> phiNodes = new HashSet<PhiNode>();
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
