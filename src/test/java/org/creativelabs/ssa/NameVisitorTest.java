package org.creativelabs.ssa;

import japa.parser.ParseException;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import org.creativelabs.ParseHelper;
import org.creativelabs.ssa.NameVisitor;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class NameVisitorTest {

    @Test
    public void testBlockStmt() throws ParseException {
        BlockStmt blockStmt = ParseHelper.createBlockStmt("void method(int y){" +
                "int x = y + 1;" +
                "int z = x;" +
                "}");

        Set<String> expectedResult = new HashSet<String>() {
            {add("x");}
            {add("y");}
            {add("z");}
        };

        Set<String> actualResult = new HashSet<String>();
        new NameVisitor().visit(blockStmt, actualResult);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNameExpr() throws ParseException {
        NameExpr nameExpr = (NameExpr) ParseHelper.createExpression("a");

        Set<String> expectedResult = new HashSet<String>() {
            {add("a");}
        };

        Set<String> actualResult = new HashSet<String>();
        new NameVisitor().visit(nameExpr, actualResult);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testVariableDeclaratorId() throws ParseException {
        VariableDeclaratorId declaratorId = ((VariableDeclarationExpr)
                ParseHelper.createExpression("int a = 2")).
                getVars().get(0).getId();

        Set<String> expectedResult = new HashSet<String>() {
            {add("a");}
        };

        Set<String> actualResult = new HashSet<String>();
        new NameVisitor().visit(declaratorId, actualResult);

        assertEquals(expectedResult, actualResult);
    }
}
