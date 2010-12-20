package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.*;

class TypeFinder {

    private String processingClassName = null;

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

    //TODO delete some determineType methods

    String determineType(Expression expr, VariableList varType, MethodList methodList,
                         ImportList imports, String className) throws Exception {
        this.processingClassName = className;
        return determineType(expr, varType, methodList, imports);
    }

    String determineType(Expression expr, VariableList varType,
                         ImportList imports, String className) throws Exception {
        this.processingClassName = className;
        return determineType(expr, varType, null, imports);
    }

    String determineType(Expression expr, VariableList varType,
                         ImportList imports) throws Exception {
        return determineType(expr, varType, null, imports);
    }

    String determineType(Expression expr, VariableList varType, MethodList methodList,
                         ImportList imports) throws Exception {
        if (expr instanceof NameExpr) {
            return determineType((NameExpr) expr, varType, methodList, imports);
        } else if (expr instanceof MethodCallExpr) {
            return determineType((MethodCallExpr) expr, varType, methodList, imports);
        } else if (expr instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr, varType, methodList, imports);
        } else if (expr instanceof LiteralExpr) {
            return determineType((LiteralExpr) expr, varType, methodList, imports);
        } else if (expr instanceof AssignExpr) {
            return determineType((AssignExpr) expr, varType, methodList, imports);
        } else if (expr instanceof ThisExpr) {
            return determineType((ThisExpr) expr, varType, methodList, imports);
        } else if (expr instanceof ObjectCreationExpr) {
            return determineType((ObjectCreationExpr) expr, varType, methodList, imports);
        }

        throw new UnsupportedExpressionException();
    }

    private String determineType(NameExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        String name = expr.getName();
        if (Character.isUpperCase(name.charAt(0))) {
            return imports.getClassByShortName(name).getName();
        } else {
            if (varType != null && varType.getFieldTypeAsClass(name) != null) {
                return varType.getFieldTypeAsClass(name).getName();
            }
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(FieldAccessExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        String fieldName = expr.getField();
        if (varType.hasName(fieldName)) {
            return varType.getFieldTypeAsString(fieldName);
        } else {

            String scopeClassName = determineType(expr.getScope(), varType, methodList, imports);
            return getFieldType(scopeClassName, expr.getField());
        }
    }

    private String determineType(MethodCallExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {

        Expression scope = expr.getScope();
        if (scope == null) {
            scope = new ThisExpr();
        }

        String scopeClassName = determineType(scope, varType, methodList, imports);

        Class[] argType = getArguments(expr.getArgs(), varType, methodList, imports);

        if (scope instanceof ThisExpr && methodList != null) {
            List<String> argumentTypes = new ArrayList<String>();
            for (Class argument : argType) {
                argumentTypes.add(argument.getName());
            }
            return methodList.getMethodTypeAsString(expr.getName(), argumentTypes, imports);
        }
        return getReturnType(scopeClassName, expr.getName(), argType);
    }

    private Class[] getArguments(List<Expression> args, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception{
        ArrayList<Expression> emptyExpressionsList = new ArrayList<Expression>();
        List<Expression> arguments = args == null ? emptyExpressionsList : args;

        int countOfArguments = arguments.size();

        Class[] argType = new Class[countOfArguments];

        for (int i = 0; i < countOfArguments; i++) {
            if (arguments.get(i) instanceof NameExpr) {
                argType[i] = varType.getFieldTypeAsClass(((NameExpr) arguments.get(i)).getName());
            } else if (arguments.get(i) instanceof LiteralExpr) {
                String type = determineType((LiteralExpr) arguments.get(i), varType, methodList, imports);
                if (!"null".equals(type)) {
                    argType[i] = getPrimitiveClass(type);
                } else {
                    //TODO check correctness of Object type creation
                    argType[i] = Class.forName("java.lang.Object");
                }
            } else if (arguments.get(i) instanceof ObjectCreationExpr) {
                String simpleType = determineType((ObjectCreationExpr) arguments.get(i), varType, methodList, imports);
                if (classIsPrimitive(simpleType)) {
                    argType[i] = getPrimitiveClass(simpleType);
                } else {
                    String type = imports.getClassByShortName(simpleType).getName();
                    argType[i] = Class.forName(type);
                }
            } else if (arguments.get(i) instanceof MethodCallExpr) {
                String type = determineType((MethodCallExpr) arguments.get(i), varType, methodList, imports);
                argType[i] = Class.forName(type);
            } else {
                if (arguments.get(i) instanceof CastExpr) {
                    String type = determineType((CastExpr) arguments.get(i), varType, methodList, imports);
                    argType[i] = Class.forName(type);
                }
            }
        }

        return argType;
    }

    private String determineType(LiteralExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        if (expr instanceof NullLiteralExpr) {
            return "null";
        }
        String className = expr.getClass().getSimpleName();
        //All javaparser's literals have the special class names : Type + "LiteralExpr"
        String typeOfExpression = className.substring(0, className.indexOf("Literal"));
        return getPrimitiveClass(typeOfExpression).getName();
    }

    private String determineType(AssignExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        if (expr.getTarget() instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr.getTarget(), varType, methodList, imports);
        } else if (expr.getTarget() instanceof NameExpr) {
            return determineType((NameExpr) expr.getTarget(), varType, methodList, imports);
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(ThisExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        if (processingClassName == null) {
            throw new UnsupportedExpressionException();
        }
        return processingClassName;
    }

    private String determineType(ObjectCreationExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        return expr.getType().getName();
    }

    private String determineType(CastExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        return determineType(new NameExpr(expr.getType().toString()), varType, methodList, imports);
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
