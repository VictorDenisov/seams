package org.creativelabs.introspection;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import org.creativelabs.ParseHelper;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class TestingReflectionIntrospectionTest {

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

    @Test
    public void testGetClassType() throws Exception{
        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();

        reflectionAbstraction.addClass("Sample", "org.creativelabs.Sample");

        String type = reflectionAbstraction.getClassType("Sample");

        assertEquals("org.creativelabs.Sample", type);
    }

    @Test
    public void testGetClassTypeForPrimitiveType() throws Exception{
        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();

        reflectionAbstraction.addClass("int", "int");

        String type = reflectionAbstraction.getClassType("int");

        assertEquals("int", type);
    }
}
