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
    public void testClassWithNameExists_doesntFailWithNativeInit() throws Exception {
        ClassLoader cl = this.getClass().getClassLoader();

        ReflectionAbstractionImpl impl = new ReflectionAbstractionImpl(cl);

        boolean result = impl.classWithNameExists("org.creativelabs.samples.SampleOS");

        assertTrue(result);
    }

    @Test
    public void testGetClassTypeByName() throws Exception {
        ClassLoader classLoader = mock(ClassLoader.class);
        ReflectionAbstraction impl = new ReflectionAbstractionImpl(classLoader);

        ClassType classType = impl.getClassTypeByName("org.creativelabs.Main");

        verify(classLoader).loadClass(eq("org.creativelabs.Main"));
        assertNotNull(classType);
    }

    @Test
    public void testGetClassTypeByName_doesntFailWithNativeInit() throws Exception {
        ClassLoader cl = this.getClass().getClassLoader();
        ReflectionAbstraction impl = new ReflectionAbstractionImpl(cl);

        ClassType classType = impl.getClassTypeByName("org.creativelabs.samples.SampleOS");

        assertEquals("org.creativelabs.samples.SampleOS", classType.toString());
    }

}
