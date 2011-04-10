package org.creativelabs.graph;

import org.creativelabs.graph.edge.condition.EmptyEdgeCondition;
import org.creativelabs.graph.edge.condition.StringEdgeCondition;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.testng.AssertJUnit.assertEquals;

public class GraphvizGraphBuilderTest {
    @Test
    public void testAddEdgeWithEmptyCondition() {
        StringWriter stringWriter = new StringWriter();
        GraphvizGraphBuilder graphBuilder = new GraphvizGraphBuilder(new PrintWriter(stringWriter));

        Vertex a = graphBuilder.addVertex("a");
        Vertex b = graphBuilder.addVertex("b");
        graphBuilder.addEdge(a, b, new EmptyEdgeCondition());
        graphBuilder.finalizeGraph();

        assertEquals("digraph G {\n    size=\"60,30\"\n    ratio=fill\n    a -> b;\n}\n", stringWriter.toString());
    }

    @Test
    public void testAddEdgeWithStringCondition() {
        StringWriter stringWriter = new StringWriter();
        GraphvizGraphBuilder graphBuilder = new GraphvizGraphBuilder(new PrintWriter(stringWriter));

        Vertex a = graphBuilder.addVertex("a");
        Vertex b = graphBuilder.addVertex("b");
        graphBuilder.addEdge(a, b, new StringEdgeCondition("someLabel"));
        graphBuilder.finalizeGraph();

        assertEquals("digraph G {\n    size=\"60,30\"\n    ratio=fill\n    a -> b [label = \"(someLabel)\"];\n}\n", stringWriter.toString());
    }
}
