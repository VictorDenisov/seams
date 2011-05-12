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
        /*
        WildcardType tt = (WildcardType)((ClassOrInterfaceType)((ReferenceType)type).getType()).getTypeArgs().get(0);
        System.out.println(tt.getClass().toString());
        System.out.println(tt.getExtends() + "");
        System.out.println(tt.getSuper() + "");
        */

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
}
