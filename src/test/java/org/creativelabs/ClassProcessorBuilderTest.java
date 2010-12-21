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
        DependencyCounterVisitor constructDependencyCounterVisitor(VariableList fieldList,
                ImportList imports) {

            if (fieldList.hasName("this")) {
                thisValue = fieldList.getFieldTypeAsString("this");
            }
            if (fieldList.hasName("super")) {
                superValue = fieldList.getFieldTypeAsString("super");
            }
            return null;
        }

        @Override
        VariableList constructVariableList(ClassOrInterfaceDeclaration typeDeclaration, 
                ImportList imports) {
            return new VariableList();
        }
    }

    @Test
    public void testClassProcessorConstruction() throws Exception {
        ClassProcessorBuilderTestSpecific builder = new ClassProcessorBuilderTestSpecific();
        ClassProcessor classProcessor = builder
            .setImportList(ParseHelper.createImportList(""))
            .setTypeDeclaration(ParseHelper.createClassDeclaration("class Foo extends Bar {}"))
            .buildClassProcessor();

        assertEquals("Foo", builder.thisValue);
        assertEquals("Bar", builder.superValue);
    }

}
