package org.creativelabs.typefinder;

import japa.parser.ast.expr.ObjectCreationExpr;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.iig.SimpleInternalInstancesGraph;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

public class ExpressionSeparatorVisitorTest {

    @Test
    public void testExcludedClasses() throws Exception {
        ObjectCreationExpr expr = (ObjectCreationExpr) ParseHelper.createExpression("new ArrayList()");
        InternalInstancesGraph internalInstances = new SimpleInternalInstancesGraph();
        Set<String> excludedClasses = new HashSet<String>();
        excludedClasses.add("ArrayList");
        ExpressionSeparatorVisitor visitor =
            new ExpressionSeparatorVisitor(internalInstances, excludedClasses);
        visitor.visit(expr, null);
        boolean value = visitor.isAssignedInternalInstance();
        assertEquals(false, value);
    }
}
