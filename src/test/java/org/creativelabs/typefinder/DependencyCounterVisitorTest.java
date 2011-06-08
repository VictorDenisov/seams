package org.creativelabs.typefinder;

import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.type.*;
import japa.parser.ast.stmt.*;

import org.creativelabs.introspection.*;

import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;

public class DependencyCounterVisitorTest {

    @Test
    public void testVisitNameExpr() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String name; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = ConstructionHelper
            .createVariableListFromClassFields(classDeclaration, imports);

        DependencyCounterVisitor dc = new DependencyCounterVisitor(fieldList, imports);

        NameExpr expr = (NameExpr)ParseHelper.createExpression("name");

        dc.visit(expr, null);

        Set<Dependency> deps = dc.getDependencies();

        assertEquals("name", deps.iterator().next().getExpression());
        assertEquals("java.lang.String", deps.iterator().next().getType().toString());
    }

    @Test
    public void testVisitNameExprClass() throws Exception {
        ImportList importList = ConstructionHelper.createEmptyImportList();
        DependencyCounterVisitor dc = new DependencyCounterVisitor(
                ConstructionHelper.createEmptyVariableList(), importList);

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

    @Test
    public void testVisitVariableDeclarationExpr() throws Exception {
        ImportList imports = spy(ParseHelper.createImportList("import java.util.*;"));
        Expression expr = ParseHelper.createExpression("Map.Entry<String, String> entry;");
        VariableList varList = ConstructionHelper.createEmptyVariableList();

        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(varList, imports);
        expr.accept(dependencyCounter, null);

        verify(imports, atLeastOnce()).getClassByType(any(Type.class));
    }

    @Test(dependsOnGroups="parse-helper.create-stmt")
    public void testExceptionProcessing() throws Exception {
        Statement expr = ParseHelper.createStatement("try {} catch (Exception e) {}");

        ImportList imports = ParseHelper.createImportList("");
        VariableList classFields = ConstructionHelper.createEmptyVariableList();

        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(classFields, imports);
        expr.accept(dependencyCounter, null);
        assertEquals("java.lang.Exception", 
                dependencyCounter.localVariables.getFieldTypeAsClass("e").toString());
    }

    @Test(dependsOnGroups = "parse-helper.create-stmt", enabled=false)
    public void testForeachProcessing() throws Exception {
        Statement expr = ParseHelper.createStatement(
                "for (Map.Entry<String, Integer> entry : map) {}");
        ImportList imports = ParseHelper.createImportList("import java.util.Map;");
        VariableList classFields = ConstructionHelper.createEmptyVariableList();

        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(classFields, imports);
        expr.accept(dependencyCounter, null);

        assertEquals("java.util.Map$Entry<java.lang.String, java.lang.Integer, >",
                dependencyCounter.localVariables.getFieldTypeAsClass("entry").toString());
    }

    @Test
    public void testVisitFieldAccessExpr_LongClassName() throws Exception {
        Expression expr = ParseHelper.createExpression("java.lang.String");

        ImportList imports = ConstructionHelper.createEmptyImportList();
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(varList, imports);

        expr.accept(dependencyCounter, null);

        assertEquals(1, dependencyCounter.getDependencies().size());
    }

    @Test
    public void testVisitNameExprUsesTypeFinder() throws Exception {
        Expression expr = ParseHelper.createExpression("name");

        ImportList imports = ConstructionHelper.createEmptyImportList();
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        DependencyCounterVisitor dependencyCounter = 
            spy(new DependencyCounterVisitor(varList, imports));

        expr.accept(dependencyCounter, null);
        verify(dependencyCounter).runTypeFinder(any(Expression.class));
    }

    @Test
    public void testPrimitiveArrayDeclaration() throws Exception {
        Statement expr = ParseHelper.createStatement("byte buffer[];");

        ImportList imports = ConstructionHelper.createEmptyImportList();
        VariableList varList = ConstructionHelper.createEmptyVariableList();

        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(varList, imports);
        expr.accept(dependencyCounter, null);
        ClassType result = dependencyCounter.localVariables.getFieldTypeAsClass("buffer");
        assertEquals("[B", result.toString());
    }
}