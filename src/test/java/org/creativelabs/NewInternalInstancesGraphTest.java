package org.creativelabs;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class NewInternalInstancesGraphTest {

    private String internalInstancesToString(NewInternalInstancesGraph graph) {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        graph.buildGraph(gb);
        return gb.toString();
    }

    @Test
    public void testConstructor() {
        NewInternalInstancesGraph graph = new NewInternalInstancesGraph();
    }

    @Test
    public void testAdd() {
        NewInternalInstancesGraph graph = new NewInternalInstancesGraph();
        graph.add("source", "target");
        String resultValue = internalInstancesToString(graph);
        assertEquals("{source -> target, }", resultValue);
    }

    @Test
    public void testContains() {
        NewInternalInstancesGraph graph = new NewInternalInstancesGraph();
        graph.add("source", "target");
        assertTrue(graph.contains("source"));
    }

    @Test
    public void testToSet() {
        NewInternalInstancesGraph graph = new NewInternalInstancesGraph();
        graph.add("source", "target");
        ArrayList<String> res = new ArrayList<String> (graph.toSet());

        assertEqualsList(Arrays.asList(new String[]{"source"}), res);
    }

    @Test
    public void testBuildGraph() {
        NewInternalInstancesGraph graph = new NewInternalInstancesGraph();
        graph.add("source", "target");

        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        graph.buildGraph(gb);
        assertEquals("{source -> target, }", gb.toString());
    }
}
