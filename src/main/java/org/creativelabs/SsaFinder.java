package org.creativelabs;


import japa.parser.ast.expr.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SsaFinder {

    private static final String UNSUPPORTED = "Unsupported expression: ";
    private static final String NULL = "Expression is null ";
    private static final String SEPARATOR = "#";

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
        }
        //TODO add exception
        return new NameExpr(expr != null ? UNSUPPORTED + expr.toString() : NULL);
    }

    NameExpr determineSsa(NameExpr expr) {
        String variableName = expr.getName();
        Integer variableIndex = variables.read(variableName);
        if (variableIndex != null) {
            if (isNeededToIncreaseIndex) {
                variableIndex = variables.readFrom(variableName, false);
                variableIndex++;
                variables.write(variableName, variableIndex);
            }
            expr.setName(variableName + SEPARATOR + variableIndex);
        } else {
            expr.setName(variableName + " <not contains in key set>");
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

    ArrayAccessExpr determineSsa(ArrayAccessExpr expr) {
        //TODO refactor
        boolean increaseIndex = isNeededToIncreaseIndex;
        isNeededToIncreaseIndex = false;
        determineSsa(expr.getIndex());
        determineSsa(expr.getName());
        isNeededToIncreaseIndex = increaseIndex;
        return expr;
    }

    ArrayCreationExpr determineSsa(ArrayCreationExpr expr) {
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
        return expr;
    }
}
