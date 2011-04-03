package org.creativelabs.introspection;

import java.lang.reflect.*;
import java.util.*;

public class ReflectionAbstractionImpl implements ReflectionAbstraction {

    private static class ClassTypeImpl implements ClassType {

        private Class clazz;

        private HashMap<String, ClassType> genericArgs = null;

        private ClassTypeImpl() {
            genericArgs = new HashMap<String, ClassType>();
        }

        @Override
        public String toString() {
            StringBuffer result = new StringBuffer();
            result.append(clazz.getName());
            if (clazz.getTypeParameters().length != 0) {
                result.append("<");
                for (TypeVariable variable : clazz.getTypeParameters()) {
                    if (!genericArgs.isEmpty()) {
                        result.append(genericArgs.get(variable.toString()) + ", ");
                    } else {
                        result.append(variable + ", ");
                    }
                }
                result.append(">");
            }
            return result.toString();
        }

        @Override
        public String getShortString() {
            return clazz.getSimpleName();
        }
    }

    private static class ClassTypeErrorImpl implements ClassType, ClassTypeError {
        private String message;

        @Override
        public String toString() {
            return message;
        }

        @Override
        public String getShortString() {
            return message;
        }
    }

    private static class ClassTypeNullImpl implements ClassType, ClassTypeNull {

        @Override
        public String toString() {
            return "NullClassType";
        }

        @Override
        public String getShortString() {
            return "NullClassType";
        }
    }

    private HashMap<String, ArrayList<Class>> boxingMap;

    private HashMap<String, Class> primitivesMap;

    private HashMap<String, String> arrayChar;

    private void addToBoxing(String data, Class clazz) {
        if (!boxingMap.containsKey(data)) {
            boxingMap.put(data, new ArrayList<Class>());
        }
        boxingMap.get(data).add(clazz);
    }

    public static ReflectionAbstraction create() {
        return new HookReflectionAbstraction(new ReflectionAbstractionImpl());
    }

