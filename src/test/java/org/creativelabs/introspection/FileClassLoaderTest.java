package org.creativelabs.introspection;

import japa.parser.ParseException;
import japa.parser.ast.expr.AssignExpr;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class FileClassLoaderTest {

    @Test
    public void testLoadClass() throws Exception {
        FileClassLoader loader = new FileClassLoader("testdata/classloaderdata");
        Class cl = loader.loadClass("Sample");
        assertEquals("Sample", cl.getName());
    }
}
