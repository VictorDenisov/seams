package org.creativelabs.typefinder;

import org.creativelabs.introspection.ClassType;
import org.creativelabs.introspection.ReflectionAbstraction;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class ConstructionHelperTest {

    @Test
    public void testClassCreation() {
        ReflectionAbstraction ra = mock(ReflectionAbstraction.class);
        ClassType result = ConstructionHelper.createClassTypeFromNotation(ra, "java.io.class[][][]");

        verify(ra).getClassTypeByName(eq("java.io.class"));
        verify(ra).addArrayDepth(any(ClassType.class), eq(3));
    }
}
