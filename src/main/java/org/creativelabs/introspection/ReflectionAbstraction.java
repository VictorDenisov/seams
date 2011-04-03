package org.creativelabs.introspection;

public interface ReflectionAbstraction {

    ClassType getReturnType(ClassType className, String methodName, ClassType[] types);

    ClassType getFieldType(ClassType className, String fieldName);

    ClassType getClassTypeByName(String className);

    boolean classWithNameExists(String className);

    ClassType createErrorClassType(String message);

    ClassType substGenericArgs(ClassType className, ClassType[] args);

    ClassType getNestedClass(ClassType className, String nestedClassName);

    ClassType getElementType(ClassType classType);

    ClassType createNullClassType();

    ClassType addArrayDepth(ClassType classType);

    ClassType addArrayDepth(ClassType classType, int count);
}
