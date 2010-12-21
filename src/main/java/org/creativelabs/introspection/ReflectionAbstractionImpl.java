package org.creativelabs.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectionAbstractionImpl implements ReflectionAbstraction {

    private Class[] getTypeClasses(String[] types) throws Exception {
        Class[] result = new Class[types.length];
        for (int i = 0; i < types.length; ++i) {
            if (builtInMap.containsKey(types[i])) {
                result[i] = builtInMap.get(types[i]);
            } else {
                result[i] = Class.forName(types[i]);
            }
        }
        return result;
    }

   private Map<String, Class> builtInMap = new HashMap<String, Class>(){{
        put("int", Integer.TYPE);
        put("long", Long.TYPE);
        put("double", Double.TYPE);
        put("float", Float.TYPE);
        put("boolean", Boolean.TYPE);
        put("char", Character.TYPE);
        put("byte", Byte.TYPE);
        put("void", Void.TYPE);
        put("short", Short.TYPE);
    }};

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
