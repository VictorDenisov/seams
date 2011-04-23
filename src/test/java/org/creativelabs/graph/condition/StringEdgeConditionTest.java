package org.creativelabs.graph.condition;

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
        assertEquals("", new EmptyCondition().getStringRepresentation());
    }

    @Test
    public void testAndWithEmptyCondition() throws Exception {
        assertEquals("(a)", new StringCondition("a").and(new EmptyCondition()).getStringRepresentation());
    }

    @Test
    public void testAnd() throws Exception {
        assertEquals("((a)&&(b))", new StringCondition("a").and(new StringCondition("b")).getStringRepresentation());
    }

    @Test
    public void testOrWithEmptyCondition() throws Exception {
        assertEquals("(a)", new StringCondition("a").or(new EmptyCondition()).getStringRepresentation());
    }

    @Test
    public void testOr() throws Exception {
        assertEquals("((a)||(b))", new StringCondition("a").or(new StringCondition("b")).getStringRepresentation());
    }

    @Test
    public void testNot() throws Exception {
        assertEquals("(!(a))", new StringCondition("a").not().getStringRepresentation());
    }
}
