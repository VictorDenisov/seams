package org.creativelabs.introspection;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class ReflectionAbstractionImplTest {

    private ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

    @Test
    public void testGetReturnType() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
        String returnType = ra.getReturnType(ra.getClassTypeByName("java.lang.String"), "matches", types).toStringRepresentation();
        assertEquals("boolean", returnType);
    }

    @Test
    public void testGetReturnTypeArray() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
        String returnType = ra.getReturnType(ra.getClassTypeByName("java.lang.String"), "split", types).toStringRepresentation();
        assertEquals("[Ljava.lang.String;", returnType);
    }

    @Test
    public void testGetReturnTypeComment() throws Exception {
        String returnType = ra.getReturnType(ra.getClassTypeByName("japa.parser.ast.Comment"),
                "getContent", new ClassType[0]).toStringRepresentation(); 
        assertEquals("java.lang.String", returnType);
    }

    @Test
    public void testGetReturnTypeCommentVoid() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
        String returnType = ra.getReturnType(ra.getClassTypeByName("japa.parser.ast.Comment"),
                "setContent", types).toStringRepresentation();
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
	public void testGetClassTypeByName() throws Exception {
		ClassType type = new ReflectionAbstractionImpl().getClassTypeByName("java.lang.String");
		assertEquals("java.lang.String", type.toStringRepresentation());
	}

	@Test
	public void testGetClassTypeByNamePrimitive() throws Exception {
		ClassType type = new ReflectionAbstractionImpl().getClassTypeByName("int");
		assertEquals("int", type.toStringRepresentation());
	}

    @Test
    public void testGetClassTypeByNameInnerClass() throws Exception {
        ClassType type = new ReflectionAbstractionImpl().getClassTypeByName("java.util.Map$Entry");
        assertEquals("java.util.Map$Entry", type.toStringRepresentation());
    }

	@Test
	public void testGetClassTypeByNameFromReferenceClass() throws Exception {
		ClassType type = new ReflectionAbstractionImpl().getClassTypeByName("java.lang.Integer");
		assertEquals("int", type.toStringRepresentation());
	}

    @Test
    public void testGetReturnTypeClassType() throws Exception {
		ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType returnType = ra.getReturnType(myClass, "matches", types);

        assertEquals("boolean", returnType.toStringRepresentation());
    }

    @Test
    public void testGetFieldTypeClassType() throws Exception {
		ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType fieldType = new ReflectionAbstractionImpl().getFieldType(myClass, "CASE_INSENSITIVE_ORDER");

        assertEquals("java.util.Comparator", fieldType.toStringRepresentation());
    }
}
