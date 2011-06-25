package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 13.06.11
 *         Time: 20:53
 */
public class SimpleMethodArgsHolderTest {

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new SimpleMethodArgsHolder());
    }

    @Test
    public void testAddArgName() throws Exception {
        SimpleMethodArgsHolder holder = new SimpleMethodArgsHolder();
        holder.addArgName("name");
        assertEquals(1, holder.getCountOfArgNames());
        assertTrue(holder.containsArgName("name"));
    }

    @Test
    public void testAddArgNames() throws Exception {
        SimpleMethodArgsHolder holder = new SimpleMethodArgsHolder();
        holder.addArgName("name1");
        holder.addArgName("name2");
        assertEquals(2, holder.getCountOfArgNames());
        assertTrue(holder.containsArgName("name1"));
        assertTrue(holder.containsArgName("name2"));
    }

    @Test
    public void testContainsArgName() throws Exception {
        SimpleMethodArgsHolder holder = new SimpleMethodArgsHolder();
        holder.addArgName("name1");
        assertTrue(holder.containsArgName("name1"));
        assertFalse(holder.containsArgName("name2"));
    }

    @Test
    public void testGetCountOfArgs() throws Exception {
        SimpleMethodArgsHolder holder = new SimpleMethodArgsHolder();
        assertEquals(0, holder.getCountOfArgNames());
        holder.addArgName("name1");
        holder.addArgName("name2");
        assertEquals(2, holder.getCountOfArgNames());
    }

    @Test
    public void testCopy() throws Exception {
        SimpleMethodArgsHolder holder = new SimpleMethodArgsHolder();
        SimpleMethodArgsHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }

}
