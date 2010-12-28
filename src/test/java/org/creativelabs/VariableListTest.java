package org.creativelabs;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*; 
import org.testng.annotations.Test;
import org.creativelabs.introspection.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class VariableListTest {

    @Test
    public void testConstructor() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertEqualsList(Arrays.asList(new String[]{"v"}), fieldList.getNames());
    }

    @Test
    public void testTypeAsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertEquals("int", fieldList.getFieldTypeAsClass("v").toStringRepresentation());
    }

    @Test
    public void testTypeAsStringTypeIsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertEquals("java.lang.String", fieldList.getFieldTypeAsClass("str").toStringRepresentation());
    }

    @Test
    public void testHasName() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertTrue(fieldList.hasName("str"));
    }

    @Test
    public void testPut() throws Exception {
        VariableList varList = new VariableList();
        varList.put("name", new ClassTypeStub("String"));
        assertEquals("String", varList.getFieldTypeAsClass("name").toStringRepresentation());
    }

    @Test
    public void testPutClass() throws Exception {
        VariableList varList = new VariableList();
        varList.put("name", new ClassTypeStub(String.class.getName()));
        assertEquals("java.lang.String", varList.getFieldTypeAsClass("name").toStringRepresentation());
    }

    @Test
    public void testAddAll() throws Exception {
        VariableList varList = new VariableList();
        VariableList fullList = new VariableList();
        fullList.put("name", new ClassTypeStub("String"));

        varList.addAll(fullList);

        assertEquals("String", varList.getFieldTypeAsClass("name").toStringRepresentation());
    }

    @Test
    public void testTypeAsClass() throws Exception {
        VariableList varList = new VariableList();
        varList.put("name", new ClassTypeStub("java.lang.String"));

        assertEquals("java.lang.String", varList.getFieldTypeAsClass("name").toStringRepresentation());
    }

    @Test
    public void testTypeAsClassInt() throws Exception {
        VariableList varList = new VariableList();
        varList.put("name", new ClassTypeStub("int"));

        String clazz = varList.getFieldTypeAsClass("name").toStringRepresentation();
        assertEquals("int", clazz);
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethod() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String arg) {}");

        ImportList imports = ParseHelper.createImportList("");

        VariableList varList = new VariableList(md, imports);

        assertEquals(1, varList.getNames().size());
        assertEquals("java.lang.String", varList.getFieldTypeAsClass("arg").toStringRepresentation());
    }

}
