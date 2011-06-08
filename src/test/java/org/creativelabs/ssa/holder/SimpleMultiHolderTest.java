package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 11:04
 */
//TODO: to implement
public class SimpleMultiHolderTest {
    @Test
    public void testRead() throws Exception {

    }

    @Test
    public void testReadFrom() throws Exception {

    }

    @Test
    public void testWrite() throws Exception {

    }

    @Test
    public void testWriteTo() throws Exception {

    }

    @Test
    public void testGetDifferenceInVariables() throws Exception {

    }

    @Test
    public void testContainsKey() throws Exception {

    }

    @Test
    public void testCopyWriteToReadVariables() throws Exception {

    }

    @Test
    public void testCopyReadToWriteVariables() throws Exception {

    }

    @Test
    public void testIncreaseIndex() throws Exception {

    }

    @Test
    public void testIncreaseIndexIn() throws Exception {

    }

    @Test
    public void testMergeHolders() throws Exception {

    }

    @Test
    public void testCopy() throws Exception {
        SimpleMultiHolder holder = new SimpleMultiHolder();
        SimpleMultiHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }
}
