package org.creativelabs;

import org.testng.annotations.*;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.type.*;
import japa.parser.ast.body.*;

import java.util.List;
import java.util.Arrays;

import org.creativelabs.introspection.*;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;
import static org.mockito.Mockito.*;

public class ImportListTest {

    private ParseHelper parseHelper;

    @BeforeClass
    public void setUpClass() {
        TestingReflectionAbstraction tra = new TestingReflectionAbstraction();
        tra.addClass("java.util.ArrayList", "java.util.ArrayList");
        tra.addClass("java.lang.String", "java.lang.String");
        tra.addClass("java.lang.Integer", "java.lang.Integer");
        tra.addClass("java.io.File", "java.io.File");
        tra.addClass("java.util.Map", "java.util.Map");
        tra.addClass("java.util.Map$Entry", "java.util.Map$Entry");
        tra.addClass("int", "int");
        parseHelper = new ParseHelper(tra);
    }

    @Test
    public void testImportsConstruction() throws Exception {
        ImportList importList = parseHelper.createImportListRA("import java.util.*;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"java.util", "java.lang"}), imports);
    }

    @Test
    public void testImportsConstructionNonStandardImport() throws Exception {
        ImportList importList = parseHelper.createImportListRA("import org.apache.log4j.*;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"org.apache.log4j", "java.lang"}), imports);
    }

    @Test
    public void testImportsConstructionConcreteClass() throws Exception { ImportList importList = parseHelper.createImportListRA("import org.apache.log4j.Logger;");

        List<String> imports = importList.getImports();

        assertEqualsList(Arrays.asList(new String[]{"org.apache.log4j.Logger", "java.lang"}), imports);
    }

    @Test
    public void testImportsEmpty() throws Exception {
		String exception = "NoException";
		try {
			ImportList imports = parseHelper.createImportListRA("");
		} catch (Exception e) {
			exception = e.toString();
		}

        assertEquals("NoException", exception);
    }

    @Test
    public void testGetClass() throws Exception {
        ImportList imports = parseHelper.createImportListRA("import java.util.ArrayList;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").toString());
    }

    @Test
    public void testGetClassFromAsterisk() throws Exception {
        ImportList imports = parseHelper.createImportListRA("import java.util.*;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").toString());
    }

    @Test
    public void testGetClassFromAsteriskTwoImports() throws Exception {
        ImportList imports = parseHelper.createImportListRA("import java.io.*; import java.util.*;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").toString());
    }

    @Test
    public void testGetClassFromAsteriskFirstConcreteImport() throws Exception {
        ImportList imports = parseHelper.createImportListRA("import java.io.File; import java.util.*;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").toString());
    }

    @Test
    public void testGetClassFromJavaLang() throws Exception {
        ImportList imports = parseHelper.createImportListRA("");
        assertEquals("java.lang.String", imports.getClassByShortName("String").toString());
    }

    @Test
    public void testGetClassFromPrimitiveInt() throws Exception {
        ImportList imports = parseHelper.createImportListRA("");
        assertEquals("int", imports.getClassByShortName("int").toString());
    }

    @Test
    public void testEmptyImportList() throws Exception {
        ImportList imports = parseHelper.createImportListRA("");
        String message = "noException";
        ClassType result = imports.getClassByShortName("ArrayList");
        assertEquals("Unknown class: ArrayList", result + "");
    }

    @Test
    public void testGetClassFromLocalPackage() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package java.util;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").toString());
    }

    @Test
    public void testGetClassFromLocalPackageAsterisk() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package java.util; import java.io.*;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").toString());
    }

    @Test
    public void testGetClassFromAsteriskLocalPackage() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package java.util; import java.io.*;");
        assertEquals("java.io.File", imports.getClassByShortName("File").toString());
    }

    @Test
    public void testGetClassFromLocalPackageConcreteImport() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package java.util; import java.io.File;");
        assertEquals("java.util.ArrayList", imports.getClassByShortName("ArrayList").toString());
    }

    @Test
    public void testGetClassFromConcreteImportLocalPackage() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package java.util; import java.io.*;");
        assertEquals("java.io.File", imports.getClassByShortName("File").toString());
    }

    @Test
    public void testGetClassFromGeneric() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package org.sample; import java.util.*;");
        assertEquals("java.util.Map", imports.getClassByShortName("Map<String, String>").toString());
    }

    @Test
    public void testGetClassFromNested() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package org.sample; import java.util.*;");
        assertEquals("java.util.Map$Entry", imports.getClassByShortName("Map.Entry<String, String>").toString());
    }

    @Test
    public void testGetClassByType() throws Exception {
        ParseHelper ph = new ParseHelper(ReflectionAbstractionImpl.create());

        ImportList imports = ph.createImportListRA("package org.sample; import java.util.*;");
        Type type = ParseHelper.createType("ArrayList<String>");

        ClassType result = imports.getClassByType(type);
        assertEquals("java.util.ArrayList<java.lang.String, >", result.toString());
    }

    @Test
    public void testGetClassByTypePrimitive() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package org.sample; import java.util.*;");
        Type type = parseHelper.createType("int");

        ClassType result = imports.getClassByType(type);
        assertEquals("int", result.toString());
    }

    @Test
    public void testGetClassByTypePrimitiveReference() throws Exception {
        ImportList imports = parseHelper.createImportListRA("package org.sample; import java.util.*;");
        Type type = parseHelper.createType("int[]");

        ClassType result = imports.getClassByType(type);
        assertEquals("int[]", result.toString());
    }

    @Test
    public void testGetClassByTypeFullName() throws Exception {
        ImportList imports = ParseHelper.createImportList("");
        Type type = ParseHelper.createType("java.lang.String");

        ClassType result = imports.getClassByType(type);
        assertEquals("java.lang.String", result.toString());
    }

    @Test
    public void testCreateMapEntry() throws Exception {
        ParseHelper ph = new ParseHelper(ReflectionAbstractionImpl.create());

        ImportList imports = ph.createImportListRA("package org.sample; import java.util.*;");
        Type type = ph.createType("Map.Entry<String, String>");

        ClassType result = imports.getClassByType(type);

        assertEquals("java.util.Map$Entry<java.lang.String, java.lang.String, >", result.toString());
    }

    @Test
    public void testCreateMapEntryMapImported() throws Exception {
        ParseHelper ph = new ParseHelper(ReflectionAbstractionImpl.create());

        ImportList imports = ph.createImportListRA("package org.sample; import java.util.Map;");
        Type type = ph.createType("Map.Entry<String, String>");

        ClassType result = imports.getClassByType(type);

        assertEquals("java.util.Map$Entry<java.lang.String, java.lang.String, >", result.toString());
    }

    @Test
    public void testCreateFromDefaultPackage() throws Exception {
        ReflectionAbstraction ra = ReflectionAbstractionImpl.create();
        ParseHelper ph = new ParseHelper(ReflectionAbstractionImpl.create());
        CompilationUnit cu = ph.createCompilationUnit("import java.io.*; public class Value {}");
        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        assertEquals("Value", imports.className);
    }

    @Test
    public void testNestedClassTypeDetection() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package org.creativelabs; public class Main { public static class Nested {}}");
        ReflectionAbstraction ra = mock(ReflectionAbstraction.class);

        when(ra.classWithNameExists("org.creativelabs.Main$Nested")).thenReturn(true);

        ImportList imports = 
            new ImportList(ra, cu, (ClassOrInterfaceDeclaration)(cu.getTypes().get(0)));

        ClassType result = imports.getClassByShortName("Nested");

        verify(ra).getClassTypeByName("org.creativelabs.Main$Nested");
    }
}
