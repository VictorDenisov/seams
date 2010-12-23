package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.*;
import org.creativelabs.introspection.ReflectionAbstraction;
import org.creativelabs.introspection.ReflectionAbstractionImpl;

class TypeFinder {

    static class UnsupportedExpressionException extends RuntimeException {
    }

    String getReturnType(String className, String methodName, Class[] types) throws Exception {
        Class cl = Class.forName(className);
        Method method = cl.getMethod(methodName, types);
        Class myCl = method.getReturnType();
        return myCl.getName();
    }

    String getFieldType(String className, String fieldName) throws Exception {
        Class cl = Class.forName(className);
        Field field = cl.getField(fieldName);
        return field.getType().getName();
    }

    private ReflectionAbstraction reflectionAbstraction;

    private VariableList varType;

    private ImportList imports;

    public TypeFinder(ReflectionAbstraction reflectionAbstraction, VariableList varType, ImportList imports) {
        this.reflectionAbstraction = reflectionAbstraction;
        this.varType = varType;
        this.imports = imports;
    }

    public TypeFinder(VariableList varType, ImportList imports) {
        this(new ReflectionAbstractionImpl(), varType, imports);
    }

    String determineType(Expression expr) throws Exception {
        if (expr instanceof NameExpr) {
            return determineType((NameExpr) expr);
        } else if (expr instanceof MethodCallExpr) {
            return determineType((MethodCallExpr) expr);
        } else if (expr instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr);
        } else if (expr instanceof LiteralExpr) {
            return determineType((LiteralExpr) expr);
        } else if (expr instanceof AssignExpr) {
            return determineType((AssignExpr) expr);
        } else if (expr instanceof ThisExpr) {
            return determineType((ThisExpr) expr);
        } else if (expr instanceof ObjectCreationExpr) {
            return determineType((ObjectCreationExpr) expr);
        } else if (expr instanceof SuperExpr) {
            return determineType((SuperExpr) expr);
        } else if (expr instanceof CastExpr) {
            return determineType((CastExpr) expr);
        } else if (expr instanceof BinaryExpr) {
            return determineType((BinaryExpr) expr);
        }

