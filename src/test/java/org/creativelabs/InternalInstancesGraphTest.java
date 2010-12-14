package org.creativelabs;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class InternalInstancesGraphTest {

    private String internalInstancesToString(InternalInstancesGraph graph) {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        graph.buildGraph(gb);
        return gb.toString();
    }

    @Test
    public void testConstructor() {
        InternalInstancesGraph graph = new InternalInstancesGraph();
    }

    @Test
    public void testAdd() {
        InternalInstancesGraph graph = new InternalInstancesGraph();
        graph.add("source", "target");
        String resultValue = internalInstancesToString(graph);
        assertEquals("{source -> target, }", resultValue);
    }

    @Test
    public void testContains() {
        InternalInstancesGraph graph = new InternalInstancesGraph();
        graph.add("source", "target");
        assertTrue(graph.contains("source"));
    }

    @Test
    public void testToSet() {
        InternalInstancesGraph graph = new InternalInstancesGraph();
        graph.add("source", "target");
        ArrayList<String> res = new ArrayList<String> (graph.toSet());

        assertEqualsList(Arrays.asList(new String[]{"source"}), res);
    }

    @Test
    public void testBuildGraph() {
        InternalInstancesGraph graph = new InternalInstancesGraph();
        graph.add("source", "target");

        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        graph.buildGraph(gb);
        assertEquals("{source -> target, }", gb.toString());
    }
}
