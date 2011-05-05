package org.creativelabs.graph;

import org.creativelabs.graph.condition.EmptyCondition;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.creativelabs.graph.condition.VertexConditions.EMPTY_CONDITIONS_STRING;
import static org.testng.AssertJUnit.assertEquals;

public class GraphvizGraphBuilderTest {
    @Test
    public void testAddEdge() {
        StringWriter stringWriter = new StringWriter();
        GraphvizGraphBuilder graphBuilder = new GraphvizGraphBuilder(new PrintWriter(stringWriter));

        Vertex a = graphBuilder.addVertex("a", new EmptyCondition(), new EmptyCondition());
        Vertex b = graphBuilder.addVertex("b", new EmptyCondition(), new EmptyCondition());
        graphBuilder.addEdge(a, b);
        graphBuilder.finalizeGraph();

        assertEquals("digraph G {\n    size=\"120,60\"\n    ratio=fill\n    \"a" + EMPTY_CONDITIONS_STRING +
                "\" -> \"b" + EMPTY_CONDITIONS_STRING + "\";\n}\n",
                stringWriter.toString());
    }

    @Test
    public void testAddVertexAddEdge() {
        StringWriter stringWriter = new StringWriter();
        GraphvizGraphBuilder graphBuilder = new GraphvizGraphBuilder(new PrintWriter(stringWriter));

        Vertex a = graphBuilder.addVertex("a", new EmptyCondition(), new EmptyCondition());
        Vertex b = graphBuilder.addVertex("b", new EmptyCondition(), new EmptyCondition());
        Vertex c = graphBuilder.addVertex("c", new EmptyCondition(), new EmptyCondition());
        graphBuilder.addEdge(a, b);
        graphBuilder.finalizeGraph();

        assertEquals("digraph G {\n    size=\"120,60\"\n    ratio=fill\n    \"a" + EMPTY_CONDITIONS_STRING +
                "\" -> \"b" + EMPTY_CONDITIONS_STRING + "\";\n    \"c" + EMPTY_CONDITIONS_STRING + "\";\n}\n",
                stringWriter.toString());
    }

}
