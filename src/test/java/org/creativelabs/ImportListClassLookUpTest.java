package org.creativelabs;

import org.testng.annotations.*;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.type.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.body.*;

import java.util.List;
import java.util.Arrays;
import org.creativelabs.introspection.*;

import static org.testng.AssertJUnit.*;
import static org.creativelabs.AssertHelper.*;
import static org.mockito.Mockito.*;

public class ImportListClassLookUpTest {

    private ClassOrInterfaceType createClassOrInterfaceType(String data) throws Exception {
        Type type = ParseHelper.createType(data);
        ReferenceType rType = (ReferenceType)type;
        return (ClassOrInterfaceType)rType.getType();
    }

    @Test
    public void testGetClassByClassOrInterfaceType_ShortClassName() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.util.*;");
        ClassOrInterfaceType type = createClassOrInterfaceType("String");

        ClassType result = imports.getClassByClassOrInterfaceType(type);
        assertEquals("java.lang.String", result.toString());
    }

    @Test
    public void testGetClassByClassOrInterfaceType_LongClassName() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.util.*;");
        ClassOrInterfaceType type = createClassOrInterfaceType("java.lang.String");

        ClassType result = imports.getClassByClassOrInterfaceType(type);
        assertEquals("java.lang.String", result.toString());
    }

    @Test
    public void testGetClassByClassOrInterfaceType_NestedClass() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.util.*;");
        ClassOrInterfaceType type = createClassOrInterfaceType("java.util.Map.Entry");

        ClassType result = imports.getClassByClassOrInterfaceType(type);
        assertEquals("java.util.Map$Entry<K, V, >", result.toString());
    }

    @Test
    public void testGetClassByType_ClassOrInterfaceDirectly() throws Exception {
        ImportList imports = ParseHelper.createImportList("");
        ClassOrInterfaceType type = createClassOrInterfaceType("java.util.Map");
        ClassType result = imports.getClassByType(type);

        assertEquals("java.util.Map<K, V, >", result.toString());
    }

    @Test
    public void testGetClassByShortName_FromDefaultPackage() throws Exception {
        ReflectionAbstraction ra = mock(ReflectionAbstraction.class);
        when(ra.classWithNameExists("Main")).thenReturn(true);

        ImportList imports = new ParseHelper(ra).createImportListRA("");
        imports.getClassByShortName("Main");
        verify(ra).getClassTypeByName("Main");
    }

    @Test
    public void testGetClassByType_IteratorWithWildcardEmpty() throws Exception {
        VariableDeclarationExpr expr = (VariableDeclarationExpr)ParseHelper.createExpression("Iterator<?> iter");
        Type type = expr.getType();

        ImportList imports = ParseHelper.createImportList("import java.util.*;");

        ClassType classType = imports.getClassByType(type);

        assertEquals("java.util.Iterator<java.lang.Object, >", classType.toString());
    }

    @Test
    public void testGetClassByShortName_ForNestedClass() throws Exception {
        ImportList imports = ParseHelper.createImportList("import java.util.Map.Entry;");

        ClassType classType = imports.getClassByShortName("Entry");

        assertEquals("java.util.Map$Entry<K, V, >", classType.toString());
    }

    @Test
    public void testGetClassByShortName_ForTwoTimesNestedClass() throws Exception {
        TestingReflectionAbstraction ra = new TestingReflectionAbstraction();
        ra.addClass("org.apache.NioEndpoint$Handler$SocketState", "org.apache.NioEndpoint$Handler$SocketState");

        ImportList imports = new ParseHelper(ra).createImportListRA("import org.apache.NioEndpoint.Handler.SocketState;");

        ClassType classType = imports.getClassByShortName("SocketState");

        assertEquals("org.apache.NioEndpoint$Handler$SocketState", classType.toString());
    }

    @Test
    public void testGetClassByShortName_ForTwoNestedClassFromThis() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package java.util; public class Map {}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        ClassType classType = imports.getClassByShortName("Entry");

        assertEquals("java.util.Map$Entry<K, V, >", 
                classType.toString());
    }

    @Test
    public void testGetClassByShortName_ForTwoNestedClassFromSuper() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        //TODO There is duplication in SampleClass and this line.
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package org.creativelabs.samples; "
                + "public class SampleClass extends SampleSuperClass {}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        ClassType classType = imports.getClassByShortName("SampleNestedClass");

        assertEquals("org.creativelabs.samples.SampleSuperClass$SampleNestedClass", 
                classType.toString());
    }

    @Test
    public void testGetClassByShortName_ForNestedClassWithSameNameAsInJavaLang() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        //TODO There is duplication in SampleClass and this line.
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package org.creativelabs.samples; "
                + "public class SampleClass extends SampleSuperClass {}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        ClassType classType = imports.getClassByShortName("Compiler");

        assertEquals("org.creativelabs.samples.SampleClass$Compiler",
                classType.toString());
    }

    @Test
    public void testGetClassByShortName_ForImportedClassWithSameNameAsInJavaLang() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        //TODO There is duplication in SampleImportedClass and this line.
        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package org.creativelabs.samples; "
                +"import org.creativelabs.samples.mycompiler.Compiler; "
                + "public class SampleImportedClass {}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        ClassType classType = imports.getClassByShortName("Compiler");

        assertEquals("org.creativelabs.samples.mycompiler.Compiler",
                classType.toString());
    }

    @Test
    public void testGetClassByShortName_ForGenericTypes() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package org.creativelabs.samples; "
                + "public class SampleImportedClass<K, V> {}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        ClassType classType = imports.getClassByShortName("K");

        assertEquals("java.lang.Object", classType.toString());
    }

    @Test
    public void testGetClassSameAsInCurrentPackage() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package org.creativelabs.samples; "
                + "import java.util.HashMap;"
                + "public class SampleUsingClass {}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        ClassType classType = imports.getClassByShortName("HashMap");

        assertEquals("java.util.HashMap<K, V, >", classType.toString());
    }

    @Test
    public void testGetClassDeclaredInTheMethod() throws Exception {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();

        CompilationUnit cu = ParseHelper.createCompilationUnit(
                "package org.creativelabs.samples; "
                + "public class SampleDeclaringClassInMethod {}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)cu.getTypes().get(0);

        ImportList imports = new ImportList(ra, cu, cd);

        ClassType classType = imports.getClassByShortName("InnerClass");

        assertEquals(
                "org.creativelabs.samples.SampleDeclaringClassInMethod$1InnerClass",
                classType.toString());
    }
}
