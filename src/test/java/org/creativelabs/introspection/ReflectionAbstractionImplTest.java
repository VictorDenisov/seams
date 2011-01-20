package org.creativelabs.introspection;

import org.testng.annotations.*;

import java.util.*;

import java.lang.reflect.*;

import static org.testng.AssertJUnit.*;

public class ReflectionAbstractionImplTest {

    private ReflectionAbstraction ra;

    @BeforeMethod
    public void setUp() {
        ra = ReflectionAbstractionImpl.create();
    }

    public static ClassType createParameterizedClass(String className, String... parameters) {
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();
        ClassType clazz = ra.getClassTypeByName(className);
        ClassType[] genericArgs = new ClassType[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            genericArgs[i] = ra.getClassTypeByName(parameters[i]);
        }
        clazz = ra.substGenericArgs(clazz, genericArgs);
        return clazz;
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
            { "java.lang.Byte", "java.lang.Byte" },
            { "java.lang.Short", "java.lang.Short" },
            { "java.lang.Integer", "java.lang.Integer" },
            { "java.lang.Long", "java.lang.Long" },
            { "java.lang.Float", "java.lang.Float" },
            { "java.lang.Double", "java.lang.Double" },
            { "java.lang.Character", "java.lang.Character" },
            { "java.lang.Void", "java.lang.Void" },
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
        ClassType fieldType = ReflectionAbstractionImpl.create().getFieldType(myClass, "CASE_INSENSITIVE_ORDER");

        assertEquals("java.util.Comparator<java.lang.String, >", fieldType.toString());
    }

    @Test
    public void testGetFieldTypeError() throws Exception {
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType fieldType = ReflectionAbstractionImpl.create().getFieldType(myClass, "NO_SUCH_FIELD");

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
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
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
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        Class[] args = new Class[] {Class.forName("japa.parser.ast.expr.LiteralExpr")};

        Method result = ra.getMethod(className, "determineType", args);
        assertEquals("japa.parser.ast.expr.LiteralExpr", result.getParameterTypes()[0].getName());
    }

    @Test
    public void testGetReturnTypeTypeFinderNameExpr() throws Exception {
        Class className = Class.forName("org.creativelabs.TypeFinder");
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        Class[] args = new Class[] {Class.forName("japa.parser.ast.expr.NameExpr")};

        Method result = ra.getMethod(className, "determineType", args);
        assertEquals("japa.parser.ast.expr.NameExpr", result.getParameterTypes()[0].getName());
    }

    @Test(dependsOnGroups="parse-helper.create-type.*")
    public void testReflectionAbstractionGetClassName() throws Exception {
        ClassType className = ra.getClassTypeByName("java.util.ArrayList");
        assertEquals("java.util.ArrayList<E, >", className.toString());
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

    @Test
    public void testStringIndexOf() {
        ClassType clazz = ra.getClassTypeByName("java.lang.String");

        ClassType arg = ra.getClassTypeByName("char");

        ClassType result = ra.getReturnType(clazz, "indexOf", new ClassType[]{arg});

        assertEquals("int", result.toString());
    }

    @Test
    public void testGetClasses() {
        ClassType clazz = ra.getClassTypeByName("japa.parser.ast.expr.BinaryExpr");

        ClassType result = ra.getNestedClass(clazz, "Operator");

        assertEquals("japa.parser.ast.expr.BinaryExpr$Operator", result.toString());
    }

    @Test
    public void testGetClassesError() {
        ClassType clazz = new ClassTypeStub("stub");

        ClassType result = ra.getNestedClass(clazz, "Operator");

        assertEquals("ClassTypeError", result.getClass().getSimpleName());
    }

    @Test
    public void testGetClassesNoSuchNestedClass() {
        ClassType clazz = ra.getClassTypeByName("japa.parser.ast.expr.BinaryExpr");

        ClassType result = ra.getNestedClass(clazz, "length");

        assertEquals("ClassTypeError", result.getClass().getSimpleName());
    }

    @Test
    public void testGetFieldGenericArgs() {
        ClassType clazz = ra.getClassTypeByName(
                "org.creativelabs.introspection.ReflectionAbstractionImpl$ClassTypeImpl");

        ClassType result = ra.getFieldType(clazz, "genericArgs");

        assertEquals("java.util.HashMap<java.lang.String, org.creativelabs.introspection.ClassType, >",
                result.toString());
    }

    @Test
    public void testLengthArrayField() throws Exception {
        ClassType clazz = ra.getClassTypeByName("java.lang.String");
        clazz = ra.convertToArray(clazz, 1);
        ClassType result = ra.getFieldType(clazz, "length");
        
        assertEquals("int", result.toString());
    }

    @Test(groups = "reflection-abstraction-impl.interface-has-to-string")
    public void testInterfaceHashMethodToString() throws Exception {
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

        ClassType argsType = ra.getClassTypeByName("java.lang.reflect.Type");

        ClassType result = ra.getReturnType(argsType, "toString", new ClassType[0]);

        assertEquals("java.lang.String", result.toString());
    }

    @Test(groups = "reflection-abstraction-impl.interface-has-object-method")
    public void testInterfaceHashMethodFromObjectClass() throws Exception {
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

        ClassType argsType = ra.getClassTypeByName("java.lang.reflect.Type");

        ClassType result = ra.getReturnType(argsType, "getClass", new ClassType[0]);

        assertEquals("java.lang.Class<null, >", result.toString());
    }
}
