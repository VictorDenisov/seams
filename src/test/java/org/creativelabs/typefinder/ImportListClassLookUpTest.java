package org.creativelabs.typefinder;

import org.testng.annotations.*;
import japa.parser.ast.type.*;

import org.creativelabs.introspection.*;

import static org.testng.AssertJUnit.*;

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

}
