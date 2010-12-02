package org.creativelabs;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.BlockStmt;
import org.testng.annotations.Test;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

import static org.testng.AssertJUnit.*;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 30.11.2010
 * Time: 23:39:37
 * To change this template use File | Settings | File Templates.
 */
public class DependencyCounterVisitorTest {

    @Test
    public void testVisitBlockStmtForEmptyMethod() throws Exception {
        BlockStmt blockStmt = ParseHelper.createBlockStmt("public void method(){}");
        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(null);
        String result = "noException";
        try {
            dependencyCounter.visit(blockStmt, null);
        } catch (NullPointerException e) {
            result = "NullPointerException";
        }
        assertEquals("noException", result);
    }
}
