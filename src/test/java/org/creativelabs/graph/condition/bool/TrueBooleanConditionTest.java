package org.creativelabs.graph.condition.bool;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 15.05.11
 *         Time: 16:25
 */
public class TrueBooleanConditionTest {

    @Test
    public void testConstructor() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();
        assertNotNull(condition);
    }

    @Test
    public void testGetStringRepresentation() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();
        assertEquals("true", condition.getStringRepresentation());
    }

    @Test
    public void testAndEmptyCondition() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();
        EmptyCondition newCondition = new EmptyCondition();

        Condition andCondition = condition.and(newCondition);
        assertEquals("true", andCondition.getStringRepresentation());
    }

    @Test
    public void testAndFalseCondition() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();
        FalseBooleanCondition newCondition = new FalseBooleanCondition();

        Condition andCondition = condition.and(newCondition);
        assertEquals("false", andCondition.getStringRepresentation());
    }

    @Test
    public void testOrEmptyCondition() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();
        EmptyCondition newCondition = new EmptyCondition();

        Condition orCondition = condition.or(newCondition);
        assertEquals("true", orCondition.getStringRepresentation());
    }

    @Test
    public void testOrTrueCondition() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();
        FalseBooleanCondition newCondition = new FalseBooleanCondition();

        Condition orCondition = condition.or(newCondition);
        assertEquals("true", orCondition.getStringRepresentation());
    }

    @Test
    public void testNot() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();

        Condition notCondition = condition.not();
        assertNotSame(condition, notCondition);
        assertEquals("false", notCondition.getStringRepresentation());
    }

    @Test
    public void testCopy() throws Exception {
        TrueBooleanCondition condition = new TrueBooleanCondition();
        Condition copyCondition = condition.copy();

        assertNotSame(condition, copyCondition);
        assertEquals(condition.getStringRepresentation(), copyCondition.getStringRepresentation());
    }
}
