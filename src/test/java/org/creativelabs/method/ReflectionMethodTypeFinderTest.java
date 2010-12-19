package org.creativelabs.method;

import org.testng.annotations.Test;

import static junit.framework.Assert.*;

public class ReflectionMethodTypeFinderTest {

    @Test
    public void testMethodGetMethodTypeAsString(){
        MethodTypeFinderBuilder methodTypeFinder = new ReflectionMethodTypeFinder();
        String type = null;
        String result  = "noException";
        try {
            type = methodTypeFinder.getMethodTypeAsString("java.lang.String", "compareTo", new Class[]{String.class});
        } catch (Exception e) {
            result = "exception";
        }
        assertEquals("noException", result);
        assertEquals("int", type);
    }

    @Test
    public void testMethodGetMethodTypeAsClass(){
        MethodTypeFinderBuilder methodTypeFinder = new ReflectionMethodTypeFinder();
        Class type = null;
        String result  = "noException";
        try {
            type = methodTypeFinder.getMethodTypeAsClass("java.lang.String", "compareTo", new Class[]{String.class});
        } catch (Exception e) {
            result = "exception";
        }
        assertEquals("noException", result);
        assertEquals(int.class, type);
    }

}