        throw new UnsupportedExpressionException();
    }

    private String determineType(NameExpr expr) throws Exception {
        String name = expr.getName();
        if (Character.isUpperCase(name.charAt(0))) {
            return imports.getClassByShortName(name).toStringRepresentation();
        } else {
            if (varType != null && varType.getFieldTypeAsClass(name) != null) {
                return varType.getFieldTypeAsClass(name);
            }
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(FieldAccessExpr expr) throws Exception {
        String fieldName = expr.getField();
        if (varType.hasName(fieldName)) {
            return varType.getFieldTypeAsString(fieldName);
        } else {

            String scopeClassName = determineType(expr.getScope());
            return getFieldType(scopeClassName, expr.getField());
        }
    }

    private String determineType(MethodCallExpr expr) throws Exception {

        Expression scope = expr.getScope();
        if (scope == null) {
            scope = new ThisExpr();
        }

        String scopeClassName = determineType(scope);

        ArrayList<Expression> emptyExpressionsList = new ArrayList<Expression>();
        List<Expression> arguments = expr.getArgs() == null ? emptyExpressionsList : expr.getArgs();

        int countOfArguments = arguments.size();

        String[] argType = new String[countOfArguments];

        for (int i = 0; i < countOfArguments; i++) {
            argType[i] = determineType(arguments.get(i));
        }
        return reflectionAbstraction.getReturnType(scopeClassName, expr.getName(), argType);
    }

    private String determineType(LiteralExpr expr) throws Exception {
        if (expr instanceof NullLiteralExpr) {
            return "java.lang.Object";
        }
        String className = expr.getClass().getSimpleName();
        //All javaparser's literals have the special class names : Type + "LiteralExpr"
        String typeOfExpression = className.substring(0, className.indexOf("Literal"));
        return reflectionAbstraction.getClassType("java.lang." + typeOfExpression);
    }

    private String determineType(AssignExpr expr) throws Exception {
        if (expr.getTarget() instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr.getTarget());
        } else if (expr.getTarget() instanceof NameExpr) {
            return determineType((NameExpr) expr.getTarget());
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(ThisExpr expr) throws Exception {
        return varType.getFieldTypeAsString("this");
    }

    private String determineType(SuperExpr expr) throws Exception {
        return varType.getFieldTypeAsString("super");
    }

    private String determineType(ObjectCreationExpr expr) throws Exception {
        return imports.getClassByShortName(expr.getType().getName()).toStringRepresentation();
    }

    private String determineType(CastExpr expr) throws Exception {
        return determineType(new NameExpr(expr.getType().toString()));
    }

        private String determineType(BinaryExpr expr) throws Exception {
        String leftOperatorType = determineType(expr.getLeft());
        String rightOperatorType = determineType(expr.getRight());
        BinaryExpr.Operator operator = expr.getOperator();

        if (BinaryExpr.Operator.plus.equals(operator)){
            return getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType, reflectionAbstraction);
        }
        if (BinaryExpr.Operator.divide.equals(operator)){
            //TODO think why we return double;
            return reflectionAbstraction.getClassType("double");
        }
        if (BinaryExpr.Operator.minus.equals(operator)
                || BinaryExpr.Operator.times.equals(operator)
                || BinaryExpr.Operator.remainder.equals(operator)){
            return getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType, reflectionAbstraction);
        }
            throw new UnsupportedExpressionException();
    }

    private boolean oneOfArgumentsHaveType(String type, String firstArgType, String secondArgType){
        if (firstArgType.equals(type) || secondArgType.equals(type)){
            return true;
        }
        return false;
    }

    private String getReturnTypeIfBothArgumentsIsDigit(String firstArgType,
                                                       String secondArgType,
                                                       ReflectionAbstraction reflectionAbstraction) throws Exception{
        if (oneOfArgumentsHaveType("double", firstArgType, secondArgType)
                || oneOfArgumentsHaveType("java.lang.Double", firstArgType, secondArgType)){
                return reflectionAbstraction.getClassType("double");
            }
            if (oneOfArgumentsHaveType("float", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Float", firstArgType, secondArgType)){
                return reflectionAbstraction.getClassType("float");
            }
            if (oneOfArgumentsHaveType("long", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Long", firstArgType, secondArgType)){
                return reflectionAbstraction.getClassType("long");
            }
            if (oneOfArgumentsHaveType("int", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Integer", firstArgType, secondArgType)){
                return reflectionAbstraction.getClassType("int");
            }
            if (oneOfArgumentsHaveType("short", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Short", firstArgType, secondArgType)){
                return reflectionAbstraction.getClassType("short");
            }
        return null;
    }

    private String getReturnTypeIfBothArgumentsIsChar(String firstArgType,
                                                       String secondArgType,
                                                       ReflectionAbstraction reflectionAbstraction) throws Exception{
        if (oneOfArgumentsHaveType("java.lang.String", firstArgType, secondArgType)){
                return reflectionAbstraction.getClassType("java.lang.String");
            }
            if (oneOfArgumentsHaveType("char", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Char", firstArgType, secondArgType)){
                return reflectionAbstraction.getClassType("char");
            }
        return null;
    }

    private String getReturnTypeIfBothArgumentsHaveAnyType(String firstArgType, String secondArgType, ReflectionAbstraction reflectionAbstraction) throws Exception {
        String type = getReturnTypeIfBothArgumentsIsChar(firstArgType, secondArgType, reflectionAbstraction);
        if (type != null){
            return type;
        } else{
            type = getReturnTypeIfBothArgumentsIsDigit(firstArgType, secondArgType, reflectionAbstraction);
            if (type != null){
                return type;
            }
        }
        throw new UnsupportedExpressionException();
    }




    //TODO delete this method
    public static Class getPrimitiveClass(String className) {
        if ("byte".equals(className)
                || "Byte".equals(className)
                || "java.lang.Byte".equals(className)) {
            return byte.class;
        }
        if ("short".equals(className) || "Short".equals(className) || "java.lang.Short".equals(className)) {
            return short.class;
        }
        if ("int".equals(className) || "Integer".equals(className) || "java.lang.Integer".equals(className)) {
            return int.class;
        }
        if ("long".equals(className) || "Long".equals(className) || "java.lang.Long".equals(className)) {
            return long.class;
        }
        if ("float".equals(className) || "Float".equals(className) || "java.lang.Float".equals(className)) {
            return float.class;
        }
        if ("double".equals(className) || "Double".equals(className) || "java.lang.Double".equals(className)) {
            return double.class;
        }
        if ("char".equals(className) || "Char".equals(className) || "java.lang.Char".equals(className)) {
            return char.class;
        }
        if ("boolean".equals(className) || "Boolean".equals(className) || "java.lang.Boolean".equals(className)) {
            return boolean.class;
        }
        if ("void".equals(className) || "Void".equals(className) || "java.lang.Void".equals(className)) {
            return void.class;
        }
        if ("String".equals(className) || "java.lang.String".equals(className)) {
            return String.class;
        }
        throw new TypeFinder.UnsupportedExpressionException();
    }

    public static boolean classIsPrimitive(String className) {
        return "byte".equals(className)
                || "short".equals(className)
                || "int".equals(className)
                || "long".equals(className)
                || "float".equals(className)
                || "double".equals(className)
                || "char".equals(className)
                || "boolean".equals(className)
                || "void".equals(className)
                || "String".equals(className)
                || "java.lang.String".equals(className);
    }

}
