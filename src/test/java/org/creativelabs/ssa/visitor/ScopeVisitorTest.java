package org.creativelabs.ssa.visitor;

import japa.parser.ParseException;
import japa.parser.ast.expr.FieldAccessExpr;
import org.creativelabs.typefinder.ParseHelper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author azotcsit
 *         Date: 17.08.11
 *         Time: 22:36
 */
public class ScopeVisitorTest {

    @Test
    public void testSimpleScope() throws ParseException {
        FieldAccessExpr expr = (FieldAccessExpr) ParseHelper.createExpression("a.b");
        String actual = new ScopeVisitor().visit(expr, null);
        String expected = "a.b";

        assertEquals(actual, expected);
    }

    @Test
    public void testLongScope() throws ParseException {
        FieldAccessExpr expr = (FieldAccessExpr) ParseHelper.createExpression("a.b.c.d");
        String actual = new ScopeVisitor().visit(expr, null);
        String expected = "a.b.c.d";

        assertEquals(actual, expected);
    }

    @Test
    public void testCastScope() throws ParseException {
        FieldAccessExpr expr = (FieldAccessExpr) ParseHelper.createExpression("((b) a.b).c.d");
        String actual = new ScopeVisitor().visit(expr, null);
        String expected = "a.b.c.d";

        assertEquals(actual, expected);
    }

}
