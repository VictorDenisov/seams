package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;

import java.util.Map;

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

    String determineType(Expression expr, Map<String, Class> varType, Map<String, String> imports)
        throws Exception {
        return "org.apache.log4j.Logger";
    }

    String determineType(Expression expr, Map<String, Class> varType) throws Exception {
        if (expr instanceof NameExpr) {
            String name = ((NameExpr) expr).getName();
            if (Character.isUpperCase(name.charAt(0))) {
                return "java.lang." + name;
            } else {
                return varType.get(name).getName();
            }
        } else if (expr instanceof MethodCallExpr) {
            return determineType((MethodCallExpr) expr, varType);
        } else if (expr instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr, varType);
        }

        throw new UnsupportedExpressionException();
    }

    private String determineType(FieldAccessExpr expr, Map<String, Class> varType) throws Exception {
        String scopeClassName = determineType(expr.getScope(), varType);

        return getFieldType(scopeClassName, expr.getField());
    }
 
    private String determineType(MethodCallExpr expr, Map<String, Class> varType) throws Exception {
        String scopeClassName = determineType(expr.getScope(), varType);

        Class[] argType = new Class[1];

        NameExpr arg = (NameExpr) expr.getArgs().get(0);

        argType[0] = varType.get(arg.getName());
        return getReturnType(scopeClassName, expr.getName(), argType);
    }
}
