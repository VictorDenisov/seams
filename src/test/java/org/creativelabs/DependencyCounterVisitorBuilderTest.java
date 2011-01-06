package org.creativelabs;

import org.testng.annotations.Test;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;

import static org.testng.AssertJUnit.*;

public class DependencyCounterVisitorBuilderTest {

    @Test
    public void testBuilderConstruction() throws Exception {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        ImportList imports = ParseHelper.createImportList("");
        VariableList fields = VariableList.createEmpty();
        VariableList methodArguments = VariableList.createEmpty();

        builder.setImports(imports);
        builder.setClassFields(fields);
        builder.setMethodArguments(methodArguments);

        DependencyCounterVisitor dependencyVisitor = builder.build();

        assertNotNull(dependencyVisitor);
    }

    @Test
    public void testBuilderConstructionNullImports() throws Exception {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        VariableList fields = VariableList.createEmpty();
        VariableList methodArguments = VariableList.createEmpty();

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

        ImportList imports = ParseHelper.createImportList("");
        VariableList methodArguments = VariableList.createEmpty();

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

        ImportList imports = ParseHelper.createImportList("");
        VariableList classFields = VariableList.createEmpty();

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

        ImportList imports = ParseHelper.createImportList("");
        VariableList classFields = VariableList.createEmpty();

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

        VariableList classFields = VariableList.createEmpty();

        builder.setClassFields(classFields);

        String result = null;
        try {
            builder.setConstructedMethodArguments(md);
        } catch (IllegalStateException e) {
            result = e.getMessage();
        }

        assertEquals("Imports should be not null for this operation", result);
    }

    @Test(dependsOnGroups="parse-helper.create-stmt")
    public void testExceptionProcessing() throws Exception {
        Statement expr = ParseHelper.createStatement("try {} catch (Exception e) {}");

        ImportList imports = ParseHelper.createImportList("");
        VariableList classFields = VariableList.createEmpty();

        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(classFields, imports);
        expr.accept(dependencyCounter, null);
        assertEquals("java.lang.Exception", 
                dependencyCounter.localVariables.getFieldTypeAsClass("e").toString());
    }

}
