package org.creativelabs.graph;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.creativelabs.graph.condition.EmptyCondition;
import org.testng.annotations.Test;

import static org.creativelabs.graph.condition.VertexConditions.EMPTY_CONDITIONS_STRING;
import static org.testng.AssertJUnit.assertEquals;

public class JungGraphBuilderTest {

    @Test
    public void testConstruction() {
        new JungGraphBuilder();
    }

    @Test
    public void testAddVertex() {
        JungGraphBuilder builder = new JungGraphBuilder();
        Vertex v = builder.addVertex("label", new EmptyCondition(), new EmptyCondition());
        assertEquals("label" + EMPTY_CONDITIONS_STRING, v.getLabel());
    }

    @Test
    public void testAddEdge() {
        JungGraphBuilder builder = new JungGraphBuilder();
        Vertex a = builder.addVertex("a", new EmptyCondition(), new EmptyCondition());
        Vertex b = builder.addVertex("b", new EmptyCondition(), new EmptyCondition());
        builder.addEdge(a, b);
        Graph graph = builder.getGraph();
        assertEquals(2, graph.getVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getEdges(EdgeType.DIRECTED).size());
    }

    @Test
    public void testAddVertexAddEdge() {
        JungGraphBuilder builder = new JungGraphBuilder();
        Vertex a = builder.addVertex("a", new EmptyCondition(), new EmptyCondition());
        Vertex b = builder.addVertex("b", new EmptyCondition(), new EmptyCondition());
        Vertex c = builder.addVertex("c", new EmptyCondition(), new EmptyCondition());
        builder.addEdge(a, b);
        Graph graph = builder.getGraph();
        assertEquals(3, graph.getVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getEdges(EdgeType.DIRECTED).size());
    }

}
