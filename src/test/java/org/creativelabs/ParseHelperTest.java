package org.creativelabs; 

import org.testng.annotations.Test;
import org.testng.annotations.Configuration;
import org.creativelabs.TypeFinder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;

import java.util.Arrays;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

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

}

