package org.creativelabs; 

import japa.parser.ast.expr.*;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class DependencyCounterVisitorTest {

    @Test
    public void testVisitNameExpr() throws Exception {
        Map<String, String> mp = new HashMap<String, String>();
        mp.put("name", "String");

        DependencyCounterVisitor dc = new DependencyCounterVisitor(mp, null);

        NameExpr expr = (NameExpr)ParseHelper.createExpression("name");

        dc.visit(expr, null);

        Set<Dependency> deps = dc.getDependencies();
        assertEquals("name", deps.iterator().next().getExpression());
        assertEquals("String", deps.iterator().next().getType());
    }
}
