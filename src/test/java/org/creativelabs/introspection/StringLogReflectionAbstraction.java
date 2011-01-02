package org.creativelabs.introspection;

public class StringLogReflectionAbstraction implements ReflectionAbstraction {

    private StringBuffer log = new StringBuffer();

    private ReflectionAbstraction decoratedReflectionAbstraction;

    private StringLogReflectionAbstraction() {
    }
    
    public static StringLogReflectionAbstraction createDumbStringLogReflectionAbstraction() {
        return new StringLogReflectionAbstraction();
    }

    public static StringLogReflectionAbstraction 
        createDecoratingStringLogReflectionAbstraction (ReflectionAbstraction reflectionAbstraction) {
        StringLogReflectionAbstraction result = new StringLogReflectionAbstraction();
        result.decoratedReflectionAbstraction = reflectionAbstraction;
        return result;
    }

    public String getLog() {
        return log.toString();
    }

    private String argsToString(ClassType[] args) {
        StringBuffer result = new StringBuffer("{");
        for (ClassType type : args) {
            result.append(type.toString() + ", ");
        }
        return result + "}";
    }

    @Override
    public ClassType getReturnType(ClassType className, String methodName, ClassType[] types) {
        String argsString = argsToString(types);
        log.append("getReturnType(" + className + ", " + methodName + ", " + argsString + ")");
        ClassType result = null;
        if (decoratedReflectionAbstraction != null) {
            result = decoratedReflectionAbstraction.getReturnType(className, methodName, types);
            log.append(":" + result);
        }
        log.append("; ");
        return result;
    }

    @Override
    public ClassType getFieldType(ClassType className, String fieldName) {
        log.append("getFieldType(" + className + ", " + fieldName + ")");
        ClassType result = null;
        if (decoratedReflectionAbstraction != null) {
            result = decoratedReflectionAbstraction.getFieldType(className, fieldName);
            log.append(":" + result);
        }
        log.append("; ");
        return result;
    }

    @Override
    public ClassType getClassTypeByName(String className) {
        log.append("getClassTypeByName(" + className + ")");
        ClassType result = null;
        if (decoratedReflectionAbstraction != null) {
            result = decoratedReflectionAbstraction.getClassTypeByName(className);
            log.append(":" + result);
        }
        log.append("; ");
        return result;
    }

    @Override
    public boolean classWithNameExists(String className) {
        log.append("classWithNameExists(" + className + ")");
        boolean result = false;
        if (decoratedReflectionAbstraction != null) {
            result = decoratedReflectionAbstraction.classWithNameExists(className);
            log.append(":" + result);
        }
        log.append("; ");
        return result;
    }

    @Override
    public ClassType createErrorClassType(String message) {
        log.append("createErrorClassType(" + message + ")");
        ClassType result = null;
        if (decoratedReflectionAbstraction != null) {
            result = decoratedReflectionAbstraction.createErrorClassType(message);
            log.append(":" + result);
        }
        log.append("; ");
        return result;
    }
}
