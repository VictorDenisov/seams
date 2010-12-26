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

        String type = reflectionAbstraction.getReturnType(new ClassTypeStub("Sample"), "methodCall", new ClassTypeStub[]{new ClassTypeStub("int")}).toStringRepresentation();

        assertEquals("org.creativelabs.A", type);
    }

}
