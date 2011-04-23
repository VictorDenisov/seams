package japa.parser.ast.stmt;


import japa.parser.ast.Node;
import org.creativelabs.ssa.PhiNode;

import java.util.HashSet;
import java.util.Set;

public abstract class Statement extends Node {

    public Statement() {
    }

    public Statement(int beginLine, int beginColumn, int endLine, int endColumn) {
        super(beginLine, beginColumn, endLine, endColumn);
    }


    private Set<PhiNode> phiNodes = new HashSet<PhiNode>();

    public Set<PhiNode> getPhiNodes() {
        return phiNodes;
    }

    public void addPhi(PhiNode phiNode){
        phiNodes.add(phiNode);
    }

    public void removePhiNodes(){
        phiNodes.clear();
    }

}
