package org.creativelabs.ssa;

import org.creativelabs.graph.condition.StringCondition;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 11:04
 */
public class VariablesHolderTest {
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

        VariablesHolder basicHolder = new VariablesHolder(new HashMap<String, Integer>(){
                    {put("a", 1);}
                    {put("b", 2);}
                });
        basicHolder.setCondition(new StringCondition("condition"));

        //creating of the copy
        VariablesHolder copyOfHolder = basicHolder.copy();

        //checking of equals
        assertEquals(basicHolder.getReadVariables(), copyOfHolder.getReadVariables());
        assertEquals(basicHolder.getWriteVariables(), copyOfHolder.getWriteVariables());
        //TODO to umplement equals method
        assertEquals(basicHolder.getCondition().getStringRepresentation(),
                copyOfHolder.getCondition().getStringRepresentation());


        //changing of the the basic variables holder
        basicHolder.setWriteVariables(new HashMap<String, Integer>() {
            {put("a", 2);}
            {put("b", 2);}
        });
        basicHolder.setReadVariables(new HashMap<String, Integer>() {
            {put("a", 2);}
            {put("b", 2);}
        });
        basicHolder.getCondition().and(new StringCondition("condition2"));

        //checking of non equals
        assertNotSame(basicHolder.getReadVariables(), copyOfHolder.getReadVariables());
        assertNotSame(basicHolder.getWriteVariables(), copyOfHolder.getWriteVariables());
        assertNotSame(basicHolder.getCondition().getStringRepresentation(),
                copyOfHolder.getCondition().getStringRepresentation());

    }
}
