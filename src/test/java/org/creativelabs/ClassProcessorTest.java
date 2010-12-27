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

        private StringBuffer logString = new StringBuffer();

        DependencyCounterVisitorToStringLogger(VariableList classFields, ImportList imports) {
            super(classFields, imports);
        }

        @Override
        public void visit(BlockStmt n, Object o) {
            logString.append("visit BlockStmt; "); 
        }

    }

    @Test
    public void testCompute() throws Exception {
        ClassOrInterfaceDeclaration classDecl = ParseHelper.createClassDeclaration("class Test { void method() {}}");
        StringBuffer answer = new StringBuffer();
        DependencyCounterVisitorToStringLogger dependencyVisitor = new DependencyCounterVisitorToStringLogger(null, null);
        
        ClassProcessor classProcessor = new ClassProcessor(classDecl, dependencyVisitor);
        classProcessor.compute();

        assertEquals("visit BlockStmt; ", dependencyVisitor.logString.toString());
    }

}
