package org.creativelabs.method;

import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import org.creativelabs.ParseHelper;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

public class TestingMethodTypeFinderTest {

    @Test
    public void testMethodGetMethodTypeAsString() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "java.lang.String methodCall(int i){}"
                + "}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);

        MethodTypeFinderBuilder methodTypeFinder = new TestingMethodTypeFinder(cd);
        String type = null;
        String result  = "noException";
        try {
            type = methodTypeFinder.getMethodTypeAsString("Sample", "methodCall", new Class[]{int.class});
        } catch (Exception e) {
            result = "exception";
        }
        assertEquals("noException", result);
        assertEquals("java.lang.String", type);
    }

    @Test
    public void testMethodGetMethodTypeAsClass() throws Exception {
        CompilationUnit cu = ParseHelper.createCompilationUnit("public class Sample {"
                + "java.lang.String methodCall(int i){}"
                + "}");

        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) cu.getTypes().get(0);

        MethodTypeFinderBuilder methodTypeFinder = new TestingMethodTypeFinder(cd);
        Class type = null;
        String result  = "noException";
        try {
            type = methodTypeFinder.getMethodTypeAsClass("Sample", "methodCall", new Class[]{int.class});
        } catch (Exception e) {
            result = "exception";
        }
        assertEquals("noException", result);
        assertEquals(String.class, type);
    }

}
