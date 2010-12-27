package org.creativelabs;

import org.testng.annotations.Test;

import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.*;

import org.creativelabs.introspection.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

public class ClassProcessorTest {

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
            .setClassFields(new VariableList());

        return dependencyVisitorBuilder;
    }

    @Test
    public void testCompute() throws Exception {
        ClassOrInterfaceDeclaration classDecl = ParseHelper.createClassDeclaration("class Test { void method() {}}");
        StringBuffer logBuffer = new StringBuffer();

        DependencyCounterVisitorBuilder builder = createEmptyDependencyCounterBuilder(logBuffer);
        
        ClassProcessor classProcessor = new ClassProcessor(classDecl, builder);
        classProcessor.compute();

        assertEquals("visit BlockStmt; ", logBuffer.toString());
    }

}
