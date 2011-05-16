package org.creativelabs.ssa;

import japa.parser.ast.*;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;

/**
 * This class helps for visiting the statements and expressions.
 *
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 14:23
 */
public class GenericVisitorHelper {

    /**
     * Visits a statement by visitor with some arguments.
     *
     * @param statement for processing
     * @param arg       arguments are used in processing
     * @param visitor   the using visitor
     * @param <T>       the type of argument
     */
    public static <R, T> R visitStatement(Statement statement, T arg, GenericVisitorAdapter<R, T> visitor) {
        if (statement instanceof AssertStmt) {
            return visitor.visit((AssertStmt) statement, arg);
        } else if (statement instanceof BlockStmt) {
            return visitor.visit((BlockStmt) statement, arg);
        } else if (statement instanceof BreakStmt) {
            return visitor.visit((BreakStmt) statement, arg);
        } else if (statement instanceof ContinueStmt) {
            return visitor.visit((ContinueStmt) statement, arg);
        } else if (statement instanceof DoStmt) {
            return visitor.visit((DoStmt) statement, arg);
        } else if (statement instanceof EmptyStmt) {
            return visitor.visit((EmptyStmt) statement, arg);
        } else if (statement instanceof ExplicitConstructorInvocationStmt) {
            return visitor.visit((ExplicitConstructorInvocationStmt) statement, arg);
        } else if (statement instanceof ExpressionStmt) {
            return visitor.visit((ExpressionStmt) statement, arg);
        } else if (statement instanceof ForeachStmt) {
            return visitor.visit((ForeachStmt) statement, arg);
        } else if (statement instanceof ForStmt) {
            return visitor.visit((ForStmt) statement, arg);
        } else if (statement instanceof IfStmt) {
            return visitor.visit((IfStmt) statement, arg);
        } else if (statement instanceof LabeledStmt) {
            return visitor.visit((LabeledStmt) statement, arg);
        } else if (statement instanceof ReturnStmt) {
            return visitor.visit((ReturnStmt) statement, arg);
        } else if (statement instanceof SwitchEntryStmt) {
            return visitor.visit((SwitchEntryStmt) statement, arg);
        } else if (statement instanceof SwitchStmt) {
            return visitor.visit((SwitchStmt) statement, arg);
        } else if (statement instanceof SynchronizedStmt) {
            return visitor.visit((SynchronizedStmt) statement, arg);
        } else if (statement instanceof ThrowStmt) {
            return visitor.visit((ThrowStmt) statement, arg);
        } else if (statement instanceof TryStmt) {
            return visitor.visit((TryStmt) statement, arg);
        } else if (statement instanceof TypeDeclarationStmt) {
            return visitor.visit((TypeDeclarationStmt) statement, arg);
        } else if (statement instanceof WhileStmt) {
            return visitor.visit((WhileStmt) statement, arg);
        } else {
            throw new UnsupportedOperationException("VoidVisitorHelper is not support statement for " +
                    statement.getClass() + ".");
        }
    }

