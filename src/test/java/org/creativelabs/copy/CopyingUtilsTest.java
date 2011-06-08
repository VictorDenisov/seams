package org.creativelabs.copy;

import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ReturnStmt;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 11:25
 */
public class CopyingUtilsTest {

    private void validate(Object original, Object copy) {
        if (original == copy) {
            fail("The original and it copy are the same object!!!");
        }
        assertEquals(original, copy);
    }

    @Test
    public void testCopyInteger() throws Exception {

        Integer original = new Integer(0);
        Integer copy = CopyingUtils.copy(original);

        validate(original, copy);
    }

    @Test
    public void testCopyNameExpr() throws Exception {

        NameExpr original = new NameExpr("original");
        NameExpr copy = CopyingUtils.copy(original);

        validate(original, copy);
    }

    @Test
    public void testCopyReturnStmt() throws Exception {

        ReturnStmt original = new ReturnStmt();
        ReturnStmt copy = CopyingUtils.copy(original);

        validate(original, copy);
    }
}
