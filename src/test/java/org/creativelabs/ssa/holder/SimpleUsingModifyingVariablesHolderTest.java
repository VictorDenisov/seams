package org.creativelabs.ssa.holder;

import org.creativelabs.ssa.holder.variable.StringVariable;
import org.creativelabs.ssa.holder.variable.Variable;
import org.testng.annotations.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author azotcsit
 *         Date: 21.05.11
 *         Time: 14:00
 */
public class SimpleUsingModifyingVariablesHolderTest {

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new SimpleUsingModifyingVariablesHolder());
    }

    @Test
    public void testAddUsingVariable() throws Exception {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        holder.addUsingVariable(new StringVariable("name1", "scope1"));
        assertEquals(1, holder.getUsingVariables().size());
        assertTrue(holder.getUsingVariables().contains(new StringVariable("name1", "scope1")));
    }

    @Test
    public void testAddUsingVariables() throws Exception {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        holder.addUsingVariables(new HashSet<Variable>(){{
            add(new StringVariable("name1", "scope1"));
            add(new StringVariable("name2", "scope2"));}});
        assertEquals(2, holder.getUsingVariables().size());
        assertTrue(holder.getUsingVariables().contains(new StringVariable("name1", "scope1")));
        assertTrue(holder.getUsingVariables().contains(new StringVariable("name2", "scope2")));
    }

    @Test
    public void testAddModifyingVariable() throws Exception {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        holder.addModifyingVariable(new StringVariable("name1", "scope1"));
        assertEquals(1, holder.getModifyingVariables().size());
        assertTrue(holder.getModifyingVariables().contains(new StringVariable("name1", "scope1")));
    }

    @Test
    public void testAddModifyingVariables() throws Exception {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        holder.addModifyingVariables(new HashSet<Variable>() {{
            add(new StringVariable("name1", "scope1"));
            add(new StringVariable("name2", "scope2"));
        }});
        assertEquals(2, holder.getModifyingVariables().size());
        assertTrue(holder.getModifyingVariables().contains(new StringVariable("name1", "scope1")));
        assertTrue(holder.getModifyingVariables().contains(new StringVariable("name2", "scope2")));
    }

    @Test
    public void testAdd() throws Exception {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        holder.addUsingVariables(new HashSet<Variable>(){{
            add(new StringVariable("name1", "scope1"));
            add(new StringVariable("name2", "scope2"));}});
        assertEquals(2, holder.getUsingVariables().size());
        assertTrue(holder.getUsingVariables().contains(new StringVariable("name1", "scope1")));
        assertTrue(holder.getUsingVariables().contains(new StringVariable("name2", "scope2")));
    }

    @Test
    public void testCopy() throws Exception {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        SimpleUsingModifyingVariablesHolder copyHolder = holder.copy();
        assertEquals(holder, copyHolder);
        assertNotSame(holder, copyHolder);
    }

}
