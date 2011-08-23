package org.creativelabs;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.stmt.BlockStmt;
import org.creativelabs.introspection.ReflectionAbstraction;
import org.creativelabs.introspection.ReflectionAbstractionImpl;
import org.creativelabs.report.ReportBuilder;
import org.creativelabs.ssa.holder.SimpleMultiHolderBuilder;
import org.creativelabs.typefinder.*;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class ClassProcessorTest {

    private static VariableList methodArgumentsList;

    private static class DependencyCounterVisitorToStringLogger extends DependencyCounterVisitor {
        private StringBuffer logString;

        DependencyCounterVisitorToStringLogger(StringBuffer logString, 
                VariableList classFields, ImportList imports, ReflectionAbstraction ra) {
            super(classFields, imports, ra);
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
                    null, null, (null));
        }
    }

    private DependencyCounterVisitorBuilder 
        createEmptyDependencyCounterBuilder(StringBuffer logString) throws Exception {

        DependencyCounterVisitorToStringLoggerBuilder dependencyVisitorBuilder 
            = new DependencyCounterVisitorToStringLoggerBuilder();
        dependencyVisitorBuilder.logString = logString;
        dependencyVisitorBuilder
            .setReflectionAbstraction(ReflectionAbstractionImpl.create())
            .setImports(ConstructionHelper.createEmptyImportList())
            .setClassFields(ConstructionHelper.createEmptyVariableList());

        return dependencyVisitorBuilder;
    }

    @Test(dependsOnGroups="variable-list.method-construction")
    public void testCompute() throws Exception {
        ClassOrInterfaceDeclaration classDecl = ParseHelper.createClassDeclaration("class Test { void method(String arg) {}}");
        StringBuffer logBuffer = new StringBuffer();
        methodArgumentsList = null;

        DependencyCounterVisitorBuilder builder = createEmptyDependencyCounterBuilder(logBuffer);
        
        ClassProcessor classProcessor = new ClassProcessor(classDecl, builder, new SimpleMultiHolderBuilder());
        classProcessor.compute();

        assertEquals("build; visit BlockStmt; ", logBuffer.toString());

        assertEquals(1, methodArgumentsList.getNames().size());
        assertEquals("arg", methodArgumentsList.getNames().get(0));

    }

    @Test
    public void testBuildReport() throws Exception {
        ClassOrInterfaceDeclaration typeDeclaration =
            ParseHelper.createClassDeclaration("class Main { }");

        ClassProcessor classProcessor = new ClassProcessor(typeDeclaration, null, new SimpleMultiHolderBuilder());
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
