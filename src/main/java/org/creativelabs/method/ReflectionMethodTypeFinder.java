package org.creativelabs.method;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 17.12.10
 * Time: 0:30
 * To change this template use File | Settings | File Templates.
 */
public class ReflectionMethodTypeFinder implements MethodTypeFinderBuilder {
    @Override
    public String getMethodTypeAsString(String className, String methodName, Class[] types) throws Exception {
        Class cl = Class.forName(className);
        Method method = cl.getMethod(methodName, types);
        Class myCl = method.getReturnType();
        return myCl.getName();
    }

//    @Override
//    public Class getMethodTypeAsClass(String className, String methodName, Class[] types) throws Exception {
//        Class cl = Class.forName(className);
//        Method method = cl.getMethod(methodName, types);
//        return method.getReturnType();
//    }
}
