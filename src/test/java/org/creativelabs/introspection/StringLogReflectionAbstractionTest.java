package org.creativelabs.introspection;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

public class StringLogReflectionAbstractionTest {

    public static final String SIMPLE_TESTS_GROUP_NAME 
        = "simple-string-log-reflection-abstraction-simple";
    public static final String DECORATING_TESTS_GROUP_NAME 
        = "decorating-string-log-reflection-abstraction-decorating";

    StringLogReflectionAbstraction ra;

    StringLogReflectionAbstraction decoratingReflectionAbstraction;

    @BeforeMethod
    public void setUp() {
        ra = StringLogReflectionAbstraction.createDumb();

        decoratingReflectionAbstraction = StringLogReflectionAbstraction
            .createDecorating(ra);
    }

    @Test(groups=SIMPLE_TESTS_GROUP_NAME)
    public void testGetLog() {
        assertEquals("", decoratingReflectionAbstraction.getLog());
        assertEquals("", ra.getLog());
    }

    @Test(groups=SIMPLE_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testGetReturnType() {
        ClassType className = new ClassTypeStub("className");
        ClassType args[] = new ClassType[2];
        args[0] = new ClassTypeStub("a");
        args[1] = new ClassTypeStub("b");

        ra.getReturnType(className, "methodName", args);

        String result = ra.getLog();
        assertEquals("getReturnType(className, methodName, {a, b, }); ", result);
    }

    @Test(groups=DECORATING_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testGetReturnTypeFromDecorated() {
        ClassType className = new ClassTypeStub("className");
        ClassType args[] = new ClassType[2];
        args[0] = new ClassTypeStub("a");
        args[1] = new ClassTypeStub("b");

        decoratingReflectionAbstraction.getReturnType(className, "methodName", args);

        String result = ra.getLog();
        assertEquals("getReturnType(className, methodName, {a, b, }); ", result);
        assertEquals("getReturnType(className, methodName, {a, b, }):null; ", 
                decoratingReflectionAbstraction.getLog());
    }

    @Test(groups=SIMPLE_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testGetFieldType() {
        ClassType className = new ClassTypeStub("className");

        ra.getFieldType(className, "fieldName");

        String result = ra.getLog();
        assertEquals("getFieldType(className, fieldName); ", result);
    }

    @Test(groups=DECORATING_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testGetFieldTypeFromDecorated() {
        ClassType className = new ClassTypeStub("className");

        decoratingReflectionAbstraction.getFieldType(className, "fieldName");

        String result = ra.getLog();
        assertEquals("getFieldType(className, fieldName); ", result);
        assertEquals("getFieldType(className, fieldName):null; ", 
                decoratingReflectionAbstraction.getLog());
    }

    @Test(groups=SIMPLE_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testGetClassTypeByName() {
        ra.getClassTypeByName("className");

        String result = ra.getLog();
        assertEquals("getClassTypeByName(className); ", result);
    }

    @Test(groups=DECORATING_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testGetClassTypeByNameFromDecorated() {
        decoratingReflectionAbstraction.getClassTypeByName("className");

        String result = ra.getLog();
        assertEquals("getClassTypeByName(className); ", result);
        assertEquals("getClassTypeByName(className):null; ", 
                decoratingReflectionAbstraction.getLog());
    }

    @Test(groups=SIMPLE_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testClassWithNameExists() {
        ra.classWithNameExists("className");

        String result = ra.getLog();
        assertEquals("classWithNameExists(className); ", result);
    }

    @Test(groups=DECORATING_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testClassWithNameExistsFromDecorated() {
        decoratingReflectionAbstraction.classWithNameExists("className");

        String result = ra.getLog();
        assertEquals("classWithNameExists(className); ", result);
        assertEquals("classWithNameExists(className):false; ", 
                decoratingReflectionAbstraction.getLog());
    }

    @Test(groups=SIMPLE_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testCreateErrorClassType() {
        ra.createErrorClassType("message");

        String result = ra.getLog();
        assertEquals("createErrorClassType(message); ", result);
    }

    @Test(groups=DECORATING_TESTS_GROUP_NAME, dependsOnMethods="testGetLog")
    public void testCreateErrorClassTypeFromDecorated() {
        decoratingReflectionAbstraction.createErrorClassType("message");

        String result = ra.getLog();
        assertEquals("createErrorClassType(message); ", result);
        assertEquals("createErrorClassType(message):null; ", 
                decoratingReflectionAbstraction.getLog());
    }

}
