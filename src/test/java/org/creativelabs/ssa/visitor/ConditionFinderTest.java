package org.creativelabs.ssa.visitor;

import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.StringCondition;
import org.creativelabs.iig.ConditionInternalInstancesGraph;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.holder.ScopeVariablesHolder;
import org.creativelabs.ssa.holder.SimpleMethodArgsHolder;
import org.creativelabs.ssa.holder.SimpleMethodModifiersHolder;
import org.creativelabs.ssa.holder.SimplePhiNodesHolder;
import org.creativelabs.typefinder.ParseHelper;
import org.testng.annotations.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * @author azotcsit
 *         Date: 08.05.11
 *         Time: 12:22
 */
public class ConditionFinderTest {
    @Test
    public void testVisitBinaryExpr() throws Exception {
        BinaryExpr expr = (BinaryExpr) ((AssignExpr) ParseHelper.createExpression("temp = a + 2;")).getValue();
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addVertexConditions("methodName#a", new StringCondition("inCondition"), new StringCondition("exCondition"));

        Condition[] conditions = new ConditionFinder(graph, "methodName", "className", new SimpleMethodModifiersHolder(), new ScopeVariablesHolder(), new SimpleMethodArgsHolder(), new SimplePhiNodesHolder()).visit(expr, new HashSet<String>());

        assertEquals("((inCondition)&&true)", conditions[0].getStringRepresentation());
        assertEquals("((exCondition)||false)", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitNameExpr() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("a");
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addVertexConditions("methodName#a", new StringCondition("inCondition"), new StringCondition("exCondition"));

        Condition[] conditions = new ConditionFinder(graph, "methodName", "className", new SimpleMethodModifiersHolder(), new ScopeVariablesHolder(), new SimpleMethodArgsHolder(), new SimplePhiNodesHolder()).visit(expr, new HashSet<String>());

        assertEquals("(inCondition)", conditions[0].getStringRepresentation());
        assertEquals("(exCondition)", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitUnknownNameExpr() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("a");
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();

        Condition[] conditions = new ConditionFinder(graph, "methodName", "className", new SimpleMethodModifiersHolder(), new ScopeVariablesHolder(), new SimpleMethodArgsHolder(), new SimplePhiNodesHolder()).visit(expr, new HashSet<String>());

        assertEquals("true", conditions[0].getStringRepresentation());
        assertEquals("false", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitMethodCallExpr1() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("method();");
        expr.setModifier(2);
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();

        Condition[] conditions = new ConditionFinder(graph, "methodName", "className", new SimpleMethodModifiersHolder(), new ScopeVariablesHolder(), new SimpleMethodArgsHolder(), new SimplePhiNodesHolder()).visit(expr, new HashSet<String>());

        assertEquals("true", conditions[0].getStringRepresentation());
        assertEquals("false", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitMethodCallExpr2() throws Exception {
        MethodCallExpr expr = (MethodCallExpr) ParseHelper.createExpression("method();");
        expr.setModifier(0);
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();

        Condition[] conditions = new ConditionFinder(graph, "methodName", "className", new SimpleMethodModifiersHolder(), new ScopeVariablesHolder(), new SimpleMethodArgsHolder(), new SimplePhiNodesHolder()).visit(expr, new HashSet<String>());

        assertEquals("false", conditions[0].getStringRepresentation());
        assertEquals("true", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitFieldExpr() throws Exception {
        //TODO: to create tests
    }

    //TODO: to create other tests


}
