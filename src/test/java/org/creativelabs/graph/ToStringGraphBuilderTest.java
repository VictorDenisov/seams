package org.creativelabs.graph;

import org.testng.annotations.Test;
import org.testng.annotations.Configuration;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class ToStringGraphBuilderTest {

    @Test
    public void testAddVertex() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex vert = gb.addVertex("label");

        assertEquals("label", vert.getLabel());
    }

    @Test
    public void testAddEdge() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex a = gb.addVertex("a");
        Vertex b = gb.addVertex("b");

        gb.addEdge(a, b);

        assertEquals("{a -> b, }", gb.toString());
    }

    @Test
    public void testSeveralEdgesFromA() {
        ToStringGraphBuilder gb = new ToStringGraphBuilder();
        Vertex a = gb.addVertex("a");
        Vertex b = gb.addVertex("b");
        Vertex c = gb.addVertex("c");

        gb.addEdge(a, b);
        gb.addEdge(a, c);

        assertEquals("{a -> b, a -> c, }", gb.toString());
    }
}
