package org.creativelabs;

import org.testng.annotations.Test;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import java.util.*;

import static org.testng.AssertJUnit.*;

public class ClassProcessorBuilderTest {

    private class ClassProcessorBuilderTestSpecific extends ClassProcessorBuilder {
        String thisValue = null;
        String superValue = null;

        @Override
        DependencyCounterVisitorBuilder constructDependencyCounterVisitor() {

            if (fieldList.hasName("this")) {
                thisValue = fieldList.getFieldTypeAsClass("this").toString();
            }
            if (fieldList.hasName("super")) {
                superValue = fieldList.getFieldTypeAsClass("super").toString();
            }
            return null;
        }

        @Override
        VariableList constructVariableList() {
            return ConstructionHelper.createEmptyVariableList();
        }
    }

    @Test
    public void testClassProcessorConstruction() throws Exception {
        ClassProcessorBuilderTestSpecific builder = new ClassProcessorBuilderTestSpecific();
        ClassProcessor classProcessor = builder
            .setImports(ParseHelper.createImportList(
                        "package org.creativelabs; import org.creativelabs.Dependency;"))
            .setTypeDeclaration(ParseHelper.createClassDeclaration(
                        "class ImportList extends Dependency {}"))
            .buildClassProcessor();

        assertEquals("org.creativelabs.ImportList", builder.thisValue);
        assertEquals("org.creativelabs.Dependency", builder.superValue);
    }

    @Test
    public void testClassProcessorConstructionNoExtends() throws Exception {
        ClassProcessorBuilderTestSpecific builder = new ClassProcessorBuilderTestSpecific();
        ClassProcessor classProcessor = builder
            .setImports(ParseHelper.createImportList(
                        "package org.creativelabs; import org.creativelabs.Bar;"))
            .setTypeDeclaration(ParseHelper.createClassDeclaration("class ImportList {}"))
            .buildClassProcessor();

        assertEquals("org.creativelabs.ImportList", builder.thisValue);
        assertEquals("java.lang.Object", builder.superValue);
    }

}
