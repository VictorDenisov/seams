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
    public void testSimpleAssignVariable() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "x = x;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod#x#0[(false) | (true)] -> x#0[(false) | (true)], " +
                "mainMethod#x#1[(false) | (true)] -> mainMethod#x#0[(false) | (true)], }";

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
    public void testIf1() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "if (x < 2) {" +
                    "int y = x;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod#x#0[(false) | (true)] -> x#0[(false) | (true)], " +
                "mainMethod#y#0[(false) | (true)] -> mainMethod#y#0[(false) | (true)], }";

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
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, null);

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        //TODO !!!!
        String expectedResult = "{mainMethod#x#0[(false) | (true)] -> x#0[(false) | (true)], " +
                "mainMethod#y#0[(true) | (false)] -> mainMethod#y#0[(true) | (false)], " +
                "mainMethod#y#1[(false) | (true)] -> mainMethod#x#0[(false) | (true)], " +
                "mainMethod#y#2[ | ] -> mainMethod#1[ | ], }";

        assertEquals(expectedResult, builder.toString());
    }
}
