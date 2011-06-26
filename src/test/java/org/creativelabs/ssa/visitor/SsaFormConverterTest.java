package org.creativelabs.ssa.visitor;

import japa.parser.ast.body.MethodDeclaration;
import org.creativelabs.Constants;
import org.creativelabs.ssa.holder.*;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.creativelabs.ssa.holder.variable.Variable;
import org.creativelabs.typefinder.ParseHelper;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.testng.Assert.assertEquals;

public class SsaFormConverterTest {

    private SimpleMultiHolder createVariablesHolder(Map<Variable, Integer> map) {
        SimpleConditionHolder conditionHolder = new SimpleConditionHolder();
        SimpleClassFieldsHolder fieldsHolder = new SimpleClassFieldsHolder();
        SimpleMethodArgsHolder methodArgsHolder = new SimpleMethodArgsHolder();
        ScopeVariablesHolder variablesHolder = new ScopeVariablesHolder(map);
        SimpleMethodModifiersHolder modifiersHolder = new SimpleMethodModifiersHolder();
        return new SimpleMultiHolder(
                conditionHolder,
                fieldsHolder,
                methodArgsHolder,
                variablesHolder,
                modifiersHolder);
    }

    @Test
    public void testArgumentsOfMethod() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "x = 1;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void method(int x#0, int ar#0[]) {\n" +
                        "    ar#1 = Update(ar#0, 2, x#0);\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testArrayWitVariableIndexAssignExpr() throws Exception {
        MethodDeclaration methodDeclaration =
                ParseHelper.createMethodDeclaration("void method(int x, int ar[], int i){" +
                        "ar[i] = x;" +
                        "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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

        MethodDeclaration methodDeclaration =
                ParseHelper.createMethodDeclaration("public void visit(AssignExpr n, Object o) {\n" +
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
                new HashMap<Variable, Integer>() {
                    {
                        put(new StringVariable("internalInstances", ""), 0);
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void outData(Map<String, Collection<Dependency>> deps#0, String fileName#0) {\n" +
                        "    try {\n" +
                        "        File file#0 = new File(fileName#0 + \".deps\");\n" +
                        "        if (file#0.createNewFile() || MainApp.NEED_TO_REWRITE_OLD_REPORT#0) {\n" +
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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

    @Test
    public void testForStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "for (int i = 0; i < 10; i = i + 1) {" +
                "x = i + 1;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    x#1 = #phi(x#0, x#3);\n" +
                "    i#1 = #phi(i#0, i#3);\n" +
                "    for (int i#0 = 0; i#1 < 10; i#3 = i#2 + 1) {\n" +
                "        x#2 = #phi(x#1, x#3);\n" +
                "        i#2 = #phi(i#1, i#3);\n" +
                "        x#3 = i#2 + 1;\n" +
                "    }\n" +
                "    x#4 = #phi(x#1, x#3);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
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

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(int x#0) {\n" +
                "    x#1 = #phi(x#0, x#7);\n" +
                "    i#1 = #phi(i#0, i#3);\n" +
                "    for (int i#0 = 0; i#1 < 10; i#3 = i#2 + 1) {\n" +
                "        x#2 = #phi(x#1, x#7);\n" +
                "        i#2 = #phi(i#1, i#3);\n" +
                "        x#3 = i#2 + 1;\n" +
                "        x#4 = #phi(x#3, x#6);\n" +
                "        j#1 = #phi(j#0, j#3);\n" +
                "        for (int j#0 = 1; j#1 < 5; j#3 = j#2 + 1) {\n" +
                "            x#5 = #phi(x#4, x#6);\n" +
                "            j#2 = #phi(j#1, j#3);\n" +
                "            x#6 = j#2 + 2;\n" +
                "        }\n" +
                "        x#7 = #phi(x#4, x#6);\n" +
                "    }\n" +
                "    x#8 = #phi(x#1, x#7);\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test()
    public void testWhileStmt() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (x < 2) {" +
                "x = x - 1;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
                new HashMap<Variable, Integer>() {
                    {
                        put(new StringVariable("a", Constants.THIS_SCOPE), 2);
                    }

                    {
                        put(new StringVariable("i", Constants.THIS_SCOPE), 1);
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "void method(Set<ComplexClass> set#0) {\n" +
                "    for (ComplexClass cc#0 = set#0.#next() : set#0) {\n" +
                "        for (String value#0 = cc#0.values#0.#next() : cc#0.values#0) {\n" +
                "            System.out#0.println(value#0);\n" +
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));
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
                new HashMap<Variable, Integer>() {
                    {
                        put(new StringVariable("internalInstances", ""), 0);
                    }

                    {
                        put(new StringVariable("assignedInternalInstance", ""), 0);
                    }

                    {
                        put(new StringVariable("value", ""), 0);
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
                new HashMap<Variable, Integer>() {
                    {
                        put(new StringVariable("internalInstances", ""), 0);
                    }

                    {
                        put(new StringVariable("assignedInternalInstance", ""), 0);
                    }

                    {
                        put(new StringVariable("value", ""), 0);
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
    public void testArrayDimensionsExpr() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("ClassType processTypeArguments(ClassOrInterfaceType classType) {\n" +
                "        ClassType[] args = new ClassType[classType.getTypeArgs().size()];\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "ClassType processTypeArguments(ClassOrInterfaceType classType#0) {\n" +
                        "    ClassType[] args#0 = new ClassType[classType#0.getTypeArgs().size()];\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testStaticMethodsProcessing() throws Exception {

        MethodDeclaration methodDeclaration =
                ParseHelper.createMethodDeclaration(
                        "public static <T> void visitComment(Comment comment, T arg, VoidVisitorAdapter<T> visitor) {\n" +
                                "        if (comment instanceof LineComment) {\n" +
                                "            visitor.visit((LineComment) comment, arg);\n" +
                                "        } else if (comment instanceof BlockComment) {\n" +
                                "            visitor.visit((BlockComment) comment, arg);\n" +
                                "        } else if (comment instanceof JavadocComment) {\n" +
                                "            visitor.visit((JavadocComment) comment, arg);\n" +
                                "        } else {\n" +
                                "            throw new UnsupportedOperationException(\"VoidVisitorHelper is not support comment for \" +\n" +
                                "                    comment.getClass() + \".\");\n" +
                                "        }\n" +
                                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "public static <T> void visitComment(Comment comment#0, T arg#0, VoidVisitorAdapter<T> visitor#0) {\n" +
                        "    if (comment#0 instanceof LineComment) {\n" +
                        "        visitor#0.visit((LineComment) comment#0, arg#0);\n" +
                        "    } else if (comment#0 instanceof BlockComment) {\n" +
                        "        visitor#0.visit((BlockComment) comment#0, arg#0);\n" +
                        "    } else if (comment#0 instanceof JavadocComment) {\n" +
                        "        visitor#0.visit((JavadocComment) comment#0, arg#0);\n" +
                        "    } else {\n" +
                        "        throw new UnsupportedOperationException(\"VoidVisitorHelper is not support comment for \" + comment.getClass() + \".\");\n" +
                        "    }\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testMethod1() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("public ClassType addArrayDepth(ClassType classType) {\n" +
                "        try {\n" +
                "            ClassTypeImpl previous = (ClassTypeImpl) classType;\n" +
                "            ClassTypeImpl result = new ClassTypeImpl();\n" +
                "            String className = previous.toString();\n" +
                "            if (!previous.clazz.isArray()) {\n" +
                "                className = takeArrayName(previous.clazz);\n" +
                "            }\n" +
                "            className = \"[\" + className;\n" +
                "            result.clazz = Class.forName(className);\n" +
                "            return result;\n" +
                "        } catch (Exception e) {\n" +
                "            return createErrorClassType(e.toString());\n" +
                "        }\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "public ClassType addArrayDepth(ClassType classType#0) {\n" +
                "    try {\n" +
                "        ClassTypeImpl previous#0 = (ClassTypeImpl) classType#0;\n" +
                "        ClassTypeImpl result#0 = new ClassTypeImpl();\n" +
                "        String className#0 = previous#0.toString();\n" +
                "        if (!previous#0.clazz#0.isArray()) {\n" +
                "            className#1 = takeArrayName(previous#0.clazz#0);\n" +
                "        }\n" +
                "        className#2 = #phi(className#0, className#1);\n" +
                "        className#3 = \"[\" + className#2;\n" +
                "        result#1.clazz#0 = Class.forName(className#3);\n" +
                "        return result#1;\n" +
                "    } catch (Exception e#0) {\n" +
                "        return createErrorClassType(e#0.toString());\n" +
                "    }\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    //TODO
    @Test
    public void testMethod3() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("public Expression visit(VariableDeclarationExpr n, MultiHolder arg) {\n" +
                "        List<VariableDeclarator> vars = n.getVars();\n" +
                "        for (VariableDeclarator variableDeclarator : vars) {\n" +
                "            String variableName = variableDeclarator.getId().getName();\n" +
                "            arg.write(createVariable(variableName, null, arg.getMethodArgsHolder()), 0);\n" +
                "            if (variableDeclarator.getInit() != null) {\n" +
                "                variableDeclarator.setInit(GenericVisitorHelper.visitExpression(variableDeclarator.getInit(), arg, this));\n" +
                "            }\n" +
                "            variableDeclarator.getId().setName(variableName + Constants.SEPARATOR + 0);\n" +
                "\n" +
                "            String target = methodName + Constants.SEPARATOR + variableName;\n" +
                "\n" +
                "            Condition[] conditions;\n" +
                "            if (variableDeclarator.getInit() != null) {\n" +
                "                Set<String> valuesNames = new TreeSet<String>();\n" +
                "                conditions = GenericVisitorHelper.visitExpression(\n" +
                "                        variableDeclarator.getInit(),\n" +
                "                        valuesNames,\n" +
                "                        new ConditionFinder(graph, methodName, className, arg.getModifiersHolder(), arg.getVariablesHolder(), arg.getMethodArgsHolder(), arg.getPhiNodesHolder()));\n" +
                "                for (String name : valuesNames) {\n" +
                "                    graph.addEdge(target + Constants.SEPARATOR + 0, methodName + Constants.SEPARATOR + name);\n" +
                "\n" +
                "                }\n" +
                "            } else {\n" +
                "                conditions = new Condition[]{new TrueBooleanCondition(), new FalseBooleanCondition()};\n" +
                "            }\n" +
                "\n" +
                "            if (conditions == null) {\n" +
                "                throw new IllegalStateException(variableDeclarator.getInit().getClass() + \" is not supported by ConditionFinder.\");\n" +
                "            }\n" +
                "\n" +
                "            graph.addVertexConditions(target + Constants.SEPARATOR + 0,\n" +
                "                    conditions[0].and(arg.getBasicBlockCondition()),\n" +
                "                    conditions[1].and(arg.getBasicBlockCondition()));\n" +
                "        }\n" +
                "\n" +
                "        n.setVars(vars);\n" +
                "        return n;\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        SimpleMultiHolder holder = createVariablesHolder(new HashMap<Variable, Integer>());
        holder.addFieldName("methodName");
        holder.addFieldName("graph");
        holder.addFieldName("className");
        visitor.visit(methodDeclaration, holder);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "public Expression visit(VariableDeclarationExpr n#0, MultiHolder arg#0) {\n" +
                "    List<VariableDeclarator> vars#0 = n#0.getVars();\n" +
                "    for (VariableDeclarator variableDeclarator#0 = vars#0.#next() : vars#0) {\n" +
                "        conditions#0 = #phi(conditions#3);\n" +
                "        String variableName#0 = variableDeclarator#0.getId().getName();\n" +
                "        arg#0.write(createVariable(variableName#0, null, arg#0.getMethodArgsHolder()), 0);\n" +
                "        if (variableDeclarator#0.getInit() != null) {\n" +
                "            variableDeclarator#0.setInit(GenericVisitorHelper.visitExpression(variableDeclarator#0.getInit(), arg#0, this));\n" +
                "        }\n" +
                "        variableDeclarator#0.getId().setName(variableName#0 + Constants.SEPARATOR#0 + 0);\n" +
                "        String target#0 = this.methodName#0 + Constants.SEPARATOR#0 + variableName#0;\n" +
                "        Condition[] conditions#0;\n" +
                "        if (variableDeclarator#0.getInit() != null) {\n" +
                "            Set<String> valuesNames#0 = new TreeSet<String>();\n" +
                "            conditions#1 = GenericVisitorHelper.visitExpression(variableDeclarator#0.getInit(), valuesNames#0, new ConditionFinder(this.graph#0, this.methodName#0, this.className#0, arg#0.getModifiersHolder(), arg#0.getVariablesHolder(), arg#0.getMethodArgsHolder(), arg#0.getPhiNodesHolder()));\n" +
                "            for (String name#0 = valuesNames#0.#next() : valuesNames#0) {\n" +
                "                this.graph#0.addEdge(target#0 + Constants.SEPARATOR#0 + 0, this.methodName#0 + Constants.SEPARATOR#0 + name#0);\n" +
                "            }\n" +
                "        } else {\n" +
                "            conditions#2 = new Condition[] { new TrueBooleanCondition(), new FalseBooleanCondition() };\n" +
                "        }\n" +
                "        conditions#3 = #phi(conditions#1, conditions#2);\n" +
                "        if (conditions#3 == null) {\n" +
                "            throw new IllegalStateException(variableDeclarator.getInit().getClass() + \" is not supported by ConditionFinder.\");\n" +
                "        }\n" +
                "        this.graph#0.addVertexConditions(target#0 + Constants.SEPARATOR#0 + 0, #Access(conditions#3, 0).and(arg#0.getBasicBlockCondition()), #Access(conditions#3, 1).and(arg#0.getBasicBlockCondition()));\n" +
                "    }\n" +
                "    n#0.setVars(vars#0);\n" +
                "    return n#0;\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testMethod2() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("public ClassType convertToArray(ClassType classType, int dimension) {\n" +
                "        if (dimension == 0) {\n" +
                "            return classType;\n" +
                "        }\n" +
                "        try {\n" +
                "            ClassTypeImpl classTypeImpl = (ClassTypeImpl) classType;\n" +
                "            String className = takeArrayName(classTypeImpl.clazz);\n" +
                "            for (int i = 0; i < dimension; ++i) {\n" +
                "                className = \"[\" + className;\n" +
                "            }\n" +
                "            ClassTypeImpl result = new ClassTypeImpl();\n" +
                "            result.clazz = Class.forName(className);\n" +
                "            return result;\n" +
                "        } catch (Exception e) {\n" +
                "            return createErrorClassType(e.toString());\n" +
                "        }\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<Variable, Integer>() {
                    {
                        put(new StringVariable("classType", Constants.ARG_SCOPE), 0);
                    }

                    {
                        put(new StringVariable("dimension", Constants.ARG_SCOPE), 0);
                    }
                }));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "public ClassType convertToArray(ClassType classType#0, int dimension#0) {\n" +
                "    if (dimension#0 == 0) {\n" +
                "        return classType#0;\n" +
                "    }\n" +
                "    try {\n" +
                "        ClassTypeImpl classTypeImpl#0 = (ClassTypeImpl) classType#0;\n" +
                "        String className#0 = takeArrayName(classTypeImpl#0.clazz#0);\n" +
                "        for (int i#0 = 0; i#0 < dimension#0; ++i#1) {\n" +
                "            className#1 = \"[\" + className#0;\n" +
                "        }\n" +
                "        ClassTypeImpl result#0 = new ClassTypeImpl();\n" +
                "        result#1.clazz#0 = Class.forName(className#0);\n" +
                "        return result#1;\n" +
                "    } catch (Exception e#0) {\n" +
                "        return createErrorClassType(e#0.toString());\n" +
                "    }\n" +
                "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testForStmt3() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void buildGraph(GraphBuilder graphBuilder) {\n" +
                "        for (int i = 0; i < fromVertexes.size(); i++) {\n" +
                "            Vertex a = map.get(fromVertexes.get(i));\n" +
                "            Vertex b = map.get(toVertexes.get(i));\n" +
                "            graphBuilder.addEdge(a, b);\n" +
                "        }\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        SimpleMultiHolder holder = createVariablesHolder(
                new HashMap<Variable, Integer>() {
                    {
                        put(new StringVariable("fromVertexes", Constants.ARG_SCOPE), 0);
                    }

                    {
                        put(new StringVariable("toVertexes", Constants.ARG_SCOPE), 0);
                    }

                    {
                        put(new StringVariable("map", Constants.ARG_SCOPE), 0);
                    }
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

    @Test
    public void testInnerField() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void setDependencies(Map<String, Collection<Dependency>> dependencies) {\n" +
                "        ClassData cd = new ClassData();\n" +
                "        cd.dependencies = dependencies;\n" +
                "    }");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(
                new HashMap<Variable, Integer>()));
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult =
                "void setDependencies(Map<String, Collection<Dependency>> dependencies#0) {\n" +
                        "    ClassData cd#0 = new ClassData();\n" +
                        "    cd#0.dependencies = dependencies#0;\n" +
                        "}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testMethod4() throws Exception {

        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("public void actionInternal(ActionCode actionCode, Object param) {\n" +
                "\n" +
                "        if (actionCode == ActionCode.CLOSE) {\n" +
                "            // Close\n" +
                "            // End the processing of the current request, and stop any further\n" +
                "            // transactions with the client\n" +
                "\n" +
                "            try {\n" +
                "                outputBuffer.endRequest();\n" +
                "            } catch (IOException e) {\n" +
                "                // Set error flag\n" +
                "                error = true;\n" +
                "            }\n" +
                "\n" +
                "        } else if (actionCode == ActionCode.REQ_SSL_ATTRIBUTE ) {\n" +
                "\n" +
                "            try {\n" +
                "                if (sslSupport != null) {\n" +
                "                    Object sslO = sslSupport.getCipherSuite();\n" +
                "                    if (sslO != null)\n" +
                "                        request.setAttribute\n" +
                "                            (SSLSupport.CIPHER_SUITE_KEY, sslO);\n" +
                "                    sslO = sslSupport.getPeerCertificateChain(false);\n" +
                "                    if (sslO != null)\n" +
                "                        request.setAttribute\n" +
                "                            (SSLSupport.CERTIFICATE_KEY, sslO);\n" +
                "                    sslO = sslSupport.getKeySize();\n" +
                "                    if (sslO != null)\n" +
                "                        request.setAttribute\n" +
                "                            (SSLSupport.KEY_SIZE_KEY, sslO);\n" +
                "                    sslO = sslSupport.getSessionId();\n" +
                "                    if (sslO != null)\n" +
                "                        request.setAttribute\n" +
                "                            (SSLSupport.SESSION_ID_KEY, sslO);\n" +
                "                    request.setAttribute(SSLSupport.SESSION_MGR, sslSupport);\n" +
                "                }\n" +
                "            } catch (Exception e) {\n" +
                "                log.warn(sm.getString(\"http11processor.socket.ssl\"), e);\n" +
                "            }\n" +
                "\n" +
                "        } else if (actionCode == ActionCode.REQ_HOST_ADDR_ATTRIBUTE) {\n" +
                "\n" +
                "            if ((remoteAddr == null) && (socket != null)) {\n" +
                "                InetAddress inetAddr = socket.getSocket().getInetAddress();\n" +
                "                if (inetAddr != null) {\n" +
                "                    remoteAddr = inetAddr.getHostAddress();\n" +
                "                }\n" +
                "            }\n" +
                "            request.remoteAddr().setString(remoteAddr);\n" +
                "\n" +
                "        } else if (actionCode == ActionCode.REQ_LOCAL_NAME_ATTRIBUTE) {\n" +
                "\n" +
                "            if ((localName == null) && (socket != null)) {\n" +
                "                InetAddress inetAddr = socket.getSocket().getLocalAddress();\n" +
                "                if (inetAddr != null) {\n" +
                "                    localName = inetAddr.getHostName();\n" +
                "                }\n" +
                "            }\n" +
                "            request.localName().setString(localName);\n" +
                "\n" +
                "        } else if (actionCode == ActionCode.REQ_HOST_ATTRIBUTE) {\n" +
                "\n" +
                "            if ((remoteHost == null) && (socket != null)) {\n" +
                "                InetAddress inetAddr = socket.getSocket().getInetAddress();\n" +
                "                if (inetAddr != null) {\n" +
                "                    remoteHost = inetAddr.getHostName();\n" +
                "                }\n" +
                "                if(remoteHost == null) {\n" +
                "                    if(remoteAddr != null) {\n" +
                "                        remoteHost = remoteAddr;\n" +
                "                    } else { // all we can do is punt\n" +
                "                        request.remoteHost().recycle();\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "            request.remoteHost().setString(remoteHost);\n" +
                "\n" +
                "        } else if (actionCode == ActionCode.REQ_LOCAL_ADDR_ATTRIBUTE) {\n" +
                "\n" +
                "            if (localAddr == null)\n" +
                "               localAddr = socket.getSocket().getLocalAddress().getHostAddress();\n" +
                "\n" +
                "            request.localAddr().setString(localAddr);\n" +
                "\n" +
                "        }}");

        SsaFormConverter visitor = new SsaFormConverter();
        SimpleMultiHolder holder = createVariablesHolder(new HashMap<Variable, Integer>() {
            {
                put(new StringVariable("actionCode", Constants.ARG_SCOPE), 0);
            }

            {
                put(new StringVariable("param", Constants.ARG_SCOPE), 0);
            }

        });
        holder.addFieldName("remoteAddr");
        holder.addFieldName("log");
        holder.addFieldName("sslSupport");
        holder.addFieldName("outputBuffer");
        holder.addFieldName("error");
        visitor.visit(methodDeclaration, holder);
        String actualResult = visitor.getMethodDeclaration().toString();

        String expectedResult = "";

        assertEquals(expectedResult, actualResult);
    }

}
