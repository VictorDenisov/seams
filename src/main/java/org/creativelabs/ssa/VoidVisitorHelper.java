package org.creativelabs.ssa;

import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 14:23
 */
public class VoidVisitorHelper {

    public static<T> void visitStatement(Statement statement, T arg, VoidVisitorAdapter<T> visitor) {
        if (statement instanceof BlockStmt) {
            visitor.visit((BlockStmt) statement, arg);
        } else if (statement instanceof ExpressionStmt) {
            visitor.visit((ExpressionStmt) statement, arg);
        } else if (statement instanceof IfStmt) {
            visitor.visit((IfStmt) statement, arg);
        } else if (statement instanceof ForStmt) {
            visitor.visit((ForStmt) statement, arg);
        } else if (statement instanceof WhileStmt) {
            visitor.visit((WhileStmt) statement, arg);
        } else if (statement instanceof ReturnStmt) {
            visitor.visit((ReturnStmt) statement, arg);
        } else if (statement instanceof TryStmt) {
            visitor.visit((TryStmt) statement, arg);
        } else if (statement instanceof ForeachStmt) {
            visitor.visit((ForeachStmt) statement, arg);
        }
        //TODO to remove exception
        throw new UnsupportedOperationException("VoidVisitorHelper is not support statement of type " +
                statement.getClass());
    }

    public static<T> void visitExpression(Expression expression, T arg, VoidVisitorAdapter<T> visitor) {
        if (expression instanceof AssignExpr) {
            visitor.visit((AssignExpr) expression, arg);
        } else if (expression instanceof VariableDeclarationExpr) {
            visitor.visit((VariableDeclarationExpr) expression, arg);
        } else if (expression instanceof MethodCallExpr) {
            visitor.visit((MethodCallExpr) expression, arg);
        } else if (expression instanceof CastExpr) {
            visitor.visit((CastExpr) expression, arg);
        }
        //TODO to remove exception
        throw new UnsupportedOperationException("VoidVisitorHelper is not support expression of type " +
                expression.getClass());
    }

}
