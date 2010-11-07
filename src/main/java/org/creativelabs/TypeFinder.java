package org.creativelabs;

import java.lang.reflect.Method;

class TypeFinder {

    String getReturnType(String className, String methodName, Class[] types) throws Exception {
        Class cl = Class.forName(className);
        Method method = cl.getMethod(methodName, types);
        Class myCl = method.getReturnType();
        return myCl.getName();
    }

    /*
    String determineType(MethodCallExpr expr) throws Exception {
        String scopeType = determineType();

        Class cl = Class.forName(className);
        Method method = cl.getMethod(methodName, types);
        Class myCl = method.getReturnType();
        return myCl.getName();
    }
    */
}
