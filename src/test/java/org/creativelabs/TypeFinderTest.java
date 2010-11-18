package org.creativelabs; 

import org.testng.annotations.Test;
import org.testng.annotations.Configuration;
import org.creativelabs.TypeFinder;

import java.util.HashMap;

import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.Expression;

import static org.testng.AssertJUnit.*;

public class TypeFinderTest {

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
    public void testDetermineTypeExpression() throws Exception {
        Expression expr = ParseHelper.createExpression("string");

        HashMap<String, Class> varTypes = new HashMap<String, Class> ();
        varTypes.put("string", String.class);

        String type = new TypeFinder().determineType(expr, varTypes);

        assertEquals("java.lang.String", type);
    }

    @Test
    public void testDetermineTypeMethodCall() throws Exception {
        Expression expr = ParseHelper.createExpression("string.compareTo(x)");

        HashMap<String, Class> varTypes = new HashMap<String, Class> ();
        varTypes.put("string", String.class);
        varTypes.put("x", String.class);

        String type = new TypeFinder().determineType(expr, varTypes);

        assertEquals("int", type);
    }

    @Test
    public void testDetermineTypeForFieldAccess() throws Exception {
        Expression expr = ParseHelper.createExpression("str.CASE_INSENSITIVE_ORDER");

        HashMap<String, Class> varTypes = new HashMap<String, Class> ();
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes);
        assertEquals("java.util.Comparator", type);
    }

    @Test
    public void testDetermineType() throws Exception {
        MethodCallExpr expr = (MethodCallExpr)ParseHelper.createExpression("String.valueOf(x)");

        HashMap<String, Class> varTypes = new HashMap<String, Class> ();
        varTypes.put("x", int.class);
        
        String type = new TypeFinder().determineType(expr, varTypes);

        assertEquals("java.lang.String", type);
    }

    @Test
    public void testDetermineTypeMethodExprVariable() throws Exception {
        MethodCallExpr expr = (MethodCallExpr)ParseHelper.createExpression("str.compareTo(x)");

        HashMap<String, Class> varTypes = new HashMap<String, Class> ();
        varTypes.put("x", String.class);
        varTypes.put("str", String.class);
        
        String type = new TypeFinder().determineType(expr, varTypes);

        assertEquals("int", type);
    }

    @Test
    public void testDetermineTypeFieldAccessExpr() throws Exception {
        FieldAccessExpr expr = (FieldAccessExpr)ParseHelper.createExpression("str.CASE_INSENSITIVE_ORDER");

        HashMap<String, Class> varTypes = new HashMap<String, Class> ();
        varTypes.put("str", String.class);

        String type = new TypeFinder().determineType(expr, varTypes);
        assertEquals("java.util.Comparator", type);
    }
}
