package org.creativelabs;

import org.creativelabs.typefinder.ConstructionHelper;
import org.creativelabs.typefinder.DependencyCounterVisitorBuilder;
import org.creativelabs.typefinder.ParseHelper;
import org.creativelabs.typefinder.VariableList;
import org.testng.annotations.Test;

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
                        "package org.creativelabs.typefinder; import org.creativelabs.typefinder.Dependency;"))
            .setTypeDeclaration(ParseHelper.createClassDeclaration(
                        "class ImportList extends Dependency {}"))
            .buildClassProcessor();

        assertEquals("org.creativelabs.typefinder.ImportList", builder.thisValue);
        assertEquals("org.creativelabs.typefinder.Dependency", builder.superValue);
    }

    @Test
    public void testClassProcessorConstructionNoExtends() throws Exception {
        ClassProcessorBuilderTestSpecific builder = new ClassProcessorBuilderTestSpecific();
        ClassProcessor classProcessor = builder
            .setImports(ParseHelper.createImportList(
                        "package org.creativelabs.typefinder; import org.creativelabs.Bar;"))
            .setTypeDeclaration(ParseHelper.createClassDeclaration("class ImportList {}"))
            .buildClassProcessor();

        assertEquals("org.creativelabs.typefinder.ImportList", builder.thisValue);
        assertEquals("java.lang.Object", builder.superValue);
    }

}
