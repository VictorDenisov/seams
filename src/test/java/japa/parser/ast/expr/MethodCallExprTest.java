package japa.parser.ast.expr;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author azotcsit
 *         Date: 18.06.11
 *         Time: 18:25
 */
public class MethodCallExprTest {
    @Test
    public void testGetModifier() throws Exception {
        MethodCallExpr expr = new MethodCallExpr();
        expr.setModifier(0);
        assertEquals(0, expr.getModifier());
    }
}
