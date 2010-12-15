package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.*;

class TypeFinder {

    private String className = null;

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
        this.className = className;
        return determineType(expr, varType, methodList, imports);
    }

    String determineType(Expression expr, VariableList varType,
                         ImportList imports, String className) throws Exception {
        this.className = className;
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
            if (imports != null && imports.containsKey(name)) {
                return imports.get(name);
            } else {
                //TODO check that java.lang contains name type.
                return "java.lang." + name;
            }
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

        List<Expression> arguments =
                expr.getArgs() == null ?
                        new ArrayList<Expression>() :
                        expr.getArgs();
        int countOfArguments = arguments.size();

        Class[] argType = new Class[countOfArguments];

        for (int i = 0; i < countOfArguments; i++) {
            if (arguments.get(i) instanceof NameExpr) {
                argType[i] = varType.getFieldTypeAsClass(((NameExpr) arguments.get(i)).getName());
            } else if (arguments.get(i) instanceof LiteralExpr) {
                String type = determineType((LiteralExpr) arguments.get(i), varType, methodList, imports);
                if (!"null".equals(type)) {
                    argType[i] = PrimitiveClassFactory.getFactory().getPrimitiveClass(type);
                } else {
                    //TODO check correctness of Object type creation
                    argType[i] = Class.forName("java.lang.Object");
                }
            } else if (arguments.get(i) instanceof ObjectCreationExpr) {
                String simpleType = determineType((ObjectCreationExpr) arguments.get(i), varType, methodList, imports);
                if (PrimitiveClassFactory.getFactory().classIsPrimitive(simpleType)) {
                    argType[i] = PrimitiveClassFactory.getFactory().getPrimitiveClass(simpleType);
                } else {
                    String type = imports.get(simpleType);
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

        if (scope instanceof ThisExpr && methodList != null) {
            List<String> argumentTypes = new ArrayList<String>();
            for (Class argument : argType) {
                argumentTypes.add(argument.getName());
            }
            return methodList.getMethodTypeAsString(expr.getName(), argumentTypes, imports);
        }
        return getReturnType(scopeClassName, expr.getName(), argType);
    }

    private String determineType(LiteralExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        if (expr instanceof NullLiteralExpr) {
            return "null";
        }
        String className = expr.getClass().getSimpleName();
        //All javaparser's literals have the special class names : Type + "LiteralExpr"
        String typeOfExpression = className.substring(0, className.indexOf("Literal"));
        return PrimitiveClassFactory.getFactory().getPrimitiveClass(typeOfExpression).getName();
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
        if (className == null) {
            throw new UnsupportedExpressionException();
        }
        return className;
    }

    private String determineType(ObjectCreationExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        return expr.getType().getName();
    }

    private String determineType(CastExpr expr, VariableList varType, MethodList methodList,
                                 ImportList imports) throws Exception {
        return determineType(new NameExpr(expr.getType().toString()), varType, methodList, imports);
    }

}
