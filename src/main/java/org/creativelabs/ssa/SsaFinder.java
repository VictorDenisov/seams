package org.creativelabs.ssa;


import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses for modification ast expressions.
 */
public class SsaFinder {

    private static final String UNSUPPORTED = "Unsupported expression: ";
    private static final String NULL = "Expression is null ";
    private static final String SEPARATOR = "#";
    private static final String NOT_CONTAINS = "<not contains in key set>";

    VariablesHolder variables;

    private boolean isNeededToIncreaseIndex;

    public SsaFinder(VariablesHolder variables, boolean isNeededToIncreaseIndex) {
        this.variables = variables;
        this.isNeededToIncreaseIndex = isNeededToIncreaseIndex;
    }

    Expression determineSsa(Expression expr) {
        if (expr instanceof NameExpr) {
            return determineSsa((NameExpr) expr);
        } else if (expr instanceof LiteralExpr) {
            return determineSsa((LiteralExpr) expr);
        } else if (expr instanceof BinaryExpr) {
            return determineSsa((BinaryExpr) expr);
        } else if (expr instanceof ArrayAccessExpr) {
            return determineSsa((ArrayAccessExpr) expr);
        } else if (expr instanceof ArrayCreationExpr) {
            return determineSsa((ArrayCreationExpr) expr);
        } else if (expr instanceof ObjectCreationExpr) {
            return determineSsa((ObjectCreationExpr) expr);
        } else if (expr instanceof MethodCallExpr) {
            return determineSsa((MethodCallExpr) expr);
        } else if (expr instanceof InstanceOfExpr) {
            return determineSsa((InstanceOfExpr) expr);
        } else if (expr instanceof CastExpr) {
            return determineSsa((CastExpr) expr);
        } else if (expr instanceof EnclosedExpr) {
            return determineSsa((EnclosedExpr) expr);
        } else if (expr instanceof ArrayInitializerExpr) {
            return determineSsa((ArrayInitializerExpr) expr);
        } else if (expr instanceof UnaryExpr) {
            return determineSsa((UnaryExpr) expr);
        }
        return new NameExpr(expr != null ? UNSUPPORTED + expr.toString() : NULL);
    }

    NameExpr determineSsa(NameExpr expr) {
        String variableName = expr.getName();

        boolean isClassWithStaticMethod = Character.isUpperCase(variableName.charAt(0));
        if (isClassWithStaticMethod) {
            return expr;
        }

        Integer variableIndex = variables.read(variableName);
        if (variableIndex != null) {
            if (isNeededToIncreaseIndex) {
                variableIndex = variables.readFrom(variableName, false);
                variableIndex++;
                variables.write(variableName, variableIndex);
            }
            expr.setName(variableName + SEPARATOR + variableIndex);
        } else {
            expr.setName(variableName + " " + NOT_CONTAINS);
        }
        return expr;
    }

    LiteralExpr determineSsa(LiteralExpr expr) {
        return expr;
    }

    BinaryExpr determineSsa(BinaryExpr expr) {
        determineSsa(expr.getLeft());
        determineSsa(expr.getRight());
        return expr;
    }

    MethodCallExpr determineSsa(ArrayAccessExpr expr) {
        //TODO refactor
        boolean b = isNeededToIncreaseIndex;
        isNeededToIncreaseIndex = false;

        determineSsa(expr.getIndex());
        determineSsa(expr.getName());

        isNeededToIncreaseIndex = b;

        List<Expression> args = new ArrayList<Expression>();
        args.add(expr.getName());
        args.add(expr.getIndex());

        if (isNeededToIncreaseIndex) {
            return new MethodCallExpr(null, "Update", args);
        } else {
            return new MethodCallExpr(null, "Access", args);
        }
    }

    ArrayCreationExpr determineSsa(ArrayCreationExpr expr) {
        if (expr.getInitializer() != null) {
            determineSsa(expr.getInitializer());
        }
        return expr;
    }

    ArrayInitializerExpr determineSsa(ArrayInitializerExpr expr) {
        if (expr.getValues() != null) {
            for (Expression expression : expr.getValues()) {
                determineSsa(expression);
            }
        }
        return expr;
    }

    ObjectCreationExpr determineSsa(ObjectCreationExpr expr) {
        if (expr.getArgs() != null) {
            for (Expression expression : expr.getArgs()) {
                determineSsa(expression);
            }
        }
        return expr;
    }

    MethodCallExpr determineSsa(MethodCallExpr expr) {
        if (expr.getArgs() != null) {
            for (Expression expression : expr.getArgs()) {
                determineSsa(expression);
            }
        }
        if (expr.getScope() != null) {
            determineSsa(expr.getScope());
        }
        return expr;
    }

    InstanceOfExpr determineSsa(InstanceOfExpr expr) {
        if (expr.getExpr() != null) {
            determineSsa(expr.getExpr());
        }
        return expr;
    }

    CastExpr determineSsa(CastExpr expr) {
        if (expr.getExpr() != null) {
            determineSsa(expr.getExpr());
        }
        return expr;
    }

    EnclosedExpr determineSsa(EnclosedExpr expr) {
        if (expr.getInner() != null) {
            determineSsa(expr.getInner());
        }
        return expr;
    }

    UnaryExpr determineSsa(UnaryExpr expr) {
        if (expr.getExpr() != null) {
            determineSsa(expr.getExpr());
        }
        return expr;
    }

    VariableDeclaratorId determineSsa(VariableDeclaratorId id) {
        id.setName(determineSsa(new NameExpr(id.getName())).getName());
        return id;
    }
}
