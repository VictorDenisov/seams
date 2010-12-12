package org.creativelabs.ui.jung;

import org.creativelabs.Dependency;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

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
        Vertex firstVertex = new Vertex("label");

        assertEquals("label", firstVertex.toString());
    }
    
    @Test
    public void testTwoInstancesAreDifferent() throws Exception {
        Vertex one = new Vertex("label");
        Vertex two = new Vertex("label");
        assertFalse(one.equals(two));
    }
}
