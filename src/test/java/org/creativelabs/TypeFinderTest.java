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

import java.io.File;

import static org.testng.AssertJUnit.assertEquals;

public class TypeFinderTest {

    private ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

    private VariableList createEmptyVariableList() {
        return VariableList.createEmpty();
    }

    private ImportList createEmptyImportList() throws Exception{
        return ParseHelper.createImportList("");
    }

    @Test
    public void testDetermineTypeNameExprVariable() throws Exception {
        Expression expr = ParseHelper.createExpression("string");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("string", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

    @Test
    public void testDetermineTypeNameExprClass() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("String");
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("string", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

    @Test
    public void testDetermineTypeMethodCallStatic() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("String.valueOf(x)");

        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("x", ra.getClassTypeByName(int.class.getName()));

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

    @Test
    public void testDetermineTypeMethodExprVariable() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("str.compareTo(x)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("x", ra.getClassTypeByName(String.class.getName()));
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("int", type.toString());
    }

    @Test
    public void testDetermineTypeFieldAccessExpr() throws Exception {
        FieldAccessExpr expr = (FieldAccessExpr) ParseHelper
                .createExpression("str.CASE_INSENSITIVE_ORDER");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);
        assertEquals("java.util.Comparator<java.lang.String, >", type.toString());
    }

    @Test
    public void testDetermineTypeNonStandardClass() throws Exception {
        Expression expr = ParseHelper.createExpression("Logger.getLogger(str)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("org.apache.log4j.Logger", type.toString());
    }

    @Test
    public void testDetermineTypeNonStandardClassFieldAccess() throws Exception {
        Expression expr = ParseHelper.createExpression("LogLevel.DEBUG");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.lf5.LogLevel;");

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("org.apache.log4j.lf5.LogLevel", type.toString());
    }

    @Test(enabled = false)
    public void testDetermineTypeThrowsUnsupportedExpression() throws Exception {
        Expression expr = ParseHelper.createExpression("x = y");

        String result = "noException";
            ClassType type = new TypeFinder(null, null).determineType(expr);
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

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals(expectedValue, type.toString());
    }

    @Test
    public void testDetermineTypeIntLiteral() throws Exception {
        Expression expr = ParseHelper.createExpression("1");

        ClassType type = new TypeFinder(null, null).determineType(expr);

        assertEquals("java.lang.Integer", type.toString());
    }

    @Test
    public void testDetermineTypeStringLiteral() throws Exception {

        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("\"string\".compareTo(str)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("int", type.toString());

    }

    @Test
    public void testDetermineTypeOfAssignExpressionWithStringLiteral() throws Exception {

        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("str = \"string\"");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

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

        VariableList varTypes = createEmptyVariableList();
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

        VariableList varTypes = createEmptyVariableList();
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

        VariableList varTypes = createEmptyVariableList();
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
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "public static void main(String[] args) {"
                + "this.a = new A();"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(0);
        Expression expr = ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression();

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("a", new ClassTypeStub("org.creativelabs.A"));
        varTypes.put("this", new ClassTypeStub(cd.getName()));

        ClassType type = new TypeFinder(varTypes, importList).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());

    }

    @Test
    public void testNullLiteralAsArgumentOfSimpleArgumentMethod() throws Exception {

        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("\"string\".compareTo(null)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("java.lang.String", "compareTo", new String[]{"java.lang.String"}, "int");
        ImportList imports = ParseHelper.createImportList("");

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("int", type.toString());

    }

    private void testNullLiteralAsArgumentOfOverloadedArgumentsMethod(String expression,
                                                                      String expectedValue) throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression(expression);
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ClassType type = null;
        type = new TypeFinder(varTypes, imports).determineType(expr);
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

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("java.lang.String", type.toString());

    }

    @Test
    public void testSuperLiteralInFieldAccessExpression() throws Exception {
        SuperExpr expr = (SuperExpr) ((FieldAccessExpr) ParseHelper.createExpression("super.someField")).getScope();

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("super", new ClassTypeStub("org.creativelabs.A"));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());
    }

    @Test
    public void testSuperLiteralInMethodCallExpression() throws Exception {
        SuperExpr expr = (SuperExpr) ((MethodCallExpr) ParseHelper.createExpression("super.methodCall()")).getScope();

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("super", new ClassTypeStub("org.creativelabs.A"));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.A", type.toString());
    }

    @DataProvider(name = "binary-ops-list")
    public Object[][] createBinaryOperationTests() throws Exception {
        ImportList emptyImportList = createEmptyImportList();

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
        ClassType type = new TypeFinder(null, imports).determineType(expr);
        assertEquals(expectedType, type.toString());
    }

    @Test
    public void testProcessingArgumentOfMethodSelectClassField() throws Exception{
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "String file;"
                + "String methodCall(File file){"
                + "this.file = new File();"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        FieldAccessExpr expr = (FieldAccessExpr) ((AssignExpr) ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression()).getTarget();

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.io.File;");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("file", new ClassTypeStub(String.class.getName()));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{File.class.getName()}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.String", type.toString());
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

        VariableList varTypes = VariableList.createFromClassFields(cd, importList);
        varTypes.put("file", new ClassTypeStub("java.io.File"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{File.class.getName()}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.io.File", type.toString());
    }

    @Test
    public void testImportDeclarationGetNameGetNameEquals() throws Exception {
        StringLogReflectionAbstraction reflectionAbstraction 
            = StringLogReflectionAbstraction
            .createDecorating(new ReflectionAbstractionImpl());
        Expression expr = ParseHelper.createExpression("id.getName().getName().equals(\"h\")");
        VariableList varList = VariableList.createEmpty();
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

        VariableList varList = VariableList.createEmpty();
        ImportList imports = ParseHelper.createImportList("");

        TypeFinder typeFinder = new TypeFinder(new ReflectionAbstractionImpl(), varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("int", type.toString());
    }

    @Test
    public void testConstructorExpression() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        Expression expr = ParseHelper.createExpression(
                "new ChartDrawer(chartBuilder.getChart()).saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT, fileName)");
        VariableList varList = VariableList.createEmpty();
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
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        Expression expr = ParseHelper.createExpression("BinaryExpr.Operator");
        VariableList varList = VariableList.createEmpty();
        ImportList imports = ParseHelper.createImportList("import japa.parser.ast.expr.*;");

        TypeFinder typeFinder = new TypeFinder(ra, varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("japa.parser.ast.expr.BinaryExpr$Operator", type.toString());
    }

    @Test(enabled = false)
    public void testFieldAccess() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        Expression expr = ParseHelper.createExpression("clazz.typeDeclaration");
        VariableList varList = VariableList.createEmpty();
        varList.put("typeDeclaration", ra.getClassTypeByName("java.lang.String"));
        varList.put("clazz", ra.getClassTypeByName("org.creativelabs.ClassProcessor"));
        ImportList imports = ParseHelper.createImportList("");

        TypeFinder typeFinder = new TypeFinder(ra, varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("japa.parser.ast.body.ClassOrInterfaceDeclaration", type.toString());
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

        VariableList varTypes = createEmptyVariableList();
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

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("clazz", new ClassTypeStub("java.lang.Class[]"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.Class[]", type.toString());

    }

    @Test
    public void testDetermineTypeOfDoubleDimArrayAccessExpr() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "Class[][] clazz;"
                + "String methodCall(){"
                + "clazz[0][1] = Class.forName(\"java.lang.String\");"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        ArrayAccessExpr expr = (ArrayAccessExpr)
                ((AssignExpr)((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression()).getTarget();

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.lang.Class;");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("clazz", new ClassTypeStub("java.lang.Class[][]"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.Class", type.toString());

    }

    @Test
    public void testDetermineTypeOfDoubleDimArrayExpr() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "Class[][] clazz;"
                + "String methodCall(){"
                + "clazz = new Class[2][1];"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        NameExpr expr = (NameExpr) ((AssignExpr)
                ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression()).getTarget();

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.lang.Class;");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("clazz", new ClassTypeStub("java.lang.Class[][]"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.Class[][]", type.toString());

    }

    @Test
    public void testFullClassNameProcessing() throws Exception {
        VariableDeclarationExpr expr = (VariableDeclarationExpr) ParseHelper.createExpression("java.lang.String str");

        ImportList importList = ParseHelper.createImportList(
                "package java.util;"
                + "import java.lang.Class;");

        VariableList varTypes = createEmptyVariableList();

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addClass("java.lang.String", "java.lang.String");

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.lang.String", type.toString());
    }

}
