package org.creativelabs;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.*;
import org.creativelabs.introspection.*;

class TypeFinder {

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

    ClassType determineType(Expression expr) {
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

        return reflectionAbstraction.createErrorClassType("unsupported expression");
    }

    private ClassType determineType(CastExpr expr) {
        return imports.getClassByType(expr.getType());
    }

    private ClassType determineType(NameExpr expr) {
        String name = expr.getName();
        ClassType result = null;
        if (imports != null) {
            result = imports.getClassByShortName(name);
        }
        if (result != null && !result.getClass().getSimpleName().equals("ClassTypeError")) {
            return result;
        } else {
            return varType.getFieldTypeAsClass(name);
        }
    }

    private ClassType determineType(FieldAccessExpr expr) {
        String fieldName = expr.getField();
        if (varType.hasName(fieldName)) {
            return varType.getFieldTypeAsClass(fieldName);
        } else {
            ClassType scopeClassName = determineType(expr.getScope());
            ClassType result = reflectionAbstraction.getFieldType(scopeClassName, expr.getField());
            if (result.getClass().getSimpleName().equals("ClassTypeError")) {
                result = reflectionAbstraction.getNestedClass(scopeClassName, expr.getField());
            }
            return result;
        }
    }

    private ClassType determineType(MethodCallExpr expr) {

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

    private ClassType determineType(LiteralExpr expr) {
        if (expr instanceof NullLiteralExpr) {
            return imports.getClassByShortName("Object");
        }
        String className = expr.getClass().getSimpleName();
        //All javaparser's literals have the special class names : Type + "LiteralExpr" 
        //except Character
        String typeOfExpression = className.substring(0, className.indexOf("Literal"));
        if ("Char".equals(typeOfExpression)) {
            return reflectionAbstraction.getClassTypeByName("java.lang.Character");
        } else {
            return reflectionAbstraction.getClassTypeByName("java.lang." + typeOfExpression);
        }
    }

    private ClassType determineType(AssignExpr expr) {
        return determineType(expr.getTarget());
    }

    private ClassType determineType(ThisExpr expr) {
        return varType.getFieldTypeAsClass("this");
    }

    private ClassType determineType(SuperExpr expr) {
        return varType.getFieldTypeAsClass("super");
    }

    private ClassType determineType(ObjectCreationExpr expr) {
        return imports.getClassByShortName(expr.getType().getName());
    }

    private ClassType determineType(BinaryExpr expr) {
        ClassType leftOperatorType = determineType(expr.getLeft());
        ClassType rightOperatorType = determineType(expr.getRight());
        BinaryExpr.Operator operator = expr.getOperator();

        if (BinaryExpr.Operator.plus.equals(operator)) {
            ClassType ress = getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType);
            if (ress == null) {
                return reflectionAbstraction.createErrorClassType("null");
            } else {
                return ress;
            }
        }
        if (BinaryExpr.Operator.divide.equals(operator)) {
            ClassType ress = getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType);
            if (ress == null) {
                return reflectionAbstraction.createErrorClassType("null");
            } else {
                return ress;
            }
        }
        if (BinaryExpr.Operator.minus.equals(operator)
                || BinaryExpr.Operator.times.equals(operator)
                || BinaryExpr.Operator.remainder.equals(operator)) {
            ClassType ress = getReturnTypeIfBothArgumentsHaveAnyType(leftOperatorType, rightOperatorType);

            if (ress == null) {
                return reflectionAbstraction.createErrorClassType("null");
            } else {
                return ress;
            }
        }
        return reflectionAbstraction.createErrorClassType("unknown binary operation: " + operator);
    }

    private boolean oneOfArgumentsHaveType(String type, ClassType firstArgType, ClassType secondArgType) {
        if (firstArgType.toString().equals(type) || secondArgType.toString().equals(type)) {
            return true;
        }
        return false;
    }

    private ClassType getReturnTypeIfBothArgumentsIsDigit(ClassType firstArgType,
                                                       ClassType secondArgType) {
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

    private ClassType getReturnTypeIfBothArgumentsIsChar(ClassType firstArgType, ClassType secondArgType) {
        if (oneOfArgumentsHaveType("java.lang.String", firstArgType, secondArgType)) {
            return reflectionAbstraction.getClassTypeByName("java.lang.String");
        }
        if (oneOfArgumentsHaveType("char", firstArgType, secondArgType)
                || oneOfArgumentsHaveType("java.lang.Character", firstArgType, secondArgType)) {
            return reflectionAbstraction.getClassTypeByName("char");
        }
        return null;
    }

    private ClassType getReturnTypeIfBothArgumentsHaveAnyType(ClassType firstArgType, ClassType secondArgType) {
        ClassType type = getReturnTypeIfBothArgumentsIsChar(firstArgType, secondArgType);
        if (type != null) {
            return type;
        } else {
            type = getReturnTypeIfBothArgumentsIsDigit(firstArgType, secondArgType);
            if (type != null) {
                return type;
            }
        }
        return reflectionAbstraction.createErrorClassType("getReturnTypeIfBothArgumentsHaveAnyType null");
    }
}
