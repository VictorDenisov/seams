package org.creativelabs;

import japa.parser.ast.expr.Expression;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class PrimitiveClassFactoryTest {

    private PrimitiveClassFactory factory = PrimitiveClassFactory.getFactory();

    @Test
    public void testGetFactoryMethod() throws Exception {
        assertNotNull(PrimitiveClassFactory.getFactory());
    }

    @Test
    public void testGetPrimitiveClassMethod() throws Exception {

        String className = "byte";
        assertEquals(byte.class, factory.getPrimitiveClass(className));

        className = "Byte";
        assertEquals(byte.class, factory.getPrimitiveClass(className));

        className = "java.lang.Byte";
        assertEquals(byte.class, factory.getPrimitiveClass(className));

        className = "short";
        assertEquals(short.class, factory.getPrimitiveClass(className));

        className = "Short";
        assertEquals(short.class, factory.getPrimitiveClass(className));

        className = "java.lang.Short";
        assertEquals(short.class, factory.getPrimitiveClass(className));

        className = "int";
        assertEquals(int.class, factory.getPrimitiveClass(className));

        className = "Integer";
        assertEquals(int.class, factory.getPrimitiveClass(className));

        className = "java.lang.Integer";
        assertEquals(int.class, factory.getPrimitiveClass(className));

        className = "long";
        assertEquals(long.class, factory.getPrimitiveClass(className));

        className = "Long";
        assertEquals(long.class, factory.getPrimitiveClass(className));

        className = "java.lang.Long";
        assertEquals(long.class, factory.getPrimitiveClass(className));

        className = "float";
        assertEquals(float.class, factory.getPrimitiveClass(className));

        className = "Float";
        assertEquals(float.class, factory.getPrimitiveClass(className));

        className = "java.lang.Float";
        assertEquals(float.class, factory.getPrimitiveClass(className));

        className = "double";
        assertEquals(double.class, factory.getPrimitiveClass(className));

        className = "Double";
        assertEquals(double.class, factory.getPrimitiveClass(className));

        className = "java.lang.Double";
        assertEquals(double.class, factory.getPrimitiveClass(className));

        className = "char";
        assertEquals(char.class, factory.getPrimitiveClass(className));

        className = "Char";
        assertEquals(char.class, factory.getPrimitiveClass(className));

        className = "java.lang.Char";
        assertEquals(char.class, factory.getPrimitiveClass(className));

        className = "boolean";
        assertEquals(boolean.class, factory.getPrimitiveClass(className));

        className = "Boolean";
        assertEquals(boolean.class, factory.getPrimitiveClass(className));

        className = "java.lang.Boolean";
        assertEquals(boolean.class, factory.getPrimitiveClass(className));

        className = "void";
        assertEquals(void.class, factory.getPrimitiveClass(className));

        className = "Void";
        assertEquals(void.class, factory.getPrimitiveClass(className));

        className = "java.lang.Void";
        assertEquals(void.class, factory.getPrimitiveClass(className));

        className = "String";
        assertEquals(String.class, factory.getPrimitiveClass(className));

        className = "java.lang.String";
        assertEquals(String.class, factory.getPrimitiveClass(className));

        className = "org.creativelabs.SomeClass";
        String result = "NoException";
        try{
            factory.getPrimitiveClass(className);
        } catch (TypeFinder.UnsupportedExpressionException e){
            result = "UnsupportedExpressionException";
        }
        assertEquals("UnsupportedExpressionException", result);

    }

    @Test
    public void testClassIsPrimitiveMethod() throws Exception {

        String className = "byte";
        assertTrue(factory.classIsPrimitive(className));

        className = "Byte";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Byte";
        assertFalse(factory.classIsPrimitive(className));

        className = "short";
        assertTrue(factory.classIsPrimitive(className));

        className = "Short";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Short";
        assertFalse(factory.classIsPrimitive(className));

        className = "int";
        assertTrue(factory.classIsPrimitive(className));

        className = "Integer";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Integer";
        assertFalse(factory.classIsPrimitive(className));

        className = "long";
        assertTrue(factory.classIsPrimitive(className));

        className = "Long";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Long";
        assertFalse(factory.classIsPrimitive(className));

        className = "float";
        assertTrue(factory.classIsPrimitive(className));

        className = "Float";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Float";
        assertFalse(factory.classIsPrimitive(className));

        className = "double";
        assertTrue(factory.classIsPrimitive(className));

        className = "Double";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Double";
        assertFalse(factory.classIsPrimitive(className));

        className = "char";
        assertTrue(factory.classIsPrimitive(className));

        className = "Char";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Char";
        assertFalse(factory.classIsPrimitive(className));

        className = "boolean";
        assertTrue(factory.classIsPrimitive(className));

        className = "Boolean";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Boolean";
        assertFalse(factory.classIsPrimitive(className));

        className = "void";
        assertTrue(factory.classIsPrimitive(className));

        className = "Void";
        assertFalse(factory.classIsPrimitive(className));

        className = "java.lang.Void";
        assertFalse(factory.classIsPrimitive(className));

        className = "String";
        assertTrue(factory.classIsPrimitive(className));

        className = "java.lang.String";
        assertTrue(factory.classIsPrimitive(className));

        className = "org.creativelabs.SomeClass";
        assertFalse(factory.classIsPrimitive(className));

    }
}
