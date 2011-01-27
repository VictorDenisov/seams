package org.creativelabs;

import japa.parser.ast.body.MethodDeclaration;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

public class SsaFormVisitorTest {

    @Test
    public void testArgumentsOfMethod() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = 1;" +
                "}");


        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x1 <- 1\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testCreateNewVariable() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "int x = 1;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x0 <- 1\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testNameExprValueOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = x;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x1 <- x0\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testLiteralExprValueOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = 2;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x1 <- 2\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testBinaryExprValueOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = x + 2;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x1 <- x0 plus 2\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testBinaryExprValueWithTwoVariablesOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "int y = 1;" +
                "x = x + y;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("y0 <- 1\n");
        expectedResult.append("x1 <- x0 plus y0\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testBinaryExprValueWithThreeVariablesOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "x = x + y + y;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x1 <- x0 plus y0 plus y0\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testIfStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "if (x > y) {" +
                "x = 1;" +
                "y = x + y;" +
                "} else {" +
                "x = y;" +
                "y = x + 2;" +
                "}" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("if x0 greater y0\n");
        expectedResult.append("then do\n");
        expectedResult.append("x1 <- 1\n");
        expectedResult.append("y1 <- x1 plus y0\n");
        expectedResult.append("end\n");
        expectedResult.append("else do\n");
        expectedResult.append("x2 <- y0\n");
        expectedResult.append("y2 <- x2 plus 2\n");
        expectedResult.append("end\n");
        expectedResult.append("phi(y1,y2)\n");
        expectedResult.append("phi(x1,x2)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testForStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "for (int i = 0; i < 10; i = i + 1) {" +
                "x = i + 1;" +
                "}" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("i0 <- 0\n");
        expectedResult.append("repeat\n");
        expectedResult.append("begin\n");
        //TODO check necessity of phi(x0,x2)
        expectedResult.append("x1 <- phi(x0,x2)\n");
        expectedResult.append("i1 <- phi(i0,i2)\n");
        expectedResult.append("x2 <- i1 plus 1\n");
        expectedResult.append("i2 <- i1 plus 1\n");
        expectedResult.append("end\n");
        expectedResult.append("until(i0 less 10)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test()
    public void testWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (x < 2) {" +
                "x = x - 1;" +
                "}" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("repeat\n");
        expectedResult.append("begin\n");
        expectedResult.append("x1 <- phi(x0,x2)\n");
        expectedResult.append("x2 <- x1 minus 1\n");
        expectedResult.append("end\n");
        expectedResult.append("until(x0 less 2)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testArrayAccess() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = a[i];" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x1 <- Access(a,i)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testArrayUpdate() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "a[i] = x;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("Update(a,i) <- x0\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testArrayAsArgument() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int[] x){" +
                "int []y = x;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("y0 <- x0\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testArrayCreation() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "int []y = new int[2];" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("y0 <- new int[2]\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

}
