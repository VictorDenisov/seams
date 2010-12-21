package org.creativelabs.introspection;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class ReflectionIntrospectionImplTest {

    @Test
    public void testGetReturnType() throws Exception {
        String[] types = new String[1];
        types[0] = "java.lang.String";
        String returnType = new ReflectionAbstractionImpl().getReturnType("java.lang.String", "matches", types);
        assertEquals("boolean", returnType);
    }

    @Test
    public void testGetFieldType() throws Exception {
        String fieldType = new ReflectionAbstractionImpl().getFieldType("java.lang.String", "CASE_INSENSITIVE_ORDER");
        assertEquals("java.util.Comparator", fieldType);
    }

    @Test
    public void testGetReturnTypeArray() throws Exception {
        String[] types = new String[1];
        types[0] = "java.lang.String";
        String returnType = new ReflectionAbstractionImpl().getReturnType("java.lang.String", "split", types);
        assertEquals("[Ljava.lang.String;", returnType);
    }

    @Test
    public void testGetReturnTypeComment() throws Exception {
        String returnType = new ReflectionAbstractionImpl().getReturnType("japa.parser.ast.Comment",
                "getContent", new String[0]); assertEquals("java.lang.String", returnType);
    }

    @Test
    public void testGetReturnTypeCommentVoid() throws Exception {
        String[] types = new String[1];
        types[0] = "java.lang.String";
        String returnType = new ReflectionAbstractionImpl().getReturnType("japa.parser.ast.Comment",
                "setContent", types);
        assertEquals("void", returnType);
    }

    @Test
    public void testClassWithNameExists() throws Exception {
        assertTrue(new ReflectionAbstractionImpl().classWithNameExists("java.lang.String"));
    }

    @Test
    public void testClassWithNameExistsAbsent() throws Exception {
        assertFalse(new ReflectionAbstractionImpl().classWithNameExists("far.far.away.UnExisting"));
    }

    @Test
    public void testGetClassForReferenceClass() throws Exception {
        String type = new ReflectionAbstractionImpl().getClassType("java.lang.String");
        assertEquals("java.lang.String", type);
    }

    @Test
    public void testGetClassForPrimitiveClass() throws Exception {
        String type = new ReflectionAbstractionImpl().getClassType("int");
        assertEquals("int", type);
    }
}
