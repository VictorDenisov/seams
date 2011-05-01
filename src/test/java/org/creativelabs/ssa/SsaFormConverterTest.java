package org.creativelabs.ssa;

import japa.parser.ast.body.MethodDeclaration;
import org.creativelabs.ParseHelper;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SsaFormConverterTest {

    private VariablesHolder createVariablesHolder(Map<String, Integer> map){
        return new VariablesHolder(map);
    }

    @Test
    public void testArgumentsOfMethod() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = 1;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0) {\n" +
                        "    x#1 = 1;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCreateNewVariable() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "int x = 1;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method() {\n" +
                        "    int x#0 = 1;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNameExprValueOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = x;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0) {\n" +
                        "    x#1 = x#0;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int ar[]){" +
                "ar[2] = x;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int ar#0[]) {\n" +
                        "    ar#1 = Update(ar#0, 2, x);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayWitVariableIndexAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int ar[], int i){" +
                "ar[i] = x;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int ar#0[], int i#0) {\n" +
                        "    ar#1 = Update(ar#0, i#0, x);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testBinaryExprValueOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = x + 2;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0) {\n" +
                        "    x#1 = x#0 + 2;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testBinaryExprValueWithTwoVariablesOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "int y = 1;" +
                "x = x + y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0) {\n" +
                        "    int y#0 = 1;\n" +
                        "    x#1 = x#0 + y#0;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testBinaryExprValueWithThreeVariablesOfAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "x = x + y + y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int y#0) {\n" +
                        "    x#1 = x#0 + y#0 + y#0;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
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

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int y#0) {\n" +
                        "    if (x#0 > y#0) {\n" +
                        "        x#1 = 1;\n" +
                        "        y#1 = x#1 + y#0;\n" +
                        "    } else {\n" +
                        "        x#2 = y#0;\n" +
                        "        y#2 = x#2 + 2;\n" +
                        "    }\n" +
                        "    x#3 = #phi(x#1, x#2);\n" +
                        "    y#3 = #phi(y#1, y#2);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSmartIfStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "if (x > y) {" +
                "x = 1;" +
                "y = x + y;" +
                "} else {" +
                "x = y;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int y#0) {\n" +
                        "    if (x#0 > y#0) {\n" +
                        "        x#1 = 1;\n" +
                        "        y#1 = x#1 + y#0;\n" +
                        "    } else {\n" +
                        "        x#2 = y#0;\n" +
                        "    }\n" +
                        "    x#3 = #phi(x#1, x#2);\n" +
                        "    y#2 = #phi(y#0, y#1);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testIfStmtWithoutElse() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, int y){" +
                "if (x > y) {" +
                "x = 1;" +
                "y = x + y;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int y#0) {\n" +
                        "    if (x#0 > y#0) {\n" +
                        "        x#1 = 1;\n" +
                        "        y#1 = x#1 + y#0;\n" +
                        "    }\n" +
                        "    x#2 = #phi(x#0, x#1);\n" +
                        "    y#2 = #phi(y#0, y#1);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
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

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int y#0) {\n" +
                        "    if (x#0 > y#0) {\n" +
                        "        x#1 = 1;\n" +
                        "        if (1 > x#1) {\n" +
                        "            y#1 = x#1 + y#0;\n" +
                        "        }\n" +
                        "        y#2 = #phi(y#0, y#1);\n" +
                        "    } else {\n" +
                        "        x#2 = y#0;\n" +
                        "        y#3 = x#2 + 2;\n" +
                        "    }\n" +
                        "    x#3 = #phi(x#1, x#2);\n" +
                        "    y#4 = #phi(y#2, y#3);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
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

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int y#0) {\n" +
                        "    if (x#0 > y#0) {\n" +
                        "        x#1 = 1;\n" +
                        "        if (1 > x#1) {\n" +
                        "            y#1 = x#1 + y#0;\n" +
                        "        } else {\n" +
                        "            if (1 > x#1) {\n" +
                        "                y#2 = 2;\n" +
                        "            }\n" +
                        "            y#3 = #phi(y#0, y#2);\n" +
                        "        }\n" +
                        "        y#4 = #phi(y#1, y#3);\n" +
                        "    } else {\n" +
                        "        x#2 = y#0;\n" +
                        "        y#5 = x#2 + 2;\n" +
                        "    }\n" +
                        "    x#3 = #phi(x#1, x#2);\n" +
                        "    y#6 = #phi(y#4, y#5);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

//    @Test
//    public void testForStmt() throws Exception {
//        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
//                "for (int i = 0; i < 10; i = i + 1) {" +
//                "x = i + 1;" +
//                "}" +
//                "}");
//
//        SsaFormConverter visitor = new SsaFormConverter();
//        visitor.visit(methodDeclaration, null);
//        String actualResult = visitor.getMethodDeclaration().toString();
//
//        String expectedResult = "void method(int x#0) {\n" +
//                "    i#0 = #phi(i#1, i#3);\n" +
//                "    for (int i#1 = 0; i#0 < 10; i#3 = i#2 + 1) {\n" +
//                "        i#2 = #phi(i#1, i#2);\n" +
//                "        x#1 = #phi(x#0, x#2);\n" +
//                "        x#2 = i#1 + 1;\n" +
//                "    }\n" +
//                "    x#3 = #phi(x#0, x#2);\n" +
//                "}";
//
//        assertEquals(expectedResult, actualResult);
//    }

    //    @Test
//    public void testNestedForStmt() throws Exception {
//        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
//                "for (int i = 0; i < 10; i = i + 1) {" +
//                    "x = i + 1;" +
//                    "for (int j = 1; j < 5; j = j + 1) {" +
//                        "x = j + 2;" +
//                    "}" +
//                "}" +
//                "}");
//
//        SsaFormConverter visitor = new SsaFormConverter();
//        StringBuilder actualResult = visitor.visit(methodDeclaration, null);
//
//        StringBuilder expectedResult = new StringBuilder();
//        expectedResult.append("i0 <- 0\n");
//        expectedResult.append("repeat\n");
//        expectedResult.append("begin\n");
//            expectedResult.append("x1 <- #phi(x0,x5)\n");
//            expectedResult.append("i1 <- #phi(i0,i2)\n");
//            expectedResult.append("x2 <- i1 plus 1\n");
//            expectedResult.append("j0 <- 1\n");
//            expectedResult.append("repeat\n");
//            expectedResult.append("begin\n");
//            expectedResult.append("j1 <- #phi(j0,j2)\n");
//            expectedResult.append("x3 <- #phi(x2,x4)\n");
//            expectedResult.append("x4 <- j1 plus 2\n");
//            expectedResult.append("j2 <- j1 plus 1\n");
//            expectedResult.append("end\n");
//            expectedResult.append("until(j0 less 5)\n");
//            expectedResult.append("x5 <- #phi(x2,x4)\n");
//            expectedResult.append("i2 <- i1 plus 1\n");
//        expectedResult.append("end\n");
//        expectedResult.append("until(i0 less 10)\n");
//        expectedResult.append("x6 <- #phi(x0,x5)\n");
//
//        assertEquals(expectedResult.toString(), actualResult.toString());
//    }
//
    @Test()
    public void testWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (x < 2) {" +
                "x = x - 1;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    x#1 = #phi(x#0, x#3);\n" +
                "    while (x#1 < 2) {\n" +
                "        x#2 = #phi(x#1, x#3);\n" +
                "        x#3 = x#2 - 1;\n" +
                "    }\n" +
                "    x#4 = #phi(x#1, x#3);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test()
    public void testEmptyWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (method(x)) {" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    while (method(x#0)) {\n" +
                "    }\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayAccess() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = a[i];" +
                "}");


        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<String, Integer>(){
                    {put("a", 2);}
                    {put("i", 1);}
                }));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    x#1 = Access(a#2, i#1);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayAsArgument() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int[] x){" +
                "int []y = x;" +
                "}");


        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int[] x#0) {\n" +
                "    int[] y#0 = x#0;\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayCreation() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "int []y = new int[2];" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method() {\n" +
                "    int[] y#0 = new int[2];\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayCreation2() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "int []y = new int[] { 2, x };" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    int[] y#0 = new int[] { 2, x#0 };\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testObjectCreation() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "A x = new A();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method() {\n" +
                "    A x#0 = new A();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testObjectCreationWithParameters() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "A y = new A(x);" +
                "}");

         SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    A y#0 = new A(x#0);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testAssignExprMethodExecution() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "int y = method2();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method() {\n" +
                "    int y#0 = method2();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testAssignExprMethodExecutionWithParameter() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "int y = method2(x);" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    int y#0 = method2(x#0);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testVoidMethodExecution() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "method2();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method() {\n" +
                "    method2();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testVoidMethodExecutionWithParameters() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "method2(x);" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    method2(x#0);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testReturnStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("int method(int x){" +
                "return x;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "int method(int x#0) {\n" +
                "    return x#0;\n" +
                "}";

        assertEquals(expectedResult, actualResult);
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

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "int method(int x#0) {\n" +
                "    try {\n" +
                "        x#1 = 2;\n" +
                "    } catch (Exception e#0) {\n" +
                "        e#0.printStackTrace();\n" +
                "    }\n" +
                "}";

        assertEquals(expectedResult, actualResult);
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

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "int method(int x#0) {\n" +
                "    try {\n" +
                "        x#1 = 2;\n" +
                "    } catch (Exception e#0) {\n" +
                "        e#0.printStackTrace();\n" +
                "    } finally {\n" +
                "        x#2 = 1;\n" +
                "    }\n" +
                "}";
        //TODO resolve problem if assignment was in catch block.
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testForeachStmtArray() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int[] ar, int x){" +
                "for (int i : ar) {" +
                    "x = i;" +
                    "i = 2;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int[] ar#0, int x#0) {\n" +
                "    for (int i#0 = ar#0.#next() : ar#0) {\n" +
                "        x#1 = #phi(x#0, x#2);\n" +
                "        x#2 = i#0;\n" +
                "        i#1 = 2;\n" +
                "    }\n" +
                "    x#3 = #phi(x#0, x#2);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testObjectMethodScope() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(A a){" +
                "a.method();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(A a#0) {\n" +
                "    a#0.method();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testThisMethodScope() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "this.method();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method() {\n" +
                "    this.method();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSuperMethodScope() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(){" +
                "super.method();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method() {\n" +
                "    super.method();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testInstanceOfExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(A a){" +
                "if (a instanceof A) {" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(A a#0) {\n" +
                "    if (a#0 instanceof A) {\n" +
                "    }\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testClassCastExprInCondition() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(A a){" +
                "if ((A) a) {" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(A a#0) {\n" +
                "    if ((A) a#0) {\n" +
                "    }\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testClassCastExprInArgument() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(A a){" +
                "method((A) a);" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(A a#0) {\n" +
                "    method((A) a#0);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testClassCastExprInEnclosedExpr() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(A a){" +
                "((A) a).getB();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(A a#0) {\n" +
                "    ((A) a#0).getB();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testEnclosedExprInMethodScope() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(A a){" +
                "(a).getB();" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(A a#0) {\n" +
                "    (a#0).getB();\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test(enabled = false)
    public void testClassExprInMethodScope() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(List<A> a){" +
                "Collections.sort(a);" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(List<A> a#0) {\n" +
                "    Collections.sort(a#0);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

}
