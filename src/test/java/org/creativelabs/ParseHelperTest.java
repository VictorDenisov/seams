package org.creativelabs; 

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.creativelabs.AssertHelper.assertEqualsList;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class ParseHelperTest {

    @Test(groups="parse-helper")
    public void testCreateExpression() throws Exception {
        Expression expr = ParseHelper.createExpression("main.methodCall()");

        assertEquals("class japa.parser.ast.expr.MethodCallExpr", expr.getClass().toString());
    }

    @Test(groups="parse-helper")
    public void testCreateCompilationUnit() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("package org.sample;"
                + "import org.apache.log4j.Logger;"
                + "class Main {}");
        assertEquals("org.sample", cu.getPackage().getName().toString());
        assertEquals(1, cu.getImports().size());
        assertEquals("org.apache.log4j.Logger", cu.getImports().get(0).getName().toString());
    }

    @Test(groups="parse-helper")
    public void testCreateImportList() throws Exception {
        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.Logger;");
        assertEqualsList(Arrays.asList(new String[]{"java.lang", "org.apache.log4j.Logger"}), 
                imports.getImports());
    }

    @Test(groups="parse-helper")
    public void testCreateClassDeclaration() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");
        assertEquals("Main", classDeclaration.getName());
    }

    @Test(groups="parse-helper.create-method")
    public void testCreateMethodDeclaration() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String arg) {}");

        assertEquals("method", md.getName());
    }

    @Test(groups="parse-helper.create-type.construction")
    public void testCreateType() throws Exception {
        Type type = ParseHelper.createType("ArrayList<String>");

        ReferenceType rt = (ReferenceType) type;
        ClassOrInterfaceType cit = (ClassOrInterfaceType) rt.getType();
        assertEquals(1, cit.getTypeArgs().size());
        assertEquals("String", cit.getTypeArgs().get(0).toString());
        assertEquals("ArrayList<String>", rt.getType() + "");
        assertEquals("ArrayList<String>", type.toString());
    }

    @Test(groups="parse-helper.create-type.primitive-types")
    public void testCreatePrimitiveTypes() throws Exception {
        Type type = ParseHelper.createType("int");

        assertEquals("PrimitiveType", type.getClass().getSimpleName());
    }

    @Test(groups="parse-helper.create-type.reference-types")
    public void testCreateReferenceTypes() throws Exception {
        Type type = ParseHelper.createType("Integer");
        ClassOrInterfaceType cit = (ClassOrInterfaceType) ((ReferenceType) type).getType();

        assertEquals("ReferenceType", type.getClass().getSimpleName());
        assertNull(cit.getScope());
    }

    @Test(groups="parse-helper.create-type.nested-types")
    public void testCreateNestedTypes() throws Exception {
        Type type = ParseHelper.createType("Map.Entry<String, String>");
        ClassOrInterfaceType classType = (ClassOrInterfaceType) ((ReferenceType)type).getType();

        assertEquals("ReferenceType", type.getClass().getSimpleName());
        assertEquals("Entry", classType.getName());
        assertEquals("Map", classType.getScope().getName());
    }

    @Test(groups="parse-helper.create-stmt")
    public void testCreateStatement() throws Exception {
        Statement statement = ParseHelper.createStatement("System.out.println();");

        assertEquals("ExpressionStmt", statement.getClass().getSimpleName());
    }
}

