package org.creativelabs.introspection;

public interface ReflectionAbstraction {

    ClassType getReturnType(ClassType className, String methodName, ClassType[] types) throws Exception;

    ClassType getFieldType(ClassType className, String fieldName) throws Exception;

    ClassType getClassTypeByName(String className) throws Exception;

    boolean classWithNameExists(String className);

}
