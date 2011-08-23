package org.creativelabs.typefinder;

import japa.parser.ast.expr.*;
import org.creativelabs.introspection.*;
import org.testng.annotations.*;

import static org.testng.AssertJUnit.assertEquals;

public class TypeFinderRealExamplesTest {

    private ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

    @Test
    public void testInputStreamRead() throws Exception {

        Expression expr = ParseHelper.createExpression("input.read(buffer)");

        VariableList varTypes = ConstructionHelper.createVarListWithValues(ra,
                "input", "java.io.InputStream",
                "buffer", "byte[]");

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);
        assertEquals("int", type.toString());

    }

    @Test
    public void testThisPath() throws Exception {
        Expression expr = ParseHelper.createExpression("(this.arrayChar)");
        VariableList varTypes = ConstructionHelper.createVarListWithValues(ra,
                "this", "org.creativelabs.introspection.ReflectionAbstractionImpl");

        ClassType type = new TypeFinder(ra, varTypes, null).determineType(expr);

        assertEquals("java.util.HashMap<java.lang.String, java.lang.String, >", type.toString());
    }

    @Test
    public void testClassField() throws Exception {
        Expression expr = ParseHelper.createExpression("String.class");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();

        ImportList imports = ConstructionHelper.createEmptyImportList();

        ClassType type = new TypeFinder(ra, varTypes, imports).determineType(expr);

        assertEquals("java.lang.Class<java.lang.String, >", type.toString());
    }
}
