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
        expectedResult.append("y3 <- phi(y1,y2)\n");
        expectedResult.append("x3 <- phi(x1,x2)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testIfStmtWithoutElse() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "if (x > y) {" +
                    "x = 1;" +
                    "y = x + y;" +
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
        expectedResult.append("y2 <- phi(y0,y1)\n");
        expectedResult.append("x2 <- phi(x0,x1)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testOneNestedIfStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "if (x > y) {" +
                    "x = 1;" +
                    "if (1 > x) {" +
                        "y = x + y;" +
                    "}" +
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
            expectedResult.append("if 1 greater x1\n");
            expectedResult.append("then do\n");
                expectedResult.append("y1 <- x1 plus y0\n");
                expectedResult.append("end\n");
            expectedResult.append("y2 <- phi(y0,y1)\n");
        expectedResult.append("end\n");
        expectedResult.append("else do\n");
            expectedResult.append("x2 <- y0\n");
            expectedResult.append("y3 <- x2 plus 2\n");
        expectedResult.append("end\n");
        expectedResult.append("y4 <- phi(y2,y3)\n");
        expectedResult.append("x3 <- phi(x1,x2)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

@Test
    public void testManyNestedIfStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "if (x > y) {" +
                    "x = 1;" +
                    "if (1 > x) {" +
                        "y = x + y;" +
                    "} else {" +
                        "if (1 > x) {" +
                            "y = 2;" +
                        "}" +
                    "}" +
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
            expectedResult.append("if 1 greater x1\n");
            expectedResult.append("then do\n");
                expectedResult.append("y1 <- x1 plus y0\n");
            expectedResult.append("end\n");
            expectedResult.append("else do\n");
                expectedResult.append("if 1 greater x1\n");
                expectedResult.append("then do\n");
                    expectedResult.append("y2 <- 2\n");
                expectedResult.append("end\n");
                expectedResult.append("y3 <- phi(y1,y2)\n");
            expectedResult.append("end\n");
            expectedResult.append("y4 <- phi(y1,y3)\n");
        expectedResult.append("end\n");
        expectedResult.append("else do\n");
            expectedResult.append("x2 <- y0\n");
            expectedResult.append("y5 <- x2 plus 2\n");
        expectedResult.append("end\n");
        expectedResult.append("y6 <- phi(y4,y5)\n");
        expectedResult.append("x3 <- phi(x1,x2)\n");

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
        expectedResult.append("x1 <- phi(x0,x2)\n");
        expectedResult.append("i1 <- phi(i0,i2)\n");
        expectedResult.append("x2 <- i1 plus 1\n");
        expectedResult.append("i2 <- i1 plus 1\n");
        expectedResult.append("end\n");
        expectedResult.append("until(i0 less 10)\n");
        expectedResult.append("x3 <- phi(x0,x2)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testNestedForStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "for (int i = 0; i < 10; i = i + 1) {" +
                    "x = i + 1;" +
                    "for (int j = 1; j < 5; j = j + 1) {" +
                        "x = j + 2;" +
                    "}" +
                "}" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("i0 <- 0\n");
        expectedResult.append("repeat\n");
        expectedResult.append("begin\n");
            expectedResult.append("x1 <- phi(x0,x5)\n");
            expectedResult.append("i1 <- phi(i0,i2)\n");
            expectedResult.append("x2 <- i1 plus 1\n");
            expectedResult.append("j0 <- 1\n");
            expectedResult.append("repeat\n");
            expectedResult.append("begin\n");
            expectedResult.append("j1 <- phi(j0,j2)\n");
            expectedResult.append("x3 <- phi(x2,x4)\n");
            expectedResult.append("x4 <- j1 plus 2\n");
            expectedResult.append("j2 <- j1 plus 1\n");
            expectedResult.append("end\n");
            expectedResult.append("until(j0 less 5)\n");
            expectedResult.append("x5 <- phi(x2,x4)\n");
            expectedResult.append("i2 <- i1 plus 1\n");
        expectedResult.append("end\n");
        expectedResult.append("until(i0 less 10)\n");
        expectedResult.append("x6 <- phi(x0,x5)\n");

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
        expectedResult.append("x3 <- phi(x0,x2)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test()
    public void testOneNestedWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (x < 2) {" +
                    "x = x - 1;" +
                    "int y = x;" +
                    "while (y < 2) {" +
                        "x = x - y;" +
                    "}" +
                    "}" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("repeat\n");
        expectedResult.append("begin\n");
            expectedResult.append("x1 <- phi(x0,x5)\n");
            expectedResult.append("x2 <- x1 minus 1\n");
            expectedResult.append("y0 <- x2\n");
            expectedResult.append("repeat\n");
            expectedResult.append("begin\n");
                expectedResult.append("x3 <- phi(x2,x4)\n");
                expectedResult.append("x4 <- x3 minus y0\n");
            expectedResult.append("end\n");
            expectedResult.append("until(y0 less 2)\n");
            expectedResult.append("x5 <- phi(x2,x4)\n");
        expectedResult.append("end\n");
        expectedResult.append("until(x0 less 2)\n");
        expectedResult.append("x6 <- phi(x0,x5)\n");

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

    @Test
    public void testObjectCreation() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "A x = new A();" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("x0 <- new A()\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testObjectCreationWithParameters() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "A y = new A(x);" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("y0 <- new A(x0)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testAssignExprMethodExecution() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "int y = method2();" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("y0 <- method2()\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testAssignExprMethodExecutionWithParameter() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "int y = method2(x);" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("y0 <- method2(x0)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testVoidMethodExecution() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "method2();" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("method2()\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testVoidMethodExecutionWithParameters() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "method2(x);" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("method2(x0)\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testReturnStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("int method(int x){" +
                "return x;" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("return x0\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testTryStmtWithoutFinallyBlock() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("int method(int x){" +
                "try {" +
                    "x = 2;" +
                "} catch (Exception e) {" +
                    "e.printStackTrace();" +
                "}" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("try\n");
        expectedResult.append("begin\n");
            expectedResult.append("x1 <- 2\n");
        expectedResult.append("end\n");
        expectedResult.append("catch Exception\n");
        expectedResult.append("begin\n");
            expectedResult.append("printStackTrace()\n");
        expectedResult.append("end\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

    @Test
    public void testTryStmtWithFinallyBlock() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("int method(int x){" +
                "try {" +
                    "x = 2;" +
                "} catch (Exception e) {" +
                    "e.printStackTrace();" +
                "} finally {" +
                    "x = 1;" +
                "}" +
                "}");

        SsaFormBuilderVisitor visitor = new SsaFormBuilderVisitor();
        StringBuilder actualResult = visitor.visit(methodDeclaration, null);

        StringBuilder expectedResult = new StringBuilder();
        expectedResult.append("try\n");
        expectedResult.append("begin\n");
            expectedResult.append("x1 <- 2\n");
        expectedResult.append("end\n");
        expectedResult.append("catch Exception\n");
        expectedResult.append("begin\n");
            expectedResult.append("printStackTrace()\n");
        expectedResult.append("end\n");
        expectedResult.append("finally\n");
        expectedResult.append("begin\n");
            expectedResult.append("x2 <- 1\n");
        expectedResult.append("end\n");

        assertEquals(expectedResult.toString(), actualResult.toString());
    }

}
