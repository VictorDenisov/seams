package org.creativelabs;

import japa.parser.ParseException;
import japa.parser.ast.expr.AssignExpr;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AssignVisitorTest {

    @Test
    public void testAssignExpression() throws ParseException {
        List<String> list = new ArrayList<String>();
        AssignExpr assignExpr = (AssignExpr) ParseHelper.createExpression("x = 1");
        new AssignVisitor().visit(assignExpr, list);
        assertEquals(Arrays.asList("x"), list);
    }
}
