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

    @Test
    public void testAnd() throws Exception {
        EmptyCondition thisCondition = new EmptyCondition();
        EmptyCondition otherCondition = new EmptyCondition();

        assertEquals(thisCondition.and(otherCondition), otherCondition);
    }

    @Test
    public void testOr() throws Exception {
        EmptyCondition thisCondition = new EmptyCondition();
        EmptyCondition otherCondition = new EmptyCondition();

        assertEquals(thisCondition.or(otherCondition), otherCondition);
    }

    @Test
    public void testNot() throws Exception {
        EmptyCondition thisCondition = new EmptyCondition();

        assertEquals(thisCondition.not(), thisCondition);
    }
}
