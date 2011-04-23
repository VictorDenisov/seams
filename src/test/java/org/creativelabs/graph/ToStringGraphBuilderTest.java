package org.creativelabs.graph;

import org.creativelabs.graph.condition.EmptyCondition;
import org.creativelabs.graph.condition.StringCondition;
import org.testng.annotations.Test;

import static org.creativelabs.graph.condition.VertexConditions.EMPTY_CONDITIONS_STRING;
import static org.testng.AssertJUnit.assertEquals;

public class ToStringGraphBuilderTest {

    @Test
    public void testAddVertex() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex vertex = gb.addVertex("label", new EmptyCondition(), new EmptyCondition());

        assertEquals("label" + EMPTY_CONDITIONS_STRING, vertex.getLabel());
    }

    @Test
    public void testAddEdgeWithEmptyCondition() {
        ToStringGraphBuilder graphBuilder = new ToStringGraphBuilder();
        Vertex a = graphBuilder.addVertex("a", new EmptyCondition(), new EmptyCondition());
        Vertex b = graphBuilder.addVertex("b", new EmptyCondition(), new EmptyCondition());

        graphBuilder.addEdge(a, b, new EmptyCondition());

        assertEquals("{a" + EMPTY_CONDITIONS_STRING + " -> b" + EMPTY_CONDITIONS_STRING + ", }",
                graphBuilder.toString());
    }

    @Test
    public void testAddEdgeWithStringCondition() {
        ToStringGraphBuilder graphBuilder = new ToStringGraphBuilder();
        Vertex a = graphBuilder.addVertex("a", new EmptyCondition(), new EmptyCondition());
        Vertex b = graphBuilder.addVertex("b", new EmptyCondition(), new EmptyCondition());

        graphBuilder.addEdge(a, b, new StringCondition("someLabel"));

        assertEquals("{a" + EMPTY_CONDITIONS_STRING + " -> b" + EMPTY_CONDITIONS_STRING +
                " [(someLabel)], }",
                graphBuilder.toString());
    }

    @Test
    public void testSeveralEdgesFromA() {
        ToStringGraphBuilder graphBuilder = new ToStringGraphBuilder();
        Vertex a = graphBuilder.addVertex("a", new EmptyCondition(), new EmptyCondition());
        Vertex b = graphBuilder.addVertex("b", new EmptyCondition(), new EmptyCondition());
        Vertex c = graphBuilder.addVertex("c", new EmptyCondition(), new EmptyCondition());

        graphBuilder.addEdge(a, b, new EmptyCondition());
        graphBuilder.addEdge(a, c, new EmptyCondition());

        assertEquals("{a" + EMPTY_CONDITIONS_STRING + " -> b" + EMPTY_CONDITIONS_STRING +
                ", a" + EMPTY_CONDITIONS_STRING + " -> c" + EMPTY_CONDITIONS_STRING + ", }",
                graphBuilder.toString());
    }
}
