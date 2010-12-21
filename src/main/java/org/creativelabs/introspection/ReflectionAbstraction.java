package org.creativelabs.introspection;

public interface ReflectionAbstraction {

    String getReturnType(String className, String methodName, String[] types) throws Exception;

    String getFieldType(String className, String fieldName) throws Exception;

    String getClassType(String className) throws Exception;

	ClassType getReturnType(ClassType className, String methodName, ClassType[] types) throws Exception;

    ClassType getFieldType(ClassType className, String fieldName) throws Exception;

    ClassType getClassTypeByName(String className) throws Exception;

    boolean classWithNameExists(String className);

}
