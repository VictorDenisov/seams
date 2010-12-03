package org.creativelabs;

import org.testng.annotations.Test;
import japa.parser.ast.CompilationUnit;

import java.util.List;
import java.util.Arrays;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;

public class ImportsListTest {

    @Test
    public void testImportsConstruction() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "import java.lang.*; public class Main{}");
        ImportList importList = new ImportList(cu);
        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"java.lang"}), imports);
    }

    @Test
    public void testImportsConstructionNonStandardImport() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "import org.apache.log4j.*; public class Main{}");
        ImportList importList = new ImportList(cu);
        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"org.apache.log4j"}), imports);
    }

    @Test
    public void testImportsConstructionConcreteClass() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "import org.apache.log4j.Logger; public class Main{}");
        ImportList importList = new ImportList(cu);
        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"org.apache.log4j.Logger"}), imports);
    }
}
