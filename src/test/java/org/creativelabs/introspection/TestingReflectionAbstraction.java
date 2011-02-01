package org.creativelabs.introspection;

import japa.parser.ast.body.*;

import java.util.*;

public class TestingReflectionAbstraction implements ReflectionAbstraction {

    private static class MethodWrapper {

        String className;

        String methodName;

        String[] argumentsTypes;

        private MethodWrapper(String className, String methodName, String[] argumentsTypes) {
            this.className = className;
            this.methodName = methodName;
            this.argumentsTypes = argumentsTypes; 
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodWrapper that = (MethodWrapper) o;

            if (!Arrays.equals(argumentsTypes, that.argumentsTypes)) return false;
            if (className != null ? !className.equals(that.className) : that.className != null) return false;
            if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = className != null ? className.hashCode() : 0;
            result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
            result = 31 * result + (argumentsTypes != null ? Arrays.hashCode(argumentsTypes) : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MethodWrapper{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", argumentsTypes=" + (argumentsTypes == null ? null : Arrays.asList(argumentsTypes)) +
                '}';
        }
    }

    private static class FieldWrapper {

        String className;

        String fieldName;

        private FieldWrapper(String className, String fieldName) {
            this.className = className;
            this.fieldName = fieldName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FieldWrapper that = (FieldWrapper) o;

            if (className != null ? !className.equals(that.className) : that.className != null) return false;
            if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = className != null ? className.hashCode() : 0;
            result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "FieldWrapper{" +
                "className='" + className + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
        }
    }

    private Map<MethodWrapper, String> methods = new HashMap<MethodWrapper, String>();

    private Map<FieldWrapper, String> fields = new HashMap<FieldWrapper, String>();

    private Map<String, String> classes = new HashMap<String, String>();

    public TestingReflectionAbstraction() {
    }

    public void addMethod(String className, String methodName, String[] types, String returnType) {
        methods.put(new MethodWrapper(className, methodName, types), returnType);
    }

    public void addField(String className, String fieldName, String fieldType) {
        fields.put(new FieldWrapper(className, fieldName), fieldType);
    }

    public void addClass(String className, String classType){
        classes.put(className, classType);
    }

    @Override
    public ClassType getReturnType(ClassType className, String methodName, ClassType[] types) {
        String[] args = new String[types.length];
        for (int i = 0; i < types.length; ++i) {
            args[i] = types[i].toString();
        }
        return new ClassTypeStub(methods.get(new MethodWrapper(className.toString(), methodName, args)));
    }

    @Override
    public ClassType getFieldType(ClassType className, String fieldName) {
        return new ClassTypeStub(fields.get(new FieldWrapper(className.toString(), fieldName)));
    }

    @Override
    public ClassType getClassTypeByName(String className) {
        return new ClassTypeStub(classes.get(className));
    }

    @Override
    public boolean classWithNameExists(String className) {
        return classes.keySet().contains(className);
    }

    @Override
    public ClassType createErrorClassType(String message) {
        return new ClassTypeStub(message);
    }

    @Override
    public ClassType substGenericArgs(ClassType className, ClassType[] args) {
        return null;
    }

    @Override
    public ClassType getNestedClass(ClassType className, String nestedClassName) {
        return null;
    }

    @Override
    public ClassType convertToArray(ClassType classType, int dimension) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < dimension; i++){
            buffer.append("[]");
        }
        return new ClassTypeStub(classType.toString() + buffer);
    }

    @Override
    public ClassType convertFromArray(ClassType classType) {
        return new ClassTypeStub(classType.toString().
                substring(0, classType.toString().indexOf("[")));
    }
    
    @Override
    public ClassType getElementType(ClassType classType) {
        return new ClassTypeStub(classType.toString().
            substring(0, classType.toString().lastIndexOf("[")));
    }

    @Override
    public ClassType createNullClassType() {
        return null;
    }

    @Override
    public ClassType addArrayDepth(ClassType classType) {
        return null;
    }
}
