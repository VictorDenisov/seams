package org.creativelabs.typefinder;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.type.Type;
import org.creativelabs.introspection.ReflectionAbstractionImpl;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.creativelabs.typefinder.AssertHelper.assertEqualsList;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class VariableListBuilderTest {

    private VariableListBuilder variableListBuilder = new VariableListBuilder()
        .setReflectionAbstraction(ReflectionAbstractionImpl.create());

    @Test
    public void testConstructor() throws Exception {
        ClassOrInterfaceDeclaration classDeclaration =
            ParseHelper.createClassDeclaration("class Main { int v; }");

        ImportList imports = ConstructionHelper.createEmptyImportList();
        imports = spy(imports);

        VariableList fieldList = variableListBuilder
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

        VariableList fieldList = variableListBuilder
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

        VariableList varList = variableListBuilder
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

        VariableList varList = variableListBuilder
            .setImports(imports)
            .buildFromMethod(md);

        assertEquals(0, varList.getNames().size());
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethodArgumentsVarArgs() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String... args) {}");

        ImportList imports = ConstructionHelper.createEmptyImportList();

        VariableList varList = variableListBuilder
            .setImports(imports)
            .buildFromMethod(md);

        assertEquals("[Ljava.lang.String;", varList.getFieldTypeAsClass("args").toString());
    }

    @Test(groups="variable-list.method-construction", dependsOnGroups="parse-helper.create-method")
    public void testConstructionFromMethodArgumentsArrays() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String args[]) {}");
        ImportList imports = ConstructionHelper.createEmptyImportList();

        VariableList varList = variableListBuilder
            .setImports(imports)
            .buildFromMethod(md);

        assertEquals("[Ljava.lang.String;", varList.getFieldTypeAsClass("args").toString());
    }

}
