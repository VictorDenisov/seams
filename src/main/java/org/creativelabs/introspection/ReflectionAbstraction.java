package org.creativelabs.introspection;

public interface ReflectionAbstraction {

    ClassType getReturnType(ClassType className, String methodName, ClassType[] types);

    ClassType getFieldType(ClassType className, String fieldName);

    ClassType getClassTypeByName(String className);

    boolean classWithNameExists(String className);

    ClassType createErrorClassType(String message);

}
