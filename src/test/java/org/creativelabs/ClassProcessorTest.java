package org.creativelabs;

import org.testng.annotations.Test;

import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.*;

import org.creativelabs.introspection.*;
import org.creativelabs.report.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

public class ClassProcessorTest {

    private static VariableList methodArgumentsList;

    private static class DependencyCounterVisitorToStringLogger extends DependencyCounterVisitor {
        private StringBuffer logString;

        DependencyCounterVisitorToStringLogger(StringBuffer logString, 
                VariableList classFields, ImportList imports) {
            super(classFields, imports);
            this.logString = logString;
        }

        @Override
        public void visit(BlockStmt n, Object o) {
            logString.append("visit BlockStmt; "); 
        }

    }

    private static class DependencyCounterVisitorToStringLoggerBuilder 
            extends DependencyCounterVisitorBuilder {
        private StringBuffer logString;
        @Override
        public DependencyCounterVisitor build() {
            super.build();
            methodArgumentsList = methodArguments;
            logString.append("build; ");
            return new DependencyCounterVisitorToStringLogger(logString,
                    null, null);
        }
    }

    private DependencyCounterVisitorBuilder 
        createEmptyDependencyCounterBuilder(StringBuffer logString) throws Exception {

        DependencyCounterVisitorToStringLoggerBuilder dependencyVisitorBuilder 
            = new DependencyCounterVisitorToStringLoggerBuilder();
        dependencyVisitorBuilder.logString = logString;
        dependencyVisitorBuilder
            .setImports(ParseHelper.createImportList(""))
            .setClassFields(VariableList.createEmpty());

        return dependencyVisitorBuilder;
    }

    @Test(dependsOnGroups="variable-list.method-construction")
    public void testCompute() throws Exception {
        ClassOrInterfaceDeclaration classDecl = ParseHelper.createClassDeclaration("class Test { void method(String arg) {}}");
        StringBuffer logBuffer = new StringBuffer();
        methodArgumentsList = null;

        DependencyCounterVisitorBuilder builder = createEmptyDependencyCounterBuilder(logBuffer);
        
        ClassProcessor classProcessor = new ClassProcessor(classDecl, builder);
        classProcessor.compute();

        assertEquals("build; visit BlockStmt; ", logBuffer.toString());

        assertEquals(1, methodArgumentsList.getNames().size());
        assertEquals("arg", methodArgumentsList.getNames().get(0));

    }

    @Test
    public void testBuildReport() throws Exception {
        ClassOrInterfaceDeclaration typeDeclaration =
            ParseHelper.createClassDeclaration("class Main { }");

        ClassProcessor classProcessor = new ClassProcessor(typeDeclaration, null);
        HashMap dependencies = new HashMap();
        HashMap internalInstances = new HashMap();
        classProcessor.dependencies = dependencies;
        classProcessor.internalInstances = internalInstances;

        ReportBuilder reportBuilder = mock(ReportBuilder.class);

        classProcessor.buildReport(reportBuilder);

        verify(reportBuilder).setDependencies(eq("Main"), eq(dependencies));
        verify(reportBuilder).setInternalInstances(eq("Main"), eq(internalInstances));
    }

}
