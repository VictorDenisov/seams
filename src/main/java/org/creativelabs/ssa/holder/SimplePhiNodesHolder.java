package org.creativelabs.ssa.holder;

import org.creativelabs.copy.CopyingUtils;
import org.creativelabs.ssa.PhiNode;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author azotcsit
 *         Date: 22.05.11
 *         Time: 22:35
 */
public class SimplePhiNodesHolder implements PhiNodesHolder {

    Set<PhiNode> phiNodes;

    public SimplePhiNodesHolder() {
        phiNodes = new TreeSet<PhiNode>();
    }

    public SimplePhiNodesHolder(Set<PhiNode> phiNodes) {
        this.phiNodes = phiNodes;
    }

    @Override
    public Set<PhiNode> getPhiNodes() {
        return phiNodes;
    }

    @Override
    public void setPhiNodes(Set<PhiNode> phiNodes) {
        this.phiNodes = phiNodes;
    }

    @Override
    public void addPhiNodes(Set<PhiNode> phiNodes) {
        this.phiNodes.addAll(phiNodes);
    }

    @Override
    public void addPhiNode(PhiNode phiNode) {
        this.phiNodes.add(phiNode);
    }

    @Override
    public SimplePhiNodesHolder copy() {
        return CopyingUtils.<SimplePhiNodesHolder>copy(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePhiNodesHolder that = (SimplePhiNodesHolder) o;

        if (phiNodes != null ? !phiNodes.equals(that.phiNodes) : that.phiNodes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return phiNodes != null ? phiNodes.hashCode() : 0;
    }
}
