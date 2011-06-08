package org.creativelabs.ssa.holder;

import org.testng.annotations.Test;

import static org.junit.Assert.*;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:29
 */
public class SimpleClassFieldsHolderTest {

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new SimpleClassFieldsHolder());
    }

    @Test
    public void testAddFieldName() throws Exception {
        SimpleClassFieldsHolder holder = new SimpleClassFieldsHolder();
        holder.addFieldName("name");
        assertEquals(1, holder.getCountOfFieldNames());
        assertTrue(holder.containsFieldName("name"));
    }

    @Test
    public void testAddFieldNames() throws Exception {
        SimpleClassFieldsHolder holder = new SimpleClassFieldsHolder();
        holder.addFieldName("name1");
        holder.addFieldName("name2");
        assertEquals(2, holder.getCountOfFieldNames());
        assertTrue(holder.containsFieldName("name1"));
        assertTrue(holder.containsFieldName("name2"));
    }

    @Test
    public void testContainsFieldName() throws Exception {
        SimpleClassFieldsHolder holder = new SimpleClassFieldsHolder();
        holder.addFieldName("name1");
        assertTrue(holder.containsFieldName("name1"));
        assertFalse(holder.containsFieldName("name2"));
    }

    @Test
    public void testGetCountOfFieldNames() throws Exception {
        SimpleClassFieldsHolder holder = new SimpleClassFieldsHolder();
        assertEquals(0, holder.getCountOfFieldNames());
        holder.addFieldName("name1");
        holder.addFieldName("name2");
        assertEquals(2, holder.getCountOfFieldNames());
    }

    @Test
    public void testCopy() throws Exception {
        SimpleClassFieldsHolder holder = new SimpleClassFieldsHolder();
        SimpleClassFieldsHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }
}
