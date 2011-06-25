package org.creativelabs.ssa.visitor;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import org.creativelabs.graph.GraphBuilder;
import org.creativelabs.graph.ToStringGraphBuilder;
import org.creativelabs.ssa.holder.*;
import org.creativelabs.ssa.holder.variable.Variable;
import org.creativelabs.typefinder.ParseHelper;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.testng.Assert.assertEquals;

/**
 * @author azotcsit
 *         Date: 02.05.11
 *         Time: 9:58
 */
public class SsaFormConverterGraphConditionTest {

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
    public void testSimpleDeclareVariableByConstant() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(){" +
                "int x = 0;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#x#0[false | true] -> mainMethod(int, )#y#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByVariable() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(){" +
                "int y = 1;" +
                "int x = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#x#0[false | true], " +
                "mainMethod(int, )#x#1[true | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleAssignVariableByVariable() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x, int y){" +
                "x = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, int, )#x#1[false | true] -> mainMethod(int, int, )#y#0[false | true], " +
                "mainMethod(int, int, )#x#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleAssignMethod() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = 2;" +
                "x = method(y);" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        //TODO
        String expectedResult = "{mainMethod(int, )#x#0[false | true], " +
                "mainMethod(int, )#x#1[true | false] -> method[ | ], " +
                "mainMethod(int, )#y#0[true | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByBinary1() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = x + 2;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#y#0[false | true] -> mainMethod(int, )#x#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByBinary4() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = 2 + x;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#y#0[false | true] -> mainMethod(int, )#x#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByBinary2() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x, int z){" +
                "int y = x + z;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, int, )#y#0[false | true] -> mainMethod(int, int, )#x#0[false | true], " +
                "mainMethod(int, int, )#y#0[false | true] -> mainMethod(int, int, )#z#0[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testSimpleDeclareVariableByBinary3() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(){" +
                "int y = 1 + 2;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod()#y#0[true | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test
    public void testIf1() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void mainMethod(int x){" +
                "int y = 1;" +
                "if (x < 2) {" +
                    "y = x;" +
                "}" +
                "int z = y;" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#y#1[false | (x#0 < 2)] -> mainMethod(int, )#x#0[false | true], " +
                "mainMethod(int, )#y#2[true | (x#0 < 2)] -> mainMethod(int, )#y#0[true | false], " +
                "mainMethod(int, )#y#2[true | (x#0 < 2)] -> mainMethod(int, )#y#1[false | (x#0 < 2)], " +
                "mainMethod(int, )#z#0[true | (x#0 < 2)] -> mainMethod(int, )#y#2[true | (x#0 < 2)], }";

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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#y#1[false | (x#0 < 2)] -> mainMethod(int, )#x#0[false | true], " +
                "mainMethod(int, )#y#3[(!(x#0 < 2)) | ((x#0 < 2)||false)] -> mainMethod(int, )#y#1[false | (x#0 < 2)], " +
                "mainMethod(int, )#y#3[(!(x#0 < 2)) | ((x#0 < 2)||false)] -> mainMethod(int, )#y#2[(!(x#0 < 2)) | false], " +
                "mainMethod(int, )#z#0[(!(x#0 < 2)) | ((x#0 < 2)||false)] -> mainMethod(int, )#y#3[(!(x#0 < 2)) | ((x#0 < 2)||false)], " +
                "mainMethod(int, )#y#0[true | false], }";

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
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "{mainMethod(int, )#y#1[false | ((x#0 < 2)&&(x#0 < 2))] -> mainMethod(int, )#x#0[false | true], " +
                "mainMethod(int, )#y#0[true | false], " +
                "mainMethod(int, )#y#3[(x#0 < 2) | false], " +
                "mainMethod(int, )#y#4[(!(x#0 < 2)) | false], " +
                "mainMethod(int, )#z#0[((x#0 < 2)&&(!(x#0 < 2))) | false], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test()
    public void testWhile() throws Exception {
        MethodDeclaration methodDeclaration = ParseHelper.createMethodDeclaration("void method(int x){" +
                "while (x < 2) {" +
                "x = x - 1;" +
                "}" +
                "}");

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

//        String expectedResult = "{method(int, )#x#0[false | true] -> x#0[false | true], " +
//                "method(int, )#x#1[false | true] -> method(int, )#x#0[false | true], " +
//                "method(int, )#x#2[false | true] -> method(int, )#x#1[false | true], " +
//                "method(int, )#x#3[false | (x < 2)] -> x#2[ | ], }";

        //TODO add second to phi
        String expectedResult = "{method(int, )#x#1[false | true] -> method(int, )#x#0[false | true], " +
                "method(int, )#x#2[false | true] -> method(int, )#x#1[false | true], " +
                "method(int, )#x#3[false | (x#1 < 2)] -> method(int, )#x#2[false | true], }";

        assertEquals(expectedResult, builder.toString());
    }

    @Test()
    public void testFieldProcessing() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "A fieldA;"
                + "public void method() {"
                + "A a = fieldA;"
                + "}"
                + "}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);
        MethodDeclaration methodDeclaration = (MethodDeclaration) cd.getMembers().get(1);

        SsaFormConverter visitor = new SsaFormConverter();
        visitor.visit(methodDeclaration, createVariablesHolder(new TreeMap<Variable, Integer>()));

        GraphBuilder builder = new ToStringGraphBuilder();
        visitor.getGraph().buildGraph(builder);

        String expectedResult = "";

        assertEquals(expectedResult, builder.toString());
    }
}
