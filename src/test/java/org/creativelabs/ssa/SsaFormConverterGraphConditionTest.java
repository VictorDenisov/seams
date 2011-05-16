package org.creativelabs.ssa;

import japa.parser.ast.body.MethodDeclaration;
import org.creativelabs.ParseHelper;
import org.creativelabs.graph.GraphBuilder;
import org.creativelabs.graph.ToStringGraphBuilder;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author azotcsit
 *         Date: 02.05.11
 *         Time: 9:58
 */
public class SsaFormConverterGraphConditionTest {

    @Test
    public void testSimpleDeclareVariableByConstant() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(){" +
                "int x = 0;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod()#x#0[true | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByArgument() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int y){" +
                "int x = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#x#0[false | true] -> mainMethod(int, )#y#0[false | true], " +
                "mainMethod(int, )#y#0[false | true] -> y#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByVariable() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(){" +
                "int y = 1;" +
                "int x = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod()#x#0[true | false] -> mainMethod()#y#0[true | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleAssignVariableByConstant() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "x = 1;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#x#0[false | true] -> x#0[false | true], " +
                "mainMethod(int, )#x#1[true | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleAssignVariableByVariable() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x, int y){" +
                "x = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, int, )#x#0[false | true] -> x#0[false | true], " +
                "mainMethod(int, int, )#x#1[false | true] -> mainMethod(int, int, )#y#0[false | true], " +
                "mainMethod(int, int, )#y#0[false | true] -> y#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleAssignMethod() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = 2;" +
                "x = method(y);" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        //TODO !!!!
        String expectedResult = "{mainMethod#x#0[(false) | (true)] -> x#0[(false) | (true)], " +
                "mainMethod#x#1[ | ] -> mainMethod#method(y#0)[ | ], " +
                "mainMethod#y#0[(true) | (false)] -> mainMethod#y#0[(true) | (false)], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByBinary1() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = x + 2;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#x#0[false | true] -> x#0[false | true], " +
                "mainMethod(int, )#y#0[false | true] -> mainMethod(int, )#x#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByBinary2() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x, int z){" +
                "int y = x + z;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, int, )#x#0[false | true] -> x#0[false | true], " +
                "mainMethod(int, int, )#y#0[false | true] -> mainMethod(int, int, )#x#0[false | true], " +
                "mainMethod(int, int, )#y#0[false | true] -> mainMethod(int, int, )#z#0[false | true], " +
                "mainMethod(int, int, )#z#0[false | true] -> z#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByBinary3() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(){" +
                "int y = 1 + 2;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod()#y#0[true | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testIf1() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "if (x < 2) {" +
                    "int y = x;" +
                "}" +
                "int z = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testIf2() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = 0;" +
                "if (x < 2) {" +
                "    y = x;" +
                "} else {" +
                "    y = 1;" +
                "}" +
                "int z = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testComplexInnerIf() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = 0;" +
                "if (x < 2) {" +
                    "if (x < 2) {" +
                    "    y = x;" +
                    "} else {" +
                    "    int z = 1;" +
                    "}" +
                "    y = 2;" +
                "} else {" +
                "    y = 1;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "";

        assertEquals(expectedResult, builder.toString());
    }
}
