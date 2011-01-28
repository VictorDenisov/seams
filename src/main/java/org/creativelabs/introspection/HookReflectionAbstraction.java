package org.creativelabs.introspection;

class HookReflectionAbstraction implements ReflectionAbstraction {

    private ReflectionAbstraction ra;

    HookReflectionAbstraction(ReflectionAbstractionImpl ra) {
        this.ra = ra;
    }
    
    public ClassType getReturnType(ClassType className, String methodName, ClassType[] types) {
        ClassType result = ra.getReturnType(className, methodName, types);
        if (result instanceof ClassTypeError) {
            result = ra.getReturnType(ra.getClassTypeByName("java.lang.Object"), methodName, types);
        }

        return result;
    }

    public ClassType getFieldType(ClassType className, String fieldName) {
        if (fieldName.equals("length")) {
            // Hook for length. Length is not a field of the array type.
            return getClassTypeByName("int");
        }
        return ra.getFieldType(className, fieldName);
    }

    public ClassType getClassTypeByName(String className) {
        return ra.getClassTypeByName(className);
    }

    public boolean classWithNameExists(String className) {
        return ra.classWithNameExists(className);
    }

    public ClassType createErrorClassType(String message) {
        return ra.createErrorClassType(message);
    }

    public ClassType substGenericArgs(ClassType className, ClassType[] args) {
        return ra.substGenericArgs(className, args);
    }

    public ClassType getNestedClass(ClassType className, String nestedClassName) {
        return ra.getNestedClass(className, nestedClassName);
    }

    public ClassType convertToArray(ClassType classType, int dimension) {
        return ra.convertToArray(classType, dimension);
    }

    public ClassType convertFromArray(ClassType classType) {
        return ra.convertFromArray(classType);
    }

    public ClassType getElementType(ClassType classType) {
        return ra.getElementType(classType);
    }

    public ClassType createNullClassType() {
        return ra.createNullClassType();
    }
}
