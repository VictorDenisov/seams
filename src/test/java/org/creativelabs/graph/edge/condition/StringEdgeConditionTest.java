package org.creativelabs.graph.edge.condition;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author azotcsit
 * Date: 10.04.11
 * Time: 20:18
 */
public class StringEdgeConditionTest {
    @Test
    public void testGetStringRepresentation() throws Exception {
        assertEquals("", new EmptyEdgeCondition().getStringRepresentation());
    }

    @Test
    public void testAndWithEmptyCondition() throws Exception {
        assertEquals("(a)", new StringEdgeCondition("a").and(new EmptyEdgeCondition()).getStringRepresentation());
    }

    @Test
    public void testAnd() throws Exception {
        assertEquals("((a)&&(b))", new StringEdgeCondition("a").and(new StringEdgeCondition("b")).getStringRepresentation());
    }

    @Test
    public void testOrWithEmptyCondition() throws Exception {
        assertEquals("(a)", new StringEdgeCondition("a").or(new EmptyEdgeCondition()).getStringRepresentation());
    }

    @Test
    public void testOr() throws Exception {
        assertEquals("((a)||(b))", new StringEdgeCondition("a").or(new StringEdgeCondition("b")).getStringRepresentation());
    }

    @Test
    public void testNot() throws Exception {
        assertEquals("(!(a))", new StringEdgeCondition("a").not().getStringRepresentation());
    }
}
