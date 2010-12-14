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
        JungVertex firstVertex = new JungVertex("label");

        assertEquals("label", firstVertex.toString());
    }
    
    @Test
    public void testTwoInstancesAreDifferent() throws Exception {
        JungVertex one = new JungVertex("label");
        JungVertex two = new JungVertex("label");
        assertFalse(one.equals(two));
    }
}
