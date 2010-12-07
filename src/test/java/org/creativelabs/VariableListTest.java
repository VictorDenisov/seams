package org.creativelabs;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class VariableListTest {

    @Test
    public void testConstructor() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = null;

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertEqualsList(Arrays.asList(new String[]{"v"}), fieldList.getNames());
    }

    @Test
    public void testTypeAsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = null;

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertEquals("int", fieldList.getFieldTypeAsString("v"));
    }

    @Test
    public void testTypeAsStringTypeIsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = null;

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertEquals("String", fieldList.getFieldTypeAsString("str"));
    }

    @Test
    public void testHasName() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = null;

        VariableList fieldList = new VariableList(classDeclaration, imports);

        assertTrue(fieldList.hasName("str"));
    }

    @Test
    public void testPut() throws Exception {
        VariableList varList = new VariableList();
        varList.put("name", "String");
        assertEquals("String", varList.getFieldTypeAsString("name"));
    }

    @Test
    public void testPutClass() throws Exception {
        VariableList varList = new VariableList();
        varList.put("name", String.class);
        assertEquals("java.lang.String", varList.getFieldTypeAsString("name"));
    }

    @Test
    public void testAddAll() throws Exception {
        VariableList varList = new VariableList();
        VariableList fullList = new VariableList();
        fullList.put("name", "String");

        varList.addAll(fullList);

        assertEquals("String", varList.getFieldTypeAsString("name"));
    }

    @Test
    public void testTypeAsClass() throws Exception {
        VariableList varList = new VariableList();
        varList.put("name", "String");

        assertEquals(String.class, varList.getFieldTypeAsClass("name"));
    }

}