    ReflectionAbstractionImpl() {
        boxingMap = new HashMap<String, ArrayList<Class>>();

        addToBoxing("byte", Byte.class);
        addToBoxing("short", Short.class);
        addToBoxing("int", Integer.class);
        addToBoxing("int", short.class);
        addToBoxing("int", long.class);
        addToBoxing("long", Long.class);
        addToBoxing("long", int.class);
        addToBoxing("float", Float.class);
        addToBoxing("double", Double.class);
        addToBoxing("char", Character.class);
        addToBoxing("char", int.class);
        addToBoxing("short", int.class);
        addToBoxing("boolean", Boolean.class);
        addToBoxing("void", Void.class);

        addToBoxing("java.lang.Byte", byte.class);
        addToBoxing("java.lang.Short", short.class);
        addToBoxing("java.lang.Integer", int.class);
        addToBoxing("java.lang.Integer", long.class);
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
        arrayChar = new HashMap<String, String>();
        arrayChar.put("byte", "B");
        arrayChar.put("short", "S");
        arrayChar.put("int", "I");
        arrayChar.put("long", "J");
        arrayChar.put("float", "F");
        arrayChar.put("double", "D");
        arrayChar.put("char", "C");
        arrayChar.put("boolean", "Z");
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
            if (types[i] instanceof ClassTypeNull) {
                try {
                    result[i] = Class.forName("java.lang.Object");
                } catch (Exception e) {
                    //java.lang.Object always exists
                }
            } else if (types[i] instanceof ClassTypeImpl) {
                result[i] = ((ClassTypeImpl) types[i]).clazz;
            }
        }
        return result;
    }

    boolean isSuperClass(Class superClass, Class clazz) {
        return superClass.isAssignableFrom(clazz);
    }

    boolean isParameterMatch(Class parameter, ClassType classType) {
        if (classType instanceof ClassTypeNull) {
            return true;
        } else if (classType instanceof ClassTypeImpl) {
            Class arg = ((ClassTypeImpl) classType).clazz;
            if (!isSuperClass(parameter, arg)) {
                List<Class> classList = boxingMap.get(arg.getName());
                if (classList != null) {
                    boolean result = false;
                    for (Class varg: classList) {
                        if (isSuperClass(parameter, varg)) {
                            result = true;
                            break;
                        }
                    }
                    if (result == false) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    boolean isEligible(Method method, String methodName, ClassType[] args) {
        if (!method.getName().equals(methodName)) {
            return false;
        }
        Class[] parameters = method.getParameterTypes();
        if (method.isVarArgs()) {
            if (args.length < parameters.length - 1) {
                return false;
            }
        } else {
            if (args.length != parameters.length) {
                return false;
            }
        }
        for (int i = 0; i < args.length; ++i) {
            ClassType classType = args[i];
            Class parameter;
            if (i >= (parameters.length - 1) && method.isVarArgs()) {
                parameter = parameters[parameters.length - 1].getComponentType();
            } else {
                parameter = parameters[i];
            }

            if (!isParameterMatch(parameter, classType)) {
                return false;
            }
        }
        return true;
    }

    Method getMethod(Class clazz, String methodName, ClassType[] types) throws NoSuchMethodException {
        try {
            Class[] args = getTypeClasses(types);
            Method result = clazz.getDeclaredMethod(methodName, args);
            return result;
        } catch (NoSuchMethodException e) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (isEligible(method, methodName, types)) {
                    return method;
                }
            }
            methods = clazz.getMethods();
            for (Method method : methods) {
                if (isEligible(method, methodName, types)) {
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
                if (genericArgs.get(actualArgs[i].toString()) != null) {
                    classTypeArgs[i] = genericArgs.get(actualArgs[i].toString());
                } else {
                    classTypeArgs[i] = getClassTypeByName("java.lang.Object");
                }
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

    private ClassTypeImpl processGenericArgs(Type genericReturnType, 
            ClassTypeImpl classNameImpl, ClassTypeImpl result) {

        if (genericReturnType instanceof TypeVariable) {
            String varReturnType = genericReturnType.toString();
            if (classNameImpl.genericArgs.get(varReturnType) != null) {
                return (ClassTypeImpl) (classNameImpl.genericArgs.get(varReturnType));
            } else {
                return (ClassTypeImpl) getClassTypeByName("java.lang.Object");
            }
        } else if (genericReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            return processParameterizedType(parameterizedType,
                    classNameImpl.genericArgs, result);
        }
        return result;
    }

    @Override
    public ClassType getReturnType(ClassType className, String methodName, ClassType[] types) {
        try {
            ClassTypeImpl classNameImpl = (ClassTypeImpl) className;
            Class cl = classNameImpl.clazz;
            Method method = null;
            while (cl != null) {
                try {
                    method = getMethod(cl, methodName, types);
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

            result = processGenericArgs(genericReturnType, classNameImpl, result);

            return result;
        } catch (Exception e) {
            return createErrorClassType(e.toString());
        }
    }

    @Override
    public ClassType getFieldType(ClassType className, String fieldName) {
        try {
            ClassTypeImpl classNameImpl = (ClassTypeImpl) className;
            
            Class cl = classNameImpl.clazz;
            Field field = null;
            while (cl != null) {
                try {
                    field = cl.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException nsfe) {
                    cl = cl.getSuperclass();
                }
            }
            if (field == null) {
                field = classNameImpl.clazz.getField(fieldName);
            }

            ClassTypeImpl result = new ClassTypeImpl();
            result.clazz = field.getType();

            Type genericReturnType = field.getGenericType();

            result = processGenericArgs(genericReturnType, classNameImpl, result);

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
        ClassTypeErrorImpl err = new ClassTypeErrorImpl();
        err.message = message;
        return err;
    }
    
    @Override
    public ClassType substGenericArgs(ClassType className, ClassType[] args) {
        try {
            ClassTypeImpl result = new ClassTypeImpl();
            result.clazz = ((ClassTypeImpl) className).clazz;

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

    private String takeArrayName(Class clazz) {
        if (clazz.isPrimitive()) {
            return arrayChar.get(clazz.getName());
        } else {
            return "L" + clazz.getName() + ";";
        }
    }

    @Override
    public ClassType addArrayDepth(ClassType classType, int count) {
        ClassType result = classType;
        for (int i = 0; i < count; ++i) {
            result = addArrayDepth(result);
        }
        return result;
    }

    @Override
    public ClassType addArrayDepth(ClassType classType) {
        try {
            ClassTypeImpl previous = (ClassTypeImpl) classType;
            ClassTypeImpl result = new ClassTypeImpl();
            String className = previous.toString();
            if (!previous.clazz.isArray()) {
                className = takeArrayName(previous.clazz);
            }
            className = "[" + className;
            result.clazz = Class.forName(className);
            return result;
        } catch (Exception e) {
            return createErrorClassType(e.toString());
        }
    }

    @Override
    public ClassType getElementType(ClassType classType) {
        try {
            ClassTypeImpl classTypeImpl = (ClassTypeImpl) classType;
            ClassTypeImpl result = new ClassTypeImpl();
            result.clazz = classTypeImpl.clazz.getComponentType();
            return result;
        } catch (Exception e) {
            return createErrorClassType(e.toString());
        }
    }

    @Override
    public ClassType createNullClassType() {
        return new ClassTypeNullImpl();
    }
}
