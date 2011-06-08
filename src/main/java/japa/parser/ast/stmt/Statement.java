package japa.parser.ast.stmt;


import japa.parser.ast.Node;
import org.creativelabs.ssa.holder.SimpleUsingModifyingVariablesHolder;
import org.creativelabs.ssa.PhiNode;
import org.creativelabs.ssa.holder.UsingModifyingVariablesHolder;

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
    private UsingModifyingVariablesHolder variablesHolder = new SimpleUsingModifyingVariablesHolder();

    public Set<PhiNode> getPhiNodes() {
        return phiNodes;
    }

    public void addPhi(PhiNode phiNode){
        phiNodes.add(phiNode);
    }

    public void removePhiNodes(){
        phiNodes.clear();
    }

    public UsingModifyingVariablesHolder getVariablesHolder() {
        return variablesHolder;
    }

}
