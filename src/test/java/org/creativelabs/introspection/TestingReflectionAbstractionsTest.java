package org.creativelabs.introspection;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import org.creativelabs.ParseHelper;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class TestingReflectionAbstractionsTest {

    @Test
    public void testGetReturnType() throws Exception{
        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();

        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{"int"}, "org.creativelabs.A");

        String type = reflectionAbstraction.getReturnType("Sample", "methodCall", new String[]{"int"});

        assertEquals("org.creativelabs.A", type);
    }

    @Test
    public void testGetFieldType() throws Exception{
        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();

        reflectionAbstraction.addField("Sample", "someField", "org.creativelabs.A");

        String type = reflectionAbstraction.getFieldType("Sample", "someField");

        assertEquals("org.creativelabs.A", type);
    }

}
