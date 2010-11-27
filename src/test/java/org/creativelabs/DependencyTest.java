package org.creativelabs; 

import org.testng.annotations.Test;

import japa.parser.ast.expr.Expression;

import static org.testng.AssertJUnit.*;

public class DependencyTest {

    @Test
    public void testConstructor() throws Exception {
        Dependency dependency = new Dependency("value", "type");

        assertEquals("value", dependency.expression);
        assertEquals("type", dependency.type);

    }
}
