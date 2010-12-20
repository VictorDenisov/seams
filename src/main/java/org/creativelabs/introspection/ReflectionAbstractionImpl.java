package org.creativelabs.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class ReflectionAbstractionImpl implements ReflectionAbstraction {

    private Class[] getTypeClasses(String[] types) throws Exception {
        Class[] result = new Class[types.length];
        for (int i = 0; i < types.length; ++i) {
            result[i] = Class.forName(types[i]);
        }
        return result;
    }

    public String getReturnType(String className, String methodName, String[] types) throws Exception {
        Class[] classTypes = getTypeClasses(types);
        Class cl = Class.forName(className);
        Method method = cl.getMethod(methodName, classTypes);
        Class myCl = method.getReturnType();
        return myCl.getName();
    }

    public String getFieldType(String className, String fieldName) throws Exception {
        Class cl = Class.forName(className);
        Field field = cl.getField(fieldName);
        return field.getType().getName();
    }

    public boolean classWithNameExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
