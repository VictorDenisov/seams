package org.creativelabs;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*; 
import japa.parser.ast.type.*; 
import org.testng.annotations.Test;
import org.creativelabs.introspection.*;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class VariableListTest {

    @Test
    public void testTypeAsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = ConstructionHelper
            .createVariableListFromClassFields(classDeclaration, imports);

        assertEquals("int", fieldList.getFieldTypeAsClass("v").toString());
    }

    @Test
    public void testTypeAsStringTypeIsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = ConstructionHelper
            .createVariableListFromClassFields(classDeclaration, imports);

        assertEquals("java.lang.String", fieldList.getFieldTypeAsClass("str").toString());
    }

    @Test
    public void testHasName() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = ConstructionHelper.createEmptyImportList();

        VariableList fieldList = ConstructionHelper
            .createVariableListFromClassFields(classDeclaration, imports);

        assertTrue(fieldList.hasName("str"));
    }

    @Test
    public void testPut() throws Exception {
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("name", new ClassTypeStub("String"));
        assertEquals("String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testPutClass() throws Exception {
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("name", new ClassTypeStub(String.class.getName()));
        assertEquals("java.lang.String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testAddAll() throws Exception {
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        VariableList fullList = ConstructionHelper.createEmptyVariableList();
        fullList.put("name", new ClassTypeStub("String"));

        varList.addAll(fullList);

        assertEquals("String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testTypeAsClass() throws Exception {
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("name", new ClassTypeStub("java.lang.String"));

        assertEquals("java.lang.String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testTypeAsClassInt() throws Exception {
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("name", new ClassTypeStub("int"));

        String clazz = varList.getFieldTypeAsClass("name").toString();
        assertEquals("int", clazz);
    }

    @Test
    public void testIfNameIsAbsentReturnNotNull() throws Exception {
        VariableList varList = ConstructionHelper.createEmptyVariableList();

        ClassType value = varList.getFieldTypeAsClass("name");

        assertNotNull(value);
        assertEquals("name doesn't exist", value.toString());
    }


}
