package org.creativelabs; 

import org.testng.annotations.*;
import japa.parser.ast.expr.*;
import org.creativelabs.introspection.*;
import java.util.*;

import static org.testng.AssertJUnit.*;

public class ExpressionSeparatorVisitorTest {

    @Test
    public void testExcludedClasses() throws Exception {
        ObjectCreationExpr expr = (ObjectCreationExpr) ParseHelper.createExpression("new ArrayList()");
        InternalInstancesGraph internalInstances = new InternalInstancesGraph();
        Set<String> excludedClasses = new HashSet<String>();
        excludedClasses.add("ArrayList");
        ExpressionSeparatorVisitor visitor = 
            new ExpressionSeparatorVisitor(internalInstances, excludedClasses);
        visitor.visit(expr, null);
        boolean value = visitor.isAssignedInternalInstance();
        assertEquals(false, value);
    }
}
