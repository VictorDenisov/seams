package org.creativelabs.ssa;

import japa.parser.ParseException;
import japa.parser.ast.expr.AssignExpr;
import org.creativelabs.ParseHelper;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class AssignVisitorTest {

    @Test
    public void testAssignExpression() throws ParseException {
        Set<String> set = new HashSet<String>();
        AssignExpr assignExpr = (AssignExpr) ParseHelper.createExpression("x = 1");
        new AssignVisitor().visit(assignExpr, set);
        assertEquals(new HashSet<String>(){{add("x");}}, set);
    }
}
