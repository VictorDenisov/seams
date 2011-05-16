package org.creativelabs.ssa;

import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.NameExpr;
import org.creativelabs.ParseHelper;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.StringCondition;
import org.creativelabs.iig.ConditionInternalInstancesGraph;
import org.creativelabs.iig.InternalInstancesGraph;
import org.testng.annotations.Test;

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

        Condition[] conditions = new ConditionFinder(graph, "methodName").visit(expr, null);

        assertEquals("((inCondition)||true)", conditions[0].getStringRepresentation());
        assertEquals("((exCondition)||false)", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitNameExpr() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("a");
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addVertexConditions("methodName#a", new StringCondition("inCondition"), new StringCondition("exCondition"));

        Condition[] conditions = new ConditionFinder(graph, "methodName").visit(expr, null);

        assertEquals("(inCondition)", conditions[0].getStringRepresentation());
        assertEquals("(exCondition)", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitUnknownNameExpr() throws Exception {
        NameExpr expr = (NameExpr) ParseHelper.createExpression("a");
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();

        Condition[] conditions = new ConditionFinder(graph, "methodName").visit(expr, null);

        assertEquals("true", conditions[0].getStringRepresentation());
        assertEquals("false", conditions[1].getStringRepresentation());
    }

    @Test
    public void testVisitMethodCallExpr() throws Exception {
        //TODO^ to create tests
    }
}