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
    public void testConstructor() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = ParseHelper.createImportList("");
        imports = spy(imports);

        VariableList fieldList = VariableList.createFromClassFields(classDeclaration, imports);

        assertEqualsList(Arrays.asList(new String[]{"v"}), fieldList.getNames());
        verify(imports).getClassByType(any(Type.class));
    }

    @Test
    public void testTypeAsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = VariableList.createFromClassFields(classDeclaration, imports);

        assertEquals("int", fieldList.getFieldTypeAsClass("v").toString());
    }

    @Test
    public void testTypeAsStringTypeIsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = VariableList.createFromClassFields(classDeclaration, imports);

        assertEquals("java.lang.String", fieldList.getFieldTypeAsClass("str").toString());
    }

    @Test
    public void testHasName() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = ParseHelper.createImportList("");

        VariableList fieldList = VariableList.createFromClassFields(classDeclaration, imports);

        assertTrue(fieldList.hasName("str"));
    }

    @Test
    public void testPut() throws Exception {
        VariableList varList = VariableList.createEmpty();
        varList.put("name", new ClassTypeStub("String"));
        assertEquals("String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testPutClass() throws Exception {
        VariableList varList = VariableList.createEmpty();
        varList.put("name", new ClassTypeStub(String.class.getName()));
        assertEquals("java.lang.String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testAddAll() throws Exception {
        VariableList varList = VariableList.createEmpty();
        VariableList fullList = VariableList.createEmpty();
        fullList.put("name", new ClassTypeStub("String"));

        varList.addAll(fullList);

        assertEquals("String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testTypeAsClass() throws Exception {
        VariableList varList = VariableList.createEmpty();
        varList.put("name", new ClassTypeStub("java.lang.String"));

        assertEquals("java.lang.String", varList.getFieldTypeAsClass("name").toString());
    }

    @Test
    public void testTypeAsClassInt() throws Exception {
        VariableList varList = VariableList.createEmpty();
        varList.put("name", new ClassTypeStub("int"));

        String clazz = varList.getFieldTypeAsClass("name").toString();
        assertEquals("int", clazz);
    }

    @Test
    public void testIfNameIsAbsentReturnNotNull() throws Exception {
        VariableList varList = VariableList.createEmpty();

        ClassType value = varList.getFieldTypeAsClass("name");

        assertNotNull(value);
        assertEquals("name doesn't exist", value.toString());
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethod() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String arg) {}");

        ImportList imports = ParseHelper.createImportList("");
        imports = spy(imports);

        VariableList varList = VariableList.createFromMethodArguments(md, imports);

        assertEquals(1, varList.getNames().size());
        assertEquals("java.lang.String", varList.getFieldTypeAsClass("arg").toString());
        verify(imports).getClassByType(any(Type.class));
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethodEmptyArgList() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method() {}");

        ImportList imports = ParseHelper.createImportList("");

        VariableList varList = VariableList.createFromMethodArguments(md, imports);

        assertEquals(0, varList.getNames().size());
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethodArgumentsVarArgs() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String... args) {}");

        ImportList imports = ConstructionHelper.createEmptyImportList();

        VariableList varList = VariableList.createFromMethodArguments(md, imports);

        assertEquals("[Ljava.lang.String;", varList.getFieldTypeAsClass("args").toString());
    }

}
