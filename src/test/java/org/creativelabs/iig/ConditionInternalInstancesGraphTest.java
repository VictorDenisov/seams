package org.creativelabs.iig;

import org.creativelabs.typefinder.AssertHelper;
import org.creativelabs.graph.ToStringGraphBuilder;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.creativelabs.graph.condition.VertexConditions.EMPTY_CONDITIONS_STRING;

/**
 * @author azotcsit
 * Date: 10.04.11
 * Time: 23:50
 */
public class ConditionInternalInstancesGraphTest {
    private String internalInstancesToString(InternalInstancesGraph graph) {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        graph.buildGraph(gb);
        return gb.toString();
    }

    @Test
    public void testConstructor() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
    }

    @Test
    public void testAdd() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addEdge("source", "target");
        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source" + EMPTY_CONDITIONS_STRING + " -> target" + EMPTY_CONDITIONS_STRING + ", }",
                resultValue);
    }

    @Test
    public void testAddMultipleOutgoingVertexes() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addEdge("source", "target1");
        graph.addEdge("source", "target2");
        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source" + EMPTY_CONDITIONS_STRING + " -> target1" + EMPTY_CONDITIONS_STRING +
                ", source" + EMPTY_CONDITIONS_STRING + " -> target2" + EMPTY_CONDITIONS_STRING + ", }",
                resultValue);
    }

    @Test
    public void testContains() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addEdge("source", "target");
        AssertJUnit.assertTrue(graph.contains("source"));
    }

    @Test
    public void testToSet() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addEdge("source", "target");
        ArrayList<String> res = new ArrayList<String> (graph.toSet());

        AssertHelper.assertEqualsList(Arrays.asList(new String[]{"source"}), res);
    }

    @Test
    public void testBuildGraph() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.addEdge("source", "target");

        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source" + EMPTY_CONDITIONS_STRING + " -> target" + EMPTY_CONDITIONS_STRING + ", }",
                resultValue);
    }
}
