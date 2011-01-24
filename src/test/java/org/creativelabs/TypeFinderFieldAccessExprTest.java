package org.creativelabs;

import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import org.creativelabs.introspection.*;
import org.testng.annotations.*;
import java.util.*;
import java.lang.reflect.*;

import java.io.File;

import org.creativelabs.introspection.ReflectionAbstractionImplTest;

import static org.testng.AssertJUnit.assertEquals;
import static org.creativelabs.AssertHelper.*;
import static org.mockito.Mockito.*;

public class TypeFinderFieldAccessExprTest {

    private ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

    @Test
    public void testDetermineTypeFieldAccessExpr() throws Exception {
        FieldAccessExpr expr = (FieldAccessExpr) ParseHelper
                .createExpression("str.CASE_INSENSITIVE_ORDER");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);
        assertEquals("java.util.Comparator<java.lang.String, >", type.toString());
    }

    @Test
    public void testDetermineTypeNonStandardClassFieldAccess() throws Exception {
        Expression expr = ParseHelper.createExpression("LogLevel.DEBUG");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.lf5.LogLevel;");

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("org.apache.log4j.lf5.LogLevel", type.toString());
    }

    @Test
    public void testSuperLiteralInFieldAccessExpression() throws Exception {
        SuperExpr expr = (SuperExpr) ((FieldAccessExpr) ParseHelper.createExpression("super.someField")).getScope();

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("super", new ClassTypeStub("org.creativelabs.A"));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());
    }

    @Test(enabled = false)
    public void testFieldAccess() throws Exception {
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();
        Expression expr = ParseHelper.createExpression("clazz.typeDeclaration");
        VariableList varList = VariableList.createEmpty();
        varList.put("typeDeclaration", ra.getClassTypeByName("java.lang.String"));
        varList.put("clazz", ra.getClassTypeByName("org.creativelabs.ClassProcessor"));
        ImportList imports = ParseHelper.createImportList("");

        TypeFinder typeFinder = new TypeFinder(ra, varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("japa.parser.ast.body.ClassOrInterfaceDeclaration", type.toString());
    }

}
