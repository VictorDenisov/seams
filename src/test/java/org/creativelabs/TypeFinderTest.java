package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import org.creativelabs.introspection.*;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.AssertJUnit.assertEquals;

public class TypeFinderTest {

    private ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

    private VariableList createEmptyVariableList() {
        return new VariableList();
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

        assertEquals("java.lang.String", type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeNameExprClass() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("String");
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("string", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("java.lang.String", type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeMethodCallStatic() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("String.valueOf(x)");

        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("x", ra.getClassTypeByName(int.class.getName()));

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("java.lang.String", type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeMethodExprVariable() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("str.compareTo(x)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("x", ra.getClassTypeByName(String.class.getName()));
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("int", type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeFieldAccessExpr() throws Exception {
        FieldAccessExpr expr = (FieldAccessExpr) ParseHelper
                .createExpression("str.CASE_INSENSITIVE_ORDER");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);
        assertEquals("java.util.Comparator", type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeNonStandardClass() throws Exception {
        Expression expr = ParseHelper.createExpression("Logger.getLogger(str)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("org.apache.log4j.Logger", type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeNonStandardClassFieldAccess() throws Exception {
        Expression expr = ParseHelper.createExpression("LogLevel.DEBUG");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.lf5.LogLevel;");

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("org.apache.log4j.lf5.LogLevel", type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeThrowsUnsupportedExpression() throws Exception {
        Expression expr = ParseHelper.createExpression("x = y");

        String result = "noException";
        try {
            ClassType type = new TypeFinder(null, null).determineType(expr);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("UnsupportedExpressionException", result);
    }

    private void testDetermineTypeOfMethodWithLiteralsAsArgument(String expression,
                                                                 String expectedValue) throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression(expression);
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals(expectedValue, type.toStringRepresentation());
    }

    @Test
    public void testDetermineTypeOfMethodWithLiteralsAsArgumentString() throws Exception {
        testDetermineTypeOfMethodWithLiteralsAsArgument("str.compareTo(\"string\")", "int");
    }

    @Test
    public void testDetermineTypeOfMethodWithLiteralsAsArgumentInt() throws Exception {
        testDetermineTypeOfMethodWithLiteralsAsArgument("str.substring(1)", "java.lang.String");
    }

    @Test
    public void testDetermineTypeOfMethodWithLiteralsAsArgumentBoolean() throws Exception {
        testDetermineTypeOfMethodWithLiteralsAsArgument("String.valueOf(true)", "java.lang.String");
    }

    @Test
    public void testDetermineTypeOfMethodWithLiteralsAsArgumentDouble() throws Exception {
        testDetermineTypeOfMethodWithLiteralsAsArgument("String.valueOf(1.5)", "java.lang.String");
    }

    @Test
    public void testDetermineTypeStringLiteral() throws Exception {

        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("\"string\".compareTo(str)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("int", type.toStringRepresentation());

    }

    @Test
    public void testDetermineTypeOfAssignExpressionWithStringLiteral() throws Exception {

        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("str = \"string\"");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("java.lang.String", type.toStringRepresentation());

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

        assertEquals("org.creativelabs.A", type.toStringRepresentation());
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

        assertEquals("org.creativelabs.A", type.toStringRepresentation());
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

        assertEquals("int", type.toStringRepresentation());
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

        assertEquals("org.creativelabs.A", type.toStringRepresentation());

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

        assertEquals("int", type.toStringRepresentation());

    }

    private void testNullLiteralAsArgumentOfOverloadedArgumentsMethod(String expression,
                                                                      String expectedValue) throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression(expression);
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        String result = "noException";
        ClassType type = null;
        try {
            type = new TypeFinder(varTypes, imports).determineType(expr);
        } catch (java.lang.NoSuchMethodException e) {
            result = "java.lang.NoSuchMethodException";
        }
        assertEquals("noException", result);
        assertEquals(expectedValue, type.toStringRepresentation());
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

        assertEquals("java.lang.String", type.toStringRepresentation());

    }

    @Test
    public void testSuperLiteralInFieldAccessExpression() throws Exception {
        SuperExpr expr = (SuperExpr) ((FieldAccessExpr) ParseHelper.createExpression("super.someField")).getScope();

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("super", new ClassTypeStub("org.creativelabs.A"));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.A", type.toStringRepresentation());
    }

    @Test
    public void testSuperLiteralInMethodCallExpression() throws Exception {
        SuperExpr expr = (SuperExpr) ((MethodCallExpr) ParseHelper.createExpression("super.methodCall()")).getScope();

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("super", new ClassTypeStub("org.creativelabs.A"));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);

        assertEquals("org.creativelabs.A", type.toStringRepresentation());
    }

    @Test
    public void testPlusBinaryExpressionIntInt() throws Exception {
        testBinaryExpression("i = 1 + 1;", "int", createEmptyImportList());

    }

    @Test
    public void testPlusBinaryExpressionIntChar() throws Exception {
        testBinaryExpression("i = 1 + '1';", "char", createEmptyImportList());
    }

    @Test
    public void testPlusBinaryExpressionIntString() throws Exception {
        testBinaryExpression("i = 1 + \"1\";", "java.lang.String", createEmptyImportList());
    }

    @Test
    public void testPlusBinaryExpressionIntFloat() throws Exception {
        testBinaryExpression("i = 1 + new Float(1.5);", "float", createEmptyImportList());
    }

    @Test
    public void testPlusBinaryExpressionIntShort() throws Exception {
        testBinaryExpression("i = 1 + new Short(1);", "int", createEmptyImportList());
    }

    @Test
    public void testPlusBinaryExpressionIntLong() throws Exception {
        testBinaryExpression("i = 1 + new Long(1);", "long", createEmptyImportList());
    }

    @Test
    public void testPlusBinaryExpressionFloatLong() throws Exception {
        testBinaryExpression("i = new Float(1.1) + new Long(1);", "float", createEmptyImportList());
    }

    @Test
    public void testPlusBinaryExpressionIntDouble() throws Exception {
        testBinaryExpression("i = 1 + 1.5;", "double", createEmptyImportList());
    }

    @Test
    public void testMinusBinaryExpressionIntFloat() throws Exception {
        testBinaryExpression("i = 1 - new Float(1.5);", "float", createEmptyImportList());
    }

    @Test
    public void testMinusBinaryExpressionIntShort() throws Exception {
        testBinaryExpression("i = 1 - new Short(1);", "int", createEmptyImportList());
    }

    @Test
    public void testMinusBinaryExpressionIntLong() throws Exception {
        testBinaryExpression("i = 1 - new Long(1);", "long", createEmptyImportList());
    }

    @Test
    public void testMinusBinaryExpressionFloatLong() throws Exception {
        testBinaryExpression("i = new Float(1.1) - new Long(1);", "float", createEmptyImportList());
    }

    @Test
    public void testMinusBinaryExpressionIntDouble() throws Exception {
        testBinaryExpression("i = 1 - 1.5;", "double", createEmptyImportList());
    }

    @Test
    public void testDivideBinaryExpressionIntInt() throws Exception {
        testBinaryExpression("i = 1 / 1;", "int", createEmptyImportList());
    }

    @Test
    public void testDivideBinaryExpressionIntDouble() throws Exception {
        testBinaryExpression("i = 1 / new Double(1.5);", "double", createEmptyImportList());
    }

    @Test
    public void testDivideBinaryExpressionFloatInt() throws Exception {
        testBinaryExpression("i = new Float(1) / 1;", "float", createEmptyImportList());
    }

    @Test
    public void testRemainderBinaryExpressionFloatInt() throws Exception {
        testBinaryExpression("i = new Float(1) % 1;", "float", createEmptyImportList());
    }

    @Test
    public void testRemainderBinaryExpressionDoubleInt() throws Exception {
        testBinaryExpression("i = new Double(1) % 1;", "double", createEmptyImportList());
    }

    @Test
    public void testRemainderBinaryExpressionLongInt() throws Exception {
        testBinaryExpression("i = new Long(1) % 1;", "long", createEmptyImportList());
    }

    @Test
    public void testRemainderBinaryExpressionIntInt() throws Exception {
        testBinaryExpression("i = 2 % 1;", "int", createEmptyImportList());
    }

    @Test
    public void testRemainderBinaryExpressionShortInt() throws Exception {
        testBinaryExpression("i = new Short(1) % 1;", "int", createEmptyImportList());
    }

    private void testBinaryExpression(String expression, String expectedType, ImportList imports) throws Exception{
        BinaryExpr expr = (BinaryExpr) ((AssignExpr) ParseHelper.createExpression(expression)).getValue();
        ClassType type = new TypeFinder(null, imports).determineType(expr);
        assertEquals(expectedType, type.toStringRepresentation());
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

        assertEquals("java.lang.String", type.toStringRepresentation());
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

        VariableList varTypes = new VariableList(cd, importList);
        varTypes.put("file", new ClassTypeStub("java.io.File"));

        TestingReflectionAbstraction reflectionAbstraction = new TestingReflectionAbstraction();
        reflectionAbstraction.addMethod("Sample", "methodCall", new String[]{File.class.getName()}, String.class.getName());

        ClassType type = new TypeFinder(reflectionAbstraction, varTypes, importList).determineType(expr);

        assertEquals("java.io.File", type.toStringRepresentation());
    }
}
