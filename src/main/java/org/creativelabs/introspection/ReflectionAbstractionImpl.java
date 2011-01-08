package org.creativelabs.introspection;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.ArrayList;

public class ReflectionAbstractionImpl implements ReflectionAbstraction {

    private static class ClassTypeImpl implements ClassType {

        private Class clazz;

        private HashMap<String, ClassType> genericArgs = null;

        private int arrayCount;

        private ClassTypeImpl() {
            arrayCount = 0;
        }

        private ClassTypeImpl(int arrayCount) {
            this.arrayCount = arrayCount;
        }

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
            for (int i = 0; i < arrayCount; i++) {
                result.append("[]");
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

    private HashMap<String, ArrayList<Class>> boxingMap;

    private HashMap<String, Class> primitivesMap;

    private void addToBoxing(String data, Class clazz) {
        if (!boxingMap.containsKey(data)) {
            boxingMap.put(data, new ArrayList<Class>());
        }
        boxingMap.get(data).add(clazz);
    }

    public ReflectionAbstractionImpl() {
        boxingMap = new HashMap<String, ArrayList<Class>>();

        addToBoxing("byte", Byte.class);
        addToBoxing("short", Short.class);
        addToBoxing("int", Integer.class);
        addToBoxing("long", Long.class);
        addToBoxing("float", Float.class);
        addToBoxing("double", Double.class);
        addToBoxing("char", Character.class);
        addToBoxing("char", int.class);
        addToBoxing("boolean", Boolean.class);
        addToBoxing("void", Void.class);

        addToBoxing("java.lang.Byte", byte.class);
        addToBoxing("java.lang.Short", short.class);
        addToBoxing("java.lang.Integer", int.class);
        addToBoxing("java.lang.Long", long.class);
        addToBoxing("java.lang.Float", float.class);
        addToBoxing("java.lang.Double", double.class);
        addToBoxing("java.lang.Character", char.class);
        addToBoxing("java.lang.Character", int.class);
        addToBoxing("java.lang.Boolean", boolean.class);
        addToBoxing("java.lang.Void", void.class);

        primitivesMap = new HashMap<String, Class>();
        primitivesMap.put("byte", byte.class);
        primitivesMap.put("short", short.class);
        primitivesMap.put("int", int.class);
        primitivesMap.put("long", long.class);
        primitivesMap.put("float", float.class);
        primitivesMap.put("double", double.class);
        primitivesMap.put("char", char.class);
        primitivesMap.put("boolean", boolean.class);
        primitivesMap.put("void", void.class);
    }

    private Class getClass(String type) throws ClassNotFoundException {
        if (primitivesMap.containsKey(type)) {
            return primitivesMap.get(type);
        } else {
            return Class.forName(type);
        }
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
        return superClass.isAssignableFrom(clazz);
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
            Class arg = args[i];
            if (!isSuperClass(parameters[i], arg)) {
                boolean result = false;
                for (Class varg: boxingMap.get(arg.getName())) {
                    if (isSuperClass(parameters[i], varg)) {
                        result = true;
                        break;
                    }
                }
                if (result == false) {
                    return false;
                }
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

    private ClassTypeImpl processParameterizedType(ParameterizedType parameterizedType, 
            HashMap<String, ClassType> genericArgs, ClassType result) {
        Type[] actualArgs = parameterizedType.getActualTypeArguments();
        ClassType[] classTypeArgs = new ClassType[actualArgs.length];
        for (int i = 0; i < actualArgs.length; ++i) {
            if (actualArgs[i] instanceof TypeVariable) {
                classTypeArgs[i] = genericArgs.get(actualArgs[i].toString());
            } else if (actualArgs[i] instanceof Class) {
                Class argClass = (Class) actualArgs[i];
                classTypeArgs[i] = getClassTypeByName(argClass.getName());
            } else if (actualArgs[i] instanceof ParameterizedType) {
                ClassTypeImpl classTypeArg = new ClassTypeImpl();
                classTypeArg.clazz = (Class) ((ParameterizedType) actualArgs[i]).getRawType();
                classTypeArgs[i] = processParameterizedType((ParameterizedType) actualArgs[i], 
                        genericArgs, classTypeArg);
            }
        }

        return (ClassTypeImpl) substGenericArgs(result, classTypeArgs);
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
                result = processParameterizedType(parameterizedType,
                        classNameImpl.genericArgs, result);
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
            Field field = cl.getDeclaredField(fieldName);

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

    @Override
    public ClassType getNestedClass(ClassType className, String nestedClassName) {
        try {
            ClassTypeImpl classNameImpl = (ClassTypeImpl) className;
            Class[] list = classNameImpl.clazz.getDeclaredClasses();
            ClassTypeImpl result = new ClassTypeImpl();
            for (Class clazz : list) {
                if (clazz.getSimpleName().equals(nestedClassName)) {
                    result.clazz = clazz;
                }
            }
            if (result.clazz != null) {
                return result;
            } else {
                return createErrorClassType("no such nested class : " + nestedClassName);
            }
        } catch (Exception e) {
            return createErrorClassType(e.toString());
        }
    }

    @Override
    public ClassType convertToArray(ClassType classType, int dimension) {
        ClassTypeImpl arrayType = (ClassTypeImpl) classType;
        arrayType.arrayCount = dimension;
        return arrayType;
    }

    @Override
    public ClassType convertFromArray(ClassType classType) {
        ClassTypeImpl arrayType = (ClassTypeImpl) classType;
        arrayType.arrayCount = 0;
        return arrayType;
    }
}
