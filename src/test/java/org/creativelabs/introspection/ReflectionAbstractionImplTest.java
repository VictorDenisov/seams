package org.creativelabs.introspection;

import org.testng.annotations.*;

import java.util.*;

import java.lang.reflect.*;

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
        String returnType = ra.getReturnType(
                ra.getClassTypeByName("java.lang.String"), "matches", types) + "";
        assertEquals("boolean", returnType);
    }

    @Test
    public void testGetReturnTypeArray() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
        String returnType = 
            ra.getReturnType(ra.getClassTypeByName("java.lang.String"), "split", types) + "";
        assertEquals("[Ljava.lang.String;", returnType);
    } 
    @Test
    public void testGetReturnTypeComment() throws Exception {
        String returnType = ra.getReturnType(ra.getClassTypeByName("japa.parser.ast.Comment"),
                "getContent", new ClassType[0]).toString(); 
        assertEquals("java.lang.String", returnType);
    }

    @Test
    public void testGetReturnTypeCommentVoid() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
        String returnType = ra.getReturnType(ra.getClassTypeByName("japa.parser.ast.Comment"),
                "setContent", types).toString();
        assertEquals("void", returnType);
    }

    @Test
    public void testGetReturnTypeError() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
        ClassType returnType = ra.getReturnType(ra.getClassTypeByName("japa.parser.ast.Comment"),
                "absentMethod", types);
        assertEquals("ClassTypeError", returnType.getClass().getSimpleName());
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
            { "java.util.Map$Entry", "java.util.Map$Entry<K, V, >" },
            { "java.lang.Integer", "int" },
        };
    }

	@Test(dataProvider = "type-provider")
	public void testGetClassTypeByName(String input, String answer) throws Exception {
		ClassType type = ra.getClassTypeByName(input);

		assertEquals(answer, type.toString());
	}

    @Test
    public void testGetClassTypeByNameError() {
        ClassType type = ra.getClassTypeByName("NoSuchClass");

        assertEquals("ClassTypeError", type.getClass().getSimpleName());
    }

    @Test
    public void testGetReturnTypeClassType() throws Exception {
        ClassType[] types = new ClassType[1];
        types[0] = ra.getClassTypeByName("java.lang.String");
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType returnType = ra.getReturnType(myClass, "matches", types);

        assertEquals("boolean", returnType.toString());
    }

    @Test
    public void testGetFieldTypeClassType() throws Exception {
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType fieldType = new ReflectionAbstractionImpl().getFieldType(myClass, "CASE_INSENSITIVE_ORDER");

        assertEquals("java.util.Comparator<T, >", fieldType.toString());
    }

    @Test
    public void testGetFieldTypeError() throws Exception {
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType fieldType = new ReflectionAbstractionImpl().getFieldType(myClass, "NO_SUCH_FIELD");

        assertEquals("ClassTypeError", fieldType.getClass().getSimpleName());
    }

    @Test
    public void testGetReturnTypeStringEquals() {
        ClassType className = ra.getClassTypeByName("java.lang.String");
        ClassType[] args = new ClassType[1];
        args[0] = ra.getClassTypeByName("java.lang.Object");

        ClassType result = ra.getReturnType(className, "equals", args);
        assertEquals("boolean", result.toString());
    }

    @Test(enabled=false)
    public void testGetReturnTypeStringEqualsString() {
        ClassType className = ra.getClassTypeByName("java.lang.String");
        ClassType[] args = new ClassType[1];
        args[0] = ra.getClassTypeByName("java.lang.String");

        ClassType result = ra.getReturnType(className, "equals", args);
        assertEquals("boolean", result.toString());
    }

    @Test(dependsOnGroups="parse-helper.create-type.*")
    public void testReflectionAbstractionGetClassName() throws Exception {
        ClassType className = ra.getClassTypeByName("java.util.ArrayList");
        assertEquals("java.util.ArrayList<E, >", className.toString());
    }

    @Test(dependsOnGroups="parse-helper.create-type.*")
    public void testReflectionAbstractionSetGenericArgs() throws Exception {
        ClassType className = ra.getClassTypeByName("java.util.ArrayList");

        ClassType genericArg = ra.getClassTypeByName("java.lang.String");

        ClassType result = ra.substGenericArgs(className, new ClassType[]{genericArg});
        assertEquals("java.util.ArrayList<java.lang.String, >", result.toString());
    }

    @Test(dependsOnMethods=
            {"testReflectionAbstractionGetClassName", 
                "testReflectionAbstractionSetGenericArgs"})
    public void testGetReturnTypeWithGenerics() throws Exception {
        ClassType className = ra.getClassTypeByName("java.util.ArrayList");

        ClassType genericArg = ra.getClassTypeByName("java.lang.String");

        className = ra.substGenericArgs(className, new ClassType[]{genericArg});

        ClassType arg = ra.getClassTypeByName("int");
        ClassType result = ra.getReturnType(className, "get", new ClassType[]{arg});

        assertEquals("java.lang.String", result.toString());
    }

    @Test(dependsOnMethods=
            {"testReflectionAbstractionGetClassName", 
                "testReflectionAbstractionSetGenericArgs"})
    public void testGetReturnTypeWithGenericsReturnValueLetter() throws Exception {
        ClassType className = ra.getClassTypeByName("java.util.HashMap");

        ClassType genericArgString = ra.getClassTypeByName("java.lang.String");
        ClassType genericArgInteger = ra.getClassTypeByName("java.lang.Integer");

        className = ra.substGenericArgs(className, 
                new ClassType[]{genericArgString, genericArgInteger});

        ClassType result = ra.getReturnType(className, "keySet", new ClassType[0]);

        assertEquals("java.util.Set<java.lang.String, >", result.toString());
    }

    @Test(dependsOnMethods=
            {"testReflectionAbstractionGetClassName", 
                "testReflectionAbstractionSetGenericArgs"})
    public void testGetReturnTypeWithGenericsReturnValueClass() throws Exception {
        ClassType className = ra.getClassTypeByName("org.creativelabs.ClassProcessor");

        ClassType result = ra.getReturnType(className, "getInternalInstances", new ClassType[0]);

        assertEquals(
                "java.util.Map<java.lang.String, org.creativelabs.InternalInstancesGraph, >", 
                result.toString());
    }

    @Test
    public void testDependencyCounterVisitorGetDependenciesNonPublicMethod() {
        ClassType clazz = ra.getClassTypeByName("org.creativelabs.DependencyCounterVisitor");

        ClassType result = ra.getReturnType(clazz, "getDependencies", new ClassType[0]);

        assertEquals("java.util.Set<org.creativelabs.Dependency, >", result.toString());
    }

    @Test
    public void testDependencyCounterVisitorGetDependenciesInheritedMethod() {
        ClassType clazz = ra.getClassTypeByName("japa.parser.ast.body.ClassOrInterfaceDeclaration");

        ClassType result = ra.getReturnType(clazz, "getMembers", new ClassType[0]);

        assertEquals("java.util.List<japa.parser.ast.body.BodyDeclaration, >", result.toString());
    }

    @Test
    public void testDependencyCounterVisitorGetDependenciesNonExistentMethod() {
        ClassType clazz = ra.getClassTypeByName("japa.parser.ast.body.ClassOrInterfaceDeclaration");

        ClassType result = ra.getReturnType(clazz, "getFooBar", new ClassType[0]);

        assertEquals("ClassTypeError", result.getClass().getSimpleName());
        assertEquals("no such method : getFooBar", result.toString());
    }

    @Test
    public void testGetTypeWrongClassType() {
        ClassType result = ra.getReturnType(new ClassTypeStub("h"), "getFooBar", new ClassType[0]);

        assertEquals("ClassTypeError", result.getClass().getSimpleName());
    }

    @Test
    public void testMapEntry() {
        ClassType clazz = ra.getClassTypeByName("java.util.Map$Entry");

        ClassType genericArgString = ra.getClassTypeByName("java.lang.String");
        ClassType genericArgInteger = ra.getClassTypeByName("java.lang.Integer");

        clazz = ra.substGenericArgs(clazz, 
                new ClassType[]{genericArgString, genericArgInteger});

        ClassType result = ra.getReturnType(clazz, "getKey", new ClassType[0]);

        assertEquals("java.lang.String", result.toString());
    }

    @Test
    public void testExceptionInSubstGenericArgs() {
        ClassType stub = new ClassTypeStub("Hello");

        stub = ra.substGenericArgs(stub, new ClassType[]{stub, stub});

        assertEquals("ClassTypeError", stub.getClass().getSimpleName());
    }

}
