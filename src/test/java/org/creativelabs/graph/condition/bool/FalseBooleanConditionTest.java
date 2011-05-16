package org.creativelabs.graph.condition.bool;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;
import org.testng.annotations.Test;

import static org.junit.Assert.*;

/**
 * @author azotcsit
 *         Date: 15.05.11
 *         Time: 15:55
 */
public class FalseBooleanConditionTest {
    
    @Test
    public void testConstructor() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();
        assertNotNull(condition);
    }

    @Test
    public void testGetStringRepresentation() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();
        assertEquals("false", condition.getStringRepresentation());
    }

    @Test
    public void testAndEmptyCondition() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();
        EmptyCondition newCondition = new EmptyCondition();

        Condition andCondition = condition.and(newCondition);
        assertEquals("false", andCondition.getStringRepresentation());
    }

    @Test
    public void testAndTrueCondition() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();
        TrueBooleanCondition newCondition = new TrueBooleanCondition();

        Condition andCondition = condition.and(newCondition);
        assertEquals("false", andCondition.getStringRepresentation());
    }

    @Test
    public void testOrEmptyCondition() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();
        EmptyCondition newCondition = new EmptyCondition();

        Condition orCondition = condition.or(newCondition);
        assertEquals("false", orCondition.getStringRepresentation());
    }

    @Test
    public void testOrTrueCondition() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();
        TrueBooleanCondition newCondition = new TrueBooleanCondition();

        Condition orCondition = condition.or(newCondition);
        assertEquals("true", orCondition.getStringRepresentation());
    }

    @Test
    public void testNot() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();

        Condition notCondition = condition.not();
        assertNotSame(condition, notCondition);
        assertEquals("true", notCondition.getStringRepresentation());
    }

    @Test
    public void testCopy() throws Exception {
        FalseBooleanCondition condition = new FalseBooleanCondition();
        Condition copyCondition = condition.copy();

        assertNotSame(condition, copyCondition);
        assertEquals(condition.getStringRepresentation(), copyCondition.getStringRepresentation());
    }
    
}
