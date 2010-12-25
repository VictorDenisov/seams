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
                return varType.getFieldTypeAsClass(name).toStringRepresentation();
            }
        }
        throw new UnsupportedExpressionException();
    }

    private String determineType(FieldAccessExpr expr) throws Exception {
        String fieldName = expr.getField();
        if (varType.hasName(fieldName)) {
            return varType.getFieldTypeAsClass(fieldName).toStringRepresentation();
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
        return reflectionAbstraction.getClassTypeByName("java.lang." + typeOfExpression).toStringRepresentation();
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
        return varType.getFieldTypeAsClass("this").toStringRepresentation();
    }

    private String determineType(SuperExpr expr) throws Exception {
        return varType.getFieldTypeAsClass("super").toStringRepresentation();
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

        if (BinaryExpr.Operator.plus.equals(operator)) {
            return getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType);
        }
        if (BinaryExpr.Operator.divide.equals(operator)) {
            return getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType);
        }
        if (BinaryExpr.Operator.minus.equals(operator)
                || BinaryExpr.Operator.times.equals(operator)
                || BinaryExpr.Operator.remainder.equals(operator)) {
            return getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType);
        }
        throw new UnsupportedExpressionException();
    }

    private boolean oneOfArgumentsHaveType(String type, String firstArgType, String secondArgType) {
        if (firstArgType.equals(type) || secondArgType.equals(type)) {
            return true;
        }
        return false;
    }

    private String getReturnTypeIfBothArgumentsIsDigit(String firstArgType,
                                                       String secondArgType) throws Exception {
        if (oneOfArgumentsHaveType("double", firstArgType, secondArgType)
                || oneOfArgumentsHaveType("java.lang.Double", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("double").toStringRepresentation();
            }
            if (oneOfArgumentsHaveType("float", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Float", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("float").toStringRepresentation();
            }
            if (oneOfArgumentsHaveType("long", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Long", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("long").toStringRepresentation();
            }
            if (oneOfArgumentsHaveType("int", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Integer", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("int").toStringRepresentation();
            }
            if (oneOfArgumentsHaveType("short", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Short", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("short").toStringRepresentation();
            }
        return null;
    }

    private String getReturnTypeIfBothArgumentsIsChar(String firstArgType, String secondArgType) throws Exception {
        if (oneOfArgumentsHaveType("java.lang.String", firstArgType, secondArgType)) {
            return reflectionAbstraction.getClassTypeByName("java.lang.String").toStringRepresentation();
        }
        if (oneOfArgumentsHaveType("char", firstArgType, secondArgType)
                || oneOfArgumentsHaveType("java.lang.Char", firstArgType, secondArgType)) {
            return reflectionAbstraction.getClassTypeByName("char").toStringRepresentation();
        }
        return null;
    }

    private String getReturnTypeIfBothArgumentsHaveAnyType(String firstArgType, String secondArgType) throws Exception {
        String type = getReturnTypeIfBothArgumentsIsChar(firstArgType, secondArgType);
        if (type != null) {
            return type;
        } else {
            type = getReturnTypeIfBothArgumentsIsDigit(firstArgType, secondArgType);
            if (type != null) {
                return type;
            }
        }
        throw new UnsupportedExpressionException();
    }
}
