package org.creativelabs;

import org.creativelabs.ui.jung.JungVertex;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class JungGraphBuilderTest {

    @Test
    public void testConstruction() {
        new JungGraphBuilder();
    }

    @Test
    public void testAddVertex() {
        JungGraphBuilder builder = new JungGraphBuilder();
        JungVertex v = (JungVertex)builder.addVertex("label");
        assertEquals("label", v.getLabel());
    }

    @Test
    public void testAddEdge() {
        JungGraphBuilder builder = new JungGraphBuilder();
        JungVertex a = (JungVertex)builder.addVertex("a");
        JungVertex b = (JungVertex)builder.addVertex("b");
        builder.addEdge(a, b);
        Graph graph = builder.getGraph();
        assertEquals(2, graph.getVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getEdges(EdgeType.DIRECTED).size());
    }

}
