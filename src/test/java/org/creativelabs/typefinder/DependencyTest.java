package org.creativelabs.typefinder;

import org.creativelabs.typefinder.Dependency;
import org.testng.annotations.Test;
import org.creativelabs.introspection.*;

import static org.testng.AssertJUnit.*;

public class DependencyTest {

    @Test
    public void testConstructor() throws Exception {
        Dependency dependency = new Dependency("value", new ClassTypeStub("type"));

        assertEquals("value", dependency.getExpression());
        assertEquals("type", dependency.getType().toString());

    }
}
