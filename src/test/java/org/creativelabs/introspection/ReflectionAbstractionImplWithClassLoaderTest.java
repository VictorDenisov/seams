package org.creativelabs.introspection;

import org.testng.annotations.*;

import java.util.*;

import java.lang.reflect.*;

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

public class ReflectionAbstractionImplWithClassLoaderTest {
    
    @Test
    public void testClassWithNameExists() throws Exception {
        ClassLoader classLoader = mock(ClassLoader.class);
        ReflectionAbstraction impl = new ReflectionAbstractionImpl(classLoader);

        impl.classWithNameExists("org.creativelabs.Main");

        verify(classLoader).loadClass(eq("org.creativelabs.Main"));
    }

    @Test
    public void testGetClassTypeByName() throws Exception {
        ClassLoader classLoader = mock(ClassLoader.class);
        ReflectionAbstraction impl = new ReflectionAbstractionImpl(classLoader);

        ClassType classType = impl.getClassTypeByName("org.creativelabs.Main");

        verify(classLoader).loadClass(eq("org.creativelabs.Main"));
        assertNotNull(classType);
    }

}
