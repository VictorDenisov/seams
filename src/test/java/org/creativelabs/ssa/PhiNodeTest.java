package org.creativelabs.ssa;


import org.creativelabs.ssa.PhiNode;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

public class PhiNodeTest {

    @Test
    public void testCreationOfExprStmt() {
        PhiNode phiNode = new PhiNode("x", 2, 0, 1);

        String expectedResult = "x#2 = #phi(x#0, x#1);";

        String actualResult = phiNode.convertToExprStmt().toString();

        assertEquals(expectedResult, actualResult);
    }

}
