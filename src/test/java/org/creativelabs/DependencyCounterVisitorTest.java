package org.creativelabs; 

import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.type.*;
import japa.parser.ast.stmt.*;

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

    @Test
    public void testVisitVariableDeclarationExpr() throws Exception {
        ImportList imports = spy(ParseHelper.createImportList("import java.util.*;"));
        Expression expr = ParseHelper.createExpression("Map.Entry<String, String> entry;");
        VariableList varList = VariableList.createEmpty();

        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(varList, imports);
        expr.accept(dependencyCounter, null);

        verify(imports, atLeastOnce()).getClassByType(any(Type.class));
    }

    @Test(dependsOnGroups="parse-helper.create-stmt")
    public void testExceptionProcessing() throws Exception {
        Statement expr = ParseHelper.createStatement("try {} catch (Exception e) {}");

        ImportList imports = ParseHelper.createImportList("");
        VariableList classFields = VariableList.createEmpty();

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
        VariableList classFields = VariableList.createEmpty();

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
}
