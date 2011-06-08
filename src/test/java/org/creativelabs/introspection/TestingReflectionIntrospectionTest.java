package org.creativelabs.introspection;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class TestingReflectionIntrospectionTest {

    @Test
    public void testGetReturnType() throws Exception{
        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();

        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{"int"}, "org.creativelabs.A");

        String type = reflectionAbstraction.getReturnType(new ClassTypeStub("Sample"), "methodCall", new ClassTypeStub[]{new ClassTypeStub("int")}).toString();

        assertEquals("org.creativelabs.A", type);
    }

}
