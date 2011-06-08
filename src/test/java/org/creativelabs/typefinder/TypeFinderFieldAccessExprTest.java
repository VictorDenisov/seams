package org.creativelabs.typefinder;

import japa.parser.ast.expr.*;
import org.creativelabs.introspection.*;
import org.testng.annotations.*;

import static org.testng.AssertJUnit.assertEquals;

public class TypeFinderFieldAccessExprTest {

    private ReflectionAbstraction ra = ReflectionAbstractionImpl.create();

    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void testDetermineTypeFieldAccessExpr() throws Exception {
        Expression expr = ParseHelper.createExpression("str.CASE_INSENSITIVE_ORDER");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", ra.getClassTypeByName(String.class.getName()));

        ClassType type = new TypeFinder(varTypes, null).determineType(expr);
        assertEquals("java.util.Comparator<java.lang.String, >", type.toString());
    }

    @Test
    public void testDetermineTypeNonStandardClassFieldAccess() throws Exception {
        Expression expr = ParseHelper.createExpression("LogLevel.DEBUG");

        VariableList varTypes = ConstructionHelper.createEmptyVariableList();
        varTypes.put("str", new ClassTypeStub(String.class.getName()));

        ImportList imports = ParseHelper.createImportList("import org.apache.log4j.lf5.LogLevel;");

        ClassType type = new TypeFinder(varTypes, imports).determineType(expr);

        assertEquals("org.apache.log4j.lf5.LogLevel", type.toString());
    }


    @Test
    public void testFieldAccess() throws Exception {
        Expression expr = ParseHelper.createExpression("clazz.typeDeclaration");

        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("typeDeclaration", ra.getClassTypeByName("java.lang.String"));
        varList.put("clazz", ra.getClassTypeByName("org.creativelabs.ClassProcessor"));

        ImportList imports = ParseHelper.createImportList("");

        TypeFinder typeFinder = new TypeFinder(ra, varList, imports);
        ClassType type = typeFinder.determineType(expr);

        assertEquals("japa.parser.ast.body.ClassOrInterfaceDeclaration", type.toString());
    }

    @Test
    public void testFieldAccessFromLongClassName() throws Exception {
        Expression expr = ParseHelper.createExpression("javax.swing.SwingConstants.CENTER");
        VariableList varList = ConstructionHelper.createEmptyVariableList();

        ImportList imports = ConstructionHelper.createEmptyImportList();
        TypeFinder typeFinder = new TypeFinder(varList, imports);

        ClassType type = typeFinder.determineType(expr);

        assertEquals("int", type.toString());
    }

    @Test
    public void testFieldAccessMouseEvent() throws Exception {
        Expression expr = ParseHelper.createExpression("MouseEvent.BUTTON3_MASK");
        VariableList varList = ConstructionHelper.createEmptyVariableList();

        ImportList imports = ParseHelper.createImportList("import java.awt.event.*;");
        TypeFinder typeFinder = new TypeFinder(varList, imports);

        ClassType type = typeFinder.determineType(expr);

        assertEquals("int", type.toString());
    }

    private class JFrameDescendant extends javax.swing.JFrame {
    }

    /**
     * class JFrameDescendant extends JFrame {
     *     void method() {
     *         rootPane.doSomething();
     *     }
     * }
     */
    @Test
    public void testFieldAccessWithoutScope() throws Exception {
        Expression expr = ParseHelper.createExpression("rootPane");
        
        ImportList importList = ConstructionHelper.createEmptyImportList();
        VariableList varList = ConstructionHelper.createEmptyVariableList();
        varList.put("this", ra.getClassTypeByName(
                    "org.creativelabs.typefinder.TypeFinderFieldAccessExprTest$JFrameDescendant"));

        TypeFinder typeFinder = new TypeFinder(varList, importList);

        ClassType result = typeFinder.determineType(expr);

        assertEquals("javax.swing.JRootPane", result.toString());
    }
}
