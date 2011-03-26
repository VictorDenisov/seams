package japa.parser.ast.stmt;


import org.creativelabs.ssa.PhiNode;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;


/**
 * For other subclasses of the Statement mechanism of insertion phi nodes is the same.
 */
public class StatementTest {

    @Test
    public void testAddAndGetPhiNodeFromBlockStmt(){
        Statement blockStmt = new BlockStmt();
        Set<PhiNode> phiNodes = new HashSet<PhiNode>();

        PhiNode phiNode = new PhiNode("v", 2, 0, 1);
        phiNodes.add(phiNode);
        blockStmt.addPhi(phiNode);

        phiNode = new PhiNode("x", 3, 0, 2);
        phiNodes.add(phiNode);
        blockStmt.addPhi(phiNode);

        assertEquals(phiNodes, blockStmt.getPhiNodes());
    }

}
