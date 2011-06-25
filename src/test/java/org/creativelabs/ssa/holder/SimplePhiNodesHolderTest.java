package org.creativelabs.ssa.holder;

import org.creativelabs.ssa.PhiNode;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 22.05.11
 *         Time: 22:36
 */
public class SimplePhiNodesHolderTest {

    @Test
    public void testSetPhiNodes() throws Exception {
        SimplePhiNodesHolder holder = new SimplePhiNodesHolder();
        PhiNode phiNode1 = new PhiNode(new StringVariable("name1", "scope1"), 2, 0, 1);
        PhiNode phiNode2 = new PhiNode(new StringVariable("name2", "scope2"), 2, 0, 1);
        Set<PhiNode> phiNodes = new HashSet<PhiNode>();
        phiNodes.add(phiNode1);
        phiNodes.add(phiNode2);
        holder.setPhiNodes(phiNodes);
        assertEquals(2, holder.getPhiNodes().size());
        assertEquals(phiNodes, holder.getPhiNodes());
    }

    @Test
    public void testAddPhiNodes() throws Exception {
        SimplePhiNodesHolder holder = new SimplePhiNodesHolder();
        PhiNode phiNode1 = new PhiNode(new StringVariable("name1", "scope1"), 2, 0, 1);
        PhiNode phiNode2 = new PhiNode(new StringVariable("name2", "scope2"), 2, 0, 1);
        Set<PhiNode> phiNodes = new HashSet<PhiNode>();
        phiNodes.add(phiNode1);
        phiNodes.add(phiNode2);
        holder.addPhiNodes(phiNodes);
        assertEquals(2, holder.getPhiNodes().size());
        assertEquals(phiNodes, holder.getPhiNodes());
    }

    @Test
    public void testAddPhiNode() throws Exception {
        SimplePhiNodesHolder holder = new SimplePhiNodesHolder();
        PhiNode phiNode1 = new PhiNode(new StringVariable("name1", "scope1"), 2, 0, 1);
        PhiNode phiNode2 = new PhiNode(new StringVariable("name2", "scope2"), 2, 0, 1);
        holder.addPhiNode(phiNode1);
        holder.addPhiNode(phiNode2);
        assertEquals(2, holder.getPhiNodes().size());
    }

    @Test
    public void testCopy() throws Exception {
        SimplePhiNodesHolder holder = new SimplePhiNodesHolder();
        SimplePhiNodesHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }
}
