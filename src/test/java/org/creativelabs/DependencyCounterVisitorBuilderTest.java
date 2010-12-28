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
        VariableList fields = new VariableList();
        VariableList methodArguments = new VariableList();

        builder.setImports(imports);
        builder.setClassFields(fields);
        builder.setMethodArguments(methodArguments);

        DependencyCounterVisitor dependencyVisitor = builder.build();

        assertNotNull(dependencyVisitor);
    }

    @Test
    public void testBuilderConstructionNullImports() throws Exception {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();

        VariableList fields = new VariableList();
        VariableList methodArguments = new VariableList();

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
        VariableList methodArguments = new VariableList();

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
        VariableList classFields = new VariableList();

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
        VariableList classFields = new VariableList();

        builder.setImports(imports);
        builder.setClassFields(classFields);
        builder.setConstructedMethodArguments(md);

        DependencyCounterVisitor dependencyVisitor = builder.build();

        assertNotNull(dependencyVisitor);
    }

}
