package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;

import java.util.Map;

class TypeFinder {

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

    String determineType(Expression expr, Map<String, Class> varType) throws Exception {
        if (expr instanceof NameExpr) {
            return varType.get(((NameExpr)expr).getName()).getName();
        } else if (expr instanceof MethodCallExpr) {
            return determineType((MethodCallExpr)expr, varType);
        } else if (expr instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr)expr, varType);
        }

        return "";
    }

    String determineType(FieldAccessExpr expr, Map<String, Class> varType) throws Exception {
        NameExpr scope = (NameExpr)expr.getScope();

        String scopeClassName;

        if (Character.isUpperCase(scope.getName().charAt(0))) {
            scopeClassName = "java.lang." + scope.getName();
        } else {
            scopeClassName = varType.get(scope.getName()).getName();
        }

        return getFieldType(scopeClassName, expr.getField());
    }
 
    String determineType(MethodCallExpr expr, Map<String, Class> varType) throws Exception {
        NameExpr scope = (NameExpr)expr.getScope();
        
        String scopeClassName;

        if (Character.isUpperCase(scope.getName().charAt(0))) {
            scopeClassName = "java.lang." + scope.getName();
        } else {
            scopeClassName = varType.get(scope.getName()).getName();
        }

        Class[] argType = new Class[1];

        NameExpr arg = (NameExpr) expr.getArgs().get(0);

        argType[0] = varType.get(arg.getName());
        return getReturnType(scopeClassName, expr.getName(), argType);
    }
}
