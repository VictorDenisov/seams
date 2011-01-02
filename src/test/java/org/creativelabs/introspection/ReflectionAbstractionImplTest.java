package org.creativelabs.introspection;

import org.testng.annotations.*;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class ReflectionAbstractionImplTest {

    private ReflectionAbstractionImpl ra;

    @BeforeMethod
    public void setUp() {
        ra = new ReflectionAbstractionImpl();
    }

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
        assertTrue(ra.classWithNameExists("java.lang.String"));
    }

    @Test
    public void testClassWithNameExistsAbsent() throws Exception {
        assertFalse(ra.classWithNameExists("far.far.away.UnExisting"));
    }

    @DataProvider(name = "type-provider")
    public Object[][] createTypeList() {
        // Input data, Answer data
        return new Object[][] {
            { "java.lang.String", "java.lang.String" },
            { "int", "int" },
            { "short", "short" },
            { "byte", "byte" },
            { "long", "long" },
            { "float", "float" },
            { "double", "double" },
            { "char", "char" },
            { "boolean", "boolean" },
            { "void", "void" },
            { "java.util.Map$Entry", "java.util.Map$Entry" },
            { "java.lang.Integer", "int" },
        };
    }

	@Test(dataProvider = "type-provider")
	public void testGetClassTypeByName(String input, String answer) throws Exception {
		ClassType type = ra.getClassTypeByName(input);

		assertEquals(answer, type.toStringRepresentation());
	}

    @Test
    public void testGetReturnTypeClassType() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType returnType = ra.getReturnType(myClass, "matches", types);

        assertEquals("boolean", returnType.toStringRepresentation());
    }

    @Test
    public void testGetFieldTypeClassType() throws Exception {
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType fieldType = new ReflectionAbstractionImpl().getFieldType(myClass, "CASE_INSENSITIVE_ORDER");

        assertEquals("java.util.Comparator", fieldType.toStringRepresentation());
    }

    @Test
    public void testGetReturnTypeStringEquals() {
        ClassType className = ra.getClassTypeByName("java.lang.String");
        ClassType[] args = new ClassType[1];
        args[0] = ra.getClassTypeByName("java.lang.Object");

        ClassType result = ra.getReturnType(className, "equals", args);
        System.out.println(result.getClass());
        assertEquals("boolean", result.toStringRepresentation());
    }
}
