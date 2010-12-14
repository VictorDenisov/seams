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
        ImportList importList = ParseHelper.createImportList("import java.lang.*;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"java.lang"}), imports);
    }

    @Test
    public void testImportsConstructionNonStandardImport() throws Exception {
        ImportList importList = ParseHelper.createImportList("import org.apache.log4j.*;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"org.apache.log4j"}), imports); }

    @Test
    public void testImportsConstructionConcreteClass() throws Exception {
        ImportList importList = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"org.apache.log4j.Logger"}), imports);
    }

    @Test
    public void testImportsPut() throws Exception {
        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        assertEquals("org.apache.log4j.Logger", imports.get("Logger"));
    }

    @Test
    public void testImportsContainsKey() throws Exception {
        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.Logger;");

        assertTrue(imports.containsKey("Logger"));
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
}
