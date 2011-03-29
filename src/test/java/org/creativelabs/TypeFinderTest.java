package org.creativelabs;

import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import org.creativelabs.introspection.*;
import org.testng.annotations.*;
import java.util.*;
import java.lang.reflect.*;

import java.io.File;

import org.creativelabs.introspection.ReflectionAbstractionImplTest;
import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;
import static org.mockito.Mockito.*;

public class TypeFinderTest {

    private ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

    @Test
    public void testCreateVarListWithValues() {
        TestingReflectionAbstraction ra = spy(new TestingReflectionAbstraction());
        VariableList varList = ConstructionHelper.createVarListWithValues(ra,
                "a", "TypeA", "b", "TypeB");
        List<String> result = varList.getNames();
        Collections.sort(result);
        assertEqualsList(Arrays.asList(new String[]{"a", "b"}), result);
        verify(ra).getClassTypeByName("TypeA");
        verify(ra).getClassTypeByName("TypeB");
    }

    @Test
    public void testDetermineTypeNameExprVariable() throws Exception {
        Expression expr = ParseHelper.createExpression("string");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("string", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

    @Test
    public void testDetermineTypeNameExprClass() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("String");
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("string", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, imports).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

    @Test
    public void testDetermineTypeMethodCallStatic() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("String.valueOf(x)");

        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("x", ra.getClassTypeByName(int.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, imports).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

    @Test
    public void testDetermineTypeMethodExprVariable() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("str.compareTo(x)");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("x", ra.getClassTypeByName(String.class.getName()));
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("int", type.toString());
    }

    @Test
    public void testDetermineTypeNonStandardClass() throws Exception {
        Expression expr = ParseHelper.createExpression("Logger.getLogger(str)");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        ClassType type = new TypeFinder(ra, varTypes, imports).determineType(expr);

        assertEquals("org.apache.log4j.Logger", type.toString());
    }


    @Test(enabled = false)
    public void testDetermineTypeThrowsUnsupportedExpression() throws Exception {
        Expression expr = ParseHelper.createExpression("x = y");

        String result = "noException";
            ClassType type = new TypeFinder(ra, null, null).determineType(expr);
        assertEquals("UnsupportedExpressionException", result);
    }

    @DataProvider(name = "literals-provider")
    public Object[][] createLiteralsAsArgumentList() {
        return new Object[][] {
            // Input data, Answer data
            { "str.compareTo(\"string\")", "int" },
            { "str.substring(1)", "java.lang.String" }, 
            { "String.valueOf(true)", "java.lang.String" },
            { "String.valueOf(1.5)", "java.lang.String" },
        };
    }

    @Test(dataProvider = "literals-provider") 
    public void testDetermineTypeOfMethodWithLiteralsAsArgument(String expression,
                                                                 String expectedValue) throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression(expression);
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, imports).determineType(expr);

        assertEquals(expectedValue, type.toString());
    }

    @Test
    public void testDetermineTypeIntLiteral() throws Exception {
        Expression expr = ParseHelper.createExpression("1");

        ClassType type = new TypeFinder(ra, null, null).determineType(expr);

        assertEquals("java.lang.Integer", type.toString());
    }

    @Test
    public void testDetermineTypeStringLiteral() throws Exception {

        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("\"string\".compareTo(str)");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("int", type.toString());

    }

    @Test
    public void testDetermineTypeOfAssignExpressionWithStringLiteral() throws Exception {

        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("str = \"string\"");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("java.lang.String", type.toString());

    }

