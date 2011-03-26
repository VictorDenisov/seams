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

public class VariableListBuilderTest {

    @Test
    public void testConstructor() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration = 
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = ConstructionHelper.createEmptyImportList();
        imports = spy(imports);

        VariableList fieldList = new VariableListBuilder()
            .setImports(imports)
            .buildFromClass(classDeclaration);

        assertEqualsList(Arrays.asList(new String[]{"v"}), fieldList.getNames());
        verify(imports).getClassByType(any(Type.class));
    }

    @Test
    public void testConstructionFromClass() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration =
            ParseHelper.createClassDeclaration("class Main { int[] a; String v[];}");

        ImportList imports = ConstructionHelper.createEmptyImportList();

        VariableList fieldList = new VariableListBuilder()
            .setImports(imports)
            .buildFromClass(classDeclaration);
        
        List<String> names = fieldList.getNames();
        Collections.sort(names);
        assertEqualsList(Arrays.asList(new String[]{"a", "v"}), names);
        assertEquals("[I", fieldList.getFieldTypeAsClass("a").toString());
        assertEquals("[Ljava.lang.String;", fieldList.getFieldTypeAsClass("v").toString());
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethod() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String arg) {}");

        ImportList imports = ParseHelper.createImportList("");
        imports = spy(imports);

        VariableList varList = new VariableListBuilder()
            .setImports(imports)
            .buildFromMethod(md);

        assertEquals(1, varList.getNames().size());
        assertEquals("java.lang.String", varList.getFieldTypeAsClass("arg").toString());
        verify(imports).getClassByType(any(Type.class));
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethodEmptyArgList() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method() {}");

        ImportList imports = ParseHelper.createImportList("");

        VariableList varList = new VariableListBuilder()
            .setImports(imports)
            .buildFromMethod(md);

        assertEquals(0, varList.getNames().size());
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethodArgumentsVarArgs() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String... args) {}");

        ImportList imports = ConstructionHelper.createEmptyImportList();

        VariableList varList = new VariableListBuilder()
            .setImports(imports)
            .buildFromMethod(md);

        assertEquals("[Ljava.lang.String;", varList.getFieldTypeAsClass("args").toString());
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethodArgumentsArrays() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String args[]) {}");
        ImportList imports = ConstructionHelper.createEmptyImportList();

        VariableList varList = new VariableListBuilder()
            .setImports(imports)
            .buildFromMethod(md);

        assertEquals("[Ljava.lang.String;", varList.getFieldTypeAsClass("args").toString());
    }

}
