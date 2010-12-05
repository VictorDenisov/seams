package org.creativelabs;

import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class FieldListTest {

    @Test
    public void testConstructor() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = null;

        FieldList fieldList = new FieldList(classDeclaration, imports);

        assertEqualsList(Arrays.asList(new String[]{"v"}), fieldList.getNames());
    }

    @Test
    public void testTypeAsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = null;

        FieldList fieldList = new FieldList(classDeclaration, imports);

        assertEquals("int", fieldList.getFieldTypeAsString("v"));
    }

    @Test
    public void testTypeAsStringTypeIsString() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { String str; }");

        ImportList imports = null;

        FieldList fieldList = new FieldList(classDeclaration, imports);

        assertEquals("String", fieldList.getFieldTypeAsString("str"));
    }
}
