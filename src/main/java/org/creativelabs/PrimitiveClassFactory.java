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
        if ("byte".equals(className) || "Byte".equals(className)) return byte.class;
        if ("short".equals(className) || "Short".equals(className)) return short.class;
        if ("int".equals(className) || "Integer".equals(className)) return int.class;
        if ("long".equals(className) || "Long".equals(className)) return long.class;
        if ("float".equals(className) || "Float".equals(className)) return float.class;
        if ("double".equals(className) || "Double".equals(className)) return double.class;
        if ("char".equals(className) || "Char".equals(className)) return char.class;
        if ("boolean".equals(className) || "Boolean".equals(className)) return boolean.class;
        if ("void".equals(className) || "Void".equals(className)) return void.class;
        if ("String".equals(className)) return String.class;
        throw new TypeFinder.UnsupportedExpressionException();
    }

    public Class getPrimitiveClass(Class clazz) {
        //TODO to change the method of comparison - getSimpleName() it's wrong - imho
        if ("Byte".equals(clazz.getSimpleName())) return byte.class;
        if ("Short".equals(clazz.getSimpleName())) return short.class;
        if ("Integer".equals(clazz.getSimpleName())) return int.class;
        if ("Long".equals(clazz.getSimpleName())) return long.class;
        if ("Float".equals(clazz.getSimpleName())) return float.class;
        if ("Double".equals(clazz.getSimpleName())) return double.class;
        if ("Char".equals(clazz.getSimpleName())) return char.class;
        if ("Boolean".equals(clazz.getSimpleName())) return boolean.class;
        if ("Void".equals(clazz.getSimpleName())) return void.class;
        if ("String".equals(clazz.getSimpleName())) return String.class;
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
                "String".equals(className)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean classIsPrimitive(Class clazz) {
        if ("byte".equals(clazz.getSimpleName()) ||
                "short".equals(clazz.getSimpleName()) ||
                "int".equals(clazz.getSimpleName()) ||
                "long".equals(clazz.getSimpleName()) ||
                "float".equals(clazz.getSimpleName()) ||
                "double".equals(clazz.getSimpleName()) ||
                "char".equals(clazz.getSimpleName()) ||
                "boolean".equals(clazz.getSimpleName()) ||
                "void".equals(clazz.getSimpleName()) ||
                "String".equals(clazz.getSimpleName())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean classMayBeCastToPrimitive(String className) {
        if ("byte".equals(className) || "Byte".equals(className) ||
                "short".equals(className) || "Short".equals(className) ||
                "int".equals(className) || "Integer".equals(className) ||
                "long".equals(className) || "Long".equals(className) ||
                "float".equals(className) || "Float".equals(className) ||
                "double".equals(className) || "Double".equals(className) ||
                "char".equals(className) || "Char".equals(className) ||
                "boolean".equals(className) || "Boolean".equals(className) ||
                "void".equals(className) || "Void".equals(className) ||
                "String".equals(className)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean classMayBeCastToPrimitive(Class clazz) {
        if ("byte".equals(clazz.getSimpleName()) || "Byte".equals(clazz.getSimpleName()) ||
                "short".equals(clazz.getSimpleName()) || "Short".equals(clazz.getSimpleName()) ||
                "int".equals(clazz.getSimpleName()) || "Integer".equals(clazz.getSimpleName()) ||
                "long".equals(clazz.getSimpleName()) || "Long".equals(clazz.getSimpleName()) ||
                "float".equals(clazz.getSimpleName()) || "Float".equals(clazz.getSimpleName()) ||
                "double".equals(clazz.getSimpleName()) || "Double".equals(clazz.getSimpleName()) ||
                "char".equals(clazz.getSimpleName()) || "Char".equals(clazz.getSimpleName()) ||
                "boolean".equals(clazz.getSimpleName()) || "Boolean".equals(clazz.getSimpleName()) ||
                "void".equals(clazz.getSimpleName()) || "Void".equals(clazz.getSimpleName()) ||
                "String".equals(clazz.getSimpleName())) {
            return true;
        } else {
            return false;
        }
    }

}
