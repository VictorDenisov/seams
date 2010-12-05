package org.creativelabs; 

import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class DependencyCounterVisitorTest {

    @Test
    public void testVisitNameExpr() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String name; }");

        FieldList fieldList = new FieldList(classDeclaration, null);

        DependencyCounterVisitor dc = new DependencyCounterVisitor(fieldList, null);

        NameExpr expr = (NameExpr)ParseHelper.createExpression("name");

        dc.visit(expr, null);

        Set<Dependency> deps = dc.getDependencies();
        assertEquals("name", deps.iterator().next().getExpression());
        assertEquals("String", deps.iterator().next().getType());
    }
}
