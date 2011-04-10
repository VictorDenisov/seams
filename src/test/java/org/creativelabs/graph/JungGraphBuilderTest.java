package org.creativelabs.graph;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;

import org.creativelabs.graph.edge.condition.EmptyEdgeCondition;
import org.creativelabs.graph.edge.condition.StringEdgeCondition;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class JungGraphBuilderTest {

    @Test
    public void testConstruction() {
        new JungGraphBuilder();
    }

    @Test
    public void testAddVertex() {
        JungGraphBuilder builder = new JungGraphBuilder();
        Vertex v = builder.addVertex("label");
        assertEquals("label", v.getLabel());
    }

    @Test
    public void testToStringReturnsLabel() {
        JungGraphBuilder builder = new JungGraphBuilder();
        Vertex v = builder.addVertex("label");
        assertEquals("label", v.toString());
    }

    @Test
    public void testAddEdgeWithEmptyCondition() {
        JungGraphBuilder builder = new JungGraphBuilder();
        Vertex a = builder.addVertex("a");
        Vertex b = builder.addVertex("b");
        builder.addEdge(a, b, new EmptyEdgeCondition());
        Graph graph = builder.getGraph();
        assertEquals(2, graph.getVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getEdges(EdgeType.DIRECTED).size());
    }

    @Test
    public void testAddEdgeWithStringCondition() {
        JungGraphBuilder builder = new JungGraphBuilder();
        Vertex a = builder.addVertex("a");
        Vertex b = builder.addVertex("b");
        builder.addEdge(a, b, new StringEdgeCondition("someLabel"));
        Graph graph = builder.getGraph();
        assertEquals(2, graph.getVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getEdges(EdgeType.DIRECTED).size());
        assertEquals("(someLabel)", graph.getEdges(EdgeType.DIRECTED).toArray()[0]);
    }

}
