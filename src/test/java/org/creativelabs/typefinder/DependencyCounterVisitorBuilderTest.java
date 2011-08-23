package org.creativelabs.typefinder;

import japa.parser.ast.body.MethodDeclaration;
import org.creativelabs.introspection.ReflectionAbstractionImpl;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class DependencyCounterVisitorBuilderTest {

    @Test
    public void testBuilderConstruction() throws Exception {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        ImportList imports = ConstructionHelper.createEmptyImportList();
        VariableList fields = ConstructionHelper.createEmptyVariableList();
        VariableList methodArguments = ConstructionHelper.createEmptyVariableList();

        builder.setImports(imports);
        builder.setClassFields(fields);
        builder.setMethodArguments(methodArguments);

        DependencyCounterVisitor dependencyVisitor = builder.build();

        assertNotNull(dependencyVisitor);
    }

    @Test
    public void testBuilderConstructionNullImports() throws Exception {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        VariableList fields = ConstructionHelper.createEmptyVariableList();
        VariableList methodArguments = ConstructionHelper.createEmptyVariableList();

        builder.setClassFields(fields);
        builder.setMethodArguments(methodArguments);

        String hasException = "noException";
        try {
            DependencyCounterVisitor dependencyVisitor = builder.build();
        } catch (IllegalStateException e) {
            hasException = e.getMessage();
        }
        assertEquals("Imports can't be null", hasException);
    }

    @Test
    public void testBuilderConstructionNullFields() throws Exception {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        ImportList imports = ConstructionHelper.createEmptyImportList();
        VariableList methodArguments = ConstructionHelper.createEmptyVariableList();

        builder.setImports(imports);
        builder.setMethodArguments(methodArguments);

        String hasException = "noException";
        try {
            DependencyCounterVisitor dependencyVisitor = builder.build();
        } catch (IllegalStateException e) {
            hasException = e.getMessage();
        }
        assertEquals("Class fields can't be null", hasException);
    }

    @Test
    public void testBuilderConstructionNullMethodArgs() throws Exception {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        ImportList imports = ConstructionHelper.createEmptyImportList();
        VariableList classFields = ConstructionHelper.createEmptyVariableList();

        builder.setImports(imports);
        builder.setClassFields(classFields);

        String hasException = "noException";
        try {
            DependencyCounterVisitor dependencyVisitor = builder.build();
        } catch (IllegalStateException e) {
            hasException = e.getMessage();
        }
        assertEquals("Method arguments can't be null", hasException);
    }

    @Test(dependsOnGroups="parse-helper.create-method")
    public void testBuilderConstructMethodList() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String arg) {}");
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        ImportList imports = ConstructionHelper.createEmptyImportList();
        VariableList classFields = ConstructionHelper.createEmptyVariableList();

        builder.setReflectionAbstraction(ReflectionAbstractionImpl.create());
        builder.setImports(imports);
        builder.setClassFields(classFields);
        builder.setConstructedMethodArguments(md);

        DependencyCounterVisitor dependencyVisitor = builder.build();

        assertNotNull(dependencyVisitor);
        assertEquals("java.lang.String", 
                dependencyVisitor.classFields.getFieldTypeAsClass("arg").toString());
    }

    @Test(dependsOnGroups="parse-helper.create-method")
    public void testBuilderConstructMethodListNullImports() throws Exception {
        MethodDeclaration md = ParseHelper.createMethodDeclaration("void method(String arg) {}");
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        VariableList classFields = ConstructionHelper.createEmptyVariableList();

        builder.setClassFields(classFields);

        String result = null;
        try {
            builder.setConstructedMethodArguments(md);
        } catch (IllegalStateException e) {
            result = e.getMessage();
        }

        assertEquals("Imports should be not null for this operation", result);
    }

}
