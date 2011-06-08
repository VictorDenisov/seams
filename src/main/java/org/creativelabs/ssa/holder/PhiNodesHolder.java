package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;
import org.creativelabs.ssa.PhiNode;

import java.util.Set;

/**
 * @author azotcsit
 *         Date: 22.05.11
 *         Time: 22:34
 */
public interface PhiNodesHolder extends Copyable {
    Set<PhiNode> getPhiNodes();
    void setPhiNodes(Set<PhiNode> phiNodes);
    void addPhiNodes(Set<PhiNode> phiNodes);
    void addPhiNode(PhiNode phiNode);
}
