package org.creativelabs.method;


public interface MethodTypeFinderBuilder {

    public String getMethodTypeAsString(String className, String methodName, Class[] types) throws Exception;

    public Class getMethodTypeAsClass(String className, String methodName, Class[] types) throws Exception;

}
