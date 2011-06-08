package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 21.05.11
 *         Time: 14:01
 */
//TODO: to implement
public class SimpleMethodModifiersHolderTest {
    @Test
    public void testGetModifier() throws Exception {

    }

    @Test
    public void testCopy() throws Exception {
        SimpleConditionHolder holder = new SimpleConditionHolder();
        SimpleConditionHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }
}
