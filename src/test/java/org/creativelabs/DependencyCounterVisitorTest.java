package org.creativelabs; 

import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class DependencyCounterVisitorTest {

    @Test
    public void testVisitNameExpr() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String name; }");

        VariableList fieldList = new VariableList(classDeclaration, null);

        DependencyCounterVisitor dc = new DependencyCounterVisitor(fieldList, null);

        NameExpr expr = (NameExpr)ParseHelper.createExpression("name");

        dc.visit(expr, null);

        Set<Dependency> deps = dc.getDependencies();
        assertEquals("name", deps.iterator().next().getExpression());
        assertEquals("String", deps.iterator().next().getType());
    }

    @Test
    public void testVisitBlockStmtForEmptyMethod() throws Exception {
        BlockStmt blockStmt = ParseHelper.createBlockStmt("public void method(){}");

        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(null, null);

        String result = "noException";
        try {
            dependencyCounter.visit(blockStmt, null);
        } catch (NullPointerException e) {
            result = "NullPointerException";
        }
        assertEquals("noException", result);
    }
}
