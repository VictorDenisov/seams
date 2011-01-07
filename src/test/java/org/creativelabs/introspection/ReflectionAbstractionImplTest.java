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
            { "java.lang.Integer", "java.lang.Integer" },
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

    @Test
    public void testGetMethod() throws Exception {
        Class clazz = Class.forName("java.lang.String");
        Method method = ra.getMethod(clazz, "equals", new Class[]{Class.forName("java.lang.String")});
        assertNotNull(method);
    }

    @Test
    public void testGetReturnTypeStringEqualsString() {
        ClassType className = ra.getClassTypeByName("java.lang.String");
        ClassType[] args = new ClassType[1];
        args[0] = ra.getClassTypeByName("java.lang.String");

        ClassType result = ra.getReturnType(className, "equals", args);
        assertEquals("boolean", result.toString());
    }

    @Test
    public void testGetReturnTypeTypeFinderLiteral() throws Exception {
        Class className = Class.forName("org.creativelabs.TypeFinder");

        Class[] args = new Class[] {Class.forName("japa.parser.ast.expr.LiteralExpr")};

        Method result = ra.getMethod(className, "determineType", args);
        assertEquals("japa.parser.ast.expr.LiteralExpr", result.getParameterTypes()[0].getName());
    }

    @Test
    public void testGetReturnTypeTypeFinderNameExpr() throws Exception {
        Class className = Class.forName("org.creativelabs.TypeFinder");

        Class[] args = new Class[] {Class.forName("japa.parser.ast.expr.NameExpr")};

        Method result = ra.getMethod(className, "determineType", args);
        assertEquals("japa.parser.ast.expr.NameExpr", result.getParameterTypes()[0].getName());
    }

    @Test(dependsOnGroups="parse-helper.create-type.*")
    public void testReflectionAbstractionGetClassName() throws Exception {
        ClassType className = ra.getClassTypeByName("java.util.ArrayList");
        assertEquals("java.util.ArrayList<E, >", className.toString());
    }

    private ClassType createParameterizedClass(String className, String... parameters) {
        ClassType clazz = ra.getClassTypeByName(className);
        ClassType[] genericArgs = new ClassType[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            genericArgs[i] = ra.getClassTypeByName(parameters[i]);
        }
        clazz = ra.substGenericArgs(clazz, genericArgs);
        return clazz;
    }

    @Test(dependsOnGroups="parse-helper.create-type.*")
    public void testReflectionAbstractionSetGenericArgs() throws Exception {
        ClassType result = createParameterizedClass("java.util.ArrayList", "java.lang.String");

        assertEquals("java.util.ArrayList<java.lang.String, >", result.toString());
    }

    @Test(dependsOnMethods=
            {"testReflectionAbstractionGetClassName", 
                "testReflectionAbstractionSetGenericArgs"})
    public void testGetReturnTypeWithGenerics() throws Exception {
        ClassType className = createParameterizedClass("java.util.ArrayList", "java.lang.String");

        ClassType arg = ra.getClassTypeByName("int");
        ClassType result = ra.getReturnType(className, "get", new ClassType[]{arg});

        assertEquals("java.lang.String", result.toString());
    }

    @Test(dependsOnMethods=
            {"testReflectionAbstractionGetClassName", 
                "testReflectionAbstractionSetGenericArgs"})
    public void testGetReturnTypeWithGenericsReturnValueLetter() throws Exception {
        ClassType className = createParameterizedClass("java.util.HashMap", 
                "java.lang.String", "java.lang.Integer");

        ClassType result = ra.getReturnType(className, "keySet", new ClassType[0]);

        assertEquals("java.util.Set<java.lang.String, >", result.toString());
    }

    @Test(dependsOnMethods=
            {"testReflectionAbstractionGetClassName"})
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

    @Test(dependsOnMethods = "testReflectionAbstractionSetGenericArgs")
    public void testMapEntry() {
        ClassType clazz = createParameterizedClass("java.util.Map$Entry", 
                "java.lang.String", "java.lang.Integer");

        ClassType result = ra.getReturnType(clazz, "getKey", new ClassType[0]);

        assertEquals("java.lang.String", result.toString());
    }

    @Test
    public void testExceptionInSubstGenericArgs() {
        ClassType stub = new ClassTypeStub("Hello");

        stub = ra.substGenericArgs(stub, new ClassType[]{stub, stub});

        assertEquals("ClassTypeError", stub.getClass().getSimpleName());
    }

    @Test(dependsOnMethods = "testReflectionAbstractionSetGenericArgs")
    public void testMapAndPrimitiveTypes() {
        ClassType clazz = createParameterizedClass("java.util.Map",
                "java.lang.Integer", "java.lang.String");

        ClassType intClass = ra.getClassTypeByName("int");
        ClassType strClass = ra.getClassTypeByName("java.lang.String");

        ClassType result = ra.getReturnType(clazz, "put", new ClassType[]{intClass, strClass});

        assertEquals("java.lang.String", result.toString());
    }

    @Test(dependsOnMethods = "testReflectionAbstractionSetGenericArgs")
    public void testMapGetEntrySet() {
        ClassType clazz = createParameterizedClass("java.util.Map",
                "java.lang.Integer", "java.lang.String");

        ClassType result = ra.getReturnType(clazz, "entrySet", new ClassType[0]);

        assertEquals("java.util.Set<java.util.Map$Entry<java.lang.Integer, java.lang.String, >, >",
                result.toString());
    }
    
    @Test(dependsOnMethods = "testReflectionAbstractionSetGenericArgs")
    public void testSetAddAll() {
        ClassType clazz = createParameterizedClass("java.util.Set", "java.lang.String");

        ClassType arg = ra.getClassTypeByName("java.util.Set");
        ClassType result = ra.getReturnType(clazz, "addAll", new ClassType[]{arg});

        assertEquals("boolean", result.toString());
    }

}
