package org.creativelabs.ui.jung;

import org.creativelabs.Dependency;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 07.12.2010
 * Time: 22:28:35
 * To change this template use File | Settings | File Templates.
 */
public class VertexTest {
    @Test
    public void testConstructor() throws Exception {
        Vertex<String> firstVertex = new Vertex<String>("id", "label");

        assertEquals("id", firstVertex.getId());
        assertEquals("label", firstVertex.getLabel());

        Vertex<Integer> secondVertex = new Vertex<Integer>(0, "label");

        assertEquals(new Integer(0), secondVertex.getId());
        assertEquals("label", secondVertex.getLabel());
    }
}
