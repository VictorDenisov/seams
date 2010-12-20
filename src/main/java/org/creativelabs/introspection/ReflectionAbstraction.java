package org.creativelabs.introspection;

public interface ReflectionAbstraction {

    String getReturnType(String className, String methodName, String[] types) throws Exception;

    String getFieldType(String className, String fieldName) throws Exception;

}
