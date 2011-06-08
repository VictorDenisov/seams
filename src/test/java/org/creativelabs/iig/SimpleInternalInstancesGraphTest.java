package org.creativelabs.iig;

import org.creativelabs.typefinder.AssertHelper;
import org.creativelabs.graph.ToStringGraphBuilder;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.creativelabs.graph.condition.VertexConditions.EMPTY_CONDITIONS_STRING;

public class SimpleInternalInstancesGraphTest {

    private String internalInstancesToString(InternalInstancesGraph graph) {
        ToStringGraphBuilder graphBuilder = new ToStringGraphBuilder();
        graph.buildGraph(graphBuilder);
        return graphBuilder.toString();
    }

    @Test
    public void testConstructor() {
        InternalInstancesGraph graph = new SimpleInternalInstancesGraph();
    }

    @Test
    public void testAdd() {
        InternalInstancesGraph graph = new SimpleInternalInstancesGraph();
        graph.addEdge("source", "target");
        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source" + EMPTY_CONDITIONS_STRING + " -> target" + EMPTY_CONDITIONS_STRING + ", }",
                resultValue);
    }

    @Test
    public void testAddMultipleOutgoingVertexes() {
        InternalInstancesGraph graph = new SimpleInternalInstancesGraph();
        graph.addEdge("source", "target1");
        graph.addEdge("source", "target2");
        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source" + EMPTY_CONDITIONS_STRING + " -> target1" + EMPTY_CONDITIONS_STRING +
                ", source" + EMPTY_CONDITIONS_STRING + " -> target2" + EMPTY_CONDITIONS_STRING + ", }",
                resultValue);
    }

    @Test
    public void testContains() {
        InternalInstancesGraph graph = new SimpleInternalInstancesGraph();
        graph.addEdge("source", "target");
        AssertJUnit.assertTrue(graph.contains("source"));
    }

    @Test
    public void testToSet() {
        InternalInstancesGraph graph = new SimpleInternalInstancesGraph();
        graph.addEdge("source", "target");
        ArrayList<String> res = new ArrayList<String> (graph.toSet());

        AssertHelper.assertEqualsList(Arrays.asList(new String[]{"source"}), res);
    }

    @Test
    public void testBuildGraph() {
        InternalInstancesGraph graph = new SimpleInternalInstancesGraph();
        graph.addEdge("source", "target");

        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source" + EMPTY_CONDITIONS_STRING + " -> target" + EMPTY_CONDITIONS_STRING + ", }",
                resultValue);
    }
}
