package org.creativelabs.ssa.holder.variable;

import org.testng.annotations.Test;

/**
 * @author azotcsit
 *         Date: 13.06.11
 *         Time: 8:50
 */
public class StringVariableTest {

    @Test
    public void testConstructor() {
        org.junit.Assert.assertNotNull(new StringVariable("name", "scope"));
    }

    @Test
    public void testGetString() {
        org.junit.Assert.assertEquals(new StringVariable("name", "scope").getString(), "scope.name");
    }

}
