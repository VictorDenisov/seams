package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:36
 */
public class SimpleConditionHolderTest {

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new SimpleConditionHolder());
    }

    @Test
    public void testCopy() throws Exception {
        SimpleConditionHolder holder = new SimpleConditionHolder();
        SimpleConditionHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }

}
