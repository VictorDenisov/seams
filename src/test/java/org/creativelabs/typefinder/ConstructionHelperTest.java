package org.creativelabs.typefinder;

import org.creativelabs.typefinder.ConstructionHelper;
import org.testng.annotations.Test;

import org.creativelabs.introspection.*;

import static org.mockito.Mockito.*;

public class ConstructionHelperTest {

    @Test
    public void testClassCreation() {
        ReflectionAbstraction ra = mock(ReflectionAbstraction.class);
        ClassType result = ConstructionHelper.createClassTypeFromNotation(ra, "java.io.class[][][]");

        verify(ra).getClassTypeByName(eq("java.io.class"));
        verify(ra).convertToArray(any(ClassType.class), eq(3));
    }
}
