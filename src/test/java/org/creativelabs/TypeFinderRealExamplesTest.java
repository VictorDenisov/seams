package org.creativelabs;

import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import org.creativelabs.introspection.*;
import org.testng.annotations.*;
import java.util.*;
import java.lang.reflect.*;

import java.io.File;

import org.creativelabs.introspection.ReflectionAbstractionImplTest;

import static org.testng.AssertJUnit.assertEquals;
import static org.creativelabs.AssertHelper.*;
import static org.mockito.Mockito.*;

public class TypeFinderRealExamplesTest {

    private ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

    @Test
    public void testInputStreamRead() throws Exception {

        Expression expr = ParseHelper.createExpression("input.read(buffer)");

        VariableList varTypes = ConstructionHelper.createVarListWithValues(ra, 
                "input", "java.io.InputStream",
                "buffer", "byte[]");

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);
        assertEquals("int", type.toString());

    }
}
