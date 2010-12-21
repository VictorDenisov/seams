package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
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

    String determineType(Expression expr, VariableList varType,
                         ImportList imports) throws Exception {
        return determineType(expr, varType, new ReflectionAbstractionImpl(), imports);
    }

    String determineType(Expression expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                         ImportList imports) throws Exception {
        if (expr instanceof NameExpr) {
            return determineType((NameExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof MethodCallExpr) {
            return determineType((MethodCallExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof LiteralExpr) {
            return determineType((LiteralExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof AssignExpr) {
            return determineType((AssignExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof ThisExpr) {
            return determineType((ThisExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof ObjectCreationExpr) {
            return determineType((ObjectCreationExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof SuperExpr) {
            return determineType((SuperExpr) expr, varType, reflectionAbstraction, imports);
        } else if (expr instanceof CastExpr) {
            return determineType((CastExpr) expr, varType, reflectionAbstraction, imports);
        }

        throw new UnsupportedExpressionException();
    }

    private String determineType(NameExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {
        String name = expr.getName();
        if (Character.isUpperCase(name.charAt(0))) {
            return imports.getClassByShortName(name);
        } else {
            if (varType != null && varType.getFieldTypeAsClass(name) != null) {
                return varType.getFieldTypeAsClass(name).getName();
            }
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(FieldAccessExpr expr, VariableList varType,
                                 ReflectionAbstraction reflectionAbstraction, ImportList imports) throws Exception {
        String fieldName = expr.getField();
        if (varType.hasName(fieldName)) {
            return varType.getFieldTypeAsString(fieldName);
        } else {

            String scopeClassName = determineType(expr.getScope(), varType, reflectionAbstraction, imports);
            return getFieldType(scopeClassName, expr.getField());
        }
    }

    private String determineType(MethodCallExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {

        Expression scope = expr.getScope();
        if (scope == null) {
            scope = new ThisExpr();
        }

        String scopeClassName = determineType(scope, varType, reflectionAbstraction, imports);

        ArrayList<Expression> emptyExpressionsList = new ArrayList<Expression>();
        List<Expression> arguments = expr.getArgs() == null ? emptyExpressionsList : expr.getArgs();

        int countOfArguments = arguments.size();

        String[] argType = new String[countOfArguments];

        for (int i = 0; i < countOfArguments; i++) {
            argType[i] = determineType(arguments.get(i), varType, reflectionAbstraction, imports);
        }
        return reflectionAbstraction.getReturnType(scopeClassName, expr.getName(), argType);
    }

    private String determineType(LiteralExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {
        if (expr instanceof NullLiteralExpr) {
            return "java.lang.Object";
        }
        String className = expr.getClass().getSimpleName();
        //All javaparser's literals have the special class names : Type + "LiteralExpr"
        String typeOfExpression = className.substring(0, className.indexOf("Literal"));
        return reflectionAbstraction.getClassType("java.lang." + typeOfExpression);
    }

    private String determineType(AssignExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {
        if (expr.getTarget() instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr.getTarget(), varType, reflectionAbstraction, imports);
        } else if (expr.getTarget() instanceof NameExpr) {
            return determineType((NameExpr) expr.getTarget(), varType, reflectionAbstraction, imports);
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(ThisExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {
        return  varType.getFieldTypeAsString("this");
    }

    private String determineType(SuperExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {
        return  varType.getFieldTypeAsString("super");
    }

    private String determineType(ObjectCreationExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {
        return expr.getType().getName();
    }

    private String determineType(CastExpr expr, VariableList varType, ReflectionAbstraction reflectionAbstraction,
                                 ImportList imports) throws Exception {
        return determineType(new NameExpr(expr.getType().toString()), varType, reflectionAbstraction, imports);
    }


    //TODO think about String as a primitive class...
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
