package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.ExpressionStmt;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class TypeFinderTest {

    private VariableList createEmptyVariableList() {
        return new VariableList();
    }

    @Test
    public void testGetReturnType() throws Exception {
        Class[] types = new Class[1];
        types[0] = String.class;
        String returnType = new TypeFinder().getReturnType("java.lang.String", "matches", types);
        assertEquals("boolean", returnType);
    }

    @Test
    public void testGetFieldType() throws Exception {
        String fieldType = new TypeFinder().getFieldType("java.lang.String", "CASE_INSENSITIVE_ORDER");
        assertEquals("java.util.Comparator", fieldType);
    }

    @Test
    public void testGetReturnTypeArray() throws Exception {
        Class[] types = new Class[1];
        types[0] = String.class;
        String returnType = new TypeFinder().getReturnType("java.lang.String", "split", types);
        assertEquals("[Ljava.lang.String;", returnType);
    }

    @Test
    public void testGetReturnTypeComment() throws Exception {
        String returnType = new TypeFinder().getReturnType("japa.parser.ast.Comment",
                "getContent", new Class[0]);
        assertEquals("java.lang.String", returnType);
    }

    @Test
    public void testGetReturnTypeCommentVoid() throws Exception {
        Class[] types = new Class[1];
        types[0] = String.class;
        String returnType = new TypeFinder().getReturnType("japa.parser.ast.Comment",
                "setContent", types);
        assertEquals("void", returnType);
    }

    @Test
    public void testDetermineTypeNameExprVariable() throws Exception {
        Expression expr = ParseHelper.createExpression("string");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("string", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, null);

        assertEquals("java.lang.String", type);
    }

    @Test
    public void testDetermineTypeNameExprClass() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("String");
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("string", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, imports);

        assertEquals("java.lang.String", type);
    }

    @Test
    public void testDetermineTypeMethodCallStatic() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("String.valueOf(x)");

        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("x", int.class);

        String type = new TypeFinder().determineType(expr, varTypes, imports);

        assertEquals("java.lang.String", type);
    }

    @Test
    public void testDetermineTypeMethodExprVariable() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("str.compareTo(x)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("x", String.class);
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, null);

        assertEquals("int", type);
    }

    @Test
    public void testDetermineTypeFieldAccessExpr() throws Exception {
        FieldAccessExpr expr = (FieldAccessExpr) ParseHelper
                .createExpression("str.CASE_INSENSITIVE_ORDER");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, null);
        assertEquals("java.util.Comparator", type);
    }

    @Test
    public void testDetermineTypeNonStandardClass() throws Exception {
        Expression expr = ParseHelper.createExpression("Logger.getLogger(str)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", String.class);

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        String type = new TypeFinder().determineType(expr, varTypes, imports);

        assertEquals("org.apache.log4j.Logger", type);
    }

    @Test
    public void testDetermineTypeNonStandardClassFieldAccess() throws Exception {
        Expression expr = ParseHelper.createExpression("LogLevel.DEBUG");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", String.class);

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.lf5.LogLevel;");

        String type = new TypeFinder().determineType(expr, varTypes, imports);

        assertEquals("org.apache.log4j.lf5.LogLevel", type);
    }

    @Test
    public void testDetermineTypeThrowsUnsupportedExpression() throws Exception {
        Expression expr = ParseHelper.createExpression("x = y");

        String result = "noException";
        try {
            String type = new TypeFinder().determineType(expr, null, null);
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
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, imports);

        assertEquals(expectedValue, type);
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
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, null);

        assertEquals("int", type);

    }

    @Test
    public void testDetermineTypeOfAssignExpressionWithStringLiteral() throws Exception {

        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("str = \"string\"");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, null);

        assertEquals("java.lang.String", type);

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
        importList.put("this", cd.getName());

        MethodList methodList = new MethodList(cd);

        String result = "noException";
        String type = null;
        try {
             type = new TypeFinder().determineType(expr, null, methodList, importList);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("noException", result);
        assertEquals("org.creativelabs.A", type);
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
        String className = cd.getName();
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        Expression expr = ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression();

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");
        importList.put("this", cd.getName());

        MethodList methodList = new MethodList(cd);

        String result = "noException";
        String type = null;
        try {
            type = new TypeFinder().determineType(expr, null, methodList, importList);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("noException", result);
        assertEquals("org.creativelabs.A", type);
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
        String className = cd.getName();
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(1);
        Expression expr = ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression();

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", String.class);

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");
        importList.put("this", cd.getName());

        MethodList methodList = new MethodList(cd);

        String result = "noException";
        String type = null;
        try {
            type = new TypeFinder().determineType(expr, varTypes, methodList, importList);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("noException", result);
        assertEquals("int", type);
    }

    @Test
    public void testDetermineTypeOfVariableWithThisLiteral() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "public static void main(String[] args) {"
                + "this.a = new A();"
                + "this.b = 3;"
                + "c = this.b;"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        String className = cd.getName();
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(0);
        Expression expr = ((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression();

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("a", "org.creativelabs.A");
        varTypes.put("b", int.class);
        varTypes.put("c", int.class);

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");
        importList.put("this", cd.getName());

        String result = "noException";
        String type = null;
        try {
            type = new TypeFinder().determineType(expr, varTypes, importList);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("noException", result);
        assertEquals("org.creativelabs.A", type);

        expr = ((ExpressionStmt) md.getBody().getStmts().get(1)).getExpression();
        type = null;
        try {
            type = new TypeFinder().determineType(expr, varTypes, importList);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("noException", result);
        assertEquals("int", type);

        expr = ((ExpressionStmt) md.getBody().getStmts().get(2)).getExpression();
        type = null;
        try {
            type = new TypeFinder().determineType(expr, varTypes, importList);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("noException", result);
        assertEquals("int", type);
    }

    @Test
    public void testNullLiteralAsArgumentOfSimpleArgumentMethod() throws Exception {

        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("\"string\".compareTo(null)");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, null);

        assertEquals("int", type);

    }

    private void testNullLiteralAsArgumentOfOverloadedArgumentsMethod(String expression, 
            String expectedValue) throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression(expression);
        ImportList imports = ParseHelper.createImportList("");

        VariableList varTypes = createEmptyVariableList();
        varTypes.put("str", String.class);

        String result = "noException";
        String type = null;
        try {
            type = new TypeFinder().determineType(expr, varTypes, imports);
        } catch (java.lang.NoSuchMethodException e) {
            result = "java.lang.NoSuchMethodException";
        }
        assertEquals("noException", result);
        assertEquals(expectedValue, type);
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
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes, null);

        assertEquals("java.lang.String", type);

    }

    @Test
    public void testSuperLiteral() throws Exception{
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample extends A {"
                + "public static void main(String[] args) {"
                + "super.methodCall();"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration md = (MethodDeclaration) cd.getMembers().get(0);
        Expression expr = ((MethodCallExpr)((ExpressionStmt) md.getBody().getStmts().get(0)).getExpression()).getScope();

        ImportList importList = ParseHelper.createImportList(
                "import org.creativelabs.A;");
        importList.put("super", importList.get(cd.getExtends().get(0).getName()));
        String result = "noException";
        String type = null;
        try {
            type = new TypeFinder().determineType(expr, null, importList);
        } catch (TypeFinder.UnsupportedExpressionException e) {
            result = "UnsupportedExpressionException";
        }
        assertEquals("noException", result);
        assertEquals("org.creativelabs.A", type);
    }
}
