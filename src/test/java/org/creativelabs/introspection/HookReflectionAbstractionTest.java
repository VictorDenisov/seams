package org.creativelabs.introspection;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class HookReflectionAbstractionTest {

    private ReflectionAbstraction ra;

    @BeforeMethod
    public void setUp() {
        ra = new HookReflectionAbstraction(new ReflectionAbstractionImpl());
    }

    @Test(groups = "reflection-abstraction-impl.interface-has-to-string")
    public void testInterfaceHasMethodToString() throws Exception {
        ClassType argsType = ra.getClassTypeByName("java.lang.reflect.Type");

        ClassType result = ra.getReturnType(argsType, "toString", new ClassType[0]);

        assertEquals("java.lang.String", result.toString());
    }

    @Test
    public void testLengthArrayField() throws Exception {
        ClassType clazz = ra.getClassTypeByName("java.lang.String");
        clazz = ra.addArrayDepth(clazz, 1);
        ClassType result = ra.getFieldType(clazz, "length");
        
        assertEquals("int", result.toString());
    }

    @Test(groups = "reflection-abstraction-impl.interface-has-object-method")
    public void testInterfaceHashMethodFromObjectClass() throws Exception {
        ClassType argsType = ra.getClassTypeByName("java.lang.reflect.Type");

        ClassType result = ra.getReturnType(argsType, "getClass", new ClassType[0]);

        assertEquals("java.lang.Class<null, >", result.toString());
    }
}
