package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 21.05.11
 *         Time: 14:00
 */
//TODO: to implement
public class SimpleVariablesHolderTest {

    @Test
    public void testConstructorWithOneArg() throws Exception {
        SimpleVariablesHolder holder =
                new SimpleVariablesHolder(new HashMap<String, Integer>());
        assertEquals(holder.getReadVariables(), holder.getWriteVariables());
        assertNotSame(holder.getReadVariables(), holder.getWriteVariables());
    }

    @Test
    public void testGetReadVariables() throws Exception {

    }

    @Test
    public void testGetWriteVariables() throws Exception {

    }

    @Test
    public void testSetReadVariables() throws Exception {

    }

    @Test
    public void testSetWriteVariables() throws Exception {

    }

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

    }
}
