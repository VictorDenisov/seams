package org.creativelabs.ssa.visitor;

import japa.parser.ast.body.MethodDeclaration;
import org.creativelabs.ssa.holder.*;
import org.creativelabs.typefinder.ParseHelper;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SsaFormConverterTest {

    private SimpleMultiHolder createVariablesHolder(Map<String, Integer> map){
        SimpleConditionHolder conditionHolder = new SimpleConditionHolder();
        SimpleClassFieldsHolder fieldsHolder = new SimpleClassFieldsHolder();
        SimpleVariablesHolder variablesHolder = new SimpleVariablesHolder(map);
        SimpleMethodModifiersHolder modifiersHolder = new SimpleMethodModifiersHolder();
        return new SimpleMultiHolder(conditionHolder, fieldsHolder, variablesHolder, modifiersHolder);
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
                        "    ar#1 = Update(ar#0, 2, x#0);\n" +
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
                        "    ar#1 = Update(ar#0, i#0, x#0);\n" +
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
    public void testSmartIfStmt2() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("public void visit(AssignExpr n, Object o) {\n" +
                "        if (n.getValue() != null) {\n" +
                "            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);\n" +
                "            n.getValue().accept(esv, null);\n" +
                "            if (esv.isAssignedInternalInstance()) {\n" +
                "                internalInstances.addEdge(n.getTarget().toString(), esv.getValue());\n" +
                "            }\n" +
                "        }\n" +
                "        super.visit(n, o);\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        SimpleMultiHolder holder = createVariablesHolder(
                new HashMap<String, Integer>() {
                    {
                        put("internalInstances", 0);
                    }
                });
        holder.addFieldName("internalInstances");
        visitor.visit(methodDeclaration, holder);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "public void visit(AssignExpr n#0, Object o#0) {\n" +
                        "    if (n#0.getValue() != null) {\n" +
                        "        ExpressionSeparatorVisitor esv#0 = new ExpressionSeparatorVisitor(this.internalInstances#0);\n" +
                        "        n#0.getValue().accept(esv#0, null);\n" +
                        "        if (esv#0.isAssignedInternalInstance()) {\n" +
                        "            this.internalInstances#0.addEdge(n#0.getTarget().toString(), esv#0.getValue());\n" +
                        "        }\n" +
                        "    }\n" +
                        "    super.visit(n#0, o#0);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testIfStmtInTry() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void outData(Map<String, Collection<Dependency>> deps, String fileName) {\n" +
                "        try {\n" +
                "            File file = new File(fileName + \".deps\");\n" +
                "            if (file.createNewFile() || MainApp.NEED_TO_REWRITE_OLD_REPORT) {\n" +
                "                PrintWriter writer = new PrintWriter(file);\n" +
                "                printDeps(deps, writer);\n" +
                "                writer.flush();\n" +
                "                writer.close();\n" +
                "            }\n" +
                "        } catch (IOException e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void outData(Map<String, Collection<Dependency>> deps#0, String fileName#0) {\n" +
                        "    try {\n" +
                        "        File file#0 = new File(fileName#0 + \".deps\");\n" +
                        "        if (file#0.createNewFile() || MainApp.NEED_TO_REWRITE_OLD_REPORT) {\n" +
                        "            PrintWriter writer#0 = new PrintWriter(file#0);\n" +
                        "            printDeps(deps#0, writer#0);\n" +
                        "            writer#0.flush();\n" +
                        "            writer#0.close();\n" +
                        "        }\n" +
                        "    } catch (IOException e#0) {\n" +
                        "        e#0.printStackTrace();\n" +
                        "    }\n" +
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
    public void testWhileStmt2() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x, boolean b){" +
                "while (b) {" +
                "x = x - 1;" +
                "}" +
                "}");

        //TODO
        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0, boolean b#0) {\n" +
                "    x#1 = #phi(x#0, x#3);\n" +
                "    while (b#0) {\n" +
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
        SimpleMultiHolder holder = createVariablesHolder(
                new HashMap<String, Integer>() {
                    {
                        put("a", 2);
                    }

                    {
                        put("i", 1);
                    }
                });
        visitor.visit(methodDeclaration, holder);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    x#1 = #Access(a#2, i#1);\n" +
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
        visitor.visit(methodDeclaration, new SimpleMultiHolder());
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
        visitor.visit(methodDeclaration, new SimpleMultiHolder());
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
    public void testInnerForeachStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(Set<ComplexClass> set){" +
                "for (ComplexClass cc : set) {" +
                "    for (String value : cc.values) {" +
                    "    System.out.println(value);" +
                "    }" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(Set<ComplexClass> set#0) {\n" +
                "    for (ComplexClass cc#0 = set#0.#next() : set#0) {\n" +
                "        for (String value#0 = cc#0.values.#next() : cc#0.values) {\n" +
                "            System.out.println(value#0);\n" +
                "        }\n" +
                "    }\n" +
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

    @Test
    public void testThisStmtInFields1() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void visit(MethodCallExpr n, Object o) {\n" +
                "        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();\n" +
                "        scopeDetector.visit(n, o);\n" +
                "        String name = scopeDetector.getName();\n" +
                "        if (internalInstances.contains(name)) {\n" +
                "            assignedInternalInstance = true;\n" +
                "            value = name;\n" +
                "        }\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        SimpleMultiHolder holder = createVariablesHolder(
                new HashMap<String, Integer>() {
                    {
                        put("internalInstances", 0);
                    }

                    {
                        put("assignedInternalInstance", 0);
                    }

                    {
                        put("value", 0);
                    }
                });
        holder.addFieldName("internalInstances");
        holder.addFieldName("assignedInternalInstance");
        holder.addFieldName("value");
        visitor.visit(methodDeclaration, holder);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void visit(MethodCallExpr n#0, Object o#0) {\n" +
                        "    ScopeDetectorVisitor scopeDetector#0 = new ScopeDetectorVisitor();\n" +
                        "    scopeDetector#0.visit(n#0, o#0);\n" +
                        "    String name#0 = scopeDetector#0.getName();\n" +
                        "    if (this.internalInstances#0.contains(name#0)) {\n" +
                        "        this.assignedInternalInstance#1 = true;\n" +
                        "        this.value#1 = name#0;\n" +
                        "    }\n" +
                        "    this.assignedInternalInstance#2 = #phi(this.assignedInternalInstance#0, this.assignedInternalInstance#1);\n" +
                        "    this.value#2 = #phi(this.value#0, this.value#1);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testThisStmtInFields2() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void visit(MethodCallExpr n, Object o) {\n" +
                "        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();\n" +
                "        scopeDetector.visit(n, o);\n" +
                "        String name = scopeDetector.getName();\n" +
                "        if (this.internalInstances.contains(name)) {\n" +
                "            this.assignedInternalInstance = true;\n" +
                "            this.value = name;\n" +
                "        }\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        SimpleMultiHolder holder = createVariablesHolder(
                new HashMap<String, Integer>() {
                    {put("internalInstances", 0);}
                    {put("assignedInternalInstance", 0);}
                    {put("value", 0);}
                });
        holder.addFieldName("internalInstances");
        holder.addFieldName("assignedInternalInstance");
        holder.addFieldName("value");
        visitor.visit(methodDeclaration, holder);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void visit(MethodCallExpr n#0, Object o#0) {\n" +
                        "    ScopeDetectorVisitor scopeDetector#0 = new ScopeDetectorVisitor();\n" +
                        "    scopeDetector#0.visit(n#0, o#0);\n" +
                        "    String name#0 = scopeDetector#0.getName();\n" +
                        "    if (this.internalInstances#0.contains(name#0)) {\n" +
                        "        this.assignedInternalInstance#1 = true;\n" +
                        "        this.value#1 = name#0;\n" +
                        "    }\n" +
                        "    this.assignedInternalInstance#2 = #phi(this.assignedInternalInstance#0, this.assignedInternalInstance#1);\n" +
                        "    this.value#2 = #phi(this.value#0, this.value#1);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayDimensionsExpr() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("ClassType processTypeArguments(ClassOrInterfaceType classType) {\n" +
                "        ClassType[] args = new ClassType[classType.getTypeArgs().size()];\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<String, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "ClassType processTypeArguments(ClassOrInterfaceType classType#0) {\n" +
                "    ClassType[] args#0 = new ClassType[classType#0.getTypeArgs().size()];\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    //TODO
    @Test
    public void testForStmt() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void buildGraph(GraphBuilder graphBuilder) {\n" +
                "        for (int i = 0; i < fromVertexes.size(); i++) {\n" +
                "            Vertex a = map.get(fromVertexes.get(i));\n" +
                "            Vertex b = map.get(toVertexes.get(i));\n" +
                "            graphBuilder.addEdge(a, b);\n" +
                "        }\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        SimpleMultiHolder holder = createVariablesHolder(
                new HashMap<String, Integer>() {
                    {put("fromVertexes", 0);}
                    {put("toVertexes", 0);}
                    {put("map", 0);}
                });
        holder.addFieldName("fromVertexes");
        holder.addFieldName("toVertexes");
        holder.addFieldName("map");
        visitor.visit(methodDeclaration, holder);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void buildGraph(GraphBuilder graphBuilder#0) {\n" +
                        "    for (int i#0 = 0; i#0 < this.fromVertexes#0.size(); i#1++) {\n" +
                        "        Vertex a#0 = this.map#0.get(this.fromVertexes#0.get(i#1));\n" +
                        "        Vertex b#0 = this.map#0.get(this.toVertexes#0.get(i#1));\n" +
                        "        graphBuilder#0.addEdge(a#0, b#0);\n" +
                        "    }\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    //TODO
    @Test
    public void testInnerField() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void setDependencies(Map<String, Collection<Dependency>> dependencies) {\n" +
                "        ClassData cd = new ClassData();\n" +
                "        cd.dependencies = dependencies;\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<String, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void setDependencies(Map<String, Collection<Dependency>> dependencies#0) {\n" +
                        "    ClassData cd#0 = new ClassData();\n" +
                        "    cd#0.dependencies = dependencies#0;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

}
