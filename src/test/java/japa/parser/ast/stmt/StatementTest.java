package japa.parser.ast.stmt;


import org.creativelabs.Constants;
import org.creativelabs.ssa.PhiNode;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * For other subclasses of the Statement mechanism of insertion phi nodes is the same.
 */
public class StatementTest {

    @Test
    public void testAddAndGetPhiNodeFromBlockStmt(){
        Statement blockStmt = new BlockStmt();
        Set<PhiNode> phiNodes = new HashSet<PhiNode>();

        PhiNode phiNode = new PhiNode(new StringVariable("x", Constants.ARG_SCOPE), 2, 0, 1);
        phiNodes.add(phiNode);

        blockStmt.addPhi(phiNode);

        assertEquals(phiNodes, blockStmt.getPhiNodes());
    }

}
