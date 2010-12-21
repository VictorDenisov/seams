package org.creativelabs.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectionAbstractionImpl implements ReflectionAbstraction {

	private static class ClassTypeImpl implements ClassType {

		private Class clazz;

		@Override
		public String toStringRepresentation() {
			return clazz.getName();
		}
	}

    private Class[] getTypeClasses(String[] types) throws Exception {
        Class[] result = new Class[types.length];
        for (int i = 0; i < types.length; ++i) {
            result[i] = getClass(types[i]);
        }
        return result;
    }

    private Class getClass(String type) throws Exception {
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
    public String getReturnType(String className, String methodName, String[] types) throws Exception {
        Class[] classTypes = getTypeClasses(types);
        Class cl = Class.forName(className);
        Method method = cl.getMethod(methodName, classTypes);
        Class myCl = method.getReturnType();
        return myCl.getName();
    }

	@Override
    public String getFieldType(String className, String fieldName) throws Exception {
        Class cl = Class.forName(className);
        Field field = cl.getField(fieldName);
        return field.getType().getName();
    }

    @Override
    public String getClassType(String className) throws Exception {
        return getClass(className).getName();
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

    private Class[] getTypeClasses(ClassType[] types) throws Exception {
        Class[] result = new Class[types.length];
        for (int i = 0; i < types.length; ++i) {
            result[i] = ((ClassTypeImpl) types[i]).clazz;
        }
        return result;
    }

	@Override
	public ClassType getReturnType(ClassType className, String methodName, ClassType[] types) throws Exception {
        Class[] classTypes = getTypeClasses(types);
        Class cl = ((ClassTypeImpl) className).clazz;
        Method method = cl.getMethod(methodName, classTypes);
        Class myCl = method.getReturnType();

		ClassTypeImpl result = new ClassTypeImpl();
		result.clazz = myCl;
		return result;
	}

	@Override
	public ClassType getFieldType(ClassType className, String fieldName) throws Exception {
        Class cl = ((ClassTypeImpl)className).clazz;
        Field field = cl.getField(fieldName);

		ClassTypeImpl result = new ClassTypeImpl();
		result.clazz = field.getType();
		return result;
	}

	@Override
	public ClassType getClassTypeByName(String className) throws Exception {
		ClassTypeImpl c = new ClassTypeImpl();
		c.clazz = Class.forName(className);
		return c;
	}
}
