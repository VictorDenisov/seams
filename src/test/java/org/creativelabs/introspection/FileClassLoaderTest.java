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
        String[] data = new String[]{"testdata/classloaderdata"};
        FileClassLoader loader = new FileClassLoader(data);
        Class cl = loader.loadClass("Sample");
        assertEquals("Sample", cl.getName());
    }

    @Test
    public void testLoadClassFromJar() throws Exception {
        String[] data = new String[]{"testdata/classloaderdata/sample.jar"};
        FileClassLoader loader = new FileClassLoader(data);
        Class cl = loader.loadClass("Sample");
        assertEquals("Sample", cl.getName());
    }

    @Test
    public void testLoadClassForSeveralJars() throws Exception {
        String[] data = new String[]{"testdata/classloaderdata/sample.jar",
            "testdata/classloaderdata/test.jar"};
        FileClassLoader loader = new FileClassLoader(data);

        Class sample = loader.loadClass("Sample");
        Class test = loader.loadClass("Test");

        assertEquals("Sample", sample.getName());
        assertEquals("Test", test.getName());
    }

    @Test
    public void testLoadClassFromFolderNamedAsJarFile() throws Exception {
        String[] data = new String[]{"testdata/classloaderdata/sample.jar",
            "testdata/classloaderdata/folder.jar"};

        FileClassLoader loader = new FileClassLoader(data);

        Class sample = loader.loadClass("Sample");
        Class folder = loader.loadClass("Folder");

        assertEquals("Sample", sample.getName());
        assertEquals("Folder", folder.getName());
    }
}
