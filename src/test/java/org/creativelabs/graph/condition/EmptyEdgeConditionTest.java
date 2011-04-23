package org.creativelabs.graph.condition;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author azotcsit
 * Date: 10.04.11
 * Time: 20:19
 */
public class EmptyEdgeConditionTest {
    @Test
    public void testGetStringRepresentation() throws Exception {
        assertEquals("", new EmptyCondition().getStringRepresentation());
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testAnd() throws Exception {
        new EmptyCondition().and(new EmptyCondition());
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testOr() throws Exception {
        new EmptyCondition().and(new EmptyCondition());
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testNot() throws Exception {
        new EmptyCondition().not();
    }
}