    @Test
    public void testDetermineTypeOfMethodWithoutThisLiteral() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "A methodCall(int i){}"
                + "public static void main(String[] args) {"
                + "methodCall(1);"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        String className = cd.getName();
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        Expression expr = ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression();

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("this", new ClassTypeStub(cd.getName()));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{"int"}, "org.creativelabs.A");
        reflectionAbstraction.addClass("java.lang.Integer", "int");

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());
    }

    @Test
    public void testDetermineTypeOfMethodWithThisLiteral() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "A methodCall(int i){}"
                + "public static void main(String[] args) {"
                + "this.methodCall(1);"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        Expression expr = ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression();

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("this", new ClassTypeStub(cd.getName()));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{"int"}, "org.creativelabs.A");
        reflectionAbstraction.addClass("java.lang.Integer", "int");

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());
    }

    @Test
    public void testDetermineTypeOfMethodWithThisLiteralAsArgument() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "String methodCall(int i){}"
                + "public static void main(String[] args) {"
                + "str.compareTo(this.methodCall(1));"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        Expression expr = ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression();

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));
        varTypes.put("this", new ClassTypeStub(cd.getName()));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{"int"}, "java.lang.String");
        reflectionAbstraction.addMethod("java.lang.String", "compareTo", new String[]{"java.lang.String"}, "int");
        reflectionAbstraction.addClass("java.lang.Integer", "int");

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("int", type.toString());
    }

    @Test
    public void testDetermineTypeOfVariableWithThisLiteral() throws Exception {
        Expression expr = ParseHelper.createExpression("this.imports");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("this", ra.getClassTypeByName("org.creativelabs.MainApp"));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.ImportList", type.toString());
    }

    @Test
    public void testNullLiteralAsArgumentOfSimpleArgumentMethod() throws Exception {
        Expression expr = ParseHelper.createExpression("\"string\".compareTo(null)");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        ImportList imports = ConstructionHelper.createEmptyImportList();

        ClassType type = new TypeFinder(ra, varTypes, imports).determineType(expr);

        assertEquals("int", type.toString());
    }

    private void testNullLiteralAsArgumentOfOverloadedArgumentsMethod(String expression,
                                                                      String expectedValue) throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression(expression);
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ClassType type = null;
        type = new TypeFinder(ra, varTypes, imports).determineType(expr);
        assertEquals(expectedValue, type.toString());
    }

    @Test
    public void testNullLiteralAsArgumentOfOverloadedArgumentsMethodCastToString() throws Exception {
        testNullLiteralAsArgumentOfOverloadedArgumentsMethod("\"string\".getBytes((String)null)", "[B");
    }

    @Test
    public void testNullLiteralAsArgumentOfOverloadedArgumentsMethodCastToStringBuffer()
            throws Exception {
        testNullLiteralAsArgumentOfOverloadedArgumentsMethod(
                "\"string\".contentEquals((StringBuffer)null)", "boolean");
    }

    @Test
    public void testNullLiteral() throws Exception {

        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("str = null");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("java.lang.String", type.toString());

    }


    @Test
    public void testSuperLiteralInMethodCallExpression() throws Exception {
        SuperExpr expr = (SuperExpr) ((MethodCallExpr) ParseHelper.createExpression("super.methodCall()")).getScope();

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("super", new ClassTypeStub("org.creativelabs.A"));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());
    }

    @DataProvider(name = "binary-ops-list")
    public Object[][] createBinaryOperationTests() throws Exception {
        ImportList emptyImportList = ConstructionHelper.createEmptyImportList();

        return new Object[][] {
            { "i = 1 + 1;", "int", emptyImportList},
            { "i = 1 + '1';", "char", emptyImportList},
            { "i = 1 + \"1\";", "java.lang.String", emptyImportList},
            { "i = 1 + new Float(1.5);", "float", emptyImportList},
            {"i = 1 + new Short(1);", "int", emptyImportList},
            {"i = 1 + new Long(1);", "long", emptyImportList},
            {"i = new Float(1.1) + new Long(1);", "float", emptyImportList},
            {"i = 1 + 1.5;", "double", emptyImportList},
            {"i = 1 - new Float(1.5);", "float", emptyImportList},
            {"i = 1 - new Short(1);", "int", emptyImportList},
            {"i = 1 - new Long(1);", "long", emptyImportList},
            {"i = new Float(1.1) - new Long(1);", "float", emptyImportList},
            {"i = 1 - 1.5;", "double", emptyImportList},
            {"i = 1 / 1;", "int", emptyImportList},
            {"i = 1 / new Double(1.5);", "double", emptyImportList},
            {"i = new Float(1) / 1;", "float", emptyImportList},
            {"i = new Float(1) % 1;", "float", emptyImportList},
            {"i = new Double(1) % 1;", "double", emptyImportList},
            {"i = new Long(1) % 1;", "long", emptyImportList},
            {"i = 2 % 1;", "int", emptyImportList},
            {"i = new Short(1) % 1;", "int", emptyImportList},
        };
    }

    @Test(dataProvider = "binary-ops-list")
    public void testBinaryExpression(String expression, String expectedType, ImportList imports) throws Exception{
        BinaryExpr expr = (BinaryExpr) ((AssignExpr) ParseHelper.createExpression(expression)).getValue();
        ClassType type = new TypeFinder(ra, null, imports).determineType(expr);
        assertEquals(expectedType, type.toString());
    }

    /** 
     * class Sample {
     *     String file;
     *     String method(File file) {
     *         this.file = new File();
     *     }
     * }
     */
    @Test
    public void testProcessingArgumentOfMethodSelectClassField() throws Exception{
        Expression expr = ParseHelper.createExpression("this.file");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("file", new ClassTypeStub("java.io.File"));
        varTypes.put("this", new ClassTypeStub("Sample"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addField("Sample", "file", "java.lang.String");

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, null).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

    @Test
    public void testSuperLiteralInFieldAccessExpression() throws Exception {
        SuperExpr expr = (SuperExpr) ((FieldAccessExpr) ParseHelper.createExpression("super.someField")).getScope();

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("super", new ClassTypeStub("org.creativelabs.A"));

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());
    }

    @Test
    public void testProcessingArgumentOfMethodSelectMethodArgument() throws Exception{
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "String file;"
                + "String methodCall(File file){"
                + "file = new File();"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        NameExpr expr = (NameExpr) ((AssignExpr) ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression()).getTarget();

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.io.File;");

        VariableList varTypes = ConstructionHelper.createVariableListFromClassFields(cd, importList);
        varTypes.put("file", new ClassTypeStub("java.io.File"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{File.class.getName()}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.io.File", type.toString());
    }

    @Test
    public void testImportDeclarationGetNameGetNameEquals() throws Exception {
        ReflectionAbstraction reflectionAbstraction 
            = ReflectionAbstractionImpl.create();
        Expression expr = ParseHelper.createExpression("id.getName().getName().equals(\"h\")");
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("id", 
                reflectionAbstraction.getClassTypeByName("japa.parser.ast.ImportDeclaration"));
        ImportList imports = ParseHelper.createImportList("");
        TypeFinder typeFinder = new TypeFinder(reflectionAbstraction, varList, imports);

        ClassType type = typeFinder.determineType(expr);
        
        assertEquals("boolean", type.toString());
    }

    @Test
    public void testStringIndexOf() throws Exception {
        Expression expr = ParseHelper.createExpression("\"foo\".indexOf('a')");

        VariableList varList = ConstructionHelper.createEmptyVariableList();
        ImportList imports = ConstructionHelper.createEmptyImportList();

        TypeFinder typeFinder = new TypeFinder(ReflectionAbstractionImpl.create(), varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("int", type.toString());
    }

    @Test
    public void testConstructorExpression() throws Exception {
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();
        Expression expr = ParseHelper.createExpression(
                "new ChartDrawer(chartBuilder.getChart()).saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT, fileName)");
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("chartBuilder", ra.getClassTypeByName("org.creativelabs.chart.BarChartBuilder"));
        varList.put("IMAGE_WIDTH", ra.getClassTypeByName("int"));
        varList.put("IMAGE_HEIGHT", ra.getClassTypeByName("int"));
        varList.put("fileName", ra.getClassTypeByName("java.lang.String"));
        ImportList imports = ParseHelper.createImportList("import org.creativelabs.ui.ChartDrawer;");

        TypeFinder typeFinder = new TypeFinder(ra, varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("void", type.toString());
    }

    @Test
    public void testBinaryExprOperator() throws Exception {
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();
        Expression expr = ParseHelper.createExpression("BinaryExpr.Operator");
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        ImportList imports = ParseHelper.createImportList("import japa.parser.ast.expr.*;");

        TypeFinder typeFinder = new TypeFinder(ra, varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("japa.parser.ast.expr.BinaryExpr$Operator", type.toString());
    }


    @Test
    public void testDetermineTypeOfArrayAccessExpr() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "Class[] clazz;"
                + "String methodCall(){"
                + "clazz[0] = Class.forName(\"java.lang.String\");"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        ArrayAccessExpr expr = (ArrayAccessExpr)
                ((AssignExpr)((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression()).getTarget();

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.lang.Class;");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("clazz", new ClassTypeStub("java.lang.Class[]"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.Class", type.toString());

    }

    @Test
    public void testDetermineTypeOfArrayExpr() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "Class[] clazz;"
                + "String methodCall(){"
                + "clazz = new Class[2];"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        NameExpr expr = (NameExpr) ((AssignExpr)
                ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression()).getTarget();

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.lang.Class;");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("clazz", new ClassTypeStub("java.lang.Class[]"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.Class[]", type.toString());

    }

    @Test
    public void testDetermineTypeOfDoubleDimArrayAccessExpr() throws Exception {
        ExpressionStmt statement = (ExpressionStmt) ParseHelper.createStatement(
                "clazz[0][1] = Class.forName(\"java.lang.String\");");
        ArrayAccessExpr expr = (ArrayAccessExpr) ((AssignExpr) (statement).getExpression()).getTarget();

        ImportList importList = ParseHelper.createImportList("");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("clazz", new ClassTypeStub("java.lang.Class[][]"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList)
            .determineType(expr);

        assertEquals("java.lang.Class", type.toString());
    }

    @Test
    public void testDetermineTypeOfDoubleDimArrayExpr() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("clazz");

        ImportList importList = ParseHelper.createImportList("");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("clazz", new ClassTypeStub("java.lang.Class[][]"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList)
            .determineType(expr);

        assertEquals("java.lang.Class[][]", type.toString());
    }

    @Test
    public void testFullClassNameProcessing() throws Exception {
        VariableDeclarationExpr expr = (VariableDeclarationExpr) ParseHelper.createExpression("java.lang.String str");

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.lang.Class;");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addClass("java.lang.String", "java.lang.String");

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }
    
    @Test(dependsOnMethods="testCreateVarListWithValues")
    public void testEnclosedExprProcessing() throws Exception {
        ExpressionStmt statement = (ExpressionStmt) ParseHelper.createStatement(
                "var = ((String)a[0]);");
        Expression expr = ((AssignExpr) (statement).getExpression()).getValue();

        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();
        ImportList importList = ParseHelper.createImportList("");

        VariableList varList = ConstructionHelper.createVarListWithValues(ra, "a", "[Ljava.lang.Object;");

        TypeFinder typeFinder = new TypeFinder(ra, varList, importList);
        ClassType result = typeFinder.determineType(expr);

        assertEquals("java.lang.String", result.toString());
    }

    @Test
    public void testCollectionsUnmodifiableSet() throws Exception {
        Expression expr = ParseHelper.createExpression("Collections.unmodifiableSet(set)");

        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

        ImportList importList = ParseHelper.createImportList("import java.util.Collections;");
        VariableList varList = ConstructionHelper.createVarListWithValues(ra, "set", "java.util.Set");
        TypeFinder typeFinder = new TypeFinder(ra, varList, importList);
        ClassType result = typeFinder.determineType(expr);

        assertEquals("java.util.Set<java.lang.Object, >", result.toString());
    }

    @Test(dependsOnGroups = "reflection-abstraction-impl.interface-has-to-string")
    public void testHashMapPut() throws Exception {
        Class clazz = Class.forName("java.lang.reflect.Type");
        Expression expr = ParseHelper.createExpression("map.get(args[0].toString())");

        ClassType mapType = ReflectionAbstractionImplTest
            .createParameterizedClass("java.util.HashMap", 
                    "java.lang.String", 
                    "org.creativelabs.introspection.ClassType");

        ClassType argsType = ra.getClassTypeByName("java.lang.reflect.Type");
        argsType = ra.convertToArray(argsType, 1);

        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("map", mapType);
        varList.put("args", argsType);

        ImportList importList = ParseHelper.createImportList("");

        TypeFinder typeFinder = new TypeFinder(ra, varList, importList);
        ClassType result = typeFinder.determineType(expr);
        assertEquals("org.creativelabs.introspection.ClassType", result.toString());
    }

    @Test
    public void testPartialArrayAccess() throws Exception {
        ImportList importList = ParseHelper.createImportList("");

        ClassType argsType = ra.getClassTypeByName("java.lang.String");
        argsType = ra.convertToArray(argsType, 2);

        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("x", argsType);

        Expression expr = ParseHelper.createExpression("x[3]");
        TypeFinder typeFinder = new TypeFinder(ra, varList, importList);

        ClassType result = typeFinder.determineType(expr);

        assertEquals("[Ljava.lang.String;", result.toString());
    }

    @Test(enabled=false)
    public void testImageIO() throws Exception {
        ImportList importList = ParseHelper.createImportList(
                "import javax.imageio.*; import java.io.*;");

        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("image", ra.getClassTypeByName("java.awt.image.BufferedImage"));
        varList.put("filename", ra.getClassTypeByName("java.lang.String"));

        Expression expr = ParseHelper.createExpression(
                "ImageIO.write(image, \"jpg\", new File(fileName + \".jpg\"))");

        TypeFinder typeFinder = new TypeFinder(ra, varList, importList);

        ClassType result = typeFinder.determineType(expr);
        
        assertEquals("boolean", result.toString());
    }

    @Test
    public void testObjectCreationExpr() throws Exception {
        ImportList importList = ConstructionHelper.createEmptyImportList();
        VariableList varList = ConstructionHelper.createEmptyVariableList();

        Expression expr = ParseHelper.createExpression("new java.util.Map()");

        TypeFinder typeFinder = new TypeFinder(ra, varList, importList);

        ClassType result = typeFinder.determineType(expr);
        assertEquals("java.util.Map<K, V, >", result.toString());
    }

    @Test
    public void testNullLiteralExpression() throws Exception {
        ImportList importList = ConstructionHelper.createEmptyImportList();
        VariableList varList = ConstructionHelper.createEmptyVariableList();

        Expression expr = ParseHelper.createExpression("null");

        TypeFinder typeFinder = new TypeFinder(ra, varList, importList);

        ClassType result = typeFinder.determineType(expr);
        assertTrue(result instanceof ClassTypeNull);
    }
}
