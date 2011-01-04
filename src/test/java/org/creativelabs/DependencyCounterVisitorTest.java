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

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = VariableList.createFromClassFields(classDeclaration, imports);

        DependencyCounterVisitor dc = new DependencyCounterVisitor(fieldList, imports);

        NameExpr expr = (NameExpr)ParseHelper.createExpression("name");

        dc.visit(expr, null);

        Set<Dependency> deps = dc.getDependencies();
        assertEquals("name", deps.iterator().next().getExpression());
        assertEquals("java.lang.String", deps.iterator().next().getType().toString());
    }

    @Test
    public void testVisitNameExprClass() throws Exception {
        ImportList importList = ParseHelper.createImportList("");
        DependencyCounterVisitor dc = new DependencyCounterVisitor(VariableList.createEmpty(), importList);

        MethodCallExpr expr = (MethodCallExpr)ParseHelper.createExpression("String.valueOf(true)");

        dc.visit(expr, null);

        Set<Dependency> deps = dc.getDependencies();
        assertEquals("java.lang.String", deps.iterator().next().getType().toString());
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

    @Test
    public void testVisitBlockStmtForInterfaceMethod() throws Exception {
        BlockStmt blockStmt = ParseHelper.createBlockStmt("public void method();");

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
