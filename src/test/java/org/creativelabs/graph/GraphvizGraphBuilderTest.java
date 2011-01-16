package org.creativelabs.graph;

import org.testng.annotations.Test;

import java.util.*;
import java.io.*;

import static org.testng.AssertJUnit.*;

public class GraphvizGraphBuilderTest {
    @Test
    public void testBuildGraph() {
        StringWriter stringWriter = new StringWriter();
        GraphvizGraphBuilder graphBuilder = new GraphvizGraphBuilder(new PrintWriter(stringWriter));

        Vertex a = graphBuilder.addVertex("a");
        Vertex b = graphBuilder.addVertex("b");
        graphBuilder.addEdge(a, b);
        graphBuilder.finalizeGraph();

        assertEquals("digraph G {\n    a -> b;\n}\n", stringWriter.toString());
    }
}
