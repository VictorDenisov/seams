package org.creativelabs; 

import org.testng.annotations.Test;
import org.testng.annotations.Configuration;
import org.creativelabs.TypeFinder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;

import static org.testng.AssertJUnit.*;

public class ParseHelperTest {

    @Test
    public void testCreateExpression() throws Exception {
        Expression expr = ParseHelper.createExpression("main.methodCall()");

        assertEquals("class japa.parser.ast.expr.MethodCallExpr", expr.getClass().toString());
    }
}

