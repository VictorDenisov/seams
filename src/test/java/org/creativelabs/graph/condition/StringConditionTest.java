package org.creativelabs.graph.condition;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 15.05.11
 *         Time: 17:03
 */
public class StringConditionTest {

    @Test
    public void testConstructor() throws Exception {
        StringCondition condition = new StringCondition("P");
        assertNotNull(condition);
    }

    @Test
    public void testGetStringRepresentation() throws Exception {
        StringCondition condition = new StringCondition("P");
        assertEquals("(P)", condition.getStringRepresentation());
    }

    @Test
    public void testAndEmptyCondition() throws Exception {
        StringCondition condition = new StringCondition("P");
        EmptyCondition newCondition = new EmptyCondition();

        Condition andCondition = condition.and(newCondition);
        assertEquals("(P)", andCondition.getStringRepresentation());
    }

    @Test
    public void testAndStringCondition() throws Exception {
        StringCondition condition = new StringCondition("P");
        StringCondition newCondition = new StringCondition("Q");

        Condition andCondition = condition.and(newCondition);
        assertEquals("((P)&&(Q))", andCondition.getStringRepresentation());
    }

    @Test
    public void testOrEmptyCondition() throws Exception {
        StringCondition condition = new StringCondition("P");
        EmptyCondition newCondition = new EmptyCondition();

        Condition andCondition = condition.and(newCondition);
        assertEquals("(P)", andCondition.getStringRepresentation());
    }

    @Test
    public void testOrStringCondition() throws Exception {
        StringCondition condition = new StringCondition("P");
        StringCondition newCondition = new StringCondition("Q");

        Condition andCondition = condition.or(newCondition);
        assertEquals("((P)||(Q))", andCondition.getStringRepresentation());
    }

    @Test
    public void testNot() throws Exception {
        StringCondition condition = new StringCondition("P");

        Condition notCondition = condition.not();
        assertEquals("(!(P))", notCondition.getStringRepresentation());
    }

    @Test
    public void testCopy() throws Exception {
        StringCondition condition = new StringCondition("P");
        Condition copyCondition = condition.copy();

        assertNotSame(condition, copyCondition);
        assertEquals(condition.getStringRepresentation(), copyCondition.getStringRepresentation());
    }
}
