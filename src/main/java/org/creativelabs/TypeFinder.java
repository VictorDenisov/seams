package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.*;

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
        if (expr instanceof NameExpr) {
            return determineType((NameExpr) expr, varType, imports);
        } else if (expr instanceof MethodCallExpr) {
            return determineType((MethodCallExpr) expr, varType, imports);
        } else if (expr instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr, varType, imports);
        }  else if (expr instanceof LiteralExpr){
            return determineType((LiteralExpr) expr);
        } else if (expr instanceof AssignExpr){
            return determineType((AssignExpr) expr, varType, imports);
        }

        throw new UnsupportedExpressionException();
    }

    private String determineType(NameExpr expr, VariableList varType,
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
            if (varType != null && varType.getFieldTypeAsClass(name) != null){
                return varType.getFieldTypeAsClass(name).getName();
            }
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(FieldAccessExpr expr, VariableList varType,
            ImportList imports) throws Exception {
        String scopeClassName = determineType(expr.getScope(), varType, imports);

        return getFieldType(scopeClassName, expr.getField());
    }
 
    private String determineType(MethodCallExpr expr, VariableList varType, 
            ImportList imports) throws Exception {
        String scopeClassName = determineType(expr.getScope(), varType, imports);

        List<Expression> arguments =
                expr.getArgs() == null ?
                        new ArrayList<Expression>() :
                        expr.getArgs();
        int countOfArguments = arguments.size();

        Class[] argType = new Class[countOfArguments];

        for (int i = 0; i < countOfArguments; i++){
            if (arguments.get(i) instanceof NameExpr){
                argType[i] = varType.getFieldTypeAsClass(((NameExpr)arguments.get(i)).getName());
            } else if (arguments.get(i) instanceof LiteralExpr){
                argType[i] = PrimitiveClassFactory.getFactory().getPrimitiveClass(determineType((LiteralExpr) arguments.get(i), varType, imports));
            }
        }
        return getReturnType(scopeClassName, expr.getName(), argType);
    }

    private String determineType(LiteralExpr expr) throws Exception {
        String className = expr.getClass().getSimpleName();
        //All javaparser's literals have the special class names : Type + "LiteralExpr"
        String typeOfExpression = className.substring(0, className.indexOf("Literal"));
        return PrimitiveClassFactory.getFactory().getPrimitiveClass(typeOfExpression).getName();
    }

    private String determineType(AssignExpr expr, VariableList varType,
            ImportList imports) throws Exception {
        return determineType((NameExpr) expr.getTarget(), varType, imports);
    }
}
