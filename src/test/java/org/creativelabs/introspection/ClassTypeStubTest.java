package org.creativelabs.introspection;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

public class ClassTypeStubTest {

    @Test
    public void testConstruction() {
        ClassTypeStub classType = new ClassTypeStub("org.creativelabs");
        assertEquals("org.creativelabs", classType.toStringRepresentation());
    }
}
