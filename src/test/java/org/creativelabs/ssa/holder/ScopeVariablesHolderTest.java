package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 13.06.11
 *         Time: 20:50
 */

//TODO: to implement more tests
public class ScopeVariablesHolderTest {
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
    public void testGetPhiIndexes() throws Exception {

    }

    @Test
    public void testContainsKey() throws Exception {

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
        ScopeVariablesHolder holder = new ScopeVariablesHolder();
        ScopeVariablesHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }
}
