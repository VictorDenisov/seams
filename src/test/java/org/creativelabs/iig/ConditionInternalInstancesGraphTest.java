package org.creativelabs.iig;

import org.creativelabs.AssertHelper;
import org.creativelabs.graph.ToStringGraphBuilder;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
        graph.add("source", "target");
        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source -> target, }", resultValue);
    }

    @Test
    public void testAddMultipleOutgoingVertexes() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.add("source", "target1");
        graph.add("source", "target2");
        String resultValue = internalInstancesToString(graph);
        AssertJUnit.assertEquals("{source -> target1, source -> target2, }", resultValue);
    }

    @Test
    public void testContains() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.add("source", "target");
        AssertJUnit.assertTrue(graph.contains("source"));
    }

    @Test
    public void testToSet() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.add("source", "target");
        ArrayList<String> res = new ArrayList<String> (graph.toSet());

        AssertHelper.assertEqualsList(Arrays.asList(new String[]{"source"}), res);
    }

    @Test
    public void testBuildGraph() {
        InternalInstancesGraph graph = new ConditionInternalInstancesGraph();
        graph.add("source", "target");

        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        graph.buildGraph(gb);
        AssertJUnit.assertEquals("{source -> target, }", gb.toString());
    }
}
