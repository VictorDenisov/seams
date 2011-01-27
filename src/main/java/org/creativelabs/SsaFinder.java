package org.creativelabs;


import japa.parser.ast.expr.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SsaFinder {

    private static final String UNSUPPORTED = "Unsupported expression: ";

    VariablesHolder variables;

    private boolean isNeededToIncreaseIndex;

    public SsaFinder(VariablesHolder variables, boolean isNeededToIncreaseIndex) {
        this.variables = variables;
        this.isNeededToIncreaseIndex = isNeededToIncreaseIndex;
    }

    String determineSsa(Expression expr) {
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
        return UNSUPPORTED + expr.toString() + "\n";
    }

    String determineSsa(NameExpr expr) {
        String variableName = expr.getName();
        Integer variableIndex = variables.read(variableName);
        if (isNeededToIncreaseIndex) {
            variableIndex = variables.readFrom(variableName, false);
            variableIndex++;
            variables.write(variableName, variableIndex);
        }
        return variableName + (variableIndex != null ? variableIndex : "<not contains in key set>");
    }

    String determineSsa(LiteralExpr expr) {
        return expr.toString();
    }

    String determineSsa(BinaryExpr expr) {
        String leftPart = determineSsa(expr.getLeft());
        String rightPart = determineSsa(expr.getRight());
        String operator = expr.getOperator().name();
        return leftPart + " " + operator + " " + rightPart;
    }

    String determineSsa(ArrayAccessExpr expr) {
        if (isNeededToIncreaseIndex) {
            return "Update(" + expr.getName() + "," + expr.getIndex() + ")";
        } else {
            return "Access(" + expr.getName() + "," + expr.getIndex() + ")";
        }
    }

    String determineSsa(ArrayCreationExpr expr) {
        return expr.toString();
    }

    String determineSsa(ObjectCreationExpr expr) {
        String type = expr.getType().getName();
        if (expr.getArgs() != null) {
            List<String> args = new ArrayList<String>();
            for (Expression expression : expr.getArgs()) {
                args.add(determineSsa(expression));
            }
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = args.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next());
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
            return "new " + type + "(" + builder + ")";
        }
        return "new " + type + "()";
    }

    String determineSsa(MethodCallExpr expr) {
        String name = expr.getName();
        if (expr.getArgs() != null) {
            List<String> args = new ArrayList<String>();
            for (Expression expression : expr.getArgs()) {
                args.add(determineSsa(expression));
            }
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = args.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next());
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
            return name + "(" + builder + ")";
        }
        return name + "()";
    }
}
