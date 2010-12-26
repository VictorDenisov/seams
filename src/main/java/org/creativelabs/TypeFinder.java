package org.creativelabs;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.*;
import org.creativelabs.introspection.*;

class TypeFinder {

    static class UnsupportedExpressionException extends RuntimeException {
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

    ClassType determineType(Expression expr) throws Exception {
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

    private ClassType determineType(NameExpr expr) throws Exception {
        String name = expr.getName();
        if (Character.isUpperCase(name.charAt(0))) {
            return imports.getClassByShortName(name);
        } else {
            if (varType != null && varType.getFieldTypeAsClass(name) != null) {
                return varType.getFieldTypeAsClass(name);
            }
        }
        throw new UnsupportedExpressionException();
    }

    private ClassType determineType(FieldAccessExpr expr) throws Exception {
        String fieldName = expr.getField();
        if (varType.hasName(fieldName)) {
            return varType.getFieldTypeAsClass(fieldName);
        } else {
            ClassType scopeClassName = determineType(expr.getScope());
            return reflectionAbstraction.getFieldType(scopeClassName, expr.getField());
        }
    }

    private ClassType determineType(MethodCallExpr expr) throws Exception {

        Expression scope = expr.getScope();
        if (scope == null) {
            scope = new ThisExpr();
        }

        ClassType scopeClassName = determineType(scope);

        ArrayList<Expression> emptyExpressionsList = new ArrayList<Expression>();
        List<Expression> arguments = expr.getArgs() == null ? emptyExpressionsList : expr.getArgs();

        int countOfArguments = arguments.size();

        ClassType[] argType = new ClassType[countOfArguments];

        for (int i = 0; i < countOfArguments; i++) {
            argType[i] = determineType(arguments.get(i));
        }
        return reflectionAbstraction.getReturnType(scopeClassName, expr.getName(), argType);
    }

    private ClassType determineType(LiteralExpr expr) throws Exception {
        if (expr instanceof NullLiteralExpr) {
            return imports.getClassByShortName("Object");
        }
        String className = expr.getClass().getSimpleName();
        //All javaparser's literals have the special class names : Type + "LiteralExpr"
        String typeOfExpression = className.substring(0, className.indexOf("Literal"));
        return reflectionAbstraction.getClassTypeByName("java.lang." + typeOfExpression);
    }

    private ClassType determineType(AssignExpr expr) throws Exception {
        if (expr.getTarget() instanceof FieldAccessExpr) {
            return determineType((FieldAccessExpr) expr.getTarget());
        } else if (expr.getTarget() instanceof NameExpr) {
            return determineType((NameExpr) expr.getTarget());
        }
        throw new UnsupportedExpressionException();
    }

    private ClassType determineType(ThisExpr expr) throws Exception {
        return varType.getFieldTypeAsClass("this");
    }

    private ClassType determineType(SuperExpr expr) throws Exception {
        return varType.getFieldTypeAsClass("super");
    }

    private ClassType determineType(ObjectCreationExpr expr) throws Exception {
        return imports.getClassByShortName(expr.getType().getName());
    }

    private ClassType determineType(CastExpr expr) throws Exception {
        return determineType(new NameExpr(expr.getType().toString()));
    }

    private ClassType determineType(BinaryExpr expr) throws Exception {
        ClassType leftOperatorType = determineType(expr.getLeft());
        ClassType rightOperatorType = determineType(expr.getRight());
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

    private boolean oneOfArgumentsHaveType(String type, ClassType firstArgType, ClassType secondArgType) {
        if (firstArgType.toStringRepresentation().equals(type) || secondArgType.toStringRepresentation().equals(type)) {
            return true;
        }
        return false;
    }

    private ClassType getReturnTypeIfBothArgumentsIsDigit(ClassType firstArgType,
                                                       ClassType secondArgType) throws Exception {
        if (oneOfArgumentsHaveType("double", firstArgType, secondArgType)
                || oneOfArgumentsHaveType("java.lang.Double", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("double");
            }
            if (oneOfArgumentsHaveType("float", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Float", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("float");
            }
            if (oneOfArgumentsHaveType("long", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Long", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("long");
            }
            if (oneOfArgumentsHaveType("int", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Integer", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("int");
            }
            if (oneOfArgumentsHaveType("short", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Short", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("short");
            }
        return null;
    }

    private ClassType getReturnTypeIfBothArgumentsIsChar(ClassType firstArgType, ClassType secondArgType) throws Exception {
        if (oneOfArgumentsHaveType("java.lang.String", firstArgType, secondArgType)) {
            return reflectionAbstraction.getClassTypeByName("java.lang.String");
        }
        if (oneOfArgumentsHaveType("char", firstArgType, secondArgType)
                || oneOfArgumentsHaveType("java.lang.Char", firstArgType, secondArgType)) {
            return reflectionAbstraction.getClassTypeByName("char");
        }
        return null;
    }

    private ClassType getReturnTypeIfBothArgumentsHaveAnyType(ClassType firstArgType, ClassType secondArgType) throws Exception {
        ClassType type = getReturnTypeIfBothArgumentsIsChar(firstArgType, secondArgType);
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