    /**
     * Visits a expression by visitor with some arguments.
     *
     * @param expression for processing
     * @param arg        arguments are used in processing
     * @param visitor    the using visitor
     * @param <T>        the type of argument
     */
    public static <R, T> R visitExpression(Expression expression, T arg, GenericVisitorAdapter<R, T> visitor) {
        if (expression instanceof ArrayAccessExpr) {
            return visitor.visit((ArrayAccessExpr) expression, arg);
        } else if (expression instanceof ArrayCreationExpr) {
            return visitor.visit((ArrayCreationExpr) expression, arg);
        } else if (expression instanceof ArrayInitializerExpr) {
            return visitor.visit((ArrayInitializerExpr) expression, arg);
        } else if (expression instanceof AssignExpr) {
            return visitor.visit((AssignExpr) expression, arg);
        } else if (expression instanceof BinaryExpr) {
            return visitor.visit((BinaryExpr) expression, arg);
        } else if (expression instanceof BooleanLiteralExpr) {
            return visitor.visit((BooleanLiteralExpr) expression, arg);
        } else if (expression instanceof CastExpr) {
            return visitor.visit((CastExpr) expression, arg);
        } else if (expression instanceof CharLiteralExpr) {
            return visitor.visit((CharLiteralExpr) expression, arg);
        } else if (expression instanceof ClassExpr) {
            return visitor.visit((ClassExpr) expression, arg);
        } else if (expression instanceof ConditionalExpr) {
            return visitor.visit((ConditionalExpr) expression, arg);
        } else if (expression instanceof DoubleLiteralExpr) {
            return visitor.visit((DoubleLiteralExpr) expression, arg);
        } else if (expression instanceof EnclosedExpr) {
            return visitor.visit((EnclosedExpr) expression, arg);
        } else if (expression instanceof FieldAccessExpr) {
            return visitor.visit((FieldAccessExpr) expression, arg);
        } else if (expression instanceof InstanceOfExpr) {
            return visitor.visit((InstanceOfExpr) expression, arg);
        } else if (expression instanceof IntegerLiteralMinValueExpr) {
            return visitor.visit((IntegerLiteralMinValueExpr) expression, arg);
        } else if (expression instanceof IntegerLiteralExpr) {
            return visitor.visit((IntegerLiteralExpr) expression, arg);
        } else if (expression instanceof LongLiteralMinValueExpr) {
            return visitor.visit((LongLiteralMinValueExpr) expression, arg);
        } else if (expression instanceof LongLiteralExpr) {
            return visitor.visit((LongLiteralExpr) expression, arg);
        } else if (expression instanceof MarkerAnnotationExpr) {
            return visitor.visit((MarkerAnnotationExpr) expression, arg);
        } else if (expression instanceof MethodCallExpr) {
            return visitor.visit((MethodCallExpr) expression, arg);
        } else if (expression instanceof QualifiedNameExpr) {
            return visitor.visit((QualifiedNameExpr) expression, arg);
        } else if (expression instanceof NameExpr) {
            return visitor.visit((NameExpr) expression, arg);
        } else if (expression instanceof NormalAnnotationExpr) {
            return visitor.visit((NormalAnnotationExpr) expression, arg);
        } else if (expression instanceof NullLiteralExpr) {
            return visitor.visit((NullLiteralExpr) expression, arg);
        } else if (expression instanceof ObjectCreationExpr) {
            return visitor.visit((ObjectCreationExpr) expression, arg);
        } else if (expression instanceof SingleMemberAnnotationExpr) {
            return visitor.visit((SingleMemberAnnotationExpr) expression, arg);
        } else if (expression instanceof StringLiteralExpr) {
            return visitor.visit((StringLiteralExpr) expression, arg);
        } else if (expression instanceof SuperExpr) {
            return visitor.visit((SuperExpr) expression, arg);
        } else if (expression instanceof ThisExpr) {
            return visitor.visit((ThisExpr) expression, arg);
        } else if (expression instanceof UnaryExpr) {
            return visitor.visit((UnaryExpr) expression, arg);
        } else if (expression instanceof VariableDeclarationExpr) {
            return visitor.visit((VariableDeclarationExpr) expression, arg);
        } else {
            throw new UnsupportedOperationException("VoidVisitorHelper is not support expression for " +
                    expression.getClass() + ".");
        }
    }

    /**
     * Visits a comment by visitor with some arguments.
     *
     * @param comment for processing
     * @param arg     arguments are used in processing
     * @param visitor the using visitor
     * @param <T>     the type of argument
     */
    public static <R, T> R visitComment(Comment comment, T arg, GenericVisitorAdapter<R, T> visitor) {
        if (comment instanceof LineComment) {
            return visitor.visit((LineComment) comment, arg);
        } else if (comment instanceof BlockComment) {
            return visitor.visit((BlockComment) comment, arg);
        } else if (comment instanceof JavadocComment) {
            return visitor.visit((JavadocComment) comment, arg);
        } else {
            throw new UnsupportedOperationException("VoidVisitorHelper is not support comment for " +
                    comment.getClass() + ".");
        }
    }

    /**
     * Visits a node by visitor with some arguments.
     *
     * @param node    for processing
     * @param arg     arguments are used in processing
     * @param visitor the using visitor
     * @param <T>     the type of argument
     */
    public static <R, T> R visitNode(Node node, T arg, GenericVisitorAdapter<R, T> visitor) {
        if (node instanceof CatchClause) {
            return visitor.visit((CatchClause) node, arg);
        } else if (node instanceof MemberValuePair) {
            return visitor.visit((MemberValuePair) node, arg);
        } else if (node instanceof CompilationUnit) {
            return visitor.visit((CompilationUnit) node, arg);
        } else if (node instanceof PackageDeclaration) {
            return visitor.visit((PackageDeclaration) node, arg);
        } else if (node instanceof TypeParameter) {
            return visitor.visit((TypeParameter) node, arg);
        } else if (node instanceof Statement) {
            return visitStatement((Statement) node, arg, visitor);
        } else if (node instanceof Expression) {
            return visitExpression((Expression) node, arg, visitor);
        } else if (node instanceof Comment) {
            return visitComment((Comment) node, arg, visitor);
        } else {
            //TODO: to addEdge processing other constructions
            throw new UnsupportedOperationException("VoidVisitorHelper is not support node for " +
                    node.getClass() + ".");
        }
    }

}
