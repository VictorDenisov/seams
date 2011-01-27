package org.creativelabs;


import japa.parser.ParseException;
import japa.parser.ast.expr.*;
import org.creativelabs.introspection.ClassType;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SsaFinderTest {

    @Test(dataProvider = "literal-provider")
    public void testLiteralExpr(String literal, String expectedResult) throws ParseException {

        LiteralExpr expr = (LiteralExpr) ParseHelper.createExpression(literal);

        String actualResult = new SsaFinder(null, false).determineSsa(expr);

        assertEquals(expectedResult, actualResult);
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

        String actualResult = new SsaFinder(holder, false).determineSsa(expr);

        String expectedResult = "testVariable1";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNameExprWithIncreaseIndex() throws ParseException {

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("testVariable", 1);
        VariablesHolder holder = new VariablesHolder(map);

        NameExpr expr = (NameExpr) ParseHelper.createExpression("testVariable");

        String actualResult = new SsaFinder(holder, true).determineSsa(expr);

        String expectedResult = "testVariable2";
        assertEquals(expectedResult, actualResult);
    }

    @Test(dataProvider = "binary-expr-provider")
    public void testBinaryExpr(String binExpr, String expectedResult) throws ParseException {
        BinaryExpr expr = (BinaryExpr) ((AssignExpr) ParseHelper.createExpression(binExpr)).getValue();

        String actualResult = new SsaFinder(null, false).determineSsa(expr);
        assertEquals(expectedResult, actualResult);
    }

    @DataProvider(name = "binary-expr-provider")
    public Object[][] createBinaryExprList() {
        return new Object[][] {
            { "i = 1 + 1;", "1 plus 1"},
            { "i = 1 - 1;", "1 minus 1"},
            { "i = 1 > 1;", "1 greater 1"},
            { "i = 1 < 1;", "1 less 1"},
        };
    }

    @Test
    public void testArrayAccessExpr() throws ParseException {
        ArrayAccessExpr expr = (ArrayAccessExpr) ParseHelper.createExpression("a[i]");

        String actualResult = new SsaFinder(null, false).determineSsa(expr);
        String expectedResult = "Access(a,i)";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayAccessExprWithIncreaseIndex() throws ParseException {
        ArrayAccessExpr expr = (ArrayAccessExpr) ParseHelper.createExpression("a[i]");

        String actualResult = new SsaFinder(null, true).determineSsa(expr);
        String expectedResult = "Update(a,i)";
        assertEquals(expectedResult, actualResult);
    }

}
