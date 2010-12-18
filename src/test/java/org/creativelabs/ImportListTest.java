package org.creativelabs;

import org.testng.annotations.Test;
import japa.parser.ast.CompilationUnit;

import java.util.List;
import java.util.Arrays;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class ImportListTest {

    @Test
    public void testImportsConstruction() throws Exception {
        ImportList importList = ParseHelper.createImportList("import java.util.*;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"java.lang", "java.util"}), imports);
    }

    @Test
    public void testImportsConstructionNonStandardImport() throws Exception {
        ImportList importList = ParseHelper.createImportList("import org.apache.log4j.*;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"java.lang", "org.apache.log4j"}), imports);
    }

    @Test
    public void testImportsConstructionConcreteClass() throws Exception {
        ImportList importList = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"java.lang", "org.apache.log4j.Logger"}), imports);
    }

    @Test
    public void testImportsEmpty() throws Exception {
		String exception = "NoException";
		try {
			ImportList imports = ParseHelper.createImportList("");
		} catch (Exception e) {
			exception = e.toString();
		}

        assertEquals("NoException", exception);
    }

    @Test
    public void testGetClass() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.util.ArrayList;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").getName());
    }

    @Test
    public void testGetClassFromAsterisk() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.util.*;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").getName());
    }

    @Test
    public void testGetClassFromAsteriskTwoImports() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.io.*; import java.util.*;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").getName());
    }

    @Test
    public void testGetClassFromAsteriskFirstConcreteImport() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.io.File; import java.util.*;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").getName());
    }

    @Test
    public void testGetClassFromJavaLang() throws Exception {
        ImportList imports = ParseHelper.createImportList("");
        assertEquals("java.lang.String", imports.getClassByShortName("String").getName());
    }

    @Test
    public void testEmptyImportList() throws Exception {
        ImportList imports = ParseHelper.createImportList("");
        String message = "noException";
        try {
            imports.getClassByShortName("ArrayList");
        } catch (RuntimeException e) {
            message = "RuntimeException";
        }
        assertEquals("RuntimeException", message);
    }
}
