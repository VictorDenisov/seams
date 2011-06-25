package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 11:04
 */

public class SimpleMultiHolderTest {

    @Test
    public void testCopy() throws Exception {
        SimpleMultiHolder holder = new SimpleMultiHolder();
        SimpleMultiHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }
}
