package org.creativelabs.ssa.visitor;

import japa.parser.ParseException;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.*;
import org.creativelabs.ssa.holder.MethodArgsHolder;
import org.creativelabs.ssa.holder.SimpleMethodArgsHolder;
import org.creativelabs.ssa.holder.SimpleUsingModifyingVariablesHolder;
import org.creativelabs.typefinder.ParseHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Attention! Some tests are using holder in assert, other are using the
 * SimpleUsingModifyingVariablesHolder#getVariablesHolder() method of expressions and statements.
 *
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 14:48
 */
public class UsingModifyingVariablesVisitorTest {

    MethodArgsHolder createMethodArgs() {
        MethodArgsHolder holder = new SimpleMethodArgsHolder();
        return holder;
    }

    @Test
    public void testNameExpr() throws ParseException {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("name");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        new UsingModifyingVariablesVisitor(createMethodArgs()).visit(expr, holder);

        assertEquals("[this.name]", holder.getUsingVariables().toString());
        assertEquals("[]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testAssignExprNameExpr() throws ParseException {
        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("a = b");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        new UsingModifyingVariablesVisitor(createMethodArgs()).visit(expr, holder);

        assertEquals("[this.a, this.b]", holder.getUsingVariables().toString());
        assertEquals("[this.a]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testAssignExprMethodExpr() throws ParseException {
        AssignExpr expr = (AssignExpr) ParseHelper.createExpression("a = method(b)");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        new UsingModifyingVariablesVisitor(createMethodArgs()).visit(expr, holder);

        assertEquals("[this.a, this.b]", holder.getUsingVariables().toString());
        assertEquals("[this.a]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testVariableDeclaratorNameExpr() throws ParseException {
        VariableDeclarationExpr expr = (VariableDeclarationExpr) ParseHelper.createExpression("int a = b;");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        new UsingModifyingVariablesVisitor(createMethodArgs()).visit(expr, holder);

        assertEquals("[a, this.b]", holder.getUsingVariables().toString());
        assertEquals("[]", holder.getModifyingVariables().toString());
    }

    @Test
    public void testVariableDeclaratorMethodExpr() throws ParseException {
        VariableDeclarationExpr expr = (VariableDeclarationExpr) ParseHelper.createExpression("int a = method(b);");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        new UsingModifyingVariablesVisitor(createMethodArgs()).visit(expr, holder);

        assertEquals("[a, this.b]", holder.getUsingVariables().toString());
        assertEquals("[]", holder.getModifyingVariables().toString());
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

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        MethodArgsHolder methodArgs = createMethodArgs();
        methodArgs.addArgName("x");
        new UsingModifyingVariablesVisitor(methodArgs).visit(methodDeclaration.getBody(), holder);

        //check condition
        assertEquals("[x]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check basic block
        assertEquals("[x, y]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check then
        assertEquals("[x]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getThenStmt()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getThenStmt()).
                getVariablesHolder().getModifyingVariables().toString());

        //check else
        assertEquals("[y]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getElseStmt()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getElseStmt()).
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

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        MethodArgsHolder methodArgs = createMethodArgs();
        methodArgs.addArgName("x");
        methodArgs.addArgName("z");
        new UsingModifyingVariablesVisitor(methodArgs).visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, y, z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) methodDeclaration.getBody()).
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
        assertEquals("[]", ((Statement) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getElseStmt()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (x < 2) {" +
                "    x = z - 1;" +
                "}" +
                "}");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        MethodArgsHolder methodArgs = createMethodArgs();
        methodArgs.addArgName("x");
        new UsingModifyingVariablesVisitor(methodArgs).visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, this.z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[x]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check then
        assertEquals("[x, this.z]", ((Statement) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
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

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        MethodArgsHolder methodArgs = createMethodArgs();
        methodArgs.addArgName("x");
        new UsingModifyingVariablesVisitor(methodArgs).visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, this.y, this.z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[x]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[this.y]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());

        //check then
        assertEquals("[x, this.z]", ((Statement) ((WhileStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
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
        assertEquals("[x, this.z]", ((Statement) ((WhileStmt) ((BlockStmt) ((WhileStmt) methodDeclaration.getBody().
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

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        MethodArgsHolder methodArgs = createMethodArgs();
        methodArgs.addArgName("x");
        new UsingModifyingVariablesVisitor(methodArgs).visit(methodDeclaration.getBody(), holder);

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

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        MethodArgsHolder methodArgs = createMethodArgs();
        methodArgs.addArgName("x");
        new UsingModifyingVariablesVisitor(methodArgs).visit(methodDeclaration.getBody(), holder);

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

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        MethodArgsHolder methodArgs = createMethodArgs();
        methodArgs.addArgName("x");
        new UsingModifyingVariablesVisitor(methodArgs).visit(methodDeclaration.getBody(), holder);

        //check condition
        assertEquals("[x]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((IfStmt) methodDeclaration.getBody().getStmts().get(0)).getCondition()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testForeachStmt() throws Exception {
        //TODO arr processing
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int[] arr){" +
                "for (int x : arr) {" +
                "    z = x - 1;" +
                "}" +
                "}");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        new UsingModifyingVariablesVisitor(createMethodArgs()).visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[x, this.z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[this.z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[x]", ((Expression) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getVariable()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getVariable()).
                getVariablesHolder().getModifyingVariables().toString());

        //check body
        assertEquals("[x, this.z]", ((Statement) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[this.z]", ((Statement) ((ForeachStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getModifyingVariables().toString());
    }

    @Test
    public void testForStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "for (int i = 0; i < x; i++) {" +
                "    z = x - i;" +
                "}" +
                "}");

        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        new UsingModifyingVariablesVisitor(createMethodArgs()).visit(methodDeclaration.getBody(), holder);

        //check basic block
        assertEquals("[i, this.x, this.z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[this.z]", ((Statement) methodDeclaration.getBody()).
                getVariablesHolder().getModifyingVariables().toString());

        //check init
        assertEquals("[i]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getInit().get(0)).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getInit().get(0)).
                getVariablesHolder().getModifyingVariables().toString());

        //check condition
        assertEquals("[i, this.x]", ((Expression) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getCompare()).
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
        assertEquals("[i, this.x, this.z]", ((Statement) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getUsingVariables().toString());
        assertEquals("[this.z]", ((Statement) ((ForStmt) methodDeclaration.getBody().getStmts().get(0)).getBody()).
                getVariablesHolder().getModifyingVariables().toString());
    }

}
