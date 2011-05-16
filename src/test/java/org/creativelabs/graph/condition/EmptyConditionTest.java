package org.creativelabs.graph.condition;

import org.testng.annotations.Test;

import static org.junit.Assert.*;

/**
 * @author azotcsit
 *         Date: 15.05.11
 *         Time: 15:44
 */
public class EmptyConditionTest {

    @Test
    public void testConstructor() throws Exception {
        EmptyCondition condition = new EmptyCondition();
        assertNotNull(condition);
    }

    @Test
    public void testGetStringRepresentation() throws Exception {
        EmptyCondition condition = new EmptyCondition();
        assertEquals("", condition.getStringRepresentation());
    }

    @Test
    public void testAnd() throws Exception {
        EmptyCondition condition = new EmptyCondition();
        EmptyCondition newCondition = new EmptyCondition();

        Condition andCondition = condition.and(newCondition);
        assertSame(newCondition, andCondition);
    }

    @Test
    public void testOr() throws Exception {
        EmptyCondition condition = new EmptyCondition();
        EmptyCondition newCondition = new EmptyCondition();

        Condition orCondition = condition.or(newCondition);
        assertSame(newCondition, orCondition);
    }

    @Test
    public void testNot() throws Exception {
        EmptyCondition condition = new EmptyCondition();

        Condition notCondition = condition.not();
        assertSame(condition, notCondition);
    }

    @Test
    public void testCopy() throws Exception {
        EmptyCondition condition = new EmptyCondition();
        Condition copyCondition = condition.copy();

        assertNotSame(condition, copyCondition);
        assertEquals(condition.getStringRepresentation(), copyCondition.getStringRepresentation());
    }

}
