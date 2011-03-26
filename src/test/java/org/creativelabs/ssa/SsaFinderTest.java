package org.creativelabs.ssa;


import japa.parser.ParseException;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.IfStmt;
import org.creativelabs.ParseHelper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SsaFinderTest {

    @Test(dataProvider = "literal-provider")
    public void testLiteralExpr(String literal, String expectedResult) throws ParseException {

        LiteralExpr expr = (LiteralExpr) ParseHelper.createExpression(literal);

        new SsaFinder(null, false).determineSsa(expr);

        assertEquals(expectedResult, expr.toString());
    }

    @DataProvider(name = "literal-provider")
    public Object[][] createLiteralList() {
        return new Object[][] {
            { "1", "1"},
            { "1.5", "1.5"},
            { "\"word\"", "\"word\""},
            { "true", "true"},
            { "\'c\'", "\'c\'"},
        };
    }

    @Test
    public void testNameExpr() throws ParseException {

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("testVariable", 1);
        VariablesHolder holder = new VariablesHolder(map);

        NameExpr expr = (NameExpr) ParseHelper.createExpression("testVariable");

        new SsaFinder(holder, false).determineSsa(expr);

        String expectedResult = "testVariable#1";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testNameExprWithIncreaseIndex() throws ParseException {

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("testVariable", 1);
        VariablesHolder holder = new VariablesHolder(map);

        NameExpr expr = (NameExpr) ParseHelper.createExpression("testVariable");

        new SsaFinder(holder, true).determineSsa(expr);

        String expectedResult = "testVariable#2";
        assertEquals(expectedResult, expr.toString());
    }

    @Test(dataProvider = "binary-expr-provider")
    public void testBinaryExpr(String binExpr, String expectedResult) throws ParseException {
        BinaryExpr expr = (BinaryExpr) ((AssignExpr) ParseHelper.createExpression(binExpr)).getValue();

        new SsaFinder(null, false).determineSsa(expr);
        assertEquals(expectedResult, expr.toString());
    }

    @DataProvider(name = "binary-expr-provider")
    public Object[][] createBinaryExprList() {
        return new Object[][] {
            { "i = 1 + 1;", "1 + 1"},
            { "i = 1 - 1;", "1 - 1"},
            { "i = 1 > 1;", "1 > 1"},
            { "i = 1 < 1;", "1 < 1"},
        };
    }

    @Test
    public void testArrayAccessExpr() throws ParseException {
        ArrayAccessExpr expr = (ArrayAccessExpr) ParseHelper.createExpression("a[i]");

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 0);
        map.put("i", 0);
        VariablesHolder holder = new VariablesHolder(map);


        new SsaFinder(holder, false).determineSsa(expr);
        String expectedResult = "a#0[i#0]";
        assertEquals(expectedResult, expr.toString());
        //TODO refactor
    }

    @Test
    public void testArrayAccessExprWithIncreaseIndex() throws ParseException {
        ArrayAccessExpr expr = (ArrayAccessExpr) ParseHelper.createExpression("a[i]");

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 0);
        map.put("i", 1);
        VariablesHolder holder = new VariablesHolder(map);


        new SsaFinder(holder, true).determineSsa(expr);
        String expectedResult = "a#0[i#1]";
        assertEquals(expectedResult, expr.toString());
        //TODO refactor
    }

    @Test
    public void testArrayCreationExpr() throws ParseException {
        ArrayCreationExpr expr = (ArrayCreationExpr) ParseHelper.createExpression("new Type[5]");

        new SsaFinder(null, false).determineSsa(expr);
        String expectedResult = "new Type[5]";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testObjectCreationExprWithoutArgument() throws ParseException {
        ObjectCreationExpr expr = (ObjectCreationExpr) ParseHelper.createExpression("new A()");

        new SsaFinder(null, false).determineSsa(expr);
        String expectedResult = "new A()";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testObjectCreationExpr() throws ParseException {
        ObjectCreationExpr expr = (ObjectCreationExpr) ParseHelper.createExpression("new A(a, 4)");

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 0);
        VariablesHolder holder = new VariablesHolder(map);

        new SsaFinder(holder, false).determineSsa(expr);
        String expectedResult = "new A(a#0, 4)";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testVoidMethodCallExpr() throws ParseException {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("method()");

        new SsaFinder(null, false).determineSsa(expr);
        String expectedResult = "method()";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testMethodCallExpr() throws ParseException {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("method(a, 4)");

        Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("a", 0);
                VariablesHolder holder = new VariablesHolder(map);

        new SsaFinder(holder, false).determineSsa(expr);
        String expectedResult = "method(a#0, 4)";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testInstanceOfExpr() throws ParseException {
        InstanceOfExpr expr = (InstanceOfExpr) ((IfStmt) ParseHelper.createStatement("if (a instanceof A) {}")).getCondition();

        Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("a", 0);
                VariablesHolder holder = new VariablesHolder(map);

        new SsaFinder(holder, false).determineSsa(expr);
        String expectedResult = "a#0 instanceof A";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testCastExpr() throws ParseException {
        CastExpr expr = (CastExpr) ((AssignExpr) ParseHelper.createExpression("b = (A) a;")).getValue();

        Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("a", 0);
                VariablesHolder holder = new VariablesHolder(map);

        new SsaFinder(holder, false).determineSsa(expr);
        String expectedResult = "(A) a#0";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testEnclosedExpr() throws ParseException {
        EnclosedExpr expr = (EnclosedExpr) ((AssignExpr) ParseHelper.createExpression("b = ((A) a);")).getValue();

        Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("a", 0);
                VariablesHolder holder = new VariablesHolder(map);

        new SsaFinder(holder, false).determineSsa(expr);
        String expectedResult = "((A) a#0)";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testArrayInitializerExpr() throws ParseException {
        ArrayInitializerExpr expr = ((ArrayCreationExpr) ParseHelper.createExpression("new int[]{1, a};")).getInitializer();

        Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("a", 0);
                VariablesHolder holder = new VariablesHolder(map);

        new SsaFinder(holder, false).determineSsa(expr);
        String expectedResult = "{ 1, a#0 }";
        assertEquals(expectedResult, expr.toString());
    }

    @Test
    public void testUnaryExpr() throws ParseException {
        UnaryExpr expr = (UnaryExpr) ((AssignExpr) ParseHelper.createExpression("b = -1;")).getValue();

        new SsaFinder(null, false).determineSsa(expr);
        String expectedResult = "-1";
        assertEquals(expectedResult, expr.toString());
    }
}
