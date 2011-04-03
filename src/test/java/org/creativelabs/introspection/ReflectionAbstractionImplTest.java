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
        assertTrue(returnType instanceof ClassTypeError);
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

        assertTrue(type instanceof ClassTypeError);
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
    public void testGetFieldTypePrivateField() throws Exception {
		ClassType myClass = ra.getClassTypeByName(
                "org.creativelabs.introspection.ReflectionAbstractionImpl");
        ClassType fieldType = ra.getFieldType(myClass, "arrayChar");

        assertEquals("java.util.HashMap<java.lang.String, java.lang.String, >",
                fieldType.toString());
    }

    @Test
    public void testGetFieldTypeClassType() throws Exception {
		ClassType myClass = ra.getClassTypeByName("java.lang.String");

        ClassType fieldType = ra.getFieldType(myClass, "CASE_INSENSITIVE_ORDER");

        assertEquals("java.util.Comparator<java.lang.String, >", fieldType.toString());
    }

    @Test
    public void testGetFieldTypeError() throws Exception {
		ClassType myClass = ra.getClassTypeByName("java.lang.String");
        ClassType fieldType = ReflectionAbstractionImpl.create().getFieldType(myClass, "NO_SUCH_FIELD");

        assertTrue(fieldType instanceof ClassTypeError);
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
        Method method = ra.getMethod(clazz, "equals", new ClassType[]{ra.getClassTypeByName("java.lang.String")});
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

        ClassType[] args = new ClassType[] {ra.getClassTypeByName("japa.parser.ast.expr.LiteralExpr")};

        Method result = ra.getMethod(className, "determineType", args);
        assertEquals("japa.parser.ast.expr.LiteralExpr", result.getParameterTypes()[0].getName());
    }

    @Test
    public void testGetReturnTypeTypeFinderNameExpr() throws Exception {
        Class className = Class.forName("org.creativelabs.TypeFinder");
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        ClassType[] args = new ClassType[] {ra.getClassTypeByName("japa.parser.ast.expr.NameExpr")};

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

        assertTrue(result instanceof ClassTypeError);
        assertEquals("no such method : getFooBar", result.toString());
    }

    @Test
    public void testGetTypeWrongClassType() {
        ClassType result = ra.getReturnType(new ClassTypeStub("h"), "getFooBar", new ClassType[0]);

        assertTrue(result instanceof ClassTypeError);
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

        assertTrue(stub instanceof ClassTypeError);
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

        assertTrue(result instanceof ClassTypeError);
    }

    @Test
    public void testGetClassesNoSuchNestedClass() {
        ClassType clazz = ra.getClassTypeByName("japa.parser.ast.expr.BinaryExpr");

        ClassType result = ra.getNestedClass(clazz, "length");

        assertTrue(result instanceof ClassTypeError);
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
    public void testWhenArgumentIsAGenericType() {
        ClassType classType = createParameterizedClass("edu.uci.ics.jung.graph.Graph",
                "org.creativelabs.graph.Vertex", "java.lang.String");

        ClassType argument = ra.getClassTypeByName("org.creativelabs.graph.Vertex");

        ClassType result = ra.getReturnType(classType, "addVertex", new ClassType[]{argument});

        assertEquals("boolean", result.toString());
    }

    @Test
    public void testGetFieldInherited() throws Exception {
        ClassType classType = ra.getClassTypeByName("java.awt.event.MouseEvent");

        ClassType result = ra.getFieldType(classType, "BUTTON3_MASK");

        assertEquals("int", result.toString());
    }

    @Test
    public void testGetMethodWithShortInt() throws Exception {
        ClassType classType = ra.getClassTypeByName("javax.swing.GroupLayout$ParallelGroup");
        ClassType a = ra.getClassTypeByName("java.lang.Integer");
        ClassType s = ra.getClassTypeByName("short");
        
        ClassType result = ra.getReturnType(classType, "addGap", new ClassType[]{a, a, s});

        assertEquals("javax.swing.GroupLayout$ParallelGroup", result.toString());
    }

    @Test
    public void testGetMethodWithIntegerLong() throws Exception {
        ClassType classType = ra.getClassTypeByName("java.lang.Thread");
        ClassType a = ra.getClassTypeByName("java.lang.Integer");
        
        ClassType result = ra.getReturnType(classType, "sleep", new ClassType[]{a});

        assertEquals("void", result.toString());
    }

    @Test
    public void testCreateNullClassType() throws Exception {
        ClassType classType = ra.createNullClassType();

        assertTrue(classType instanceof ClassTypeNull);
    }

    @Test
    public void testIsEligible() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        Class clazz = Class.forName("javax.swing.GroupLayout$SequentialGroup");
        Class arg1 = Class.forName("javax.swing.JComponent");
        Class arg2 = Class.forName("javax.swing.LayoutStyle$ComponentPlacement");
        Method method = clazz.getDeclaredMethod("addPreferredGap", new Class[]{arg1, arg1, arg2});

        ClassType arg = ra.getClassTypeByName("javax.swing.LayoutStyle$ComponentPlacement");
        ClassType intCT = ra.getClassTypeByName("java.lang.Integer");
        ClassType shortCT = ra.getClassTypeByName("short");

        boolean result = ra.isEligible(method, "addPreferredGap", new ClassType[]{arg, intCT, shortCT});

        assertFalse(result);
    }

    public void sampleMethod(Integer i, String... args) {
    }

    @Test
    public void testIsEligibleVariadic() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        Class clazz = Class.forName("org.creativelabs.introspection.ReflectionAbstractionImplTest");
        Method method = clazz.getDeclaredMethod("sampleMethod", Class.forName("java.lang.Integer"),
                Class.forName("[Ljava.lang.String;"));
        ClassType intg = ra.getClassTypeByName("java.lang.Integer");
        ClassType arg = ra.getClassTypeByName("java.lang.String");
        boolean value = ra.isEligible(method, "sampleMethod", new ClassType[]{intg, arg});
        assertTrue(value);
    }

    @Test
    public void testIsEligibleVariadicLonger() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        Class clazz = Class.forName("org.creativelabs.introspection.ReflectionAbstractionImplTest");
        Method method = clazz.getDeclaredMethod("sampleMethod", Class.forName("java.lang.Integer"),
                Class.forName("[Ljava.lang.String;"));
        ClassType intg = ra.getClassTypeByName("java.lang.Integer");
        ClassType arg = ra.getClassTypeByName("java.lang.String");
        boolean value = ra.isEligible(method, "sampleMethod", new ClassType[]{intg, arg, arg, arg});
        assertTrue(value);
    }

    @Test
    public void testIsEligibleVariadicShorter() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        Class clazz = Class.forName("org.creativelabs.introspection.ReflectionAbstractionImplTest");
        Method method = clazz.getDeclaredMethod("sampleMethod", Class.forName("java.lang.Integer"),
                Class.forName("[Ljava.lang.String;"));
        ClassType intg = ra.getClassTypeByName("java.lang.Integer");
        boolean value = ra.isEligible(method, "sampleMethod", new ClassType[]{intg});
        assertTrue(value);
    }

    @Test
    public void testAddPreferredGap() throws Exception {
        ClassType classType = ra.getClassTypeByName("javax.swing.GroupLayout$SequentialGroup");

        ClassType arg = ra.getClassTypeByName("javax.swing.LayoutStyle$ComponentPlacement");
        ClassType intCT = ra.getClassTypeByName("java.lang.Integer");
        ClassType shortCT = ra.getClassTypeByName("short");

        ClassType result = ra.getReturnType(classType, "addPreferredGap", 
                new ClassType[]{arg, intCT, shortCT});
        
        assertEquals("javax.swing.GroupLayout$SequentialGroup", result.toString());
    }

    private class JFrameDescendant extends javax.swing.JFrame {
    }

    @Test
    public void testInheritedFieldAccess() {
        ClassType classType = ra.getClassTypeByName(
                "org.creativelabs.introspection.ReflectionAbstractionImplTest$JFrameDescendant");

        ClassType result = ra.getFieldType(classType, "rootPane");

        assertEquals("javax.swing.JRootPane", result.toString());
    }

    @Test
    public void testJframeLEFT() {
        ClassType classType = ra.getClassTypeByName("javax.swing.JLabel");

        ClassType result = ra.getFieldType(classType, "LEFT");

        assertEquals("int", result.toString());
    }

    @Test
    public void testAddArrayDepth() {
        ClassType classType = ra.getClassTypeByName("java.lang.String");

        classType = ra.addArrayDepth(classType);

        assertEquals("[Ljava.lang.String;", classType.toString());
    }

    @Test
    public void testAddArrayDepthWithCount() {
        ClassType classType = ra.getClassTypeByName("java.lang.String");

        classType = ra.addArrayDepth(classType, 2);

        assertEquals("[[Ljava.lang.String;", classType.toString());
    }
    
    @Test
    public void testAddArrayDepthAlreadyArray() {
        ClassType classType = ra.getClassTypeByName("[Ljava.lang.String;");

        classType = ra.addArrayDepth(classType);

        assertEquals("[[Ljava.lang.String;", classType.toString());
    }

    @Test
    public void testAddArrayDepthForPrimitive() {
        ClassType classType = ra.getClassTypeByName("int");

        classType = ra.addArrayDepth(classType);

        assertEquals("[I", classType.toString());
    }

    @Test
    public void testConvertToArrayFromPrimitive() throws Exception {
        ClassType classType = ra.getClassTypeByName("byte");

        classType = ra.addArrayDepth(classType, 1);

        assertEquals("[B", classType.toString());
    }

}
