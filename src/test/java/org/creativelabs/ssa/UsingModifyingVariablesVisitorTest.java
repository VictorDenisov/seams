package org.creativelabs.ssa;

import japa.parser.ParseException;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.*;
import japa.parser.ast.helper.UMVariablesHolder;
import japa.parser.ast.stmt.*;
import org.creativelabs.ParseHelper;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;

/**
 * Attention! Some tests are using holder in assert, other are using the
 * UMVariablesHolder#getVariablesHolder() method of expressions and statements.
 *
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 14:48
 */
public class UsingModifyingVariablesVisitorTest {

    @Test
    public void testNameExpr() throws ParseException {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("name");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(expr, holder);

        assertEquals("[name]", holder.getUsingVariables().toString());
        assertEquals("[]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testAssignExprNameExpr() throws ParseException {
        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("a = b");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(expr, holder);

        assertEquals("[a, b]", holder.getUsingVariables().toString());
        assertEquals("[a]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testAssignExprMethodExpr() throws ParseException {
        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("a = method(b)");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(expr, holder);

        assertEquals("[a, b]", holder.getUsingVariables().toString());
        assertEquals("[a]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testVariableDeclaratorNameExpr() throws ParseException {
        VariableDeclarationExpr expr = (VariableDeclarationExpr) ParseHelper.createExpression("int a = b;");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(expr, holder);

        assertEquals("[a, b]", holder.getUsingVariables().toString());
        assertEquals("[a]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testVariableDeclaratorMethodExpr() throws ParseException {
        VariableDeclarationExpr expr = (VariableDeclarationExpr) ParseHelper.createExpression("int a = method(b);");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(expr, holder);

        assertEquals("[a, b]", holder.getUsingVariables().toString());
        assertEquals("[a]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testIfElseStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "if (x < 1) {" +
                "    x = 2;" +
                "} else {" +
                "    int y = 0;" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check condition
        assertEquals("[x]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check basic block
        assertEquals("[x, y]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x, y]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check then
        assertEquals("[x]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getThenStmt()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getThenStmt()).
                getVariablesHolder().getModifyingVariables().toString());

        //check else
        assertEquals("[y]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getElseStmt()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[y]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getElseStmt()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testNestedIfElseStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int z){" +
                "if (x < 1) {" +
                "    x = 2;" +
                "    if (x != null) {" +
                "        method(z);" +
                "    }" +
                "} else {" +
                "    int y = 0;" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, y, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x, y]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[x]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check then
        assertEquals("[x, z]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getThenStmt()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getThenStmt()).
                getVariablesHolder().getModifyingVariables().toString());

        //check nested condition
        assertEquals("[x]", ((Expression) ((IfStmt) ((BlockStmt) ((IfStmt) methodDeclaration.getBody().getStmts().
                get(0)).getThenStmt()).getStmts().get(1)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) ((BlockStmt) ((IfStmt) methodDeclaration.getBody().getStmts().
                get(0)).getThenStmt()).getStmts().get(1)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check nested then
        assertEquals("[z]", ((Statement) ((IfStmt) ((BlockStmt) ((IfStmt) methodDeclaration.getBody().getStmts().
                get(0)).getThenStmt()).getStmts().get(1)).getThenStmt()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Statement) ((IfStmt) ((BlockStmt) ((IfStmt) methodDeclaration.getBody().getStmts().
                get(0)).getThenStmt()).getStmts().get(1)).getThenStmt()).
                getVariablesHolder().getModifyingVariables().toString());

        //check else
        assertEquals("[y]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getElseStmt()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[y]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getElseStmt()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (x < 2) {" +
                "    x = z - 1;" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[x]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check then
        assertEquals("[x, z]", ((Statement) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testNestedWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (y < 2) {" +
                "    while (x < 2) {" +
                "        x = z - 1;" +
                "    }" +
                "    x = z - 1;" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, y, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[y]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check then
        assertEquals("[x, z]", ((Statement) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check nested condition
        assertEquals("[x]", ((Expression) ((WhileStmt) ((BlockStmt) ((WhileStmt) methodDeclaration.getBody().
                getStmts().get(0)).getBody()).getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((WhileStmt) ((BlockStmt) ((WhileStmt) methodDeclaration.getBody().
                getStmts().get(0)).getBody()).getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check nested then
        assertEquals("[x, z]", ((Statement) ((WhileStmt) ((BlockStmt) ((WhileStmt) methodDeclaration.getBody().
                getStmts().get(0)).getBody()).getStmts().get(0)).getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) ((WhileStmt) ((BlockStmt) ((WhileStmt) methodDeclaration.getBody().
                getStmts().get(0)).getBody()).getStmts().get(0)).getBody()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testBinaryExprCondition() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "if (x < 1) {" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check condition
        assertEquals("[x]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testMethodExprCondition() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "if (method(x)) {" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check condition
        assertEquals("[x]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testNameExprCondition() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "if (x) {" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check condition
        assertEquals("[x]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testForeachStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int[] arr){" +
                "for (int x : arr) {" +
                "    z = x - 1;" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[x]", ((Expression) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getVariable()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Expression) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getVariable()).
                getVariablesHolder().getModifyingVariables().toString());

        //check body
        assertEquals("[x, z]", ((Statement) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[z]", ((Statement) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testForStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "for (int i = 0; i < x; i++) {" +
                "    z = x - i;" +
                "}" +
                "}");

        UMVariablesHolder holder = new UMVariablesHolder();
        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[i, x, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[i, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check init
        assertEquals("[i]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getInit().get(0)).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[i]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getInit().get(0)).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[i, x]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getCompare()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getCompare()).
                getVariablesHolder().getModifyingVariables().toString());

        //check update
        assertEquals("[i]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getUpdate().get(0)).
                getVariablesHolder().getUsingVariables().toString());
        //TODO to implement unary expression
//        assertEquals("[i]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getUpdate().get(0)).
//                getVariablesHolder().getModifyingVariables().toString());

        //check body
        assertEquals("[i, x, z]", ((Statement) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[z]", ((Statement) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getModifyingVariables().toString());
    }


}
