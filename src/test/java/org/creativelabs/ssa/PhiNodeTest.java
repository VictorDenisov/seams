package org.creativelabs.ssa;


import org.creativelabs.Constants;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;

public class PhiNodeTest {

    @Test
    public void testCreationOfExprStmt() {
        PhiNode phiNode = new PhiNode(new StringVariable("x", Constants.THIS_SCOPE), 2, 0, 1);

        String expectedResult = "this.x#2 = #phi(this.x#0, this.x#1);";

        String actualResult = phiNode.convertToExprStmt().toString();

        assertEquals(expectedResult, actualResult);
    }

}
