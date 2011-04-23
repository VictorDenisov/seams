package org.creativelabs;

import org.testng.annotations.Test;

import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.*;

import org.creativelabs.introspection.*;
import org.creativelabs.report.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
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
