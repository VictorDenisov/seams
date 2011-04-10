package org.creativelabs.graph;

import org.creativelabs.graph.edge.condition.EmptyEdgeCondition;
import org.creativelabs.graph.edge.condition.StringEdgeCondition;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class ToStringGraphBuilderTest {

    @Test
    public void testAddVertex() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex vert = gb.addVertex("label");

        assertEquals("label", vert.getLabel());
    }

    @Test
    public void testAddEdgeWithEmptyCondition() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex a = gb.addVertex("a");
        Vertex b = gb.addVertex("b");

        gb.addEdge(a, b, new EmptyEdgeCondition());

        assertEquals("{a -> b, }", gb.toString());
    }

    @Test
    public void testAddEdgeWithStringCondition() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex a = gb.addVertex("a");
        Vertex b = gb.addVertex("b");

        gb.addEdge(a, b, new StringEdgeCondition("someLabel"));

        assertEquals("{a -> b [(someLabel)], }", gb.toString());
    }

    @Test
    public void testSeveralEdgesFromA() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex a = gb.addVertex("a");
        Vertex b = gb.addVertex("b");
        Vertex c = gb.addVertex("c");

        gb.addEdge(a, b, new EmptyEdgeCondition());
        gb.addEdge(a, c, new EmptyEdgeCondition());

        assertEquals("{a -> b, a -> c, }", gb.toString());
    }
}
