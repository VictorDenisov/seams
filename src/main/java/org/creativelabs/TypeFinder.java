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
        this(ReflectionAbstractionImpl.create(), varType, imports);
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
        } else if (expr instanceof ArrayAccessExpr) {
            return determineType((ArrayAccessExpr) expr);
        } else if (expr instanceof VariableDeclarationExpr) {
            return determineType((VariableDeclarationExpr) expr);
        } else if (expr instanceof EnclosedExpr) {
            return determineType((EnclosedExpr) expr);
        } else if (expr instanceof ClassExpr) {
            return determineType((ClassExpr) expr);
        }

        return reflectionAbstraction.createErrorClassType("unsupported expression");
    }

    private ClassType determineType(ClassExpr expr) {
        ClassType result = reflectionAbstraction.getClassTypeByName("java.lang.Class");
        ClassType[] genericArgs = new ClassType[1];
        genericArgs[0] = imports.getClassByType(expr.getType());
        return reflectionAbstraction.substGenericArgs(result, genericArgs);
    }

    private ClassType determineType(EnclosedExpr expr) {
        return determineType(expr.getInner());
    }

    private ClassType determineType(CastExpr expr) {
        return imports.getClassByType(expr.getType());
    }

    private ClassType determineType(NameExpr expr) {
        String name = expr.getName();
        if (isFullClassName(name)){
            return reflectionAbstraction.getClassTypeByName(name);
        }
        ClassType result = null;
        if (imports != null) {
            result = imports.getClassByShortName(name);
        }
        if (result != null && !(result instanceof ClassTypeError)) {
            return result;
        } else {
            result = varType.getFieldTypeAsClass(name);
            if (result instanceof ClassTypeError) {
                ClassType thisType = varType.getFieldTypeAsClass("this");
                result = reflectionAbstraction.getFieldType(thisType, name);
            }
            return result;
        }
    }

    private boolean isFullClassName(String className){
        if (className.indexOf('.') == -1){
            return false;
        }
        return true;
    }

    private ClassType determineType(FieldAccessExpr expr) {
        String fieldName = expr.getField();

        ClassType scopeClassName = determineType(expr.getScope());
        ClassType result = null;
        if (scopeClassName instanceof ClassTypeError) {
            result = reflectionAbstraction.getClassTypeByName(expr.toString());
        } else {
            result = reflectionAbstraction.getFieldType(scopeClassName, expr.getField());
            if (result instanceof ClassTypeError) {
                result = reflectionAbstraction.getNestedClass(scopeClassName, expr.getField());
            }
        }
        return result;
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
            return reflectionAbstraction.createNullClassType();
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
        return imports.getClassByType(expr.getType());
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
        try {
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
        } catch (Exception e) {
            return null;
        }
    }

    private ClassType getReturnTypeIfBothArgumentsIsChar(ClassType firstArgType, ClassType secondArgType) {
        try {
            if (oneOfArgumentsHaveType("java.lang.String", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("java.lang.String");
            }
            if (oneOfArgumentsHaveType("char", firstArgType, secondArgType)
                    || oneOfArgumentsHaveType("java.lang.Character", firstArgType, secondArgType)) {
                return reflectionAbstraction.getClassTypeByName("char");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
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

    private ClassType determineType(ArrayAccessExpr expr) {
        ClassType nameResult = determineType(expr.getName());
        return reflectionAbstraction.getElementType(nameResult);
    }

    private ClassType determineType(VariableDeclarationExpr expr) {
        return determineType(new NameExpr(expr.getType().toString()));
    }

}
