package org.creativelabs.ssa;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 11:25
 */
public class CopyingUtilsTest {
    @Test
    public void testCopy() throws Exception {

        Integer integer = new Integer(0);
        Integer copy = new CopyingUtils<Integer>().copy(integer);

        if (integer == copy) {
            fail(" Some integer and it copy are the same object!!!");
        }

        assertEquals(integer, copy);
    }
}
