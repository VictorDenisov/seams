package org.creativelabs.introspection;

import java.lang.reflect.*;
import java.util.HashMap;

public class ReflectionAbstractionImpl implements ReflectionAbstraction {

    private static class ClassTypeImpl implements ClassType {

        private Class clazz;

        private HashMap<String, ClassType> genericArgs = null;

        @Override
        public String toString() {
            StringBuffer result = new StringBuffer();
            result.append(clazz.getName());
            if (clazz.getTypeParameters().length != 0) {
                result.append("<");
                for (TypeVariable variable : clazz.getTypeParameters()) {
                    if (genericArgs != null) {
                        result.append(genericArgs.get(variable.toString()) + ", ");
                    } else {
                        result.append(variable + ", ");
                    }
                }
                result.append(">");
            }
            return result.toString();
        }
    }

    private static class ClassTypeError implements ClassType {
        private String message;

        @Override
        public String toString() {
            return message;
        }
    }

    private Class getClass(String type) throws ClassNotFoundException {
        if ("byte".equals(type)
                || "java.lang.Byte".equals(type)) {
            return byte.class;
        }
        if ("short".equals(type)
                || "java.lang.Short".equals(type)) {
            return short.class;
        }
        if ("int".equals(type)
                || "java.lang.Integer".equals(type)) {
            return int.class;
        }
        if ("long".equals(type)
                || "java.lang.Long".equals(type)) {
            return long.class;
        }
        if ("float".equals(type)
                || "java.lang.Float".equals(type)) {
            return float.class;
        }
        if ("double".equals(type)
                || "java.lang.Double".equals(type)) {
            return double.class;
        }
        if ("char".equals(type)
                || "java.lang.Char".equals(type)) {
            return char.class;
        }
        if ("boolean".equals(type)
                || "java.lang.Boolean".equals(type)) {
            return boolean.class;
        }
        if ("void".equals(type)
                || "java.lang.Void".equals(type)) {
            return void.class;
        }
        return Class.forName(type);
    }

    @Override
    public boolean classWithNameExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private Class[] getTypeClasses(ClassType[] types) {
        Class[] result = new Class[types.length];
        for (int i = 0; i < types.length; ++i) {
            result[i] = ((ClassTypeImpl) types[i]).clazz;
        }
        return result;
    }

    boolean isSuperClass(Class superClass, Class clazz) {
        while (clazz != null) {
            if (clazz.getName().equals(superClass.getName())) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    boolean isEligible(Method method, String methodName, Class[] args) {
        if (!method.getName().equals(methodName)) {
            return false;
        }
        if (args.length != method.getParameterTypes().length) {
            return false;
        }
        Class[] parameters = method.getParameterTypes();
        for (int i = 0; i < args.length; ++i) {
            if (!isSuperClass(parameters[i], args[i])) {
                return false;
            }
        }
        return true;
    }

    Method getMethod(Class clazz, String methodName, Class[] args) throws NoSuchMethodException {
        try {
            Method result = clazz.getDeclaredMethod(methodName, args);
            return result;
        } catch (NoSuchMethodException e) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (isEligible(method, methodName, args)) {
                    return method;
                }
            }
            throw new NoSuchMethodException();
        }
    }

    @Override
    public ClassType getReturnType(ClassType className, String methodName, ClassType[] types) {
        try {
            ClassTypeImpl classNameImpl = (ClassTypeImpl) className;
            Class[] classTypes = getTypeClasses(types);
            Class cl = classNameImpl.clazz;
            Method method = null;
            while (cl != null) {
                try {
                    method = getMethod(cl, methodName, classTypes);
                    break;
                } catch (NoSuchMethodException nsme) {
                    cl = cl.getSuperclass();
                }
            }
            if (method == null) {
                return createErrorClassType("no such method : " + methodName);
            }
            Class myCl = method.getReturnType();

            ClassTypeImpl result = new ClassTypeImpl();
            result.clazz = myCl;

            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof TypeVariable) {
                String varReturnType = genericReturnType.toString();
                result = (ClassTypeImpl) (classNameImpl.genericArgs.get(varReturnType));
            } else if (genericReturnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                Type[] actualArgs = parameterizedType.getActualTypeArguments();
                ClassType[] classTypeArgs = new ClassType[actualArgs.length];
                for (int i = 0; i < actualArgs.length; ++i) {
                    if (actualArgs[i] instanceof TypeVariable) {
                        classTypeArgs[i] = classNameImpl.genericArgs.get(actualArgs[i].toString());
                    } else if (actualArgs[i] instanceof Class) {
                        Class argClass = (Class) actualArgs[i];
                        classTypeArgs[i] = getClassTypeByName(argClass.getName());
                    }
                }

                result = (ClassTypeImpl) substGenericArgs(result, classTypeArgs);
            }

            return result;
        } catch (Exception e) {
            return createErrorClassType(e.toString());
        }
    }

    @Override
    public ClassType getFieldType(ClassType className, String fieldName) {
        try {
            Class cl = ((ClassTypeImpl) className).clazz;
            Field field = cl.getField(fieldName);

            ClassTypeImpl result = new ClassTypeImpl();
            result.clazz = field.getType();
            return result;
        } catch (Exception e) {
            return createErrorClassType(e.toString());
        }
    }

    @Override
    public ClassType getClassTypeByName(String className) {
        try {
            ClassTypeImpl c = new ClassTypeImpl();
            c.clazz = getClass(className);
            return c;
        } catch (ClassNotFoundException e) {
            return createErrorClassType(e.toString());
        }
    }

    @Override
    public ClassType createErrorClassType(String message) {
        ClassTypeError err = new ClassTypeError();
        err.message = message;
        return err;
    }
    
    @Override
    public ClassType substGenericArgs(ClassType className, ClassType[] args) {
        try {
            ClassTypeImpl result = new ClassTypeImpl();
            result.clazz = ((ClassTypeImpl) className).clazz;
            result.genericArgs = new HashMap<String, ClassType>();

            TypeVariable[] vars = result.clazz.getTypeParameters();
            for (int i = 0; i < args.length; ++i) {
                TypeVariable variable = vars[i];
                result.genericArgs.put(variable.toString(), args[i]);
            }
            return result;
        } catch (Exception e) {
            return createErrorClassType(e.toString());
        }
    }
}
