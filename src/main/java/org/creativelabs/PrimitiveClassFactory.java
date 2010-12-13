package org.creativelabs;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 12.12.10
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class PrimitiveClassFactory {

    private static PrimitiveClassFactory factory = null;

    public static synchronized PrimitiveClassFactory getFactory() {
        if (factory == null) {
            factory = new PrimitiveClassFactory();
        }
        return factory;
    }

    public Class getPrimitiveClass(String className) {
        if ("byte".equals(className) || "Byte".equals(className) || "java.lang.Byte".equals(className))
            return byte.class;
        if ("short".equals(className) || "Short".equals(className) || "java.lang.Short".equals(className))
            return short.class;
        if ("int".equals(className) || "Integer".equals(className) || "java.lang.Integer".equals(className))
            return int.class;
        if ("long".equals(className) || "Long".equals(className) || "java.lang.Long".equals(className))
            return long.class;
        if ("float".equals(className) || "Float".equals(className) || "java.lang.Float".equals(className))
            return float.class;
        if ("double".equals(className) || "Double".equals(className) || "java.lang.Double".equals(className))
            return double.class;
        if ("char".equals(className) || "Char".equals(className) || "java.lang.Char".equals(className))
            return char.class;
        if ("boolean".equals(className) || "Boolean".equals(className) || "java.lang.Boolean".equals(className))
            return boolean.class;
        if ("void".equals(className) || "Void".equals(className) || "java.lang.Void".equals(className))
            return void.class;
        if ("String".equals(className) || "java.lang.String".equals(className)) return String.class;
        throw new TypeFinder.UnsupportedExpressionException();
    }

    public boolean classIsPrimitive(String className) {
        if ("byte".equals(className) ||
                "short".equals(className) ||
                "int".equals(className) ||
                "long".equals(className) ||
                "float".equals(className) ||
                "double".equals(className) ||
                "char".equals(className) ||
                "boolean".equals(className) ||
                "void".equals(className) ||
                "String".equals(className) ||
                "java.lang.String".equals(className)) {
            return true;
        } else {
            return false;
        }
    }

}
